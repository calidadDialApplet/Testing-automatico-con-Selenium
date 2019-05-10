package gui;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
        actionType = new ComboBox<>();
        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            lastType = actionType.getValue().toString();
            switch (actionType.getValue().toString()) {
                case "Click":
                case "ReadFrom":
                    initializeComboBox(gridParent);
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        generatedTextField(gridParent,rowIndex, "FirstValueArgs", firstValueArgsValue);
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "DragAndDrop":
                    textFieldNotGenerated = true;
                    placeNotGenerated = true;
                    initializeComboBox(gridParent);
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
                                generatedTextField(gridParent,rowIndex, "SecondValueArgs", secondValueArgsValue);
                            });
                            selectPlaceBy.setValue(Action.getSelectElementById(selectPlaceByValue));
                        }
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "WriteTo":
                    initializeComboBox(gridParent);
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
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "SwitchTo":
                case "WaitTime":
                    initializeComboBox(gridParent);
                    generatedTextField(gridParent,rowIndex, "FirstValueArgs", firstValueArgsValue);
                    break;
                case "Waiting":
                    initializeComboBox(gridParent);
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
                        generatedTextField(gridParent,rowIndex, "SecondValueArgs", secondValueArgsValue);
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                default:
                    break;
            }
        });
        actionType.getSelectionModel().select(Action.getActionTypeId(actionTypeValue));
    }

    public void addActiontoGrid(GridPane gridParent, int rowIndex)
    {


        actionType = new ComboBox<>();
        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            initializeComboBox(gridParent);
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
        MainController.dragAndDrop(gridParent, actionType);
    }



    private void initializeComboBox(GridPane gridParent)
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
                    generatedTextField(gridParent,rowIndex,"FirstValueArgs","");
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
                            generatedTextField(gridParent,rowIndex,"SecondValueArgs","");
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
                generatedTextField(gridParent,rowIndex,"FirstValueArgs","");
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
                    generatedTextField(gridParent,rowIndex,"SecondValueArgs","");
                });
                break;

        }
    }

    public void generatedTextField(GridPane gridParent,int rowIndex, String name, String value)
    {
        if(name.equals("FirstValueArgs")){
            if (textFieldNotGenerated) {
                firstValueArgs = new TextField();
                firstValueArgs.setText(value);
                gridParent.addRow(rowIndex,firstValueArgs);
            }
            textFieldNotGenerated = false;
        }
        if (name.equals("SecondValueArgs")){
            if (textFieldNotGenerated) {
                secondValueArgs = new TextField();
                secondValueArgs.setText(value);
                gridParent.addRow(rowIndex,secondValueArgs);
            }
            textFieldNotGenerated = false;
        }
    }

    public void setDefaultAction(GridPane gridParent)
    {
        gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs, value);
    }



    public void  dragAndDrop (DragEvent event)
    {
        event.getPickResult().getIntersectedNode();
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
