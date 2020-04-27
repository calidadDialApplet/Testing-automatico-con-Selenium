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
    static String showflowSMCopyName;
    static String serviceSMName;
    static String serviceSMCopyName;
    static String campaignBeginningDate;
    static String campaignEndDate;
    static String manualCallModeName;
    static String incomingCallModeName;
    static String emailChannelName;
    static String chatChannelName;
    static String telegramChannelName;
    static String twitterChannelName;
    static String agentCoordName;
    static String agentName4;
    static String agentName5;
    static String groupName1y2;

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
        requiredParameters.put("ServiceSM", new ArrayList<>(Arrays.asList("serviceSMName", "serviceSMCopyName")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("campaignBeginningDate", "campaignEndDate", "manualCallModeName", "incomingCallModeName")));
        requiredParameters.put("Channel", new ArrayList<>(Arrays.asList("emailChannelName", "chatChannelName", "telegramChannelName", "twitterChannelName")));
        requiredParameters.put("Contact", new ArrayList<>(Arrays.asList("number")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName4", "agentName5")));
        requiredParameters.put("Coordinator", new ArrayList<>(Arrays.asList("agentCoordName6")));
        requiredParameters.put("Group", new ArrayList<>(Arrays.asList("groupName1y2")));


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
            showflowSMCopyName = commonIni.get("ShowflowSM", "showflowSMCopyName");
            statesCsvpath = commonIni.get("CSV", "statesCsvPath");
            serviceSMName = commonIni.get("ServiceSM", "serviceSMName");
            serviceSMCopyName = commonIni.get("ServiceSM", "serviceSMCopyName");
            campaignBeginningDate = commonIni.get("CallMode", "campaignBeginningDate");
            campaignEndDate = commonIni.get("CallMode", "campaignEndDate");
            manualCallModeName = commonIni.get("CallMode", "manualCallModeName");
            incomingCallModeName = commonIni.get("CallMode", "incomingCallModeName");
            emailChannelName = commonIni.get("Channel", "emailChannelName");
            chatChannelName = commonIni.get("Channel", "chatChannelName");
            telegramChannelName = commonIni.get("Channel", "telegramChannelName");
            twitterChannelName = commonIni.get("Channel", "twitterChannelName");
            number = commonIni.get("Contact", "number");
            agentName4 = commonIni.get("Agent", "agentName4");
            agentName5 = commonIni.get("Agent", "agentName5");
            agentCoordName = commonIni.get("Coordinator", "agentCoordName6");
            groupName1y2 = commonIni.get("Group", "groupName1y2");



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
            results.put("Configure telegram channel  ->  ", configureTelegramChannel());
            results.put("Configure twitter channel  ->  ", configureTwitterChannel());
            results.put("Configure chat channel  ->  ", configureChatChannel());
            results.put("Configure service coordinators  ->  ", configureServiceCoordinators());
            results.put("Configure contact data  ->  ", configureContactData());
            results.put("Confirm service  ->  ", confirmService());
            results.put("Configure web chat  ->  ", configureWebChat());
            results.put("Clone service and check it  ->  ", cloneService());

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
            showflowNameInput.sendKeys(showflowSMCopyName);

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
                searcher.sendKeys(showflowSMCopyName);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowSMCopyName + "')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "ERROR: Something went wrong. The copy of the showflow does not appear on the showflows table";
            }

            WebElement showflowCopy = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowSMCopyName + "')]", firefoxDriver);
            SeleniumDAO.click(showflowCopy);

            WebElement contactFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_contact']/a", firefoxDriver);
            SeleniumDAO.click(contactFieldsTab);

            String contactFieldsRes = ShowflowUtils.checkContactFields(firefoxDriver, firefoxWaiting, showflowOptionsGroupName, showflowAuxField);
            String showflowFieldsRes = ShowflowUtils.checkShowflowFields(firefoxDriver, firefoxWaiting, showflowQuestion1, showflowQuestion2, showflowOptionsGroupName, true);

            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", firefoxDriver);
            SeleniumDAO.click(showflowPanel);

            String actionFieldsRes = checkShowflowStates(showflowSMCopyName); //Este metodo no está en utils ya que no sirve para showflow de contacto. Solo para showflow tipo ticket
            String checkPagesRes = ShowflowUtils.checkPages(firefoxDriver, firefoxWaiting, showflowSMCopyName, true);

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

            String configureEmailSignatureRes = configureEmailSignature();
            if(configureEmailSignatureRes.contains("ERROR")) return configureEmailAcountRes;

            String configureEmailTemplateRes = configureEmailTemplate();
            if(configureEmailTemplateRes.contains("ERROR")) return configureEmailTemplateRes;

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
            Thread.sleep(2000);
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

    public String configureTelegramChannel(){
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createChannel")));
            WebElement createChannelButton = SeleniumDAO.selectElementBy("id", "createChannel", firefoxDriver);
            SeleniumDAO.click(createChannelButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'channelType']/option[@value = 'mail']")));
            Select channelTypeSelector = SeleniumDAO.findSelectElementBy("id", "channelType", firefoxDriver);
            channelTypeSelector.selectByValue("telegram");

            WebElement channelNameInput = SeleniumDAO.selectElementBy("id", "name", firefoxDriver);
            channelNameInput.sendKeys(telegramChannelName);

            Select telegramAccountSelector = SeleniumDAO.findSelectElementBy("id", "accounttelegram", firefoxDriver);
            telegramAccountSelector.selectByVisibleText("+34662179631");

            selectAllGroups();

            String checkChannelOutOfRangeRes = checkChannelOutOfRange();
            if(checkChannelOutOfRangeRes.contains("ERROR")) return checkChannelOutOfRangeRes;

            String checkChannelDissabledRes = checkChannelDissabled(telegramChannelName);
            if(checkChannelDissabledRes.contains("ERROR")) return checkChannelDissabledRes;

            String checkChannelEnabledRes = checkChannelEnabled(telegramChannelName);
            if(checkChannelEnabledRes.contains("ERROR")) return checkChannelEnabledRes;

            return "Test OK. The telegram channel was created";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception. The telegram channel could not be created";
        }
    }

    public String configureTwitterChannel()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createChannel")));
            WebElement createChannelButton = SeleniumDAO.selectElementBy("id", "createChannel", firefoxDriver);
            SeleniumDAO.click(createChannelButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'channelType']/option[@value = 'mail']")));
            Select channelTypeSelector = SeleniumDAO.findSelectElementBy("id", "channelType", firefoxDriver);
            channelTypeSelector.selectByValue("twitterdm");

            WebElement channelNameInput = SeleniumDAO.selectElementBy("id", "name", firefoxDriver);
            channelNameInput.sendKeys(twitterChannelName);

            Select twitterAccountSelector = SeleniumDAO.findSelectElementBy("id", "twitteraccounts", firefoxDriver);
            twitterAccountSelector.selectByVisibleText("PruebasSelenium");

            selectAllGroups();

            String checkChannelOutOfRangeRes = checkChannelOutOfRange();
            if(checkChannelOutOfRangeRes.contains("ERROR")) return checkChannelOutOfRangeRes;

            String checkChannelDissabledRes = checkChannelDissabled(twitterChannelName);
            if(checkChannelDissabledRes.contains("ERROR")) return checkChannelDissabledRes;

            String checkChannelEnabledRes = checkChannelEnabled(twitterChannelName);
            if(checkChannelEnabledRes.contains("ERROR")) return checkChannelEnabledRes;

            return "Test OK. The twitter channel was created";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception. The twitter channel could not be created";
        }
    }

    public String configureChatChannel()
    {
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


    public String configureServiceCoordinators()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("next")));
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "next", firefoxDriver);
            SeleniumDAO.click(nextButton);

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

            nextButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit' and @name = 'send']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(nextButton);

            return "Test OK. The coordinators were configurated correctly";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The coodinator could not be configured on the service";
        }
    }

    public String configureContactData()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'name']")));
            WebElement aux2NameInput = SeleniumDAO.selectElementBy("xpath", "//p[contains(., 'aux2')]/following-sibling::input", firefoxDriver);
            aux2NameInput.clear();
            aux2NameInput.sendKeys("Tiempo");

            WebElement aux2ChatCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux2']//input[@class = 'chat']", firefoxDriver);
            WebElement aux2SearchableCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux2']//input[@class = 'searchable']", firefoxDriver);
            if(!aux2ChatCheckbox.isSelected()) SeleniumDAO.click(aux2ChatCheckbox);
            if(!aux2SearchableCheckbox.isSelected()) SeleniumDAO.click(aux2SearchableCheckbox);

            WebElement cityChatCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city']//input[@class = 'chat']", firefoxDriver);
            WebElement citySearchableCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city']//input[@class = 'searchable']", firefoxDriver);
            if(!cityChatCheckbox.isSelected()) SeleniumDAO.click(cityChatCheckbox);
            if(!citySearchableCheckbox.isSelected()) SeleniumDAO.click(citySearchableCheckbox);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("submit-btn")));
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit-btn", firefoxDriver);
            SeleniumDAO.click(nextButton);

            return "Test OK. The contact data was configurated";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The contact data could not be configurated";
        }
    }

    public String confirmService() {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = 'Confirm Service']")));
            WebElement confirmServiceButton = SeleniumDAO.selectElementBy("xpath", "//input[@value = 'Confirm Service']", firefoxDriver);
            SeleniumDAO.click(confirmServiceButton);

            return "Test OK. The service was created";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The service could not be created";
        }

    }

    public String configureWebChat()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(., 'Webchat')]")));
            WebElement webchatTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Webchat')]", firefoxDriver);
            SeleniumDAO.click(webchatTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createChannel")));
            WebElement createChannelGroup = SeleniumDAO.selectElementBy("id", "createChannel", firefoxDriver);
            SeleniumDAO.click(createChannelGroup);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("channelGroupName")));
            WebElement groupNameInput = SeleniumDAO.selectElementBy("id", "channelGroupName", firefoxDriver);
            groupNameInput.sendKeys("nuevo grupo selenium");

            WebElement addChannelGroup = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addChannelGroup);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(okButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@class = 'associateChannels']")));
            WebElement associateChannelButton = SeleniumDAO.selectElementBy("xpath", "//img[@class = 'associateChannels']", firefoxDriver);
            SeleniumDAO.click(associateChannelButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + chatChannelName + "')]/following-sibling::td//img[contains(@id, 'checkImg')]")));
            WebElement chatChannelCheckbox = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + chatChannelName + "')]/following-sibling::td//img[contains(@id, 'checkImg')]",
                    firefoxDriver);
            SeleniumDAO.click(chatChannelCheckbox);

            Thread.sleep(1000);
            Actions actions = new Actions(firefoxDriver);
            actions.sendKeys(Keys.ESCAPE).perform();

            return "Test OK. The chat channel was associated to the new group webchat";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The chat channel could not be associated to a group webchat";
        }
    }

    public String cloneService()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("clone-service")));
            WebElement cloneServiceButton = SeleniumDAO.selectElementBy("id", "clone-service", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(cloneServiceButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service")));
            WebElement serviceCopyNameInput = SeleniumDAO.selectElementBy("id", "service", firefoxDriver);
            serviceCopyNameInput.clear();
            serviceCopyNameInput.sendKeys(serviceSMCopyName);

            WebElement selectOtherSFRadiobutton = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'radioCloneType' and @value = 'select-other-showflow']", firefoxDriver);
            SeleniumDAO.click(selectOtherSFRadiobutton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("select-other-showflow")));
            Select otherSFSelector = SeleniumDAO.findSelectElementBy("id", "select-other-showflow", firefoxDriver);
            otherSFSelector.selectByVisibleText(showflowSMCopyName);

            WebElement cloneTelegramChannel = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'Telegram')]/following-sibling::td//input[@type = 'radio']", firefoxDriver);
            SeleniumDAO.click(cloneTelegramChannel);

            WebElement cloneChatChannel = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'Chat')]/following-sibling::td//input[@type = 'radio']", firefoxDriver);
            SeleniumDAO.click(cloneChatChannel);

            WebElement cloneTwitterChannel = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'Twitter DM')]/following-sibling::td//input[@type = 'radio']", firefoxDriver);
            SeleniumDAO.click(cloneTwitterChannel);

            Thread.sleep(500);

            WebElement submitButton = SeleniumDAO.selectElementBy("id", "sendButton", firefoxDriver);
            SeleniumDAO.click(submitButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(okButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(okButton);

            return "Test OK";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. ";
        }
    }

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
        Thread.sleep(1500);
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

    public String configureEmailSignature()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenterTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenterTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'mailSignatures.php']")));
            WebElement mailSignaturesButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'mailSignatures.php']", firefoxDriver);
            SeleniumDAO.click(mailSignaturesButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("newButton")));
            WebElement createNewSignature = SeleniumDAO.selectElementBy("id", "newButton", firefoxDriver);
            SeleniumDAO.click(createNewSignature);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("signatureName")));
            WebElement signatureNameInput = SeleniumDAO.selectElementBy("id", "signatureName", firefoxDriver);
            signatureNameInput.sendKeys("firmaEmailSelenium");

            SeleniumDAO.switchToFrame("textEditor_ifr", firefoxDriver);
            WebElement bodyText = SeleniumDAO.selectElementBy("id", "tinymce", firefoxDriver);
            bodyText.sendKeys(" DIAL_SERVICEID  DIAL_TICKET_NUMBER  DIAL_AUX1 ");

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "saveSignature", firefoxDriver);
            SeleniumDAO.click(saveButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(okButton);

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Could not create email signatures";
        }
    }


    public String configureEmailTemplate()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenterTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenterTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'MailTemplates.php']")));
            WebElement mailTemplates = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'MailTemplates.php']", firefoxDriver);
            SeleniumDAO.click(mailTemplates);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("newButton")));
            WebElement createNewTemplate = SeleniumDAO.selectElementBy("id", "newButton", firefoxDriver);
            SeleniumDAO.click(createNewTemplate);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name = 'name']")));
            WebElement templateNameInput = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'name']", firefoxDriver);
            templateNameInput.sendKeys("plantillaSelenium");

            SeleniumDAO.switchToFrame("textEditor_ifr", firefoxDriver);

            WebElement bodyText = SeleniumDAO.selectElementBy("id", "tinymce", firefoxDriver);
            bodyText.sendKeys(" DIAL_NAME  DIAL_EMAIL  DIAL_ADDRESS  DIAL_AGENT_USERNAME  DIAL_ORIGINAL_SUBJECT ");

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "savebutton", firefoxDriver);
            SeleniumDAO.click(saveButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(800);
            SeleniumDAO.click(okButton);

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Could not create the email template";
        }
    }


}
