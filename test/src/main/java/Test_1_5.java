import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Create a user agent 1 -> connect and be a minute at rest.
// Format of the user name Agente1rcNverXYZ, where N is the number of the RC and XYZ is the version

public class Test_1_5 {

    public static void main(String[] args) {

        WebDriver driver = SeleniumDAO.initializeFirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        // Login
        Main.loginDialappletWeb("admin","admin",driver);
        // Admin window
        WebElement adminButton = driver.findElement(By.id("ADMIN"));

        adminButton.click();
        // Click on "Users" left menu
        WebElement users = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[1]/h3[2]",driver);
        SeleniumDAO.click(users);
        // Configure Users
        WebElement configureUsers = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a", driver);
        SeleniumDAO.click(configureUsers);
        // Create Users
        WebElement createUser = SeleniumDAO.selectElementBy("xpath","/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a", driver);
        SeleniumDAO.click(createUser);
        // Create new Agent
        String name ="Agente1Nver7816";
        Utils.generateUniqueID(name);
        //uniqueID = uniqueID.substring(0,3);
        //name = name.concat(uniqueID);
        WebElement username = SeleniumDAO.selectElementBy("id", "username", driver);
        username.sendKeys(name);

        WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", driver);
        userPass.sendKeys("contraseña1234");

        WebElement confirmUserPass = SeleniumDAO.selectElementBy("id","pass2", driver);
        confirmUserPass.sendKeys("contraseña1234");
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

        WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two",driver);
        SeleniumDAO.click(submitSecondPage);

        // Login as Agente1rcNver7816
        driver.get("https://pruebas7.dialcata.com/clienteweb/login.php");

        Main.loginWebClient(name,"contraseña1234",3,driver);
        // Wait to take a rest button
        WebDriverWait waiting = new WebDriverWait(driver, 20);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
        // Take a rest

        WebElement takeABreak = SeleniumDAO.selectElementBy("className", "headerButton", driver);
        SeleniumDAO.click(takeABreak);

        //WebElement counter = driver.findElement(By.id("counter"));

        //driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        WebElement resumeButton = SeleniumDAO.selectElementBy("xpath", "/html/body/div[1]/table/tbody/tr[3]/td[2]/input", driver);
        SeleniumDAO.click(resumeButton);
    }
}
