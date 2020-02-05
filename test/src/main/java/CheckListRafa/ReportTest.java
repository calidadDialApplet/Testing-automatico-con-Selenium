package CheckListRafa;

import main.Main;
import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReportTest extends Test {
    static String adminName;
    static String adminPassword;
    static String url;
    static String headless;
    static String serviceID;
    static String dateSearch;
    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;
    static List<WebElement> graphList;
    @Override
    public void check() {
        try {
            //Load inicializacion settings
            try {
                Wini ini = new Wini(new File("InicializationSettings.ini"));
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                serviceID = ini.get("Service", "serviceID");
                dateSearch = ini.get("Service", "dateSearch");
            } catch (IOException e) {
                System.out.println("The inicialization file can't be loaded");
                e.printStackTrace();
                return;
            }

            firefoxDriver = headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            firefoxDriver.get(url + "dialapplet-web");
            Main.loginDialappletWeb(adminName, adminPassword, firefoxDriver);


            //Searchs the service by the id
            if(!searchService()) return;

            clickOnProductivity();

            if(!selectDateSearch()) return;

            if(!checkGraphs()) return;

            try
            {
                Thread.sleep(10000);
            } catch (Exception e) {}

        } finally
        {
            firefoxDriver.close();
        }
    }

    public boolean searchService()
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
    }
}
