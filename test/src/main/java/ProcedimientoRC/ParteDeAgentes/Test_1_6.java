package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

// Crea un usuario agente2, se conecta y está un minuto en preturno

public class Test_1_6 {
    static final String PASSWORD = "A1234567890a";
    static final String ADMIN = "sebasAdmin";
    static final String NAME = "Agente2rcver833";
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        try {
            System.setProperty("webdriver.gecko.driver", "geckodriver");
            driver = SeleniumDAO.initializeFirefoxDriver();
            driver.get("http://pruebas7rc.dialcata.com/dialapplet-web/");
            // Login
            Main.loginDialappletWeb(ADMIN, PASSWORD, driver);
            // Click on Admin tab
            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", driver);
            SeleniumDAO.click(adminTab);
            // Click on "Users"
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//img[@alt = 'Create, edit and manage users']", driver);
            SeleniumDAO.click(users);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//*[text() = 'Create a user']", driver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", driver);
            username.sendKeys(NAME);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", driver);
            userPass.sendKeys(PASSWORD);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", driver);
            confirmUserPass.sendKeys(PASSWORD);
            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", driver);
            SeleniumDAO.click(agent);
            // Click on submit button
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", driver);
            SeleniumDAO.click(accept);
            WebElement checkAll = SeleniumDAO.selectElementBy("name", "checkall", driver);
            SeleniumDAO.click(checkAll);
            // Configure tabs (default configuration)
            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", driver);
            SeleniumDAO.click(send);
            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", driver);
            SeleniumDAO.click(submit);
            ;
            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", driver);
            SeleniumDAO.click(submitSecondPage);

            // Login as Agente2rcver833
            driver.get("https://pruebas7rc.dialcata.com/clienteweb/login.php");

            Thread.sleep(3000);
            Main.loginWebClient(NAME, PASSWORD, 2, driver);

            //Por defecto al conectarse el estado es preturno
            System.out.println("Descansando...");
            Thread.sleep(60000);

            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", driver);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", driver);

            //Ponemos el estado a disponible para dejar de estar en preturno
            WebElement availableState = SeleniumDAO.selectElementBy("id", "available", driver);
            SeleniumDAO.click(availableState);
            System.out.println("Disponible");

            SeleniumDAO.switchToDefaultContent(driver);
        } finally {
            driver.close();
        }
    }
}