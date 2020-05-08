package ProcedimientoRC.ParteDeSocialmedia;

import Utils.CleanTest;
import Utils.DriversConfig;
import Utils.Utils;
import exceptions.MissingParameterException;
import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CleanParteDeSocialmedia extends CleanTest {

    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String superAdminName;
    static String superAdminPassword;
    static String serviceSMName;
    static String serviceSMCopyName;
    static String showflowSMName;
    static String showflowSMCopyName;



    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    public CleanParteDeSocialmedia(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap requiredParameters = new HashMap();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("superAdminName", "superAdminPassword", "adminName", "adminPassword")));
        requiredParameters.put("ServiceSM", new ArrayList<>(Arrays.asList("serviceSMName", "serviceSMCopyName")));
        requiredParameters.put("ShowflowSM", new ArrayList<>(Arrays.asList("showflowSMName", "showflowSMCopyName")));

        return requiredParameters;
    }

    @Override
    public void clean() throws MissingParameterException {
        url = commonIni.get("General", "url");
        headless = commonIni.get("General", "headless");
        adminName = commonIni.get("Admin", "adminName");
        adminPassword = commonIni.get("Admin", "adminPassword");
        superAdminName = commonIni.get("Admin", "superAdminName");
        superAdminPassword = commonIni.get("Admin", "superAdminPassword");
        serviceSMName = commonIni.get("ServiceSM","serviceSMName");
        serviceSMCopyName = commonIni.get("ServiceSM", "serviceSMCopyName");
        showflowSMName = commonIni.get("ShowflowSM", "showflowSMName");
        showflowSMCopyName = commonIni.get("ShowflowSM", "showflowSMCopyName");


        try
        {
            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);

            cleanContactCenter();

            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(superAdminName, superAdminPassword, firefoxDriver);

            deleteServices();

            deleteShowflows();

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            firefoxDriver.close();
        }
    }

    public void cleanContactCenter()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id = 'ADMIN']")));
            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(adminTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);

            deleteMailAccounts();

            deleteMailSignatures();

            deleteMailTemplates();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void deleteServices()
    {
        try
        {
            //Deletes the cloned service
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
            searcher.clear();
            searcher.sendKeys(serviceSMCopyName);
            Thread.sleep(1000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceSMCopyName + "')]")));

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceSMCopyName + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'delete-service']/a")));
            WebElement deleteServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'delete-service']/a", firefoxDriver);
            SeleniumDAO.click(deleteServiceTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            Thread.sleep(3000);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Could not delete the cloned service");
        }


        try
        {
            //Deletes the service
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
            searcher.clear();
            searcher.sendKeys(serviceSMName);
            Thread.sleep(1000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceSMName + "')]")));

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceSMName + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'delete-service']/a")));
            WebElement deleteServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'delete-service']/a", firefoxDriver);
            SeleniumDAO.click(deleteServiceTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            Thread.sleep(3000);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Could not delete the created service");
        }
    }

    public void deleteShowflows()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("SHOWFLOW")));
            WebElement showflowTab = SeleniumDAO.selectElementBy("id", "SHOWFLOW", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(showflowTab);

            //Deletes the showflow created when the service is cloned
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
            searcher.clear();
            searcher.sendKeys(showflowSMCopyName + "forService");
            Thread.sleep(1000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowSMCopyName + "forService" + "')]" +
                    "/following-sibling::td/a[@class = 'delete']")));
            Thread.sleep(3000);
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowSMCopyName + "forService" + "')]" +
                    "/following-sibling::td/a[@class = 'delete']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(deleteButton));
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement yesDeleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(yesDeleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);
        } catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Could not delete the showflow created when the service is cloned");
        }

        try
        {
            //Deletes the cloned showflow
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
            searcher.clear();
            searcher.sendKeys(showflowSMCopyName);
            Thread.sleep(1000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowSMCopyName + "')]" +
                    "/following-sibling::td/a[@class = 'delete']")));
            Thread.sleep(3000);
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowSMCopyName + "')]" +
                    "/following-sibling::td/a[@class = 'delete']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(deleteButton));
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement yesDeleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(yesDeleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);
        } catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Could not delete the cloned showflow");
        }

        try
        {
            //Deletes the created showflow
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
            searcher.clear();
            searcher.sendKeys(showflowSMName);
            Thread.sleep(1000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowSMName + "')]" +
                    "/following-sibling::td/a[@class = 'delete']")));
            Thread.sleep(3000);
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowSMName + "')]" +
                    "/following-sibling::td/a[@class = 'delete']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(deleteButton));
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement yesDeleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(yesDeleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);


        } catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Could not delete the created showflow");
        }
    }

    void deleteMailAccounts() throws InterruptedException {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'MailAccountFBoxTicket.php']")));
            WebElement mailAccounts = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'MailAccountFBoxTicket.php']", firefoxDriver);
            SeleniumDAO.click(mailAccounts);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/delete2.png']")));
            WebElement deleteMailAccount = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/delete2.png']", firefoxDriver);
            SeleniumDAO.click(deleteMailAccount);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/delete2.png']")));
            deleteMailAccount = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/delete2.png']", firefoxDriver);
            SeleniumDAO.click(deleteMailAccount);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);
        } catch (Exception e)
        {
            System.out.println("Could not delete mail accounts");
            e.printStackTrace();
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);
        }

    }

    void deleteMailSignatures() throws InterruptedException {

        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'mailSignatures.php']")));
            WebElement mailSignatures = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'mailSignatures.php']", firefoxDriver);
            SeleniumDAO.click(mailSignatures);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@onclick, 'deleteSignature')]")));
            WebElement deleteSignature = SeleniumDAO.selectElementBy("xpath", "//button[contains(@onclick, 'deleteSignature')]", firefoxDriver);
            SeleniumDAO.click(deleteSignature);

            Thread.sleep(1500);
            Actions actions = new Actions(firefoxDriver);
            actions.sendKeys(Keys.ESCAPE).perform();

            Thread.sleep(2000);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);
        } catch (Exception e)
        {
            System.out.println("Could not delete mail signatures");
            e.printStackTrace();
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);
        }


    }

    void deleteMailTemplates() throws InterruptedException {

        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'MailTemplates.php']")));
            WebElement mailTemplates = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'MailTemplates.php']", firefoxDriver);
            SeleniumDAO.click(mailTemplates);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@onclick, 'deleteTemplate')]")));
            WebElement deleteTemplate = SeleniumDAO.selectElementBy("xpath", "//button[contains(@onclick, 'deleteTemplate')]", firefoxDriver);
            SeleniumDAO.click(deleteTemplate);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'confirm']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);
        } catch (Exception e)
        {
            System.out.println("Could not delete mail templates");
            e.printStackTrace();
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'admin-contactCenter.php']")));
            WebElement contactCenter = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'admin-contactCenter.php']", firefoxDriver);
            SeleniumDAO.click(contactCenter);
        }


    }

}
