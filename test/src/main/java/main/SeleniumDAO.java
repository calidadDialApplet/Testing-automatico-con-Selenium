package main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;

public class SeleniumDAO {

    public static WebDriver initializeDriver(){
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        return driver;
    }

    public static WebDriver initializeHeadLessDriver(){
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
        return driver;
    }

    public static WebElement selectElementBy(String mode, String args, WebDriver driver){

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

    public static WebElement selectElementBy(String mode, String args, WebElement element){

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
    public static void click(WebElement element){
        element.click();
    }
    public static void switchToFrame(String frameID, WebDriver driver){
        WebDriverWait waiting = new WebDriverWait(driver, 20);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id(frameID)));
        driver.switchTo().frame(frameID);

    }
    public static void switchToDefaultContent(WebDriver driver){
        driver.switchTo().defaultContent();
    }
    public static void dragAndDropAction(WebElement draguedElement, WebElement droppedPlace, WebDriver driver){
        Actions moveAgent = new Actions(driver);
        moveAgent.dragAndDrop(draguedElement,droppedPlace).build().perform();
    }
    public static Select findSelectElementBy(String mode, String args, WebDriver driver){
        Select elementReturned = null;

        switch (mode){
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
            default:
                break;
        }

        if(elementReturned == null)
        {
            throw new NullPointerException("Element not found or could not be selected!");
        }

        return elementReturned;
    }
    public static Select findSelectElementBy(String mode, String args, WebElement element){
        Select elementReturned = null;

        switch (mode){
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
            default:
                break;
        }

        if(elementReturned == null)
        {
            throw new NullPointerException("Element not found or could not be selected!");
        }

        return elementReturned;
    }
    public static HashMap<Integer, String> getSelectOptions(Select selector){
        HashMap<Integer, String> hmap = new HashMap<Integer,String>();
        for(int i = 0; i < selector.getOptions().size(); i++){
            hmap.put(i,selector.getOptions().get(i).getText());
        }
        return hmap;
    }
    public static void selectOption(String mode, String args, Select selector){
        switch (mode){
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
    }
    public static void writeInTo(WebElement element, String value){
        element.sendKeys(value);
    }

    public static void doWaiting(int seconds, String elementBy, String args, WebDriver driver){
        WebDriverWait waiting = new WebDriverWait(driver, seconds);
        switch (elementBy){
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
        }
    }
}
