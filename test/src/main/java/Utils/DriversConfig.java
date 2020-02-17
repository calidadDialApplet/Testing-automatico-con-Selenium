package Utils;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

//*
// This class is for the instantiation of drivers
// */
public class DriversConfig {

    //Instance a firefox driver
    public static FirefoxDriver headlessOrNot(String headless)
    {
        if (headless.equals("true")) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("--headless");
            System.setProperty("webdriver.gecko.driver", "./geckodriver");
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setBinary(firefoxBinary);
            FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
            return firefoxDriver;
        } else {
            System.setProperty("webdriver.gecko.driver", "./geckodriver");
            FirefoxDriver firefoxDriver = new FirefoxDriver();
            return firefoxDriver;
        }
    }

    //Instance a chrome driver
    public static ChromeDriver headlessOrNot(String headless, String chromePath)
    {
        if (headless.equals("true")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setBinary(chromePath);
            chromeOptions.addArguments("--headless");
            System.setProperty("webdriver.chrome.driver", "./chromedriver");
            ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
            return chromeDriver;
        } else {
            System.setProperty("webdriver.chrome.driver", "./chromedriver");
            ChromeDriver chromeDriver = new ChromeDriver();
            return chromeDriver;
        }
    }

    //Instance a firefox driver that hides the pop up that shows up when trying to download
    public static FirefoxDriver noDownloadPopUp(String headless)
    {
        System.setProperty("webdriver.gecko.driver", "./geckodriver");

        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("browser.download.folderList", 2);
        firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
        firefoxProfile.setPreference("browser.download.dir", System.getProperty("user.dir"));
        firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv;audio/wav");

        FirefoxOptions firefoxOptions = new FirefoxOptions();

        if (headless.equals("true")) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("--headless");

            firefoxOptions.setBinary(firefoxBinary);
        }
        firefoxOptions.setProfile(firefoxProfile);

        FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
        return firefoxDriver;
    }
}
