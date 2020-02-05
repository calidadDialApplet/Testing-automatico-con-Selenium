package CheckListRafa;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import main.Main;
import main.SeleniumDAO;
import org.ini4j.Wini;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExportReportTest extends Test {
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

    @Override
    public void check() {
        try {
            //Load inicializacion settings
            try {
                Wini ini = new Wini(new File("InicializationSettings.ini"));
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                serviceID = ini.get("Service", "serviceID");
                dateSearch = ini.get("Service", "dateSearch");
                //reportName = ini.get("Service", "reportName"); //TODO Para el to:do de mas abajo
                reportID = ini.get("Service", "reportID");
                reportDownloadPath = ini.get("Service", "reportDownloadPath");
            } catch (IOException e) {
                System.err.println("The inicialization file can't be loaded");
                e.printStackTrace();
                return;
            }

            firefoxDriver = noDownloadPopUp(headless, reportDownloadPath);

            firefoxDriver.get(url + "dialapplet-web");
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            Main.loginDialappletWeb(adminName, adminPassword, firefoxDriver);


            if(!searchService()) return;

            //Wait to click on productivity
            clickOnProductivity();

            try
            {
                Thread.sleep(2000);
            } catch (Exception e){}

            clickOnExport();

            try
            {
                Thread.sleep(2000);
            } catch (Exception e){}

            //Wait until the table is shown
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs-export")));

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

            selectDateSearch();
            clickOnDownload();
            if (!waitUntilReportExists()) return;

            if(!checkCSVFormat()) return;

            //Wait 5 sec for avoiding that the driver closes too fast
            try
            {
                Thread.sleep(5000);
            } catch (Exception e){}

        } finally
        {
            firefoxDriver.close();
        }
    }

    public boolean waitUntilReportExists()
    {
        File tempFile = new File(reportDownloadPath + "/contactsqualification.csv");
        //Wait for the download to finish
        int i = 0;
        for(; i < 100; i++){
            if(tempFile.exists()){return true;}
            else
            {
                try
                {
                    System.out.println("Waiting the download to finish");
                    Thread.sleep(1000);

                } catch(Exception e)
                { }
            }
        }

        System.err.println("Download failed");
        return false;
    }
    public boolean searchService()
    {
        try
        {
            WebElement searchField = SeleniumDAO.selectElementBy("id", "search", firefoxDriver);
            searchField.sendKeys(serviceID);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service-" + serviceID)));
            WebElement service = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'service-" + serviceID + "']/td", firefoxDriver);
            SeleniumDAO.click(service);
        } catch(Exception e)
        {
            System.out.println("There is no service with the ID " + serviceID);
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void clickOnProductivity()
    {
        //Wait to click on productivity
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("service_productivity")));
        WebElement productivity = SeleniumDAO.selectElementBy("id", "service_productivity", firefoxDriver);
        SeleniumDAO.click(productivity);
    }
    public void clickOnExport()
    {
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs")));
        WebElement exportButton = SeleniumDAO.selectElementBy("xpath", "//a[@href = '#tabs-export']", firefoxDriver);
        SeleniumDAO.click(exportButton);
        SeleniumDAO.click(exportButton);
    }
    public void selectDateSearch()
    {
        Select dateSelector = SeleniumDAO.findSelectElementBy("id", "select_hour_date", firefoxDriver);
        dateSelector.selectByValue(dateSearch);

        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("trigger_start_date_hour")));
        WebElement startDate = SeleniumDAO.selectElementBy("id", "trigger_start_date_hour", firefoxDriver);
        SeleniumDAO.click(startDate);

        WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
        SeleniumDAO.click(previousMonthButton);
        SeleniumDAO.click(previousMonthButton);
    }
    public void clickOnDownload()
    {
        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@id = 'download-" + reportID + "']")));
        WebElement downloadButton = SeleniumDAO.selectElementBy("xpath","//img[@id = 'download-" + reportID + "']", firefoxDriver);
        SeleniumDAO.click(downloadButton);
    }
    public boolean checkCSVFormat()
    {
        try {
            String archCSV = reportDownloadPath +"/contactsqualification.csv";
            BufferedReader reader = new BufferedReader(new FileReader(archCSV));
            reader.readLine();
            String line = reader.readLine();
            ArrayList<String> firstRow = new ArrayList<>(Arrays.asList(line.split(";",0)));
            ArrayList<String> dateColumns = new ArrayList<>();
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
                System.err.println("Theres no column with date name");
                return false;
            }


            boolean res = true;
            while ((line = reader.readLine()) != null) {
                String[] splitedLine = line.split(";",100);
                //System.out.println(splitedLine[columnDate]);

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
            System.err.println("The csv downloaded has not been found");
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
