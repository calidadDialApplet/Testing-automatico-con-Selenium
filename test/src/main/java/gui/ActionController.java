package gui;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import persistence.H2DAO;

import java.util.ArrayList;
import java.util.List;

public class ActionController
{
    private ComboBox actionType = new ComboBox();
    private ComboBox selectElementBy = new ComboBox();
    private ComboBox selectPlaceBy = new ComboBox();
    private TextField firstValueArgs = new TextField();
    private TextField secondValueArgs = new TextField();
    private Label value = new Label();
    private CheckBox actionSelected = new CheckBox();
    private String lastType;
    private boolean textFieldNotGenerated, needToDelete, placeNotGenerated, checkboxNotGenerated;

    private static DataFormat comboBoxFormat = new DataFormat();
    private static Integer rowIndexDrag;
    private static Integer rowIndexDrop;
    private static List<Node> draguedChildList = new ArrayList<>();;
    private static List<Node> movedChilds = new ArrayList<>();;




    public void setAction(GridPane gridParent, int rowIndex,String actionTypeValue, String selectElementByValue, String firstValueArgsValue, String selectPlaceByValue, String secondValueArgsValue)
    {
        Label rowIndexLabel = new Label("# "+rowIndex);
        gridParent.addRow(rowIndex,rowIndexLabel);
        actionType = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            lastType = actionType.getValue().toString();
            checkboxNotGenerated = true;
            switch (actionType.getValue().toString()) {
                case "Click":
                case "ReadFrom":
                    initializeComboBox(gridParent);
                    selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridParent.addRow(rowIndex, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        generatedTextField(gridParent,rowIndex, "FirstValueArgs", firstValueArgsValue);
                        if (checkboxNotGenerated) {
                            drawCheckBox(gridParent, gridParent.getRowIndex(selectElementBy));
                            checkboxNotGenerated = false;
                        }
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "DragAndDrop":
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

                            gridParent.addRow(rowIndex, selectPlaceBy);
                            placeNotGenerated = false;
                            selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                            {
                                generatedTextField(gridParent,rowIndex, "SecondValueArgs", secondValueArgsValue);
                                if (checkboxNotGenerated) {
                                    drawCheckBox(gridParent, gridParent.getRowIndex(selectPlaceBy));
                                    checkboxNotGenerated = false;
                                }
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
                            if (checkboxNotGenerated) {
                                drawCheckBox(gridParent, gridParent.getRowIndex(selectElementBy));
                                checkboxNotGenerated = false;
                            }
                        }
                        textFieldNotGenerated = false;
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "SwitchTo":
                case "WaitTime":
                    initializeComboBox(gridParent);
                    generatedTextField(gridParent,rowIndex, "FirstValueArgs", firstValueArgsValue);
                    if (checkboxNotGenerated){
                        drawCheckBox(gridParent,rowIndex);
                    }
                    break;
                case "Waiting For":
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
                        if (checkboxNotGenerated) {
                            drawCheckBox(gridParent, gridParent.getRowIndex(selectElementBy));
                            checkboxNotGenerated = false;
                        }
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                default:
                    break;
            }
        });
        actionType.getSelectionModel().select(Action.getActionTypeId(actionTypeValue));

        actionType.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rowIndexDrop = -1;
                rowIndexDrag = 0;
                draguedChildList.clear();
                movedChilds.clear();

                Dragboard db = actionType.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(comboBoxFormat, " ");
                db.setContent(content);
                rowIndexDrag = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                for (Node child : gridParent.getChildren()){                                        // Almacenar en la lista los elementos de la accion
                    if (gridParent.getRowIndex(child) == rowIndexDrag)
                    {
                        draguedChildList.add(child);
                    }
                }

                for (Node child : gridParent.getChildren()){                                        // Reducir el rowIndex de las que estan por debajo de la seleccionada -1
                    if (gridParent.getRowIndex(child) > rowIndexDrag)
                    {
                        gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1);
                    }
                }
                event.consume();

            }
        });

        actionType.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                gridParent.getChildren().removeAll(draguedChildList);                               // Eliminar los elementos
                gridParent.getRowConstraints().remove(rowIndexDrag);                                // Eliminar la fila del grid
                event.consume();
            }
        });

        actionType.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {


                rowIndexDrop = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());

                System.out.println("DragIndex = "+ rowIndexDrag);
                System.out.println("DropIndex = "+rowIndexDrop);

                System.out.println(getLastChildIndex(gridParent));
                /*if (rowIndexDrop == getLastChildIndex(gridParent))                                  // Insertar al final
                {
                    for (Node child : gridParent.getChildren())
                    {
                        if (gridParent.getRowIndex(child) > rowIndexDrag) {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1); // Reducir el RowIndex de las acciones que tiene por encima en 1
                        }
                    }
                }*/

                if (rowIndexDrag > rowIndexDrop || rowIndexDrag < rowIndexDrop || rowIndexDrop == 0) // Insertar en cabeza o en el medio
                {
                    for (Node child : gridParent.getChildren()) {
                        if (gridParent.getRowIndex(child) >= rowIndexDrop) {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) + 1); // Aumentar el RowIndex de las acciones que tiene por debajo en 1

                        }
                    }
                }

                System.out.println("Tomaaa con to mi node " + rowIndexDrop);



                event.consume();
            }
        });

        actionType.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                if (event.getTransferMode() == TransferMode.MOVE) {

                }

                if (rowIndexDrop == -1){
                    /*for (Node item : draguedChildList) {
                        gridParent.addRow(rowIndexDrag, item);
                        gridParent.setRowIndex(item, rowIndexDrag);
                        //gridParent.setRowIndex(item, gridParent.getRowIndex(item));
                    }*/

                    event.consume();
                } else {

                    for (Node item : draguedChildList) {
                        gridParent.addRow(rowIndexDrop, item);
                        gridParent.setRowIndex(item, rowIndexDrop);
                        //gridParent.setRowIndex(item, gridParent.getRowIndex(item));
                    }
                }

                System.out.println("RowIndex = "+rowIndex);
                System.out.println("------------------------------------");
                event.consume();
            }
        });
    }

    public void addActiontoGrid(GridPane gridParent, int rowIndex)
    {

        Label rowIndexLabel = new Label("# "+rowIndex);
        gridParent.addRow(rowIndex,rowIndexLabel);
        actionType = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        gridParent.addRow(rowIndex, actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            initializeComboBox(gridParent);
            lastType = actionType.getValue().toString();
            placeNotGenerated = true;
            checkboxNotGenerated = true;
            switch (actionType.getValue().toString()) {
                case "Click":
                case "DragAndDrop":
                case "WriteTo":
                case "ReadFrom":
                case "SwitchTo":
                case "Waiting For":
                case "WaitTime":
                    drawElements(gridParent, lastType,actionType);
                    break;
                default:
                    break;
            }
        });





        actionType.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rowIndexDrop = -1;
                rowIndexDrag = 0;
                draguedChildList.clear();
                movedChilds.clear();

                Dragboard db = actionType.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(comboBoxFormat, " ");
                db.setContent(content);
                rowIndexDrag = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                for (Node child : gridParent.getChildren()){                                        // Almacenar en la lista los elementos de la accion
                    if (gridParent.getRowIndex(child) == rowIndexDrag)
                    {
                        draguedChildList.add(child);
                    }
                }

                for (Node child : gridParent.getChildren()){                                        // Reducir el rowIndex de las que estan por debajo de la seleccionada -1
                    if (gridParent.getRowIndex(child) > rowIndexDrag)
                    {
                        gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1);
                    }
                }
                event.consume();

            }
        });

        actionType.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                gridParent.getChildren().removeAll(draguedChildList);                               // Eliminar los elementos
                gridParent.getRowConstraints().remove(rowIndexDrag);                                // Eliminar la fila del grid
                event.consume();
            }
        });

        actionType.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {


                rowIndexDrop = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());

                System.out.println("DragIndex = "+ rowIndexDrag);
                System.out.println("DropIndex = "+rowIndexDrop);

                System.out.println(getLastChildIndex(gridParent));
                /*if (rowIndexDrop == getLastChildIndex(gridParent))                                  // Insertar al final
                {
                    for (Node child : gridParent.getChildren())
                    {
                        if (gridParent.getRowIndex(child) > rowIndexDrag) {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1); // Reducir el RowIndex de las acciones que tiene por encima en 1
                        }
                    }
                }*/

                if (rowIndexDrag > rowIndexDrop || rowIndexDrag < rowIndexDrop || rowIndexDrop == 0) // Insertar en cabeza o en el medio
                {
                    for (Node child : gridParent.getChildren()) {
                        if (gridParent.getRowIndex(child) >= rowIndexDrop) {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) + 1); // Aumentar el RowIndex de las acciones que tiene por debajo en 1

                        }
                    }
                }

                System.out.println("Tomaaa con to mi node " + rowIndexDrop);



                event.consume();
            }
        });

        actionType.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                if (event.getTransferMode() == TransferMode.MOVE) {

                }

                if (rowIndexDrop == -1){
                    event.consume();
                } else {

                    for (Node item : draguedChildList) {
                        gridParent.addRow(rowIndexDrop, item);
                        gridParent.setRowIndex(item, rowIndexDrop);
                        //gridParent.setRowIndex(item, gridParent.getRowIndex(item));
                    }
                }

                System.out.println("RowIndex = "+rowIndex);
                System.out.println("------------------------------------");
                event.consume();
            }
        });


    }

    private int getLastChildIndex(GridPane gridPane)
    {
        int index = 0;

        for (Node child : gridPane.getChildren())
        {
            if (gridPane.getRowIndex(child) > index){
                index = gridPane.getRowIndex(child);
            }
        }

        return index;
    }

    private void initializeComboBox(GridPane gridParent)
    {
        textFieldNotGenerated = true;
        if (needToDelete) {
            setDefaultAction(gridParent);
        }
        needToDelete = true;
    }

    private void drawElements(GridPane gridParent, String type, ComboBox actionType)
    {
        int rowIndex = gridParent.getRowIndex(actionType);
        switch (type) {
            case "Click":
            case "ReadFrom":
                selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                gridParent.addRow(rowIndex, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    generatedTextField(gridParent,gridParent.getRowIndex(selectElementBy),"FirstValueArgs","");
                    if (checkboxNotGenerated) {
                        drawCheckBox(gridParent, gridParent.getRowIndex(selectElementBy));
                        checkboxNotGenerated = false;
                    }
                });
                break;
            case "DragAndDrop":
                selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                gridParent.addRow(rowIndex, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    if (placeNotGenerated) {
                        firstValueArgs = new TextField();
                        gridParent.addRow(gridParent.getRowIndex(selectElementBy), firstValueArgs);
                        selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                        gridParent.addRow(gridParent.getRowIndex(selectElementBy), selectPlaceBy);
                        placeNotGenerated = false;
                        selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                        {
                            generatedTextField(gridParent,gridParent.getRowIndex(selectPlaceBy),"SecondValueArgs","");
                            if (checkboxNotGenerated) {
                                drawCheckBox(gridParent, gridParent.getRowIndex(selectPlaceBy));
                                checkboxNotGenerated = false;
                            }
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
                        gridParent.addRow(gridParent.getRowIndex(selectElementBy), firstValueArgs);

                        value.setText("Value");
                        gridParent.addRow(gridParent.getRowIndex(selectElementBy),value);

                        secondValueArgs = new TextField();
                        gridParent.addRow(gridParent.getRowIndex(selectElementBy),secondValueArgs);
                        if (checkboxNotGenerated) {
                            drawCheckBox(gridParent, gridParent.getRowIndex(selectElementBy));
                            checkboxNotGenerated = false;
                        }
                    }
                    textFieldNotGenerated = false;
                });

                break;
            case "SwitchTo":
            case "WaitTime":
                generatedTextField(gridParent,rowIndex,"FirstValueArgs","");
                if (checkboxNotGenerated) {
                    drawCheckBox(gridParent, rowIndex);
                    checkboxNotGenerated = false;
                }
                break;
            case "Waiting For":
                firstValueArgs = new TextField();
                gridParent.addRow(rowIndex, firstValueArgs);

                value.setText("Element");
                gridParent.addRow(rowIndex,value);

                selectElementBy = new ComboBox();
                selectElementBy.setItems(FXCollections.observableArrayList(persistence.H2DAO.getSelectElementBy()));
                gridParent.addRow(rowIndex, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    generatedTextField(gridParent,gridParent.getRowIndex(selectElementBy),"SecondValueArgs","");
                    if (checkboxNotGenerated) {
                        drawCheckBox(gridParent, gridParent.getRowIndex(selectElementBy));
                        checkboxNotGenerated = false;
                    }
                });

                break;

        }
    }

    public void drawCheckBox(GridPane gridParent, int rowIndex)
    {
        actionSelected = new CheckBox();
        gridParent.addRow(rowIndex, actionSelected);
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
        gridParent.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs, value, actionSelected);
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
