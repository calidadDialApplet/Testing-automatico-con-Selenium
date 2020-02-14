package main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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
    public static void loginDialappletWeb(String name, String password, WebDriver driver){
        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys(name);

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys(password);

        WebElement login = driver.findElement(By.id("login"));
        login.click();
    }

    // TODO: Garbage clean
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


}
