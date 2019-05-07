package gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.SeleniumDAO;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import persistence.H2DAO;

// TODO: Refactor this as ActionController, use it from MainController
//       Add Action, Trial, etc DataModels and Controllers (https://stackoverflow.com/questions/32342864/applying-mvc-with-javafx)
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

        //StackPane stackPane = new StackPane();





        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        //dragComboBox(actionType);
        //addDropHandling(stackPane);
        //stackPane.getChildren().add(actionType);
        //gridParent.addRow(rowIndex, stackPane);

        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (actionType.getValue().toString()) {
                case "Click":

                    initialiceCheckBox(gridParent);
                    lastType = "Click";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                    placeNotGenerated = true;
                    initialiceCheckBox(gridParent);
                    lastType = "DragAndDrop";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if (placeNotGenerated) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                            selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                    initialiceCheckBox(gridParent);
                    lastType = "WriteTo";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                    initialiceCheckBox(gridParent);
                    lastType = "ReadFrom";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                case "SwitchTo":
                    initialiceCheckBox(gridParent);
                    lastType = "SwitchTo";

                    if (textFieldNotGenerated) {
                        firstValueArgs = new TextField();
                        gridParent.addRow(rowIndex, firstValueArgs);
                    }
                    textFieldNotGenerated = false;
                    break;
                case "Waiting":
                    initialiceCheckBox(gridParent);
                    lastType = "Waiting";
                    firstValueArgs = new TextField();
                    gridParent.addRow(rowIndex, firstValueArgs);

                    value.setText("Element");
                    gridParent.addRow(rowIndex,value);

                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(persistence.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (textFieldNotGenerated) {
                            secondValueArgs = new TextField();
                            gridParent.addRow(rowIndex,secondValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    break;
                case "WaitTime":
                    initialiceCheckBox(gridParent);
                    lastType = "WaitTime";

                    if (textFieldNotGenerated) {
                        firstValueArgs = new TextField();
                        gridParent.addRow(rowIndex, firstValueArgs);
                    }
                    textFieldNotGenerated = false;
                    break;
                default:
                    break;
            }
        });
    }

    private void initialiceCheckBox(GridPane gridParent) {
        textFieldNotGenerated = true;
        if (needToDelete) {
            setDefaultAction(gridParent, lastType);
        }
        needToDelete = true;
    }


    public Action(String actionType, String selectElementBy, String firstValueArgs, String selectPlaceBy, String secondValueArgs)
   {
        if(actionType.matches("1|2|3|4|5|6|7")){
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
        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (actionType.getValue().toString()) {
                case "Click":
                    initialiceCheckBox(gridParent);
                    lastType = "Click";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if (placeNotGenerated) {
                            firstValueArgs = new TextField();
                            firstValueArgs.setText(firstValueArgsValue);
                            gridParent.addRow(rowIndex, firstValueArgs);
                            selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                    initialiceCheckBox(gridParent);
                    lastType = "WriteTo";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                    initialiceCheckBox(gridParent);
                    lastType = "ReadFrom";
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
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
                case "SwitchTo":
                    initialiceCheckBox(gridParent);
                    lastType = "SwitchTo";

                    if (textFieldNotGenerated) {
                        firstValueArgs = new TextField();
                        firstValueArgs.setText(firstValueArgsValue);
                        gridParent.addRow(rowIndex, firstValueArgs);
                    }
                    textFieldNotGenerated = false;
                    break;
                case "Waiting":
                    initialiceCheckBox(gridParent);
                    lastType = "Waiting";
                    firstValueArgs = new TextField();
                    firstValueArgs.setText(firstValueArgsValue);
                    gridParent.addRow(rowIndex, firstValueArgs);

                    value.setText("Element");
                    gridParent.addRow(rowIndex,value);

                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(persistence.H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (textFieldNotGenerated) {
                            secondValueArgs = new TextField();
                            secondValueArgs.setText(secondValueArgsValue);
                            gridParent.addRow(rowIndex,secondValueArgs);
                        }
                        textFieldNotGenerated = false;
                    });
                    selectElementBy.setValue(selectElementByValue);
                    break;
                case "WaitTime":
                    initialiceCheckBox(gridParent);
                    lastType = "WaitTime";

                    if (textFieldNotGenerated) {
                        firstValueArgs = new TextField();
                        firstValueArgs.setText(firstValueArgsValue);
                        gridParent.addRow(rowIndex, firstValueArgs);
                    }
                    textFieldNotGenerated = false;
                    break;
                default:
                    break;
            }
        });
        actionType.getSelectionModel().select(actionTypeValue);
    }

   public String executeAction(WebDriver driver){

        String result = "Fail";

        try
        {
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
                    result = "Ok";
                    break;
                case "SwitchTo":
                    SeleniumDAO.switchToFrame(this.firstValueArgs.getText(), driver);
                    result = "Ok";
                    break;
                case "Waiting":
                    SeleniumDAO.waitForElement(Integer.parseInt(this.firstValueArgs.getText()),this.selectElementBy.getValue().toString(), this.secondValueArgs.getText() ,driver);
                    result = "Ok";
                    break;
                case "WaitTime":
                    SeleniumDAO.implicitWait(Integer.parseInt(this.firstValueArgs.getText()));
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
           case "Waiting":
               gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,secondValueArgs,value);
               break;
           case "SwitchTo":
           case "WaitTime":
               gridParent.getChildren().removeAll(firstValueArgs);
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
            case "5":
                type = "SwitchTo";
                break;
            case "6":
                type = "Waiting";
                break;
            case "7":
                type = "WaitTime";
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
    /*
    private DataFormat comboBoxFormat = new DataFormat();
    private ComboBox draggingComboBox;
    private void dragComboBox(ComboBox comboBox)
    {
        comboBox.setOnDragDetected(event -> {
            Dragboard db = comboBox.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(comboBox.snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.put(comboBoxFormat, " ");

            db.setContent(cc);
            draggingComboBox = comboBox;
        });
    }

    private void addDropHandling(StackPane pane)
    {
        pane.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasContent(comboBoxFormat)) {
                ((Pane)draggingComboBox.getParent()).getChildren().remove(draggingComboBox);
                pane.getChildren().add(draggingComboBox);
                event.setDropCompleted(true);

                draggingComboBox = null;
            }
        });
    }*/

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
