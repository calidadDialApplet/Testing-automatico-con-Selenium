package gui;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    @FXML
    private Button buttonPlay;

    @FXML
    private Button buttonAdd;

    @FXML
    private  Button buttonDelete;

    @FXML
    private  ListView<CheckBox> testList;

    @FXML
    private GridPane gridPaneTrialList;

    @FXML
    private ComboBox<String> comboBoxNewAction;

    @FXML
    private MenuItem menuItemClose;

   /* @FXML
    private TableView<Trial> tableViewTest;

    @FXML
    private TableColumn<Trial, String> tableColumnTestCol;
    */
    private boolean firstTime;
    private boolean firstTimeDragAndDrop;
    boolean firstTime2 = false;
    private String lastType;

    private ComboBox<String> newAction;
    private ComboBox<String> selectBy;
    private String firstArgs;
    private ComboBox<String> secondSelectBy;
    private String secondArgs;



    private int rowIndex = 0;
    /*
    @FXML
    private TableColumn<Trial, ComboBox> tableColumnSelectedByCol;

    ObservableList<Trial> list = FXCollections.observableArrayList();
    */

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //tableColumnTestCol.setCellValueFactory( (param) -> new SimpleStringProperty( param.getValue().toString()));


        comboBoxNewAction.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
        comboBoxNewAction.valueProperty().addListener((observable, oldValue, newValue) ->
        {

            switch (comboBoxNewAction.getValue())
            {
                case "Click":
                    firstTime = true;
                    gridPaneTrialList.getChildren().remove(1,gridPaneTrialList.getChildren().size());
                    ComboBox<String> selectElementBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridPaneTrialList.addRow(0, selectElementBy);
                        selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                        {

                            if(firstTime)
                            {
                                TextField firstValueArgs = new TextField();
                                gridPaneTrialList.addRow( 0, firstValueArgs);
                            }
                            firstTime = false;
                        });
                    break;
                case "DragAndDrop":
                    firstTime = true;
                    firstTimeDragAndDrop = true;
                    gridPaneTrialList.getChildren().remove(1,gridPaneTrialList.getChildren().size());
                    ComboBox<String> selectElementDragableBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridPaneTrialList.addRow(0, selectElementDragableBy);
                    selectElementDragableBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if(firstTimeDragAndDrop)
                        {
                            TextField firstValueArgs = new TextField();
                            gridPaneTrialList.addRow(0, firstValueArgs);
                            ComboBox<String> selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                            gridPaneTrialList.addRow(0, selectPlaceBy);
                            firstTimeDragAndDrop = false;
                            selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                            {

                                if(firstTime)
                                {
                                    TextField secondValueArgs = new TextField();
                                    gridPaneTrialList.addRow(0, secondValueArgs);
                                }
                                firstTime = false;
                            });
                        }
                    });
                    break;
                case "Selector":
                    firstTime = true;
                    gridPaneTrialList.getChildren().remove(1,gridPaneTrialList.getChildren().size());
                    ComboBox<String> selectSelectorBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                    gridPaneTrialList.addRow(0,selectSelectorBy);
                    selectSelectorBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                    {

                        if(firstTime)
                        {
                            TextField firstValueArgs = new TextField();
                            gridPaneTrialList.addRow( 0, firstValueArgs);
                        }
                        firstTime = false;
                    });
                    break;

            }

        });
    }

    public void addActionRow()
    {
            rowIndex++;
            Trial newaction = new Trial(gridPaneTrialList,rowIndex);

            /*ComboBox<String> newAction = new ComboBox<>();
            gridPaneTrialList.addRow(rowIndex, newAction);
            newAction.setItems(FXCollections.observableArrayList(H2DAO.getTypeAction()));
            newAction.valueProperty().addListener((observable, oldValue, newValue) ->
            {

                switch (newAction.getValue()) {
                    case "Click":
                        firstTime = true;
                        System.out.println(lastType);
                        System.out.println("" + gridPaneTrialList.getChildren());
                        if(firstTime2) {
                                switch (lastType){
                                    case "Id":
                                        gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 2, gridPaneTrialList.getChildren().size());
                                        break;
                                    case "DragAndDrop":
                                        gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 4, gridPaneTrialList.getChildren().size());
                                        break;
                                    case "Selector":
                                        gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 2, gridPaneTrialList.getChildren().size());
                                        break;
                                }
                        }
                        ComboBox<String> selectElementBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                        gridPaneTrialList.addRow(rowIndex, selectElementBy);
                        selectElementBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                        {

                            if (firstTime) {
                                TextField firstValueArgs = new TextField();
                                gridPaneTrialList.addRow(rowIndex, firstValueArgs);
                            }
                            firstTime = false;
                            firstTime2 = true;
                        });
                        System.out.println(gridPaneTrialList.getChildren());
                        lastType = "Click";
                        break;
                    case "DragAndDrop":
                        firstTime = true;
                        System.out.println(lastType);
                        firstTimeDragAndDrop = true;
                        System.out.println("" + gridPaneTrialList.getChildren());
                        if(firstTime2) {
                                switch (lastType){
                                    case "Id":
                                        gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 2, gridPaneTrialList.getChildren().size());
                                        break;
                                    case "DragAndDrop":
                                        gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 4, gridPaneTrialList.getChildren().size());
                                        break;
                                    case "Selector":
                                        gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 2, gridPaneTrialList.getChildren().size());
                                        break;
                                }
                        }
                        System.out.println(gridPaneTrialList.getChildren());
                        ComboBox<String> selectElementDragableBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                        gridPaneTrialList.addRow(rowIndex, selectElementDragableBy);
                        selectElementDragableBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                        {

                            if (firstTimeDragAndDrop) {
                                TextField firstValueArgs = new TextField();
                                gridPaneTrialList.addRow(rowIndex, firstValueArgs);
                                ComboBox<String> selectPlaceBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                                gridPaneTrialList.addRow(rowIndex, selectPlaceBy);
                                firstTimeDragAndDrop = false;
                                selectPlaceBy.valueProperty().addListener((observableSelect1, oldValueSelect1, newValueSelect1) ->
                                {
                                    if (firstTime) {
                                        TextField secondValueArgs = new TextField();
                                        gridPaneTrialList.addRow(rowIndex, secondValueArgs);
                                    }
                                    firstTime = false;
                                    firstTime2 = true;
                                });
                            }
                        });
                        System.out.println(gridPaneTrialList.getChildren());
                        lastType = "DragAndDrop";
                        break;
                    case "Selector":
                        firstTime = true;
                        System.out.println(lastType);
                        System.out.println("" + gridPaneTrialList.getChildren());
                        if(firstTime2) {
                            switch (lastType){
                                case "Id":
                                    gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 2, gridPaneTrialList.getChildren().size());
                                    break;
                                case "DragAndDrop":
                                    gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 4, gridPaneTrialList.getChildren().size());
                                    break;
                                case "Selector":
                                    gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size() - 2, gridPaneTrialList.getChildren().size());
                                    break;
                            }

                        }
                        System.out.println(gridPaneTrialList.getChildren());
                        ComboBox<String> selectSelectorBy = new ComboBox<>(FXCollections.observableArrayList(H2DAO.getSelectElementBy()));
                        gridPaneTrialList.addRow(rowIndex, selectSelectorBy);
                        selectSelectorBy.valueProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
                        {
                            if (firstTime) {
                                TextField firstValueArgs = new TextField();
                                gridPaneTrialList.addRow(rowIndex, firstValueArgs);
                            }
                            firstTime = false;
                            firstTime2 = true;
                        });
                        System.out.println(gridPaneTrialList.getChildren());
                        lastType = "Selector";
                        break;
                    case "default":

                        break;
                }
            });
        */
    }

   public void deleteActionRow()
   {
       //tableViewTest.getItems().removeAll(tableViewTest.getSelectionModel().getSelectedItem());
   }

   public void totalClose()
   {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("¿Nos dejas?");
       alert.setHeaderText("¿Alguien lee esto?");

       Optional<ButtonType> result = alert.showAndWait();
       if(result.get() == ButtonType.OK)
       {
           System.out.println("Muero porque me matas");
           System.exit(0);
       }
       else
       {
           System.out.println("Sigo vivo un día más");
       }

   }

}
