package CheckListRafa;

import main.SeleniumDAO;
import Utils.Utils;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ini4j.*;

import java.util.HashMap;

public class CordinatorTest extends TestWithConfig {
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

    HashMap<String, String> results = new HashMap<>();

    public CordinatorTest(Wini ini) {
        super(ini);
    }

    public HashMap<String, String> check() {
        try {
            //Load inicializacion settings
            try {
                agentName = ini.get("Agent", "agentName");
                agentPassword = ini.get("Agent", "agentPassword");
                cordinatorName = ini.get("Cordinator", "cordinatorName");
                cordiantorPassword = ini.get("Cordinator", "cordinatorPassword");
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                chromePath = ini.get("Red", "chromePath");
            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 10);


            chromeDriver.get(url + "clienteweb");
            firefoxDriver.get(url + "clienteweb");

            results.put("--Coordinator and agent test  -->  ", coordinatorTest());
            return results;

        } catch(Exception e)
        {
            results.put("--Coordinator and agent test  -->  ", "ERROR. Unexpected exception");
            return results;

        } finally {
            chromeDriver.close();
            firefoxDriver.close();
        }
    }

    public String coordinatorTest(){
        try
        {
            //Login Cordinator on Chrome
            String loginStatus = loginOnChrome();
            if(loginStatus.contains("ERROR")) return loginStatus;
            //Login agent on Firefox
            loginStatus = loginOnFirefox();
            if(loginStatus.contains("ERROR")) return loginStatus;

            //Checks if the agent appears on the coordinator view
            chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("ui-id-4")));
            WebElement coordinatorTab = SeleniumDAO.selectElementBy("id", "ui-id-4", chromeDriver);
            SeleniumDAO.click(coordinatorTab);

            try {
                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@username = '" + agentName + "']")));
            } catch (Exception e) {                                                         //div[@username = agenteSebas]
                return e.toString() + "ERROR. The agent does not appear connected on the coordinator view";
            }

            //Change the state of the agent
            WebElement agentStatusOnAgentView = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
            SeleniumDAO.click(agentStatusOnAgentView);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("available")));


            WebElement availableStateOnAgentView = SeleniumDAO.selectElementBy("id", "available", firefoxDriver);
            SeleniumDAO.click(availableStateOnAgentView);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //Checks if the agent state has changed on Cordinator View
            try {
                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@status = 'available']")));
            } catch (Exception e) {
                return e.toString() + "\nERROR. The agent state has not been updated correctly on the coordinator view";
            }
            return "Test OK. The agent appears connected on the coordinator view, the agent has changed his state to available " +
                    "and the agent state has been updated correctly on the coordinator view ";
        } catch (Exception e)
        {
            return e.toString() + "ERROR. Unexpected exception";
        }
    }

    public String loginOnFirefox()
    {
        Utils.loginWebClient(agentName, agentPassword, 2, firefoxDriver);
        String res =Utils.checkCorrectLoginWebClient(firefoxDriver);
        if(res.contains("ERROR")) return res;
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
        return res;

    }
    public String loginOnChrome()
    {
        Utils.loginWebClient(cordinatorName, cordiantorPassword, 2, chromeDriver);
        String res = Utils.checkCorrectLoginWebClient(firefoxDriver);
        if(res.contains("ERROR")) return res;
        chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
        return res;
    }

}
