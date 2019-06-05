import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;

// Create a group of agents called AGENT1Y2rcNverXYZ, where N is the number of the RC and XYZ is the version.

public class Test_1_3 {
    public static void main(String[] args) {

        WebDriver driver = SeleniumDAO.initializeFirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        Main.loginDialappletWeb("admin", "admin", driver);
        // Click on Admin tab
        WebElement adminTab = SeleniumDAO.selectElementBy("id","ADMIN", driver);
        SeleniumDAO.click(adminTab);
        // Click on "Users" left menu
        WebElement users = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[1]/h3[2]",driver);
        SeleniumDAO.click(users);
        // Click on Modify agent groups button in left menu
        WebElement modAgentsGroups = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[1]/div[2]/div/div[2]/p/a", driver);
        SeleniumDAO.click(modAgentsGroups);

        WebDriverWait waiting = new WebDriverWait(driver, 20);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
        WebElement containerTableFirstSearch = SeleniumDAO.selectElementBy("id", "contenido",driver);
        WebElement tableFirstSearch = SeleniumDAO.selectElementBy("className", "tabla-principal", containerTableFirstSearch);

        WebElement lastElement = SeleniumDAO.selectElementBy("cssSelector", "tbody :nth-last-child(3) ~ tr :first-child",tableFirstSearch);

        //tbody :nth-last-child(3) ~ tr :first-child
        System.out.println(lastElement.getTagName());
        System.out.println(lastElement.getText());


        List<WebElement> listOfRows = tableFirstSearch.findElements(By.tagName("tr"));
        int rows = listOfRows.size();
        int[] ids = new int[rows];
        for(int i = 1; i<rows-1; i++){
            WebElement currentId = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[3]/div[2]/div[3]/div/table/tbody/tr["+i+"]/td[1]", driver);
            ids[i] = Integer.parseInt(currentId.getText());
        }

        int currentMax = 0;

        if(Arrays.stream(ids).max().isPresent())
        {
            currentMax = Arrays.stream(ids).max().getAsInt();
        }

        else System.out.println("Algo ha ido MAL !");

        String name = "AGENTE1Y2rcNver7816";
        name = Utils.generateUniqueID(name);

        //name = name.concat(uniqueID);

        WebElement groupName = SeleniumDAO.selectElementBy("id","new_groupname",driver);
        groupName.sendKeys(name);
        //Click on add button
        WebElement newGroup = SeleniumDAO.selectElementBy("cssSelector", "img[src='imagenes/add2.png']",driver);
        SeleniumDAO.click(newGroup);
        //Taking ID of new Group
        SeleniumDAO.switchToDefaultContent(driver);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));

        WebElement containerTableSecondSearch = SeleniumDAO.selectElementBy("id", "contenido",driver);
        WebElement tableSecondSearch = SeleniumDAO.selectElementBy("className","tabla-principal", containerTableSecondSearch);
        List<WebElement> listOfRows2 = tableSecondSearch.findElements(By.tagName("tr"));
        int rows2 = listOfRows2.size();
        int[] ids2 = new int[rows2];
        for(int i = 1; i<rows2-1; i++){
            WebElement currentId = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[3]/div[2]/div[3]/div/table/tbody/tr["+i+"]/td[1]", driver);
            //ids2[i] = Integer.parseInt(currentId.getText());
            ids2[i] = Integer.parseInt(SeleniumDAO.readFrom(currentId));
        }

        int newMax = 0;

        if(Arrays.stream(ids2).max().isPresent())
        {
            newMax = Arrays.stream(ids2).max().getAsInt();
        }

        else System.out.println("Algo ha ido MAL !");


        if(currentMax < newMax) System.out.println("Prueba creación grupo agentes finalizada con éxito !\nGenerado grupo con ID: "+newMax);
        else System.out.println("Algo ha petado, repasar");


        /*
        //Transfer users to new group
        WebElement gestUsers = driver.findElement(By.xpath("//*[@id=\"users-"+newMax+"\"]"));
        gestUsers.click();


        //Waiting to load fancy-box
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        //Change driver to fancy-box
        driver.switchTo().frame("fancybox-frame");
        //Take left column(users column)
        WebElement leftColumn = driver.findElement(By.xpath("//*[@id=\"leftCol\"]"));
        //Select a Agent
        WebElement agent = leftColumn.findElement(By.xpath("/html/body/form/div/div[1]/div/div[1]/div/ul/li[4]"));
        //Take right column(group users)
        WebElement groupSpace = driver.findElement(By.xpath("//*[@id=\"rightList\"]"));
        //Make the drag and drop action
        Actions moveAgent = new Actions(driver);
        moveAgent.dragAndDrop(agent,groupSpace).build().perform();
        //CLick on send button to finish the modification
        WebElement send = driver.findElement(By.xpath("/html/body/form/div/p[3]/input"));
        send.click();
        */


        //Checking that the test works correctly
        //Take all ids of the agent groups of tabla principal and find the max value,
        // this value is the id of our new AgentGroup


        //driver.close();
    }
}
