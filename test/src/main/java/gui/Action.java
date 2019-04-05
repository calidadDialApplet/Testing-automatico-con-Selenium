package gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import main.SeleniumDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Action {

   private GridPane gridParent;
   private int rowIndex;
   private String lastType;
   private boolean textFieldNotGenerated, needToDelete, placeNotGenerated;

   private ComboBox actionType = new ComboBox();
   private ComboBox selectElementBy = new ComboBox();
   private ComboBox selectPlaceBy = new ComboBox();
   private TextField firstValueArgs = new TextField();
   private TextField secondValueArgs = new TextField();
   private Label value = new Label();
   private Label infoText = new Label();


    public Action(GridPane gridParent, int rowIndex) {

        this.gridParent = gridParent;
        this.rowIndex = rowIndex;

        actionType = new ComboBox<>();
        actionType.setItems(FXCollections.observableArrayList(gui.H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.setItems(FXCollections.observableArrayList(gui.H2DAO.getTypeAction()));
            actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (actionType.getValue().toString()) {
                case "Click":
                    textFieldNotGenerated = true;
                    if(needToDelete) {
                        setDefaultAction(gridParent,lastType);
                    }
                    needToDelete = true;
                    lastType = "Click";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if (textFieldNotGenerated) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    break;
                case "DragAndDrop":
                    textFieldNotGenerated = true;
                    placeNotGenerated = true;
                    if(needToDelete) {
                        setDefaultAction(gridParent,lastType);
                    }
                    needToDelete = true;
                    lastType = "DragAndDrop";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if (placeNotGenerated) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                            selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                            //selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                            gridParent.addRow(rowIndex, selectPlaceBy);
                            placeNotGenerated = false;
                            selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                            {
                                if (textFieldNotGenerated) {
                                    secondValueArgs = new TextField();
                                    gridParent.addRow(rowIndex, secondValueArgs);
                                }
                                textFieldNotGenerated = false;
                            });
                        }
                    });
                    break;
                case "WriteTo":
                    textFieldNotGenerated = true;
                    if(needToDelete) {
                        setDefaultAction(gridParent,lastType);

                    }
                    needToDelete = true;
                    lastType = "WriteTo";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (textFieldNotGenerated) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);

                            value.setText("Value");
                            gridParent.addRow(rowIndex,value);

                            secondValueArgs = new TextField();
                            gridParent.addRow(rowIndex,secondValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    break;
                case "ReadFrom":
                    textFieldNotGenerated = true;
                    if(needToDelete) {
                        setDefaultAction(gridParent,lastType);
                    }
                    needToDelete = true;
                    lastType = "ReadFrom";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (textFieldNotGenerated) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    break;

                // WIP
                /*case "Waiting":
                    firstValueArgs = new TextField();
                    gridParent.addRow(rowIndex, firstValueArgs);

                    infoText.setText("Element");
                    gridParent.addRow(rowIndex, infoText);

                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (textFieldNotGenerated) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);

                            value.setText("Value");
                            gridParent.addRow(rowIndex,value);

                            secondValueArgs = new TextField();
                            gridParent.addRow(rowIndex,secondValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });

                    break;*/
                default:
                    break;
            }
        });
    }

   public Action(String actionType, String selectElementBy, String firstValueArgs, String selectPlaceBy, String secondValueArgs){
        this.actionType.setValue(actionType);
        this.selectElementBy.setValue(selectElementBy);
        this.firstValueArgs.setText(firstValueArgs);
        this.selectPlaceBy.setValue(selectPlaceBy);
        this.secondValueArgs.setText(secondValueArgs);
   }

   public void executeTrial(WebDriver driver){
        switch (this.actionType.getValue().toString()){
            case "Click":
                WebElement clickElement  = SeleniumDAO.selectElementBy(this.selectElementBy.getValue().toString(), this.firstValueArgs.getText(), driver);
                SeleniumDAO.click(clickElement);
                break;
            case "DragAndDrop":
                WebElement dragElement = SeleniumDAO.selectElementBy(this.selectElementBy.getValue().toString(), this.firstValueArgs.getText(), driver);
                WebElement dropPlaceElement = SeleniumDAO.selectElementBy(this.selectPlaceBy.getValue().toString(), this.secondValueArgs.getText(), driver);
                SeleniumDAO.dragAndDropAction(dragElement,dropPlaceElement,driver);
                break;
            case "WriteTo":
                System.out.println(secondValueArgs.getText());
                WebElement writeToElement = SeleniumDAO.selectElementBy(selectElementBy.getValue().toString(),firstValueArgs.getText(),driver);
                writeToElement.sendKeys(secondValueArgs.getText());
                //SeleniumDAO.writeInTo(writeToElement,this.secondValueArgs.getText());
                break;
            case "ReadFrom":
                break;
            default:
                break;

        }

   }

   public void setDefaultAction(GridPane gridParent, String lastType)
   {
       switch (lastType){
           case "Click":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
               break;
           case "DragAndDrop":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
               break;
           case "WriteTo":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,secondValueArgs,value);
               break;
           case "ReadFrom":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
               break;
           case "default":
               break;
       }
   }

   @Override
   public String toString() {
        return "Action{" +
                "actionType=" + actionType.getValue().toString() +
                ", selectElementBy=" + selectElementBy.getValue().toString() +
                ", selectPlaceBy=" + selectPlaceBy.getValue().toString() +
                ", firstValueArgs=" + firstValueArgs.getText() +
                ", secondValueArgs=" + secondValueArgs.getText() +
                '}';
    }

    public String getActionType() {
        return actionType.getValue().toString();
    }

    public String getSelectElementBy() {
        return selectElementBy.getValue().toString();
    }

    public String getSelectPlaceBy() {
        return selectPlaceBy.getValue().toString();
    }

    public String getFirstValueArgs() {
        return firstValueArgs.getText();
    }

    public String getSecondValueArgs() {
        return secondValueArgs.getText();
    }
}