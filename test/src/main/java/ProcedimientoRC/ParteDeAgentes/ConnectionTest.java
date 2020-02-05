package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.WebDriver;




// Connect with the user admin that user and start performing operations.

//First test
public class ConnectionTest {
    static final String ADMIN = "sebasAdmin";
    static final String PASSWORD = "A1234567890a";
    static WebDriver driver;

    public static void main(String[] args) {
        try {
            driver = SeleniumDAO.initializeFirefoxDriver();
            driver.get("https://pruebas7rc.dialcata.com/dialapplet-web/");
            Main.loginDialappletWeb(ADMIN, PASSWORD, driver);
        } finally
        {
            driver.close();
        }
    }
}
