package gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import main.SeleniumDAO;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;



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
        actionType.setMinWidth(100);

        actionType.setItems(FXCollections.observableArrayList(gui.H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
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
                    selectElementBy.setMinWidth(100);

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
                            //selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementByString()));
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
                    selectElementBy.setItems(FXCollections.observableArrayList(gui.H2DAO.getSelectElementByString()));
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

   public Action(String actionType, String selectElementBy, String firstValueArgs, String selectPlaceBy, String secondValueArgs)
   {
        if(actionType.matches("1|2|3|4")){
            this.actionType.setValue(getActionTypeId(actionType));
        } else {
            this.actionType.setValue(actionType);
        }
        if(selectElementBy.matches("1|2|3|4|5"))
        {
            this.selectElementBy.setValue(getSelectElementById(selectElementBy));
        } else {
            this.selectElementBy.setValue(selectElementBy);
        }
       this.firstValueArgs.setText(firstValueArgs);
       if(selectElementBy.matches("1|2|3|4|5")) {
           this.selectPlaceBy.setValue(getSelectElementById(selectPlaceBy));
       }else{
           this.selectPlaceBy.setValue(selectPlaceBy);
       }
       this.secondValueArgs.setText(secondValueArgs);
   }



    public Action(GridPane gridParent, int rowIndex, String actionTypeValue, String selectElementByValue, String firstValueArgsValue, String selectPlaceByValue, String secondValueArgsValue) {

        this.gridParent = gridParent;
        this.rowIndex = rowIndex;

        actionType = new ComboBox<>();
        actionType.setItems(FXCollections.observableArrayList(gui.H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
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
                            firstValueArgs.setText(firstValueArgsValue);
                            gridParent.addRow(rowIndex, firstValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    selectElementBy.setValue(selectElementByValue);
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
                            firstValueArgs.setText(firstValueArgsValue);
                            gridParent.addRow(rowIndex, firstValueArgs);
                            selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(gui.H2DAO.getSelectElementBy()));
                            //selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementByString()));

                            gridParent.addRow(rowIndex, selectPlaceBy);
                            placeNotGenerated = false;
                            selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                            {
                                if (textFieldNotGenerated) {
                                    secondValueArgs = new TextField();
                                    secondValueArgs.setText(secondValueArgsValue);
                                    gridParent.addRow(rowIndex, secondValueArgs);
                                }
                                textFieldNotGenerated = false;
                            });
                            selectPlaceBy.setValue(selectPlaceByValue);
                        }
                    });
                    selectElementBy.setValue(selectElementByValue);
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
                            firstValueArgs.setText(firstValueArgsValue);
                            gridParent.addRow(rowIndex, firstValueArgs);

                            value.setText("Value");
                            gridParent.addRow(rowIndex,value);

                            secondValueArgs = new TextField();
                            secondValueArgs.setText(secondValueArgsValue);
                            gridParent.addRow(rowIndex,secondValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    selectElementBy.setValue(selectElementByValue);
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
                            firstValueArgs.setText(firstValueArgsValue);
                            gridParent.addRow(rowIndex, firstValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    selectElementBy.setValue(selectElementByValue);
                    break;
                default:
                    break;
            }
        });
        actionType.getSelectionModel().select(actionTypeValue);
    }

   public String executeAction(WebDriver driver){
        String result = "Fail";
        try {
            switch (this.actionType.getValue().toString()) {
                case "Click":
                    WebElement clickElement = SeleniumDAO.selectElementBy(this.selectElementBy.getValue().toString(), this.firstValueArgs.getText(), driver);
                    SeleniumDAO.click(clickElement);
                    result = "Ok";
                    break;
                case "DragAndDrop":
                    WebElement dragElement = SeleniumDAO.selectElementBy(this.selectElementBy.getValue().toString(), this.firstValueArgs.getText(), driver);
                    WebElement dropPlaceElement = SeleniumDAO.selectElementBy(this.selectPlaceBy.getValue().toString(), this.secondValueArgs.getText(), driver);
                    SeleniumDAO.dragAndDropAction(dragElement, dropPlaceElement, driver);
                    result = "Ok";
                    break;
                case "WriteTo":
                    WebElement writeToElement = SeleniumDAO.selectElementBy(selectElementBy.getValue().toString(), firstValueArgs.getText(), driver);
                    //writeToElement.sendKeys(secondValueArgs.getText());
                    SeleniumDAO.writeInTo(writeToElement,this.secondValueArgs.getText());
                    result = "Ok";
                    break;
                case "ReadFrom":
                    WebElement readFromElement = SeleniumDAO.selectElementBy(selectElementBy.getValue().toString(),firstValueArgs.getText(),driver);
                    result = SeleniumDAO.readFrom(readFromElement);
                    break;
                default:
                    break;
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }

   }

   public void setDefaultAction(GridPane gridParent, String lastType)
   {
       switch (lastType){
           case "Click":
           case "ReadFrom":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
               break;
           case "DragAndDrop":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
               break;
           case "WriteTo":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,secondValueArgs,value);
               break;
           case "default":
               break;
       }
   }

    public static String getActionTypeId(String actionType)
    {
        String type = "NULL"; // No action type
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
            default:
                break;
        }
        return type;
    }

    public static String getSelectElementById(String actionType)
    {
        String SelectBy = "NULL"; // No action type
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

    public String getActionTypeString() {
        return actionType.getValue().toString();
    }

    public String getSelectElementByString() {
        return selectElementBy.getValue().toString();
    }

    public String getSelectPlaceByString() {
        return selectPlaceBy.getValue().toString();
    }

    public String getFirstValueArgsString() {
        return firstValueArgs.getText();
    }

    public String getSecondValueArgsString() {
        return secondValueArgs.getText();
    }


    public ComboBox getActionType() {
        return actionType;
    }

    public ComboBox getSelectElementBy() {
        return selectElementBy;
    }

    public ComboBox getSelectPlaceBy() {
        return selectPlaceBy;
    }

    public TextField getFirstValueArgs() {
        return firstValueArgs;
    }

    public TextField getSecondValueArgs() {
        return secondValueArgs;
    }
}
