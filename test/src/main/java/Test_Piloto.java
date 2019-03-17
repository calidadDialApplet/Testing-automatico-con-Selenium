import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Creates test service: Seleniumtelemarketing

public class Test_Piloto{

    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        //driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();

        WebElement operationButton = driver.findElement(By.id("OPERATION"));
        operationButton.click();

        WebElement newService = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[1]/p[2]/a"));
        newService.click();

        WebElement name = driver.findElement(By.id("name"));
        name.sendKeys("Seleniumtelemarketing");

        WebDriverWait waiting = new WebDriverWait(driver, 20);
        waiting.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-id-122")));
        // Add users to service
        WebElement user1 = driver.findElement(By.id("user-id-122"));
        user1.click();

        //WebElement user2 = driver.findElement(By.id("user-id-121"));
        //user2.click();

        WebElement send = driver.findElement(By.id("send"));
        send.click();

        WebElement next = driver.findElement(By.id("next"));
        next.click();
        // Coordinators tab, drag and drop not implemented
        WebElement next2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[2]/div[3]/form/p[2]/input[2]"));
        next2.click();
        // Contact data tab, drag and drop not implemented
        WebElement next3 = driver.findElement(By.id("submit-btn"));
        next3.click();
        // Web client tab
        WebElement submit = driver.findElement(By.id("submit"));
        submit.click();
        // Confirm service tab
        WebElement confirm = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[2]/div[3]/form/p/input[1]"));
        confirm.click();
        // Import contacts tab
        WebElement next4 = driver.findElement(By.xpath("//*[@id=\"next\"]"));
        next4.click();
    }
}
