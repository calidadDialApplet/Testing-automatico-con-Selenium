package gui;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;

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
    private Label rowIndexLabel = new Label();
    private CheckBox actionSelected = new CheckBox();
    private HBox uniqueValue = new HBox();
    private HBox hBox = new HBox();
    private StackPane stackPane = new StackPane();
    private String lastType;
    private boolean textFieldNotGenerated, needToDelete, placeNotGenerated, modified ,checkboxNotGenerated;

    private static DataFormat hBoxFormat = new DataFormat();
    private static Integer rowIndexDrag;
    private static Integer rowIndexDrop;
    private static List<Node> draggedChildList = new ArrayList<>();;
    private static List<Node> movedChilds = new ArrayList<>();;




    public void setAction(GridPane gridParent, int rowIndex,String actionTypeValue, String selectElementByValue, String firstValueArgsValue, String selectPlaceByValue, String secondValueArgsValue)
    {
        MainController mainController = new MainController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainController"));
        loader.setController(mainController);

        mainController.setModified(false);
        hBox = new HBox();
        hBox.setFillHeight(true);
        hBox.setMinWidth(2000);
        hBox.setSpacing(10);

        rowIndexLabel = new Label("# "+rowIndex);
        rowIndexLabel.setPadding(new Insets(5,0,0,0));

        drawCheckBox(hBox);
        dragAndDrop(gridParent, rowIndexLabel);
        gridParent.addRow(rowIndex,hBox);

        hBox.getChildren().add(rowIndexLabel);
        actionType = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        //gridParent.addRow(rowIndex, actionType);
        hBox.getChildren().add(actionType);

        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {

            System.out.println("1"+mainController.isModified());
            if (oldValue != newValue && oldValue != null) {
                mainController.setModified(true);
                System.out.println("2"+mainController.isModified());
            }
            lastType = actionType.getValue().toString();
            checkboxNotGenerated = true;
            switch (actionType.getValue().toString()) {
                case "Click":
                    initializeComboBox(hBox);
                    selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    //gridParent.addRow(rowIndex, selectElementBy);
                    dragAndDrop(gridParent, selectElementBy);
                    hBox.getChildren().add(selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (oldValue != newValue && oldValue != null) {
                            mainController.setModified(true);
                            System.out.println(mainController.isModified());
                        }
                        generatedTextField(hBox, "FirstValueArgs", firstValueArgsValue);
                        /*if (checkboxNotGenerated) {
                            drawCheckBox(hBox);
                            checkboxNotGenerated = false;
                        }*/
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "DragAndDrop":
                    placeNotGenerated = true;
                    initializeComboBox(hBox);
                    selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    //gridParent.addRow(rowIndex, selectElementBy);
                    dragAndDrop(gridParent, selectElementBy);
                    hBox.getChildren().add(selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (oldValue != newValue && oldValue != null) {
                            mainController.setModified(true);
                            System.out.println(mainController.isModified());
                        }
                        if (placeNotGenerated) {
                            firstValueArgs = new TextField(firstValueArgsValue);
                            firstValueArgs.textProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->{
                                if (oldValueSelect1 != newValueSelect1) {
                                    mainController.setModified(true);
                                }
                            });
                            //firstValueArgs.setText(firstValueArgsValue);
                            //gridParent.addRow(rowIndex, firstValueArgs);
                            hBox.getChildren().add(firstValueArgs);
                            selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                            dragAndDrop(gridParent, selectPlaceBy);
                            //gridParent.addRow(rowIndex, selectPlaceBy);
                            hBox.getChildren().add(selectPlaceBy);
                            placeNotGenerated = false;
                            selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                            {
                                if (oldValueSelect1 != newValueSelect1 && oldValueSelect1 != null) {
                                    mainController.setModified(true);
                                }
                                generatedTextField(hBox, "SecondValueArgs", secondValueArgsValue);
                                /*if (checkboxNotGenerated) {
                                    drawCheckBox(hBox);
                                    checkboxNotGenerated = false;
                                }*/
                            });
                            selectPlaceBy.setValue(Action.getSelectElementById(selectPlaceByValue));
                        }
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "WriteTo":
                    initializeComboBox(hBox);
                    selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    //gridParent.addRow(rowIndex, selectElementBy);
                    dragAndDrop(gridParent,selectElementBy);
                    hBox.getChildren().add(selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (oldValueSelect != newValueSelect && oldValueSelect != null) {
                            mainController.setModified(true);
                        }
                        if (textFieldNotGenerated) {
                            firstValueArgs = new TextField(firstValueArgsValue);
                            firstValueArgs.textProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->{
                                if (oldValueSelect1 != newValueSelect1 && oldValueSelect1 != null) {
                                    mainController.setModified(true);
                                }
                            });
                            //firstValueArgs.setText(firstValueArgsValue);
                            //gridParent.addRow(rowIndex, firstValueArgs);
                            hBox.getChildren().add(firstValueArgs);

                            value.setText("Value");
                            value.setPadding(new Insets(5,0,0,0));
                            dragAndDrop(gridParent, value);
                            //gridParent.addRow(rowIndex,value);
                            hBox.getChildren().add(value);

                            uniqueValue = new HBox();
                            uniqueValue.setSpacing(8);
                            uniqueValue.setPadding(new Insets(5, 0, 0, 0));
                            CheckBox uniqueCheckBox = new CheckBox();
                            Label unique = new Label("Unique");

                            uniqueValue.getChildren().addAll(unique,uniqueCheckBox);
                            //gridParent.addRow(rowIndex, uniqueValue);
                            dragAndDrop(gridParent, uniqueValue);
                            hBox.getChildren().add(uniqueValue);

                            secondValueArgs = new TextField(secondValueArgsValue);
                            secondValueArgs.textProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->{
                                if (oldValueSelect1 != newValueSelect1 && oldValueSelect1 != null) {
                                    mainController.setModified(true);
                                }
                            });
                            //secondValueArgs.setText(secondValueArgsValue);
                            //gridParent.addRow(rowIndex,secondValueArgs);
                            addFileButton(secondValueArgs,hBox);
                            for (Node child : stackPane.getChildren()){
                                if (child instanceof TextField){
                                    ((TextField) child).setText(secondValueArgsValue);
                                }
                            }
                            //hBox.getChildren().add(secondValueArgs);
                            /*if (checkboxNotGenerated) {
                                drawCheckBox(hBox);
                                checkboxNotGenerated = false;
                            }*/
                        }
                        textFieldNotGenerated = false;
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "ReadFrom":
                    initializeComboBox(hBox);
                    selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    hBox.getChildren().add(selectElementBy);
                    dragAndDrop(gridParent, selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (oldValueSelect != newValueSelect && oldValueSelect != null) {
                            mainController.setModified(true);
                        }
                        if (textFieldNotGenerated) {
                            generatedTextField(hBox, "FirstValueArgs", firstValueArgsValue);
                            value.setText("Variable");
                            value.setPadding(new Insets(5, 0, 0, 0));
                            dragAndDrop(gridParent, value);
                            hBox.getChildren().add(value);

                            secondValueArgs = new TextField(secondValueArgsValue);
                            secondValueArgs.textProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->{
                                if (oldValueSelect1 != newValueSelect1 && oldValueSelect1 != null) {
                                    mainController.setModified(true);
                                }
                            });
                            //secondValueArgs.setText(secondValueArgsValue);
                            hBox.getChildren().add(secondValueArgs);
                            /*if (checkboxNotGenerated) {
                                drawCheckBox(hBox);
                                checkboxNotGenerated = false;
                            }*/
                        }
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "SwitchTo":
                case "WaitTime":
                case "NavigateTo":
                    initializeComboBox(hBox);
                    generatedTextField(hBox, "FirstValueArgs", firstValueArgsValue);
                    /*if (checkboxNotGenerated){
                        drawCheckBox(hBox);
                    }*/
                    break;
                case "Waiting For":
                    initializeComboBox(hBox);

                    value.setText("Element");
                    value.setPadding(new Insets(5,0,0,0));
                    //gridParent.addRow(rowIndex,value);
                    dragAndDrop(gridParent, value);
                    hBox.getChildren().add(value);

                    selectElementBy = new ComboBox(FXCollections.observableArrayList(persistence.H2DAO.getSelectElementBy()));
                    //gridParent.addRow(rowIndex, selectElementBy);
                    dragAndDrop(gridParent, selectElementBy);
                    hBox.getChildren().add(selectElementBy);
                    selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {
                        if (oldValueSelect != newValueSelect && oldValueSelect != null) {
                            mainController.setModified(true);
                        }
                        if (textFieldNotGenerated) {
                            firstValueArgs = new TextField(firstValueArgsValue);
                            firstValueArgs.textProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->{
                                if (oldValueSelect1 != newValueSelect1 && oldValueSelect1 != null) {
                                    mainController.setModified(true);
                                }
                            });
                            //firstValueArgs.setText(firstValueArgsValue);
                            //gridParent.addRow(rowIndex, firstValueArgs);
                            hBox.getChildren().add(firstValueArgs);
                            generatedTextField(hBox, "SecondValueArgs", secondValueArgsValue);
                            /*if (checkboxNotGenerated) {
                                drawCheckBox(hBox);
                                checkboxNotGenerated = false;
                            }*/
                        }
                    });
                    selectElementBy.setValue(Action.getSelectElementById(selectElementByValue));
                    break;
                case "SwitchDefault":
                    initializeComboBox(hBox);
                default:
                    break;
            }
        });
        actionType.getSelectionModel().select(Action.getActionTypeId(actionTypeValue));

        dragAndDrop(gridParent,actionType);
        dragAndDrop(gridParent,hBox);
    }

    public void addActionToGrid(GridPane gridParent, int rowIndex)
    {
        hBox = new HBox();
        hBox.setFillHeight(true);
        hBox.setMinWidth(2000);
        hBox.setSpacing(10);

        drawCheckBox(hBox);

        gridParent.addRow(rowIndex, hBox);
        rowIndexLabel = new Label("# "+rowIndex);
        dragAndDrop(gridParent, rowIndexLabel);
        rowIndexLabel.setPadding(new Insets(5,0,0,0));
        //gridParent.addRow(rowIndex,rowIndexLabel);
        //hBox.setHgrow(rowIndexLabel, Priority.ALWAYS);


        hBox.getChildren().add(rowIndexLabel);



        actionType = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        //gridParent.addRow(rowIndex, actionType);
        hBox.getChildren().add(actionType);
        actionType.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            initializeComboBox(hBox);
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
                case "NavigateTo":
                case "SwitchDefault":
                    drawElements(hBox, lastType, gridParent);
                    break;
                default:
                    break;
            }
        });


        dragAndDrop(gridParent,actionType);
        dragAndDrop(gridParent,hBox);


    }

    private void dragAndDrop(GridPane gridParent, Node node)
    {
        node.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rowIndexDrop = -1;
                rowIndexDrag = 0;
                draggedChildList.clear();
                movedChilds.clear();

                Dragboard db = hBox.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(hBoxFormat, " ");
                db.setContent(content);


                if (node instanceof ComboBox)
                {
                    rowIndexDrag = gridParent.getRowIndex(event.getPickResult().getIntersectedNode().getParent());

                } else if (node instanceof TextField || node instanceof Label){

                    rowIndexDrag = gridParent.getRowIndex(node.getParent());
                }else if (node instanceof HBox){
                    if (((HBox) node).getChildren().size() == 2)
                    {
                        rowIndexDrag = gridParent.getRowIndex(node.getParent());
                    }else {
                        rowIndexDrag = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                    }
                }else {
                    rowIndexDrag = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                }

                //System.out.println("DragIndex = "+ rowIndexDrag);


                for (Node child : gridParent.getChildren())
                {                                        // Almacenar en la lista el Hbox de la accion
                    if (gridParent.getRowIndex(child) == rowIndexDrag)
                    {
                        draggedChildList.add(child);
                    }
                }
                /*
                if (node instanceof ComboBox)
                {
                    gridParent.getChildren().remove(event.getPickResult().getIntersectedNode().getParent());
                }else if (node instanceof TextField || node instanceof Label || node instanceof HBox) {

                    gridParent.getChildren().remove(node.getParent());

                } else {
                    gridParent.getChildren().remove(event.getPickResult().getIntersectedNode());
                }


                gridParent.getRowConstraints().remove(rowIndexDrag);


                if (rowIndexDrag >= 0 || rowIndexDrag < getRowCount(gridParent))                    // Si es la cabeza o medio...
                {
                    for (Node child : gridParent.getChildren()){                                        // Reducir el rowIndex de las que estan por debajo de la seleccionada -1
                        if (gridParent.getRowIndex(child) > rowIndexDrag)
                        {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1);
                        }
                    }
                }
                */
                event.consume();

            }
        });

        node.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                //event.acceptTransferModes(TransferMode.NONE);
                //event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                //gridParent.getChildren().removeAll(draggedChildList);                               // Eliminar los elementos
                //gridParent.getRowConstraints().remove(rowIndexDrag);                                // Eliminar la fila del grid
                if (event.getPickResult().getIntersectedNode() != null){
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });

        // Animación al pasar por un target
        /*node.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getPickResult().getIntersectedNode() != null){
                    for (Node child : gridParent.getChildren()) {
                        if (gridParent.getRowIndex(child) >= rowIndexDrop) {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) + 1); // Aumentar el RowIndex de las acciones que tiene por debajo en 1
                        }
                    }
                }

            }
        });

        node.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getPickResult().getIntersectedNode() != null){
                    for (Node child : gridParent.getChildren()) {
                        if (gridParent.getRowIndex(child) >= rowIndexDrop) {
                            gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1); // Aumentar el RowIndex de las acciones que tiene por debajo en 1
                        }
                    }
                }
            }
        });*/
        //


        node.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                if (node instanceof ComboBox)
                {
                    rowIndexDrop = gridParent.getRowIndex(event.getPickResult().getIntersectedNode().getParent());

                } else if (node instanceof TextField || node instanceof Label){

                    rowIndexDrop = gridParent.getRowIndex(node.getParent());

                }else if (node instanceof HBox){
                    if (((HBox) node).getChildren().size() == 2)
                    {
                        rowIndexDrop = gridParent.getRowIndex(node.getParent());
                    }else {
                        rowIndexDrop = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                    }
                }else {
                    rowIndexDrop = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                }


                System.out.println("DragIndex = "+ rowIndexDrag);
                System.out.println("DropIndex = "+rowIndexDrop);
                System.out.println("RowCount = "+ getRowCount(gridParent));


                if  (rowIndexDrop != null && rowIndexDrag != null) {

                    gridParent.getChildren().removeAll(draggedChildList);
                    gridParent.getRowConstraints().remove(rowIndexDrag);


                    if (rowIndexDrag >= 0 || rowIndexDrag < getRowCount(gridParent))                    // Si es la cabeza o medio...
                    {
                        for (Node child : gridParent.getChildren()){                                        // Reducir el rowIndex de las que estan por debajo de la seleccionada -1
                            if (gridParent.getRowIndex(child) > rowIndexDrag)
                            {
                                gridParent.setRowIndex(child, gridParent.getRowIndex(child) - 1);
                            }
                        }
                    }


                    /*if (rowIndexDrop == getRowCount(gridParent) - 1)                                  // Insertar al final
                    {
                        //gridParent.getRowConstraints().add(new RowConstraints());
                        gridParent.addRow(getRowCount(gridParent), draggedChildList.get(0));

                    } else {   */                                                                     // Insertar en cabeza o medio
                        for (Node child : gridParent.getChildren()) {
                            if (gridParent.getRowIndex(child) >= rowIndexDrop) {
                                gridParent.setRowIndex(child, gridParent.getRowIndex(child) + 1); // Aumentar el RowIndex de las acciones que tiene por debajo en 1
                            }
                        }
                        gridParent.addRow(rowIndexDrop, draggedChildList.get(0));
                    //}



                } else{
                    event.consume();
                }



                System.out.println("Tomaaa con to mi node " + rowIndexDrop);



                event.consume();
            }
        });

        node.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                if (event.getTransferMode() == TransferMode.MOVE) {

                    MainController.reorderIndex(gridParent);
                }



                event.consume();
            }
        });
    }

    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }



    private void initializeComboBox(HBox hBox)
    {
        textFieldNotGenerated = true;
        if (needToDelete) {
            setDefaultAction(hBox);
        }
        needToDelete = true;
    }

    private void drawElements(HBox hBox, String type, GridPane gridParent)
    {
        //int rowIndex = gridParent.getRowIndex(actionType);
        switch (type) {
            case "Click":
                selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                //gridParent.addRow(rowIndex, selectElementBy);
                hBox.getChildren().add(selectElementBy);
                dragAndDrop(gridParent, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {

                    generatedTextField(hBox,"FirstValueArgs","");
                    /*if (checkboxNotGenerated) {
                        drawCheckBox(hBox);
                        checkboxNotGenerated = false;
                    }*/
                });
                break;
            case "DragAndDrop":
                selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                //gridParent.addRow(rowIndex, selectElementBy);
                hBox.getChildren().add(selectElementBy);
                dragAndDrop(gridParent, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    if (placeNotGenerated) {
                        firstValueArgs = new TextField();
                        hBox.getChildren().add(firstValueArgs);

                        selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                        hBox.getChildren().add(selectPlaceBy);
                        dragAndDrop(gridParent, selectPlaceBy);

                        placeNotGenerated = false;
                        selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                        {
                            generatedTextField(hBox,"SecondValueArgs","");
                            /*if (checkboxNotGenerated) {
                                drawCheckBox(hBox);
                                checkboxNotGenerated = false;
                            }*/
                        });
                    }
                });
                break;
            case "WriteTo":
                selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                hBox.getChildren().add(selectElementBy);
                dragAndDrop(gridParent, selectElementBy);

                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    if (textFieldNotGenerated) {
                        firstValueArgs = new TextField();
                        //gridParent.addRow(gridParent.getRowIndex(selectElementBy), firstValueArgs);

                        hBox.getChildren().add(firstValueArgs);

                        value.setText("Value");
                        value.setPadding(new Insets( 5,0,0,0));
                        dragAndDrop(gridParent, value);
                        //gridParent.addRow(gridParent.getRowIndex(selectElementBy),value);
                        hBox.getChildren().add(value);

                        uniqueValue = new HBox();
                        uniqueValue.setSpacing(8);
                        uniqueValue.setPadding(new Insets(5, 0, 0, 0));
                        CheckBox uniqueCheckBox = new CheckBox();

                        Label unique = new Label("Unique");

                        uniqueValue.getChildren().addAll(unique,uniqueCheckBox);
                        //gridParent.addRow(gridParent.getRowIndex(selectElementBy), uniqueValue);
                        dragAndDrop(gridParent, uniqueValue);
                        hBox.getChildren().add(uniqueValue);


                        secondValueArgs = new TextField();
                        //gridParent.addRow(gridParent.getRowIndex(selectElementBy),secondValueArgs);
                        //hBox.getChildren().add(secondValueArgs);
                        addFileButton(secondValueArgs, hBox);
                        /*if (checkboxNotGenerated) {
                            drawCheckBox(hBox);
                            checkboxNotGenerated = false;
                        }*/
                    }
                    textFieldNotGenerated = false;
                });

                break;
            case "ReadFrom":
                selectElementBy = new ComboBox(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                hBox.getChildren().add(selectElementBy);
                dragAndDrop(gridParent, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    if (textFieldNotGenerated) {
                        generatedTextField(hBox, "FirstValueArgs", "");
                        value.setText("Variable");
                        value.setPadding(new Insets(5, 0, 0, 0));
                        dragAndDrop(gridParent, value);
                        hBox.getChildren().add(value);

                        secondValueArgs = new TextField();
                        hBox.getChildren().add(secondValueArgs);
                        /*if (checkboxNotGenerated) {
                            drawCheckBox(hBox);
                            checkboxNotGenerated = false;
                        }*/
                    }
                });
                break;
            case "SwitchTo":
            case "WaitTime":
            case "NavigateTo":
                generatedTextField(hBox,"FirstValueArgs","");
                /*if (checkboxNotGenerated) {
                    drawCheckBox(hBox);
                    checkboxNotGenerated = false;
                }*/
                break;
            case "Waiting For":

                value.setText("Element");
                value.setPadding(new Insets( 5,0,0,0));
                //gridParent.addRow(rowIndex,value);
                hBox.getChildren().add(value);
                dragAndDrop(gridParent, value);

                selectElementBy = new ComboBox(FXCollections.observableArrayList(persistence.H2DAO.getSelectElementBy()));
                //gridParent.addRow(rowIndex, selectElementBy);
                hBox.getChildren().add(selectElementBy);
                dragAndDrop(gridParent, selectElementBy);
                selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                {
                    if (textFieldNotGenerated) {
                        firstValueArgs = new TextField();
                        //gridParent.addRow(rowIndex, firstValueArgs);
                        hBox.getChildren().add(firstValueArgs);
                        generatedTextField(hBox, "SecondValueArgs", "");
                        /*if (checkboxNotGenerated) {
                            drawCheckBox(hBox);
                            checkboxNotGenerated = false;
                        }*/
                    }
                });
                break;

        }
    }

    public void drawCheckBox(HBox hBox)
    {
        actionSelected = new CheckBox();
        hBox.setMargin(actionSelected, new Insets(4,0,0,0));
        hBox.setHgrow(actionSelected, Priority.ALWAYS);
        hBox.getChildren().add(actionSelected);
        actionSelected.setAlignment(Pos.BOTTOM_RIGHT);


    }

    public void addFileButton(TextField textField, HBox hBox)
    {
        //mainController.setModified(true);
        textField.clear();
        stackPane = new StackPane();
        hBox.getChildren().add(stackPane);

        Button buttonFile = new Button();
        buttonFile.setStyle("-fx-background-color: transparent;");
        Image image = new Image(getClass().getResource("/icons/baseline_folder_black_18dp.png").toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(17);
        imageView.setFitWidth(17);
        buttonFile.setGraphic(imageView);
        stackPane.getChildren().add(textField);
        stackPane.setAlignment(buttonFile, Pos.CENTER_RIGHT);
        stackPane.getChildren().add(buttonFile);

        buttonFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String route = MainController.getPathOFC();
                if (!route.equals("")) {
                    textField.setText(route);
                    textField.setText(route);
                    textField.setDisable(true);
                }
            }
        });

    }

    public void generatedTextField(HBox hBox, String name, String value)
    {
        if(name.equals("FirstValueArgs")){
            if (textFieldNotGenerated) {
                firstValueArgs = new TextField(value);
                firstValueArgs.textProperty().addListener((observable -> {
                    //mainController.setModified(true);
                }));
                hBox.getChildren().add(firstValueArgs);
            }
            textFieldNotGenerated = false;
        }
        if (name.equals("SecondValueArgs")){
            if (textFieldNotGenerated) {
                secondValueArgs = new TextField(value);
                secondValueArgs.textProperty().addListener((observable -> {
                    //mainController.setModified(true);
                }));
                hBox.getChildren().add(secondValueArgs);
            }
            textFieldNotGenerated = false;
        }
    }

    public void setDefaultAction(HBox hBox)
    {
        hBox.getChildren().removeAll(selectElementBy,firstValueArgs,selectPlaceBy,secondValueArgs, value, uniqueValue, stackPane);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
