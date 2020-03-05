package ProcedimientoRC;

import Utils.DriversConfig;
import Utils.TestWithConfig;
import Utils.Utils;
import gui.Action;
import org.ini4j.Wini;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import main.SeleniumDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParteDeServicioTest extends TestWithConfig {

    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String serviceID;
    static String campaignBeginningDate;
    static String campaignEndDate;
    static String showflowName;
    static String transfCallModeName;
    static String manualCallModeName;
    static String predictCallModeName;


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
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));
        requiredParameters.put("Showflow", new ArrayList<>(Arrays.asList("showflowName")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("campaignBeginningDate", "campaignEndDate", "transfCallModeName", "manualCallModeName",
                "predictCallModeName")));

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
            campaignBeginningDate = commonIni.get("CallMode", "campaignBeginningDate");
            campaignEndDate = commonIni.get("CallMode", "campaignEndDate");
            showflowName = commonIni.get("Showflow", "showflowName");
            transfCallModeName = commonIni.get("CallMode", "transfCallModeName");
            manualCallModeName = commonIni.get("CallMode", "manualCallModeName");
            predictCallModeName = commonIni.get("CallMode", "predictCallModeName");

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            results.put("--Basic data test  ->  ", basicDataTest());
            results.put("--Showflow test  ->  ", showflowTest());
            results.put("--Call mode test  ->  ", callModeTest());

            return results;
        } catch (Exception e) {
            return results;
        } finally {
            firefoxDriver.close();
        }
    }

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
                if(!assignableGroups.get(i).isSelected())
                {
                    SeleniumDAO.click(assignableGroups.get(i));
                }
            }

            //Selects the dates of the campaign
            WebElement beginningDateInput = SeleniumDAO.selectElementBy("id", "periodbegin_1", firefoxDriver);
            WebElement endDateInput = SeleniumDAO.selectElementBy("id", "periodend_1", firefoxDriver);
            beginningDateInput.clear();
            endDateInput.clear();
            beginningDateInput.sendKeys(campaignBeginningDate);
            endDateInput.sendKeys(campaignEndDate);

            //Selects the schedule
            WebElement mondayCheckbox = SeleniumDAO.selectElementBy("id", "day1", firefoxDriver);
            SeleniumDAO.click(mondayCheckbox);

            WebElement beginningHourInput = SeleniumDAO.selectElementBy("id", "since_day1_mor", firefoxDriver);
            WebElement endHourInput = SeleniumDAO.selectElementBy("id", "until_day1_mor", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("since_day1_mor")));
            beginningHourInput.sendKeys("09:00:00");
            endHourInput.sendKeys("17:00:00");

            WebElement copyToFridayButton = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'copyToFriday']", firefoxDriver);
            SeleniumDAO.click(copyToFridayButton);

            //100% recording rate
            Select recordingRateSelector = SeleniumDAO.findSelectElementBy("id", "recordingrate", firefoxDriver);
            recordingRateSelector.selectByValue("100");


            JavascriptExecutor js = (JavascriptExecutor)firefoxDriver;
            js.executeScript("window.scrollBy(0,250)");
            Utils.takeScreenshot("./ParteDeServicioOut/basicDataScreenshot", firefoxDriver);


            //save changes and go to call modes tab
            WebElement changeButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(changeButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']")));
            WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);
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

            WebElement recordLabel = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'audioformat']", firefoxDriver);
            recordLabel.clear();
            recordLabel.sendKeys("RCNvXYZ%20-%21-%1-%9-%8-%6");

            //Robinson list to yes
            WebElement robinsonRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'robinsonyes']", firefoxDriver);
            SeleniumDAO.click(robinsonRadioButton);

            //Save
            changeButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            SeleniumDAO.click(changeButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']")));
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);

            //Checks if robinsonlist tab appears after saving
            try{
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
    public String showflowTest(){
        try
        {
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
            readOnly.selectByVisibleText("ReadOnly");

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

            Utils.takeScreenshot("./ParteDeServicioOut/showflowOptionsScreenshot", firefoxDriver);

            //save
            WebElement nextButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(nextButton);

            return "Test OK. The showflow options have been configurated. Check the folder 'ParteDeServicioOut' to see the taken screenshot.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR";
        }
    }
    public String callModeTest()
    {
        try
        {
            //Create new callmode: manual transferencia
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("createCampaign")));
            WebElement createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
            SeleniumDAO.click(createCallModeButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            Select dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
            dialingModeSelector.selectByVisibleText("Manual");

            WebElement nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
            nameInput.sendKeys(transfCallModeName);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("campaignfortransfersyes")));
            WebElement transferRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'campaignfortransfersyes']", firefoxDriver);
            SeleniumDAO.click(transferRadioButton);

            WebElement addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addCallModeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            Actions actions = new Actions(firefoxDriver);
            actions.sendKeys(Keys.ESCAPE).perform();
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //Create new callMode: manual
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("createCampaign")));
            createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(createCallModeButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
            dialingModeSelector.selectByVisibleText("Manual");

            nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
            nameInput.sendKeys(manualCallModeName);

            addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addCallModeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            actions.sendKeys(Keys.ESCAPE).perform();
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //Create callMode: predictive
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("createCampaign")));
            createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(createCallModeButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
            dialingModeSelector.selectByVisibleText("Predictive");

            nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
            nameInput.sendKeys(predictCallModeName);

            addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addCallModeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            actions.sendKeys(Keys.ESCAPE).perform();
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //Create callBack for predictive callMode
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("createCampaign")));
            createCallModeButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'createCampaign']", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(createCallModeButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
            dialingModeSelector = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'createCampaignTable']//select[@id = 'dialingmode']", firefoxDriver);
            dialingModeSelector.selectByVisibleText("Power dialer (auto)");

            nameInput = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'createCampaignTable']//input[@id = 'name']", firefoxDriver);
            nameInput.sendKeys(predictCallModeName + " Callback");

            addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addCallModeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            actions.sendKeys(Keys.ESCAPE).perform();
            Thread.sleep(1000);
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            WebElement favouriteCallModeButton = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + predictCallModeName + "')]/following-sibling::td/img[@class = 'baseStarOff']",
                    firefoxDriver);
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
            Thread.sleep(400);
            callbackSelector.selectByVisibleText(predictCallModeName + " Callback");

            addCallModeButton = SeleniumDAO.selectElementBy("id", "add", firefoxDriver);
            SeleniumDAO.click(addCallModeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("send")));
            actions.sendKeys(Keys.ESCAPE).perform();
            Thread.sleep(1000);
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            WebElement phoneCallModeButton = SeleniumDAO.selectElementBy("xpath", "//tr/td[contains(., '" + predictCallModeName + "')]/following-sibling::td/a[@class = 'maximumControl iframe']",
                    firefoxDriver);
            SeleniumDAO.click(phoneCallModeButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("maximumtype")));
            Select calltypeSelector = SeleniumDAO.findSelectElementBy("id","maximumtype", firefoxDriver);
            calltypeSelector.selectByVisibleText(" Combined (Slots - Typology)");





            return "Test OK";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR";
        }
    }
}
