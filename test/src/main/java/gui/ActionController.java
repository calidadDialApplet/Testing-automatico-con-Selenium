package gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import persistence.H2DAO;

public class ActionController
{
    private ComboBox actionType = new ComboBox();
    private ComboBox selectElementBy = new ComboBox();
    private ComboBox selectPlaceBy = new ComboBox();
    private TextField firstValueArgs = new TextField();
    private TextField secondValueArgs = new TextField();
    private Label value = new Label();
    private String lastType;
    private boolean textFieldNotGenerated, needToDelete, placeNotGenerated;

    public void setAction(GridPane gridParent, int rowIndex,String actionTypeValue, String selectElementByValue, String firstValueArgsValue, String selectPlaceByValue, String secondValueArgsValue)
    {
        //this.gridParent = gridParent;
        //this.rowIndex = rowIndex;



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
                    selectElementBy.setValue(getSelectElementById(selectElementByValue));
                    break;
                case "DragAndDrop":
                    textFieldNotGenerated = true;
                    placeNotGenerated = true;
                    if(needToDelete) {
                        setDefaultAction(gridParent);
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
                            selectPlaceBy.setValue(getSelectElementById(selectPlaceByValue));
                        }
                    });
                    selectElementBy.setValue(getSelectElementById(selectElementByValue));
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
                    selectElementBy.setValue(getSelectElementById(selectElementByValue));
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
                    selectElementBy.setValue(getSelectElementById(selectElementByValue));
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
                    selectElementBy.setValue(getSelectElementById(selectElementByValue));
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
        actionType.getSelectionModel().select(getActionTypeId(actionTypeValue));
    }

    public void addActiontoGrid(GridPane gridParent, int rowIndex)
    {


        actionType = new ComboBox<>();
        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            initialiceCheckBox(gridParent);
            lastType = actionType.getValue().toString();
            placeNotGenerated = true;
            switch (actionType.getValue().toString()) {
                case "Click":
                case "DragAndDrop":
                case "WriteTo":
                case "ReadFrom":
                case "SwitchTo":
                case "Waiting":
                case "WaitTime":
                    drawElements(gridParent, rowIndex, lastType);
                    break;
                default:
                    break;
            }
        });

    }


    private void initialiceCheckBox(GridPane gridParent)
    {
        textFieldNotGenerated = true;
        if (needToDelete) {
            setDefaultAction(gridParent);
        }
        needToDelete = true;
    }

    private void drawElements(GridPane gridParent, int rowIndex, String type)
    {
        switch (type) {
            case "Click":
            case "ReadFrom":
                selectElementBy = new ComboBox();
                selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                gridParent.addRow(rowIndex, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    generatedTextField(gridParent,rowIndex,"FirstValueArgs");
                });
                break;
            case "DragAndDrop":
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
                            generatedTextField(gridParent,rowIndex,"SecondValueArgs");
                        });
                    }
                });
                break;
            case "WriteTo":
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
            case "SwitchTo":
            case "WaitTime":
                generatedTextField(gridParent,rowIndex,"FirstValueArgs");
                break;
            case "Waiting":
                firstValueArgs = new TextField();
                gridParent.addRow(rowIndex, firstValueArgs);

                value.setText("Element");
                gridParent.addRow(rowIndex,value);

                selectElementBy = new ComboBox();
                selectElementBy.setItems(FXCollections.observableArrayList(persistence.H2DAO.getSelectElementBy()));
                gridParent.addRow(rowIndex, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    generatedTextField(gridParent,rowIndex,"SecondValueArgs");
                });
                break;

        }
    }

    public void generatedTextField(GridPane gridParent,int rowIndex, String name){
        if(name.equals("FirstValueArgs")){
            if (textFieldNotGenerated) {
                firstValueArgs = new TextField();
                gridParent.addRow(rowIndex,firstValueArgs);
            }
            textFieldNotGenerated = false;
        }
        if (name.equals("SecondValueArgs")){
            if (textFieldNotGenerated) {
                secondValueArgs = new TextField();
                gridParent.addRow(rowIndex,secondValueArgs);
            }
            textFieldNotGenerated = false;
        }
    }

    public void setDefaultAction(GridPane gridParent)
    {
        gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs, value);

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
}
