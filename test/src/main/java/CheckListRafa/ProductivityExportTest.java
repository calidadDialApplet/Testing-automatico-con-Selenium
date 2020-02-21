package CheckListRafa;

import exceptions.MissingParameterException;
import main.SeleniumDAO;
import org.ini4j.Wini;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import Utils.File;
import Utils.Utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.*;

public class ProductivityExportTest extends TestWithConfig {
    static String adminName;
    static String adminPassword;
    static String url;
    static String headless;
    static String serviceID;
    static String dateSearch;
    //static String reportName;
    static String reportID;
    static String reportDownloadPath;
    static FirefoxDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;
    static List<WebElement> graphList;

    HashMap<String, String> results = new HashMap<>();

    public ProductivityExportTest(Wini ini) {
        super(ini);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID", "dateSearch")));

        return requiredParameters;
    }

    @Override
    public HashMap<String, String> check() throws MissingParameterException {
        super.checkParameters();
        try {
            //Load inicializacion settings
            try {
                adminName = commonIni.get("Admin", "adminName");
                adminPassword = commonIni.get("Admin", "adminPassword");
                url = commonIni.get("General", "url");
                headless = commonIni.get("General", "headless");
                serviceID = commonIni.get("Service", "serviceID");
                dateSearch = commonIni.get("Service", "dateSearch");
                //reportName = ini.get("Service", "reportName"); //TODO Para el to:do de mas abajo
            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.noDownloadPopUp(headless);

            firefoxDriver.get(url + "dialapplet-web");
            firefoxWaiting = new WebDriverWait(firefoxDriver, 6);


            //TODO: En el futuro se podra crear el reporte en vez de descargar uno ya creado
            /*
            //Introduce the name of the exportReport
            WebElement inputReportName = SeleniumDAO.selectElementBy("id", "configuration-name", firefoxDriver);
            SeleniumDAO.click(inputReportName);
            inputReportName.sendKeys(reportName);

            WebElement selectAll = SeleniumDAO.selectElementBy("xpath", "//label/input[@value = '1']", firefoxDriver);
            SeleniumDAO.click(selectAll);

            try
            {
                Thread.sleep(500);
            } catch (Exception e){}

            WebElement createReportButton = SeleniumDAO.selectElementBy("id", "create-configuration", firefoxDriver);
            SeleniumDAO.click(createReportButton);*/


            results.put("--Productivity report test  -->  ", productivityExport());


            Thread.sleep(5000);

            return results;

        } catch (InterruptedException e) {
            results.put("--Productivity report test  -->  ", e.toString() + "ERROR. Unexpected exception");
            return results;
        } finally
        {
            firefoxDriver.close();
        }
    }


    public String productivityExport()
    {
        try
        {
            //Login
            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
            String loginStatus = Utils.checkCorrectLoginDialappletWeb(firefoxDriver);
            if(loginStatus.contains("ERROR")) return loginStatus;
            //Searchs the service
            try
            {
                WebElement searchField = SeleniumDAO.selectElementBy("id", "search", firefoxDriver);
                searchField.sendKeys(serviceID);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service-" + serviceID)));
                WebElement service = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'service-" + serviceID + "']/td", firefoxDriver);
                SeleniumDAO.click(service);
            } catch(Exception e)
            {
                return e.toString() + "ERROR. There is no service with the ID " + serviceID;
            }

            //Wait to click on productivity
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service_productivity")));
            WebElement productivity = SeleniumDAO.selectElementBy("id", "service_productivity", firefoxDriver);
            SeleniumDAO.click(productivity);

            Thread.sleep(2000);

            //Clicks on export tab
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs")));
            WebElement exportButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = '#tabs-export']", firefoxDriver);
            SeleniumDAO.click(exportButton);
            SeleniumDAO.click(exportButton); //This second click is to fix an issue on the page in which you click on export but dont work

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs-export")));

            //Selects the type of the report
            Select selectReport = SeleniumDAO.findSelectElementBy("id", "select_report", firefoxDriver);
            selectReport.selectByValue("12");

            //Selects the date search
            Select dateSelector = SeleniumDAO.findSelectElementBy("id", "select_hour_date", firefoxDriver);
            dateSelector.selectByValue(dateSearch);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("trigger_start_date_hour")));
            WebElement startDate = SeleniumDAO.selectElementBy("id", "trigger_start_date_hour", firefoxDriver);
            SeleniumDAO.click(startDate);

            //Searchs 2 month before
            WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
            SeleniumDAO.click(previousMonthButton);
            SeleniumDAO.click(previousMonthButton);

            //Clicks on download button of the first report of the table
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'table-configurations']")));
            List<WebElement> tableElements = firefoxDriver.findElementsByXPath("//table[@id = 'table-configurations']//tbody");
            WebElement downloadButton = tableElements.get(0).findElement(By.xpath("//img[@title = 'Download report']"));
            SeleniumDAO.click(downloadButton);

            //Deletes the file if it exists
            File.deleteExistingFile("./callsDetail.csv");

            String downloadStatus = File.waitToDownload("./callsDetail.csv", 100);

            if(downloadStatus.contains("ERROR")) return downloadStatus;

            //Checks the format of the csv downloaded
            try {
                String archCSV = "./callsDetail.csv";
                BufferedReader reader = new BufferedReader(new FileReader(archCSV));
                reader.readLine();
                String line = reader.readLine();
                ArrayList<String> firstRow = new ArrayList<>(Arrays.asList(line.split(";",0)));
                int columnDate = -1;

                for(int j = 0; j < firstRow.size(); j++)
                {
                    if(firstRow.get(j).toLowerCase().contains("date") || firstRow.get(j).toLowerCase().contains("fecha"))
                    {
                        columnDate = j;
                        break;
                    }
                }

                if(columnDate == -1) {
                    return "ERROR. There's no column with date name. You should check the file manually to ensure if the error is real";
                }


                boolean res = true;
                while ((line = reader.readLine()) != null) {
                    String[] splitedLine = line.split(";",100);
                    if(splitedLine[columnDate].matches("(\"\")|(\"\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?\")"));
                    else{
                        res = false;
                        break;
                    }
                }
                if(res)
                {
                    System.out.println("The format of the dates matches with the column dates");
                } else
                {
                    System.err.println("The dates does not match with de column dates");
                }


            } catch (FileNotFoundException e)
            {
                return e.toString() + "ERROR. The csv downloaded has not been found";
            } catch (IOException e)
            {
                return e.toString() + "ERROR. Unexpected IOexception";
            }
            return "Test OK. The csv was downloaded and his format has been checked";

        } catch(Exception e)
        {
            return e.toString() + "ERROR. Unexpected exception";
        }
    }
}
