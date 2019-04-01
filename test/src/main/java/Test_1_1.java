import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


// Connect with the user admin that user and start performing operations.


public class Test_1_1 {
    public static void main(String[] args) {
        WebDriver driver = SeleniumDAO.initializeDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        Main.loginDialappletWeb("admin", "admin",driver);
    }
}
