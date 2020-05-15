package ProcedimientoRC.ParteDeAgentes;

import Utils.CleanTest;
import Utils.DriversConfig;
import Utils.Utils;
import exceptions.MissingParameterException;
import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CleanParteDeAgentes extends CleanTest {
    static String adminName;
    static String adminPassword;
    static String url;
    static String groupName;
    static String groupName1y2;
    static String groupName3;
    static String agentName1;
    static String agentName2;
    static String agentName3;
    static String agentName4;
    static String agentName5;
    static String agentName7;
    static String agentPassword;
    static String pilotAgentName1;
    static String pilotAgentName2;
    static String agentCoordName1;
    static String agentCoordName6;
    static String coordinatorPassword;
    static String headless;
    static String csvPath;
    static String serviceID;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    public CleanParteDeAgentes(Wini commonIni) {super(commonIni);}

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Group", new ArrayList<>(Arrays.asList("groupName", "groupName1y2", "groupName3")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName1", "agentName2", "agentName3", "agentName4", "agentName5", "agentName7",
                "agentPassword", "pilotAgentName1", "pilotAgentName2")));
        requiredParameters.put("Coordinator", new ArrayList<>(Arrays.asList("agentCoordName1", "agentCoordName6", "coordinatorPassword")));
        requiredParameters.put("CSV", new ArrayList<>(Arrays.asList("agentsCsvPath")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));

        return requiredParameters;
    }

    @Override
    public void clean() throws MissingParameterException {
        super.checkParameters();

        try
        {
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");

            groupName = commonIni.get("Group", "groupName");
            groupName1y2 = commonIni.get("Group", "groupName1y2");
            groupName3 = commonIni.get("Group", "groupName3");

            agentName1 = commonIni.get("Agent", "agentName1");
            agentName2 = commonIni.get("Agent", "agentName2");
            agentName3 = commonIni.get("Agent", "agentName3");
            agentName4 = commonIni.get("Agent", "agentName4");
            agentName5 = commonIni.get("Agent", "agentName5");
            agentName7 = commonIni.get("Agent", "agentName7");
            agentPassword = commonIni.get("Agent", "agentPassword");
            pilotAgentName1 = commonIni.get("Agent", "pilotAgentName1");
            pilotAgentName2 = commonIni.get("Agent", "pilotAgentName2");

            agentCoordName1 = commonIni.get("Coordinator", "agentCoordName1");
            agentCoordName6 = commonIni.get("Coordinator", "agentCoordName6");
            coordinatorPassword = commonIni.get("Coordinator", "coordinatorPassword");

            csvPath = commonIni.get("CSV", "csvPath");
            serviceID = commonIni.get("Service", "serviceID");

            firefoxDriver = DriversConfig.headlessOrNot("false");
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);

            List<String> agentsToClean = new ArrayList<String>(){{
                add(agentName1);
                add(agentName2);
                add(agentName3);
                add(agentName4);
                add(agentName5);
                add(agentName7);
                add(pilotAgentName1);
                add(pilotAgentName2);
                add(agentCoordName1);
                add(agentCoordName6);
            }};

            List<String> groupsToClean = new ArrayList<String>(){{
                add(groupName);
                add(groupName1y2);
                add(groupName3);
            }};

            firefoxDriver.get(url + "/dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);

            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", firefoxDriver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'adminTable']//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(users);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
            for(int i = 0; i < agentsToClean.size(); i++)
            {
                try
                {
                    WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//table/tbody/tr[@id = 'user-" + agentsToClean.get(i) + "']//img[@src = 'imagenes/delete2.png']", firefoxDriver);
                    firefoxWaiting.until(ExpectedConditions.elementToBeClickable(deleteButton));
                    SeleniumDAO.click(deleteButton);
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']")));
                    WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
                    firefoxWaiting.until(ExpectedConditions.elementToBeClickable(confirmButton));
                    Thread.sleep(500);
                    SeleniumDAO.click(confirmButton);
                    SeleniumDAO.click(confirmButton);
                } catch (Exception e) { }
            }

            WebElement modAgentsGroups = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'edit-groups.php']", firefoxDriver);
            SeleniumDAO.click(modAgentsGroups);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
            for (int i = 0; i < groupsToClean.size(); i++)
            {
                try{
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + groupsToClean.get(i) + "')]/following-sibling::td//img[@src = 'imagenes/delete2.png']")));
                    WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + groupsToClean.get(i) + "')]/following-sibling::td//img[@src = 'imagenes/delete2.png']", firefoxDriver);
                    firefoxWaiting.until(ExpectedConditions.elementToBeClickable(deleteButton));
                    Thread.sleep(250);
                    SeleniumDAO.click(deleteButton);
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']")));
                    WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", firefoxDriver);
                    Thread.sleep(500);
                    SeleniumDAO.click(confirmButton);
                    SeleniumDAO.click(confirmButton);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            deleteService();
        } catch (Exception e){

        } finally {
            firefoxDriver.close();
        }
    }

    public void deleteService()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("OPERATION")));
            WebElement operationsTab = SeleniumDAO.selectElementBy("id", "OPERATION", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(operationsTab);

            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceID);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
            } catch (Exception e) {
                System.out.println("Clean ERROR: The service: " + serviceID + "does not appears on the services table");
                e.printStackTrace();
                throw e;
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceID + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'delete-service']/a")));
            WebElement deleteServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'delete-service']/a", firefoxDriver);
            SeleniumDAO.click(deleteServiceTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            Thread.sleep(3000);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            SeleniumDAO.click(okButton);
        } catch (Exception e)
        {
            System.err.println("Clean ERROR trying to delete the service");
            e.printStackTrace();
        }
    }
}
