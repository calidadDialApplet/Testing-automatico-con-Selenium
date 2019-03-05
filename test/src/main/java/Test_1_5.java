import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

//Create a user agent 1 -> connect and be a minute at rest.
// Format of the user name Agente1rcNverXYZ, where N is the number of the RC and XYZ is the version


public class Test_1_5 {
    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", "/home/david/Descargas/test/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        //Login
        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();
        //Admin window
        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();
        //Users
        WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
        users.click();
        //Configure users
        WebElement configureUsers = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a"));
        configureUsers.click();
        //New User
        WebElement createUser = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a"));
        createUser.click();
        //Create new Agent
        String name ="Agente1Nver7816";
        String uniqueID = ""+Math.floor(1000 + Math.random() * 9999);
        uniqueID = uniqueID.substring(0,3);
        name = name.concat(uniqueID);
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys(name);

        WebElement userPass = driver.findElement(By.id("pswd"));
        userPass.sendKeys("contraseña1234");

        WebElement userPass2 = driver.findElement(By.id("pass2"));
        userPass2.sendKeys("contraseña1234");

        WebElement agent = driver.findElement(By.id("isagent"));
        agent.click();

        WebElement accept = driver.findElement(By.id("submit"));
        accept.click();

        WebElement send = driver.findElement(By.name("send_tabs"));
        send.click();

        WebElement submit = driver.findElement(By.name("submit-page-one"));
        submit.click();

        WebElement submit2 = driver.findElement(By.name("submit-page-two"));
        submit2.click();

        //Login as Agente1rcNver7816
        driver.get("https://pruebas7.dialcata.com/clienteweb/login.php");

        WebElement usernameWebClient = driver.findElement(By.id("userName"));
        usernameWebClient.sendKeys(name);

        WebElement passWebClient = driver.findElement(By.id("passwordBridge"));
        passWebClient.sendKeys("contraseña1234");

        Select tlf = new Select(driver.findElement(By.id("selectType")));
        tlf.selectByIndex(1);

        WebElement entryWebClient = driver.findElement(By.id("checklogin"));
        entryWebClient.click();

        WebElement enter = driver.findElement(By.id("login"));
        enter.click();
        //Wait to take a rest button
        WebDriverWait waiting = new WebDriverWait(driver, 20);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
        //Take a rest
        WebElement takeABreak = driver.findElement(By.className("headerButton"));
        takeABreak.click();

        //WebElement counter = driver.findElement(By.id("counter"));

        //driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        WebElement resumeButton = driver.findElement(By.xpath("/html/body/div[1]/table/tbody/tr[3]/td[2]/input"));
        resumeButton.click();
    }
}
