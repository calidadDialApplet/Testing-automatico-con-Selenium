package main;

import com.sun.javafx.PlatformUtil;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SeleniumDAO {

    public static WebDriver initializeFirefoxDriver(){
        if(PlatformUtil.isWindows())
        {
            System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        }
        else
        {
            System.setProperty("webdriver.gecko.driver", "geckodriver");
        }
        WebDriver driver = new FirefoxDriver();
        return driver;
    }

    public static WebDriver initializeChromeDriver(){
        if(PlatformUtil.isWindows())
        {
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        }
        else
        {
            System.setProperty("webdriver.chrome.driver", "chromedriver");
        }
        WebDriver driver = new ChromeDriver();
        return driver;
    }

    public static WebDriver initializeFirefoxHeadlessDriver()
    {
        if(PlatformUtil.isWindows())
        {
            System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        }
        else
        {
            System.setProperty("webdriver.gecko.driver", "geckodriver");
        }
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
        return driver;
    }

    public static WebDriver initializeChromeHeadlessDriver()
    {
        if(PlatformUtil.isWindows())
        {
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        }
        else
        {
            System.setProperty("webdriver.chrome.driver", "chromedriver");
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        return new ChromeDriver(options);
    }

    public static WebElement selectElementBy(String mode, String args, WebDriver driver)
    {

        WebElement elementReturned = null;

        switch (mode){
            case "id":
                WebElement elementClickedById = driver.findElement(By.id(args));
                elementReturned = elementClickedById;
                break;
            case "xpath":
                WebElement elementClickedByXpath = driver.findElement(By.xpath(args));
                elementReturned = elementClickedByXpath;
                break;
            case "cssSelector":
                WebElement elementClickedByCssSelector = driver.findElement(By.cssSelector(args));
                elementReturned = elementClickedByCssSelector;
                break;
            case "className":
                WebElement elementClickedByClassName = driver.findElement(By.className(args));
                elementReturned = elementClickedByClassName;
                break;
            case "name":
                WebElement elementClickedByName = driver.findElement(By.name(args));
                elementReturned = elementClickedByName;
                break;
            default:
                break;
        }

        if(elementReturned == null)
        {
            throw new NullPointerException("Element not found or could not be selected!");
        }
        return elementReturned;
    }

    public static WebElement selectElementBy(String mode, String args, WebElement element)
    {

        WebElement elementReturned = null;

        switch (mode){
            case "id":
                WebElement elementClickedById = element.findElement(By.id(args));
                elementReturned = elementClickedById;
                break;
            case "xpath":
                WebElement elementClickedByXpath = element.findElement(By.xpath(args));
                elementReturned = elementClickedByXpath;
                break;
            case "cssSelector":
                WebElement elementClickedByCssSelector = element.findElement(By.cssSelector(args));
                elementReturned = elementClickedByCssSelector;
                break;
            case "className":
                WebElement elementClickedByClassName = element.findElement(By.className(args));
                elementReturned = elementClickedByClassName;
                break;
            case "name":
                WebElement elementClickedByName = element.findElement(By.name(args));
                elementReturned = elementClickedByName;
                break;
            default:
                break;
        }

        if(elementReturned == null)
        {
            throw new NullPointerException("Element not found or could not be selected!");
        }

        return elementReturned;

    }

    public static void click(WebElement element)
    {
        element.click();
    }


     public static void switchToFrame (String frameArgs, WebDriver driver)
     {
            WebDriverWait waiting = new WebDriverWait(driver, 20);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id(frameArgs)));
            driver.switchTo().frame(frameArgs);

     }

     public static void switchToDefaultContent (WebDriver driver)
     {
            driver.switchTo().defaultContent();
     }

     public static void dragAndDropAction (WebElement draguedElement, WebElement droppedPlace, WebDriver driver)
     {
            Actions moveElement = new Actions(driver);
            moveElement.dragAndDrop(draguedElement, droppedPlace).build().perform();
     }

    public static Select findSelectElementBy (String mode, String args, WebDriver driver)
    {
        Select elementReturned = null;

        switch (mode) {
            case "id":
                Select elementClickedById = new Select(driver.findElement(By.id(args)));
                elementReturned = elementClickedById;
                break;
            case "xpath":
                Select elementClickedByXpath = new Select(driver.findElement(By.xpath(args)));
                elementReturned = elementClickedByXpath;
                break;
            case "cssSelector":
                Select elementClickedByCssSelector = new Select(driver.findElement(By.cssSelector(args)));
                elementReturned = elementClickedByCssSelector;
                break;
            case "className":
                Select elementClickedByClassName = new Select(driver.findElement(By.className(args)));
                elementReturned = elementClickedByClassName;
                break;
            case "name":
                Select elementClickedByName = new Select(driver.findElement(By.name(args)));
                elementReturned = elementClickedByName;
                break;
            default:
                break;
        }

        if (elementReturned == null) {
            throw new NullPointerException("Element not found or could not be selected!");
        }

        return elementReturned;
    }

    public static Select findSelectElementBy (String mode, String args, WebElement element)
    {
        Select elementReturned = null;

        switch (mode) {
            case "id":
                Select elementClickedById = new Select(element.findElement(By.id(args)));
                elementReturned = elementClickedById;
                break;
            case "xpath":
                Select elementClickedByXpath = new Select(element.findElement(By.xpath(args)));
                elementReturned = elementClickedByXpath;
                break;
            case "cssSelector":
                Select elementClickedByCssSelector = new Select(element.findElement(By.cssSelector(args)));
                elementReturned = elementClickedByCssSelector;
                break;
            case "className":
                Select elementClickedByClassName = new Select(element.findElement(By.className(args)));
                elementReturned = elementClickedByClassName;
                break;
            case "name":
                Select elementClickedByName = new Select(element.findElement(By.name(args)));
                elementReturned = elementClickedByName;
                break;
            default:
                break;
        }

        if (elementReturned == null) {
            throw new NullPointerException("Element not found or could not be selected!");
        }

        return elementReturned;
    }

    public static HashMap<Integer, String> getSelectOptions (Select selector)
    {
        HashMap<Integer, String> hmap = new HashMap<Integer, String>();
        for (int i = 0; i < selector.getOptions().size(); i++) {
            hmap.put(i, selector.getOptions().get(i).getText());
        }
        return hmap;
    }

    /*public static void selectOption (String mode, String args, Select selector)
    {
        switch (mode) {
            case "index":
                selector.selectByIndex(Integer.parseInt(args));
                break;
            case "value":
                selector.selectByValue(args);
                break;
            case "visibleText":
                selector.selectByVisibleText(args);
                break;
            default:
                break;
        }
    }*/

    public static void writeInTo (WebElement element, String value)
    {
        element.sendKeys(value);
    }

    public static String readFrom (WebElement element)
    {
        return element.getText();
    }

    public static void waitForElement ( int seconds, String elementBy, String args, WebDriver driver)
    {
        WebDriverWait waiting = new WebDriverWait(driver, seconds);
        switch (elementBy) {
            case "id":
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.id(args)));
                break;
            case "xpath":
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath(args)));
                break;
            case "cssSelector":
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(args)));
                break;
            case "className":
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.className(args)));
                break;
            case "name":
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.name(args)));
                break;
            default:
                break;
        }
    }

    public static void implicitWait (int waitingTime, WebDriver driver, Thread thread)
    {
        try {
            TimeUnit.SECONDS.sleep(waitingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void navigateTo (String URL, WebDriver driver)
    {
        driver.navigate().to(URL);
    }

    public static void screenShot(WebDriver driver)
    {
       File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String path = System.getProperty("user.home");
        try {
            FileUtils.moveToDirectory(src, new File(path + "/git_docs/ScreenShots/"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pressKey(String key, WebElement element)
    {
        switch (key)
        {
            case "F5":
                element.sendKeys(Keys.F5);
                break;
            case "Enter":
                element.sendKeys(Keys.ENTER);
                break;
            case "PageDown":
                element.sendKeys(Keys.PAGE_DOWN);
                break;
            case "PageUp":
                element.sendKeys(Keys.PAGE_UP);
                break;
            case "Delete":
                element.sendKeys(Keys.BACK_SPACE);
                break;
            default:
                break;

        }
    }

    public static void refreshPage(WebDriver driver)
    {
        driver.navigate().refresh();
    }

    public static String getAttribute(WebElement element)
    {
        return element.getAttribute("data-fieldid");
    }

    public static void selectOption(Select select, String value)
    {
        select.selectByValue(value);
    }

}

