package gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class Trial {

   private GridPane gridParent;
   private int rowIndex;
   private String lastType;
   private boolean textFieldNotGenerated, needToDelete, placeNotGenerated;

   private ComboBox actionType = new ComboBox();
   private ComboBox selectElementBy = new ComboBox();
   private ComboBox selectPlaceBy = new ComboBox();
   private TextField firstValueArgs = new TextField();
   private TextField secondValueArgs = new TextField();


    public Trial(GridPane gridParent, int rowIndex) {

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
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
                                break;
                            case "WriteTo":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "ReadFrom":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "default":
                                break;
                        }
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
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
                                break;
                            case "WriteTo":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "ReadFrom":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "default":
                                break;
                        }
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
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
                                break;
                            case "WriteTo":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "ReadFrom":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "default":
                                break;
                        }

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
                        }
                        textFieldNotGenerated = false;
                    });
                    break;
                case "ReadFrom":
                    textFieldNotGenerated = true;
                    if(needToDelete) {
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
                                break;
                            case "WriteTo":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "ReadFrom":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs);
                                break;
                            case "default":
                                break;
                        }

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
                default:
                    break;
            }
        });
    }

   public Trial(String actionType, String selectElementBy, String firstValueArgs, String selectPlaceBy, String secondValueArgs){
        this.actionType.setValue(actionType);
        this.selectElementBy.setValue(selectElementBy);
        this.firstValueArgs.setText(firstValueArgs);
        this.selectPlaceBy.setValue(selectPlaceBy);
        this.secondValueArgs.setText(secondValueArgs);
   }

   public void executeTrial(){
        switch (this.actionType.getValue().toString()){
            case "Click":

                break;
            case "DragAndDrop":
                break;
            case "WriteTo":
                break;
            case "ReadTo":
                break;
            default:
                break;

        }

   }

    @Override
    public String toString() {
        return "Trial{" +
                "actionType=" + actionType.getValue().toString() +
                ", selectElementBy=" + selectElementBy.getValue().toString() +
                ", selectPlaceBy=" + selectPlaceBy.getValue().toString() +
                ", firstValueArgs=" + firstValueArgs.getText() +
                ", secondValueArgs=" + secondValueArgs.getText() +
                '}';
    }
}
