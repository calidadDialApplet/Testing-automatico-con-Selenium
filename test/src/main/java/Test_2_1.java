import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Vector;

//

public class Test_2_1 {

    public static void main(String[] args) {
        // Login
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

        WebElement user = driver.findElement(By.id("adminusername"));
        user.sendKeys("admin");

        WebElement pass = driver.findElement(By.id("adminpassword"));
        pass.sendKeys("admin");

        WebElement entry = driver.findElement(By.id("login"));
        entry.click();
        // Showflow tab
        WebElement showflow = driver.findElement(By.id("SHOWFLOW"));
        showflow.click();
        // New Showflow
        WebElement newShowflow = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div[1]/p[2]/a"));
        newShowflow.click();
        // Showflow Name
        WebElement showflowName = driver.findElement(By.id("workflow-name"));
        showflowName.sendKeys("ShowflowRCNver7816");
        // You can configure more parameters like company...etc
        // Confirm showflow
        WebElement sendShowflow = driver.findElement(By.id("workflow-send"));
        sendShowflow.click();

        WebDriverWait waiting = new WebDriverWait(driver, 20);


        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok = sweetAlert.findElement(By.className("confirm"));
        ok.click();

        // Configure contact fields
        Vector<String> contactFieldsName = new Vector<String>() ;
        contactFieldsName.add("contact-field-name");
        contactFieldsName.add("contact-field-phone");
        int contactFieldsCounter = 2; //Name and Phone are selected by default
        WebElement city = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[21]/td[1]/input"));
        city.click();
        contactFieldsName.add("contact-field-city");
        contactFieldsCounter++;

        WebElement country = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[22]/td[1]/input"));
        country.click();
        contactFieldsName.add("contact-field-country");
        contactFieldsCounter++;

        WebElement aux1 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[1]/input"));
        aux1.click();

        WebElement aux1Name =  driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[2]/input[2]"));
        aux1Name.sendKeys("Es cliente");


        Select aux1Type = SeleniumDAO.findSelectElementBy("xpath","/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[3]/select", driver);
                //new Select(driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[3]/select")));
        aux1Type.selectByValue("select");
        //////////////////////////////// TEST
        HashMap<Integer,String> hmap = SeleniumDAO.getSelectOptions(aux1Type);
        for(Integer value: hmap.keySet()){
            String key = value.toString();
            String option = hmap.get(value);
            System.out.println(key+" " + option);
        }
        ///////////////////////////////////////////
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[24]/td[8]/select[1]")));
        Select aux1OptionsGroup = new Select(driver.findElement(By.cssSelector("select[class='optiongroup active']")));
        aux1OptionsGroup.selectByValue("6");
        contactFieldsCounter++;
        contactFieldsName.add("contact-field-aux1");

        WebElement saveAndGo = driver.findElement(By.id("save-fields-and-go"));
        saveAndGo.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert2 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok2 = sweetAlert2.findElement(By.className("sa-confirm-button-container"));

        ok2.click();

        // Showflow fields
        Vector<String> showflowsFieldsName = new Vector<String>() ;
        int showflowsFieldsCount = 0;
        WebElement question = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[1]/input"));
        question.sendKeys("¿Cual es su comida favorita?");
        WebElement addQuestionButton = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[10]/a/img"));
        addQuestionButton.click();
        WebElement questionp = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[1]"));
        showflowsFieldsName.add("workflow-field-"+questionp.getAttribute("data-fieldid"));
        showflowsFieldsCount++;

        WebElement question2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[1]/input"));
        question2.sendKeys("¿Cuantos primos tienes?");
        WebElement addQuestionButton2 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/thead/tr[2]/td[10]/a/img"));
        addQuestionButton2.click();
        WebElement questionpagain = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table[2]/tbody/tr[1]"));
        showflowsFieldsName.add("workflow-field-"+questionpagain.getAttribute("data-fieldid"));
        showflowsFieldsCount++;

        WebElement saveAndGo2 = driver.findElement(By.id("save-fields-and-go"));
        saveAndGo2.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert3 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok3 = sweetAlert3.findElement(By.className("sa-confirm-button-container"));
        ok3.click();

        // Configuration action fields
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
        // The index of the last tr, indicates the row of the table and is incremental with the number of typologies
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

        /////// THIRD TYPOLOGY
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

        //////// FOURTH TYPOLOGY
        driver.navigate().refresh();
        WebElement newTypologyField4 = driver.findElement(By.xpath("//*[@id=\"new-typology-field\"]"));
        newTypologyField4.sendKeys("VENTA");
        WebElement addNewTypologyField4 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/thead/tr[2]/td[2]/a/img"));
        addNewTypologyField4.click();
        WebElement editNewTypologyField4 = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div[2]/div[2]/table[2]/tbody/tr[4]/td[2]/a[1]/img"));
        editNewTypologyField4.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        driver.switchTo().frame("fancybox-frame");

        WebElement newSubTypologyName8 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName8.sendKeys("ADSL");
        Select newSubTypologyResult7 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult7.selectByValue("POSITIVE");
        WebElement addNewSubTypology8 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology8.click();
        WebElement saveSubTypologies8 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies8.click();

        WebElement newSubTypologyName9 = driver.findElement(By.id("new-subtypology-name"));
        newSubTypologyName9.sendKeys("MOVIL");
        Select newSubTypologyResult8 = new Select(driver.findElement(By.id("new-subtypology-result")));
        newSubTypologyResult8.selectByValue("POSITIVE");
        WebElement addNewSubTypology9 = driver.findElement(By.xpath("/html/body/table[1]/thead/tr[2]/td[6]/a/img"));
        addNewSubTypology9.click();
        WebElement saveSubTypologies9 = driver.findElement(By.id("save-subtypologies"));
        saveSubTypologies9.click();

        WebElement close4 = driver.findElement(By.id("cancel-subtypologies"));
        close4.click();

        driver.navigate().refresh();
        // Import typologies by csv
        WebElement examineButton = driver.findElement(By.id("csvfile"));
        examineButton.sendKeys("/home/david/Escritorio/Tipologias.csv"); /////////CHANGE TO V2: make a file in project with csv and put absolute route
        WebElement submitButton = driver.findElement(By.id("submitcsv"));
        submitButton.click();

        WebElement saveTypologies = driver.findElement(By.id("save-typologies"));
        saveTypologies.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert5 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok5 = sweetAlert5.findElement(By.className("sa-confirm-button-container"));
        ok5.click();

        // Configure pages
        WebElement configurePages = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div[4]/p[1]/a"));
        configurePages.click();
        // First Page
        WebElement firstPageName = driver.findElement(By.id("page-name"));
        firstPageName.sendKeys("Pagina Inicial");
        WebElement initialPage = driver.findElement(By.id("page-initial"));
        initialPage.click();
        WebElement addPage = driver.findElement(By.cssSelector("img[src='imagenes/add2.png']"));
        addPage.click();
        WebElement editFirstPage = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table/tbody/tr[1]/td[5]/a[3]/img "));
        editFirstPage.click();

        // Switch to iframe of page editor
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("showflow_page_editor")));
        driver.switchTo().frame("showflow_page_editor");

        WebElement insertElementField = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/ul/li[5]/a/div/div[1]"));
        insertElementField.click();

        WebElement nameField = driver.findElement(By.id(""+contactFieldsName.firstElement()));
        contactFieldsName.remove(0);
        WebElement placeField = driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul[1]/li/div"));
        Actions moveField = new Actions(driver);
        moveField.dragAndDrop(nameField,placeField).build().perform();

        for(int i = 2; i< contactFieldsCounter+1; i++) {
            insertElementField.click();
            WebElement field = driver.findElement(By.id(""+contactFieldsName.firstElement()));
            contactFieldsName.remove(0);
            WebElement elementField = driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul[1]/li["+i+"]/div"));
            Actions movesField = new Actions(driver);
            movesField.dragAndDrop(field,elementField).build().perform();
        }

        waiting.until(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(By.id("saving-indicator"))));

        driver.switchTo().defaultContent();
        WebElement backButton = driver.findElement(By.cssSelector("img[src='imagenes/menus/back.png']"));
        backButton.click();

        // Middle Page
        WebElement midPageName = driver.findElement(By.id("page-name"));
        midPageName.sendKeys("Pagina Intermedia");
        WebElement addPageAgainAgain = driver.findElement(By.cssSelector("img[src='imagenes/add2.png']"));
        addPageAgainAgain.click();
        WebElement editMidPage = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table/tbody/tr[2]/td[5]/a[3]/img"));
        editMidPage.click();

        // Switch to iframe of page editor
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("showflow_page_editor")));
        driver.switchTo().frame("showflow_page_editor");

        WebElement insertElementFieldAgain = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/ul/li[5]/a/div/div[1]"));

        insertElementFieldAgain.click();
        WebElement nameFieldAgain = driver.findElement(By.id("contact-field-name"));
        WebElement placeFieldAgain = driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul[1]/li/div"));
        Actions moveFieldAgain = new Actions(driver);
        moveFieldAgain.dragAndDrop(nameFieldAgain,placeFieldAgain).build().perform();

        waiting.until(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(By.id("saving-indicator"))));
        driver.switchTo().defaultContent();
        WebElement backButtonAgain = driver.findElement(By.cssSelector("img[src='imagenes/menus/back.png']"));
        backButtonAgain.click();

        // Final Page
        WebElement finalPageName = driver.findElement(By.id("page-name"));
        finalPageName.sendKeys("Pagina Final");
        WebElement finalPage = driver.findElement(By.id("page-final"));
        finalPage.click();
        WebElement addPageAgain = driver.findElement(By.cssSelector("img[src='imagenes/add2.png']"));
        addPageAgain.click();
        WebElement editLastPage = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div/div/div[2]/table/tbody/tr[3]/td[5]/a[3]/img"));
        editLastPage.click();

        // Switch to iframe of page editor
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("showflow_page_editor")));
        driver.switchTo().frame("showflow_page_editor");

        WebElement insertElementFieldLP = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/ul/li[5]/a/div/div[1]"));
        insertElementFieldLP.click();
        WebElement workflowFields = driver.findElement(By.id("workflow-fields"));
        workflowFields.click();
        WebElement firsShowFlowField = driver.findElement(By.id(""+showflowsFieldsName.firstElement()));
        showflowsFieldsName.remove(0);
        WebElement firstPlaceShowFlowField = driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul[1]/li/div"));
        Actions moveShowFlowField = new Actions(driver);
        moveShowFlowField.dragAndDrop(firsShowFlowField,firstPlaceShowFlowField).build().perform();

        for(int i = 2; i< showflowsFieldsCount+1; i++) {
            insertElementFieldLP.click();
            WebElement field = driver.findElement(By.id(""+showflowsFieldsName.firstElement()));
            showflowsFieldsName.remove(0);
            WebElement elementField = driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul[1]/li["+i+"]/div"));
            Actions movesField = new Actions(driver);
            movesField.dragAndDrop(field,elementField).build().perform();
        }

        waiting.until(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(By.id("saving-indicator"))));
        driver.switchTo().defaultContent();
        WebElement backButtonAgainAgain = driver.findElement(By.cssSelector("img[src='imagenes/menus/back.png']"));
        backButtonAgainAgain.click();

        // Configure Union Pages
        WebElement configureUnionPagesButton = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div[4]/p[2]/a"));
        configureUnionPagesButton.click();

        // Simple union page, Mid U Final
        Select originPage = new Select(driver.findElement(By.id("add-input-page")));
        originPage.selectByIndex(1);

        Select originFieldAgain = new Select(driver.findElement(By.id("add-input-field")));
        originFieldAgain.selectByIndex(1);

        WebElement nameValueAgain = driver.findElement(By.id("input-match-value"));
        nameValueAgain.sendKeys("Antonio");

        Select destPage = new Select(driver.findElement(By.id("add-output-page")));
        destPage.selectByIndex(0);
        WebElement addUnionAgain = driver.findElement(By.cssSelector("img[src='imagenes/add2.png']"));
        addUnionAgain.click();

        // Clone ShowFlow
        WebElement cloneButton = driver.findElement(By.id("clone_showflow"));
        cloneButton.click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("fancybox-frame")));
        driver.switchTo().frame("fancybox-frame");

        WebElement cloneShowflow = driver.findElement(By.id("cloneShowflow"));
        cloneShowflow.click();
        /*
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='sweet-alert showSweetAlert visible']")));
        WebElement sweetAlert6 = driver.findElement(By.cssSelector("div[class='sweet-alert showSweetAlert visible']"));
        WebElement ok6 = sweetAlert6.findElement(By.className("confirm"));
        ok6.click();
        */
        driver.navigate().refresh();

        // Return to showflow panel
        //WebElement showflowPanel = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div[1]/p[1]/a"));
        WebElement showflowPanel = driver.findElement(By.cssSelector("a[href='showflowPanel.php']"));
        showflowPanel.click();

    }
}
