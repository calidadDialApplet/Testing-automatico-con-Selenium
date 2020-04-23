package ProcedimientoRC.ParteDeShowflow;

import ProcedimientoRC.ParteDeAgentes.CleanParteDeAgentes;
import Utils.CleanTest;
import Utils.DriversConfig;
import Utils.Utils;
import exceptions.MissingParameterException;
import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CleanParteDeShowflow extends CleanTest {
    static String url;
    static String headless;
    static String superAdminName;
    static String superAdminPassword;

    static String showflowName;
    static String showflowCopyName;


    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;
    public CleanParteDeShowflow(Wini commonIni)
    {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("superAdminName", "superAdminPassword")));
        requiredParameters.put("Showflow", new ArrayList<>(Arrays.asList("showflowName", "showflowCopyName")));


        return requiredParameters;
    }

    @Override
    public void clean() throws MissingParameterException {
        super.checkParameters();

        try
        {
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");
            superAdminName = commonIni.get("Admin", "superAdminName");
            superAdminPassword = commonIni.get("Admin", "superAdminPassword");

            showflowName = commonIni.get("Showflow", "showflowName");
            showflowCopyName = commonIni.get("Showflow", "showflowCopyName");


            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);

            List<String> showflowsToClean = new ArrayList<String>(){{
                add(showflowCopyName);
                add(showflowName);
            }};

            firefoxDriver.get(url + "/dialapplet-web");

            Utils.loginDialappletWeb(superAdminName, superAdminPassword, firefoxDriver);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));

            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", firefoxDriver);
            SeleniumDAO.click(showflowTab);

            for(String showflowName : showflowsToClean)
            {
                try
                {
                    WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                    searcher.clear();
                    searcher.sendKeys(showflowName);
                    Thread.sleep(1000);
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]" +
                            "/following-sibling::td/a[@class = 'delete']")));
                    Thread.sleep(3000);
                    WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]" +
                            "/following-sibling::td/a[@class = 'delete']", firefoxDriver);
                    firefoxWaiting.until(ExpectedConditions.visibilityOf(deleteButton));
                    SeleniumDAO.click(deleteButton);

                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.className("sa-confirm-button-container")));
                    WebElement yesDeleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                    Thread.sleep(500);
                    SeleniumDAO.click(yesDeleteButton);
                    Thread.sleep(500);
                    SeleniumDAO.click(yesDeleteButton);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Clean ERROR. The Showflow could not be deleted");
                }

            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            firefoxDriver.close();
        }
    }




}
