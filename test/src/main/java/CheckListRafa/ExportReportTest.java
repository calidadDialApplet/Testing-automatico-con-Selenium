package CheckListRafa;

import main.Utils;
import main.SeleniumDAO;
import org.ini4j.Wini;
import Utils.TestWithConfig;
import Utils.DriversConfig;
import Utils.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ExportReportTest extends TestWithConfig {
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

    public ExportReportTest(Wini ini) {
        super(ini);
    }

    @Override
    public HashMap<String, String> check() {
        try {
            //Load inicializacion settings
            try {
                adminName = ini.get("Admin", "adminName");
                adminPassword = ini.get("Admin", "adminPassword");
                url = ini.get("Red", "url");
                headless = ini.get("Red", "headless");
                serviceID = ini.get("Service", "serviceID");
                dateSearch = ini.get("Service", "dateSearch");
                //reportName = ini.get("Service", "reportName"); //TODO Para el to:do de mas abajo
                reportID = ini.get("Service", "reportID");
                reportDownloadPath = ini.get("Service", "reportDownloadPath");
            } catch (Exception e) {
                results.put(e.toString() + "\nERROR. The inicialization file can't be loaded", "Tests can't be runned");
                return results;
            }

            firefoxDriver = DriversConfig.noDownloadPopUp(headless);

            firefoxDriver.get(url + "dialapplet-web");
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
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


            results.put("--Export csv report test  -->  ", exportReportTest());


            Thread.sleep(5000);

            return results;

        } catch (InterruptedException e) {
            results.put("--Export csv report test  -->  ", e.toString() + "ERROR. Unexpected exception");
            return results;
        } finally
        {
            firefoxDriver.close();
        }
    }


    public String exportReportTest()
    {
        try
        {
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
            SeleniumDAO.click(exportButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("tabs-export")));

            //Selects the date search
            Select dateSelector = SeleniumDAO.findSelectElementBy("id", "select_hour_date", firefoxDriver);
            dateSelector.selectByValue(dateSearch);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("trigger_start_date_hour")));
            WebElement startDate = SeleniumDAO.selectElementBy("id", "trigger_start_date_hour", firefoxDriver);
            SeleniumDAO.click(startDate);

            WebElement previousMonthButton = SeleniumDAO.selectElementBy("xpath", "//tr[@class = 'headrow']/td[contains(., '‹')]", firefoxDriver);
            SeleniumDAO.click(previousMonthButton);
            SeleniumDAO.click(previousMonthButton);

            //Clicks on download button
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@id = 'download-" + reportID + "']")));
            WebElement downloadButton = SeleniumDAO.selectElementBy("xpath","//img[@id = 'download-" + reportID + "']", firefoxDriver);
            SeleniumDAO.click(downloadButton);

            //Waits until the report exists
            /*File tempFile = new File( "./contactsqualification.csv");
            boolean aux = Files.deleteIfExists(tempFile.toPath()); //Deletes the file if already exists
            //Wait for the download to finish
            int i = 0;
            for(; i < 100; i++){
                if(tempFile.exists()){break;}
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

            if(i == 100)
            {
                return "ERROR. Download failed";
            }*/

            String downloadStatus = File.waitToDownload("./contactsqualification.csv", 100);
            if(downloadStatus.contains("ERROR")) return downloadStatus;

            //Checks the format of the csv downloaded
            try {
                String archCSV = "./contactsqualification.csv";
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
                    return "ERROR. Theres no column with date name";
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
