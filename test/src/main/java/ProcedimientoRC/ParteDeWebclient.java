package ProcedimientoRC;

import Utils.DriversConfig;
import Utils.TestWithConfig;
import Utils.Utils;

import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParteDeWebclient extends TestWithConfig {
    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String agentName4;
    static String agentName5;
    static String agentPassword;
    static String extension;
    static String extension2;
    static String number;
    static String manualCallModeName;
    static String incomingCallModeName;
    static String serviceID;
    static String bdUrl;
    static String bdUser;
    static String bdPassword;
    static String bdPhoneStatusTable;

    static String contactID;
    static String numberOfTouched;
    static String numberOfPositives;
    static String numberOfTouchedAfterAGENDADA;
    static String numberOfPositivesAfterAGENDADA;
    static String DDI;
    static String chromePath;

    static WebDriver firefoxDriver;
    static WebDriver chromeDriver;
    static WebDriverWait firefoxWaiting;
    static WebDriverWait chromeWaiting;


    HashMap<String, String> results = new HashMap<>();


    public ParteDeWebclient(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless", "chromePath")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName4", "agentName5", "agentPassword", "extension", "extension2")));
        requiredParameters.put("Contact", new ArrayList<>(Arrays.asList("number")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("manualCallModeName", "incomingCallModeName")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));
        requiredParameters.put("BD", new ArrayList<>(Arrays.asList("bdUrl", "bdUser", "bdPassword", "bdPhoneStatusTable")));
        requiredParameters.put("Queue", new ArrayList<>(Arrays.asList("DID")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws Exception {
        super.checkParameters();

        try
        {
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");
            chromePath = commonIni.get("General", "chromePath");
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");
            agentName4 = commonIni.get("Agent", "agentName4");
            agentName5 = commonIni.get("Agent", "agentName5");
            agentPassword = commonIni.get("Agent", "agentPassword");
            extension = commonIni.get("Agent", "extension");
            extension2 = commonIni.get("Agent", "extension2");
            number = commonIni.get("Contact", "number");
            manualCallModeName = commonIni.get("CallMode", "manualCallModeName");
            incomingCallModeName = commonIni.get("CallMode", "incomingCallModeName");
            serviceID = commonIni.get("Service", "serviceID");
            bdUrl = commonIni.get("BD", "bdUrl");
            bdUser = commonIni.get("BD", "bdUser");
            bdPassword = commonIni.get("BD", "bdPassword");
            bdPhoneStatusTable = commonIni.get("BD", "bdPhoneStatusTable");
            DDI = commonIni.get("Queue", "DID");


            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            //Este metodo borra todos los contactos para facilitar la programacion mas adelante.
            deleteContacts();
            results.put("-- Connect and go available test  ->  ", connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension));
            results.put("-- Do a manual call and finish on Venta-ADSL and get the number of contacts positives and touched ->  ", manualToVentaADSL());
            results.put("-- Do a manual call and finish on Pendiente-Agendada2 and check the number of contacts positives and touched  ->  ", manualToPendienteAgendada());
            results.put("-- Check the callback on Database  ->  ", checkCallbackOnDB());
            results.put("-- Check scheduled call on agent contact view  ->  ", checkScheduledCallback());
            results.put("-- Recall a contact to tipify as POSITIVE and check the number of contacts touched decrements and positives increments  ->  ", reManualToVentaADSL());
            results.put("-- Wait a incoming call, park it and and unpark it  ->  ", incomingCall());
            results.put("-- Do a manual call and transfer to another agent  ->  ", manualCallAndTransfer());
            results.put("-- Wait a incoming call and transfer to anocher agent  ->  ", incomingAndTransfer());
            results.put("-- Create a new agent state and check if appears on the agent view  ->  ", createNewState());

            return results;
        } catch (Exception e)
        {
            e.printStackTrace();
            return results;
        } finally
        {
            firefoxDriver.close();
        }
    }

    public void deleteContacts() throws InterruptedException {
        //Login on dialapplet web
        firefoxDriver.get(url + "dialapplet-web");
        Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: Login on dialapplet web failed");
        }

        //Searchs the service on the services table
        try {
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
            searcher.sendKeys(serviceID);
            Thread.sleep(1000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("The service ID: " + serviceID + " was not found");
        }

        WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceID + "')]", firefoxDriver);
        SeleniumDAO.click(service);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'service_operations']/a")));
        WebElement operationsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'service_operations']/a", firefoxDriver);
        SeleniumDAO.click(operationsTab);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@id = 'serv-camp-rem-cont']/img")));
        WebElement removeContactsButton = SeleniumDAO.selectElementBy("xpath", "//a[@id = 'serv-camp-rem-cont']/img", firefoxDriver);
        SeleniumDAO.click(removeContactsButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
        WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
        Thread.sleep(500);
        SeleniumDAO.click(deleteButton);

        Thread.sleep(2500);
    }

    public String connectAndAvailable(WebDriver driver, WebDriverWait driverWaiting, String agentName, String extension)
    {
        try
        {
            driver.get(url + "clienteweb/login.php");

            Utils.loginWebClient(agentName, agentPassword, 1, driver);
            //Selects the extension
            try {
                driverWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + extension + "']")));
                Select tlf = SeleniumDAO.findSelectElementBy("id", "extension", driver);
                tlf.selectByValue(extension);
            } catch (Exception e) {
                //Checks if the invalid username and/or password dialog shows up or if the problem is the exception
                try {
                    WebDriverWait waitingError = new WebDriverWait(driver, 5);
                    //If the error dialog is not found means that the username and password are correct so the problem is the extension
                    waitingError.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                    return "ERROR. The username and/or password is invalid";
                } catch (Exception e2) {
                    return "ERROR. Check if jitsi is launched, if is launched, the extension does not exist. You have to configure it first";
                }
            }

            //Login
            WebElement loginButton = SeleniumDAO.selectElementBy("id", "login", driver);
            SeleniumDAO.click(loginButton);

            WebDriverWait firefoxWaiting = new WebDriverWait(driver, 10);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));


            //Go available
            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", driver);
            Thread.sleep(1500);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", driver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("available")));
            WebElement availableState = SeleniumDAO.selectElementBy("id", "available", driver);
            SeleniumDAO.click(availableState);

            SeleniumDAO.switchToDefaultContent(driver);

            return "Test OK. The agent: " + agentName + " was logged and went available";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The agent: " + agentName + "failed when loggin or going available";
        }
    }

    public String manualToVentaADSL()
    {
        try
        {
            //Do a call creating a contact
            String callRes = doACall(manualCallModeName, number, true);
            if(callRes.contains("ERROR")) return callRes;
            selectTypology("VENTA", "ADSL");

            //Saves the contact ID
            String[] contactIDSplitted = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'col-sm-12 showflow-info']//li[1]", firefoxDriver).getText().split(" ", 0);
            contactID = contactIDSplitted[2];

            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", firefoxDriver);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            if(getTouchedAndPositives(false).contains("ERROR")) return "ERROR. The number of contacts positives and touched could not be taken";

            return "Test OK. The test runned succesfully. A call should has been done on number " + number;
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The test failed";
        }
    }

    public String manualToPendienteAgendada()
    {
        try
        {
            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);
            Thread.sleep(1500);

            //Do a call selecting the contact created before
            String callRes = doACall(manualCallModeName, number, false);
            if(callRes.contains("ERROR")) return callRes;

            selectTypology("PENDIENTE", "AGENDADA");


            programCallback();

            firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));

            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            if(getTouchedAndPositives(true).contains("ERROR")) return "ERROR. The number of contacts positives and touched after tipify as 'AGENDADA' could not be taken";

            //Despues de las dos llamadas los tocados deben incrementarse en 1 y los positivos decrementarse en 1
            if(Integer.valueOf(numberOfTouchedAfterAGENDADA) - 1 == Integer.valueOf(numberOfTouched) &&
                    Integer.valueOf(numberOfPositives) - 1 == Integer.valueOf(numberOfPositivesAfterAGENDADA))
            {
                return "Test OK. A call should has been done on number " + number + ". Also the positives have been reduced 1 and the touched have been incremented 1";

            } else
            {
                return "ERROR. The number of contacts positives and touched have not updated correctly";
            }
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The test failed";
        }
    }

    public String checkScheduledCallback()
    {
        try
        {
            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);
            SeleniumDAO.switchToFrame("frmcontent-schedCalls", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '16:00')]/following-sibling::td[contains(., '" + contactID + "')]")));

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            return "Test OK. The programed callback appears on the agent contacts view as a scheduled call";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The programed callback does not appear on the agent contacts view";
        }
    }

    public String reManualToVentaADSL()
    {
        try
        {
            String callRes = doACall(manualCallModeName, number, false);
            if(callRes.contains("ERROR")) return callRes;

            selectTypology("VENTA", "ADSL");

            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", firefoxDriver);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            if(getTouchedAndPositives(false).contains("ERROR")) return "ERROR. The number of contacts positives and touched could not be taken";

            //Despues de rellamar al contacto y tipificarlo como positivo, los positivos deben incrementarse y los tocados decrementarse
            if(Integer.valueOf(numberOfTouchedAfterAGENDADA) - 1 == Integer.valueOf(numberOfTouched) &&
                    Integer.valueOf(numberOfPositives) - 1 == Integer.valueOf(numberOfPositivesAfterAGENDADA))
            {
                return "Test OK. A call should has been done on number " + number + ". Also the positives have been reduced 1 and the touched have been incremented 1";

            } else
            {
                return "ERROR. The number of contacts positives and touched have not updated correctly";
            }
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        }

    }

    public String checkCallbackOnDB()
    {
        try
        {
            firefoxDriver.get(bdUrl);

            SeleniumDAO.switchToFrame("browser", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//body[@class = 'browser']//a[contains(., 'PostgreSQL')]")));
            WebElement postgreSQLServer = SeleniumDAO.selectElementBy("xpath", "//body[@class = 'browser']//a[contains(., 'PostgreSQL')]", firefoxDriver);
            SeleniumDAO.click(postgreSQLServer);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            SeleniumDAO.switchToFrame("detail", firefoxDriver);

            //Login
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.name("loginUsername")));
            WebElement usernameInput = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'navbar']//input[@name = 'loginUsername']", firefoxDriver);
            usernameInput.sendKeys(bdUser);
            WebElement passwordInput = SeleniumDAO.selectElementBy("id", "loginPassword", firefoxDriver);
            passwordInput.sendKeys(bdPassword);

            WebElement loginButton = SeleniumDAO.selectElementBy("xpath", "//input[@type = 'submit']", firefoxDriver);
            SeleniumDAO.click(loginButton);

            //go to dialapplet database
            WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[@href = 'redirect.php?subject=database&server=localhost%3A5432%3Aallow&database=dialapplet&']",
                    firefoxDriver);
            SeleniumDAO.click(dialappletDB);

            //Click on Schema->public
            WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'redirect.php?subject=schema&server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&']",
                    firefoxDriver);
            SeleniumDAO.click(publicSchema);

            WebElement phoneStatusTable = SeleniumDAO.selectElementBy("xpath", "//a[contains(., '" + bdPhoneStatusTable + "')]", firefoxDriver);
            SeleniumDAO.click(phoneStatusTable);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(., 'Select')]")));
            WebElement selectButton = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Select')]", firefoxDriver);
            SeleniumDAO.click(selectButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name = 'values[contactid]']")));
            WebElement contactIDInput = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'values[contactid]']", firefoxDriver);
            contactIDInput.sendKeys(contactID);

            WebElement callbackDateCheckbox = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'show[callbackdate]']", firefoxDriver);
            SeleniumDAO.click(callbackDateCheckbox);

            WebElement launchDateCheckBox = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'show[launchdate]']", firefoxDriver);
            SeleniumDAO.click(launchDateCheckBox);

            selectButton = SeleniumDAO.selectElementBy("xpath", "//input[@name = 'select']", firefoxDriver);
            SeleniumDAO.click(selectButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '16:00:00')]")));
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString() + "\nERROR. The schedule was not programed correctly";
            }

            return "Test OK. The callback was programed on 16:00:00.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return "ERROR. Unexpected error";
        }
    }

    public String incomingCall()
    {
        try
        {
            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);

            WebDriverWait waitingIncomingCall = new WebDriverWait(firefoxDriver, 600);
            System.out.println("You must do a call to the number: " + DDI);
            waitingIncomingCall.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@role = 'presentation' and contains(., '" + number + "')]")));

            WebElement parkCallButton = SeleniumDAO.selectElementBy("xpath", "//a[@id = 'aparkCall']/img", firefoxDriver);
            SeleniumDAO.click(parkCallButton);

            Thread.sleep(2000);

            WebElement unparkCallButton = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'calldata']", firefoxDriver);
            SeleniumDAO.click(unparkCallButton);

            System.out.print("You can hang up");

            Thread.sleep(5000);

            return "Test OK. A incoming call should have been entry on agent view, parked and unparked.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The incoming call did not entry on agent view";
        }
    }

    public String manualCallAndTransfer()
    {
        try
        {
            //connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);

            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 60);
            connectAndAvailable(chromeDriver, chromeWaiting, agentName5, extension2);

            String callRes = doACall(manualCallModeName, number, false);
            if(callRes.contains("ERROR")) return callRes;

            WebElement transferButton = SeleniumDAO.selectElementBy("xpath", "//a[@id = 'atransferCall']/img", firefoxDriver);
            SeleniumDAO.click(transferButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            transferButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'transferFancyBox']", firefoxDriver);
            SeleniumDAO.click(transferButton);

            System.out.println("Waiting the transfered call on the second agent");

            try
            {
                chromeWaiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@role = 'presentation' and contains(., '" + number + "')]")));
            } catch(Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The call could not be transfered";
            }


            chromeDriver.switchTo().frame(chromeDriver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));

            try
            {
                //espera hasta que se muestren algunos elemetos del showflow transferido
                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'contact-phone']")));
                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'contact-city']")));
            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The showflow did not transfered OK";
            }

            System.out.println("You must hang up");
            Thread.sleep(4000);
            return "Test OK. The call was transfered with the showflow correctly";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        } finally
        {
            chromeDriver.close();
        }
    }

    public String incomingAndTransfer()
    {
        try
        {
            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            WebElement removeTab = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
            SeleniumDAO.click(removeTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'swal2-icon swal2-warning pulse-warning']")));
            WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'swal2-confirm swal2-styled']", firefoxDriver);
            SeleniumDAO.click(confirmButton);

            //Go available
            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
            Thread.sleep(1500);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("available")));
            WebElement availableState = SeleniumDAO.selectElementBy("id", "available", firefoxDriver);
            SeleniumDAO.click(availableState);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            WebDriverWait waitingIncomingCall = new WebDriverWait(firefoxDriver, 600);
            System.out.println("You must do a call to the number: " + DDI);
            waitingIncomingCall.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@role = 'presentation' and contains(., '" + number + "')]")));

            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 60);
            connectAndAvailable(chromeDriver, chromeWaiting, agentName5, extension2);

            WebElement transferButton = SeleniumDAO.selectElementBy("xpath", "//a[@id = 'atransferCall']/img", firefoxDriver);
            SeleniumDAO.click(transferButton);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'transferFancyBox']")));
            transferButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'transferFancyBox']", firefoxDriver);
            SeleniumDAO.click(transferButton);

            System.out.println("Waiting the transfered call on the second agent");

            try
            {
                chromeWaiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@role = 'presentation' and contains(., '" + number + "')]")));
            } catch(Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The call could not be transfered";
            }


            chromeDriver.switchTo().frame(chromeDriver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));

            try
            {
                //espera hasta que se muestren algunos elemetos del showflow transferido
                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'contact-phone']")));
                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'contact-city']")));
            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The showflow did not transfered OK";
            }

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            System.out.println("You must hung up");

            return "Test OK. The call was transfered with the showflow correctly";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        } finally
        {
            chromeDriver.close();
        }
    }

    public String createNewState()
    {
        try
        {
            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            //Login on dialapplet web
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id = 'ADMIN']/a")));
            WebElement adminTab = SeleniumDAO.selectElementBy("xpath", "//li[@id = 'ADMIN']/a", firefoxDriver);
            SeleniumDAO.click(adminTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = 'changeStates.php']")));
            WebElement agentStatesButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'changeStates.php']", firefoxDriver);
            SeleniumDAO.click(agentStatesButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("textNewState")));
            WebElement newStateNameInput = SeleniumDAO.selectElementBy("id", "textNewState", firefoxDriver);
            newStateNameInput.sendKeys("Merendando");

            WebElement addStateButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/add2.png']", firefoxDriver);
            SeleniumDAO.click(addStateButton);

            String connectAndCheckRes = connectAndCheckNewState(firefoxDriver, firefoxWaiting, agentName4, extension);
            if(connectAndCheckRes.contains("ERROR")) return connectAndCheckRes;


            return "Test OK. The new state was created and appears on the agent view";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        }
    }

    public String abandonedCall()
    {
        try
        {

        } catch(Exception e)
        {

        }
    }
    public String dialCall(String callmode, String number) throws InterruptedException
    {
        //Do a call
        Thread.sleep(1000);
        firefoxWaiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("aclickToCall")));
        WebElement callButton = SeleniumDAO.selectElementBy("id", "aclickToCall", firefoxDriver);
        SeleniumDAO.click(callButton);

        //Selects the callmode
        try {
            //firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + callMode + "']")));
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("manualCampaigns")));
            Select whichCallMode = SeleniumDAO.findSelectElementBy("id", "manualCampaigns", firefoxDriver);
            Thread.sleep(1000);
            whichCallMode.selectByVisibleText(callmode);
        } catch (Exception e) {
            System.out.println("Selecting the callmode: " + callmode);
            return "ERROR. The callmode selected does not exist";
        }

        SeleniumDAO.click(callButton);

        WebElement numberField = SeleniumDAO.selectElementBy("id", "clickToCallDirect", firefoxDriver);
        SeleniumDAO.click(numberField);
        numberField.sendKeys(number);
        numberField.sendKeys(Keys.ENTER);

        return "";
    }

    public void selectTypology(String typology, String subtypology)
    {
        firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));

        WebElement timeInput = SeleniumDAO.selectElementBy("id", "contact-aux2", firefoxDriver);
        timeInput.sendKeys("3h");

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class = 'next']")));
        WebElement nextButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'next']", firefoxDriver);
        SeleniumDAO.click(nextButton);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[contains(@class, 'action-field typology')]")));
        Select typologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[contains(@class, 'action-field typology')]", firefoxDriver);
        typologySelector.selectByVisibleText(typology);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[contains(., '" + subtypology + "')]")));
        Select subtypologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[contains(@class, 'action-field subtypology')]", firefoxDriver);
        subtypologySelector.selectByVisibleText(subtypology);
    }

    public String getTouchedAndPositives(boolean after)
    {
        try
        {
            //Login on dialapplet web
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }

            //Searchs the service on the services table
            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceID);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
            } catch (Exception e) {
                return e.toString() + "ERROR: The service with ID: " + serviceID + "does not appears on the services table";
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceID + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'productivity']//a")));
            WebElement productivityTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'productivity']//a", firefoxDriver);
            SeleniumDAO.click(productivityTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = '#tabs-www']")));
            WebElement wwwTab = SeleniumDAO.selectElementBy("xpath", "//a[@href = '#tabs-www']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(wwwTab));
            Thread.sleep(500);
            SeleniumDAO.click(wwwTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., 'NULL')]/following-sibling::td")));
            if(!after)
            {
                numberOfTouched = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'NULL')]/following-sibling::td", firefoxDriver).getText();
                numberOfPositives = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'POSITIVE')]/following-sibling::td", firefoxDriver).getText();
                System.out.println("Touched: " + numberOfTouched);
                System.out.println("Positive: " + numberOfPositives);
            } else {
                numberOfTouchedAfterAGENDADA = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'NULL')]/following-sibling::td", firefoxDriver).getText();
                numberOfPositivesAfterAGENDADA = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'POSITIVE')]/following-sibling::td", firefoxDriver).getText();
                System.out.println("Touched: " + numberOfTouchedAfterAGENDADA);
                System.out.println("Positive: " + numberOfPositivesAfterAGENDADA);
            }


            return "Test OK. The number of contacts positives and touched have been taken";
        } catch(Exception e)
        {
            e.printStackTrace();
            return "ERROR. The number of contacts positives and touched could no be taken";
        }
    }

    public String doACall(String callmode, String number, boolean newContact)
    {
        try
        {
            //Marcar llamada
            if(dialCall(callmode, number).contains("ERROR")) return "ERROR. The callmode selected does not exist";

            firefoxWaiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='contactCall']")));

            if(newContact)
            {
                //LLama creando un contacto nuevo
                WebElement callButton = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'newcontactcall']//button[@class='contactCall']", firefoxDriver);
                SeleniumDAO.click(callButton);
            } else
            {
                //LLama seleccionando el contacto creado
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='contactCall']")));
                WebElement callButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + contactID + "')]/following-sibling::td//button[contains(@class, 'contactCall')]", firefoxDriver);
                SeleniumDAO.click(callButton);
            }

            Thread.sleep(15000);

            return "";

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        }
    }

    public void programCallback()
    {
        try
        {
            firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[contains(@id, 'fancybox-frame')]")));
            //SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver); //hace unos dias la pagina estaba diferente y esto era necesario
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@id = '16']//img")));
            WebElement fourPMButton = SeleniumDAO.selectElementBy("xpath", "//tr[@id = '16']//img", firefoxDriver);
            Thread.sleep(3000);
            SeleniumDAO.click(fourPMButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@id = 'cal_1600']")));
            WebElement scheduleButton = SeleniumDAO.selectElementBy("xpath", "//img[@id = 'cal_1600']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(scheduleButton));
            SeleniumDAO.click(scheduleButton);
            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver); //hace unos dias la pagina estaba diferente y esto era necesario
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = 'Save']")));
            WebElement saveButton = SeleniumDAO.selectElementBy("xpath", "//input[@value = 'Save']", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.visibilityOf(saveButton));
            SeleniumDAO.click(saveButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));
            firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[@class = 'fancybox-iframe']")));
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(okButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String connectAndCheckNewState(WebDriver driver, WebDriverWait driverWaiting, String agentName, String extension)
    {
        try
        {
            driver.get(url + "clienteweb/login.php");

            Utils.loginWebClient(agentName, agentPassword, 1, driver);
            //Selects the extension
            try {
                driverWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + extension + "']")));
                Select tlf = SeleniumDAO.findSelectElementBy("id", "extension", driver);
                tlf.selectByValue(extension);
            } catch (Exception e) {
                //Checks if the invalid username and/or password dialog shows up or if the problem is the exception
                try {
                    WebDriverWait waitingError = new WebDriverWait(driver, 5);
                    //If the error dialog is not found means that the username and password are correct so the problem is the extension
                    waitingError.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-error animateErrorIcon']")));
                    return "ERROR. The username and/or password is invalid";
                } catch (Exception e2) {
                    return "ERROR. Check if jitsi is launched, if is launched, the extension does not exist. You have to configure it first";
                }
            }

            //Login
            WebElement loginButton = SeleniumDAO.selectElementBy("id", "login", driver);
            SeleniumDAO.click(loginButton);

            WebDriverWait firefoxWaiting = new WebDriverWait(driver, 10);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("headerButton")));


            //Go available
            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", driver);
            Thread.sleep(1500);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", driver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@title = 'Merendando']")));
            WebElement merendandoState = SeleniumDAO.selectElementBy("xpath", "//a[@title = 'Merendando']/div", driver);
            SeleniumDAO.click(merendandoState);

            SeleniumDAO.switchToDefaultContent(driver);

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The new state does not appear on the agent view";
        }
    }


}
