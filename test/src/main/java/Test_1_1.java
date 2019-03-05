import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


//Connect with the user admin that user and start performing operations.


public class Test_1_1 {
    public static void main(String[] args) {

        //Go to http://pruebas7.dialcata.com/dialapplet-web/
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        WebElement user = driver.findElement(By.id("adminusername"));

        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));

        pass.sendKeys("admin");

        WebElement loggin = driver.findElement(By.id("login"));

        loggin.click();

        //Close the browser
        //driver.close();
    }
}
