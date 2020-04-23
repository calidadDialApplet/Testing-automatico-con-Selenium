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


import javax.imageio.plugins.jpeg.JPEGImageReadParam;
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
    static String agentName7;
    static String coordName;
    static String agentPassword;
    static String extension;
    static String extension2;
    static String number;
    static String manualCallModeName;
    static String transferCallModeName;
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
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName4", "agentName5", "agentName7", "agentPassword", "extension", "extension2")));
        requiredParameters.put("Coordinator", new ArrayList<>(Arrays.asList("agentCoordName6")));
        requiredParameters.put("Contact", new ArrayList<>(Arrays.asList("number")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("manualCallModeName", "incomingCallModeName", "transfCallModeName")));
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
            agentName7 = commonIni.get("Agent", "agentName7");
            agentPassword = commonIni.get("Agent", "agentPassword");
            coordName = commonIni.get("Coordinator", "agentCoordName6");
            extension = commonIni.get("Agent", "extension");
            extension2 = commonIni.get("Agent", "extension2");
            number = commonIni.get("Contact", "number");
            manualCallModeName = commonIni.get("CallMode", "manualCallModeName");
            incomingCallModeName = commonIni.get("CallMode", "incomingCallModeName");
            transferCallModeName = commonIni.get("CallMode", "transfCallModeName");
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
            //Bug en las transferencias 7rc
            results.put("-- Do a manual call and transfer to another agent  ->  ", manualCallAndTransfer());
            results.put("-- Wait a incoming call and transfer to anocher agent  ->  ", incomingAndTransfer());
            results.put("-- Do an incoming call and hang up before the agent enters  ->  ", abandonedCall());
            results.put("-- Create a new agent state and check if appears on the agent view  ->  ", createNewState());
            results.put("-- Check all options for open showflow without call  ->  ", checkOpenSF());
            results.put("-- Check the funcionality of coordinators on web client  ->  ", checkCoordinator());

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
            String callRes = doACall(firefoxDriver, firefoxWaiting, manualCallModeName, number, true);
            if(callRes.contains("ERROR")) return callRes;
            selectTypology(firefoxDriver, firefoxWaiting, "VENTA", "ADSL");

            //Saves the contact ID
            String[] contactIDSplitted = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'col-sm-12 showflow-info']//li[1]", firefoxDriver).getText().split(" ", 0);
            contactID = contactIDSplitted[2];

            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", firefoxDriver);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterPositiveCall", firefoxDriver);


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
            String callRes = doACall(firefoxDriver, firefoxWaiting, manualCallModeName, number, false);
            if(callRes.contains("ERROR")) return callRes;

            selectTypology(firefoxDriver, firefoxWaiting, "PENDIENTE", "AGENDADA");


            programCallback();

            firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));

            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterTouchedCall", firefoxDriver);


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

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = '#schedCalls']")));
            WebElement scheduledCalls = SeleniumDAO.selectElementBy("xpath", "//a[@href = '#schedCalls']", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(scheduledCalls);

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
            String callRes = doACall(firefoxDriver, firefoxWaiting, manualCallModeName, number, false);
            if(callRes.contains("ERROR")) return callRes;

            selectTypology(firefoxDriver, firefoxWaiting,"VENTA", "ADSL");

            WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", firefoxDriver);
            SeleniumDAO.click(sendButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterPositiveCall2", firefoxDriver);


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
            //TODO eliminar este if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[@href = 'redirect.php?subject=database&server=localhost%3A5432%3Aallow&database=dialapplet&']",
                        firefoxDriver);
                SeleniumDAO.click(dialappletDB);

                //Click on Schema->public
                WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'redirect.php?subject=schema&server=localhost%3A5432%3Aallow&database=dialapplet&schema=public&']",
                        firefoxDriver);
                SeleniumDAO.click(publicSchema);

            } else
            {
                WebElement dialappletDB = SeleniumDAO.selectElementBy("xpath","//a[@href = 'redirect.php?subject=database&server=127.0.0.1%3A5432%3Aallow&database=dialapplet&']",
                        firefoxDriver);
                SeleniumDAO.click(dialappletDB);

                //Click on Schema->public
                WebElement publicSchema = SeleniumDAO.selectElementBy("xpath", "//a[@href = 'redirect.php?subject=schema&server=127.0.0.1%3A5432%3Aallow&database=dialapplet&schema=public&']",
                        firefoxDriver);
                SeleniumDAO.click(publicSchema);

            }

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

            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterIncomingCallCall", firefoxDriver);


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

            String callRes = doACall(firefoxDriver, firefoxWaiting, manualCallModeName, number, false);
            if(callRes.contains("ERROR")) return callRes;

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

            System.out.println("You must hang up");
            Thread.sleep(4000);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);
            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterManualTransferCall", firefoxDriver);


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
                System.out.println("You must hung up");
                Thread.sleep(5000);
            } catch(Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The call could not be transfered";
            }

            try
            {
                selectTypology(chromeDriver, chromeWaiting,"VENTA", "ADSL");

                chromeWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class = 'send']")));
                WebElement sendButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'send']", chromeDriver);
                SeleniumDAO.click(sendButton);

                SeleniumDAO.switchToDefaultContent(chromeDriver);
            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The showflow did not transfered OK";
            }

            //Tipifica el showflow en la llamada transferida como positivo


            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            System.out.println("You must hung up");

            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterIncomingTransferCall", firefoxDriver);


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

    public String abandonedCall()
    {
        try
        {
            //go busy
            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
            Thread.sleep(1500);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("busy")));
            WebElement busyState = SeleniumDAO.selectElementBy("id", "busy", firefoxDriver);
            SeleniumDAO.click(busyState);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            System.out.println("An abandoned call is going to be tested. Please call to the number: " + DDI);

            Thread.sleep(10000);

            System.out.println("Hang up. The correct behavior would be if it had not been answered");

            Thread.sleep(10000);

            Utils.takeScreenshot("./ParteDeWebClientOut/KpiAfterAbandonedCall", firefoxDriver);

            //TODO borrar este if cuando desaparezca la version 7
            /*if(url.contains("8"))
            {
                WebElement removeTab = SeleniumDAO. selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
                Thread.sleep(1000);
                SeleniumDAO.click(removeTab);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'swal2-icon swal2-warning pulse-warning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'swal2-confirm swal2-styled']", firefoxDriver);
                SeleniumDAO.click(confirmButton);
            } else
            {
                WebElement removeTab = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
                Thread.sleep(1000);
                SeleniumDAO.click(removeTab);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(confirmButton);
            }*/

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            return "Test OK. The An incoming call should has been done where the client hung up before any agent entered";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
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

    public String checkOpenSF()
    {
        try
        {
            //caso open showflow a no
            String showflowInteractionRes = loginAndChangeShowflowInteraction("no"); //Accede a dialapplet web y cambia el modo a no
            if(showflowInteractionRes.contains("ERROR")) return showflowInteractionRes;

            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);

            //En el caso en el que no puede abrirlo la comprobacion se hace directamente cuando se realizan los filtros para buscar los SF
            String checkOpenSFToNoRes = openSF();
            if(!checkOpenSFToNoRes.equals("OK")) return "ERROR. The setting to allow opening the showflow without calling NO does not work correctly in the web client";

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            //Caso OpenShowflow a readOnly
            showflowInteractionRes = loginAndChangeShowflowInteraction("readonly"); //Accede a dialapplet web y cambia el modo a readonly
            if(showflowInteractionRes.contains("ERROR")) return showflowInteractionRes;

            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);

            String openSFRes = openSF();
            if(openSFRes.contains("ERROR")) return openSFRes;

            String checkOpenSFReadOnlyRes = checkOpenSFToReadOnly();
            if(checkOpenSFReadOnlyRes.contains("ERROR")) return checkOpenSFReadOnlyRes;

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            //Caso solo administradores y coordinadores
            showflowInteractionRes = loginAndChangeShowflowInteraction("admin_coord"); //Accede a dialapplet web y cambia el modo a coordinadores y administradores
            if(showflowInteractionRes.contains("ERROR")) return showflowInteractionRes;

            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);

            //En el caso en el que no puede abrirlo la comprobacion se hace directamente cuando se realizan los filtros para buscar los SF
            String checkOpenSFAdminCoordRes = openSF();
            //Comprueba que no puede buscar showflows del servicio
            if(!checkOpenSFAdminCoordRes.equals("OK")) return "ERROR. The setting to allow opening the showflow without calling to Admin and coord does not work correctly in the web client";

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            //Accede con un admin para comprobar que puede abrir showflows
            connectAndAvailable(firefoxDriver, firefoxWaiting, adminName, extension);

            openSFRes = openSF();
            if(openSFRes.contains("ERROR")) return openSFRes; //Comprueba que puede buscar showflows del servicio

            //Comprueba que el coordinador puede editar el showflow
            checkOpenSFAdminCoordRes = checkOpenSFEditable();
            if(checkOpenSFAdminCoordRes.contains("ERROR")) return checkOpenSFAdminCoordRes;

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            //Caso si se permite edicion al agente
            showflowInteractionRes = loginAndChangeShowflowInteraction("yes"); //Accede a dialapplet web y cambia el modo a coordinadores y administradores
            if(showflowInteractionRes.contains("ERROR")) return showflowInteractionRes;

            connectAndAvailable(firefoxDriver, firefoxWaiting, agentName4, extension);

            openSFRes = openSF();
            if(openSFRes.contains("ERROR")) return openSFRes;

            String checkOpenSFYesRes = checkOpenSFEditable();
            if(checkOpenSFYesRes.contains("ERROR")) return checkOpenSFAdminCoordRes;

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            return "Test OK";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. ";
        }
    }

    public String checkCoordinator()
    {
        try
        {
            String connectAndAvailableRes = connectAndAvailable(firefoxDriver, firefoxWaiting, coordName, extension);
            if(connectAndAvailableRes.contains("ERROR")) return connectAndAvailableRes;

            //Go busy to ensure the calls entry to the agents
            WebElement states = SeleniumDAO.selectElementBy("id", "agent-name", firefoxDriver);
            Thread.sleep(1500);
            SeleniumDAO.click(states);

            SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("busy")));
            WebElement busyState = SeleniumDAO.selectElementBy("id", "busy", firefoxDriver);
            SeleniumDAO.click(busyState);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            checkKPIPanel();

            String checkAgentOnCoordViewRes = checkAgentsOnCoordView();
            if(checkAgentOnCoordViewRes.contains("ERROR")) return checkAgentOnCoordViewRes;

           String checkExtensionPanelRes = checkExtensionPanel();
           if(checkExtensionPanelRes.contains("ERROR")) return checkExtensionPanelRes;

           String checkCallsPanelRes = checkCallsPanel();
           if(checkCallsPanelRes.contains("ERROR")) return checkCallsPanelRes;

           String checkOutgoingCallsPanelRes = checkOutgoingCallsPanel();
           if(checkOutgoingCallsPanelRes.contains("ERROR")) return checkOutgoingCallsPanelRes;

           String checkIncomingCallsPanelRes = checkIncomingCallsPanel();
           if(checkIncomingCallsPanelRes.contains("ERROR")) return checkIncomingCallsPanelRes;

           String checkStatusPanelRes = checkStatusPanel();
           if(checkStatusPanelRes.contains("ERROR")) return checkStatusPanelRes;


            return "Test OK. A screenshot from the KPI panel was taken, the agents appears on the coordinator view, the extension, calls, incoming calls, outgoing calls and status panel work. " +
                    "Check the folder ParteDeWebClientOut to see the taken screenshots";
        } catch (Exception e)
        {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public String dialCall(WebDriver driver, WebDriverWait waiting, String callmode, String number) throws InterruptedException
    {
        //Do a call
        Thread.sleep(1000);
        waiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("aclickToCall")));
        WebElement callButton = SeleniumDAO.selectElementBy("id", "aclickToCall", driver);
        SeleniumDAO.click(callButton);

        //Selects the callmode
        try {
            //firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@value = '" + callMode + "']")));
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("manualCampaigns")));
            Select whichCallMode = SeleniumDAO.findSelectElementBy("id", "manualCampaigns", driver);
            Thread.sleep(1000);
            whichCallMode.selectByVisibleText(callmode);
        } catch (Exception e) {
            System.out.println("Selecting the callmode: " + callmode);
            return "ERROR. The callmode selected does not exist";
        }

        SeleniumDAO.click(callButton);

        WebElement numberField = SeleniumDAO.selectElementBy("id", "clickToCallDirect", driver);
        SeleniumDAO.click(numberField);
        numberField.sendKeys(number);
        numberField.sendKeys(Keys.ENTER);

        return "";
    }

    public void selectTypology(WebDriver driver, WebDriverWait waiting, String typology, String subtypology)
    {
        driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@class = 'frmcontent']")));

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contact-aux2")));
        WebElement timeInput = SeleniumDAO.selectElementBy("id", "contact-aux2", driver);
        timeInput.sendKeys("3h");

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class = 'next']")));
        WebElement nextButton = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'next']", driver);
        SeleniumDAO.click(nextButton);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[contains(@class, 'action-field typology')]")));
        Select typologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[contains(@class, 'action-field typology')]", driver);
        typologySelector.selectByVisibleText(typology);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[contains(., '" + subtypology + "')]")));
        Select subtypologySelector = SeleniumDAO.findSelectElementBy("xpath", "//select[contains(@class, 'action-field subtypology')]", driver);
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
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = '#tabs-www']"))); //A veces no le da tiempo a encontrarlo
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

    public String doACall(WebDriver driver, WebDriverWait waiting, String callmode, String number, boolean newContact)
    {
        try
        {
            //Marcar llamada
            if(dialCall(driver, waiting, callmode, number).contains("ERROR")) return "ERROR. The callmode selected does not exist";

            waiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='contactCall']")));

            if(newContact)
            {
                //LLama creando un contacto nuevo
                WebElement callButton = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'newcontactcall']//button[@class='contactCall']", driver);
                SeleniumDAO.click(callButton);
            } else
            {
                //LLama seleccionando el contacto creado
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='contactCall']")));
                WebElement callButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '" + contactID + "')]/following-sibling::td//button[contains(@class, 'contactCall')]", driver);
                //WebElement callButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '292844')]/following-sibling::td//button[contains(@class, 'contactCall')]", driver);

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

    public String loginAndChangeShowflowInteraction(String selectValue)
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

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit-service']/a")));
            WebElement editServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit-service']/a", firefoxDriver);
            SeleniumDAO.click(editServiceTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li//a[contains(., 'Show Flow')]")));
            Thread.sleep(2000);
            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//li//a[contains(., 'Show Flow')]", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(showflowTab));
            SeleniumDAO.click(showflowTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@name = 'allownocallshowflow']")));
            Select allowNoCallShowflowSelector = SeleniumDAO.findSelectElementBy("xpath", "//select[@name = 'allownocallshowflow']", firefoxDriver);
            allowNoCallShowflowSelector.selectByValue(selectValue);

            WebElement saveButton = SeleniumDAO.selectElementBy("id", "submit", firefoxDriver);
            SeleniumDAO.click(saveButton);

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected error";
        }
    }

    public String checkOpenSFToReadOnly()
    {
        try
        {
            //TODO borrar este if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                SeleniumDAO.switchToFrame("frmcontent-", firefoxDriver);
            } else
            {
                Thread.sleep(1500);
                firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[contains(@id, 'frmcontent') and @class = 'frmcontent']")));
            }


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contact-name")));
            String disabledAttribute = SeleniumDAO.selectElementBy("id", "contact-name", firefoxDriver).getAttribute("disabled");
            if(disabledAttribute == null) return "ERROR. The Showflow on ReadOnly mode can be edited";

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //TODO borrar este if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                WebElement removeTab = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
                Thread.sleep(1000);
                SeleniumDAO.click(removeTab);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'swal2-icon swal2-warning pulse-warning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'swal2-confirm swal2-styled']", firefoxDriver);
                SeleniumDAO.click(confirmButton);
            } else
            {
                WebElement removeTab = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
                Thread.sleep(1000);
                SeleniumDAO.click(removeTab);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(confirmButton);
            }


            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String checkOpenSFEditable()
    {
        try
        {
            //TODO borrar este if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                SeleniumDAO.switchToFrame("frmcontent-", firefoxDriver);
            } else
            {
                Thread.sleep(1500);
                firefoxDriver.switchTo().frame(firefoxDriver.findElement(By.xpath("//iframe[contains(@id, 'frmcontent') and @class = 'frmcontent']")));
            }
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contact-name")));
            String disabledAttribute = SeleniumDAO.selectElementBy("id", "contact-name", firefoxDriver).getAttribute("disabled");
            if(disabledAttribute != null) return "ERROR. The Showflow on ReadOnly mode can't be edited";

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            //TODO borrar este if cuando desaparezca la version 7
            if(url.contains("8"))
            {
                WebElement removeTab = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
                Thread.sleep(1000);
                SeleniumDAO.click(removeTab);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'swal2-icon swal2-warning pulse-warning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'swal2-confirm swal2-styled']", firefoxDriver);
                SeleniumDAO.click(confirmButton);
            } else
            {
                WebElement removeTab = SeleniumDAO.selectElementBy("xpath", "//span[@class = 'ui-icon ui-icon-close']", firefoxDriver);
                Thread.sleep(1000);
                SeleniumDAO.click(removeTab);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(confirmButton);
            }

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String openSF()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, 'img/20x20/tabs/openSF')]")));
            WebElement openSFTab = SeleniumDAO.selectElementBy("xpath", "//img[contains(@src, 'img/20x20/tabs/openSF')]", firefoxDriver);
            Thread.sleep(1500);
            SeleniumDAO.click(openSFTab);

            SeleniumDAO.switchToFrame("frmcontent-openSF", firefoxDriver);
            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service")));
                Select serviceSelector = SeleniumDAO.findSelectElementBy("id", "service", firefoxDriver);
                serviceSelector.selectByValue(serviceID);
            } catch (Exception e)
            {
                SeleniumDAO.switchToDefaultContent(firefoxDriver);
                return "OK";
            }

            Select callmodeSelector = SeleniumDAO.findSelectElementBy("id", "callmode", firefoxDriver);
            callmodeSelector.selectByVisibleText(manualCallModeName);

            Select searchFieldSelector = SeleniumDAO.findSelectElementBy("id", "field", firefoxDriver);
            searchFieldSelector.selectByValue("phone");

            WebElement searchValueInput = SeleniumDAO.selectElementBy("id", "value", firefoxDriver);
            searchValueInput.sendKeys(number);

            WebElement searchButton = SeleniumDAO.selectElementBy("id", "search", firefoxDriver);
            SeleniumDAO.click(searchButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'selectableContactList']")));
            WebElement selectButton = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'selectableContactList']//button[@class = 'selectContact']", firefoxDriver);
            SeleniumDAO.click(selectButton);

            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.id("openSF")));
            WebElement openSFButton = SeleniumDAO.selectElementBy("id", "openSF", firefoxDriver);
            SeleniumDAO.click(openSFButton);

            SeleniumDAO.switchToDefaultContent(firefoxDriver);

            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The showflow search could not be done";
        }
    }

    public void checkKPIPanel()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, 'img/20x20/tabs/totalCalls')]")));
            WebElement KPIPanelTab = SeleniumDAO.selectElementBy("xpath", "//img[contains(@src, 'img/20x20/tabs/totalCalls')]", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(KPIPanelTab);

            SeleniumDAO.switchToFrame("frmcontent-kpi", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sub-panel-body']")));
            Utils.takeScreenshot("./ParteDeWebClientOut/KPIonCoordinator", firefoxDriver);
            SeleniumDAO.switchToDefaultContent(firefoxDriver);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String checkAgentsOnCoordView()
    {
        try
        {
            //Se conecta el agente 4 para comprobar que le aparece al coordinador
            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 60);
            connectAndAvailable(chromeDriver, chromeWaiting, agentName4, extension2);


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = '#coordUsers']")));
            WebElement extensionPanel = SeleniumDAO.selectElementBy("xpath", "//a[@href = '#coordUsers']", firefoxDriver);
            SeleniumDAO.click(extensionPanel);

            Thread.sleep(2000);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'coord-status-" + agentName4 + "' and @class = 'available']")));

            Utils.logoutWebClient(chromeWaiting, chromeDriver);

            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The agent does not appear on the coordinator view";
        }
        finally
        {
            chromeDriver.close();
        }
    }

    //Para comprobar el funcionamiento, unicamente el agente7 esta asignado al modo de llamada de transferencia. SI aparece en cualquier otro modo, el test falla.
    public String checkExtensionPanel()
    {
        try
        {
            //Conectar al agente 7 que que solo esta en el modo de llamada de transferencia para comprobar los filtros
            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 60);
            connectAndAvailable(chromeDriver, chromeWaiting, agentName7, extension2);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'select-service-users']//option[@value = '" + serviceID + "']")));
            Select serviceSelector = SeleniumDAO.findSelectElementBy("id", "select-service-users", firefoxDriver);
            serviceSelector.selectByValue(serviceID);

            Select callmodeSelector = SeleniumDAO.findSelectElementBy("id","select-campaigns-users", firefoxDriver);
            callmodeSelector.selectByVisibleText(transferCallModeName);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'coord-status-" + agentName7 + "']")));
            } catch(Exception e)
            {
                e.printStackTrace();
                return "ERROR. The extension panel does not work correctly";
            }

            callmodeSelector.selectByVisibleText(manualCallModeName);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'coord-status-" + agentName7 + "']")));
                return "ERROR. The extension panel does not work correctly";
            } catch(Exception e)
            { }

            Utils.logoutWebClient(chromeWaiting, chromeDriver);
            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        } finally {
            chromeDriver.close();
        }
    }

    public String checkCallsPanel()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, 'img/20x20/tabs/outgoing.png')]")));
            WebElement callsPanelTab = SeleniumDAO.selectElementBy("xpath", "//img[contains(@src, 'img/20x20/tabs/outgoing.png')]", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(callsPanelTab));
            SeleniumDAO.click(callsPanelTab);

            //Esta espera es debido a que tarda un poco en cargar los datos en la tabla (mas que firefoxWaiting)
            WebDriverWait auxiliarWaiting = new WebDriverWait(firefoxDriver, 15);

            auxiliarWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + manualCallModeName + "')]/following-sibling::td[@class = 'highlight']")));
            auxiliarWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + incomingCallModeName + "')]/following-sibling::td[@class = 'highlight']")));

            Utils.takeScreenshot("./ParteDeWebClientOut/CallsPanelScreenshot", firefoxDriver);

            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The calls panel does not work correctly";
        }
    }

    public String checkOutgoingCallsPanel() {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, 'img/20x20/tabs/outgoing2.png')]")));
            WebElement outgoingCallsPanel = SeleniumDAO.selectElementBy("xpath", "//img[contains(@src, 'img/20x20/tabs/outgoing2.png')]", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(outgoingCallsPanel));
            SeleniumDAO.click(outgoingCallsPanel);

            //Espera a que se carguen las graficas
            Thread.sleep(2000);
            Utils.takeScreenshot("./ParteDeWebClientOut/OutgoingPanel1", firefoxDriver);

            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 60);
            connectAndAvailable(chromeDriver, chromeWaiting, agentName4, extension2);

            Utils.takeScreenshot("./ParteDeWebClientOut/OutgoingPanel2", firefoxDriver);

            doACall(chromeDriver, chromeWaiting, manualCallModeName, number, false);

            Utils.takeScreenshot("./ParteDeWebClientOut/OutgoingPanel3", firefoxDriver);
            Utils.logoutWebClient(chromeWaiting, chromeDriver);
            return "";

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The outgoing calls panel could not be checked";
        } finally {
            chromeDriver.close();
        }

    }

    public String checkIncomingCallsPanel()
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, 'img/20x20/tabs/incoming2.png')]")));
            WebElement incomingCallsPanel = SeleniumDAO.selectElementBy("xpath", "//img[contains(@src, 'img/20x20/tabs/incoming2.png')]", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(incomingCallsPanel));
            SeleniumDAO.click(incomingCallsPanel);

            //Espera a que se carguen las graficas
            Thread.sleep(2000);
            Utils.takeScreenshot("./ParteDeWebClientOut/incomingPanel1", firefoxDriver);

            chromeDriver = DriversConfig.headlessOrNot(headless, chromePath);
            chromeWaiting = new WebDriverWait(chromeDriver, 60);
            connectAndAvailable(chromeDriver, chromeWaiting, agentName4, extension2);

            Utils.takeScreenshot("./ParteDeWebClientOut/incomingPanel2", firefoxDriver);
            WebDriverWait waitingIncomingCall = new WebDriverWait(chromeDriver, 600);
            System.out.println("You must do a call to the number: " + DDI);
            waitingIncomingCall.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@role = 'presentation' and contains(., '" + number + "')]")));

            System.out.println("You can hang up");

            Utils.takeScreenshot("./ParteDeWebClientOut/incomingPanel3", firefoxDriver);

            Utils.logoutWebClient(chromeWaiting, chromeDriver);

            return "";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The incoming calls panel could not be checked";
        } finally
        {
            chromeDriver.close();
        }
    }

    public String checkStatusPanel()
    {
        try
        {
            Thread.sleep(2000);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, 'img/20x20/tabs/database2.png')]")));
            WebElement statusPanel = SeleniumDAO.selectElementBy("xpath", "//img[contains(@src, 'img/20x20/tabs/database2.png')]", firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(statusPanel));
            SeleniumDAO.click(statusPanel);

            Thread.sleep(9000);

            WebDriverWait auxWait = new WebDriverWait(firefoxDriver, 20);

            auxWait.until(ExpectedConditions.presenceOfElementLocated(By.id("select-service-state")));
            Select serviceStateSelector = SeleniumDAO.findSelectElementBy("id", "select-service-state", firefoxDriver);
            auxWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'select-service-state']//option[@value = '" + serviceID + "']")));
            serviceStateSelector.selectByValue(serviceID);

            Thread.sleep(2000);
            Utils.takeScreenshot("./ParteDeWebClientOut/statusPanel", firefoxDriver);

            Utils.logoutWebClient(firefoxWaiting, firefoxDriver);

            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The status panel could not be checked";
        }
    }
}
