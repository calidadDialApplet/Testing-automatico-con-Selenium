package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Crea un usuario agente1, se conecta, está un minuto en descanso y se pone en disponible

public class Test_1_5 {
    static final String NAME = "agente1rcver833";
    static final String PASSWORD = "A1234567890a";
    static final String ADMIN = "sebasAdmin";
    static WebDriver driver;
    public static void main(String[] args) throws InterruptedException {

        try {
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
            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", driver);
            SeleniumDAO.click(submitSecondPage);

            // Login as Agente1rcNver7816
            driver.get("https://pruebas7rc.dialcata.com/clienteweb/login.php");

            Main.loginWebClient(NAME, PASSWORD, 2, driver);
            // Wait to take a rest button
            WebDriverWait waiting = new WebDriverWait(driver, 20);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));

            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", driver);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", driver);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("rest")));

            WebElement restState = SeleniumDAO.selectElementBy("id", "rest", driver);
            SeleniumDAO.click(restState);

            SeleniumDAO.switchToDefaultContent(driver);

            System.out.println("Descansando...");
            Thread.sleep(60000);
            System.out.println("Disponible");

            states = SeleniumDAO.selectElementBy("id", "agent-name", driver);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", driver);

            WebElement availableState = SeleniumDAO.selectElementBy("id", "available", driver);
            SeleniumDAO.click(availableState);

            SeleniumDAO.switchToDefaultContent(driver);

        } finally {
            driver.close();
        }
    }
}
