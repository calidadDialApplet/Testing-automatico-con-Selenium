package CheckListRafa;

import exceptions.MissingParameterException;
import main.SeleniumDAO;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import Utils.File;
import Utils.Utils;
import Utils.Color;
import org.apache.commons.io.FileUtils;
import org.ini4j.Wini;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Precondition: Jitsi has to be launched on the system and has selected the autocall mode. Also you need to have configured
 * the extension@url with its password
*/
public class CallsTest extends TestWithConfig {
    static String adminName;
    static String adminPassword;
    static String agentName;
    static String agentPassword;
    static String companyID;
    static String serviceID;
    static String callMode;
    static String url;
    static String number;
    static String call;
    static String extension;
    static String headless;
    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();
    String currentTime;

    public CallsTest(Wini ini) {
        super(ini);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName", "agentPassword", "extension", "callMode")));
        requiredParameters.put("Company", new ArrayList<>(Arrays.asList("companyID")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Contact", new ArrayList<>(Arrays.asList("number", "call")));

        return requiredParameters;
    }

    public HashMap<String, String> check() throws MissingParameterException {
        super.checkParameters();
        try {
            //Load inicializacion settings
            try {
                adminName = commonIni.get("Admin", "adminName");
                adminPassword = commonIni.get("Admin", "adminPassword");
                agentName = commonIni.get("Agent", "agentName");
                agentPassword = commonIni.get("Agent", "agentPassword");
                companyID = commonIni.get("Company", "companyID");
                serviceID = commonIni.get("Service", "serviceID");
                url = commonIni.get("General", "url");
                number = commonIni.get("Contact", "number");
                call = commonIni.get("Contact", "call");
                extension = commonIni.get("Agent", "extension");
                headless = commonIni.get("General", "headless");
                callMode = commonIni.get("Agent", "callMode");
            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            //Inicialize firefox driver
            firefoxDriver = DriversConfig.noDownloadPopUp(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            firefoxDriver.get(url + "clienteweb");

            //Login agent on Firefox
            Utils.loginWebClient(agentName, agentPassword, 1, firefoxDriver);

            results.put("--Calls Test  -->  ", CallsTest());
            results.put("--RecordTest -->  ", RecordTest());

            return results;

        } catch (Exception e) {
            return results;
        } finally
        {
            firefoxDriver.close();
        }
    }

    public String CallsTest()
    {
        try
        {
            //Selects the extension
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + extension + "']")));
                Select tlf = SeleniumDAO.findSelectElementBy("id", "extension", firefoxDriver);
                tlf.selectByValue(extension);
            } catch (Exception e) {
                //Checks if the invalid username and/or password dialog shows up or if the problem is the exception
                try {
                    WebDriverWait waitingError = new WebDriverWait(firefoxDriver, 5);
                    //If the error dialog is not found means that the username and password are correct so the problem is the extension
                    waitingError.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                    return "ERROR. The username and/or password is invalid";
                } catch (Exception e2) {
                    return "ERROR. Check if jitsi is launched, if is launched, the extension does not exist. You have to configure it first";
                }
            }
            //Login
            WebElement loginButton = SeleniumDAO.selectElementBy("id", "login", firefoxDriver);
            SeleniumDAO.click(loginButton);

            WebDriverWait firefoxWaiting = new WebDriverWait(firefoxDriver, 10);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));

            //Do a call
            WebElement callButton = SeleniumDAO.selectElementBy("id", "aclickToCall", firefoxDriver);
            SeleniumDAO.click(callButton);

            //Selects the callmode
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + callMode + "']")));
                Select whichCallMode = SeleniumDAO.findSelectElementBy("id", "manualCampaigns", firefoxDriver);
                whichCallMode.selectByValue(callMode);
                System.out.println("Selecting the callmode: " + callMode);
            } catch (Exception e) {
                return "ERROR. The callmode selected does not exist";
            }

            //call parameter on InicializationSettings=true. Do a call and checks the showflow and records
            if(call.equals("true"))
            {
                SeleniumDAO.click(callButton);

                WebElement numberField = SeleniumDAO.selectElementBy("id", "clickToCallDirect", firefoxDriver);
                SeleniumDAO.click(numberField);
                numberField.sendKeys(number);
                numberField.sendKeys(Keys.ENTER);

                firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='contactCall']")));

                callButton = SeleniumDAO.selectElementBy("xpath", "//button[@class='contactCall']", firefoxDriver);
                SeleniumDAO.click(callButton);
                String[] currentTimeSplited = LocalTime.now().toString().split(Pattern.quote(".")); //Aux array to delete the milisecs
                currentTime = currentTimeSplited[0];

                try
                {
                    Thread.sleep(2000);
                    File.deleteExistingFile("./screenshotCalls");
                    java.io.File screenshot = ((TakesScreenshot)firefoxDriver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(screenshot, new java.io.File("./screenshotCalls"));
                } catch (Exception e)
                {
                    return e.toString() + "\nERROR. The screenshot could not be taken";
                }

                Thread.sleep(1000);

                //Checks if the showflow appears
                try {
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//ul[@class = 'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all']//a[contains(., '" + number + "')]")));
                    System.out.println("The showflow appears");
                } catch (Exception e)
                {
                    System.out.println("Warning. The showflow does not appear");
                }

                System.out.println("Calling...");
                Thread.sleep(15000);



            } else {
                return "Test OK. The agent view works. If you want to do a call change de call parameter to 'true' in InicializationSettings.ini";
            }

            return "Test OK. If you had an entry call the test worked.";

        } catch (Exception e)
        {
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String RecordTest()
    {
        try
        {
            //Deletes all the .wav files in directory before the test starts
            File.deleteExistingFileByExtension("wav");

            //Checks the call panel to see the last call and download the record
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            String loginStatus = Utils.checkCorrectLoginDialappletWeb(firefoxDriver);
            if(loginStatus.contains("ERROR")) return loginStatus;

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("REPORTS")));
            WebElement analyzeTab = SeleniumDAO.selectElementBy("id", "REPORTS", firefoxDriver);
            SeleniumDAO.click(analyzeTab);

            Select company = SeleniumDAO.findSelectElementBy("id", "companySelect", firefoxDriver);
            company.selectByValue(companyID);
            Select service = SeleniumDAO.findSelectElementBy("id", "serviceSelect", firefoxDriver);
            service.selectByValue(serviceID);

            System.out.println("Trying to download old record");

            //First i am going to check a previous record, later the last record recently made.
            WebElement startDate = SeleniumDAO.selectElementBy("id", "f_trigger_start", firefoxDriver);
            SeleniumDAO.click(startDate);

            WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
            SeleniumDAO.click(previousMonthButton);
            SeleniumDAO.click(previousMonthButton); //Last 2 months

            WebElement endDate = SeleniumDAO.selectElementBy("id", "f_trigger_end", firefoxDriver);
            SeleniumDAO.click(endDate);

            SeleniumDAO.click(previousMonthButton); //Last month

            WebElement exitCalendarButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '×')]", firefoxDriver);
            SeleniumDAO.click(exitCalendarButton);

            WebElement showButton = SeleniumDAO.selectElementBy("id","submit", firefoxDriver);
            SeleniumDAO.click(showButton);

            //Try to download an old record
            try
            {
                WebElement saveRecord = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'activity_info']//tbody//tr[1]//a[contains(., 'WAV')]", firefoxDriver);
                SeleniumDAO.click(saveRecord);
                Thread.sleep(2000);

                String downloadState = File.waitToDownloadByExtension("wav",100);
                if(downloadState.contains("ERROR")) return downloadState;
            } catch (Exception e)
            {
                System.out.println(Color.PURPLE + "WARNING. No call appears in the chosen date range, or the chosen call was not recorded" + Color.RESET);
            }

            System.out.println("Trying to download last record");
            Thread.sleep(1000);

            //advance a month to reach the last record
            SeleniumDAO.click(endDate);
            WebElement nextMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '›')]", firefoxDriver);
            SeleniumDAO.click(nextMonthButton);
            SeleniumDAO.click(exitCalendarButton);
            SeleniumDAO.click(showButton);


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'activity_info']//tbody")));
            Thread.sleep(2000);
            String lastCallNumberOnTable;
            String lastCallTimeOnTable;

            //Get the number and time of the first row
            lastCallNumberOnTable = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[3]/div[2]/div/div[4]/table/tbody/tr[1]/td[1]", firefoxDriver).getText();
            lastCallTimeOnTable = SeleniumDAO.selectElementBy("xpath", "//table/tbody/tr[1]/td[10]",firefoxDriver).getText();

            //Substracts the times to ensure that the interval between them is small
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date timeOnCall = format.parse(currentTime);
            Date timeOnTableCalls = format.parse(lastCallTimeOnTable);
            long difference = timeOnTableCalls.getTime() - timeOnCall.getTime();

            //Compares the time and number
            if(difference < 10000 && lastCallNumberOnTable.equals(number))
            {
                WebElement saveRecord = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'activity_info']//tbody//tr[1]//img[@class = 'record']", firefoxDriver);
                SeleniumDAO.click(saveRecord);
                Thread.sleep(2000);

                String downloadState = File.waitToDownloadByExtension("wav",100);
                if(downloadState.contains("ERROR")) return downloadState;

            } else
            {
                return "ERROR. The last call in the call panel does not match with the call made in the test";
            }

            return "Test OK. The call panel and the record download function work. Check the work directory to see the downloaded records";
        } catch (Exception e)
        {
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }
}
