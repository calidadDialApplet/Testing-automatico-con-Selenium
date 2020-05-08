package ProcedimientoRC;

import main.SeleniumDAO;
import Utils.DriversConfig;
import Utils.TestWithConfig;
import org.ini4j.Wini;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Array;
import java.nio.channels.SelectionKey;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParteDeWebclientSocialmediaTest extends TestWithConfig {

    private String chatURL;
    private String headless;
    private String chatChannelName;

    private WebDriver firefoxDriver;
    private WebDriverWait firefoxWaiting;

    private HashMap<String, String> results = new HashMap<>();

    public ParteDeWebclientSocialmediaTest(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("Webchat", new ArrayList<>(Arrays.asList("chatURL")));
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("headless")));
        requiredParameters.put("Channel", new ArrayList<>(Arrays.asList("chatChannelName")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws Exception {
        super.checkParameters();

        chatURL = commonIni.get("Webchat", "chatURL");
        headless = commonIni.get("General", "headless");
        chatChannelName = commonIni.get("Channel", "chatChannelName");

        try
        {
            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            results.put("Check the contact fields of the service  ->  ", checkContactFields());
            results.put("Check you could open conversation  ->  ", checkOpenConversation());

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

    public String checkContactFields()
    {
        try
        {
            firefoxDriver.get(chatURL);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("bubbleMessage-tecsible")));
            WebElement chatIcon = SeleniumDAO.selectElementBy("id", "bubbleMessage-tecsible", firefoxDriver);
            SeleniumDAO.click(chatIcon);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("select-channel-tecsible")));
            WebElement selectButton = SeleniumDAO.selectElementBy("id", "sendChannel-tecsible", firefoxDriver);
            SeleniumDAO.click(selectButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder = 'Name *']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder = 'City']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder = 'e-Mail *']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder = 'Tiempo']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@placeholder = 'auxTextArea']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder = 'auxDate']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'aux5']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@id = 'aux6']")));


            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The contact fields do not appear correctly";
            }


            return "Test OK. The contact fields appears correctly";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String checkOpenConversation(){
        try
        {

            WebElement nameInput = SeleniumDAO.selectElementBy("xpath", "//input[@placeholder = 'Name *']", firefoxDriver);
            WebElement cityInput = SeleniumDAO.selectElementBy("xpath", "//input[@placeholder = 'City']", firefoxDriver);
            WebElement emailInput = SeleniumDAO.selectElementBy("xpath", "//input[@placeholder = 'e-Mail *']", firefoxDriver);
            WebElement tiempoInput = SeleniumDAO.selectElementBy("xpath", "//input[@placeholder = 'Tiempo']", firefoxDriver);
            WebElement auxTextArea = SeleniumDAO.selectElementBy("xpath", "//textarea[@placeholder = 'auxTextArea']", firefoxDriver);
            WebElement auxDate = SeleniumDAO.selectElementBy("xpath", "//input[@placeholder = 'auxDate']", firefoxDriver);
            Select aux5Selector = SeleniumDAO.findSelectElementBy("id", "aux5", firefoxDriver);
            Select aux6Selector = SeleniumDAO.findSelectElementBy("id", "aux6", firefoxDriver);

            nameInput.sendKeys("Sebas");
            cityInput.sendKeys("Valencia");
            emailInput.sendKeys("sebas@sebas.com");
            tiempoInput.sendKeys("hola");
            auxTextArea.sendKeys("hola hola hola hola");
            auxDate.sendKeys("22/05/2020");
            aux5Selector.selectByVisibleText("No");
            aux6Selector.selectByVisibleText("Tal vez");

            WebElement openChatButton = SeleniumDAO.selectElementBy("id", "sendContactData-tecsible", firefoxDriver);
            SeleniumDAO.click(openChatButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("chatMessage-tecsible")));
            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. Could not open the chat";
            }

            try
            {
                WebElement closeButton = SeleniumDAO.selectElementBy("id", "closeChatBtn-tecsible", firefoxDriver);
                SeleniumDAO.click(closeButton);
            } catch (Exception e)
            {
                Alert alert = firefoxDriver.switchTo().alert();
                alert.accept();
            }


            /*Thread.sleep(500);
            Actions actions = new Actions(firefoxDriver);
            actions.sendKeys(Keys.ENTER).perform();
            Thread.sleep(500);*/



            WebElement closeButton = SeleniumDAO.selectElementBy("id", "closeChatBtn-tecsible", firefoxDriver);
            SeleniumDAO.click(closeButton);




            return "Test OK.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }
}
