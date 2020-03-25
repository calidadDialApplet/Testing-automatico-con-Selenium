package ProcedimientoRC;

import Utils.Utils;
import exceptions.MissingParameterException;
import org.ini4j.Wini;
import Utils.TestWithConfig;
import Utils.DriversConfig;

import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ParteDeAgentesTest extends TestWithConfig {
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
    static String agentPassword;
    static String pilotAgentName1;
    static String pilotAgentName2;
    static String agentCoordName1;
    static String agentCoordName6;
    static String coordinatorPassword;
    static String headless;
    static String agentsCsvPath;
    static String serviceID;

    //These two variables are to prevent an issue with licenses when you try to login with AgentName1 and AgentName2
    String timeAgentName1Created;
    String timeAgentName2Created;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();

    public ParteDeAgentesTest(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Group", new ArrayList<>(Arrays.asList("groupName", "groupName1y2", "groupName3")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName1", "agentName2", "agentName3", "agentName4", "agentName5",
                "agentPassword", "pilotAgentName1", "pilotAgentName2")));
        requiredParameters.put("Coordinator", new ArrayList<>(Arrays.asList("agentCoordName1", "agentCoordName6", "coordinatorPassword")));
        requiredParameters.put("CSV", new ArrayList<>(Arrays.asList("agentsCsvPath")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));

        return requiredParameters;
    }

    public HashMap<String, String> check() throws MissingParameterException {
        super.checkParameters();

        //TODO El try catch de los ini.get no es necesario
        try {
            try {
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
                agentPassword = commonIni.get("Agent", "agentPassword");
                pilotAgentName1 = commonIni.get("Agent", "pilotAgentName1");
                pilotAgentName2 = commonIni.get("Agent", "pilotAgentName2");

                agentCoordName1 = commonIni.get("Coordinator", "agentCoordName1");
                agentCoordName6 = commonIni.get("Coordinator", "agentCoordName6");
                coordinatorPassword = commonIni.get("Coordinator", "coordinatorPassword");

                agentsCsvPath = commonIni.get("CSV", "agentsCsvPath");
                serviceID = commonIni.get("Service", "serviceID");

            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);

            results.put("--Connection Test  ->  ", connectionTest());
            results.put("\n--Import agents with CSV  ->  ", importCSV());
            results.put("\n--Add agents to a new group  ->  ", addAgentsToNewGroup());
            results.put("\n--Add new group: " + groupName1y2 + "  ->  ", newGroup1y2());
            results.put("\n--Create a Coordinator + Agent user: " + agentCoordName1 + "  ->  ", newCoordAgent());
            results.put("\n--Create a Agent user with name: " + agentName1 + "  ->  ", newAgent1());
            results.put("\n--Create a Agent user with name: " + agentName2 + "  ->  ", newAgent2());
            results.put("\n--Create a Agent user with name: " + agentName3 + "  ->  ", newAgent3());
            results.put("\n--Add new group: " + groupName3 + "  ->  ", newGroup3());
            results.put("\n--Create a Agent user with name: " + agentName4 + "  ->  ", newAgent4());
            results.put("\n--Create a Agent user with name: " + agentName5 + "  ->  ", newAgent5());
            results.put("\n--Create a Agent user with name: " + agentCoordName6 + "  ->  ", newAgentC6());
            results.put("\n--Login with agent: " + agentName1 + "test  ->  ", agent1LoginTest());
            results.put("\n--Login with agent: " + agentName2 + "test  ->  ", agent2LoginTest());

            return results;

        } catch (Exception e) {
            return results;
        } finally {
            firefoxDriver.close();
        }

    }

    public String connectionTest()
    {
        firefoxDriver.get(url + "dialapplet-web");
        Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            //System.out.println("Login successfully");
            return "Test OK";
        } catch (Exception e) {
            //System.err.println("ERROR: Login failed");
            return e.toString();
        }
    }

    public String addAgentsToNewGroup() throws InterruptedException
    {
        try {
            String idNewGroupAdded = "";

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

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                //System.err.println("ERROR: The group: " + groupName + " already exists. Delete it and try again");
                return "ERROR: The group: " + groupName + " already exists. Delete it and try again";
            } catch (Exception e) {
            }


            //Fills a list with the elements of the table
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class = 'tabla-principal']//tbody")));
            WebElement table = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'tabla-principal']//tbody", firefoxDriver);
            List<WebElement> listOfRows = table.findElements(By.tagName("tr"));

            int lastRowAdded = listOfRows.size() - 1;
            WebElement newGroupAdded = listOfRows.get(lastRowAdded);
            idNewGroupAdded = newGroupAdded.findElement(By.xpath("//tbody/tr[" + lastRowAdded + "]/td[1]")).getText();


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
            } catch (Exception e) {
                //System.err.println("The agent to add: " + pilotAgentName1 + " or " + pilotAgentName2 + " does not exists." +
                //" Check the inicializationSettings.ini");
                return e.toString() + "\nERROR: The agent to add: " + pilotAgentName1 + " or " + pilotAgentName2 + " does not exists." +
                        " Check the inicializationSettings.ini";
            }
            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//*[@type = 'submit']", firefoxDriver);
            SeleniumDAO.click(sendButton);
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Thread.sleep(1500);

            //System.out.println(pilotAgentName1 + " and " + pilotAgentName2 + " added to " + groupName);
            return "Test OK. " + pilotAgentName1 + " and " + pilotAgentName2 + " added to " + groupName + "with id = " + idNewGroupAdded;
        } catch (Exception e) {
            //System.err.println("Failed while adding " + pilotAgentName1 + " and " + pilotAgentName2 + " to " + groupName);
            return e.toString() + "\n Failed while adding " + pilotAgentName1 + " and " + pilotAgentName2 + " to " + groupName;
        }


    }

    public String newGroup1y2() throws InterruptedException
    {
        try {
            String idNewGroupAdded = "";

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("new_groupname")));
            WebElement newGroupNameInput = SeleniumDAO.selectElementBy("id", "new_groupname", firefoxDriver);
            newGroupNameInput.clear();
            newGroupNameInput.sendKeys(groupName1y2);

            WebElement addNewGroupButton = SeleniumDAO.selectElementBy("cssSelector", "img[src='imagenes/add2.png']", firefoxDriver);
            SeleniumDAO.click(addNewGroupButton);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                //System.err.println("ERROR: The group: " + groupName + " already exists. Delete it and try again");
                return "ERROR: The group: " + groupName1y2 + " already exists. Delete it and try again";
            } catch (Exception e) {
            }


            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class = 'tabla-principal']//tbody")));
                WebElement table = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'tabla-principal']//tbody", firefoxDriver);
                List<WebElement> listOfRows = table.findElements(By.tagName("tr"));

                int lastRowAdded = listOfRows.size() - 1;
                WebElement newGroupAdded = listOfRows.get(lastRowAdded);
                idNewGroupAdded = newGroupAdded.findElement(By.xpath("//tbody/tr[" + lastRowAdded + "]/td[1]")).getText();
            } catch (Exception e) {
                return e.toString() + "\n ERROR: The group " + groupName1y2 + " could not be added";
            }

            return "Test OK. " + groupName1y2 + " added successfully with id = " + idNewGroupAdded;

        } catch (Exception e) {
            return e.toString();
        }
    }

    public String newCoordAgent() throws InterruptedException
    {
        try {
            WebElement configureUsers = SeleniumDAO.selectElementBy("xpath",
                    "//div[@class = 'ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active']" +
                            "//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(configureUsers);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentCoordName1);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", firefoxDriver);
            userPass.sendKeys(coordinatorPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(coordinatorPassword);
            // Set coordinator role
            WebElement coordinator = SeleniumDAO.selectElementBy("id", "iscoordinator", firefoxDriver);
            SeleniumDAO.click(coordinator);
            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);
            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);


            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);
            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody//label[contains(., '" + serviceID + "')]")));
                WebElement service = SeleniumDAO.selectElementBy("xpath", "//tbody//label[contains(., '" + serviceID + "')]", firefoxDriver);
                SeleniumDAO.click(service);
            } catch (Exception e) {
                return e.toString() + "\nERROR: The service with id = " + serviceID + " does not exists";
            }

            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(submitSecondPage);
            // Configure agent groups as coordinator(default configuration)
            WebElement submitThirdPage = SeleniumDAO.selectElementBy("name", "submit", firefoxDriver);
            SeleniumDAO.click(submitThirdPage);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentCoordName1);
                Thread.sleep(4000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentCoordName1)));
                WebElement userOnTable = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'user-" + agentCoordName1 + "']//td", firefoxDriver);
                //WebElement userOnTable = () -> firefoxDriver.findElement(By.id("user-" + agentCoordName1));
                //WebElement userOnTable = SeleniumDAO.selectElementBy("id", "user-" + agentCoordName1, firefoxDriver);
                SeleniumDAO.click(userOnTable);
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            //Click on the new user and checks if the service is assigned to him
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'subContent']" +
                        "//a[@href = 'newServiceAssistant.php?serviceid=" + serviceID + "']")));
            } catch (Exception e) {
                return "ERROR: The service could not be assigned correctly to: " + agentCoordName1;
            }

            firefoxDriver.navigate().to(url + "dialapplet-web/edit-user.php?username=" + agentCoordName1 + "&");

            agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            coordinator = SeleniumDAO.selectElementBy("id", "iscoordinator", firefoxDriver);

            if (agent.isSelected() && coordinator.isSelected())
                return "Test OK. The user was created successfully with the agent and coordinator role" +
                        " and with the service id = " + serviceID + " assigned";
            else if (agent.isSelected()) return "ERROR: The coordinator role could not be assigned correctly";
            else return "ERROR: The agent role could not be assigned correctly";

        } catch (Exception e) {
            return e.toString();
        }

    }

    public String newAgent1() throws InterruptedException
    {
        try {
            WebElement configureUsers = SeleniumDAO.selectElementBy("xpath",
                    "//div[@class = 'ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active']" +
                            "//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(configureUsers);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentName1);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", firefoxDriver);
            userPass.sendKeys(agentPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(agentPassword);

            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);

            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);
            String[] aux = LocalTime.now().toString().split(Pattern.quote(".")); //This aux array is to split the hour without milisecs
            timeAgentName1Created = aux[0];

            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentName1 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);

            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(submitSecondPage);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentName1);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentName1)));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }



            return "Test OK. The agent: " + agentName1 + " was created.";

        } catch (Exception e) {
            return e.toString();
        }
    }

    public String newAgent2() throws InterruptedException
    {
        try {
            String aux = connectionTest();

            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", firefoxDriver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'adminTable']//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(users);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody//a[@href = 'edit-user.php?']")));
            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentName2);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", firefoxDriver);
            userPass.sendKeys(agentPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(agentPassword);

            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);

            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);
            String[] aux2 = LocalTime.now().toString().split(Pattern.quote(".")); //This aux2 array is to split the hour without milisecs
            timeAgentName2Created = aux2[0];

            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentName2 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);

            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(submitSecondPage);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentName2);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentName2)));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }



            return "Test OK. The agent: " + agentName2 + " was created.";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public String newAgent3() throws InterruptedException
    {
        try {
            String aux = connectionTest();

            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", firefoxDriver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'adminTable']//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(users);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody//a[@href = 'edit-user.php?']")));
            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentName3);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", firefoxDriver);
            userPass.sendKeys(agentPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(agentPassword);

            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);

            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);

            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentName3 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);

            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(submitSecondPage);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentName3);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentName3)));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            return "Test OK. The agent: " + agentName3 + " was created.";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public String newGroup3() throws InterruptedException
    {
        String idNewGroupAdded = "";

        try {
            // Click on Modify agent groups button in left menu
            WebElement modAgentsGroups = SeleniumDAO.selectElementBy("xpath", "//div[@role = 'tabpanel']//a[@href = 'edit-groups.php']", firefoxDriver);
            SeleniumDAO.click(modAgentsGroups);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("new_groupname")));
            WebElement newGroupNameInput = SeleniumDAO.selectElementBy("id", "new_groupname", firefoxDriver);
            newGroupNameInput.sendKeys(groupName3);

            WebElement addNewGroupButton = SeleniumDAO.selectElementBy("cssSelector", "img[src='imagenes/add2.png']", firefoxDriver);
            SeleniumDAO.click(addNewGroupButton);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                //System.err.println("ERROR: The group: " + groupName + " already exists. Delete it and try again");
                return "ERROR: The group: " + groupName3 + " already exists. Delete it and try again";
            } catch (Exception e) {
            }


            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class = 'tabla-principal']//tbody")));
                WebElement table = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'tabla-principal']//tbody", firefoxDriver);
                List<WebElement> listOfRows = table.findElements(By.tagName("tr"));

                int lastRowAdded = listOfRows.size() - 1;
                WebElement newGroupAdded = listOfRows.get(lastRowAdded);
                idNewGroupAdded = newGroupAdded.findElement(By.xpath("//tbody/tr[" + lastRowAdded + "]/td[1]")).getText();
            } catch (Exception e) {
                return e.toString() + "\n ERROR: The group " + groupName3 + " could not be added";
            }

            return "Test OK. " + groupName3 + " added successfully with id = " + idNewGroupAdded;
        } catch (Exception e) {
            return e.toString();
        }

    }

    public String newAgent4()
    {
        try {
            WebElement configureUsers = SeleniumDAO.selectElementBy("xpath",
                    "//div[@class = 'ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active']" +
                            "//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(configureUsers);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentName4);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", firefoxDriver);
            userPass.sendKeys(agentPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(agentPassword);

            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);

            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);

            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentName4 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
                WebElement grupo3Checkbox = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'groups']//label[contains(., '" + groupName3 + "')]", firefoxDriver);
                SeleniumDAO.click(grupo3Checkbox);
            } catch (Exception e) {
                return e.toString() + "\nERROR: The group: " + groupName3 + " does not exists. Create it and try again.";
            }

            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            WebElement confirmButton2 = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(confirmButton2);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentName4);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentName4)));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            return "Test OK. The agent: " + agentName4 + " was created.";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public String newAgent5()
    {
        try {
            WebElement configureUsers = SeleniumDAO.selectElementBy("xpath",
                    "//div[@class = 'ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active']" +
                            "//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(configureUsers);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentName5);

            WebElement secureMode = SeleniumDAO.selectElementBy("name", "securemode", firefoxDriver);
            SeleniumDAO.click(secureMode);

            WebDriverWait waiting = new WebDriverWait(firefoxDriver, 10);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("passnew")));

            WebElement userPass = SeleniumDAO.selectElementBy("id", "passnew", firefoxDriver);
            userPass.sendKeys(agentPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(agentPassword);

            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);

            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);

            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentName5 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
                WebElement grupo3Checkbox = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'groups']//label[contains(., '" + groupName3 + "')]", firefoxDriver);
                SeleniumDAO.click(grupo3Checkbox);
            } catch (Exception e) {
                return e.toString() + "\nERROR: The group: " + groupName3 + " does not exists. Create it and try again.";
            }

            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            WebElement confirmButton2 = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(confirmButton2);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentName5);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentName5)));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            return "Test OK. The agent: " + agentName5 + " was created.";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public String newAgentC6()
    {
        try {
            WebElement configureUsers = SeleniumDAO.selectElementBy("xpath",
                    "//div[@class = 'ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active']" +
                            "//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(configureUsers);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath", "//tbody//a[@href = 'edit-user.php?']", firefoxDriver);
            SeleniumDAO.click(createUser);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", firefoxDriver);
            username.sendKeys(agentCoordName6);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", firefoxDriver);
            userPass.sendKeys(coordinatorPassword);

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id", "pass2", firefoxDriver);
            confirmUserPass.sendKeys(coordinatorPassword);

            // Set coordinator role
            WebElement coordinator = SeleniumDAO.selectElementBy("id", "iscoordinator", firefoxDriver);
            SeleniumDAO.click(coordinator);

            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", firefoxDriver);
            SeleniumDAO.click(agent);

            // Click on submit button
            Thread.sleep(500);
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(accept);

            try {
                /*firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName1 + " already exists. Delete it and try again.";*/
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("send_tabs")));
            } catch (Exception e) {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
                return "ERROR: The user " + agentCoordName6 + " already exists. Delete it and try again.";
            }

            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", firefoxDriver);
            SeleniumDAO.click(send);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));
                WebElement grupo3Checkbox = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'groups']//label[contains(., '" + groupName3 + "')]", firefoxDriver);
                SeleniumDAO.click(grupo3Checkbox);
            } catch (Exception e) {
                return e.toString() + "\nERROR: The group: " + groupName3 + " does not exists. Create it and try again.";
            }

            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", firefoxDriver);
            SeleniumDAO.click(submit);

            WebElement confirmButton2 = SeleniumDAO.selectElementBy("name", "submit-page-two", firefoxDriver);
            SeleniumDAO.click(confirmButton2);

            WebElement submitThirdPage = SeleniumDAO.selectElementBy("name", "submit", firefoxDriver);
            SeleniumDAO.click(submitThirdPage);

            //Searchs the new user in the table and checks if appears
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'searcher']//input[@type = 'text']", firefoxDriver);
                searcher.sendKeys(agentCoordName6);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("user-" + agentCoordName6)));
            } catch (Exception e) {
                return e.toString() + "ERROR: Something went wrong. The user was created but don't appears on the users table";
            }

            return "Test OK. The agent: " + agentCoordName6 + " was created.";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public String importCSV() throws InterruptedException
    {
        try
        {
            /*WebElement configureUsers = SeleniumDAO.selectElementBy("xpath",
                    "//div[@class = 'ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active']" +
                            "//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(configureUsers);*/

            WebElement adminTab = SeleniumDAO.selectElementBy("id", "ADMIN", firefoxDriver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'adminTable']//a[@href = 'configure_users.php']", firefoxDriver);
            SeleniumDAO.click(users);

            // Import users by CSV
            WebElement importUsersCSV = SeleniumDAO.selectElementBy("xpath","//a[@href = 'import-csv-users.php']", firefoxDriver);
            importUsersCSV.click();

            WebElement browseButton = SeleniumDAO.selectElementBy("name", "userfile", firefoxDriver);
            // Tener en cuenta la ruta del fichero y que el mismo fichero no contenga errores
            SeleniumDAO.writeInTo(browseButton, agentsCsvPath);

            WebElement importCSVButton = SeleniumDAO.selectElementBy("id", "submitcsv", firefoxDriver);
            SeleniumDAO.click(importCSVButton);

            Thread.sleep(1000);

            String res = "";

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class = 'verde_negrita']")));
                res = "Test OK. The CSV was imported correctly";
            }   catch (Exception e)
            {
                try
                {
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class = 'rojo_negrita']")));
                    res = "ERROR: The CSV could not be imported. Check if the agents in the CSV already exists in the DB";
                } catch (Exception e2)
                {
                    try
                    {
                        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@style = 'font-weight: bold;color: #D67A00;']")));
                        res = "ERROR: Could not import all the users in CSV";
                    } catch (Exception e3)
                    { }
                }
            }

            return res;

        } catch (Exception e)
        {
            return e.toString();
        }
    }

    public String agent1LoginTest() throws ParseException {
        try
        {
        //try to login 30 seconds after the creation of the agent.
        String[] actualTimeSplited = LocalTime.now().toString().split(Pattern.quote("."));
        String actualTime = actualTimeSplited[0];

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date timeOnCreation = format.parse(timeAgentName1Created);
        Date timeNow = format.parse(actualTime);
        long difference = timeNow.getTime() - timeOnCreation.getTime();

        if(difference < 30000)
        {
            System.out.println("Waiting to login to prevent licence error");
            Thread.sleep(35000 - difference);
        }


        firefoxDriver.get(url + "clienteweb/login.php");

        Utils.loginWebClient(agentName1, agentPassword, 2, firefoxDriver);
        // Wait to take a rest button
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
        } catch (Exception e) {
            return "ERROR: " + agentName1 + " was created but login failed";
        }


        WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
        SeleniumDAO.click(states);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("rest")));

        WebElement restState = SeleniumDAO.selectElementBy("id", "rest", firefoxDriver);
        SeleniumDAO.click(restState);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);

        System.out.println("Waiting a minute in rest state");
        firefoxDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        states = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
        Thread.sleep(1500);
        SeleniumDAO.click(states);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("available")));
        WebElement availableState = SeleniumDAO.selectElementBy("id", "available", firefoxDriver);
        SeleniumDAO.click(availableState);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);

        Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

        return "Test OK. Login successfully. The agent stayed 60 seconds in rest state";
        } catch (Exception e)
        {
            return e.toString() + "\nERROR. Unexpected exception";
        }

    }

    public String agent2LoginTest() throws ParseException {
        try
        {
        //try to login 30 seconds after the creation of the agent.
        String[] actualTimeSplited = LocalTime.now().toString().split(Pattern.quote("."));
        String actualTime = actualTimeSplited[0];

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date timeOnCreation = format.parse(timeAgentName2Created);
        Date timeNow = format.parse(actualTime);
        long difference = timeNow.getTime() - timeOnCreation.getTime();

        if(difference < 30000)
        {
            System.out.println("Waiting to login to prevent licence error");
            Thread.sleep(35000 - difference);
        }


        firefoxDriver.get(url + "clienteweb/login.php");

        Utils.loginWebClient(agentName2, agentPassword, 2, firefoxDriver);
        // Wait to take a rest button
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));
        } catch (Exception e) {
            return "ERROR: " + agentName2 + " was created but login failed";
        }

        System.out.println("Waiting a minute in pre-shift state");
        firefoxDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
        Thread.sleep(1500);
        SeleniumDAO.click(states);

        SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

        WebElement availableState = SeleniumDAO.selectElementBy("id", "available", firefoxDriver);
        SeleniumDAO.click(availableState);

        SeleniumDAO.switchToDefaultContent(firefoxDriver);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("logout")));
        WebElement logOutButton = SeleniumDAO.selectElementBy("id", "logout", firefoxDriver);
        Thread.sleep(2000);
        SeleniumDAO.click(logOutButton);
        Thread.sleep(2000);

        return "Test OK. Login successfully. The agent stayed 60 seconds on pre-sift state";
        } catch (Exception e)
        {
            return e.toString() + "\nERROR. Unexpected exception";
        }

    }
}

