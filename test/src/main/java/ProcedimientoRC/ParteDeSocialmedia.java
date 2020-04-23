package ProcedimientoRC;

import Utils.DriversConfig;
import Utils.TestWithConfig;
import Utils.Utils;
import Utils.ShowflowUtils;
import Utils.ServiceUtils;
import Utils.ServicioUtils;
import exceptions.MissingParameterException;
import org.apache.commons.collections.ArrayStack;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.SeleniumDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParteDeSocialmedia extends TestWithConfig {

    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String showflowSMName;

    static String showflowOption1;
    static String showflowOption2;
    static String showflowOption3;
    static String showflowOptionsGroupName;
    static String showflowAuxField;
    static String showflowQuestion1;
    static String showflowQuestion2;
    static String statesCsvpath;
    static String showflowSMrcver833Copy;
    static String serviceSMName;
    static String campaignBeginningDate;
    static String campaignEndDate;
    static String manualCallModeName;
    static String incomingCallModeName;
    static String emailChannelName;
    static String chatChannelName;

    static String number;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();


    public ParteDeSocialmedia(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("ShowflowSM", new ArrayList<>(Arrays.asList("showflowSMName", "showflowSMCopyName")));
        requiredParameters.put("Showflow", new ArrayList(Arrays.asList("showflowOption1", "showflowOption2", "showflowOption3", "showflowOptionsGroupName",
                "showflowAuxField", "showflowQuestion1", "showflowQuestion2")));
        requiredParameters.put("CSV", new ArrayList<>(Arrays.asList("statesCsvPath")));
        requiredParameters.put("ServiceSM", new ArrayList<>(Arrays.asList("serviceSMName")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("campaignBeginningDate", "campaignEndDate")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("manualCallModeName", "incomingCallModeName")));
        requiredParameters.put("Channel", new ArrayList<>(Arrays.asList("emailChannelName", "chatChannelName")));
        requiredParameters.put("Contact", new ArrayList<>(Arrays.asList("number")));


        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws Exception {
        super.checkParameters();

        try
        {
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");
            showflowSMName = commonIni.get("ShowflowSM", "showflowSMName");
            showflowOption1 = commonIni.get("Showflow", "showflowOption1");
            showflowOption2 = commonIni.get("Showflow", "showflowOption2");
            showflowOption3 = commonIni.get("Showflow", "showflowOption3");
            showflowOptionsGroupName = commonIni.get("Showflow", "showflowOptionsGroupName");
            showflowAuxField = commonIni.get("Showflow", "showflowAuxField");
            showflowQuestion1 = commonIni.get("Showflow", "showflowQuestion1");
            showflowQuestion2 = commonIni.get("Showflow", "showflowQuestion2");
            showflowSMrcver833Copy = commonIni.get("ShowflowSM", "showflowSMCopyName");
            statesCsvpath = commonIni.get("CSV", "statesCsvPath");
            serviceSMName = commonIni.get("ServiceSM", "serviceSMName");
            campaignBeginningDate = commonIni.get("CallMode", "campaignBeginningDate");
            campaignEndDate = commonIni.get("CallMode", "campaignEndDate");
            manualCallModeName = commonIni.get("CallMode", "manualCallModeName");
            incomingCallModeName = commonIni.get("CallMode", "incomingCallModeName");
            emailChannelName = commonIni.get("Channel", "emailChannelName");
            chatChannelName = commonIni.get("Channel", "chatChannelName");
            number = commonIni.get("Contact", "number");

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            results.put("Create a showflow ticket and check the states  ->  ", newShowflowTicket());
            results.put("Create action fields  ->  ", createContactFields());
            results.put("Create questions  ->  ", createQuestions());
            results.put("Import states by CSV  ->  ", importStates());
            results.put("Configure pages  ->  ", configurePages());
            results.put("Joint two pages  ->  ", jointTwoPages());
            results.put("Clone showflow  ->  ", cloneShowflow());
            results.put("Configure contact center  ->  ", configureContactCenter());
            results.put("Create ticket service", createTicketService());
            results.put("Configure email channel  ->  ", configureEmailChannel());
            results.put("Configure chat channel  ->  ", configureChatChannel());
            //results.put("Configure telegram channel", configureTelegramChannel());

            return results;
        } catch (Exception e)
        {
            e.printStackTrace();
            return results;
        } finally
        {
            firefoxDriver.close();
        }
    }

    public String newShowflowTicket()
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


            String createShowflowRes = ShowflowUtils.createShowflow(firefoxDriver, firefoxWaiting, showflowSMName, "ticket", url);
            if(!createShowflowRes.contains("ERROR"))
            {
                String checkShowflowStatesRes = checkShowflowStates(showflowSMName);
                if(checkShowflowStatesRes.contains("ERROR")) return checkShowflowStatesRes;
                else return "Test OK. The ticket showflow was created";
            }
            else return createShowflowRes;

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String createContactFields()
    {
        List<String> options = new ArrayList<>();
        options.add(showflowOption1);
        options.add(showflowOption2);
        options.add(showflowOption3);
        try
        {
            String activateShowflowFieldsRes = ShowflowUtils.createContactFields(firefoxDriver, firefoxWaiting, options, showflowSMName, showflowOptionsGroupName, showflowAuxField, true);
            if(activateShowflowFieldsRes.contains("ERROR")) return activateShowflowFieldsRes;

            return "Test OK.";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String createQuestions(){
        List<String> questions = new ArrayList<>();
        questions.add(showflowQuestion1);
        questions.add(showflowQuestion2);

        try
        {
            String createQuestionRes = ShowflowUtils.createQuestions(firefoxDriver, firefoxWaiting, questions, showflowOptionsGroupName, true);
            if(createQuestionRes.contains("ERROR")) return createQuestionRes;

            return "Test OK. The question fields were created and apear on the table.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String importStates()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit_showflow_actions']/a")));
            WebElement actionFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_actions']/a", firefoxDriver);
            SeleniumDAO.click(actionFieldsTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/config.png']")));
            WebElement configStatesButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/config.png']", firefoxDriver);
            SeleniumDAO.click(configStatesButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("csvfile")));
            WebElement browseCSVButton = SeleniumDAO.selectElementBy("id", "csvfile", firefoxDriver);
            SeleniumDAO.writeInTo(browseCSVButton, statesCsvpath);

            WebElement importButton = SeleniumDAO.selectElementBy("id", "submitcsv", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(importButton));
            SeleniumDAO.click(importButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            SeleniumDAO.click(okButton);

            //Comprueba que los estados han sido creados buscandolos en la tabla
            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'status-table']//input[@value = 'estado1']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'status-table']//input[@value = 'estado2']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'status-table']//input[@value = 'estado3']")));
            } catch(Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The csv was imported but the states do not appear on the table";
            }

            return "Test OK. The states were import by csv correctly";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        }
    }

    public String configurePages()
    {
        try
        {
            ShowflowUtils.configurePages(firefoxDriver, firefoxWaiting, true);

            return "Test OK. Check your work directory to see the taken screenshots of the initial and final pages.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The pages could not be configurated";
        }
    }

    public String jointTwoPages() {
        try {
            Thread.sleep(1000);
            ShowflowUtils.configureJoints(firefoxDriver, firefoxWaiting);

            return "Test OK. The initial page and the final page were joined with the condition name = Sebas";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. The pages could not be joined";
        }
    }

    public String cloneShowflow()
    {
        try
        {
            WebElement cloneShowflowTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'clone_showflow']/a", firefoxDriver);
            SeleniumDAO.click(cloneShowflowTab);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("newShowflowCloneName")));
            WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "newShowflowCloneName", firefoxDriver);
            showflowNameInput.clear();
            showflowNameInput.sendKeys(showflowSMrcver833Copy);

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
                searcher.sendKeys(showflowSMrcver833Copy);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowSMrcver833Copy + "')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "ERROR: Something went wrong. The copy of the showflow does not appear on the showflows table";
            }

            WebElement showflowCopy = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowSMrcver833Copy + "')]", firefoxDriver);
            SeleniumDAO.click(showflowCopy);

            WebElement contactFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_contact']/a", firefoxDriver);
            SeleniumDAO.click(contactFieldsTab);

            String contactFieldsRes = ShowflowUtils.checkContactFields(firefoxDriver, firefoxWaiting, showflowOptionsGroupName, showflowAuxField);
            String showflowFieldsRes = ShowflowUtils.checkShowflowFields(firefoxDriver, firefoxWaiting, showflowQuestion1, showflowQuestion2, showflowOptionsGroupName, true);

            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", firefoxDriver);
            SeleniumDAO.click(showflowPanel);

            String actionFieldsRes = checkShowflowStates(showflowSMrcver833Copy); //Este metodo no está en utils ya que no sirve para showflow de contacto. Solo para showflow tipo ticket
            String checkPagesRes = ShowflowUtils.checkPages(firefoxDriver, firefoxWaiting, showflowSMrcver833Copy, true);

            if(contactFieldsRes.contains("ERROR")) return contactFieldsRes;
            if(showflowFieldsRes.contains("ERROR")) return showflowFieldsRes;
            if(actionFieldsRes.contains("ERROR")) return actionFieldsRes;
            if(checkPagesRes.contains("ERROR")) return checkPagesRes;


            return "Test OK. The cloned showflow matchs with the original";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception. Could not check if the showflow was cloned OK";
        }
    }
    //Auxiliar methods
    public String checkShowflowStates(String showflowName)
    {
        try
        {
            Thread.sleep(1000);
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", firefoxDriver);
            SeleniumDAO.click(showflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, 'actionFields.php')]")));
            WebElement actionFieldsTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(@href, 'actionFields.php')]", firefoxDriver);
            SeleniumDAO.click(actionFieldsTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/config.png']")));
            WebElement statesConfigButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/config.png']", firefoxDriver);
            SeleniumDAO.click(statesConfigButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@class = 'statusInitial']/td/following-sibling::td//option[contains(., 'Open')]")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = 'Open']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = 'Close']")));
            } catch (Exception e)
            {
                e.printStackTrace();
                return "ERROR. The states have not been created";
            }

            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", firefoxDriver);
            SeleniumDAO.click(showflowPanel);
            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String configureContactCenter(){
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id = 'ADMIN']/a")));
            WebElement adminTab = SeleniumDAO.selectElementBy("xpath", "//li[@id = 'ADMIN']/a", firefoxDriver);
            SeleniumDAO.click(adminTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'anadir-contacto']/a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'anadir-contacto']/a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);

            //TODO de momento no hacer telegram con selenium, muchos problemas. Esta configurado a mano
            /*String configureTelegramAcountRes = configureTelegramAcount();
            if(configureTelegramAcountRes.contains("ERROR")) return configureTelegramAcountRes;*/

            String configureEmailAcountRes = configureEmailAcount();
            if(configureEmailAcountRes.contains("ERROR")) return configureEmailAcountRes;

            return "Test OK.";
        } catch(Exception e)
        {
            e.printStackTrace();
            return "ERROR. Unexpected error";
        }
    }


    public String createTicketService()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id = 'OPERATION']/a")));
            WebElement operationsTab = SeleniumDAO.selectElementBy("xpath", "//li[@id = 'OPERATION']/a", firefoxDriver);
            SeleniumDAO.click(operationsTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'create-service']")));
            WebElement createServiceButton = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'create-service']", firefoxDriver);
            SeleniumDAO.click(createServiceButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'radio' and @value = 'ticket']")));
            WebElement ticketRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'radio' and @value = 'ticket']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(ticketRadioButton));
            SeleniumDAO.click(ticketRadioButton);

            WebElement acceptButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit' and @value = 'Accept']", firefoxDriver);
            SeleniumDAO.click(acceptButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            WebElement serviceNameInput = SeleniumDAO.selectElementBy("id", "name", firefoxDriver);
            serviceNameInput.sendKeys(serviceSMName);

            Select actionTypeSelector = SeleniumDAO.findSelectElementBy("id", "actionsid", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'actionsid']/option[@value = '1']")));
            actionTypeSelector.selectByValue("1");

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow")));
            Select showflowSelector = SeleniumDAO.findSelectElementBy("id", "workflow", firefoxDriver);
            showflowSelector.selectByVisibleText(showflowSMName);

            //Añade los grupos
            List<WebElement> assignableGroups = firefoxDriver.findElements(By.xpath("//table[@id = 'groups']/tbody/tr/td[1]/input[@type = 'checkbox']"));
            for (int i = 0; i < assignableGroups.size(); i++) {
                Thread.sleep(500);
                if (!assignableGroups.get(i).isSelected()) {
                    SeleniumDAO.click(assignableGroups.get(i));
                }
            }

            ServiceUtils.setDateTimeCampaign(firefoxDriver, firefoxWaiting, campaignBeginningDate, campaignEndDate);

            ServiceUtils.recordRateTo100(firefoxDriver);

            ServiceUtils.setRecordMap(firefoxDriver);

            ServiceUtils.setRobinsonToYes(firefoxDriver);

            //TODO linea 172 procedimientoRC

            WebElement sendButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(sendButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name = 'alloweditclosedtickets']")));
            WebElement allowEditClosedTicket = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'alloweditclosedtickets']", firefoxDriver);
            if(!allowEditClosedTicket.isSelected()) SeleniumDAO.click(allowEditClosedTicket);

            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(nextButton);

            String createManualCallmodeRes = createCallmode("manual", manualCallModeName);
            if(createManualCallmodeRes.contains("ERROR")) return createManualCallmodeRes;

            String createIncomingCallmodeRes = createCallmode("incoming", incomingCallModeName);
            if(createIncomingCallmodeRes.contains("ERROR")) return createIncomingCallmodeRes;

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("next")));
            nextButton = SeleniumDAO.selectElementBy("id", "next", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(nextButton);

            return "Test OK. The service was created and configured with the showflow: " + showflowSMName + ". Also a manual and incoming callmode were added.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        }
    }

    public String createCallmode(String dialingMode, String callmodeName)
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createCampaign")));
            WebElement createCallmodeButton = SeleniumDAO.selectElementBy("id", "createCampaign", firefoxDriver);
            Thread.sleep(2000);
            SeleniumDAO.click(createCallmodeButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("dialingmode")));
            Select dialingModeSelector = SeleniumDAO.findSelectElementBy("id", "dialingmode", firefoxDriver);
            dialingModeSelector.selectByValue(dialingMode);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            WebElement callmodeNameInput = SeleniumDAO.selectElementBy("id", "name", firefoxDriver);
            callmodeNameInput.sendKeys(callmodeName);

            WebElement addCallmodeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addCallmodeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            Actions actions = new Actions(firefoxDriver);
            actions.sendKeys(Keys.ESCAPE).perform();
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Could not create " + dialingMode + " callmode";
        }
    }


    public String configureTelegramAcount(){
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'telegramAccounts.php']")));
            WebElement telegramAccounts = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'telegramAccounts.php']", firefoxDriver);
            SeleniumDAO.click(telegramAccounts);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class = 'filter-option pull-left']")));
            WebElement countrySelector = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'filter-option pull-left']", firefoxDriver);
            SeleniumDAO.click(countrySelector);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'input-block-level form-control']")));
            WebElement countrySearchInput = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'input-block-level form-control']", firefoxDriver);
            countrySearchInput.sendKeys("Spain");

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., 'Spain')]")));
            WebElement country = SeleniumDAO.selectElementBy("xpath", "//span[contains(., 'Spain')]", firefoxDriver);
            SeleniumDAO.click(country);

            WebElement numberInput = SeleniumDAO.selectElementBy("id", "inputPhone", firefoxDriver);
            numberInput.sendKeys(number);

            Thread.sleep(1000);
            WebElement configureAccountButton = SeleniumDAO.selectElementBy("id", "buttonStepOne", firefoxDriver);
            SeleniumDAO.click(configureAccountButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            System.out.println("A validation code was sent to the number: " + number + ". Enter it manually in the web a click on validate button");

            WebDriverWait auxiliarWaiting = new WebDriverWait(firefoxDriver, 30);
            auxiliarWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']//p[contains(,. '" + number + "')]")));
            okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            SeleniumDAO.click(okButton);

            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Could not configurate telegram account";
        }
    }

    public String configureEmailAcount(){
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'anadir-contacto']/a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'anadir-contacto']/a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'MailAccountFBoxTicket.php']")));
            WebElement mailAcountsButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'MailAccountFBoxTicket.php']", firefoxDriver);
            SeleniumDAO.click(mailAcountsButton);

            //incoming email
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'address']")));
            WebElement emailAddressInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'address']", firefoxDriver);
            emailAddressInput.sendKeys("socialmediadialapplet@gmail.com");

            WebElement passwordInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'pass']", firefoxDriver);
            passwordInput.sendKeys("semana78ya");

            WebElement repeatPasswordInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'pass_sec']", firefoxDriver);
            repeatPasswordInput.sendKeys("semana78ya");

            WebElement serverAddressInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'server']", firefoxDriver);
            serverAddressInput.sendKeys("imap.gmail.com");

            WebElement emailPortInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'port']", firefoxDriver);
            emailPortInput.sendKeys("993");

            WebElement emailProtocolRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@value = 'IMAP']", firefoxDriver);
            SeleniumDAO.click(emailProtocolRadioButton);

            WebElement encryptTypeRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@value = 'ssl']", firefoxDriver);
            SeleniumDAO.click(encryptTypeRadioButton);

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "saveaccount", firefoxDriver);
            SeleniumDAO.click(saveButton);

            Thread.sleep(800);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            //outgoing email
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'address']")));
            emailAddressInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'address']", firefoxDriver);
            emailAddressInput.sendKeys("socialmediadialapplet@gmail.com");

            passwordInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'pass']", firefoxDriver);
            passwordInput.sendKeys("semana78ya");

            repeatPasswordInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'pass_sec']", firefoxDriver);
            repeatPasswordInput.sendKeys("semana78ya");

            serverAddressInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'server']", firefoxDriver);
            serverAddressInput.sendKeys("smtp.gmail.com");

            emailPortInput = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'port']", firefoxDriver);
            emailPortInput.sendKeys("465");

            emailProtocolRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@value = 'SMTP']", firefoxDriver);
            SeleniumDAO.click(emailProtocolRadioButton);

            encryptTypeRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@value = 'ssl']", firefoxDriver);
            SeleniumDAO.click(encryptTypeRadioButton);

            saveButton = SeleniumDAO.selectElementBy("id", "saveaccount", firefoxDriver);
            SeleniumDAO.click(saveButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(okButton);

            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Could not configure email accounts";
        }
    }

    public String configureEmailChannel()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createChannel")));
            WebElement createChannelButton = SeleniumDAO.selectElementBy("id", "createChannel", firefoxDriver);
            SeleniumDAO.click(createChannelButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'channelType']/option[@value = 'mail']")));
            Select channelTypeSelector = SeleniumDAO.findSelectElementBy("id", "channelType", firefoxDriver);
            channelTypeSelector.selectByValue("mail");

            WebElement channelNameInput = SeleniumDAO.selectElementBy("id", "name", firefoxDriver);
            channelNameInput.sendKeys(emailChannelName);

            Select incomingEmailSelector = SeleniumDAO.findSelectElementBy("id", "incoming", firefoxDriver);
            incomingEmailSelector.selectByVisibleText("socialmediadialapplet@gmail.com");

            Select outgoingEmailSelector = SeleniumDAO.findSelectElementBy("id", "outgoing", firefoxDriver);
            outgoingEmailSelector.selectByVisibleText("socialmediadialapplet@gmail.com");

            selectAllGroups();

            //Comprueba que si la fecha del canal es mayor a la del servicio, no deja crear el canal
            String checkChannelOutOfRangeRes = checkChannelOutOfRange();
            if(checkChannelOutOfRangeRes.contains("ERROR")) return checkChannelOutOfRangeRes;

            String checkChannelDissabledRes = checkChannelDissabled(emailChannelName);
            if(checkChannelDissabledRes.contains("ERROR")) return checkChannelDissabledRes;

            String checkChannelEnabledRes = checkChannelEnabled(emailChannelName);
            if(checkChannelEnabledRes.contains("ERROR")) return checkChannelEnabledRes;



            return "Test OK";
        } catch (Exception e)
        {
            e.printStackTrace();
            return "ERROR. Unexpected exception";
        }
    }

    public String configureChatChannel(){
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createChannel")));
            WebElement createChannelButton = SeleniumDAO.selectElementBy("id", "createChannel", firefoxDriver);
            SeleniumDAO.click(createChannelButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'channelType']/option[@value = 'mail']")));
            Select channelTypeSelector = SeleniumDAO.findSelectElementBy("id", "channelType", firefoxDriver);
            channelTypeSelector.selectByValue("chat");

            WebElement channelNameInput = SeleniumDAO.selectElementBy("id", "name", firefoxDriver);
            channelNameInput.sendKeys(chatChannelName);

            WebElement mondayCheckbox = SeleniumDAO.selectElementBy("id", "day1", firefoxDriver);
            SeleniumDAO.click(mondayCheckbox);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("since_day1_mor")));

            WebElement mondayIniHourInput = SeleniumDAO.selectElementBy("id", "since_day1_mor", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(mondayIniHourInput));
            mondayIniHourInput.clear();
            mondayIniHourInput.sendKeys("09:00:00");

            WebElement mondayEndHourInput = SeleniumDAO.selectElementBy("id", "until_day1_mor", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(mondayEndHourInput));
            mondayEndHourInput.clear();
            mondayEndHourInput.sendKeys("17:00:00");

            WebElement copyToFridayButton = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'copyToFriday']", firefoxDriver);
            SeleniumDAO.click(copyToFridayButton);

            selectAllGroups();

            String checkChannelOutOfRangeRes = checkChannelOutOfRange();
            if(checkChannelOutOfRangeRes.contains("ERROR")) return checkChannelOutOfRangeRes;

            String checkChannelDissabledRes = checkChannelDissabled(chatChannelName);
            if(checkChannelDissabledRes.contains("ERROR")) return checkChannelDissabledRes;

            String checkHolidayDayRes = checkHolidayDay(chatChannelName);
            if(checkHolidayDayRes.contains("ERROR")) return checkHolidayDayRes;

            String checkChannelEnabledRes = checkChannelEnabled(chatChannelName);
            if(checkChannelEnabledRes.contains("ERROR")) return checkChannelEnabledRes;

            return "Test OK";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }

    }

    /*public String configureTelegramChannel()
    {
        try
        {
            
        } catch(Exception e)
        {

        }
    }*/


    public void selectAllGroups()
    {
        List<WebElement> agentGroups = firefoxDriver.findElements(By.xpath("//table[@id = 'groups']/tbody/tr//input"));
        for(WebElement group : agentGroups){
            if(!group.isSelected()) SeleniumDAO.click(group);
        }
    }

    public String checkChannelOutOfRange()
    {
        WebElement endDateInput = SeleniumDAO.selectElementBy("id", "periodend_1", firefoxDriver);
        endDateInput.clear();
        endDateInput.sendKeys("2022-08-25"); //Un mes adelantado a la fecha de fin del servicio

        WebElement addChannelButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addChannelButton);

        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(700);
            SeleniumDAO.click(okButton);
        } catch(Exception e) //Si salta excepcion es que no ha encontrado la alerta de la web de que no te deja continuar por lo que estaria mal
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The channel date was higher than the service date but no alert was shown not letting create the channel";
        }
        return "";
    }

    public String checkChannelDissabled(String channelName) throws InterruptedException {
        //Restablecemos la fecha de fin a la anterior
        WebElement endDateInput = SeleniumDAO.selectElementBy("id", "periodend_1", firefoxDriver);
        endDateInput.clear();
        endDateInput.sendKeys("2022-07-25"); //Un mes adelantado a la fecha de fin del servicio

        //Ponemos una fecha de inicio del canal mayor a la actual para comprobar que se crea deshabilitado
        WebElement beginningDateInput = SeleniumDAO.selectElementBy("id", "periodbegin_1", firefoxDriver);
        beginningDateInput.clear();
        beginningDateInput.sendKeys("2021-03-15");

        WebElement addChannelButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        Thread.sleep(800);
        SeleniumDAO.click(addChannelButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
        WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
        Thread.sleep(800);
        SeleniumDAO.click(okButton);

        //Buscamos en la web el icono que indica que el canal esta fuera de fecha
        try{
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + channelName + "')]/following-sibling::td/img[@src = 'imagenes/apps/outofdate.png']")));
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. The initial date of the channel was configured as out of range but when created, appears as enabled";
        }

        return "";
    }

    public String checkChannelEnabled(String channelName) throws InterruptedException {
        //Cambiamos la fecha inicial para comprobar que queda activado
        WebElement configureChannel = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + channelName + "')]/following-sibling::td/img[@src = 'imagenes/edit2.png']", firefoxDriver);
        SeleniumDAO.click(configureChannel);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("periodbegin_1")));
        WebElement beginningDateInput = SeleniumDAO.selectElementBy("id", "periodbegin_1", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.elementToBeClickable(beginningDateInput));
        beginningDateInput.clear();
        beginningDateInput.sendKeys("2020-03-15");

        WebElement addChannelButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addChannelButton);

        Thread.sleep(500);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
        WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
        Thread.sleep(800);
        SeleniumDAO.click(okButton);

        //Comprobamos que el icono que aparece es el de activado y no el de fuera de rango
        try{
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + channelName + "')]/following-sibling::td/img[@src = 'imagenes/enabled.png']")));
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. The initial date of the channel was configured correctly but not appears as enabled";
        }

        return "";
    }

    public String checkHolidayDay(String channelName) throws InterruptedException {
        WebElement configureChannel = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + channelName + "')]/following-sibling::td/img[@src = 'imagenes/edit2.png']", firefoxDriver);
        SeleniumDAO.click(configureChannel);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@class = 'boton-calendario']")));
        WebElement calendarButton = SeleniumDAO.selectElementBy("xpath", "//img[@class = 'boton-calendario']", firefoxDriver);
        SeleniumDAO.click(calendarButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[@class = 'day selected today']")));
        WebElement todayCalendarButton = SeleniumDAO.selectElementBy("xpath", "//td[@class = 'day selected today']", firefoxDriver);
        SeleniumDAO.click(todayCalendarButton);

        WebElement addChannelButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
        SeleniumDAO.click(addChannelButton);

        Thread.sleep(500);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
        WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
        Thread.sleep(800);
        SeleniumDAO.click(okButton);

        //Comprobamos que el icono que aparece es el de vacaciones
        try{
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + channelName + "')]/following-sibling::td/img[@src = 'imagenes/apps/outofdate.png']")));
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "\nERROR. A holiday day was set up but the channel do not appear with the holiday icon";
        }

        return "";
    }
}
