package ProcedimientoRC.ParteDeServicio;

import Utils.CleanTest;
import Utils.DriversConfig;
import Utils.Utils;
import exceptions.MissingParameterException;
import main.SeleniumDAO;
import org.ini4j.Wini;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CleanParteDeServicio extends CleanTest {
    static String url;
    static String headless;
    static String superAdminName;
    static String superAdminPassword;
    static String adminName;
    static String adminPassword;
    static String serviceID;
    static String serviceCopyName;
    static String showflowCopyName;
    static String queueName;
    static String incomingCallModeName;



    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    public CleanParteDeServicio(Wini commonIni) {
        super(commonIni);
    }

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("superAdminName", "superAdminPassword", "adminName", "adminPassword")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID", "serviceCopyName")));
        requiredParameters.put("Showflow", new ArrayList<>(Arrays.asList("showflowCopyName")));
        requiredParameters.put("Queue", new ArrayList<>(Arrays.asList("queueName")));
        requiredParameters.put("CallMode", new ArrayList<>(Arrays.asList("incomingCallModeName")));

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
            adminName = commonIni.get("Admin", "adminName");
            adminPassword = commonIni.get("Admin", "adminPassword");
            serviceID = commonIni.get("Service", "serviceID");
            serviceCopyName = commonIni.get("Service", "serviceCopyName");
            showflowCopyName = commonIni.get("Showflow", "showflowCopyName");
            queueName = commonIni.get("Queue", "queueName");
            incomingCallModeName = commonIni.get("CallMode", "incomingCallModeName");

            firefoxDriver = DriversConfig.headlessOrNot(headless);
            firefoxWaiting = new WebDriverWait(firefoxDriver, 10);

            //deleteContacts();
            deleteServiceCopy();
            deleteShowflowCopy();
            revertServiceConfiguration();

        } catch (Exception e)
        {

        } finally
        {
            firefoxDriver.close();
        }
    }

    //TODO BOrrar en el futuro, esto debe ir en el CleanParteDEWebclient
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

    public void deleteServiceCopy() throws InterruptedException
    {
        try
        {
            //Login on dialapplet web
            firefoxDriver.get(url + "dialapplet-web");
            Utils.loginDialappletWeb(superAdminName, superAdminPassword, firefoxDriver);
            try {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
            } catch (Exception e) {
                System.err.println("Clean ERROR: Login failed");
                e.printStackTrace();
                throw e;
            }

            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceCopyName);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceCopyName + "')]")));
            } catch (Exception e) {
                System.out.println("Clean ERROR: The service: " + serviceCopyName + "does not appears on the services table");
                e.printStackTrace();
                throw e;
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceCopyName + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'delete-service']/a")));
            WebElement deleteServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'delete-service']/a", firefoxDriver);
            SeleniumDAO.click(deleteServiceTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            Thread.sleep(3000);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            SeleniumDAO.click(okButton);
        } catch (Exception e)
        {
            System.err.println("Clean ERROR trying to delete the cloned service");
            e.printStackTrace();
        }
    }
    public void deleteShowflowCopy() throws  InterruptedException
    {
        try
        {
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id = 'SHOWFLOW']/a")));
            WebElement showflowsTab = SeleniumDAO.selectElementBy("xpath", "//li[@id = 'SHOWFLOW']/a", firefoxDriver);
            SeleniumDAO.click(showflowsTab);

            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(showflowCopyName + "forService");
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]")));
            } catch (Exception e) {
                System.out.println("Clean ERROR: The showflow copy was not found");
                e.printStackTrace();
                throw e;
            }
            Thread.sleep(1500);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowCopyName +
                    "forService')]/following-sibling::td//img[@src = 'imagenes/delete2.png']")));
            WebElement showflowDeleteButton = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowCopyName +
                    "forService')]/following-sibling::td//img[@src = 'imagenes/delete2.png']", firefoxDriver);
            Thread.sleep(1000);
            SeleniumDAO.click(showflowDeleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
            WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(deleteButton);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(250);
            SeleniumDAO.click(okButton);

        } catch (Exception e)
        {
            System.err.println("Clean ERROR. The showflow copy could not be deleted");
            e.printStackTrace();
        }
    }



    /*public void deleteQueues() throws InterruptedException {

        //Login on dialapplet web
        firefoxDriver.get(url + "dialapplet-web");
        Utils.loginDialappletWeb(adminName, adminPassword, firefoxDriver);
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
        } catch (Exception e) {
            System.err.println("Clean ERROR: Login failed");
            e.printStackTrace();
            throw e;
        }

        try
        {
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id = 'ACD']/a")));
            WebElement acdTab = SeleniumDAO.selectElementBy("xpath", "//li[@id = 'ACD']/a", firefoxDriver);
            SeleniumDAO.click(acdTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/apps/server-database.png']")));
            WebElement queuesButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/apps/server-database.png']", firefoxDriver);
            SeleniumDAO.click(queuesButton);

            List<WebElement> queues = firefoxDriver.findElements(By.xpath("//td[contains(., '" + queueName + "')]"));
            for(WebElement queue : queues)
            {
                queues = firefoxDriver.findElements(By.xpath("//td[contains(., '" + queueName + "')]"));
                String callmode = queue.findElement(By.xpath("following-sibling::td[contains(@headers, 'campaign')]")).getText();
                if(!callmode.equals(incomingCallModeName)){
                    WebElement deleteButton = queue.findElement(By.xpath("following-sibling::td//img[@src = 'imagenes/delete2.png']"));
                    SeleniumDAO.click(deleteButton);
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                    WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                    Thread.sleep(300);
                    SeleniumDAO.click(confirmButton);
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }*/ //TODO Borrar las colas

    public void revertServiceConfiguration()
    {
        firefoxDriver.get(url + "dialapplet-web");
        Utils.loginDialappletWeb(superAdminName, superAdminPassword, firefoxDriver);
        try {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("mainMenu")));
        } catch (Exception e) {
            System.err.println("Clean ERROR: Login failed");
            e.printStackTrace();
            throw e;
        }

        try
        {
            firefoxWaiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id = 'OPERATION']")));
            WebElement operationsTab = SeleniumDAO.selectElementBy("xpath", "//li[@id = 'OPERATION']", firefoxDriver);
            SeleniumDAO.click(operationsTab);

            try {
                WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", firefoxDriver);
                searcher.sendKeys(serviceID);
                Thread.sleep(1000);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'services']//td[contains(., '" + serviceID + "')]")));
            } catch (Exception e) {
                System.out.println("Clean ERROR: The service with ID: " + serviceID + "does not appears on the services table");
                e.printStackTrace();
                throw e;
            }

            WebElement service = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'services']//td[contains(., '" + serviceID + "')]", firefoxDriver);
            SeleniumDAO.click(service);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit-service']/a")));
            WebElement editServiceTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit-service']/a", firefoxDriver);
            SeleniumDAO.click(editServiceTab);

            revertBasicData();
            revertCallModes();
            revertRobinsonList();
            revertKPI();

        } catch (Exception e)
        {

        }
    }

    public void revertBasicData() throws InterruptedException {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'groups']/tbody/tr")));
            List<WebElement> assignableGroups = firefoxDriver.findElements(By.xpath("//table[@id = 'groups']/tbody/tr/td[1]/input[@type = 'checkbox']"));
            for (int i = 0; i < assignableGroups.size(); i++) {
                Thread.sleep(500);
                if (assignableGroups.get(i).isSelected()) {
                    SeleniumDAO.click(assignableGroups.get(i));
                }
            }

            Select showflowSelector = SeleniumDAO.findSelectElementBy("id", "actionsid", firefoxDriver);
            showflowSelector.selectByValue("0");

            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            } catch (Exception e)
            { }

            Select recordingRateSelector = SeleniumDAO.findSelectElementBy("id", "recordingrate", firefoxDriver);
            recordingRateSelector.selectByValue("0");

            //TODO si eliminas estos campos peta la web
        /*WebElement beginningDateInput = SeleniumDAO.selectElementBy("id", "periodbegin_1", firefoxDriver);
        WebElement endDateInput = SeleniumDAO.selectElementBy("id", "periodend_1", firefoxDriver);
        beginningDateInput.clear();
        endDateInput.clear();*/

            WebElement recordLabel = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'audioformat']", firefoxDriver);
            recordLabel.clear();

            /*WebElement robinsonRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'robinsonno']", firefoxDriver);
            SeleniumDAO.click(robinsonRadioButton);*/

            WebElement scheduleCheckbox = SeleniumDAO.selectElementBy("id", "day1", firefoxDriver);
            if(scheduleCheckbox.isSelected()) SeleniumDAO.click(scheduleCheckbox);
            scheduleCheckbox = SeleniumDAO.selectElementBy("id", "day2", firefoxDriver);
            if(scheduleCheckbox.isSelected()) SeleniumDAO.click(scheduleCheckbox);
            scheduleCheckbox = SeleniumDAO.selectElementBy("id", "day3", firefoxDriver);
            if(scheduleCheckbox.isSelected()) SeleniumDAO.click(scheduleCheckbox);
            scheduleCheckbox = SeleniumDAO.selectElementBy("id", "day4", firefoxDriver);
            if(scheduleCheckbox.isSelected()) SeleniumDAO.click(scheduleCheckbox);
            scheduleCheckbox = SeleniumDAO.selectElementBy("id", "day5", firefoxDriver);
            if(scheduleCheckbox.isSelected()) SeleniumDAO.click(scheduleCheckbox);


            //Save
            WebElement changeButton = SeleniumDAO.selectElementBy("id", "send", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(changeButton);
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-confirm-button-container']")));
            WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(confirmButton);
            confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
            Thread.sleep(700);
            SeleniumDAO.click(confirmButton);
        } catch (Exception e)
        {
            System.err.println("The basic data could not be cleaned");
            e.printStackTrace();
        }

    }

    public void revertCallModes(){
        try
        {
            try
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., 'auxiliarCallMode')]/following-sibling::td//img[@class = 'baseStarOff']")));
                WebElement auxiliarStarButton = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'auxiliarCallMode')]/following-sibling::td//img[@class = 'baseStarOff']", firefoxDriver);
                SeleniumDAO.click(auxiliarStarButton);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                SeleniumDAO.click(okButton);
            } catch (Exception e)
            {
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., 'auxiliarCallMode')]/following-sibling::td//img[@class = 'baseStarOn']")));
            }



            List<WebElement> callmodes = firefoxDriver.findElements(By.xpath("//table[@id = 'campaigns']/tbody/tr/td[3]"));
            for(int i = 0; i < callmodes.size(); i++)
            {
                String callmodeName = callmodes.get(i).getText();
                if(!callmodeName.equals("auxiliarCallMode"))
                {
                    WebElement deleteCallmode = callmodes.get(i).findElement(By.xpath("following-sibling::td//img[@class = 'deleteCampaign']"));
                    SeleniumDAO.click(deleteCallmode);
                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                    WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                    Thread.sleep(1000);
                    SeleniumDAO.click(deleteButton);

                    if(callmodeName.contains("Callback"))
                    {
                        firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                        deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                        Thread.sleep(500);
                        SeleniumDAO.click(deleteButton);
                    }

                    firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                    WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                    Thread.sleep(500);
                    SeleniumDAO.click(okButton);

                    //Esto es por un bug de la web
                    Actions actions = new Actions(firefoxDriver);
                    actions.sendKeys(Keys.ESCAPE).perform();
                    Thread.sleep(1500);
                }
            }
        } catch (Exception e)
        {
            System.err.println("The callmodes could not be cleaned");
            e.printStackTrace();
        }

    }

    public void revertRobinsonList()
    {
        String emptyRobinson = null;
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(., 'Robinson')]")));
            WebElement robinsonListTab = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'Robinson')]", firefoxDriver);
            SeleniumDAO.click(robinsonListTab);

            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@src = 'imagenes/menus/blackListDelete.png']")));
            try
            {
                emptyRobinson = SeleniumDAO.selectElementBy("xpath", "//td[contains(., 'No data have been inserted yet.')]", firefoxDriver).getText();
            } catch (Exception e)
            { }
            if(emptyRobinson == null)
            {
                WebElement removeListButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/blackListDelete.png']", firefoxDriver);
                Thread.sleep(500);
                SeleniumDAO.click(removeListButton);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement confirmButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                Thread.sleep(300);
                SeleniumDAO.click(confirmButton);

                SeleniumDAO.switchToFrame("fancybox-frame", firefoxDriver);
                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.id("progress-bar")));
                WebElement progressBar = SeleniumDAO.selectElementBy("id", "progress-bar", firefoxDriver);

                WebDriverWait waitingRobinsonImport = new WebDriverWait(firefoxDriver, 600);
                waitingRobinsonImport.until(ExpectedConditions.stalenessOf(progressBar));

                SeleniumDAO.switchToDefaultContent(firefoxDriver);
            }

        } catch (Exception e)
        {
            System.err.println("The robinson list could not be deleted");
            e.printStackTrace();
        }
    }
    public void revertKPI() throws InterruptedException {
        try
        {
            firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(., 'KPI')]")));
            Thread.sleep(1500);
            WebElement KPITab = SeleniumDAO.selectElementBy("xpath", "//a[contains(., 'KPI')]", firefoxDriver);
            Thread.sleep(500);
            SeleniumDAO.click(KPITab);

            for(int i = 1; i <= 10; i++)
            {
                Thread.sleep(2000);
                WebElement deleteKPI = SeleniumDAO.selectElementBy("xpath",  "//table[@id = 'table-kpi-sla-service']//tbody/tr[1]//img[@src = 'imagenes/delete2.png']", firefoxDriver);
                Thread.sleep(300);
                SeleniumDAO.click(deleteKPI);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-warning pulseWarning']")));
                WebElement deleteButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                Thread.sleep(300);
                SeleniumDAO.click(deleteButton);

                firefoxWaiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//button[@class = 'confirm']", firefoxDriver);
                Thread.sleep(300);
                SeleniumDAO.click(okButton);

            }


        } catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("ERROR. Could not delete the kpi-sla");
        }

    }


}
