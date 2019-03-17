import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

// Parece lo mismo del test 10 ...

public class Test_1_11 {

    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        //Login
        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();
        //Admin tab
        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();
        //Users
        WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
        users.click();
        //Configure Users
        WebElement configureUsers = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a"));
        configureUsers.click();
        //New User
        WebElement createUser = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a"));
        createUser.click();
        //Fill form fields
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys("Agente6rcNver7816");

        WebElement userPass = driver.findElement(By.id("pswd"));
        userPass.sendKeys("contraseña1234");

        WebElement userPass2 = driver.findElement(By.id("pass2"));
        userPass2.sendKeys("contraseña1234");

        WebElement agent = driver.findElement(By.id("isagent"));
        agent.click();

        WebElement coordinator = driver.findElement(By.id("iscoordinator"));
        coordinator.click();

        WebElement accept = driver.findElement(By.id("submit"));
        accept.click();
        //Tabs for new agent
        WebElement send = driver.findElement(By.name("send_tabs"));
        send.click();
        //Agent group, add AGENTE3RCNver7816
        WebElement group = driver.findElement(By.id("g68"));
        group.click();

        WebElement submit = driver.findElement(By.name("submit-page-one"));
        submit.click();
        //Otros servicios y modos de llamada
        WebElement submit2 = driver.findElement(By.id("submit"));
        submit2.click();

        //Groups coordinator
        WebElement newSubmit = driver.findElement(By.id("submit"));
        newSubmit.click();

        //driver.close();
    }
}
