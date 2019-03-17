import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

// Ni un comentario ... ?

public class Test_1_7 {

    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();

        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();

        WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
        users.click();

        WebElement configureUsers = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a"));
        configureUsers.click();

        WebElement createUser = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a"));
        createUser.click();

        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys("Agente3rcNver7816");

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

        WebElement logout = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/p/a/img"));
        logout.click();

        //driver.close();
    }
}
