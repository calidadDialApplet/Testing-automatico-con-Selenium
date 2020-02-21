package ProcedimientoRC;

import ProcedimientoRC.ParteDeShowflow.Subtypology;
import Utils.Utils;
import exceptions.MissingParameterException;
import org.ini4j.Wini;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ParteDeShowflowTest extends TestWithConfig {
    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String showflowName;
    static String showflowAuxField;
    static String showflowOptionsGroupName;
    static String showflowOption1;
    static String showflowOption2;
    static String showflowOption3;
    static String showflowQuestion1;
    static String showflowQuestion2;
    /*static String showflowTypologies;
    static String showflowSubtypologies;
    static String showflowResults;
    static String showflowVisibleBy;*/


    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();

    public ParteDeShowflowTest(Wini ini) {
        super(ini);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Showflow", new ArrayList<>(Arrays.asList("showflowName", "showflowAuxField", "showflowOptionsGroupName", "showflowOption1", "showflowOption2",
                "showflowOption3", "showflowQuestion1", "showflowQuestion2")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws MissingParameterException {
        super.checkParameters();
        try
        {
            try
            {
                url = commonIni.get("General", "url");
                headless = commonIni.get("General", "headless");
                adminName = commonIni.get("Admin", "adminName");
                adminPassword = commonIni.get("Admin", "adminPassword");
                showflowName = commonIni.get("Showflow", "showflowName");
                showflowAuxField = commonIni.get("Showflow", "showflowAuxField");
                showflowOptionsGroupName = commonIni.get("Showflow", "showflowOptionsGroupName");
                showflowOption1 = commonIni.get("Showflow", "showflowOption1");
                showflowOption2 = commonIni.get("Showflow", "showflowOption2");
                showflowOption3 = commonIni.get("Showflow", "showflowOption3");
                showflowQuestion1 = commonIni.get("Showflow", "showflowQuestion1");
                showflowQuestion2 = commonIni.get("Showflow", "showflowQuestion2");
                /*showflowTypologies = ini.get("Showflow", "showflowTypologies");
                showflowSubtypologies = ini.get("Showflow", "showflowSubtypologies");
                showflowVisibleBy = ini.get("Showflow", "showflowVisibleBy");
                showflowResults = ini.get("Showflow", "showflowResults");*/


            } catch (Exception e)
            {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);

            results.put("--Create showflow  ->  ", createShowflow());
            results.put("--Activate showflow fields  ->  ", activateShowflowFields());
            results.put("--Create different questions  ->  ", createQuestions());
            results.put("--Create tipologies and subtipologies  ->  ", createTypologies());


            return results;
        } catch (Exception e)
        {
            return results;
        } finally
        {
            firefoxDriver.close();
        }

    }

    public String createShowflow()
    {
        try
        {
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            WebElement createShowflow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'setShowflow.php']", firefoxDriver);
            SeleniumDAO.click(createShowflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));

            WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "workflow-name", firefoxDriver);
            showflowNameInput.sendKeys(showflowName);

            WebElement refreshRadioButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'form-col input radio']//input[@name = 'workflow-showrefreshbutton']", firefoxDriver);
            SeleniumDAO.click(refreshRadioButton);

            WebElement sendButton = SeleniumDAO.selectElementBy("id", "workflow-send", firefoxDriver);
            SeleniumDAO.click(sendButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                Thread.sleep(500);
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sweet-alert showSweetAlert visible']//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            } catch (Exception e)
            {
                return e.toString() + "\nERROR: The showflow could not be created. Check if it already exists";
            }

            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", firefoxDriver);
            SeleniumDAO.click(showflowPanel);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(showflowName);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            return "Test OK. The showflow has been created";
        } catch (Exception e)
        {
            return e.toString() + "\nERROR: Test failed";
        }
    }
    public String activateShowflowFields()
    {
        //List with the options of the options group
        List<String> options = new ArrayList<>();
        options.add(showflowOption1);
        options.add(showflowOption2);
        options.add(showflowOption3);
        try
        {
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", firefoxDriver);
            SeleniumDAO.click(showflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("edit_showflow")));
            WebElement contactFields = SeleniumDAO.selectElementBy("id", "edit_showflow_contact", firefoxDriver);
            SeleniumDAO.click(contactFields);

            //Add options group
            WebElement addOptionsGroup = SeleniumDAO.selectElementBy("id", "addOptionsGroup", firefoxDriver);
            SeleniumDAO.click(addOptionsGroup);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            WebElement optionsGroupName = SeleniumDAO.selectElementBy("id", "new-option-group-name", firefoxDriver);
            optionsGroupName.sendKeys(showflowOptionsGroupName);

            WebElement addOptionsGroupButton = SeleniumDAO.selectElementBy("id", "add_new_group", firefoxDriver);
            SeleniumDAO.click(addOptionsGroupButton);

            Thread.sleep(1000);

            WebElement editOptionGroup = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'option_groups']" +
                    "//td[contains(., '" + showflowOptionsGroupName + "')]//following-sibling::td[@style = 'text-align: center;']//a[@class = 'edit_group_options']", firefoxDriver);
            SeleniumDAO.click(editOptionGroup);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            for(int i = 0; i < options.size(); i++)
            {
                WebElement newOptionNameInput = SeleniumDAO.selectElementBy("id", "new-option-name", firefoxDriver);
                WebElement addNewOptionButton = SeleniumDAO.selectElementBy("id", "add_new_option", firefoxDriver);
                newOptionNameInput.sendKeys(options.get(i));
                SeleniumDAO.click(addNewOptionButton);
            }

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //Closes the iframe of the options group
            WebElement closeIframe = SeleniumDAO.selectElementBy("id", "fancybox-close", firefoxDriver);
            firefoxDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            SeleniumDAO.click(closeIframe);

            WebElement cityCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city']//td[@style = 'text-align: center;']/input", firefoxDriver);
            SeleniumDAO.click(cityCheckbox);

            WebElement countryCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'country']//td[@style = 'text-align: center;']/input", firefoxDriver);
            SeleniumDAO.click(countryCheckbox);

            WebElement auxFieldInput = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//input[@class = 'name']", firefoxDriver);
            auxFieldInput.sendKeys(showflowAuxField);

            Select auxFieldType = SeleniumDAO.findSelectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//select[@class = 'type']", firefoxDriver);
            auxFieldType.selectByValue("radiobutton");

            Select optionGroup = SeleniumDAO.findSelectElementBy("xpath", "//select[@class = 'optiongroup active']", firefoxDriver);
            optionGroup.selectByVisibleText(showflowOptionsGroupName);

            WebElement saveAndConfigureButton = SeleniumDAO.selectElementBy("id", "save-fields-and-go", firefoxDriver);
            SeleniumDAO.click(saveAndConfigureButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                Thread.sleep(500);
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sweet-alert showSweetAlert visible']//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            } catch (Exception e)
            {
                return e.toString() + "\nERROR: Unexpected";
            }

            //Checks if the fields are checked
            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", firefoxDriver);
            SeleniumDAO.click(showflowPanel);

            showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", firefoxDriver);
            SeleniumDAO.click(showflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("edit_showflow")));
            contactFields = SeleniumDAO.selectElementBy("id", "edit_showflow_contact", firefoxDriver);
            SeleniumDAO.click(contactFields);

            cityCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city']//td[@style = 'text-align: center;']/input", firefoxDriver);
            countryCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'country']//td[@style = 'text-align: center;']/input", firefoxDriver);
            WebElement auxFieldCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//td[@style = 'text-align: center;']/input", firefoxDriver);

            if(cityCheckbox.isSelected() && countryCheckbox.isSelected() && auxFieldCheckbox.isSelected())
            {
                return "Test OK. The showflow fields name, phone, city, country and aux field: " + showflowAuxField + ", have been activated.";
            } else
            {
                return "ERROR. The showflow fields were activated but they are not saved";
            }

        } catch(Exception e)
        {
            return e.toString() + "\nERROR. Something went wrong";
        }
    }
    public String createQuestions()
    {
        List<String> questions = new ArrayList<>();
        questions.add(showflowQuestion1);
        questions.add(showflowQuestion2);
        try
        {
            //Go to showflow fields
            WebElement showflowFields = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_showflow_fields']", firefoxDriver);
            SeleniumDAO.click(showflowFields);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow_fields")));

            for(int i = 0; i < questions.size(); i++)
            {
                WebElement questionNameInput = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'new-field']//input[@class = 'label']", firefoxDriver);
                questionNameInput.sendKeys(questions.get(i));

                Thread.sleep(1000);
                Select questionType = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'workflow_fields']//tr[@id = 'new-field']//select[@class = 'type']", firefoxDriver);
                if(i == 0) questionType.selectByVisibleText("Checkbox");
                else questionType.selectByVisibleText("Text");

                Select optionGroup = SeleniumDAO.findSelectElementBy("xpath", "//tr[@id = 'new-field']//select[@class = 'optiongroup']", firefoxDriver);
                optionGroup.selectByVisibleText(showflowOptionsGroupName);

                WebElement addNewQuestionButton = SeleniumDAO.selectElementBy("id", "add_new_field", firefoxDriver);
                SeleniumDAO.click(addNewQuestionButton);
            }

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "save-fields", firefoxDriver);
            SeleniumDAO.click(saveButton);

            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
            SeleniumDAO.click(okButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = '" + showflowQuestion1 + "']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = '" + showflowQuestion2 + "']")));
                return "Test OK. The question fields were created and apear on the table.";
            } catch (Exception e)
            {
                return "ERROR. The questions were created but do not apear on the table";
            }
        } catch (Exception e)
        {
            return e.toString() + "\nERROR. Something went wrong";
        }
    }
    public String createTypologies()
    {
        HashMap<String, List<Subtypology>> typologiesData = new HashMap<>();
        try
        {
            WebElement actionFields = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_showflow_actions']]", firefoxDriver);
            SeleniumDAO.click(actionFields);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("action-field")));
            WebElement typologyCheckbox = SeleniumDAO.selectElementBy("id", "action-id-1", firefoxDriver);
            WebElement observationsCheckbox = SeleniumDAO.selectElementBy("id", "action-id-6", firefoxDriver);
            SeleniumDAO.click(typologyCheckbox);
            SeleniumDAO.click(observationsCheckbox);

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "save-fields", firefoxDriver);
            SeleniumDAO.click(saveButton);
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
            SeleniumDAO.click(okButton);

            WebElement configureActionButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'configure-action']", firefoxDriver);
            SeleniumDAO.click(configureActionButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("typologies-table")));

            typologiesData = loadTypologiesAttributes();
            List<WebElement> tableElementsAux = new ArrayList<>();

            for(Map.Entry<String, List<Subtypology>> entry : typologiesData.entrySet())
            {
                WebElement typologyNameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'typologies-table']//input[@id = 'new-typology-field']", firefoxDriver);
                typologyNameInput.sendKeys(entry.getKey());

                WebElement addTypologyButton = SeleniumDAO.selectElementBy("id", "add-new-typology", firefoxDriver);
                SeleniumDAO.click(addTypologyButton);

                List<WebElement> tableElements = firefoxDriver.findElements(By.xpath("//table[@id = 'typologies-table']//tbody"));
                if(tableElements.size() == 1) {
                    String elementID = tableElements.get(0).getAttribute("data-typology");
                    WebElement typologyConfigurationButton = SeleniumDAO.selectElementBy("xpath", "//tr[@data-typology = '" + elementID + "']" +
                            "//a[@class = 'configure-subtypologies']", firefoxDriver);
                    SeleniumDAO.click(typologyConfigurationButton);

                } else
                {
                     if(tableElementsAux.removeAll(tableElements))
                     {
                        String elementID = tableElements.get(0).getAttribute("data-typology");
                         WebElement typologyConfigurationButton = SeleniumDAO.selectElementBy("xpath", "//tr[@data-typology = '" + elementID + "']" +
                                 "//a[@class = 'configure-subtypologies']", firefoxDriver);
                         SeleniumDAO.click(typologyConfigurationButton);
                     }
                }


            }
            return "Test OK";
        } catch (Exception e)
        {
            return e.toString() + "\nERROR";
        }
    }

    public HashMap<String, List<Subtypology>> loadTypologiesAttributes()
    {
        HashMap<String, List<Subtypology>> result = new HashMap<>();

        result.put("MAQUINA", new ArrayList<Subtypology>() {{
            add(new Subtypology("TRANSFERENCIA", "NULL", "4"));
            add(new Subtypology("INVALIDO", "NEGATIVE", "4"));
            add(new Subtypology("NUM MAX INTENTOS", "NEGATIVE", "4"));
            add(new Subtypology("ROBINSON", "NEGATIVE", "4"));
        }});
        result.put("NO INTERESA", new ArrayList<Subtypology>() {{
            add(new Subtypology("SE QUEDA CON EL OPERADOR", "NS", "1"));
            add(new Subtypology("TARIFA MAS CARA", "NS", "1"));
            add(new Subtypology("YA ES CLIENTE", "NEGATIVE", "1"));
        }});
        result.put("PENDIENTE", new ArrayList<Subtypology>() {{
            add(new Subtypology("AGENDADA", "NULL", "1"));
        }});
        result.put("VENTA", new ArrayList<Subtypology>() {{
            add(new Subtypology("ADSL", "POSITIVE", "1"));
            add(new Subtypology("MOVIL", "POSITIVE", "1"));
        }});

        return result;
    }



}
