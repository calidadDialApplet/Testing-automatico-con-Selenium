package Utils;

import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ServiceUtils {
    public static void setDateTimeCampaign(WebDriver driver, WebDriverWait waiting, String beginningDate, String endDate)
    {
        //Selects the dates of the campaign
        WebElement beginningDateInput = SeleniumDAO.selectElementBy("id", "periodbegin_1", driver);
        WebElement endDateInput = SeleniumDAO.selectElementBy("id", "periodend_1", driver);
        beginningDateInput.clear();
        endDateInput.clear();
        beginningDateInput.sendKeys(beginningDate);
        endDateInput.sendKeys(endDate);

        //Selects the schedule
        WebElement mondayCheckbox = SeleniumDAO.selectElementBy("id", "day1", driver);
        SeleniumDAO.click(mondayCheckbox);

        WebElement beginningHourInput = SeleniumDAO.selectElementBy("id", "since_day1_mor", driver);
        WebElement endHourInput = SeleniumDAO.selectElementBy("id", "until_day1_mor", driver);
        waiting.until(ExpectedConditions.elementToBeClickable(By.id("since_day1_mor")));
        beginningHourInput.sendKeys("09:00:00");
        endHourInput.sendKeys("17:00:00");

        WebElement copyToFridayButton = SeleniumDAO.selectElementBy("xpath", "//input[@class = 'copyToFriday']", driver);
        SeleniumDAO.click(copyToFridayButton);
    }

    public static void recordRateTo100(WebDriver driver){
        Select recordingRateSelector = SeleniumDAO.findSelectElementBy("id", "recordingrate", driver);
        recordingRateSelector.selectByValue("100");
    }

    public static void setRecordMap(WebDriver driver)
    {
        WebElement recordLabel = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'audioformat']", driver);
        recordLabel.clear();
        recordLabel.sendKeys("RCNvXYZ%20-%21-%1-%9-%8-%6");
    }

    public static void setRobinsonToYes(WebDriver driver)
    {
        WebElement robinsonRadioButton = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'robinsonyes']", driver);
        SeleniumDAO.click(robinsonRadioButton);
    }
}
