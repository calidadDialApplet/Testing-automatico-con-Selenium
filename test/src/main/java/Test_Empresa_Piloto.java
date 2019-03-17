import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

// Create a new Company with time zone: Madrid

public class Test_Empresa_Piloto {

    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        // Login
        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();
        // Admin window
        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();
        // Companies button
        // Be careful with last td value, it defines the position of the companies button
        WebElement companies = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[3]/div[2]/table/tbody/tr[2]/td[2]/a"));
        companies.click();
        // New company
        WebElement newCompany = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a"));
        newCompany.click();
        // Company name
        WebElement companyName = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[3]/div[2]/form/p[1]/input"));
        companyName.sendKeys("CompanyrcNver7816");
        // Time zone by default is Madrid
        // Save Company
        WebElement save = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[3]/div[2]/form/p[29]/input[2]"));
        save.click();
        // Close the browser
        // driver.close();
    }
}
