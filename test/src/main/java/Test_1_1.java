
import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.WebDriver;




// Connect with the user admin that user and start performing operations.


public class Test_1_1 {
    public static void main(String[] args) {
        WebDriver driver = SeleniumDAO.initializeFirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        Main.loginDialappletWeb("admin", "admin",driver);
    }
}
