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
            File.deleteExistingFile(path);
            java.io.File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new java.io.File(path));
        } catch (Exception e)
        {
            throw e;
        }
    }

}
