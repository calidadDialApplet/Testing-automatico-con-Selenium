import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class Test_1_10 {
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
        //Pestaña Admin
        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();
        //Usuarios
        WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
        users.click();
        //ConfigurarUsuarios
        WebElement configureUsers = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a"));
        configureUsers.click();
        //Nuevo Usuario
        WebElement createUser = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a"));
        createUser.click();
        //Rellenar campos del primer formulario
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys("Agente5rcNver7816");

        //Modo seguro
        WebElement secureMode = driver.findElement(By.id("securemode"));
        secureMode.click();


        WebElement userPass = driver.findElement(By.id("passnew"));
        userPass.sendKeys("Contraseña1234");

        WebElement userPass2 = driver.findElement(By.id("pass2"));
        userPass2.sendKeys("Contraseña1234");

        WebElement agent = driver.findElement(By.id("isagent"));
        agent.click();

        WebElement accept = driver.findElement(By.id("submit"));
        accept.click();
        //Pestañas para agente creado
        WebElement send = driver.findElement(By.name("send_tabs"));
        send.click();
        //Grupos de agentes, añadir a AGENTE3RCNver7816
        WebElement group = driver.findElement(By.id("g68"));
        group.click();

        WebElement submit = driver.findElement(By.name("submit-page-one"));
        submit.click();
        //Otros servicios y modos de llamada
        WebElement submit2 = driver.findElement(By.name("submit-page-two"));
        submit2.click();

        //driver.close();
    }
}
