package CheckListRafa;

import main.SeleniumDAO;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import Utils.File;
import Utils.Utils;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    static String companyName;
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

    public CallsTest(Wini ini) {
        super(ini);
    }

    public HashMap<String, String> check() {

        try {
            //Load inicializacion settings
            try {
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                agentName = ini.get("Agent", "agentName");
                agentPassword = ini.get("Agent", "agentPassword");
                companyName = ini.get("Company", "companyName");
                serviceID = ini.get("Service", "serviceID");
                url = ini.get("Red", "url");
                number = ini.get("Contact", "number");
                call = ini.get("Contact", "call");
                extension = ini.get("Agent", "extension");
                headless = ini.get("Red", "headless");
                callMode = ini.get("Agent", "callMode");
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
                    return "ERROR. Check if jitsi is launched, if not, the extension does not exist. You have to configure it first";
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

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='contactCall']")));

                callButton = SeleniumDAO.selectElementBy("xpath", "//button[@class='contactCall']", firefoxDriver);
                SeleniumDAO.click(callButton);
                String[] currentTimeSplited = LocalTime.now().toString().split(Pattern.quote(".")); //Aux array to delete the milisecs
                String currentTime = currentTimeSplited[0];


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

                //Checks the call panel to see the last call and download the record
                firefoxDriver.get(url + "dialapplet-web");
                Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("REPORTS")));
                WebElement analyzeTab = SeleniumDAO.selectElementBy("id", "REPORTS", firefoxDriver);
                SeleniumDAO.click(analyzeTab);

                Select company = SeleniumDAO.findSelectElementBy("id", "companySelect", firefoxDriver);
                company.selectByVisibleText(companyName);
                Select service = SeleniumDAO.findSelectElementBy("id", "serviceSelect", firefoxDriver);
                service.selectByValue(serviceID);

                WebElement startDate = SeleniumDAO.selectElementBy("id", "f_trigger_start", firefoxDriver);
                SeleniumDAO.click(startDate);

                WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
                SeleniumDAO.click(previousMonthButton);

                WebElement showButton = SeleniumDAO.selectElementBy("id","submit", firefoxDriver);
                SeleniumDAO.click(showButton);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'activity_info']//tbody")));
                Thread.sleep(2000);
                String lastCallNumberOnTable;
                String lastCallTimeOnTable;
                try
                {
                    List<WebElement> callsTable = firefoxDriver.findElements(By.xpath("//table[@id = 'activity_info']//tbody"));
                    lastCallNumberOnTable = callsTable.get(0).findElement(By.xpath("//tr[1]//td[contains(., '" + number + "')]")).getText();
                    lastCallTimeOnTable = callsTable.get(0).findElement(By.xpath("//tr[1]//td[10]")).getText();
                } catch(Exception e)
                {
                    return e.toString() + "\nERROR. The phone number or the time of the last call have not been found on the calls panel";
                }


                //Substracts the times to ensure that the interval between them is small
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date timeOnCall = format.parse(currentTime);
                Date timeOnTableCalls = format.parse(lastCallTimeOnTable);
                long difference = timeOnTableCalls.getTime() - timeOnCall.getTime();

                if(difference < 10000 && lastCallNumberOnTable.equals(number))
                {
                    File.deleteExistingFileByExtension("wav");

                    WebElement saveRecord = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'activity_info']//tbody//tr[1]//img[@class = 'record']", firefoxDriver);
                    SeleniumDAO.click(saveRecord);
                    Thread.sleep(2000);

                    String downloadState = File.waitToDownloadByExtension("wav",100);
                    if(downloadState.contains("ERROR")) return downloadState;

                } else
                {
                    return "ERROR. The call was done but the report calls does not show it right";
                }

                return "Test OK. If you had an entry call the test worked. The showflow tab, the calls panel and the records download work too.";

            } else {
                return "Test OK. The agent view works. If you want to do a call change de call parameter to 'true' in InicializationSettings.ini";
            }


        } catch (Exception e)
        {
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }
}
