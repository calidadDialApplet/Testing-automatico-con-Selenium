package ProcedimientoRC;

import org.ini4j.Wini;
import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.HashMap;

public class ParteDeShowflow extends Test {
    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String showflowName;


    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();

    @Override
    public HashMap<String, String> check() {
        try
        {
            try
            {
                Wini ini = new Wini(new File("InicializationSettingsRC.ini"));
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                showflowName = ini.get("Showflow", "showflowName");

            } catch (Exception e)
            {

            }

            firefoxDriver = headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);

            results.put("--Create Showflow  ->  ", createShowflow());

            return results;
        } catch (Exception e)
        {
            return results;
        } finally
        {
            firefoxDriver.close();
        }

    }

    public String createShowflow()
    {
        try
        {
            firefoxDriver.get(url + "dialapplet-web");
            Main.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            WebElement createShowflow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'setShowflow.php']", firefoxDriver);
            SeleniumDAO.click(createShowflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));

            WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "workflow-name", firefoxDriver);
            showflowNameInput.sendKeys(showflowName);

            WebElement refreshRadioButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'form-col input radio']//input[@name = 'workflow-showrefreshbutton']", firefoxDriver);
            SeleniumDAO.click(refreshRadioButton);

            WebElement sendButton = SeleniumDAO.selectElementBy("id", "workflow-send", firefoxDriver);
            SeleniumDAO.click(sendButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                Thread.sleep(500);
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sweet-alert showSweetAlert visible']//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            } catch (Exception e)
            {
                return e.toString() + "\nERROR: The showflow could not be created. Check if it already exists";
            }

            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", firefoxDriver);
            SeleniumDAO.click(showflowPanel);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(showflowName);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            return "Test OK. The showflow has been created";
        } catch (Exception e)
        {
            return e.toString() + "\nERROR: Test failed";
        }
    }
}
