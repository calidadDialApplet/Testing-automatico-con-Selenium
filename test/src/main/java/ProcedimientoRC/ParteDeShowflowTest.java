package ProcedimientoRC;

import ProcedimientoRC.ParteDeShowflow.Subtypology;
import Utils.Utils;
import Utils.ShowflowUtils;
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
            results.put("--Activate showflow fields  ->  ", activateContactFields());
            results.put("--Create different questions  ->  ", createQuestions());
            results.put("--Create typologies and subtipologies  ->  ", createTypologies());
            results.put("--Import typologies as CSV  ->", importTypologiesCSV());
            results.put("--Check typologies on DB  ->  ",  checkDB());
            results.put("--Configure pages  ->  ", configurePages());
            results.put("Joint two pages  ->  ", jointTwoPages());
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

    //crea un showflow de tipo ticket
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

            String createShowflowRes = ShowflowUtils.createShowflow(firefoxDriver, firefoxWaiting, showflowName, "contact", url);
            if(createShowflowRes.contains("ERROR")) return createShowflowRes;
            else return "Test OK. The showflow has been created";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR: Test failed";
        }
    }

    //Crea un grupo de opciones y activa campos de contacto
    public static String activateContactFields()
    {
        //List with the options of the options group
        List<String> options = new ArrayList<>();
        options.add(showflowOption1);
        options.add(showflowOption2);
        options.add(showflowOption3);
        try
        {
            String activateShowflowFieldsRes = ShowflowUtils.createContactFields(firefoxDriver, firefoxWaiting, options, showflowName, showflowOptionsGroupName, showflowAuxField, false);
            if(activateShowflowFieldsRes.contains("ERROR")) return activateShowflowFieldsRes;

            return "Test OK. The showflow fields name, phone, city, country and aux field: " + showflowAuxField + ", have been activated.";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //Crea dos preguntas
    public String createQuestions()
    {
        List<String> questions = new ArrayList<>();
        questions.add(showflowQuestion1);
        questions.add(showflowQuestion2);
        try
        {
            String createQuestionsRes = ShowflowUtils.createQuestions(firefoxDriver, firefoxWaiting, questions, showflowOptionsGroupName, false);
            if(createQuestionsRes.contains("ERROR")) return createQuestionsRes;

            return "Test OK. The question fields were created and apear on the table.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Something went wrong";
        }
    }

    //crea 4 tipologias con sus subtipologias
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

    //importa dos tipologias por csv ubicado en typologiesCsvPath
    public String importTypologiesCSV()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("csvfile")));
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

    //Comprueba que las tipologias son guardadas en la base de datos tomando una captura
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
            WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[contains(., 'dialapplet')]",
                  firefoxDriver);
            SeleniumDAO.click(dialappletDB);

            //Click on Schema->public
            WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'public')]", firefoxDriver);
            SeleniumDAO.click(publicSchema);

            WebElement typologiesTable = SeleniumDAO.selectElementBy("xpath", "//a[contains(., '" + bdTypologiesTable + "')]", firefoxDriver);
            SeleniumDAO.click(typologiesTable);

            //Click on browse
            WebElement browseButton = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Browse')]", firefoxDriver);
            SeleniumDAO.click(browseButton);

            //Click 2 times on id column to order the rows by id
            WebElement idColumn = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'id')]", firefoxDriver);
            SeleniumDAO.click(idColumn);
            idColumn = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'id')]", firefoxDriver);
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

    //Vuelve a loggearse en dialapplet web y navega hasta configurar paginas
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
            Thread.sleep(200);
            SeleniumDAO.click(showflow);

            //configura una pagina final, inicial e intermedia
            ShowflowUtils.configurePages(firefoxDriver, firefoxWaiting, false);


            return "Test OK. Check your work directory to see the taken screenshots of the initial and final pages.";


        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //une dos paginas
    public String jointTwoPages() {
        try {
            ShowflowUtils.configureJoints(firefoxDriver, firefoxWaiting);

            return "Test OK. The initial page and the final page were joined with the condition name = Sebas";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. The pages could not be joined";
        }
    }

    //clona el showflow y comprueba que los datos son identicos al original
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

            String contactFieldsRes = ShowflowUtils.checkContactFields(firefoxDriver, firefoxWaiting, showflowOptionsGroupName, showflowAuxField);
            String showflowFieldsRes = ShowflowUtils.checkShowflowFields(firefoxDriver, firefoxWaiting, showflowQuestion1, showflowQuestion2, showflowOptionsGroupName, false);
            String actionFieldsRes = checkActionFields(); //Este metodo no está en showflow utils porque solo sirve para showflows de contacto, no para tipo ticket.
            String checkPagesRes = ShowflowUtils.checkPages(firefoxDriver, firefoxWaiting, showflowCopyName, false);

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

            //Inicia sesion para la proxima prueba
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                e.printStackTrace();
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }


            return "Check OK. The action fields of the cloned showflow match with the original. A screenshot of the DB was taken.";

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The action fields of the cloned showflow do not match with the original";
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
            WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[contains(., 'dialapplet')]",
                    firefoxDriver);
            SeleniumDAO.click(dialappletDB);

            //Click on Schema->public
            WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'public')]", firefoxDriver);
            SeleniumDAO.click(publicSchema);

            WebElement typologiesTable = SeleniumDAO.selectElementBy("xpath", "//a[contains(., '" + bdTypologiesTable + "')]", firefoxDriver);
            SeleniumDAO.click(typologiesTable);

            //Click on browse
            WebElement browseButton = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Browse')]", firefoxDriver);
            SeleniumDAO.click(browseButton);

            //Click 2 times on id column to order the rows by id
            WebElement idColumn = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'id')]", firefoxDriver);
            SeleniumDAO.click(idColumn);
            idColumn = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'id')]", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(idColumn);


        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }



    //crea y devuelve un hashmap con las tipologias y subtipologias
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

    //Crea las subtipologias
    public void fillSubtypologies(List<Subtypology> subtypologies) throws InterruptedException
    {
        for(Subtypology subtypology : subtypologies) {
            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("new-subtypology-name")));
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
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(okButton));
            SeleniumDAO.click(okButton);
        }

        WebElement closeIframe = SeleniumDAO.selectElementBy("id", "fancybox-close", firefoxDriver);
        Thread.sleep(1000);
        SeleniumDAO.click(closeIframe);

    }



}
