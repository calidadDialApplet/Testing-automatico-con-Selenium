package gui;


import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import persistence.H2DAO;

import java.util.ArrayList;
import java.util.regex.Pattern;


// TODO: Refactor this as ActionController, use it from MainController
//       Add Action, Trial, etc DataModels and Controllers (https://stackoverflow.com/questions/32342864/applying-mvc-with-javafx)
public class Action {

   private String actionTypeS = "";
   private String selectElementByS = "";
   private String selectPlaceByS = "";
   private String firstValueArgsS = "";
   private String secondValueArgsS = "";

    private static Pattern GLOBAL_VARIABLE_PATTERN = Pattern.compile("\\€\\{\\S+\\}");
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\S+\\}");



    public Action(String actionTypeS, String selectElementByS, String firstValueArgsS, String selectPlaceByS, String secondValueArgsS) {

        this.actionTypeS = actionTypeS;
        this.selectElementByS = selectElementByS;
        this.firstValueArgsS = firstValueArgsS;
        this.selectPlaceByS = selectPlaceByS;
        this.secondValueArgsS = secondValueArgsS;
    }

    public static String getActionTypeId(String actionType)
    {
        String type = actionType; // No action type
        switch (actionType){
            case "1":
                type = "Click";
                break;
            case "2":
                type = "DragAndDrop";
                break;
            case "3":
                type = "WriteTo";
                break;
            case "4":
                type = "ReadFrom";
                break;
            case "5":
                type = "SwitchTo";
                break;
            case "6":
                type = "Waiting For";
                break;
            case "7":
                type = "WaitTime";
                break;
            case "8":
                type = "SwitchDefault";
                break;
            case "9":
                type = "NavigateTo";
                break;
            case "10":
                type = "Press Key";
                break;
            case "11":
                type = "ScreenShot";
                break;
            case "12":
                type = "Refresh";
                break;
            case "13":
                type = "SelectOptionByValue";
                break;
            case "14":
                type = "GetAttribute";
                break;
            default:
                break;
        }
        return type;
    }

    public static String getSelectElementById(String actionType)
    {
        String SelectBy = actionType; // No action type
        switch (actionType){
            case "1":
                SelectBy = "";
                break;
            case "2":
                SelectBy = "id";
                break;
            case "3":
                SelectBy = "xpath";
                break;
            case "4":
                SelectBy = "cssSelector";
                break;
            case "5":
                SelectBy = "className";
                break;
            case "6":
                SelectBy = "name";
                break;
            default:
                break;
        }
        return SelectBy;
    }


   public String executeAction(WebDriver driver, ArrayList<Variable> variables, String trialName, Thread thread){

       String result = "Fail";

       String trialID = H2DAO.getTrialID(trialName);

       substituteVariables(variables);


       try
        {
            switch (actionTypeS) {
                case "Click":
                    getValueVariables(variables);
                    WebElement clickElement = SeleniumDAO.selectElementBy(this.selectElementByS, this.firstValueArgsS, driver);
                    SeleniumDAO.click(clickElement);
                    result = "Ok";
                    break;
                case "DragAndDrop":
                    getValueVariables(variables);
                    WebElement dragElement = SeleniumDAO.selectElementBy(this.selectElementByS, this.firstValueArgsS, driver);
                    WebElement dropPlaceElement = SeleniumDAO.selectElementBy(this.selectPlaceByS, this.secondValueArgsS, driver);
                    SeleniumDAO.dragAndDropAction(dragElement, dropPlaceElement, driver);
                    result = "Ok";
                    break;
                case "WriteTo":
                    getValueVariables(variables);
                    WebElement writeToElement = SeleniumDAO.selectElementBy(this.selectElementByS, this.firstValueArgsS, driver);
                    SeleniumDAO.writeInTo(writeToElement,this.secondValueArgsS);
                    result = "Ok";
                    break;
                case "ReadFrom":
                    WebElement readFromElement = SeleniumDAO.selectElementBy(this.selectElementByS,this.firstValueArgsS,driver);
                    result = SeleniumDAO.readFrom(readFromElement);
                    H2DAO.updateTrialVariable(trialID, this.secondValueArgsS,result);
                    break;
                case "SwitchTo":
                    getValueVariables(variables);
                    SeleniumDAO.switchToFrame(this.firstValueArgsS, driver);
                    result = "Ok";
                    break;
                case "Waiting For":
                    getValueVariables(variables);
                    SeleniumDAO.waitForElement(Integer.parseInt(this.secondValueArgsS),this.selectElementByS, this.firstValueArgsS ,driver);
                    result = "Ok";
                    break;
                case "WaitTime":
                    getValueVariables(variables);
                    SeleniumDAO.implicitWait(Integer.parseInt(this.firstValueArgsS), driver, thread);
                    result = "Ok";
                    break;
                case "SwitchDefault":
                    getValueVariables(variables);
                    SeleniumDAO.switchToDefaultContent(driver);
                    result = "Ok";
                    break;
                case "NavigateTo":
                    SeleniumDAO.navigateTo(this.firstValueArgsS, driver);
                    result = "Ok";
                    break;
                case "ScreenShot":
                    SeleniumDAO.screenShot(driver);
                    result = "Ok";
                    break;
                case "Press Key":
                    WebElement pressKeyElement = null;
                    if (this.selectPlaceByS.equals("PageUp") || this.selectPlaceByS.equals("PageDown"))
                    {
                        pressKeyElement = driver.findElement(By.cssSelector("body"));
                    }else {
                        pressKeyElement =SeleniumDAO.selectElementBy(this.selectElementByS, this.firstValueArgsS, driver);
                    }
                    SeleniumDAO.pressKey(this.selectPlaceByS, pressKeyElement);
                    result = "Ok";
                    break;
                case "Refresh":
                    SeleniumDAO.refreshPage(driver);
                    result = "Ok";
                    break;
                case "GetAttribute":
                    WebElement readAttributeFrom = SeleniumDAO.selectElementBy(this.selectElementByS,this.firstValueArgsS,driver);
                    result = SeleniumDAO.getAttribute(readAttributeFrom);
                    H2DAO.updateTrialVariable(trialID, this.secondValueArgsS,result);
                    break;
                case "SelectOptionByValue":
                    Select selectElement = new Select(SeleniumDAO.selectElementBy(this.selectElementByS, this.firstValueArgsS, driver));
                    SeleniumDAO.selectOption(selectElement,this.secondValueArgsS);
                    result = "Ok";
                    break;
                default:
                    break;
            }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getClass().getName().substring(e.getClass().getName().lastIndexOf(".") + 1));
            result = manageException(e);
            return result;
        }

   }

    private void substituteVariables(ArrayList<Variable> variables) {
        //if (this.firstValueArgsS.matches(String.valueOf(VARIABLE_PATTERN))) {
            for (Variable variable : variables) {
                if (this.firstValueArgsS.contains(variable.getVariableName())) {
                    this.firstValueArgsS = this.firstValueArgsS.replace(variable.getVariableName(), variable.getValue());
                }

                if (!this.getActionTypeS().equals("ReadFrom") || !this.getActionTypeS().equals("GetAttribute")) {
                    if (this.secondValueArgsS.contains(variable.getVariableName())) {
                        this.secondValueArgsS = this.secondValueArgsS.replace(variable.getVariableName(), variable.getValue());
                    }
                    if (this.firstValueArgsS.contains(variable.getVariableName())) {
                        this.firstValueArgsS = this.firstValueArgsS.replace(variable.getVariableName(), variable.getValue());
                    }
                }
            }
        //}

        //if (this.firstValueArgsS.matches(String.valueOf(GLOBAL_VARIABLE_PATTERN)))
        //{
           ArrayList<Global_Variable> global_variables = H2DAO.getGlobalVariables();
           for (Global_Variable variable : global_variables)
           {
             if (this.firstValueArgsS.contains(variable.getVariableName()))
             {
                 this.firstValueArgsS = this.firstValueArgsS.replace(variable.getVariableName(), variable.getValue());
             }

               if (!this.getActionTypeS().equals("ReadFrom") || !this.getActionTypeS().equals("GetAttribute")) {
                   if (this.secondValueArgsS.contains(variable.getVariableName())) {
                       this.secondValueArgsS = this.secondValueArgsS.replace(variable.getVariableName(), variable.getValue());
                   }
               }
           }
        //}
    }


    private String manageException(Exception exception)
   {
       String message = "";
       String type = exception.getClass().getName().substring(exception.getClass().getName().lastIndexOf(".") + 1);
       switch (type)
       {
           case "NoSuchElementException":
               message = "No se ha podido identificar el elemento con los parámetros indicados.";
               break;
           case "TimeoutException":
               message = "Se ha excedido el tiempo máximo establecido para identificar el elemento.";
               break;
           case "InvalidSelectorException":
               message = "El selector utilizado para encontrar un elemento no devuelve un elemento web.";
               break;
           case "ElementNotInteractableException":
               message = "El elemento seleccionado no es interactuable con la acción indicada.";
               break;
           case "NoSuchFrameException":
               message = "No se puede cambiar a un frame no válido o que no está disponible.";
               break;
           case "NoSuchSessionException":
               message = "¿Has cerrado el navegador o qué? Se ha perdido la sesión en uso.";
               break;
           case "WebDriverException":
               message = "El webdriver estaba realizando la acción inmediatamente después de cerrar el navegador.";
               message = "Has cerrado el puto navegador mientras estaba trabajando ...";
               break;
           case "PatternSyntaxException":
               break;
           case "StaleElementReferenceException":
               message = "Elemento obsoleto en el DOM";
               break;
           case "UnhandledAlertException":
               message = "No se puede realizar la acción en la alerta";
               break;
           case "ElementClickInterceptedException":
               message = "Otro elemento con la misma clase o con el mismo xpath / css aparece en la pantalla";
               break;
       }

       return message;
   }

    private void getValueVariables(ArrayList<Variable> variables)
    {
        for (Variable variable : variables)
        {
            if (this.firstValueArgsS.contains(variable.getVariableName()))
            {
                this.firstValueArgsS = this.firstValueArgsS.replaceAll(variable.getVariableName(), variable.getValue());
            }

            if (this.secondValueArgsS.contains(variable.getVariableName()))
            {
                this.secondValueArgsS = this.secondValueArgsS.replaceAll(variable.getVariableName(),variable.getValue());
            }
        }
    }



    public String getActionTypeS() {
        return actionTypeS;
    }

    public String getSelectElementByS() {
        return selectElementByS;
    }

    public String getSelectPlaceByS() {
        return selectPlaceByS;
    }

    public String getFirstValueArgsS() {
        return firstValueArgsS;
    }

    public String getSecondValueArgsS() {
        return secondValueArgsS;
    }

    public void setActionTypeS(String actionTypeS) {
        this.actionTypeS = actionTypeS;
    }

    public void setSelectElementByS(String selectElementByS) {
        this.selectElementByS = selectElementByS;
    }

    public void setSelectPlaceByS(String selectPlaceByS) {
        this.selectPlaceByS = selectPlaceByS;
    }

    public void setFirstValueArgsS(String firstValueArgsS) {
        this.firstValueArgsS = firstValueArgsS;
    }

    public void setSecondValueArgsS(String secondValueArgsS) {
        this.secondValueArgsS = secondValueArgsS;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actionTypeS='" + actionTypeS + '\'' +
                ", selectElementByS='" + selectElementByS + '\'' +
                ", selectPlaceByS='" + selectPlaceByS + '\'' +
                ", firstValueArgsS='" + firstValueArgsS + '\'' +
                ", secondValueArgsS='" + secondValueArgsS + '\'' +
                '}';
    }
}
