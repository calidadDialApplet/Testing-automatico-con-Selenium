package CheckListRafa;

import exceptions.MissingParameterException;
import main.SeleniumDAO;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import Utils.File;
import Utils.Utils;

import org.apache.commons.io.FileUtils;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProductivityAgentsTest extends TestWithConfig {
    static String adminName;
    static String adminPassword;
    static String url;
    static String headless;
    static String serviceID;
    static String dateSearch;
    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;
    static List<WebElement> graphList;

    HashMap<String, String> results = new HashMap<>();

    public ProductivityAgentsTest(Wini ini) {
        super(ini);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless", "chromePath")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID", "dateSearch")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws MissingParameterException {
        super.checkParameters();
        try {
            //Load inicializacion settings
            try {
                adminName = commonIni.get("Admin", "adminName");
                adminPassword = commonIni.get("Admin", "adminPassword");
                url = commonIni.get("General", "url");
                headless = commonIni.get("General", "headless");
                serviceID = commonIni.get("Service", "serviceID");
                dateSearch = commonIni.get("Service", "dateSearch");
            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);

            firefoxDriver.get(url + "dialapplet-web");

            results.put("--Productivity agents test  -->  ", productivityAgentsTest());

            try
            {
                Thread.sleep(10000);
            } catch (Exception e) {}

            return results;

        } finally
        {
            firefoxDriver.close();
        }
    }

    public String productivityAgentsTest()
    {
        try
        {
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            String loginStatus = Utils.checkCorrectLoginDialappletWeb(firefoxDriver);
            if(loginStatus.contains("ERROR")) return loginStatus;
            //Searchs the service
            try
            {
                WebElement searchField = SeleniumDAO.selectElementBy("id", "search", firefoxDriver);
                searchField.sendKeys(serviceID);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service-" + serviceID)));
                WebElement service = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'service-" + serviceID + "']/td", firefoxDriver);
                SeleniumDAO.click(service);
            } catch(Exception e)
            {
                return e.toString() + "ERROR. There is no service with the ID " + serviceID;
            }

            //Wait to click on productivity
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service_productivity")));
            WebElement productivity = SeleniumDAO.selectElementBy("id", "service_productivity", firefoxDriver);
            SeleniumDAO.click(productivity);

            //Wait the tabs to search by date
            try {
                //Thread.sleep(500);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs")));
                Select date = SeleniumDAO.findSelectElementBy("id", "select_hour_date", firefoxDriver);
                Thread.sleep(500);
                date.selectByValue(dateSearch);

                //Thread.sleep(500);

                firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.id("trigger_start_date_hour")));
                WebElement startDate = SeleniumDAO.selectElementBy("id", "trigger_start_date_hour", firefoxDriver);
                SeleniumDAO.click(startDate);

                //Searchs 2 month before
                WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
                SeleniumDAO.click(previousMonthButton);
                SeleniumDAO.click(previousMonthButton);
            } catch (Exception e)
            {
                return e.toString() + "ERROR. The search by " + dateSearch + "does not exists or the tabs have not been found";
            }

            try
            {
                Thread.sleep(500);
            } catch (Exception e)
            {
                return e.toString() + "ERROR. Unexpected exception";
            }
            //Click on sendButton
            WebElement sendButton = SeleniumDAO.selectElementBy("id","submit_form",firefoxDriver);
            SeleniumDAO.click(sendButton);

            //Takes a screenshot of the driver to check it manually after the test finishes
            try
            {
                Thread.sleep(2000);
                File.deleteExistingFile("./screenshotProductivityAgents");
                java.io.File screenshot = ((TakesScreenshot)firefoxDriver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(screenshot, new java.io.File("./screenshotProductivityAgents"));
            } catch (Exception e)
            {
                return e.toString() + "\nERROR. The screenshot could not be taken";
            }

            return "Test OK. A screenshot of the graph has been taken. Look at the folder to check if the test has worked.";



        } catch (Exception e)
        {
            return e.toString() + "ERROR. Unexpected exception";
        }

    }
}
