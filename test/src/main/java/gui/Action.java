package gui;


import main.SeleniumDAO;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import persistence.H2DAO;

import java.util.ArrayList;


// TODO: Refactor this as ActionController, use it from MainController
//       Add Action, Trial, etc DataModels and Controllers (https://stackoverflow.com/questions/32342864/applying-mvc-with-javafx)
public class Action {

   private String actionTypeS = "";
   private String selectElementByS = "";
   private String selectPlaceByS = "";
   private String firstValueArgsS = "";
   private String secondValueArgsS = "";


    public Action(String actionTypeS, String selectElementByS, String firstValueArgsS, String selectPlaceByS, String secondValueArgsS) {

        if (actionTypeS.matches("1|2|3|4|5|6|7|8")){
            this.actionTypeS = getActionTypeId(actionTypeS);
        }else {
            this.actionTypeS = actionTypeS;
        }
        if(selectElementByS.matches("1|2|3|4|5"))
        {
            this.selectElementByS = getSelectElementById(selectElementByS);
        }else {
            this.selectElementByS = selectElementByS;
        }
        this.firstValueArgsS = firstValueArgsS;
        if (selectPlaceByS.matches("1|2|3|4|5")){
            this.selectPlaceByS = getSelectElementById(selectPlaceByS);
        } else {
            this.selectPlaceByS = selectPlaceByS;
        }

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
                SelectBy = "id";
                break;
            case "2":
                SelectBy = "xpath";
                break;
            case "3":
                SelectBy = "cssSelector";
                break;
            case "4":
                SelectBy = "className";
                break;
            case "5":
                SelectBy = "name";
                break;
            default:
                break;
        }
        return SelectBy;
    }


   public String executeAction(WebDriver driver, ArrayList<Variable> variables){

        String result = "Fail";


       String trialID = variables.get(0).getVariableTrial();

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
                    result = "Ok"; // TEST
                    break;
                case "WaitTime":
                    getValueVariables(variables);
                    SeleniumDAO.implicitWait(Integer.parseInt(this.firstValueArgsS));
                    result = "Ok";
                    break;
                case "SwitchDefault":
                    getValueVariables(variables);
                    SeleniumDAO.switchToDefaultContent(driver);
                    result = "Ok";
                default:
                    break;
            }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return result;
        }

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
