package Utils;

import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShowflowUtils {

    //TODO Borrar el argumento url. Se pone para que las pruebas funcionen para la version 7rc
    public static String createShowflow(WebDriver driver, WebDriverWait waiting, String showflowName, String showflowType, String url)
    {
        WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", driver);
        SeleniumDAO.click(showflowTab);

        WebElement createShowflow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'setShowflow.php']", driver);
        SeleniumDAO.click(createShowflow);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("contenido")));

        WebElement showflowNameInput = SeleniumDAO.selectElementBy("id", "workflow-name", driver);
        showflowNameInput.sendKeys(showflowName);

        if(url.contains("8")){ //TODO borrar este if, solo es para la 7rc de momento
            Select showflowTypeSelector = SeleniumDAO.findSelectElementBy("id", "workflow-type", driver);
            showflowTypeSelector.selectByValue(showflowType);
        }

        WebElement refreshRadioButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'form-col input radio']//input[@name = 'workflow-showrefreshbutton']", driver);
        SeleniumDAO.click(refreshRadioButton);

        WebElement sendButton = SeleniumDAO.selectElementBy("id", "workflow-send", driver);
        SeleniumDAO.click(sendButton);

        try
        {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
            Thread.sleep(500);
            WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sweet-alert showSweetAlert visible']//button[@class = 'confirm']", driver);
            SeleniumDAO.click(okButton);
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR: The showflow could not be created. Check if it already exists";
        }

        WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", driver);
        SeleniumDAO.click(showflowPanel);

        //Searchs the new showflow in the table and checks if appears
        try {
            WebElement searcher = SeleniumDAO.selectElementBy("xpath", "//input[@id = 'search']", driver);
            searcher.sendKeys(showflowName);
            Thread.sleep(1000);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + "ERROR: Something went wrong. The showflow was created but don't appears on the showflows table";
        }
        return "";
    }

    public static String createContactFields(WebDriver driver, WebDriverWait waiting, List<String> options, String showflowName,
                                                String showflowOptionsGroupName, String showflowAuxField, boolean ticket) {
        try
        {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", driver);
            SeleniumDAO.click(showflow);

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("edit_showflow")));
            WebElement contactFields = SeleniumDAO.selectElementBy("id", "edit_showflow_contact", driver);
            SeleniumDAO.click(contactFields);

            //Las opciones basta con configurarlas cuando se crea el showflow de contacto. Luego se pueden reutilizar en el de ticket
            if(!ticket){
                //Add options group
                WebElement addOptionsGroup = SeleniumDAO.selectElementBy("id", "addOptionsGroup", driver);
                SeleniumDAO.click(addOptionsGroup);

                SeleniumDAO.switchToFrame("fancybox-frame", driver);

                waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("new-option-group-name")));
                WebElement optionsGroupName = SeleniumDAO.selectElementBy("id", "new-option-group-name", driver);
                optionsGroupName.sendKeys(showflowOptionsGroupName);

                WebElement addOptionsGroupButton = SeleniumDAO.selectElementBy("id", "add_new_group", driver);
                SeleniumDAO.click(addOptionsGroupButton);

                Thread.sleep(1000);

                WebElement editOptionGroup = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'option_groups']" +
                        "//td[contains(., '" + showflowOptionsGroupName + "')]//following-sibling::td[@style = 'text-align: center;']//a[@class = 'edit_group_options']", driver);
                SeleniumDAO.click(editOptionGroup);

                SeleniumDAO.switchToFrame("fancybox-frame", driver);

                for(int i = 0; i < options.size(); i++)
                {
                    WebElement newOptionNameInput = SeleniumDAO.selectElementBy("id", "new-option-name", driver);
                    WebElement addNewOptionButton = SeleniumDAO.selectElementBy("id", "add_new_option", driver);
                    newOptionNameInput.clear();
                    newOptionNameInput.sendKeys(options.get(i));
                    SeleniumDAO.click(addNewOptionButton);
                }

                SeleniumDAO.switchToDefaultContent(driver);

                //Closes the iframe of the options group
                WebElement closeIframe = SeleniumDAO.selectElementBy("id", "fancybox-close", driver);
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                SeleniumDAO.click(closeIframe);
            }


            WebElement cityCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city']//td[@style = 'text-align: center;']/input", driver);
            SeleniumDAO.click(cityCheckbox);

            WebElement countryCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'country']//td[@style = 'text-align: center;']/input", driver);
            SeleniumDAO.click(countryCheckbox);

            WebElement auxFieldInput = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//input[@class = 'name']", driver);
            auxFieldInput.sendKeys(showflowAuxField);

            Select auxFieldType = SeleniumDAO.findSelectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//select[@class = 'type']", driver);
            auxFieldType.selectByValue("radiobutton");

            Select optionGroup = SeleniumDAO.findSelectElementBy("xpath", "//select[@class = 'optiongroup active']", driver);
            optionGroup.selectByVisibleText(showflowOptionsGroupName);

            WebElement saveAndConfigureButton = SeleniumDAO.selectElementBy("id", "save-fields-and-go", driver);
            SeleniumDAO.click(saveAndConfigureButton);

            try
            {
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'sa-icon sa-success animate']")));
                Thread.sleep(500);
                WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sweet-alert showSweetAlert visible']//button[@class = 'confirm']", driver);
                SeleniumDAO.click(okButton);
            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR: Unexpected";
            }

            //Checks if the fields are checked
            WebElement showflowPanel = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'acciones']//a[@href = 'showflowPanel.php']", driver);
            SeleniumDAO.click(showflowPanel);

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]")));
            showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowName + "')]", driver);
            SeleniumDAO.click(showflow);

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("edit_showflow")));
            contactFields = SeleniumDAO.selectElementBy("id", "edit_showflow_contact", driver);
            SeleniumDAO.click(contactFields);

            cityCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city']//td[@style = 'text-align: center;']/input", driver);
            countryCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'country']//td[@style = 'text-align: center;']/input", driver);
            WebElement auxFieldCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//td[@style = 'text-align: center;']/input", driver);

            if(cityCheckbox.isSelected() && countryCheckbox.isSelected() && auxFieldCheckbox.isSelected())
            {
                return "Test OK. The showflow fields name, phone, city, country and aux field: " + showflowAuxField + ", have been activated.";
            } else
            {
                return "ERROR. The showflow fields were activated but they are not saved";
            }

        } catch(Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Something went wrong";
        }
    }

    public static String createQuestions(WebDriver driver, WebDriverWait waiting, List<String> questions, String showflowOptionsGroupName, boolean ticket) throws InterruptedException {
        //Go to showflow fields
        if(ticket)
        {
            WebElement showflowFields = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_ticket_fields']", driver);
            waiting.until(ExpectedConditions.elementToBeClickable(showflowFields));
            SeleniumDAO.click(showflowFields);
        } else
        {
            WebElement showflowFields = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_showflow_fields']", driver);
            waiting.until(ExpectedConditions.elementToBeClickable(showflowFields));
            SeleniumDAO.click(showflowFields);
        }



        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow_fields")));

        for(int i = 0; i < questions.size(); i++)
        {
            WebElement questionNameInput = SeleniumDAO.selectElementBy("xpath", "//tr[@id = 'new-field']//input[@class = 'label']", driver);
            questionNameInput.sendKeys(questions.get(i));

            Thread.sleep(1000);
            Select questionType = SeleniumDAO.findSelectElementBy("xpath", "//table[@id = 'workflow_fields']//tr[@id = 'new-field']//select[@class = 'type']", driver);
            if(i == 0) questionType.selectByVisibleText("Checkbox");
            else questionType.selectByVisibleText("Radiobutton");


            Select optionGroup = SeleniumDAO.findSelectElementBy("xpath", "//tr[@id = 'new-field']//select[@class = 'optiongroup']", driver);
            Thread.sleep(500);
            optionGroup.selectByVisibleText(showflowOptionsGroupName);


            WebElement addNewQuestionButton = SeleniumDAO.selectElementBy("id", "add_new_field", driver);
            SeleniumDAO.click(addNewQuestionButton);
            Thread.sleep(500);
        }

        waiting.until(ExpectedConditions.visibilityOfElementLocated(By.id("save-fields")));
        WebElement saveButton = SeleniumDAO.selectElementBy("id", "save-fields", driver);
        SeleniumDAO.click(saveButton);

        WebElement okButton = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sa-confirm-button-container']//button[@class = 'confirm']", driver);
        SeleniumDAO.click(okButton);

        try
        {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = '" + questions.get(0) + "']")));
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value = '" + questions.get(1) + "']")));
            return "Test OK. The question fields were created and apear on the table.";
        } catch (Exception e)
        {
            e.printStackTrace();
            return "ERROR. The questions were created but do not apear on the table";
        }
    }

    public static void configurePages(WebDriver driver, WebDriverWait waiting, boolean ticket) throws InterruptedException, IOException {
        WebElement configurePagesTab = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'auxSubItems']//p[@id = 'edit_showflow_pages']/a", driver);
        SeleniumDAO.click(configurePagesTab);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow-pages")));
        //Creates a final page
        WebElement pageNameInput = SeleniumDAO.selectElementBy("id", "page-name", driver);
        pageNameInput.sendKeys("Final page");

        WebElement finalRadioButton = SeleniumDAO.selectElementBy("id", "page-final", driver);
        SeleniumDAO.click(finalRadioButton);

        WebElement addPageButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/add2.png']", driver);
        SeleniumDAO.click(addPageButton);

        Thread.sleep(1000);
        WebElement configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[1]//a[@class = 'edit-page-template']", driver);
        SeleniumDAO.click(configurePage);
        configureFinalPage(driver, waiting, ticket); //Configures final page

        //Creates a initial page
        pageNameInput = SeleniumDAO.selectElementBy("id", "page-name", driver);
        pageNameInput.clear();
        pageNameInput.sendKeys("Initial page");

        WebElement initialRadioButton = SeleniumDAO.selectElementBy("id", "page-initial", driver);
        SeleniumDAO.click(initialRadioButton);

        addPageButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/add2.png']", driver);
        SeleniumDAO.click(addPageButton);

        Thread.sleep(1000);
        configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[2]//a[@class = 'edit-page-template']", driver);
        SeleniumDAO.click(configurePage);
        configureInitialPage(driver, waiting, ticket); //Configures initial page

        pageNameInput = SeleniumDAO.selectElementBy("id", "page-name", driver);
        pageNameInput.clear();
        pageNameInput.sendKeys("Mid page");

        Thread.sleep(500);
        addPageButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/add2.png']", driver);
        SeleniumDAO.click(addPageButton);

        Thread.sleep(1000);
        configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[3]//a[@class = 'edit-page-template']", driver);
        SeleniumDAO.click(configurePage);
        configureInitialPage(driver, waiting, ticket); //Misma configuracion que la inicial porque solo se crea para comprobar la union de paginas

    }


    public static void configureInitialPage(WebDriver driver, WebDriverWait waiting, boolean ticket) throws InterruptedException, IOException
    {
        SeleniumDAO.switchToFrame("showflow_page_editor", driver);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'sidebar-fields']")));

        WebElement thirdSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '3']", driver);
        SeleniumDAO.click(thirdSquareRow);
        Thread.sleep(250);
        WebElement secondSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '2']", driver);
        SeleniumDAO.click(secondSquareRow);

        Actions actions = new Actions(driver);

        List<WebElement> elementsToDrag = driver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'contact']"));
        List<WebElement> elementsDroppable = driver.findElements(By.xpath("//ul[@id = 'showflow-canvas']//div[contains(@class, 'container ui-droppable')]"));
        for(int i = 0; i < elementsToDrag.size(); i++)
        {
            actions.dragAndDrop(elementsToDrag.get(i), elementsDroppable.get(i)).perform();
        }

        Thread.sleep(2000);
        if(!ticket) Utils.takeScreenshot("./ParteDeShowflowOut/screenshotInitialPage", driver);
        else Utils.takeScreenshot("./ParteDeSocialmediaOut/screenshotInitialPage", driver);

        SeleniumDAO.switchToDefaultContent(driver);
        WebElement backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", driver);
        SeleniumDAO.click(backButton);
    }

    public static void configureFinalPage(WebDriver driver, WebDriverWait waiting, boolean ticket) throws InterruptedException, IOException
    {
        SeleniumDAO.switchToFrame("showflow_page_editor", driver);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'sidebar-fields']")));

        if(ticket) //Showflow de tipo ticket
        {
            WebElement fourthSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '4']", driver);
            SeleniumDAO.click(fourthSquareRow);
            Thread.sleep(250);
            fourthSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '4']", driver);
            SeleniumDAO.click(fourthSquareRow);
            Thread.sleep(250);
            WebElement firstSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '1']", driver);
            SeleniumDAO.click(firstSquareRow);
        } else { //Showflow de tipo contacto
            WebElement secondSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '2']", driver);
            SeleniumDAO.click(secondSquareRow);
            Thread.sleep(250);
            WebElement thirdSquareRow = SeleniumDAO.selectElementBy("xpath", "//div[@class = 'sel-cols']//div[@data-cols = '3']", driver);
            SeleniumDAO.click(thirdSquareRow);
        }


        Actions actions = new Actions(driver);

        //Get the showflow fields
        WebElement showflowOrTicketFieldsTab;
        List<WebElement> showflowOrTicketFieldsToDrag;
        if(ticket)
        {
            showflowOrTicketFieldsTab = SeleniumDAO.selectElementBy("id", "ticket-fields", driver);
            SeleniumDAO.click(showflowOrTicketFieldsTab);
            showflowOrTicketFieldsToDrag = driver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'workflow']"));
            showflowOrTicketFieldsToDrag.addAll(driver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'ticket']")));
        } else
        {
            showflowOrTicketFieldsTab = SeleniumDAO.selectElementBy("id", "workflow-fields", driver);
            SeleniumDAO.click(showflowOrTicketFieldsTab);
            showflowOrTicketFieldsToDrag = driver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'workflow']"));
        }


        //Get the action fields
        WebElement actionFieldsTab = SeleniumDAO.selectElementBy("id", "action-fields", driver);
        SeleniumDAO.click(actionFieldsTab);
        List<WebElement> elementsToDrag = driver.findElements(By.xpath("//div[@id = 'sidebar-fields']/div[@data-context = 'action']"));
        elementsToDrag.addAll(showflowOrTicketFieldsToDrag); //Concat showflow fields and action fields

        List<WebElement> elementsDroppable = driver.findElements(By.xpath("//ul[@id = 'showflow-canvas']//div[contains(@class, 'container ui-droppable')]"));

        for(int i = 0; i < elementsToDrag.size(); i++)
        {
            if(ticket)
            {
                //Cuando i = 1 ya ha añadido el campo status y cambia de pestaña para añadir los campos de ticket
                if(i == 1) SeleniumDAO.click(showflowOrTicketFieldsTab);
                actions.dragAndDrop(elementsToDrag.get(i), elementsDroppable.get(i)).perform();
            } else
            {
                //Cuando i = 3, ya h añadido las tipologias y cambia de pestaña para añadir los campos de showflow
                if(i == 3) SeleniumDAO.click(showflowOrTicketFieldsTab);
                actions.dragAndDrop(elementsToDrag.get(i), elementsDroppable.get(i)).perform();
            }
        }


        Thread.sleep(2000);
        if(!ticket) Utils.takeScreenshot("./ParteDeShowflowOut/screenshotFinalPage", driver);
        else Utils.takeScreenshot("./ParteDeSocialmediaOut/screenshotFinalPage", driver);

        SeleniumDAO.switchToDefaultContent(driver);
        WebElement backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", driver);
        SeleniumDAO.click(backButton);
    }

    public static void configureJoints(WebDriver driver, WebDriverWait waiting) throws InterruptedException {
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit_showflow_joints']")));
        WebElement configureJoints = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_joints']", driver);
        Thread.sleep(300);
        SeleniumDAO.click(configureJoints);

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("add-input-field")));
        Select inputFieldSelector = SeleniumDAO.findSelectElementBy("id", "add-input-field", driver);
        inputFieldSelector.selectByValue("name");

        WebElement inputValue = SeleniumDAO.selectElementBy("id", "input-match-value", driver);
        inputValue.sendKeys("Sebas");

        Select outputPage = SeleniumDAO.findSelectElementBy("id", "add-output-page", driver);
        outputPage.selectByVisibleText("Final page");

        WebElement addJoint = SeleniumDAO.selectElementBy("xpath", "//a[@class = 'add-joint']/img", driver);
        SeleniumDAO.click(addJoint);
    }

    public static String checkContactFields(WebDriver driver, WebDriverWait waiting, String showflowOptionsGroupName, String showflowAuxField)
    {
        try
        {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow_fields")));

            SeleniumDAO.selectElementBy("xpath", "//td[input[@value = 'aux1']]/preceding-sibling::td/input[@type = 'checkbox' and @checked  = '']", driver);
            SeleniumDAO.selectElementBy("xpath", "//td[input[@value = 'city']]/preceding-sibling::td/input[@type = 'checkbox' and @checked  = '']", driver);
            SeleniumDAO.selectElementBy("xpath", "//td[input[@value = 'country']]/preceding-sibling::td/input[@type = 'checkbox' and @checked  = '']", driver);

            /*WebElement aux1Checkbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1' and contains(@class, 'contact-field visible isactive')]", driver);
            WebElement cityCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'city' and contains(@class, 'contact-field visible isactive')]", driver);
            WebElement clienteCheckbox = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'country' and contains(@class, 'contact-field visible isactive')]", driver);*/

            WebElement aux1NameInput = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//td/input[@value = '" + showflowAuxField + "']", driver);
            WebElement aux1TypeSelected = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//select[@class = 'type']" +
                    "//option[@value = 'radiobutton' and @selected = '']", driver);
            WebElement aux1OptionGroup = SeleniumDAO.selectElementBy("xpath", "//tr[@data-fieldname = 'aux1']//select[@class = 'optiongroup active']" +
                    "//option[contains(., '" + showflowOptionsGroupName + "')]", driver);
            boolean res = aux1OptionGroup.isSelected();
            if(!res) throw new Exception();

            return "Check OK. The contact fields of the cloned showflow match with the original";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The contact fields of the cloned showflow do not match with the original";
        }
    }

    //TODO refactorizar
    public static String checkShowflowFields(WebDriver driver, WebDriverWait waiting, String showflowQuestion1, String showflowQuestion2, String showflowOptionsGroupName, boolean ticket)
    {
        try
        {
            WebElement showflowFieldsTab;
            //Este try catch es porque el elemento tiene id distinto en funcion de si es showflow de contacto o de ticket
            if(!ticket)
            {
                showflowFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_fields']/a", driver);
            }
            else
            {
                showflowFieldsTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_ticket_fields']/a", driver);
            }

            //waiting.until(ExpectedConditions.elementToBeClickable(showflowFieldsTab));
            SeleniumDAO.click(showflowFieldsTab);

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow_fields")));

            WebElement question1Name = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + showflowQuestion1 + "']", driver);
            WebElement question2Name = SeleniumDAO.selectElementBy("xpath", "//input[@value = '" + showflowQuestion2 + "']", driver);

            WebElement question1Type = SeleniumDAO.selectElementBy("xpath", "//select[@class = 'type']" +
                    "//option[@value = 'radiobutton' and @selected = '']", driver);
            WebElement question2Type = SeleniumDAO.selectElementBy("xpath", "//select[@class = 'type']" +
                    "//option[@value = 'checkbox' and @selected = '']", driver);

            WebElement question1OptionGroup = SeleniumDAO.selectElementBy("xpath", "//td[input[@value = '" + showflowQuestion1 + "']]/following-sibling::td//select[@class = 'optiongroup']" +
                    "//option[contains(., '" + showflowOptionsGroupName + "')]", driver);
            WebElement question2OptionGroup = SeleniumDAO.selectElementBy("xpath", "//td[input[@value = '" + showflowQuestion2 + "']]/following-sibling::td//select[@class = 'optiongroup']" +
                    "//option[contains(., '" + showflowOptionsGroupName + "')]", driver);
            boolean res = question2OptionGroup.isSelected() && question1OptionGroup.isSelected();
            if(!res) throw new Exception();

            return "Check OK. The showflow fields of the cloned showflow match with the original";
        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. The showflow fields of the cloned showflow do not match with the original";
        }
    }

    public static String checkPages(WebDriver driver, WebDriverWait waiting, String showflowCopyName, boolean ticket)
    {
        try
        {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']")));
            WebElement showflowTab = SeleniumDAO.selectElementBy("xpath", "//div[@id = 'mainMenu']//li[@id = 'SHOWFLOW']", driver);
            SeleniumDAO.click(showflowTab);

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]")));
            WebElement showflow = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'showflows']//td[contains(., '" + showflowCopyName + "')]", driver);
            SeleniumDAO.click(showflow);

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id = 'edit_showflow_pages']/a")));
            WebElement configurePagesTab = SeleniumDAO.selectElementBy("xpath", "//p[@id = 'edit_showflow_pages']/a", driver);
            SeleniumDAO.click(configurePagesTab);

            Thread.sleep(1000);
            WebElement configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr[1]//a[@class = 'edit-page-template']", driver);
            SeleniumDAO.click(configurePage);

            Thread.sleep(2000);

            if(!ticket) Utils.takeScreenshot("./ParteDeShowflowOut/screenshotInitialPageCopy", driver);
            else Utils.takeScreenshot("./ParteDeSocialmediaOut/screenshotInitialPageCopy", driver);

            WebElement backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", driver);
            SeleniumDAO.click(backButton);

            Thread.sleep(1000);
            configurePage = SeleniumDAO.selectElementBy("xpath", "//table[@id = 'workflow-pages']//tbody/tr//td[contains(., 'Final page')]/following-sibling::td//a[@class = 'edit-page-template']", driver);
            SeleniumDAO.click(configurePage);

            Thread.sleep(2000);

            if(!ticket) Utils.takeScreenshot("./ParteDeShowflowOut/screenshotFinalPageCopy", driver);
            else Utils.takeScreenshot("./ParteDeSocialmediaOut/screenshotFinalPageCopy", driver);

            backButton = SeleniumDAO.selectElementBy("xpath", "//img[@src = 'imagenes/menus/back.png']", driver);
            SeleniumDAO.click(backButton);

            Thread.sleep(2000);//Si no se espera sale un error en la web

            waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, 'Joints.php')]")));
            WebElement configurePageJoints = SeleniumDAO.selectElementBy("xpath", "//a[contains(@href, 'Joints.php')]", driver);
            SeleniumDAO.click(configurePageJoints);

            try
            {
                //espera hasta que encuentra la union entre paginas hecha
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr//td[contains(., 'Initial page')]/following-sibling::td[contains(., 'Final page')]")));
            } catch (Exception e)
            {
                e.printStackTrace();
                return e.toString() + "\nERROR. The page joint was not cloned OK";
            }
            Thread.sleep(1000);
            return "Check OK. Two screenshots have been taken to compare with the screenshots taken of the original pages";

        } catch (Exception e)
        {
            e.printStackTrace();
            return e.toString() + "\nERROR. Unexpected exception";
        }
    }

}
