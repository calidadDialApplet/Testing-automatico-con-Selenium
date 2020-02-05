package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import main.Utils;
import okio.Timeout;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;

//Second Test
/*Precondicion: Debe de estar creado 2 agentes en mi caso agentePilotoSebas1 y agentePilotoSebas2 */
public class AddAgentsToNewGroup {
    static final String GROUPNAME = "GrupoSebas";
    static final String ADMIN = "sebasAdmin";
    static final String PASSWORD = "A1234567890a";
    static WebDriver driver;

    public static void main(String[] args) {

        try{
            // Go to http://pruebas7.dialcata.com/dialapplet-web/
            driver = SeleniumDAO.initializeFirefoxDriver();
            driver.get("https://pruebas7rc.dialcata.com/dialapplet-web/");

            Main.loginDialappletWeb(ADMIN, PASSWORD, driver);
            // Click on Admin tab
            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", driver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//*[text() = 'Users']", driver);
            SeleniumDAO.click(users);
            // Click on Modify agent groups button in left menu
            WebElement modAgentsGroups = SeleniumDAO.selectElementBy("xpath", "//*[text() = 'Modify agent groups']", driver);
            SeleniumDAO.click(modAgentsGroups);


            WebDriverWait waiting = new WebDriverWait(driver, 20);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
            WebElement containerTableSearch = SeleniumDAO.selectElementBy("id", "contenido", driver);
            WebElement tableSearch = SeleniumDAO.selectElementBy("className", "tabla-principal", containerTableSearch);

            List<WebElement> listOfRows = tableSearch.findElements(By.tagName("tr"));
            int numberOfRowsBefore = listOfRows.size();
            int[] ids = new int[numberOfRowsBefore];
            for (int i = 1; i < numberOfRowsBefore - 1; i++) {
                WebElement currentId = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[3]/div[2]/div[4]/div/table/tbody/tr[" + i + "]/td[1]", driver);
                ids[i] = Integer.parseInt(currentId.getText());
            }

            WebElement groupName = SeleniumDAO.selectElementBy("id", "new_groupname", driver);
            groupName.sendKeys(GROUPNAME);
            // Click on add button
            WebElement newGroup = SeleniumDAO.selectElementBy("cssSelector", "img[src='imagenes/add2.png']", driver);
            SeleniumDAO.click(newGroup);
            // Taking ID of new Group
            SeleniumDAO.switchToDefaultContent(driver);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));

            containerTableSearch = SeleniumDAO.selectElementBy("id", "contenido", driver);
            tableSearch = SeleniumDAO.selectElementBy("className", "tabla-principal", containerTableSearch);
            listOfRows = tableSearch.findElements(By.tagName("tr"));
            int numberOfRowsAfter = listOfRows.size();
            int[] ids2 = new int[numberOfRowsAfter];
            for (int i = 1; i < numberOfRowsAfter - 1; i++) {
                WebElement currentId = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div/table/tbody/tr[" + i + "]/td[1]"));
                ids2[i] = Integer.parseInt(currentId.getText());
            }
            int newIDGroup = Arrays.stream(ids2).max().getAsInt();
            // Transfer users to new group
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@id='users-" + newIDGroup + "']")));
            WebElement manageUsers = SeleniumDAO.selectElementBy("xpath", "//img[@id='users-" + newIDGroup + "']", driver);
            SeleniumDAO.click(manageUsers);
            // Waiting to load fancy-box and change driver to fancy-box
            SeleniumDAO.switchToFrame("fancybox-frame", driver);
            // Take left column(users column)
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("leftList")));
            WebElement leftColumn = SeleniumDAO.selectElementBy("id", "leftList", driver);
            // Select a Agent
            WebElement agent = SeleniumDAO.selectElementBy("xpath", "//*[text() = 'agentePilotoSebas1']", leftColumn);
            // Take right column(group users)
            WebElement groupSpace = SeleniumDAO.selectElementBy("xpath", "//*[@id=\"rightList\"]", driver);
            // Select a Agent
            WebElement agent2 = SeleniumDAO.selectElementBy("xpath", "//*[text() = 'agentePilotoSebas2']", leftColumn);
            // Take right column(group users)
            WebElement groupSpace2 = SeleniumDAO.selectElementBy("xpath", "//*[@id=\"rightList\"]", driver);

            SeleniumDAO.dragAndDropAction(agent, groupSpace, driver);
            System.out.println("Mesperooo");
            //driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Ya !");
            SeleniumDAO.dragAndDropAction(agent2, groupSpace2, driver);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Click on send button to finish the modification
            git

            // Checking that the test works correctly
            if (numberOfRowsBefore < numberOfRowsAfter)
                System.out.println("Prueba creación grupo agentes finalizada con éxito !");
            else System.out.println("Algo ha petado, repasar");

    } finally{driver.close();}

    }
}
