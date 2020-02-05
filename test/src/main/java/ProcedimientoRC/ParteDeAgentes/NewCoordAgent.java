package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


// Create a user coordinator + agent. Format of the username CoordrcNverXYZ,
// where N is the number of the RC and XYZ is the version.
// Assign user to the telemarketing service we created before

//Fourth test
//Precondicion: Debe de haber un servicio creado, en mi caso telemarketingSebas
public class NewCoordAgent {
    static final String NAME = "Coordrcver833";
    static final String PASSWORD = "A1234567890a";
    static final String ADMIN = "sebasAdmin";
    static final String SERVICIO = "(289) telemarketingSebas";
    static WebDriver driver;

    public static void main(String[] args) {

            try {
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
                    // Set coordinator role
                    WebElement coordinator = SeleniumDAO.selectElementBy("id", "iscoordinator", driver);
                    SeleniumDAO.click(coordinator);
                    // Set agent role
                    WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", driver);
                    SeleniumDAO.click(agent);
                    // Click on submit button
                    WebElement accept = SeleniumDAO.selectElementBy("id", "submit", driver);
                    SeleniumDAO.click(accept);
                    // Configure tabs (default configuration)
                    WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", driver);
                    SeleniumDAO.click(send);
                    // Configure agent groups(default configuration)
                    WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", driver);
                    SeleniumDAO.click(submit);
                    // Configure agent services(DialappletDemoDavid service)
                    WebElement service = SeleniumDAO.selectElementBy("xpath", "//label[contains(., '" + SERVICIO + "')]", driver);
                    SeleniumDAO.click(service);
                    WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", driver);
                    SeleniumDAO.click(submitSecondPage);
                    // Configure agent groups as coordinator(default configuration)
                    WebElement submitThirdPage = SeleniumDAO.selectElementBy("name", "submit", driver);
                    SeleniumDAO.click(submitThirdPage);


                    driver.navigate().to("http://pruebas7rc.dialcata.com/dialapplet-web/edit-user.php?username=" + NAME + "&");
                    WebElement agentTest = SeleniumDAO.selectElementBy("id", "isagent", driver);
                    boolean firstCondition = agentTest.isSelected();
                    WebElement coordinatorTest = SeleniumDAO.selectElementBy("id", "iscoordinator", driver);
                    boolean secondCondition = coordinatorTest.isSelected();
                    if (firstCondition && secondCondition)
                            System.out.println("Prueba creación usuario agente y coordinador finalizada con éxito!");
                    else System.out.println("Algo ha petado, repasar");

            } finally {
                    driver.close();
            }
    }



}


