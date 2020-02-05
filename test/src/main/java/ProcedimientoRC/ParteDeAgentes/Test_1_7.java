package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

// Crea un usuario agente3 y no se conecta

public class Test_1_7 {

    static final String NAME = "agente3rcver833";
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
        } finally {
            driver.close();
        }
    }
}