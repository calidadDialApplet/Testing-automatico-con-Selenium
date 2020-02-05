package ProcedimientoRC;

import org.ini4j.Wini;
import main.Main;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.FALSE;

public class ParteDeAgentesTest extends Test {
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
    static String pilotAgentName1;
    static String pilotAgentName2;
    static String coordinatorName;
    static String agentCoordName;
    static String headless;
    static String csvPath;
    static String serviceID;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();


    public HashMap<String, String> check() {
        try {
            try {
                Wini ini = new Wini(new File("InicializationSettingsRC.ini"));
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                url = ini.get("Red", "url");

                groupName = ini.get("Group", "groupName");
                groupName1y2 = ini.get("Group", "groupName1y2");
                groupName3 = ini.get("Group", "groupName3");

                agentName1 = ini.get("Agent", "agentName1");
                agentName2 = ini.get("Agent", "agentName2");
                agentName3 = ini.get("Agent", "agentName3");
                agentName4 = ini.get("Agent", "agentName4");
                agentName5 = ini.get("Agent", "agentName5");
                pilotAgentName1 = ini.get("Agent", "pilotAgentName1");
                pilotAgentName2 = ini.get("Agent", "pilotAgentName2");

                coordinatorName = ini.get("Coordinator", "coordinatorName");
                agentCoordName = ini.get("Coordinator", "agentCoordName");

                headless = ini.get("Red", "headless");
                csvPath = ini.get("CSV", "csvPath");
                serviceID = ini.get("Service", "serviceID");

            } catch (IOException e) {
                System.err.println("The inicialization file can't be loaded");
                e.printStackTrace();
                results.put("The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            results.put("Connection Test -> ", connectionTest());
            results.put("Add agents to a new group -> ", addAgentsToNewGroup());
            return results;

        } catch (Exception e)
        {
            return results;
        }
        finally
        { firefoxDriver.close();}

    }

    public String connectionTest()
    {
        firefoxDriver.get(url + "dialapplet-web");
        Main.loginDialappletWeb(adminName,adminPassword,firefoxDriver);
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            //System.out.println("Login successfully");
            return "Test OK";
        } catch (Exception e)
        {
            //System.err.println("ERROR: Login failed");
            return e.toString();
        }
    }
    public String addAgentsToNewGroup() throws InterruptedException {
        try
        {
            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", firefoxDriver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'adminTable']//a[@href = 'edit-groups.php']", firefoxDriver);
            SeleniumDAO.click(users);
            // Click on Modify agent groups button in left menu
            WebElement modAgentsGroups = SeleniumDAO.selectElementBy("xpath", "//div[@role = 'tabpanel']//a[@href = 'edit-groups.php']", firefoxDriver);
            SeleniumDAO.click(modAgentsGroups);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("new_groupname")));
            WebElement newGroupNameInput = SeleniumDAO.selectElementBy("id", "new_groupname", firefoxDriver);
            newGroupNameInput.sendKeys(groupName);


            WebElement addNewGroupButton = SeleniumDAO.selectElementBy("cssSelector", "img[src='imagenes/add2.png']", firefoxDriver);
            SeleniumDAO.click(addNewGroupButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                //System.err.println("ERROR: The group: " + groupName + " already exists. Delete it and try again");
                return "ERROR: The group: " + groupName + " already exists. Delete it and try again";
            } catch (Exception e)
            { }



            //Fills a list with the elements of the table
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class = 'tabla-principal']//tbody")));
            WebElement table = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'tabla-principal']//tbody", firefoxDriver);
            List<WebElement> listOfRows = table.findElements(By.tagName("tr"));

            int lastRowAdded = listOfRows.size()-1;
            WebElement newGroupAdded = listOfRows.get(lastRowAdded);
            String idNewGroupAdded = newGroupAdded.findElement(By.xpath("//tbody/tr["+ lastRowAdded +"]/td[1]")).getText();




            WebElement manageUsers = SeleniumDAO.selectElementBy("id", "users-" + idNewGroupAdded, firefoxDriver);
            SeleniumDAO.click(manageUsers);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("leftList")));
            WebElement leftColumn = SeleniumDAO.selectElementBy("id", "leftList", firefoxDriver);

            try {
                WebElement agentToAdd1 = SeleniumDAO.selectElementBy("xpath", "//*[text() = '" + pilotAgentName1 + "']", leftColumn);
                WebElement groupSpace = SeleniumDAO.selectElementBy("xpath", "//*[@id=\"rightList\"]", firefoxDriver);
                WebElement agentToAdd2 = SeleniumDAO.selectElementBy("xpath", "//*[text() = '" + pilotAgentName2 + "']", leftColumn);

                SeleniumDAO.dragAndDropAction(agentToAdd1, groupSpace, firefoxDriver);
                Thread.sleep(1500);
                SeleniumDAO.dragAndDropAction(agentToAdd2, groupSpace, firefoxDriver);
                Thread.sleep(1500);
            } catch (Exception e)
            {
                //System.err.println("The agent to add: " + pilotAgentName1 + " or " + pilotAgentName2 + " does not exists." +
                        //" Check the inicializationSettings.ini");
                return e.toString() + "\nERROR: The agent to add: " + pilotAgentName1 + " or " + pilotAgentName2 + " does not exists." +
                        " Check the inicializationSettings.ini";
            }
            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//*[@type = 'submit']", firefoxDriver);
            SeleniumDAO.click(sendButton);
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //System.out.println(pilotAgentName1 + " and " + pilotAgentName2 + " added to " + groupName);
            return "Test OK. " + pilotAgentName1 + " and " + pilotAgentName2 + " added to " + groupName;
        } catch (Exception e)
        {
            //System.err.println("Failed while adding " + pilotAgentName1 + " and " + pilotAgentName2 + " to " + groupName);
            return e.toString() + "\n Failed while adding \" + pilotAgentName1 + \" and \" + pilotAgentName2 + \" to \" + groupName";
        }



    }
}
