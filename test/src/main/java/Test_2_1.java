import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;


public class Test_2_1 {
    public static void main(String[] args) {
        //Login
        System.setProperty("webdriver.gecko.driver", "/home/david/Descargas/test/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();
        //Apartado showflow
        WebElement showflow = driver.findElement(By.id("SHOWFLOW"));
        showflow.click();
        //Crear showflow nuevo
        WebElement newShowflow = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div[1]/p[2]/a"));
        newShowflow.click();
        //Nombre del showflow
        WebElement showflowName = driver.findElement(By.id("workflow-name"));
        showflowName.sendKeys("ShowflowrcNver7816");
        //Se pueden configurar más parámetros como la empresa...etc
        //confirmar showflow
        WebElement sendShowflow = driver.findElement(By.id("workflow-send"));
        sendShowflow.click();

        WebDriverWait waiting = new WebDriverWait(driver, 20);


        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok = sweetAlert.findElement(By.className("confirm"));
        ok.click();


        //driver.findElement(By.xpath("'//a[@class="fancybox-item fancybox-close"]'")).click();


        //Configure contact fields
        WebElement city = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[21]/td[1]/input"));
        city.click();

        WebElement country = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[22]/td[1]/input"));
        country.click();

        WebElement aux1 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[1]/input"));
        aux1.click();

        WebElement aux1Name =  driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[2]/input[2]"));
        aux1Name.sendKeys("Es cliente");

        Select aux1Type = new Select(driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[3]/select")));
        aux1Type.selectByValue("select");

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[8]/select[1]")));
        Select aux1OptionsGroup = new Select(driver.findElement(By.cssSelector("select[class='optiongroup active']")));
        aux1OptionsGroup.selectByValue("6");

        WebElement saveAndGo = driver.findElement(By.id("save-fields-and-go"));
        saveAndGo.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert2 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok2 = sweetAlert2.findElement(By.className("sa-confirm-button-container"));
        ok2.click();

        //Showflow fields
        WebElement question = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[1]/input"));
        question.sendKeys("¿Cual es su comida favorita?");
        WebElement addQuestionButton = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[10]/a/img"));
        addQuestionButton.click();
        WebElement question2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[1]/input"));
        question2.sendKeys("¿Cuantos primos tienes?");
        WebElement addQuestionButton2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[10]/a/img"));
        addQuestionButton2.click();

        WebElement saveAndGo2 = driver.findElement(By.id("save-fields-and-go"));
        saveAndGo2.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert3 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok3 = sweetAlert3.findElement(By.className("sa-confirm-button-container"));
        ok3.click();

        //Configuration action fields
        WebElement tipology = driver.findElement(By.id("action-id-1"));
        tipology.click();

        WebElement observations = driver.findElement(By.id("action-id-6"));
        observations.click();

        WebElement save = driver.findElement(By.id("save-fields"));
        save.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert4 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok4 = sweetAlert4.findElement(By.className("sa-confirm-button-container"));
        ok4.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[1]/div[2]/table/tbody/tr[1]/td[4]/a/img")));
        WebElement typologyActions = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[1]/div[2]/table/tbody/tr[1]/td[4]/a/img"));
        typologyActions.click();


        String windowHandleBefore = driver.getWindowHandle();

        WebElement newTypologyField = driver.findElement(By.id("new-typology-field"));
        newTypologyField.sendKeys("MAQUINA");
        WebElement addNewTypologyField = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/thead/tr[2]/td[2]/a/img"));
        addNewTypologyField.click();
        WebElement editNewTypologyField = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/tbody/tr/td[2]/a[1]/img"));
        editNewTypologyField.click();



        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        driver.switchTo().frame("fancybox-frame");

        WebElement newSubTypologyName = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName.sendKeys("INVALIDO");
        Select newSubTypologyView = new Select(driver.findElement(By.id("new-subtypology-view")));
        newSubTypologyView.selectByValue("4");
        Select newSubTypologyResult = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult.selectByValue("NEGATIVE");
        WebElement addNewSubTypology = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology.click();
        WebElement saveSubTypologies = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies.click();

        WebElement newSubTypologyName2 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName2.sendKeys("NUM MAX INTENTOS");
        Select newSubTypologyView2 = new Select(driver.findElement(By.id("new-subtypology-view")));
        newSubTypologyView2.selectByValue("4");
        Select newSubTypologyResult2 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult2.selectByValue("NEGATIVE");
        WebElement addNewSubTypology2 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology2.click();
        WebElement saveSubTypologies2 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies2.click();

        WebElement newSubTypologyName3 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName3.sendKeys("ROBISON");
        Select newSubTypologyView3 = new Select(driver.findElement(By.id("new-subtypology-view")));
        newSubTypologyView3.selectByValue("4");
        Select newSubTypologyResult3 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult3.selectByValue("NEGATIVE");
        WebElement addNewSubTypology3 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology3.click();
        WebElement saveSubTypologies3 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies3.click();

        WebElement close = driver.findElement(By.id("cancel-subtypologies"));
        close.click();
        driver.switchTo().defaultContent();

        //////////////// SECOND TYPOLOGY
        driver.navigate().refresh();
        WebElement newTypologyField2 = driver.findElement(By.xpath("//*[@id=\"new-typology-field\"]"));
        newTypologyField2.sendKeys("NO INTERESA");
        WebElement addNewTypologyField2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/thead/tr[2]/td[2]/a/img"));
        addNewTypologyField2.click();
        WebElement editNewTypologyField2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/tbody/tr[2]/td[2]/a[1]/img"));
        editNewTypologyField2.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        driver.switchTo().frame("fancybox-frame");

        WebElement newSubTypologyName4 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName4.sendKeys("SE QUEDA CON EL OPERADOR");
        Select newSubTypologyView4 = new Select(driver.findElement(By.id("new-subtypology-view")));
        newSubTypologyView4.selectByValue("1");
        Select newSubTypologyResult4 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult4.selectByValue("NS");
        WebElement addNewSubTypology4 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology4.click();
        WebElement saveSubTypologies4 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies4.click();

        WebElement newSubTypologyName5 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName5.sendKeys("TARIFA MAS CARA");
        Select newSubTypologyView5 = new Select(driver.findElement(By.id("new-subtypology-view")));
        newSubTypologyView5.selectByValue("1");
        Select newSubTypologyResult5 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult5.selectByValue("NS");
        WebElement addNewSubTypology5 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology5.click();
        WebElement saveSubTypologies5 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies5.click();

        WebElement newSubTypologyName6 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName6.sendKeys("YA ES CLIENTE");
        Select newSubTypologyView6 = new Select(driver.findElement(By.id("new-subtypology-view")));
        newSubTypologyView6.selectByValue("1");
        Select newSubTypologyResult6 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult6.selectByValue("NS");
        WebElement addNewSubTypology6 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology6.click();
        WebElement saveSubTypologies6 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies6.click();

        WebElement close2 = driver.findElement(By.id("cancel-subtypologies"));
        close2.click();

        ///////THIRD TYPOLOGY
        driver.navigate().refresh();
        WebElement newTypologyField3 = driver.findElement(By.xpath("//*[@id=\"new-typology-field\"]"));
        newTypologyField3.sendKeys("PENDIENTE");
        WebElement addNewTypologyField3 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/thead/tr[2]/td[2]/a/img"));
        addNewTypologyField3.click();
        WebElement editNewTypologyField3 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/tbody/tr[3]/td[2]/a[1]/img"));
        editNewTypologyField3.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        driver.switchTo().frame("fancybox-frame");

        WebElement newSubTypologyName7 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName7.sendKeys("AGENDADA");
        WebElement addNewSubTypology7 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology7.click();
        WebElement saveSubTypologies7 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies7.click();

        WebElement close3 = driver.findElement(By.id("cancel-subtypologies"));
        close3.click();
    }
}
