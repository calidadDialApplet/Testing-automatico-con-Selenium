package CheckListRafa;

import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ini4j.*;

import java.io.File;
import java.io.IOException;

public class CordinatorTest extends Test {
    static String cordinatorName;
    static String cordiantorPassword;
    static String agentName;
    static String agentPassword;
    static String url;
    static String headless;
    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;
    static WebDriverWait chromeWaiting;
    static ChromeDriver chromeDriver;
    static String chromePath;

    public void check() {
        try {
            //Load inicializacion settings
            try {
                Wini ini = new Wini(new File("InicializationSettings.ini"));
                agentName = ini.get("Agent", "agentName");
                agentPassword = ini.get("Agent", "agentPassword");
                cordinatorName = ini.get("Cordinator", "cordinatorName");
                cordiantorPassword = ini.get("Cordinator", "cordinatorPassword");
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                chromePath = ini.get("Red", "chromePath");
            } catch (IOException e) {
                System.out.println("The inicialization file can't be loaded");
                e.printStackTrace();
                return;
            }

            firefoxDriver = headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            chromeDriver = headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 10);


            chromeDriver.get(url + "clienteweb");
            firefoxDriver.get(url + "clienteweb");

            //Login Cordinator on Chrome
            loginOnChrome();
            //Login agent on Firefox
            loginOnFirefox();

            //Checks if the agent appears on the coordinator view
            if(!checkAgentAppears()) return;

            //Change the state of the agent
            changeAgentState();

            //Checks if the agent state has changed on Cordinator View
            if (!checkAgentStateUpdated()) return;

        } finally {
            chromeDriver.close();
            firefoxDriver.close();
            System.out.println("CordinatorTest has finished. Drivers closed.");
        }
    }

    public boolean checkAgentStateUpdated() {
        try {
            chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@status = 'available']")));
            System.out.println("The agent state has been updated correctly on the coordinator view");
        } catch (Exception e) {
            System.out.println("ERROR: The agent state has not been updated correctly on the coordinator view");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void loginOnFirefox()
    {
        Main.loginWebClient(agentName, agentPassword, 2, firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
    }
    public void loginOnChrome()
    {
        Main.loginWebClient(cordinatorName, cordiantorPassword, 2, chromeDriver);
        chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
    }
    public boolean checkAgentAppears()
    {
        chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("ui-id-4")));
        WebElement coordinatorTab = SeleniumDAO.selectElementBy("id", "ui-id-4", chromeDriver);
        SeleniumDAO.click(coordinatorTab);

        try {
            chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(., " + agentName + ")]")));
            System.out.println("The agent appears connected on the coordinator view");
        } catch (Exception e) {
            System.out.println("ERROR: The agent does not appear connected on the coordinator view");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void changeAgentState()
    {
        WebElement agentStatusOnAgentView = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
        SeleniumDAO.click(agentStatusOnAgentView);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("available")));


        WebElement availableStateOnAgentView = SeleniumDAO.selectElementBy("id", "available", firefoxDriver);
        SeleniumDAO.click(availableStateOnAgentView);
        System.out.println("The agent has changed his state to available");

        SeleniumDAO.switchToDefaultContent(firefoxDriver);
    }
}
