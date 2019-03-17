import com.sun.org.apache.xpath.internal.operations.Mod;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.*;


// Create a group called GruporcNverXYZ and add the agents that have been created before

public class Test_1_2 {
    public static void main(String[] args) {

        // Go to http://pruebas7.dialcata.com/dialapplet-web/
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();
        // Click on Admin tab
        WebElement adminButton = driver.findElement(By.id("ADMIN"));
        adminButton.click();
        // Click on "Users" left menu
        WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
        users.click();
        // Click on Modify agent groups button in left menu
        WebElement modAgentsGroups = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[2]/p/a"));
        modAgentsGroups.click();

        WebDriverWait waiting = new WebDriverWait(driver, 20);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
        WebElement containerTable = driver.findElement(By.id("contenido"));
        WebElement table = containerTable.findElement(By.className("tabla-principal"));
        List<WebElement> listOfRows = table.findElements(By.tagName("tr"));
        int rows = listOfRows.size();
        int[] ids = new int[rows];
        for(int i = 1; i<rows-1; i++){
            WebElement currentId = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[3]/div/table/tbody/tr["+i+"]/td[1]"));
            ids[i] = Integer.parseInt(currentId.getText());
        }
        int currentMax = Arrays.stream(ids).max().getAsInt();

        String uniqueID = ""+Math.random();
        String name = "GruporcNver7816";
        name = name.concat(uniqueID);
        WebElement groupName = driver.findElement(By.id("new_groupname"));
        groupName.sendKeys(name);
        // Click on add button
        WebElement newGroup = driver.findElement(By.cssSelector("img[src='imagenes/add2.png']"));
        newGroup.click();
        // Taking ID of new Group
        driver.switchTo().defaultContent();
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
        WebElement containerTable2 = driver.findElement(By.id("contenido"));
        WebElement table2 = containerTable2.findElement(By.className("tabla-principal"));
        List<WebElement> listOfRows2 = table2.findElements(By.tagName("tr"));
        int rows2 = listOfRows2.size();
        int[] ids2 = new int[rows2];
        for(int i = 1; i<rows2-1; i++){
            WebElement currentId = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[3]/div/table/tbody/tr["+i+"]/td[1]"));
            ids2[i] = Integer.parseInt(currentId.getText());
        }
        int newMax = Arrays.stream(ids2).max().getAsInt();
        // Transfer users to new group
        WebElement gestUsers = driver.findElement(By.xpath("//*[@id=\"users-"+newMax+"\"]"));
        gestUsers.click();

        // Waiting to load fancy-box
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        // Change driver to fancy-box
        driver.switchTo().frame("fancybox-frame");
        // Take left column(users column)
        WebElement leftColumn = driver.findElement(By.xpath("//*[@id=\"leftCol\"]"));
        // Select a Agent
        WebElement agent = leftColumn.findElement(By.xpath("/html/body/form/div/div[1]/div/div[1]/div/ul/li[4]"));
        // Take right column(group users)
        WebElement groupSpace = driver.findElement(By.xpath("//*[@id=\"rightList\"]"));
        // Make the drag and drop action
        Actions moveAgent = new Actions(driver);
        moveAgent.dragAndDrop(agent,groupSpace).build().perform();
        // Click on send button to finish the modification
        WebElement send = driver.findElement(By.xpath("/html/body/form/div/p[3]/input"));
        send.click();

        // Checking that the test works correctly
        // Take all ids of the agent groups of tabla principal and find the max value,
        // this value is the id of our new AgentGroup

        if(currentMax < newMax) System.out.println("Prueba creación grupo agentes finalizada con éxito !\nGenerado grupo con ID: "+newMax);
        else System.out.println("Algo ha petado, repasar");

        //driver.close();
    }
}
