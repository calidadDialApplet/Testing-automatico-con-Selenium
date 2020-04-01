package ProcedimientoRC;

import Utils.DriversConfig;
import Utils.TestWithConfig;
import Utils.Utils;
import exceptions.MissingParameterException;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.SeleniumDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParteDeSocialmedia extends TestWithConfig {

    static String url;
    static String headless;
    static String adminName;
    static String adminPassword;
    static String showflowSMName;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();


    public ParteDeSocialmedia(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("ShowflowSM", new ArrayList<>(Arrays.asList("showflowSMName")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws Exception {
        super.checkParameters();

        try
        {
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");
            showflowSMName = commonIni.get("ShowflowSM", "showflowSMName");

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            results.put("Create a showflow ticket and check the states  ->  ", newShowflowTicket());
            results.put("Create action fields  ->  ", createActionFields());

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

    public String newShowflowTicket()
    {
        try
        {
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);

            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                //System.err.println("ERROR: Login failed");
                return e.toString() + "\n ERROR: Login failed";
            }


            String createShowflowRes = Utils.createShowflow(firefoxDriver, firefoxWaiting, showflowSMName, "ticket");
            if(!createShowflowRes.contains("ERROR"))
            {
                String checkShowflowStatesRes = checkShowflowStates();
                if(checkShowflowStatesRes.contains("ERROR")) return checkShowflowStatesRes;
                else return "Test OK. The ticket showflow was created";
            }
            else return createShowflowRes;

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    public String createActionFields()
    {
        try
        {
            String createActionFieldsRes = ParteDeShowflowTest.activateShowflowFields();
            if (createActionFieldsRes.contains("ERROR")) return createActionFieldsRes;

            return "Test OK.";
        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

    //Auxiliar methods
    public String checkShowflowStates()
    {
        try
        {
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowSMName + "')]", firefoxDriver);
            SeleniumDAO.click(showflow);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, 'actionFields.php')]")));
            WebElement actionFieldsTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(@href, 'actionFields.php')]", firefoxDriver);
            SeleniumDAO.click(actionFieldsTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/config.png']")));
            WebElement statesConfigButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/config.png']", firefoxDriver);
            SeleniumDAO.click(statesConfigButton);

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@class = 'statusInitial']/td/following-sibling::td//option[contains(., 'Open')]")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = 'Open']")));
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = 'Close']")));
            } catch (Exception e)
            {
                e.printStackTrace();
                return "ERROR. The states have not been created";
            }
            return "";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }
}
