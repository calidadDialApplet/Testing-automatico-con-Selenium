package ProcedimientoRC;

import ProcedimientoRC.ParteDeShowflow.Subtypology;
import Utils.Utils;
import Utils.File;
import exceptions.MissingParameterException;
import gui.Action;
import org.apache.commons.io.FileUtils;
import org.ini4j.Wini;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import main.SeleniumDAO;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
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
    static String showflowCopyName;
    static String typologiesCsvPath;
    static String bdUrl;
    static String bdUser;
    static String bdPassword;
    static String bdTypologiesTable;


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
                "showflowOption3", "showflowQuestion1", "showflowQuestion2", "showflowCopyName")));
        requiredParameters.put("CSV", new ArrayList<>((Arrays.asList("typologiesCsvPath"))));
        requiredParameters.put("BD", new ArrayList<>((Arrays.asList("bdUrl", "bdUser", "bdPassword", "bdTypologiesTable"))));

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
                showflowCopyName = commonIni.get("Showflow", "showflowCopyName");
                typologiesCsvPath = commonIni.get("CSV", "typologiesCsvPath");
                bdUrl = commonIni.get("BD", "bdUrl");
                bdUser = commonIni.get("BD", "bdUser");
                bdPassword = commonIni.get("BD", "bdPassword");
                bdTypologiesTable = commonIni.get("BD", "bdTypologiesTable");


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
            results.put("--Create typologies and subtipologies  ->  ", createTypologies());
            results.put("--Import typologies as CSV  ->", importTypologiesCSV());
            results.put("--Check typologies on DB  ->  ",  checkDB());
            results.put("--Configure pages  ->  ", configurePages());
            results.put("--Clone showflow and check it  ->  ", cloneShowflow());


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
                e.printStackTrace();
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            String createShowflowRes = Utils.createShowflow(firefoxDriver, firefoxWaiting, showflowName, "contact");
            if(createShowflowRes.contains("ERROR")) return createShowflowRes;
            else return "Test OK. The showflow has been created";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR: Test failed";
        }
    }
    public static String activateShowflowFields()
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

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("new-option-group-name")));
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
                newOptionNameInput.clear();
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
                e.printStackTrace();
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
            e.printStackTrace();
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
                Thread.sleep(500);
            }

            firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.id("save-fields")));
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
                e.printStackTrace();
                return "ERROR. The questions were created but do not apear on the table";
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Something went wrong";
        }
    }
    public String createTypologies()
    {
        HashMap<String, List<Subtypology>> typologiesData = new HashMap<>();
        try
        {
            WebElement actionFields = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_showflow_actions']/a", firefoxDriver);
            SeleniumDAO.click(actionFields);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("action_fields")));
            WebElement typologyCheckbox = SeleniumDAO.selectElementBy("id", "action-id-1", firefoxDriver);
            WebElement observationsCheckbox = SeleniumDAO.selectElementBy("id", "action-id-6", firefoxDriver);
            SeleniumDAO.click(typologyCheckbox);
            SeleniumDAO.click(observationsCheckbox);

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "save-fields", firefoxDriver);
            SeleniumDAO.click(saveButton);
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            WebElement configureActionButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'configure-action']", firefoxDriver);
            SeleniumDAO.click(configureActionButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("typologies-table")));

            typologiesData = loadTypologiesAttributes();
            List<String> idTableElementsAux = new ArrayList<>();
            List<String> idTableElements = new ArrayList<>();

            try
            {
                //Creates the typologies and subtypologies
                for(Map.Entry<String, List<Subtypology>> entry : typologiesData.entrySet())
                {
                    WebElement typologyNameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'typologies-table']//input[@id = 'new-typology-field']", firefoxDriver);
                    typologyNameInput.sendKeys(entry.getKey());

                    WebElement addTypologyButton = SeleniumDAO.selectElementBy("id", "add-new-typology", firefoxDriver);
                    SeleniumDAO.click(addTypologyButton);

                    okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
                    Thread.sleep(500);
                    SeleniumDAO.click(okButton);

                    List<WebElement> tableElements = firefoxDriver.findElements(By.xpath("//table[@id = 'typologies-table']//tbody/tr"));
                    for(WebElement webElement : tableElements) {
                        idTableElements.add(webElement.getAttribute("data-typology"));
                    }
                    if(tableElements.size() == 1) {
                        String elementID = tableElements.get(0).getAttribute("data-typology");
                        idTableElementsAux.add(elementID);
                        WebElement typologyConfigurationButton = SeleniumDAO.selectElementBy("xpath", "//tr[@data-typology = '" + elementID + "']" +
                                "//a[@class = 'configure-subtypologies']", firefoxDriver);
                        SeleniumDAO.click(typologyConfigurationButton);
                        fillSubtypologies(entry.getValue());

                    } else
                    {
                        if(idTableElements.removeAll(idTableElementsAux))
                        {
                            idTableElementsAux.add(idTableElements.get(0));
                            WebElement typologyConfigurationButton = SeleniumDAO.selectElementBy("xpath", "//tr[@data-typology = '" + idTableElements.get(0) + "']" +
                                    "//a[@class = 'configure-subtypologies']", firefoxDriver);
                            SeleniumDAO.click(typologyConfigurationButton);
                            fillSubtypologies(entry.getValue());
                        }
                    }
                    idTableElements.clear();

                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "\nERROR. The typologies and subtypologies could not be created";
            }

            return "Test OK. The typologies and subtypologies have been created";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR";
        }
    }
    public String importTypologiesCSV()
    {
        try
        {
            WebElement browseButton = SeleniumDAO.selectElementBy("id", "csvfile", firefoxDriver);
            SeleniumDAO.writeInTo(browseButton, typologiesCsvPath);

            WebElement importButton = SeleniumDAO.selectElementBy("id", "submitcsv", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(importButton);

            String res = "";
            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(@style, 'green')]")));
                res = "Test OK. The CSV has been imported correctly.";
            } catch (Exception e2)
            {
                try
                {
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(@style, 'red')]")));
                    res = "ERROR: The CSV import failed.";
                } catch (Exception e3)
                { }
            }

            return res;
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }
    public String checkDB(){
        try
        {
            firefoxDriver.get(bdUrl);

            SeleniumDAO.switchToFrame("browser", firefoxDriver);

            WebElement postgreSQLServer = SeleniumDAO.selectElementBy("xpath", "//body[@class = 'browser']//a[contains(., 'PostgreSQL')]", firefoxDriver);
            SeleniumDAO.click(postgreSQLServer);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            SeleniumDAO.switchToFrame("detail", firefoxDriver);

            //Login
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("loginUsername")));
            WebElement usernameInput = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'navbar']//input[@name = 'loginUsername']", firefoxDriver);
            usernameInput.sendKeys(bdUser);
            WebElement passwordInput = SeleniumDAO.selectElementBy("id", "loginPassword", firefoxDriver);
            passwordInput.sendKeys(bdPassword);

            WebElement loginButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit']", firefoxDriver);
            SeleniumDAO.click(loginButton);

            //go to dialapplet database
            WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[@href = 'redirect.php?subject=database&server=localhost%3A5432%3Aallow&database=dialapplet&']",
                    firefoxDriver);
            SeleniumDAO.click(dialappletDB);

            //Click on Schema->public
            WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'redirect.php?subject=schema&server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&']",
                    firefoxDriver);
            SeleniumDAO.click(publicSchema);

            WebElement typologiesTable = SeleniumDAO.selectElementBy("xpath", "//a[contains(., '" + bdTypologiesTable + "')]", firefoxDriver);
            SeleniumDAO.click(typologiesTable);

            //Click on browse
            WebElement browseButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'display.php?server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&table=dialapplet_predictivedialer_decisiontipology&subject=table&return=table']", firefoxDriver);
            SeleniumDAO.click(browseButton);

            //Click 2 times on id column to order the rows by id
            WebElement idColumn = SeleniumDAO.selectElementBy("xpath", "//a[@href = '?server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&table=dialapplet_predictivedialer_decisiontipology&subject=table&return=table&sortkey=1&sortdir=asc&strings=collapsed&page=1']", firefoxDriver);
            SeleniumDAO.click(idColumn);
            idColumn = SeleniumDAO.selectElementBy("xpath", "//a[@href = '?server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&table=dialapplet_predictivedialer_decisiontipology&subject=table&return=table&sortkey=1&sortdir=desc&strings=collapsed&page=1']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(idColumn);

            Utils.takeScreenshot("./ParteDeShowflowOut/screenshotDB", firefoxDriver);

            return "Test OK. Check your work directory to see the taken screenshot of the database.";


        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String configurePages()
    {
        try
        {
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                e.printStackTrace();
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            //Searchs the new showflow in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(showflowName);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "ERROR: Something went wrong. The showflow does not appear on the showflows table";
            }

            Thread.sleep(1000);
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(showflow);

            WebElement configurePagesTab = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_showflow_pages']/a", firefoxDriver);
            SeleniumDAO.click(configurePagesTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow-pages")));
            //Creates a final page
            WebElement pageNameInput = SeleniumDAO.selectElementBy("id", "page-name", firefoxDriver);
            pageNameInput.sendKeys("Final page");

            WebElement finalRadioButton = SeleniumDAO.selectElementBy("id", "page-final", firefoxDriver);
            SeleniumDAO.click(finalRadioButton);

            WebElement addPageButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/add2.png']", firefoxDriver);
            SeleniumDAO.click(addPageButton);

            Thread.sleep(1000);
            WebElement configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[1]//a[@class = 'edit-page-template']", firefoxDriver);
            SeleniumDAO.click(configurePage);
            configureFinalPage(); //Configures final page

            //Creates a initial page
            pageNameInput = SeleniumDAO.selectElementBy("id", "page-name", firefoxDriver);
            pageNameInput.clear();
            pageNameInput.sendKeys("Initial page");

            WebElement initialRadioButton = SeleniumDAO.selectElementBy("id", "page-initial", firefoxDriver);
            SeleniumDAO.click(initialRadioButton);

            addPageButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/add2.png']", firefoxDriver);
            SeleniumDAO.click(addPageButton);

            Thread.sleep(1000);
            configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[2]//a[@class = 'edit-page-template']", firefoxDriver);
            SeleniumDAO.click(configurePage);
            configureInitialPage(); //Configures initial page


            return "Test OK. Check your work directory to see the taken screenshots of the initial and final pages.";


        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String cloneShowflow()
    {
        try
        {
            /*firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", firefoxDriver);
            SeleniumDAO.click(showflow);*/




            WebElement cloneShowflowTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'clone_showflow']/a", firefoxDriver);
            SeleniumDAO.click(cloneShowflowTab);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("newShowflowCloneName")));
            WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "newShowflowCloneName", firefoxDriver);
            showflowNameInput.clear();
            showflowNameInput.sendKeys(showflowCopyName);

            WebElement sendButton = SeleniumDAO.selectElementBy("id", "cloneShowflow", firefoxDriver);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            WebElement showflowPanelTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(@href, 'showflowPanel.php')]", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(showflowPanelTab));
            SeleniumDAO.click(showflowPanelTab);

            //Searchs the new showflow in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(showflowCopyName);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "ERROR: Something went wrong. The copy of the showflow does not appear on the showflows table";
            }

            WebElement showflowCopy = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]", firefoxDriver);
            SeleniumDAO.click(showflowCopy);

            WebElement contactFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_contact']/a", firefoxDriver);
            SeleniumDAO.click(contactFieldsTab);

            String contactFieldsRes = checkContactFields();
            String showflowFieldsRes = checkShowflowFields();
            String actionFieldsRes = checkActionFields();
            String checkPagesRes = checkPages();

            if(contactFieldsRes.contains("ERROR")) return contactFieldsRes;
            if(showflowFieldsRes.contains("ERROR")) return showflowFieldsRes;
            if(actionFieldsRes.contains("ERROR")) return actionFieldsRes;
            if(checkPagesRes.contains("ERROR")) return checkPagesRes;


             return "Test OK. The cloned showflow matchs with the original";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String checkContactFields()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow_fields")));

            WebElement aux1Checkbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1' and contains(@class, 'contact-field visible isactive')]", firefoxDriver);
            WebElement cityCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city' and contains(@class, 'contact-field visible isactive')]", firefoxDriver);
            WebElement clienteCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'country' and contains(@class, 'contact-field visible isactive')]", firefoxDriver);

            WebElement aux1NameInput = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//td/input[@value = '" + showflowAuxField + "']", firefoxDriver);
            WebElement aux1TypeSelected = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//select[@class = 'type']" +
                    "//option[@value = 'radiobutton' and @selected = '']", firefoxDriver);
            WebElement aux1OptionGroup = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//select[@class = 'optiongroup active']" +
                    "//option[contains(., '" + showflowOptionsGroupName + "')]", firefoxDriver);
            boolean res = aux1OptionGroup.isSelected();
            if(!res) throw new Exception();

            return "Check OK. The contact fields of the cloned showflow match with the original";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The contact fields of the cloned showflow do not match with the original";
        }
    }

    public String checkShowflowFields()
    {
        try
        {
            WebElement showflowFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_fields']/a", firefoxDriver);
            SeleniumDAO.click(showflowFieldsTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow_fields")));

            WebElement question1Name = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + showflowQuestion1 + "']", firefoxDriver);
            WebElement question2Name = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + showflowQuestion2 + "']", firefoxDriver);

            WebElement question1Type = SeleniumDAO.selectElementBy("xpath", "//select[@class = 'type']" +
                    "//option[@value = 'text' and @selected = '']", firefoxDriver);
            WebElement question2Type = SeleniumDAO.selectElementBy("xpath", "//select[@class = 'type']" +
                    "//option[@value = 'checkbox' and @selected = '']", firefoxDriver);

            WebElement question2OptionGroup = SeleniumDAO.selectElementBy("xpath", "//select[@class = 'optiongroup']" +
                    "//option[contains(., '" + showflowOptionsGroupName + "')]", firefoxDriver);
            boolean res = question2OptionGroup.isSelected();
            if(!res) throw new Exception();

            return "Check OK. The showflow fields of the cloned showflow match with the original";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The showflow fields of the cloned showflow do not match with the original";
        }
    }

    public String checkActionFields()
    {
        try
        {
            WebElement actionFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_actions']/a", firefoxDriver);
            SeleniumDAO.click(actionFieldsTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("action_fields")));
            WebElement typologyCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'action-id-1' and @checked = '']", firefoxDriver);
            WebElement subtypologyCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'action-id-2' and @checked = '']", firefoxDriver);
            WebElement observationsCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'action-id-6' and @checked = '']", firefoxDriver);

            WebElement configureActionsButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'configure-action']", firefoxDriver);
            SeleniumDAO.click(configureActionsButton);

            checkDBforShowflowCopy();

            Utils.takeScreenshot("./ParteDeShowflowOut/screenshotDBcopy", firefoxDriver);

            return "Check OK. The action fields of the cloned showflow match with the original. A screenshot of the DB was taken.";

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The action fields of the cloned showflow do not match with the original";
        }
    }

    public String checkPages()
    {
        try
        {
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                e.printStackTrace();
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']")));
            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]")));
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]", firefoxDriver);
            SeleniumDAO.click(showflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit_showflow_pages']/a")));
            WebElement configurePagesTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_pages']/a", firefoxDriver);
            SeleniumDAO.click(configurePagesTab);

            Thread.sleep(1000);
            WebElement configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[1]//a[@class = 'edit-page-template']", firefoxDriver);
            SeleniumDAO.click(configurePage);

            Thread.sleep(2000);

            Utils.takeScreenshot("./ParteDeShowflowOut/screenshotInitialPageCopy", firefoxDriver);

            WebElement backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", firefoxDriver);
            SeleniumDAO.click(backButton);

            Thread.sleep(1000);
            configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[2]//a[@class = 'edit-page-template']", firefoxDriver);
            SeleniumDAO.click(configurePage);

            Thread.sleep(2000);

            Utils.takeScreenshot("./ParteDeShowflowOut/screenshotFinalPageCopy", firefoxDriver);

            return "Check OK. Two screenshots have been taken to compare with the screenshots taken of the original pages";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public void checkDBforShowflowCopy() throws InterruptedException
    {
        try
        {
            firefoxDriver.get(bdUrl);

            SeleniumDAO.switchToFrame("browser", firefoxDriver);

            WebElement postgreSQLServer = SeleniumDAO.selectElementBy("xpath", "//body[@class = 'browser']//a[contains(., 'PostgreSQL')]", firefoxDriver);
            SeleniumDAO.click(postgreSQLServer);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            SeleniumDAO.switchToFrame("detail", firefoxDriver);

            //go to dialapplet database
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'redirect.php?subject=database&server=localhost%3A5432%3Aallow&database=dialapplet&']")));
            WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[@href = 'redirect.php?subject=database&server=localhost%3A5432%3Aallow&database=dialapplet&']",
                    firefoxDriver);
            SeleniumDAO.click(dialappletDB);

            //Click on Schema->public
            WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'redirect.php?subject=schema&server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&']",
                    firefoxDriver);
            SeleniumDAO.click(publicSchema);

            WebElement typologiesTable = SeleniumDAO.selectElementBy("xpath", "//a[contains(., '" + bdTypologiesTable + "')]", firefoxDriver);
            SeleniumDAO.click(typologiesTable);

            //Click on browse
            WebElement browseButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'display.php?server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&table=dialapplet_predictivedialer_decisiontipology&subject=table&return=table']", firefoxDriver);
            SeleniumDAO.click(browseButton);

            //Click 2 times on id column to order the rows by id
            WebElement idColumn = SeleniumDAO.selectElementBy("xpath", "//a[@href = '?server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&table=dialapplet_predictivedialer_decisiontipology&subject=table&return=table&sortkey=1&sortdir=asc&strings=collapsed&page=1']", firefoxDriver);
            SeleniumDAO.click(idColumn);
            idColumn = SeleniumDAO.selectElementBy("xpath", "//a[@href = '?server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&table=dialapplet_predictivedialer_decisiontipology&subject=table&return=table&sortkey=1&sortdir=desc&strings=collapsed&page=1']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(idColumn);


        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void configureInitialPage() throws InterruptedException, IOException
    {
        SeleniumDAO.switchToFrame("showflow_page_editor", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'sidebar-fields']")));

        WebElement thirdSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '3']", firefoxDriver);
        SeleniumDAO.click(thirdSquareRow);
        Thread.sleep(250);
        WebElement secondSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '2']", firefoxDriver);
        SeleniumDAO.click(secondSquareRow);

        Actions actions = new Actions(firefoxDriver);

        List<WebElement> elementsToDrag = firefoxDriver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'contact']"));
        List<WebElement> elementsDroppable = firefoxDriver.findElements(By.xpath("//ul[@id = 'showflow-canvas']//div[contains(@class, 'container ui-droppable')]"));
        for(int i = 0; i < elementsToDrag.size(); i++)
        {
            actions.dragAndDrop(elementsToDrag.get(i), elementsDroppable.get(i)).perform();
        }

        Thread.sleep(2000);
        Utils.takeScreenshot("./ParteDeShowflowOut/screenshotInitialPage", firefoxDriver);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);
        WebElement backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", firefoxDriver);
        SeleniumDAO.click(backButton);
    }

    public void configureFinalPage() throws InterruptedException, IOException
    {
        SeleniumDAO.switchToFrame("showflow_page_editor", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'sidebar-fields']")));

        WebElement secondSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '2']", firefoxDriver);
        SeleniumDAO.click(secondSquareRow);
        Thread.sleep(250);
        WebElement thirdSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '3']", firefoxDriver);
        SeleniumDAO.click(thirdSquareRow);

        Actions actions = new Actions(firefoxDriver);

        //Get the showflow fields
        WebElement showflowFieldsTab = SeleniumDAO.selectElementBy("id", "workflow-fields", firefoxDriver);
        SeleniumDAO.click(showflowFieldsTab);
        List<WebElement> showflowFieldsToDrag = firefoxDriver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'workflow']"));

        //Get the action fields
        WebElement actionFieldsTab = SeleniumDAO.selectElementBy("id", "action-fields", firefoxDriver);
        SeleniumDAO.click(actionFieldsTab);
        List<WebElement> elementsToDrag = firefoxDriver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'action']"));
        elementsToDrag.addAll(showflowFieldsToDrag); //Concat showflow fields and action fields

        List<WebElement> elementsDroppable = firefoxDriver.findElements(By.xpath("//ul[@id = 'showflow-canvas']//div[contains(@class, 'container ui-droppable')]"));

        for(int i = 0; i < elementsToDrag.size(); i++)
        {
            if(i == 3) SeleniumDAO.click(showflowFieldsTab);
            actions.dragAndDrop(elementsToDrag.get(i), elementsDroppable.get(i)).perform();
        }


        Thread.sleep(2000);
        Utils.takeScreenshot("./ParteDeShowflowOut/screenshotFinalPage", firefoxDriver);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);
        WebElement backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", firefoxDriver);
        SeleniumDAO.click(backButton);
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
            add(new Subtypology("AGENDADA2", "NULL", "1"));
        }});
        result.put("VENTA", new ArrayList<Subtypology>() {{
            add(new Subtypology("ADSL", "POSITIVE", "1"));
            add(new Subtypology("MOVIL", "POSITIVE", "1"));
        }});

        return result;
    }

    public void fillSubtypologies(List<Subtypology> subtypologies) throws InterruptedException
    {
        for(Subtypology subtypology : subtypologies) {
            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            WebElement subtypologyName = SeleniumDAO.selectElementBy("id", "new-subtypology-name", firefoxDriver);
            subtypologyName.clear();
            subtypologyName.sendKeys(subtypology.getName());

            Select visibleBySelector = SeleniumDAO.findSelectElementBy("id", "new-subtypology-view", firefoxDriver);
            visibleBySelector.selectByValue(subtypology.getVisibleBy());

            Select resultSelector = SeleniumDAO.findSelectElementBy("id", "new-subtypology-result", firefoxDriver);
            resultSelector.selectByValue(subtypology.getResult());

            WebElement addSubtypologyButton = SeleniumDAO.selectElementBy("id", "add-new-subtypology", firefoxDriver);
            SeleniumDAO.click(addSubtypologyButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);
        }

        WebElement closeIframe = SeleniumDAO.selectElementBy("id", "fancybox-close", firefoxDriver);
        Thread.sleep(1000);
        SeleniumDAO.click(closeIframe);

    }



}
