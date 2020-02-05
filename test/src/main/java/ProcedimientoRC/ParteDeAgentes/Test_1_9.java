package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//Crea un usuario Agente4rcver833 y lo añade al grupo AGENTE3RCver833
//Precondicion: Debe estar creado el grupo AGENTE3RCver833
//El atributo id del checkbox de AGENTE3RCver833 cambiará cuando se cree de nuevo, debe comprobarse
public class Test_1_9 {
    static final String NAME = "Agente4rcver833";
    static final String PASSWORD = "A1234567890a";
    static final String ADMIN = "sebasAdmin";
    static final String GRUPO_AGENTES = "AGENTE3RCver833";
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {

        try {
            System.setProperty("webdriver.gecko.driver", "geckodriver");

            driver = SeleniumDAO.initializeFirefoxDriver();
            driver.get("https://pruebas7rc.dialcata.com/dialapplet-web/");

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

            WebElement sendButton = SeleniumDAO.selectElementBy("name", "send_tabs", driver);
            SeleniumDAO.click(sendButton);

            WebDriverWait waiting = new WebDriverWait(driver, 10);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("groups")));

            WebElement grupo3Checkbox = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + GRUPO_AGENTES + "')]", driver);
            SeleniumDAO.click(grupo3Checkbox);

            WebElement confirmButton = SeleniumDAO.selectElementBy("name", "submit-page-one", driver);
            SeleniumDAO.click(confirmButton);

            WebElement confirmButton2 = SeleniumDAO.selectElementBy("name", "submit-page-two", driver);
            SeleniumDAO.click(confirmButton2);

        } finally {
            driver.close();
        }
    }
}
