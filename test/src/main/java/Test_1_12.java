import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Test_1_12 {
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
        //Pestaña Admin
        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();
        //Usuarios
        WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
        users.click();
        //ConfigurarUsuarios
        WebElement configureUsers = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a"));
        configureUsers.click();
        //Importar usuarios CSV
        WebElement importUsersCSV = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[3]/a/img"));
        importUsersCSV.click();
        //Boton Examinar
        //Tener en cuenta la ruta del fichero y que el mismo fichero no contenga errores
        WebElement examine = driver.findElement(By.name("userfile"));
        examine.sendKeys("/home/david/Escritorio/Importación Usuarios.csv");

        WebElement submitCSV = driver.findElement(By.id("submitcsv"));
        submitCSV.click();

    }
}
