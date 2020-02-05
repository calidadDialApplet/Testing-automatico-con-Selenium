package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

//Crear grupo usuarios AGENTE3RCver833
public class Test_1_8 {
    static final String PASSWORD = "A1234567890a";
    static final String ADMIN = "sebasAdmin";
    static final String GROUPNAME = "AGENTE3RCver833";
    static WebDriver driver;

    public static void main(String[] args) {

        try {
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

            if (numberOfRowsBefore < numberOfRowsAfter)
                System.out.println("Prueba creación grupo agentes finalizada con éxito !");
            else System.out.println("Algo ha petado, repasar");
        } finally {
            driver.close();
        }
    }
}
