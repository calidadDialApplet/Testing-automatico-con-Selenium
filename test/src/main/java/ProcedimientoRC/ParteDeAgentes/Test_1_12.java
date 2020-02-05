package ProcedimientoRC.ParteDeAgentes;

import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Test_1_12 {
    static final String PASSWORD = "A1234567890a";
    static final String ADMIN = "sebasAdmin";
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        try {
            System.setProperty("webdriver.gecko.driver", "geckodriver");

            driver = SeleniumDAO.initializeFirefoxDriver();
            driver.get("https://pruebas7rc.dialcata.com/dialapplet-web/");

            Main.loginDialappletWeb(ADMIN, PASSWORD, driver);
            // Click on Admin tab
            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", driver);
            SeleniumDAO.click(adminTab);
            // Click on "Users"
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//img[@alt = 'Create, edit and manage users']", driver);
            SeleniumDAO.click(users);
            // Import users by CSV
            WebElement importUsersCSV = driver.findElement(By.xpath("//a[contains(., 'Import users(CSV)')]"));
            importUsersCSV.click();

            WebElement browseButton = SeleniumDAO.selectElementBy("name", "userfile", driver);
            // Tener en cuenta la ruta del fichero y que el mismo fichero no contenga errores
            SeleniumDAO.writeInTo(browseButton, "/home/sebas/Documents/agentesTexto.csv");

            WebElement importCSVButton = SeleniumDAO.selectElementBy("id", "submitcsv", driver);
            SeleniumDAO.click(importCSVButton);
        }finally {
            driver.close();
        }
    }
}
