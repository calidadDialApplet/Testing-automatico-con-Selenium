package CheckListRafa;

import main.Utils;
import main.SeleniumDAO;
import Utils.TestWithConfig;
import Utils.DriversConfig;

import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;

public class ReportTest extends TestWithConfig {
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

    public ReportTest(Wini ini) {
        super(ini);
    }

    @Override
    public HashMap<String, String> check()
    {
        try {
            //Load inicializacion settings
            try {
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                serviceID = ini.get("Service", "serviceID");
                dateSearch = ini.get("Service", "dateSearch");
            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);

            results.put("--Report test  -->  ", reportTest());

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

    public String reportTest()
    {
        try
        {
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
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs")));
                Select date = SeleniumDAO.findSelectElementBy("id", "select_hour_date", firefoxDriver);
                date.selectByValue(dateSearch);

                Thread.sleep(500);

                firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.id("trigger_start_date_hour")));
                WebElement startDate = SeleniumDAO.selectElementBy("id", "trigger_start_date_hour", firefoxDriver);
                SeleniumDAO.click(startDate);

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

            Thread.sleep(1500);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs-agents")));
            graphList = firefoxDriver.findElements(By.xpath("//div[@id = 'tabs-agents']/div[@class = 'tableProductivity']"));
            System.out.println("The size of the list is " + graphList.size());

            int count = 0;
            try
            {
                Thread.sleep(500);
            } catch (Exception e)
            {
                return e.toString() + "ERROR. Unexpected exception";
            }
            for(int i = 0; i < graphList.size(); i++)
            {
                WebElement actualElement = graphList.get(i);
                String styleAttribute = actualElement.getAttribute("style");
                System.out.println(styleAttribute);
                if(styleAttribute.equals("display: block;"))
                {
                    count++;
                }
            }
            System.out.println("For the " + graphList.size() + " graph elements to be shown, it shows " + count + " of them");

            if(graphList.size() == count){return "Test OK. The report graphs appear";}
            else {return "ERROR. You should check this test without headless mode";}


        } catch (Exception e)
        {
            return e.toString() + "ERROR. Unexpected exception";
        }

    }

    /*public boolean searchService()
    {
        try
        {
            WebElement searchField = SeleniumDAO.selectElementBy("id", "search", firefoxDriver);
            searchField.sendKeys(serviceID);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service-" + serviceID)));
            WebElement service = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'service-" + serviceID + "']/td", firefoxDriver);
            SeleniumDAO.click(service);
        } catch(Exception e)
        {
            System.out.println("There is no service with the ID " + serviceID);
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void clickOnProductivity()
    {
        //Wait to click on productivity
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service_productivity")));
        WebElement productivity = SeleniumDAO.selectElementBy("id", "service_productivity", firefoxDriver);
        SeleniumDAO.click(productivity);
    }
    public boolean selectDateSearch()
    {
        //Wait the tabs to search by date
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs")));
            Select date = SeleniumDAO.findSelectElementBy("id", "select_hour_date", firefoxDriver);
            date.selectByValue(dateSearch);
        } catch (Exception e)
        {
            System.out.println("The search by " + dateSearch + "does not exists or the tabs have not been found");
            e.printStackTrace();
            return false;
        }

        try
        {
            Thread.sleep(500);
        } catch (Exception e){return false;}
        //Click on sendButton
        WebElement sendButton = SeleniumDAO.selectElementBy("id","submit_form",firefoxDriver);
        SeleniumDAO.click(sendButton);
        return true;
    }
    public boolean checkGraphs()
    {
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs-agents")));
        graphList = firefoxDriver.findElements(By.xpath("//div[@id = 'tabs-agents']/div[@class = 'tableProductivity']"));
        System.out.println("The size of the list is " + graphList.size());

        int count = 0;
        try
        {
            Thread.sleep(500);
        } catch (Exception e){return false;}
        for(int i = 0; i < graphList.size(); i++)
        {
            WebElement actualElement = graphList.get(i);
            String styleAttribute = actualElement.getAttribute("style");
            System.out.println(styleAttribute);
            if(styleAttribute.equals("display: block;"))
            {
                count++;
            }
        }
        System.out.println("For the " + graphList.size() + " graph elements to be shown, it shows " + count + " of them");
        return true;
    }*/
}
