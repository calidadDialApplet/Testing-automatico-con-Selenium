package CheckListRafa;

import main.SeleniumDAO;
import Utils.DriversConfig;
import Utils.TestWithConfig;
import Utils.Utils;
import Utils.File;
import Utils.Color;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OldRecordDownloadTest extends TestWithConfig {
    static String adminName;
    static String adminPassword;
    static String companyID;
    static String serviceID;
    static String url;
    static String headless;

    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    HashMap<String, String> results = new HashMap<>();

    public OldRecordDownloadTest(Wini commonIni){
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Company", new ArrayList<>(Arrays.asList("companyID")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws Exception {
        super.checkParameters();
        try
        {
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");
            companyID = commonIni.get("Company", "companyID");
            serviceID = commonIni.get("Service", "serviceID");
            url = commonIni.get("General", "url");
            headless = commonIni.get("General", "headless");


            firefoxDriver = DriversConfig.noDownloadPopUp(headless, "OldRecordDownloadTestOut");
            firefoxWaiting = new WebDriverWait(firefoxDriver, 5);

            firefoxDriver.get(url + "dialapplet-web");

            results.put("--Old record download test  ->  ", recordDownload());

            return results;

        } catch (Exception e)
        {
            return results;
        } finally
        {
            firefoxDriver.close();
        }
    }

    public String recordDownload() {
        try
        {
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            String loginStatus = Utils.checkCorrectLoginDialappletWeb(firefoxDriver);
            if(loginStatus.contains("ERROR")) return loginStatus;

            File.deleteExistingFileByExtension("wav", "OldRecordDownloadTestOut");


            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("REPORTS")));
            WebElement analyzeTab = SeleniumDAO.selectElementBy("id", "REPORTS", firefoxDriver);
            SeleniumDAO.click(analyzeTab);

            Select company = SeleniumDAO.findSelectElementBy("id", "companySelect", firefoxDriver);
            company.selectByValue(companyID);
            Select service = SeleniumDAO.findSelectElementBy("id", "serviceSelect", firefoxDriver);
            service.selectByValue(serviceID);

            System.out.println("Trying to download old record");

            WebElement startDate = SeleniumDAO.selectElementBy("id", "f_trigger_start", firefoxDriver);
            SeleniumDAO.click(startDate);

            WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
            SeleniumDAO.click(previousMonthButton);
            SeleniumDAO.click(previousMonthButton); //Between Last 2 months

            WebElement endDate = SeleniumDAO.selectElementBy("id", "f_trigger_end", firefoxDriver);
            SeleniumDAO.click(endDate);

            SeleniumDAO.click(previousMonthButton); //and Last month

            WebElement exitCalendarButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., '×')]", firefoxDriver);
            SeleniumDAO.click(exitCalendarButton);

            WebElement showButton = SeleniumDAO.selectElementBy("id","submit", firefoxDriver);
            SeleniumDAO.click(showButton);

            //Try to download an old record
            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class = 'tabla-principal']//tbody/tr[1]")));
                WebElement saveRecord = SeleniumDAO.selectElementBy("xpath", "//table[@class = 'tabla-principal']//tbody/tr[1]//img[contains(@title, 'WAV')]", firefoxDriver);
                SeleniumDAO.click(saveRecord);
                Thread.sleep(2000);

            } catch (Exception e)
            {
                return e.toString() + "\nWARNING. No call appears in the chosen date range, or the chosen call was not recorded";
            }

            String downloadState = File.waitToDownloadByExtension("wav", "OldRecordDownloadTestOut", 100);
            if(downloadState.contains("ERROR")) return downloadState;
            else return "Test OK. The old record was downloaded.";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }


    }
}
