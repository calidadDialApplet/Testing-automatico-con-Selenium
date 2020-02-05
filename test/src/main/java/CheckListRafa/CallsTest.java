package CheckListRafa;

import main.Main;
import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import java.io.File;
import java.io.IOException;

/**
 * Precondition: Jitsi has to be launched on the system and has selected the autocall mode. Also you need to have configured
 * the extension@url with its password
*/
public class CallsTest extends Test {
    static String agentName;
    static String agentPassword;
    static String callMode;
    static String url;
    static String number;
    static String extension;
    static String headless;
    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    public void check() {

        try {
            //Load inicializacion settings
            try {
                Wini ini = new Wini(new File("InicializationSettings.ini"));
                agentName = ini.get("Agent", "agentName");
                agentPassword = ini.get("Agent", "agentPassword");
                url = ini.get("Red", "url");
                number = ini.get("Contact", "number");
                extension = ini.get("Agent", "extension");
                headless = ini.get("Red", "headless");
                callMode = ini.get("Agent", "callMode");
            } catch (IOException e) {
                System.out.println("The inicialization file can't be loaded");
                e.printStackTrace();
                return;
            }

            firefoxDriver = headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            firefoxDriver.get(url + "clienteweb");

            //Login agent on Firefox
            Main.loginWebClient(agentName, agentPassword, 1, firefoxDriver);

            if (!selectExtension()) return;
            login();
            if (!doACall()) return;
            if (!checkShowflowTab()) return;

        }
        finally
        {
            firefoxDriver.close();
            System.out.println("CallsTest has finished. Drivers closed");
        }
    }

    public boolean selectExtension()
    {
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + extension + "']")));
            Select tlf = SeleniumDAO.findSelectElementBy("id", "extension", firefoxDriver);
            tlf.selectByValue(extension);
            return true;
        } catch (Exception e) {
            //Checks if the invalid username and/or password dialog shows up or if the problem is the exception
            try {
                WebDriverWait waitingError = new WebDriverWait(firefoxDriver, 5);
                //If the error dialog is not found means that the username and password are correct so the problem is the extension
                waitingError.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                System.err.println("The username and/or password is invalid");
                firefoxDriver.close();
                return false;
            } catch (Exception e2) {
                System.err.println("The extension does not exist. You have to configure it first.");
                e.printStackTrace();
                return false;
            }
    }
    }
    public void login()
    {
        WebElement loginButton = SeleniumDAO.selectElementBy("id", "login", firefoxDriver);
        SeleniumDAO.click(loginButton);

        WebDriverWait firefoxWaiting = new WebDriverWait(firefoxDriver, 10);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
    }
    public boolean doACall()
    {
        WebElement callButton = SeleniumDAO.selectElementBy("id", "aclickToCall", firefoxDriver);
        SeleniumDAO.click(callButton);

        //Selects the callmode
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + callMode + "']")));
            Select whichCallMode = SeleniumDAO.findSelectElementBy("id", "manualCampaigns", firefoxDriver);
            whichCallMode.selectByValue(callMode);
            System.out.println("Selecting the callmode");
        } catch (Exception e) {
            System.out.println("The callmode selected does not exist.");
            e.printStackTrace();
            return false;
        }

        SeleniumDAO.click(callButton);

        WebElement numberField = SeleniumDAO.selectElementBy("id", "clickToCallDirect", firefoxDriver);
        SeleniumDAO.click(numberField);
        numberField.sendKeys(number);
        numberField.sendKeys(Keys.ENTER);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='contactCall']")));

        callButton = SeleniumDAO.selectElementBy("xpath", "//button[@class='contactCall']", firefoxDriver);
        SeleniumDAO.click(callButton);
        return true;
    }
    public boolean checkShowflowTab()
    {
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//ul[@class = 'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all']//a[contains(., '" + number + "')]")));
            System.out.println("The showflow appears");
        } catch (Exception e)
        {
            System.err.println("ERROR: The showflow does not appear");
            return false;
        }

        try {
            System.out.println("Calling...");
            Thread.sleep(8000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
