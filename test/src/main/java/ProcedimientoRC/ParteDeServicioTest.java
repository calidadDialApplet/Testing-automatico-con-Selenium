package ProcedimientoRC;

import Utils.DriversConfig;
import Utils.TestWithConfig;
import Utils.Utils;
import Utils.ServiceUtils;
import gui.Action;
import okhttp3.internal.Util;
import org.ini4j.Wini;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import main.SeleniumDAO;

import java.io.IOException;
import java.util.*;
//TODO cambiar callback assignment a user de manualcallmode agendada
//TODO cambiar en predictive callback agendada a manual y no auto
//TODO cambiar en el modo de llamada manual, en el telefonito, assign to -> user
//TODO Revisar las colas
//TODO elegir callmode for callback en manual
//TODO en predictiva callback en tipologia agendada poner a manual
//TODO en predictiva callback en tipologia agendada poner user y no all
public class ParteDeServicioTest extends TestWithConfig {

    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String serviceID;
    static String serviceCopyName;
    static String campaignBeginningDate;
    static String campaignEndDate;
    static String showflowName;
    static String showflowCopyName;
    static String transfCallModeName;
    static String manualCallModeName;
    static String predictCallModeName;
    static String incomingCallModeName;
    static String queueName;
    static String DID;
    static String agentName4;
    static String agentName5;
    static String agentCoordName;
    static String groupName1y2;
    static String groupName3;
    static String groupName;
    static String robinsonListPath;
    static String importContactsPath;

    int randomInt;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();

    public ParteDeServicioTest(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID", "robinsonListPath", "importContactsPath", "serviceCopyName")));
        requiredParameters.put("Showflow", new ArrayList<>(Arrays.asList("showflowName", "showflowCopyName")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("campaignBeginningDate", "campaignEndDate", "transfCallModeName", "manualCallModeName",
                "predictCallModeName", "incomingCallModeName")));
        requiredParameters.put("Queue", new ArrayList<>(Arrays.asList("queueName", "DID")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName4", "agentName5")));
        requiredParameters.put("Coordinator", new ArrayList<>(Arrays.asList("agentCoordName6")));
        requiredParameters.put("Group", new ArrayList<>(Arrays.asList("groupName1y2", "groupName3", "groupName")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws Exception {
        super.checkParameters();

        try {
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");
            serviceID = commonIni.get("Service", "serviceID");
            serviceCopyName = commonIni.get("Service", "serviceCopyName");
            robinsonListPath = commonIni.get("Service", "robinsonListPath");
            importContactsPath = commonIni.get("Service", "importContactsPath");
            campaignBeginningDate = commonIni.get("CallMode", "campaignBeginningDate");
            campaignEndDate = commonIni.get("CallMode", "campaignEndDate");
            showflowName = commonIni.get("Showflow", "showflowName");
            showflowCopyName = commonIni.get("Showflow", "showflowCopyName");
            transfCallModeName = commonIni.get("CallMode", "transfCallModeName");
            manualCallModeName = commonIni.get("CallMode", "manualCallModeName");
            predictCallModeName = commonIni.get("CallMode", "predictCallModeName");
            incomingCallModeName = commonIni.get("CallMode", "incomingCallModeName");
            queueName = commonIni.get("Queue", "queueName");
            DID = commonIni.get("Queue", "DID");
            agentName4 = commonIni.get("Agent", "agentName4");
            agentName5 = commonIni.get("Agent", "agentName5");
            agentCoordName = commonIni.get("Coordinator", "agentCoordName6");
            groupName1y2 = commonIni.get("Group", "groupName1y2");
            groupName = commonIni.get("Group", "groupName");
            groupName3 = commonIni.get("Group", "groupName3");

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            results.put("--Basic data test  ->  ", basicDataTest());
            results.put("--Showflow test  ->  ", showflowTest());
            results.put("--Call mode test  ->  ", callModeTest());
            results.put("--Coordinators test  ->  ", assignCoordToGroupTest());
            results.put("--Contact data test  ->  ", configureContactDataTest());
            results.put("--Robinson list test  ->  ", insertRobinsonListTest());
            results.put("--Import contacts test  ->  ", importContactsTest());
            results.put("--KPI-SLA test  ->  ", createKPITest());
            results.put("--Clone service test  ->  \n", cloneServiceTest());

            return results;
        } catch (Exception e) {
            return results;
        } finally {
            firefoxDriver.close();
        }
    }

    //MAIN TESTS

    //Configura los datos basicos del servicio creado en ParteDeAgentesTest
    public String basicDataTest() {
        try {
            //Login on dialapplet web
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            //Searchs the service on the services table
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceID);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
            } catch (Exception e) {
                return e.toString() + "ERROR: The service with ID: " + serviceID + "does not appears on the services table";
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceID + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit-service']/a")));
            WebElement editServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit-service']/a", firefoxDriver);
            SeleniumDAO.click(editServiceTab);

            //Add the groups created on ParteDeAgentes to the service
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'groups']/tbody/tr")));
            List<WebElement> assignableGroups = firefoxDriver.findElements(By.xpath("//table[@id = 'groups']/tbody/tr/td[1]/input[@type = 'checkbox']"));
            for (int i = 0; i < assignableGroups.size(); i++) {
                Thread.sleep(500);
                if (!assignableGroups.get(i).isSelected()) {
                    SeleniumDAO.click(assignableGroups.get(i));
                }
            }

            ServiceUtils.setDateTimeCampaign(firefoxDriver, firefoxWaiting, campaignBeginningDate, campaignEndDate);

            //100% recording rate
            ServiceUtils.recordRateTo100(firefoxDriver);


            JavascriptExecutor js = (JavascriptExecutor) firefoxDriver;
            js.executeScript("window.scrollBy(0,250)");
            Utils.takeScreenshot("./ParteDeServicioOut/basicDataScreenshot", firefoxDriver);


            //save changes and go to call modes tab
            WebElement changeButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(changeButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']")));
            WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);
            //Creo que en la 8.3.9 no es necesario
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);

            //Callmode tab
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("campaigns")));
            WebElement firstCallMode = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'campaigns']//tr[1]//img[@class = 'editCampaign']", firefoxDriver);
            SeleniumDAO.click(firstCallMode);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("periodbegin_1")));
            js.executeScript("window.scrollBy(0,800)");
            Utils.takeScreenshot("./ParteDeServicioOut/callModeScreenshot", firefoxDriver);

            //Back to basic data tab
            WebElement basicDataTab = SeleniumDAO.selectElementBy("xpath", "//li[@class = 'tab_service_productivity tab_service_productivity_inactive_complete']//a[contains(., 'Basic data')]", firefoxDriver);
            SeleniumDAO.click(basicDataTab);

            ServiceUtils.setRecordMap(firefoxDriver);

            //Robinson list to yes
            ServiceUtils.setRobinsonToYes(firefoxDriver);

            //Save
            changeButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(changeButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']")));
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(confirmButton));
            SeleniumDAO.click(confirmButton);

            //Checks if robinsonlist tab appears after saving
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[@class = 'tabs_service_productivity']//a[contains(., 'Robinson List')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "\nERROR. The tab robinson list does not appear after marking the robinson list radio button to yes";
            }


            return "Test OK. Check the folder 'ParteDeServicioOut' to see the taken screenshots.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR.";
        }
    }

    //Configura el apartado de showflow de la configuración del servicio
    public String showflowTest() {
        try {
            WebElement basicDataTab = SeleniumDAO.selectElementBy("xpath", "//li[contains(@class, 'tab_service_productivity')]//a[contains(., 'Basic data')]", firefoxDriver);
            SeleniumDAO.click(basicDataTab);

            Select actionTypeSelector = SeleniumDAO.findSelectElementBy("id", "actionsid", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'actionsid']//option[@value = '1']")));
            actionTypeSelector.selectByValue("1");

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow")));
            Select showflowSelector = SeleniumDAO.findSelectElementBy("id", "workflow", firefoxDriver);
            showflowSelector.selectByVisibleText(showflowName);

            //Save
            WebElement changeButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(changeButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']")));
            WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);


            //Showflow tab
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createServiceTable")));
            Select readOnly = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'allownocallshowflow']", firefoxDriver);
            readOnly.selectByVisibleText("ReadOnly"); //TODO refactor con byVisibleValue

            WebElement openShowflowRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'showflowxfer' and @value = 'true']", firefoxDriver);
            SeleniumDAO.click(openShowflowRadioButton);

            WebElement closedContactsRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'closecontacts' and @value = 'true']", firefoxDriver);
            SeleniumDAO.click(closedContactsRadioButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@class = 'clearAll typology']")));
            Select maximumTypologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'maximumtypology']", firefoxDriver);
            maximumTypologySelector.selectByVisibleText("NUM MAX INTENTOS");

            Select maxStoppedTypologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'maxstoppedtypology']", firefoxDriver);
            maxStoppedTypologySelector.selectByVisibleText("INVALIDO");

            Select robinsonTypologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'robinsontypology']", firefoxDriver);
            robinsonTypologySelector.selectByVisibleText("ROBINSON");

            //Permitir volver a tipificar los contactos ya cerrados a sí
            WebElement tipifyclosedContacts = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'radio' and @value = '1']", firefoxDriver);
            SeleniumDAO.click(tipifyclosedContacts);

            Utils.takeScreenshot("./ParteDeServicioOut/showflowOptionsScreenshot", firefoxDriver);

            //save
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(nextButton);

            return "Test OK. The showflow options have been configurated. Check the folder 'ParteDeServicioOut' to see the taken screenshot.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR";
        }
    }

    //Crea y configura modos de llamada
    public String callModeTest() {
        try {

            createTransfCallmode();
            createManualCallmode();
            createPredictiveCallmode();
            createCallbackCallmode();

            configurePredictiveCallback();
            configureManualCallmode();
            configureCallbackCallmode();


            createIncomingRoute();
            createIncomingCampaign();

            return "Test OK. Five call modes have been added: 2 manual, 1 predictive, 1 predictive callback and 1 incoming. Also, a screenshot of the predictive configuration has been taken";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //Asigna coordinador y agentes a un grupo
    public String assignCoordToGroupTest() {
        try {
            //TODO borrar if cuando la 7 desaparezca
            if(url.contains("8")){
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'newServiceAssistant.php?page=5&serviceid=" + serviceID + "']")));
                WebElement coordinatorsTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=5&serviceid=" + serviceID + "']", firefoxDriver);
                Thread.sleep(1500);
                SeleniumDAO.click(coordinatorsTab);
            } else
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'newServiceAssistant.php?page=4&serviceid=" + serviceID + "']")));
                WebElement coordinatorsTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=4&serviceid=" + serviceID + "']", firefoxDriver);
                Thread.sleep(1500);
                SeleniumDAO.click(coordinatorsTab);
            }


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'coordList']//td[contains(., '" + agentCoordName + "')]")));
            WebElement agent4ToDrag = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'agentsList']//td[contains(., '" + agentName4 + "')]", firefoxDriver);
            WebElement agent5ToDrag = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'agentsList']//td[contains(., '" + agentName5 + "')]", firefoxDriver);
            WebElement coordinatorToDrag = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'coordList']//td[contains(., '" + agentCoordName + "')]", firefoxDriver);
            WebElement groupToDrag = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'groupsList']//td[contains(., '" + groupName1y2 + "')]", firefoxDriver);
            WebElement whereToDrag = SeleniumDAO.selectElementBy("id", "midContenido", firefoxDriver);

            Actions actions = new Actions(firefoxDriver);
            actions.dragAndDrop(coordinatorToDrag, whereToDrag).build().perform();
            Thread.sleep(1000);
            actions.dragAndDrop(groupToDrag, whereToDrag).build().perform();
            Thread.sleep(400);
            actions.dragAndDrop(agent4ToDrag, whereToDrag).build().perform();
            Thread.sleep(400);
            actions.dragAndDrop(agent5ToDrag, whereToDrag).build().perform();
            Thread.sleep(400);

            WebElement nextButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit' and @name = 'send']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            return "Test OK. The group " + groupName1y2 + "was assigned to " + agentCoordName;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //Configura los datos de contacto del servicio
    public String configureContactDataTest() {
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'name']")));
            WebElement aux2NameInput = SeleniumDAO.selectElementBy("xpath", "//p[contains(., 'aux2')]/following-sibling::input", firefoxDriver);
            aux2NameInput.clear();
            aux2NameInput.sendKeys("Tiempo");

            WebElement requiredCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux2']//input[@class = 'required']", firefoxDriver);
            Thread.sleep(500);
            if(!requiredCheckbox.isSelected()) SeleniumDAO.click(requiredCheckbox);

            WebElement aux3NameInput = SeleniumDAO.selectElementBy("xpath", "//p[contains(., 'aux3')]/following-sibling::input", firefoxDriver);
            aux3NameInput.clear();
            aux3NameInput.sendKeys("Observaciones");

            Select typeSelector = SeleniumDAO.findSelectElementBy("xpath", "//tr[@data-fieldname = 'aux3']//select[@class = 'type']", firefoxDriver);
            typeSelector.selectByValue("textarea");

            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit-btn", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            return "Test OK. Two aux fields have been added to the service.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //importa una lista robinson por medio de un csv ubicado en robinsonListPath
    public String insertRobinsonListTest() {
        try {
            //TODO eliminar if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "']")));
                Thread.sleep(1500);
                WebElement robinsonListTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "']", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(robinsonListTab);
            }
            else
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(., 'Robinson')]")));
                Thread.sleep(1500);
                WebElement robinsonListTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Robinson')]", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(robinsonListTab);
            }


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/menus/blackListAdd.png']")));
            WebElement importNewPhonesButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/blackListAdd.png']", firefoxDriver);
            SeleniumDAO.click(importNewPhonesButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fileUpload")));
            WebElement browseButton = SeleniumDAO.selectElementBy("id", "fileUpload", firefoxDriver);
            SeleniumDAO.writeInTo(browseButton, robinsonListPath);

            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("progress-bar")));
            WebElement progressBar = SeleniumDAO.selectElementBy("id", "progress-bar", firefoxDriver);

            WebDriverWait waitingRobinsonImport = new WebDriverWait(firefoxDriver, 600);
            waitingRobinsonImport.until(ExpectedConditions.stalenessOf(progressBar));

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //A veces sale un boton y otras no
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            } catch (Exception e) {
            }

            return "Test OK. The robinson list has been imported";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //importa contactos por medio de un csv ubicado en importContactsPath
    public String importContactsTest() {
        try {
            WebElement importContactsTab;
            //TODO eliminar if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href = 'newServiceAssistant.php?page=10&serviceid=" + serviceID + "&from=servicepanel']")));
                Thread.sleep(700);
                importContactsTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=10&serviceid=" + serviceID + "&from=servicepanel']", firefoxDriver);
                SeleniumDAO.click(importContactsTab);
            } else
            {
                firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "&from=servicepanel']")));
                Thread.sleep(700);
                importContactsTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "&from=servicepanel']", firefoxDriver);
                SeleniumDAO.click(importContactsTab);
            }


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("campaignSelDef")));
            Select campaignSelector = SeleniumDAO.findSelectElementBy("id", "campaignSelDef", firefoxDriver);
            campaignSelector.selectByVisibleText(predictCallModeName);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type = 'text' and @name = 'groupName']")));
            WebElement dataBaseNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'text' and @name = 'groupName']", firefoxDriver);
            dataBaseNameInput.sendKeys("DDBB1");

            WebElement newDatabaseButton = SeleniumDAO.selectElementBy("id", "newBDGroup", firefoxDriver);
            SeleniumDAO.click(newDatabaseButton);

            //Waiting to the enable icon
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., 'DDBB1')]/following-sibling::td//img[@title = 'Database enabled']")));

            //import contacts as csv
            WebElement importContactsButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'DDBB1')]/following-sibling::td//img[@src = 'imagenes/import.png']", firefoxDriver);
            SeleniumDAO.click(importContactsButton);

            try {
                SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name = 'userfile']")));
                WebElement browseButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'userfile']", firefoxDriver);
                SeleniumDAO.writeInTo(browseButton, importContactsPath);

                WebElement uploadButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
                SeleniumDAO.click(uploadButton);
                SeleniumDAO.switchToDefaultContent(firefoxDriver);
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "\nERROR. The contacts file could not be loaded. Check the path of the file on the ini file.";
            }


            //waiting to import all uploaded files button
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@id = 'importAllIdA']/input")));
            Thread.sleep(1500);
            WebElement importAllUploadedButton = SeleniumDAO.selectElementBy("xpath", "//a[@id = 'importAllIdA']/input", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(importAllUploadedButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type = 'submit' and @name = 'ready']")));


            Actions actions = new Actions(firefoxDriver);
            //Hacemos drag and drop para mover los elementos
            WebElement nameCard = SeleniumDAO.selectElementBy("id", "row_name", firefoxDriver);
            WebElement phoneCard = SeleniumDAO.selectElementBy("id", "row_phone", firefoxDriver);
            actions.dragAndDrop(phoneCard, nameCard).build().perform();

            WebElement timeCard = SeleniumDAO.selectElementBy("id", "row_aux2", firefoxDriver);
            WebElement thirdCard = SeleniumDAO.selectElementBy("xpath", "//ul[@id = 'databaseList']//li[3]", firefoxDriver);
            actions.dragAndDrop(timeCard, thirdCard).build().perform();

            WebElement observationsCard = SeleniumDAO.selectElementBy("id", "row_aux3", firefoxDriver);
            WebElement fourthCard = SeleniumDAO.selectElementBy("xpath", "//ul[@id = 'databaseList']//li[4]", firefoxDriver);
            actions.dragAndDrop(observationsCard, fourthCard).build().perform();

            WebElement readyButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit' and @name = 'ready']", firefoxDriver);
            SeleniumDAO.click(readyButton);
            Thread.sleep(500);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //switch tabs to reload the page
            //TODO eliminar if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "']")));
                Thread.sleep(1000);
                WebElement robinsonListTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "']", firefoxDriver);
                SeleniumDAO.click(robinsonListTab);
            } else
            {
                firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(., 'Robinson')]")));
                Thread.sleep(1000);
                WebElement robinsonListTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Robinson')]", firefoxDriver);
                SeleniumDAO.click(robinsonListTab);
            }


            //TODO borrar if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                importContactsTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=10&serviceid=" + serviceID + "&from=servicepanel']", firefoxDriver);
                SeleniumDAO.click(importContactsTab);
            } else
            {
                importContactsTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=9&serviceid=" + serviceID + "&from=servicepanel']", firefoxDriver);
                SeleniumDAO.click(importContactsTab);
            }


            //Takes a screenshot of the results
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@onclick = 'showHideStatisticHistory();']")));
            WebElement showHistoryButton = SeleniumDAO.selectElementBy("xpath", "//div[@onclick = 'showHideStatisticHistory();']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(showHistoryButton);

            Utils.takeScreenshot("./ParteDeServicioOut/importDataScreenshot", firefoxDriver);

            //Changes the dates to see if the enabled icon disappears
            WebElement editDBButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'DDBB1')]/following-sibling::td//img[@src = 'imagenes/edit2.png']", firefoxDriver);
            SeleniumDAO.click(editDBButton);
            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            WebElement startDateButton = SeleniumDAO.selectElementBy("xpath", "//td[input[@value = 'DDBB1']]/following-sibling::td/img[@id = 'f_trigger_start']", firefoxDriver);
            SeleniumDAO.click(startDateButton);

            WebElement nextYearArrows = SeleniumDAO.selectElementBy("xpath", "//td[@class = 'button' and contains(., '»')]", firefoxDriver);
            SeleniumDAO.click(nextYearArrows);

            WebElement editButton = SeleniumDAO.selectElementBy("id", "editBDGroup", firefoxDriver);
            SeleniumDAO.click(editButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("enableCampaign")));
            WebElement showDisabledDBCheckbox = SeleniumDAO.selectElementBy("id", "enableCampaign", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(showDisabledDBCheckbox);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., 'DDBB1')]")));
                Utils.takeScreenshot("./ParteDeServicioOut/disabledDBScreenshot", firefoxDriver);
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "\nERROR. The database does not show as disabled";
            }


            //Set the dates as before to keep the DB enabled
            editDBButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'DDBB1')]/following-sibling::td//img[@src = 'imagenes/edit2.png']", firefoxDriver);
            SeleniumDAO.click(editDBButton);
            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            startDateButton = SeleniumDAO.selectElementBy("xpath", "//td[input[@value = 'DDBB1']]/following-sibling::td/img[@id = 'f_trigger_start']", firefoxDriver);
            SeleniumDAO.click(startDateButton);

            WebElement previousYearArrows = SeleniumDAO.selectElementBy("xpath", "//td[@class = 'button' and contains(., '«')]", firefoxDriver);
            SeleniumDAO.click(previousYearArrows);

            editButton = SeleniumDAO.selectElementBy("id", "editBDGroup", firefoxDriver);
            SeleniumDAO.click(editButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            return "Test OK. The contacts have been imported and 2 screenshots have been taken to check if the result is correct.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //Crea 10 modos KPI y los configura
    public String createKPITest() {
        try {
            ArrayList<String> kpiNames = new ArrayList<>(){{
                add("abandoned");
                add("closed");
                add("incomingcalls");
                add("outgoingcalls");
                add("percentageclosed");
                add("percentagepositiveclosed");
                add("administration");
                add("positives");
                add("useful");
                add("notuseful");
            }};

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'currentStatus']//a[contains(., 'KPI-SLA')]")));
            WebElement kpiTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'currentStatus']//a[contains(., 'KPI-SLA')]", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(kpiTab);

            for(String kpiName : kpiNames)
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("create")));
                Thread.sleep(1500);
                WebElement createKPI = SeleniumDAO.selectElementBy("id", "create", firefoxDriver);
                firefoxWaiting.until(ExpectedConditions.elementToBeClickable(createKPI));
                Thread.sleep(500);
                SeleniumDAO.click(createKPI);

                Thread.sleep(1000);

                switch (kpiName)
                {
                    case "abandoned": createAbandonedKPI(kpiName); break;
                    case "closed": createClosedKPI(kpiName); break;
                    case "incomingcalls": createIncomingCallsKPI(kpiName); break;
                    case "outgoingcalls": createOutgoingCallsKPI(kpiName); break;
                    case "percentageclosed": createPercentageClosedKPI(kpiName); break;
                    case "percentagepositiveclosed": createPercentagePositiveClosedKPI(kpiName); break;
                    case "administration": createAdministrationKPI(kpiName); break;
                    case "positives": createPositivesKPI(kpiName); break;
                    case "useful": createUsefulKPI(kpiName); break;
                    case "notuseful": createNotUsefulKPI(kpiName); break;

                }

                WebElement createButton = SeleniumDAO.selectElementBy("id", "saveKpi", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(createButton);
            }

            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("finish")));
            Thread.sleep(2000);
            WebElement finishButton = SeleniumDAO.selectElementBy("id", "finish", firefoxDriver);
            SeleniumDAO.click(finishButton);

            return "Test OK. The KPI configuration has been added.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //Clona el servicio y comprueba que la configuracion es identica a la del original
    public String cloneServiceTest() {
        //Login on dialapplet web
        firefoxDriver.get(url + "dialapplet-web");
        Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
        } catch (Exception e) {
            //System.err.println("ERROR: Login failed");
            return e.toString() + "\n ERROR: Login failed";
        }







        String res = "";
        try {
            //Searchs the service on the services table
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceID);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "\nERROR. The service has not been found on the services tab";
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'service-" + serviceID + "']/td[1]", firefoxDriver);
            SeleniumDAO.click(service);

            cloneService();

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceCopyName + "')]")));
            WebElement serviceCopy = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceCopyName + "')]", firefoxDriver);
            SeleniumDAO.click(serviceCopy);

            WebElement editService = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit-service']/a", firefoxDriver);
            SeleniumDAO.click(editService);

            if(!checkClonedBasicData()) res = Utils.createResponse(res, "The basic data is different from the original service.");
            if(!checkShowflow()) res = Utils.createResponse(res, "The showflow config is different from the original service");
            if(!checkCallmodes()) res = Utils.createResponse(res, "The callmode config is different from the original service");
            if(!checkCoordinators()) res = Utils.createResponse(res, "The coordinators config is different from the original service");
            if(!checkContactData()) res = Utils.createResponse(res, "The contact data config is different from the original service");
            if(!checkImportContacts()) res = Utils.createResponse(res, "The imported contact data config is different from the original service");
            if(!checkKPI()) res = Utils.createResponse(res, "The KPI-SLA config is different from the original service");


            if(res.equals("")) return "Test OK. The cloned service has the same config as the original";
            else return Utils.createResponse(res, "ERROR");
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR.";
        }
    }


    //Auxiliar methods
    public void createAbandonedKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select SLASelector = SeleniumDAO.findSelectElementBy("id", "sla", firefoxDriver);
            SLASelector.selectByValue("t");

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("agree")));
            WebElement agreePercentageInput = SeleniumDAO.selectElementBy("id", "agree", firefoxDriver);
            agreePercentageInput.clear();
            agreePercentageInput.sendKeys("1");

            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            SeleniumDAO.click(adminCheckBox);

            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            SeleniumDAO.click(coordCheckBox);

            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);
            SeleniumDAO.click(agentCheckBox);

            Select technicalSecSelector = SeleniumDAO.findSelectElementBy("id", "technical", firefoxDriver);
            technicalSecSelector.selectByValue("1");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void createClosedKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createIncomingCallsKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            SeleniumDAO.click(adminCheckBox);

            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            SeleniumDAO.click(coordCheckBox);

            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);
            SeleniumDAO.click(agentCheckBox);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createOutgoingCallsKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-3");

            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            SeleniumDAO.click(adminCheckBox);

            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            SeleniumDAO.click(coordCheckBox);

            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);
            SeleniumDAO.click(agentCheckBox);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createPercentageClosedKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");

            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            SeleniumDAO.click(adminCheckBox);

            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            SeleniumDAO.click(coordCheckBox);

            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);
            SeleniumDAO.click(agentCheckBox);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createPercentagePositiveClosedKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");

            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            SeleniumDAO.click(adminCheckBox);

            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            SeleniumDAO.click(coordCheckBox);

            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);
            SeleniumDAO.click(agentCheckBox);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createAdministrationKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");

            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            SeleniumDAO.click(adminCheckBox);

            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            SeleniumDAO.click(coordCheckBox);

            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);
            SeleniumDAO.click(agentCheckBox);

            WebElement secondsOkayInput = SeleniumDAO.selectElementBy("id", "agreeFix", firefoxDriver);
            secondsOkayInput.sendKeys("5");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createPositivesKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createUsefulKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createNotUsefulKPI(String kpiName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'kpi-name']")));
            Select selectKPI = SeleniumDAO.findSelectElementBy("id", "key", firefoxDriver);
            selectKPI.selectByValue(kpiName);

            Thread.sleep(1000);

            WebElement kpiNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'kpi-name']", firefoxDriver);
            kpiNameInput.sendKeys(kpiName);

            Select applyToSelector = SeleniumDAO.findSelectElementBy("id", "campaign", firefoxDriver);
            applyToSelector.selectByValue("-1");

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void configureDelaysNoContacted()
    {
        try {
            WebElement attemptsInput = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'delay']/p/input[@class = 'maximum limit']", firefoxDriver);
            attemptsInput.clear();
            attemptsInput.sendKeys("20");

            WebElement unansweredInput = SeleniumDAO.selectElementBy("id", "unanswered+t", firefoxDriver);
            unansweredInput.clear();
            unansweredInput.sendKeys("5");

            WebElement congestionInput = SeleniumDAO.selectElementBy("id", "congestion+t", firefoxDriver);
            congestionInput.clear();
            congestionInput.sendKeys("5");

            WebElement rejectedInput = SeleniumDAO.selectElementBy("id", "rejected+t", firefoxDriver);
            rejectedInput.clear();
            rejectedInput.sendKeys("5");

            WebElement invalidCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'checkbox' and @name = 'stop-invalid+t']", firefoxDriver);
            if (!invalidCheckbox.isSelected()) SeleniumDAO.click(invalidCheckbox);

            WebElement defaultInput = SeleniumDAO.selectElementBy("id", "default_t", firefoxDriver);
            defaultInput.clear();
            defaultInput.sendKeys("10");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void configureDelaysContacted()
    {
        try {
            WebElement attemptsAfterInput = SeleniumDAO.selectElementBy("id", "attempts_after", firefoxDriver);
            attemptsAfterInput.clear();
            attemptsAfterInput.sendKeys("20");

            WebElement defaultInput = SeleniumDAO.selectElementBy("id", "contacted_default_t", firefoxDriver);
            defaultInput.clear();
            defaultInput.sendKeys("20");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void configureTypologies()
    {
        try {
            //AGENDADA - PENDIENTE
            WebElement manualRadioButton = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'tipology']//table[1]//label[2]/input[@type = 'radio']", firefoxDriver);
            SeleniumDAO.click(manualRadioButton);

            Select callbackAssignmentSelector = SeleniumDAO.findSelectElementBy("xpath", "//div[@id = 'tipology']//table[1]//select[contains(@id, 'agent_')]", firefoxDriver);
            callbackAssignmentSelector.selectByValue("user");

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("defaultDelayST")));
            WebElement delayInput = SeleniumDAO.selectElementBy("id", "defaultDelayST", firefoxDriver);
            delayInput.clear();
            delayInput.sendKeys("0"); //Antes estaba a 10 pero para facilitar las pruebas del web client se ha cambiado a 0

            //AGENDADA2 - PENDIENTE
            WebElement autoRadioButton = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'tipology']//table[2]//label[1]/input[@type = 'radio']", firefoxDriver);
            SeleniumDAO.click(autoRadioButton);

            Select destinyCallmodeSelector = SeleniumDAO.findSelectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA2')]]]/following-sibling::tr" +
                    "//select[contains(@name, 'campaigndest_')]", firefoxDriver);
            destinyCallmodeSelector.selectByVisibleText(predictCallModeName + " Callback");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void createIncomingRoute() throws InterruptedException
    {
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(200) + 1;
        try {
            /*
            //Login on dialapplet web
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }*/

            //Go to ACD tab
            SeleniumDAO.switchToDefaultContent(firefoxDriver);//Esta linea es por un bug del mozilla, dicen que se arregla así
            Thread.sleep(1500);
            WebElement ACDTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'ACD']/a", firefoxDriver);
            SeleniumDAO.click(ACDTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'ACDQueues.php']")));
            WebElement queuesButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'ACDQueues.php']", firefoxDriver);
            SeleniumDAO.click(queuesButton);

            //Create a queue
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'ACDEditQueues.php']")));
            WebElement createQueueButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'ACDEditQueues.php']", firefoxDriver);
            SeleniumDAO.click(createQueueButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("text-context")));
            WebElement queueNameInput = SeleniumDAO.selectElementBy("id", "text-context", firefoxDriver);
            queueNameInput.sendKeys(queueName + Integer.toString(randomInt));

            WebElement insertButton = SeleniumDAO.selectElementBy("id", "formSubmit", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(insertButton);


            ACDTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'ACD']/a", firefoxDriver);
            SeleniumDAO.click(ACDTab);

            //bind the queue with the inbound route
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'ACDInbound.php']")));
            WebElement inboundRouteButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'ACDInbound.php']", firefoxDriver);
            SeleniumDAO.click(inboundRouteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("searchInbound")));
            WebElement searchInboundRadioButton = SeleniumDAO.selectElementBy("id", "searchInbound", firefoxDriver);
            Thread.sleep(250);
            SeleniumDAO.click(searchInboundRadioButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("selectDID")));
            Select DIDSelector = SeleniumDAO.findSelectElementBy("id", "selectDID", firefoxDriver);
            //TODO borrar este if, es para hacer un apaño y que funcione en la 7rc
            if(url.contains("8"))
            {
                DIDSelector.selectByVisibleText(DID + " (Ruta entrante selenium)");
            }
            else
            {
                DIDSelector.selectByVisibleText(DID + " (sebas)");
            }


            Thread.sleep(500);
            WebElement editButton = SeleniumDAO.selectElementBy("xpath", "//form[@id = 'formulario-search-inbound']//input[@value = 'Edit']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(editButton));
            SeleniumDAO.click(editButton);

            //Waiting to the svg window
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[text() = '" + DID + "']")));

            //Delete the existing and needed conection
            WebElement elementToDelete = SeleniumDAO.selectElementBy("xpath", "//*[name() = 'svg']//*[contains(., '" + queueName + "')]", firefoxDriver);
            Actions actions = new Actions(firefoxDriver);
            actions.click(elementToDelete).build().perform();

            WebElement deleteElementsButton = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'tool-buttons']//img[@src = 'imagenes/apps/delete.png']", firefoxDriver);
            actions = new Actions(firefoxDriver);
            actions.click(deleteElementsButton).build().perform();


            Select nodeTypeSelector = SeleniumDAO.findSelectElementBy("id", "nodeType", firefoxDriver);
            Thread.sleep(250);
            nodeTypeSelector.selectByValue("3");

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("newNode")));
            Select queueSelector = SeleniumDAO.findSelectElementBy("id", "newNode", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'newNode']/option[contains(., '" + queueName + Integer.toString(randomInt) + "')]")));
            queueSelector.selectByVisibleText(queueName + Integer.toString(randomInt));

            WebElement addElementButton = SeleniumDAO.selectElementBy("id", "addNode", firefoxDriver);
            SeleniumDAO.click(addElementButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'fancybox-content']//input[@type = 'button' and @value = 'Save']")));
            WebElement saveButton = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'fancybox-content']//input[@type = 'button' and @value = 'Save']", firefoxDriver);
            SeleniumDAO.click(saveButton);

            Thread.sleep(500);
            WebElement connectElementsButton = SeleniumDAO.selectElementBy("id", "conectIcono", firefoxDriver);
            SeleniumDAO.click(connectElementsButton);

            WebElement route = SeleniumDAO.selectElementBy("xpath", "//*[name() = 'svg']//*[contains(., '" + DID + "')]", firefoxDriver);
            actions = new Actions(firefoxDriver);
            actions.click(route).build().perform();

            WebElement queue = SeleniumDAO.selectElementBy("xpath", "//*[name() = 'svg']//*[contains(., '" + queueName + "')]", firefoxDriver);
            actions = new Actions(firefoxDriver);
            actions.click(queue).build().perform();

            WebElement saveRouteButton = SeleniumDAO.selectElementBy("id", "saveInbound", firefoxDriver);
            SeleniumDAO.click(saveRouteButton);
            Thread.sleep(2000);
            //return "test OK";
        } catch (Exception e) {

            e.printStackTrace();
            //return "ERROR";
            throw e;
        }
    }

    //Crea un modo de llamada entrante con la cola creada
    public void createIncomingCampaign() throws InterruptedException
    {
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'mainMenu']//li[@id = 'OPERATION']/a")));
            WebElement operationsTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'OPERATION']/a", firefoxDriver);
            SeleniumDAO.click(operationsTab);

            //Searchs the service on the services table
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceID);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'service-" + serviceID + "']/td[1]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit-service']/a")));
            WebElement editServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit-service']/a", firefoxDriver);
            SeleniumDAO.click(editServiceTab);

            //TODO eliminar if cuando la 7rc desaparezca
            if(url.contains("8"))
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'newServiceAssistant.php?page=3&serviceid=" + serviceID + "']")));
                WebElement callmodesTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=3&serviceid=" + serviceID + "']", firefoxDriver);
                SeleniumDAO.click(callmodesTab);
            } else
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'newServiceAssistant.php?page=2&serviceid=" + serviceID + "']")));
                WebElement callmodesTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'newServiceAssistant.php?page=2&serviceid=" + serviceID + "']", firefoxDriver);
                SeleniumDAO.click(callmodesTab);
            }


            //Create new callmode: manual transferencia
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createCampaign")));
            WebElement createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
            SeleniumDAO.click(createCallModeButton);


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            Select dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
            dialingModeSelector.selectByValue("incoming");

            WebElement nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
            nameInput.sendKeys(incomingCallModeName);

            Select queueSelector = SeleniumDAO.findSelectElementBy("id", "queue", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'queue']//option[contains(., '" + queueName + Integer.toString(randomInt) + "')]")));
            queueSelector.selectByVisibleText(queueName + Integer.toString(randomInt));

            //Este grupo solo estará en la de transferencia para comprobar los filtros del panel de extensiones en el parte de webclient
            WebElement grupo1y2Checkbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + groupName1y2 + "')]", firefoxDriver);
            SeleniumDAO.click(grupo1y2Checkbox);

            WebElement addButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            Actions actions = new Actions(firefoxDriver);
            actions.sendKeys(Keys.ESCAPE).perform();
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //Este metodo hace la accion de clonar el servicio, el test es el encargado de llamarlo y de llamar a los métodos que comprueban la configuración
    public void cloneService() throws InterruptedException {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'clone-service']/a")));
            WebElement cloneServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'clone-service']/a", firefoxDriver);
            SeleniumDAO.click(cloneServiceTab);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service")));

            WebElement serviceNameInput = SeleniumDAO.selectElementBy("id", "service", firefoxDriver);
            serviceNameInput.clear();
            serviceNameInput.sendKeys(serviceCopyName);

            WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "clone-current-showflow", firefoxDriver);
            showflowNameInput.clear();
            showflowNameInput.sendKeys(showflowCopyName + "forService");

            WebElement incomingCallmodeInput = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + incomingCallModeName + "']", firefoxDriver);
            incomingCallmodeInput.clear();
            incomingCallmodeInput.sendKeys(incomingCallModeName + "forService");

            WebElement manualCallmodeInput = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + manualCallModeName + "']", firefoxDriver);
            manualCallmodeInput.clear();
            manualCallmodeInput.sendKeys(manualCallModeName + "forService");

            WebElement predictiveCallmodeInput = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + predictCallModeName + "']", firefoxDriver);
            predictiveCallmodeInput.clear();
            predictiveCallmodeInput.sendKeys(predictCallModeName + "forService");

            WebElement predictiveCallbackCallmodeInput = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + predictCallModeName + " Callback" + "']", firefoxDriver);
            predictiveCallbackCallmodeInput.clear();
            predictiveCallbackCallmodeInput.sendKeys(predictCallModeName + " CallbackforService");

            WebElement transferCallmodeInput = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + transfCallModeName + "']", firefoxDriver);
            transferCallmodeInput.clear();
            transferCallmodeInput.sendKeys(transfCallModeName + "forService");

            //TODO borrar este if cuando desaparezca la version 7
            if(url.contains("7"))
            {
                WebElement cloneBDCheckbox = SeleniumDAO.selectElementBy("id", "cloneDatabaseStructure", firefoxDriver);
                SeleniumDAO.click(cloneBDCheckbox);
            }

            //TODO borrar este if cuando desparezca la version 7
            if(url.contains("8")){
                WebElement submitButton = SeleniumDAO.selectElementBy("id", "sendButton", firefoxDriver);
                SeleniumDAO.click(submitButton);
            }
            else
            {
                WebElement submitButton = SeleniumDAO.selectElementBy("name", "submitButton", firefoxDriver);
                SeleniumDAO.click(submitButton);
            }


            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sweet-alert showSweetAlert visible']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            //TODO borrar este if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkClonedBasicData()
    {
        boolean res = true;
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'workflow']//option[contains(., '" + showflowCopyName + "')]")));

            Select recordingRateSelector = SeleniumDAO.findSelectElementBy("id", "recordingrate", firefoxDriver);
            if(!recordingRateSelector.getFirstSelectedOption().getText().equals("99")) res = false;

            WebElement recordLabel = SeleniumDAO.selectElementBy("id", "audioformat", firefoxDriver);
            if(!recordLabel.getAttribute("value").equals("RCNvXYZ%20-%21-%1-%9-%8-%6")) res = false;

            WebElement beginningPeriod = SeleniumDAO.selectElementBy("id","periodbegin_1", firefoxDriver);
            if(!beginningPeriod.getAttribute("value").equals("2020-03-15")) res = false;
            WebElement endPeriod = SeleniumDAO.selectElementBy("id", "periodend_1", firefoxDriver);
            if(!endPeriod.getAttribute("value").equals("2022-07-25")) res = false;

            List<WebElement> assignableGroups = firefoxDriver.findElements(By.xpath("//table[@id = 'groups']/tbody/tr/td[1]/input[@type = 'checkbox']"));
            for(WebElement assignableGroup : assignableGroups)
            {
                if(!assignableGroup.isSelected()) res = false;
            }

            Utils.takeScreenshot("./ParteDeServicioOut/ClonedServiceOut/basicDataScreenshot", firefoxDriver);

            return res;
        } catch (Exception e)
        {
            res = false;
            e.printStackTrace();
            return res;
        }
    }

    public boolean checkShowflow()
    {
        boolean res = true;
        try
        {
            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//li[@class = 'tab_service_productivity tab_service_productivity_inactive_complete']/a", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@name = 'allownocallshowflow']")));
            Select readOnlySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'allownocallshowflow']", firefoxDriver);
            if(!readOnlySelector.getFirstSelectedOption().getText().equals("ReadOnly")) res = false;

            WebElement openSFTransferingRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'showflowxfer']", firefoxDriver);
            if(!openSFTransferingRadioButton.isSelected()) res = false;

            WebElement allContactClosedRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'closecontacts']", firefoxDriver);
            if(!allContactClosedRadioButton.isSelected()) res = false;

            Select limitReachedSelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'maximumtypology']", firefoxDriver);
            if(!limitReachedSelector.getFirstSelectedOption().getText().equals("NUM MAX INTENTOS")) res = false;

            Select dialerStopsSelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'maxstoppedtypology']", firefoxDriver);
            if(!dialerStopsSelector.getFirstSelectedOption().getText().equals("INVALIDO")) res = false;

            Select typologyRobinsonSelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'robinsontypology']", firefoxDriver);
            if(!typologyRobinsonSelector.getFirstSelectedOption().getText().equals("ROBINSON")) res = false;

            Select contactDisabledSelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'disabledtypology']", firefoxDriver);
            if(!contactDisabledSelector.getFirstSelectedOption().getAttribute("value").equals("-1")) res = false;

            WebElement action1RadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'sfnotopenedaction']", firefoxDriver);
            if(!action1RadioButton.isSelected()) res = false;

            WebElement action2RadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'sfnotclosedaction']", firefoxDriver);
            if(!action2RadioButton.isSelected()) res = false;

            return res;
        } catch (Exception e)
        {
            res = false;
            e.printStackTrace();
            return res;
        }
    }

    public boolean checkCallmodes()
    {
        boolean res = true;
        try
        {
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody[@class = 'ui-sortable']/tr/td[3]")));

            //Comprueba si en la tabla de modos de llamada estan los nombres que utilizamos para crearlos
            List<WebElement> callmodeNames = firefoxDriver.findElements(By.xpath("//tbody[@class = 'ui-sortable']/tr/td[3]"));
            List<String> callmodeStringNames = new ArrayList<>(Arrays.asList(predictCallModeName + "forService", predictCallModeName + " CallbackforService", incomingCallModeName + "forService",
                    manualCallModeName + "forService", transfCallModeName + "forService", "auxiliarCallmode"));
            for(WebElement callmodeName : callmodeNames)
            {
                if(!callmodeStringNames.contains(callmodeName.getText())) res = false;
            }

            //Comprobamos si el modo favorito es el mismo que en el servicio original. Si no puede encontrar el elemento web significará que no está marcado  como fav y saltará una excepción
            WebElement favouriteCallmode = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + predictCallModeName + "')]/following-sibling::td//img[@class = 'baseStarOn']",
                    firefoxDriver);
            //Comprobamos la configuracion del modo favorito
            WebElement phoneButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + predictCallModeName + "forService" + "')]/following-sibling::td//img[@src = 'imagenes/oldphone.png']",
                    firefoxDriver);
            SeleniumDAO.click(phoneButton);
            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'maximumtype']//option[contains(., ' Combined (Delay - Typology)')]")));

            Select callTypeSelector = SeleniumDAO.findSelectElementBy("id", "maximumtype", firefoxDriver);
            if(!callTypeSelector.getFirstSelectedOption().getText().equals("Combined (Delay - Typology)")) res = false;

            //si no se encuentran y salta exception es porque el valor es incorrecto
            WebElement noContactedAttempts = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'maximum limit' and @value = '20']", firefoxDriver);
            WebElement noContactedUnanswered = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'unanswered+t' and @value = '5']", firefoxDriver);
            WebElement noContactedCongestion = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'congestion+t' and @value = '5']", firefoxDriver);
            WebElement noContactedRejected = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'rejected+t' and @value = '5']", firefoxDriver);
            WebElement noContactedDefault = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'default_t' and @value = '10']", firefoxDriver);
            WebElement noContactedStopCallingCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'stop-invalid+t']", firefoxDriver);
            if(!noContactedStopCallingCheckbox.isSelected()) res = false;

            WebElement contactedAttempts = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'attempts_after' and @value = '20']", firefoxDriver);
            WebElement contactedDefault = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'contacted_default_t' and @value = '20']", firefoxDriver);
            WebElement contactedStopCallingCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'stop-contacted_invalid+t']", firefoxDriver);
            if(!contactedStopCallingCheckbox.isSelected()) res = false;

            //PENDIENTE - AGENDADA
            WebElement manualRadioButton = SeleniumDAO.selectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//label[2]/input[@type = 'radio']", firefoxDriver);
            if(!manualRadioButton.isSelected()) res = false;
            Select callbackAssignmentSelector = SeleniumDAO.findSelectElementBy("xpath", "//div[@id = 'tipology']//table[1]//select[contains(@id, 'agent_')]", firefoxDriver);
            if(!callbackAssignmentSelector.getFirstSelectedOption().getAttribute("value").equals("user")) res = false;
            WebElement defaultDelay = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'defaultDelayST' and @value = '0']", firefoxDriver);

            //PENDIENTE - AGENDADA2
            WebElement autoRadioButton = SeleniumDAO.selectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA2')]]]/following-sibling::tr//label[1]/input[@type = 'radio']", firefoxDriver);
            if(!autoRadioButton.isSelected()) res = false;
            Select destinyCallmodeSelector = SeleniumDAO.findSelectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA2')]]]/following-sibling::tr" +
                    "//select[contains(@name, 'campaigndest_')]", firefoxDriver);
            if(!destinyCallmodeSelector.getFirstSelectedOption().getText().equals(predictCallModeName + " CallbackforService")) res = false;

            WebElement sendButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(sendButton);
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            return res;

        } catch (Exception e)
        { res = false;
            e.printStackTrace();
            return res;
        }
    }

    public boolean checkCoordinators()
    {
        boolean res = true;
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("next")));
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "next", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'midContenido']//td[contains(., '" + agentCoordName + "')]")));
            //Intenta encontrar los elementos. Si no, salta excepcion indicando que los servicios no son iguales
            WebElement agent = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'midContenido']//td[contains(., '" + agentCoordName + "')]", firefoxDriver);
            WebElement group = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'midContenido']//td[contains(., '" + groupName1y2 + "')]", firefoxDriver);

            return res;
        } catch (Exception e)
        {
            res = false;
            e.printStackTrace();
            return res;
        }
    }

    public boolean checkContactData()
    {
        boolean res = true;
        try
        {
            WebElement nextButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit']", firefoxDriver);
            SeleniumDAO.click(nextButton);

            WebElement aux2NameInput = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'name' and @value = 'Tiempo']", firefoxDriver);
            WebElement aux2RequiredCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux2']//input[@class = 'required']", firefoxDriver);
            if(!aux2RequiredCheckbox.isSelected()) res = false;
            WebElement aux3NameInput = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'name' and @value = 'Observaciones']", firefoxDriver);
            Select aux3TypeSelector = SeleniumDAO.findSelectElementBy("xpath", "//tr[@data-fieldname = 'aux3']//select[@class = 'type']", firefoxDriver);
            if(!aux3TypeSelector.getFirstSelectedOption().getAttribute("value").equals("textarea")) res = false;

            return res;
        } catch (Exception e)
        {
            res = false;
            e.printStackTrace();
            return res;
        }
    }

    public boolean checkImportContacts()
    {
        boolean res = true;
        try
        {
            //Se hace la navegacion entre pestañas asi porque clickar en las pestañas es complicado
            //NExt to web client tab
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("submit-btn")));
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit-btn", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);
            //Next to robinson list tab
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("submit")));
            nextButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);
            //Next to import contacts
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("button")));
            nextButton = SeleniumDAO.selectElementBy("id", "button", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("campaignSelDef")));
            Select callModeSelector = SeleniumDAO.findSelectElementBy("id", "campaignSelDef", firefoxDriver);
            if(!callModeSelector.getFirstSelectedOption().getText().equals(predictCallModeName + "forService")) res = false;

            WebElement database = SeleniumDAO.selectElementBy("xpath", "//tr[@data-enabled = 'true']//td[contains(., 'DDBB1')]", firefoxDriver);

            return res;
        } catch(Exception e)
        {
            res = false;
            e.printStackTrace();
            return res;
        }
    }

    public boolean checkKPI()
    {
        boolean res = true;
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("next")));
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "next", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., 'abandoned')]/following-sibling::td//img[@src = 'imagenes/edit2.png']")));
            WebElement editButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'abandoned')]/following-sibling::td//img[@src = 'imagenes/edit2.png']", firefoxDriver);
            SeleniumDAO.click(editButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("alertadmin")));
            WebElement adminCheckBox = SeleniumDAO.selectElementBy("id", "alertadmin", firefoxDriver);
            WebElement coordCheckBox = SeleniumDAO.selectElementBy("id", "alertcoordinator", firefoxDriver);
            WebElement agentCheckBox = SeleniumDAO.selectElementBy("id", "alertagent", firefoxDriver);

            if(!adminCheckBox.isSelected() || !coordCheckBox.isSelected() || !agentCheckBox.isSelected()) res = false;

            List<WebElement> kpisList = firefoxDriver.findElements(By.xpath("//table[@id = 'table-kpi-sla-service']//tbody/tr"));
            if(kpisList.size() != 10) res = false;
            return res;

        } catch (Exception e)
        {
            res = false;
            e.printStackTrace();
            return res;
        }

    }


    public void createTransfCallmode()
    {
        //Create new callmode: manual transferencia
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createCampaign")));
        WebElement createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
        SeleniumDAO.click(createCallModeButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        Select dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
        dialingModeSelector.selectByValue("manual");

        WebElement nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
        nameInput.sendKeys(transfCallModeName);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("campaignfortransfersyes")));
        WebElement transferRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'campaignfortransfersyes']", firefoxDriver);
        SeleniumDAO.click(transferRadioButton);

        //Se deja unicamente el grupo1y2 dentro para comprobar mas adelante en el parte de webclient el panel de extensiones
        WebElement grupo3Checkbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + groupName3 + "')]", firefoxDriver);
        SeleniumDAO.click(grupo3Checkbox);
        WebElement gropoCheckbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + groupName + "')]", firefoxDriver);
        SeleniumDAO.click(gropoCheckbox);

        WebElement addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addCallModeButton);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
        Actions actions = new Actions(firefoxDriver);
        actions.sendKeys(Keys.ESCAPE).perform();
        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }

    public void createManualCallmode() throws InterruptedException {
        //Create new callMode: manual
        firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("createCampaign")));
        WebElement createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
        Thread.sleep(1200);
        SeleniumDAO.click(createCallModeButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        Select dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
        dialingModeSelector.selectByValue("manual");

        WebElement nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
        nameInput.sendKeys(manualCallModeName);

        //Este grupo solo estará en la de transferencia para comprobar los filtros del panel de extensiones en el parte de webclient
        WebElement grupo1y2Checkbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + groupName1y2 + "')]", firefoxDriver);
        SeleniumDAO.click(grupo1y2Checkbox);

        WebElement addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addCallModeButton);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
        Actions actions = new Actions(firefoxDriver);
        actions.sendKeys(Keys.ESCAPE).perform();
        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }

    public void createPredictiveCallmode() throws InterruptedException {
        //Create callMode: predictive
        firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("createCampaign")));
        WebElement createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
        Thread.sleep(1000);
        SeleniumDAO.click(createCallModeButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        Select dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
        dialingModeSelector.selectByValue("predictive");

        WebElement nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
        nameInput.sendKeys(predictCallModeName);

        //Este grupo solo estará en la de transferencia para comprobar los filtros del panel de extensiones en el parte de webclient
        WebElement grupo1y2Checkbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + groupName1y2 + "')]", firefoxDriver);
        Thread.sleep(1000);
        SeleniumDAO.click(grupo1y2Checkbox);

        WebElement addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        Thread.sleep(500);
        SeleniumDAO.click(addCallModeButton);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
        Actions actions = new Actions(firefoxDriver);
        actions.sendKeys(Keys.ESCAPE).perform();
        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }

    public void createCallbackCallmode() throws InterruptedException {
        //Create callBack for predictive callMode
        firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("createCampaign")));

        WebElement favouriteCallModeButton = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + predictCallModeName + "')]/following-sibling::td/img[@class = 'baseStarOff']",
                firefoxDriver);
        Thread.sleep(800);
        SeleniumDAO.click(favouriteCallModeButton);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
        WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
        SeleniumDAO.click(okButton);

        //Add callback to the recently added predictive callmode
        firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(., '" + predictCallModeName + "')]/following-sibling::td/img[@class = 'editCampaign']")));
        WebElement editCallMode = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + predictCallModeName + "')]/following-sibling::td/img[@class = 'editCampaign']",
                firefoxDriver);
        SeleniumDAO.click(editCallMode);

        Select callbackSelector = SeleniumDAO.findSelectElementBy("id", "campaigncallback", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[contains(., '" + predictCallModeName + " Callback" + "')]")));
        callbackSelector.selectByVisibleText(predictCallModeName + " Callback");

        //Este grupo solo estará en la de transferencia para comprobar los filtros del panel de extensiones en el parte de webclient
        WebElement grupo1y2Checkbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + groupName1y2 + "')]", firefoxDriver);
        if(grupo1y2Checkbox.isSelected()) SeleniumDAO.click(grupo1y2Checkbox);


        WebElement addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addCallModeButton);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
        Actions actions = new Actions(firefoxDriver);
        actions.sendKeys(Keys.ESCAPE).perform();
        Thread.sleep(1000);
        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }

    public void configureManualCallmode() throws InterruptedException {
        SeleniumDAO.switchToDefaultContent(firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr/td[contains(., '" + manualCallModeName + "')]/following-sibling::td/img[@class = 'editCampaign']")));
        WebElement editCallMode = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + manualCallModeName + "')]/following-sibling::td/img[@class = 'editCampaign']",
                firefoxDriver);
        SeleniumDAO.click(editCallMode);

        firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[contains(., '" + predictCallModeName + " Callback')]")));
        Select campaignCallback = SeleniumDAO.findSelectElementBy("id", "campaigncallback", firefoxDriver);
        campaignCallback.selectByVisibleText(predictCallModeName + " Callback");

        WebElement addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addCallModeButton);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
        Actions actions = new Actions(firefoxDriver);
        actions.sendKeys(Keys.ESCAPE).perform();
        Thread.sleep(1000);
        SeleniumDAO.switchToDefaultContent(firefoxDriver);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr/td[contains(., '" + manualCallModeName + "')]/following-sibling::td/a[@class = 'maximumControl iframe']")));
        WebElement phoneCallModeButton = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + manualCallModeName + "')]/following-sibling::td/a[@class = 'maximumControl iframe']",
                firefoxDriver);
        SeleniumDAO.click(phoneCallModeButton);
        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//input[@value = '1']")));
        WebElement manualRadioButton = SeleniumDAO.selectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//input[@value = '1']", firefoxDriver);
        SeleniumDAO.click(manualRadioButton);

        WebElement sendButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
        SeleniumDAO.click(sendButton);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }

    public void configureCallbackCallmode() throws InterruptedException {
        //Configure predictive callmode callback
        WebElement phoneCallModeButton = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + predictCallModeName + " Callback')]/following-sibling::td/a[@class = 'maximumControl iframe']",
                firefoxDriver);
        Thread.sleep(1000);
        SeleniumDAO.click(phoneCallModeButton);
        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

        /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//input[@value = '1']")));
        WebElement manualRadioButton = SeleniumDAO.selectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//input[@value = '1']", firefoxDriver);
        Thread.sleep(500);
        SeleniumDAO.click(manualRadioButton);*/

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//select")));
        Select callbackAssignmentSelector = SeleniumDAO.findSelectElementBy("xpath", "//tr[td[strong[contains(., 'PENDIENTE - AGENDADA')]]]/following-sibling::tr//select", firefoxDriver);
        callbackAssignmentSelector.selectByValue("user");

        WebElement sendButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
        SeleniumDAO.click(sendButton);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }

    public void configurePredictiveCallback() throws InterruptedException, IOException {
        //Configure predictive callmode
        Thread.sleep(2000);
        WebElement phoneCallModeButton = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + predictCallModeName + "')]/following-sibling::td/a[@class = 'maximumControl iframe']",
                firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.elementToBeClickable(phoneCallModeButton));
        SeleniumDAO.click(phoneCallModeButton);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("maximumtype")));
        Select calltypeSelector = SeleniumDAO.findSelectElementBy("id", "maximumtype", firefoxDriver);
        calltypeSelector.selectByVisibleText("Combined (Delay - Typology)"); //TODO refactor byValue


        Thread.sleep(1000);
        //Configuration for the phone button
        configureDelaysNoContacted();
        configureDelaysContacted();
        configureTypologies();

        Utils.takeScreenshot("./ParteDeServicioOut/predictiveConfigScreenshot", firefoxDriver);

        WebElement sendButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
        SeleniumDAO.click(sendButton);

        Thread.sleep(500);
    }
}