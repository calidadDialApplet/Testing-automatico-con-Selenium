import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Test_1_8 {

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

        WebElement modAgentsGroups = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[2]/p/a"));
        modAgentsGroups.click();

        WebElement groupName = driver.findElement(By.id("new_groupname"));
        groupName.sendKeys("AGENTE3RCNver7816");

        WebElement newGroup = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[3]/div/table/tbody/tr[13]/td[1]/img"));
        newGroup.click();

        WebElement gestUsers = driver.findElement(By.xpath("//*[@id=\"users-18\"]"));
        gestUsers.click();

        WebDriverWait waiting = new WebDriverWait(driver, 20);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        driver.switchTo().frame("fancybox-frame");

        WebElement leftColumn = driver.findElement(By.xpath("//*[@id=\"leftCol\"]"));
        WebElement agent = leftColumn.findElement(By.xpath("/html/body/form/div/div[3]/div/div[1]/div/ul/li[4]"));
        WebElement groupSpace = driver.findElement(By.xpath("//*[@id=\"rightList\"]"));

        Actions moveAgent = new Actions(driver);
        moveAgent.dragAndDrop(agent,groupSpace).build().perform();

        WebElement send = driver.findElement(By.xpath("/html/body/form/div/p[3]/input"));
        send.click();

        //driver.close();
    }
}
