package gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Trial {

   private GridPane gridParent;
   private int rowIndex;
   private String lastType;
   private boolean firstTime, firstTime2, firstTimeDragAndDrop;

   private ComboBox selectElementBy;
   private ComboBox selectPlaceBy;
   private TextField firstValueArgs;
   private TextField secondValueArgs;


    public Trial(GridPane gridParent, int rowIndex) {

        this.gridParent = gridParent;
        this.rowIndex = rowIndex;

        ComboBox<String> actionType = new ComboBox<>();
        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
            actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {

            switch (actionType.getValue()) {
                case "Click":
                    firstTime = true;
                    System.out.println(lastType);
                    System.out.println("" + gridParent.getChildren());
                    if(firstTime2) {
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs);
                                break;
                            case "Selector":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy);
                                break;
                            case "default":
                                break;
                        }
                    }
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if (firstTime) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                        }
                        firstTime = false;
                        firstTime2 = true;
                    });
                    System.out.println(gridParent.getChildren());
                    lastType = "Click";
                    break;
                case "DragAndDrop":
                    firstTime = true;
                    System.out.println(lastType);
                    firstTimeDragAndDrop = true;
                    System.out.println("" + gridParent.getChildren());
                    if(firstTime2) {
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy,selectPlaceBy,secondValueArgs);
                                break;
                            case "Selector":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy);
                                break;
                            case "default":
                                break;
                        }
                    }
                    lastType = "DragAndDrop";
                    System.out.println(gridParent.getChildren());
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if (firstTimeDragAndDrop) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                            selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                            selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                            gridParent.addRow(rowIndex, selectPlaceBy);
                            firstTimeDragAndDrop = false;
                            selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                            {
                                if (firstTime) {
                                    secondValueArgs = new TextField();
                                    gridParent.addRow(rowIndex, secondValueArgs);
                                }
                                firstTime = false;
                                firstTime2 = true;
                            });
                        }
                    });
                    System.out.println(gridParent.getChildren());
                    //lastType = "DragAndDrop";
                    break;
                case "Selector":
                    firstTime = true;
                    System.out.println(lastType);
                    System.out.println("" + gridParent.getChildren());
                    if(firstTime2) {
                        switch (lastType){
                            case "Click":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy);
                                break;
                            case "DragAndDrop":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy,selectPlaceBy,secondValueArgs);
                                break;
                            case "Selector":
                                gridParent.getChildren().removeAll(firstValueArgs,selectElementBy);
                                break;
                            case "default":
                                break;
                        }

                    }
                    System.out.println(gridParent.getChildren());
                    selectElementBy = new ComboBox();
                    selectElementBy.setItems(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (firstTime) {
                            firstValueArgs = new TextField();
                            gridParent.addRow(rowIndex, firstValueArgs);
                        }
                        firstTime = false;
                        firstTime2 = true;
                    });
                    System.out.println(gridParent.getChildren());
                    lastType = "Selector";
                    break;
                case "default":

                    break;
            }
        });
    }
   /*
    @Override
    public String toString() {

        String result= "" + trialType ;

        switch (trialType)
        {
            case "Click":
                result = result.concat(
                        " By " + trialSelectBy +
                        " " + trialFirstArgs);
                break;
            case "DragAndDrop":
                result =  result.concat( '\'' +
                        " By " + trialSelectBy + '\'' +
                        " " + trialFirstArgs + '\'' +
                        " By " + trialSecondSelectBy + '\'' +
                        " " + trialSecondArgs);
                break;
            case "Selector":
                result = result.concat(
                        " By " + trialSelectBy  +
                        " " + trialFirstArgs);
                break;
        }
        return result;
    }*/
}
