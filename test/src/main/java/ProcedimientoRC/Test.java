package ProcedimientoRC;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;

public abstract class Test {
    abstract public HashMap<String, String> check();


    public FirefoxDriver headlessOrNot(String headless)
    {
        if (headless.equals("true")) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("--headless");
            System.setProperty("webdriver.gecko.driver", "geckodriver");
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setBinary(firefoxBinary);
            FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
            return firefoxDriver;
        } else {
            System.setProperty("webdriver.gecko.driver", "geckodriver");
            FirefoxDriver firefoxDriver = new FirefoxDriver();
            return firefoxDriver;
        }
    }
}
