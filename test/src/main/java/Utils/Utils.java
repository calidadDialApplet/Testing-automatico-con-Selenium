package Utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import main.SeleniumDAO;

import java.io.IOException;
import java.util.Random;

public class Utils {

    public static String generateUniqueID(String chain){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(Integer.toHexString(random.nextInt()));
        }

        String result = chain.concat(sb.toString().substring(0, 6));
        return result;
    }

    // TODO: Garbage clean
    //Login on DialAppletWeb
    public static void loginDialappletWeb(String name, String password, WebDriver driver){
        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys(name);

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys(password);

        WebElement login = driver.findElement(By.id("login"));
        login.click();
    }

    // TODO: Garbage clean
    //Login on WebClient
    public static void loginWebClient(String name, String password, int tlfOption, WebDriver driver){
        WebElement usernameWebClient = SeleniumDAO.selectElementBy("id","userName",driver);
        usernameWebClient.sendKeys(name);

        WebElement passWebClient = SeleniumDAO.selectElementBy("id","passwordBridge",driver);
        passWebClient.sendKeys(password);

        Select tlf = SeleniumDAO.findSelectElementBy("id","selectType", driver);
        tlf.selectByIndex(tlfOption);

        WebElement entryWebClient = SeleniumDAO.selectElementBy("id","checklogin",driver);
        SeleniumDAO.click(entryWebClient);
    }

    //Checks if the login fails on WebCliente
    public static String checkCorrectLoginWebClient(WebDriver driver)
    {
        try {
            WebDriverWait waitingError = new WebDriverWait(driver, 5);
            //If the error dialog is not found means that the username and password are correct so the problem is the extension
            waitingError.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'sa-icon sa-error animateErrorIcon']")));
            return "ERROR. The username and/or password is invalid";
        } catch (Exception e2) {
            return "Logged correctly";
        }
    }

    //checks if the login fails on DialApplet Web
    public static String checkCorrectLoginDialappletWeb(WebDriver driver)
    {
        try {
            WebDriverWait waitingError = new WebDriverWait(driver, 5);
            waitingError.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'mainMenu']")));
            return "Logged correctly";
        } catch (Exception e2) {
            return "ERROR. The username and/or password is invalid";

        }
    }

    public static void takeScreenshot(String path, WebDriver driver) throws IOException, InterruptedException {
        try
        {
            Thread.sleep(1000);
            File.deleteExistingFile(path + ".png");
            java.io.File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new java.io.File(path + ".png"));
        } catch (Exception e)
        {
            throw e;
        }
    }

    public static void logoutWebClient(WebDriverWait firefoxWaiting, WebDriver firefoxDriver) throws InterruptedException {
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("logout")));
        WebElement logOutButton = SeleniumDAO.selectElementBy("id", "logout", firefoxDriver);
        Thread.sleep(2000);
        SeleniumDAO.click(logOutButton);
        Thread.sleep(5000);
    }

    public static String createShowflow(WebDriver driver, WebDriverWait waiting, String showflowName, String showflowType)
    {
        WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", driver);
        SeleniumDAO.click(showflowTab);

        WebElement createShowflow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'setShowflow.php']", driver);
        SeleniumDAO.click(createShowflow);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));

        WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "workflow-name", driver);
        showflowNameInput.sendKeys(showflowName);

        Select showflowTypeSelector = SeleniumDAO.findSelectElementBy("id", "workflow-type", driver);
        showflowTypeSelector.selectByValue(showflowType);

        WebElement refreshRadioButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'form-col input radio']//input[@name = 'workflow-showrefreshbutton']", driver);
        SeleniumDAO.click(refreshRadioButton);

        WebElement sendButton = SeleniumDAO.selectElementBy("id", "workflow-send", driver);
        SeleniumDAO.click(sendButton);

        try
        {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
            Thread.sleep(500);
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sweet-alert showSweetAlert visible']//button[@class = 'confirm']", driver);
            SeleniumDAO.click(okButton);
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR: The showflow could not be created. Check if it already exists";
        }

        WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", driver);
        SeleniumDAO.click(showflowPanel);

        //Searchs the new showflow in the table and checks if appears
        try {
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", driver);
            searcher.sendKeys(showflowName);
            Thread.sleep(1000);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "ERROR: Something went wrong. The showflow was created but don't appears on the showflows table";
        }
        return "";
    }
}
