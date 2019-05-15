package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.SeleniumDAO;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import persistence.H2DAO;

import java.io.*;
import java.net.URL;
import java.util.*;


public class MainController implements Initializable {

    @FXML
    private Button buttonPlay;

    @FXML
    private Button buttonAdd;

    @FXML
    private  Button buttonDelete;

    @FXML
    private Button buttonDeleteTrial;

    @FXML
    private Button buttonModifyTrial;

    @FXML
    private Button buttonPlayTrials;

    @FXML
    private  ListView<CheckBox> testList;

    @FXML
    private GridPane gridPaneTrialList;

    @FXML
    private GridPane gridPaneValidationList;

    @FXML
    private Tab tabActions;

    @FXML
    private Tab tabValidation;

    @FXML
    private HBox bottomButtons;

    @FXML
    private Accordion accordionComprobationList;


    private static Scene sceneSettings;
    private static Stage stageSettings;

    private List<Action> actionList;
    private List<Action> validationList;
    private List<Action> procesedActionList;
    private List<Action> procesedValidationList;

    private int actionsRowIndex = 0;
    private int validationRowIndex = 0;

    String comboBoxActionType = "";
    String comboBoxSelectElementBy = "";
    String textFieldFirstValueArgs = "";
    String comboBoxSelectPlaceBy = "";
    String textFieldSecondValueArgs = "";
    String columnsHeadersCSV = "Action,FirstSelectBy,FirstValue,SecondSelectBy,SecondValue,Validation";


    private static DataFormat comboBoxFormat = new DataFormat();
    private static Integer rowIndexDrag;
    private static Integer rowIndexDrop;
    private static List<Node> draguedChildList;
    private static List<Node> movedChilds;



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //tableColumnTestCol.setCellValueFactory( (param) -> new SimpleStringProperty( param.getValue().toString()));
        actionList = new ArrayList<>();
        validationList = new ArrayList<>();
        procesedActionList = new ArrayList<>();
        procesedValidationList = new ArrayList<>();
        draguedChildList = new ArrayList<>();
        movedChilds = new ArrayList<>();

        //bottomButtons.setDisable(true);

        // TODO: Has to be done with:
        //            for (int i = 0; i < numColumns; i++)
        //            {
        //            ColumnConstraints colConstraint = new ColumnConstraints();
        //            colConst.setPercentWidth(100.0 / numColumns);
        //            gridPaneTrialList.getColumnConstraints().add(colConstraint);
        //            }

        int trialsCols = getColCount(gridPaneTrialList);
        for (int i = 0; i < trialsCols; i++)
        {
            ColumnConstraints columnConstraint = new ColumnConstraints();
            columnConstraint.setPercentWidth(100.0/trialsCols);
            gridPaneTrialList.getColumnConstraints().add(columnConstraint);
        }

        int validationCols = getColCount(gridPaneValidationList);
        for (int i = 0; i < validationCols; i++)
        {
            ColumnConstraints columnConstraint = new ColumnConstraints();
            columnConstraint.setPercentWidth(100.0/validationCols);
            gridPaneValidationList.getColumnConstraints().add(columnConstraint);
        }

        // TODO: This is plain wrong. You can't set row constraints
        //  this way, since you don't know the number of rows
        //  That's why scroll pane is broken after adding 5 rows or more
        //  Should be calculated and applied when adding new rows ...

        //tabPaneParent.setMinSize(1100,500);
        //tabPaneParent.setTabMinWidth(200);
        //testList.prefWidthProperty().bind(scrollPaneTrialList.widthProperty());
        //scrollPaneTrialList.setFitToWidth(true);
        //testList.prefHeightProperty().bind(scrollPaneTrialList.heightProperty());

        //tabPaneParent.setTabMinWidth(Math.round(tabPaneParent.getWidth()/2));

        if(!H2DAO.checkDB()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error en la base de datos");
            alert.setHeaderText("¿Desea reestablecer la base de datos?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK)
            {
                H2DAO.redoTables();
            } else {
                System.exit(0);
            }

        }
        testList.getSelectionModel().selectedItemProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
        {
                bottomButtons.setDisable(false);
                deleteAllTabs();
                getSelectedTrialActions();
                getSelectedTrialValidations();

        });

        if (testList.getSelectionModel().selectedItemProperty().getValue() == null)
        {
            bottomButtons.setDisable(true);
        }



        poblateTestList();
    }

    public void openSettingsDialog()
    {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Settings.fxml"));
            stageSettings = new Stage();
            stageSettings.setResizable(false);
            stageSettings.initModality(Modality.APPLICATION_MODAL);
            stageSettings.setAlwaysOnTop(true);
            stageSettings.setTitle("Settings");
            sceneSettings = new Scene(root,350,250);
            if (H2DAO.isDarkTheme()){
                setTheme("darcula");
            }else {
                setTheme("modena");
            }
            //scene.getStylesheets().add("/css/darcula.css");
            stageSettings.setScene(sceneSettings);
            stageSettings.showAndWait();


            /*
            class SceneFactory
            {
             static createSimpleScene(Parent root, int width, int height)
             {
                Scene scene = new Scene(root, width, height);
                scene.getStylesheets().add("/css/darcula.css");
                return scene;
             }
            }
             */
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addActionRow()
    {
        if(tabActions.isSelected())
        {
            //Action newAction = new Action(gridPaneTrialList, actionsRowIndex);
            ActionController actionController = new ActionController();
            actionController.addActiontoGrid(gridPaneTrialList, actionsRowIndex);
            //Action newAction = new Action(actionController.getActionTypeString(), actionController.getSelectElementByString(),
            //      actionController.getFirstValueArgsString(),actionController.getSelectPlaceByString(), actionController.getSecondValueArgsString());
            //actionList.add(newAction);
            actionsRowIndex++;
        }
        if(tabValidation.isSelected())
        {
            ActionController actionController = new ActionController();
            actionController.addActiontoGrid(gridPaneValidationList, validationRowIndex);
            //Action newAction = new Action(actionController.getActionTypeString(), actionController.getSelectElementByString(),
            //        actionController.getFirstValueArgsString(),actionController.getSelectPlaceByString(), actionController.getSecondValueArgsString());
            //validationList.add(newAction);
            validationRowIndex++;
        }

        /*int rows = getRowCount(gridPaneTrialList);
        RowConstraints rowConstraint = new RowConstraints();
        for (int i = 0; i < rows; i++)
        {

            double value = gridPaneTrialList.getHeight()/rows;
            rowConstraint.setPercentHeight(gridPaneTrialList.getHeight()/rows);

        }
        gridPaneTrialList.getRowConstraints().add(rowConstraint);
        */
    }


    public void deleteActionRow()
    {
        if(tabActions.isSelected())
        {
            /*List<Node> deleteNodes = new ArrayList<>();
            for (Node child : gridPaneTrialList.getChildren()) {
                if (gridPaneTrialList.getRowIndex(child) == actionsRowIndex - 1) {
                    deleteNodes.add(child);
                }
            }
            gridPaneTrialList.getChildren().removeAll(deleteNodes);
            if (actionsRowIndex > 0) {
                actionsRowIndex--;
            }*/
            deleteSelectedActions(gridPaneTrialList, actionsRowIndex);
        }
        if(tabValidation.isSelected())
        {
           /* List<Node> deleteNodes = new ArrayList<>();
            for (Node child : gridPaneValidationList.getChildren()) {
                if (gridPaneValidationList.getRowIndex(child) == validationRowIndex - 1) {
                    deleteNodes.add(child);
                }
            }
            gridPaneValidationList.getChildren().removeAll(deleteNodes);
            if (validationRowIndex > 0) {
                validationRowIndex--;
            }*/
           deleteSelectedActions(gridPaneValidationList, validationRowIndex);
        }
    }

    public void deleteSelectedActions(GridPane gridPane, int gridIndex)
    {
        List<Integer> actionsToDelete = new ArrayList<>();
        List<Node> nodesToDelete = new ArrayList<>();
        Integer[] actionsToKeep = new Integer[1000];
        int actionsToKeepIndex = 0;


        for (Node child : gridPane.getChildren())
        {
            if (child instanceof CheckBox && ((CheckBox)child).isSelected())
            {
                actionsToDelete.add(gridPane.getRowIndex(child));
            }
            if (child instanceof CheckBox && !((CheckBox)child).isSelected()){
                actionsToKeep[actionsToKeepIndex] = gridPane.getRowIndex(child);
                actionsToKeepIndex++;
            }
        }

        for (Integer index : actionsToDelete)
        {
            for (Node child : gridPane.getChildren())
            {
                if (gridPane.getRowIndex(child) == index)
                {
                    nodesToDelete.add(child);                                   // Obtener los hijos de cada fila
                }

            }
        }

        gridPane.getChildren().removeAll(nodesToDelete);
        for (Integer index : actionsToDelete)
        {
            gridPane.getRowConstraints().remove(index);
        }

       /* for (Node child : gridPane.getChildren())
        {
            /*for (int i = 0; i < actionsToKeep.length; i++) {
                //System.out.println(actionsToDelete.get(actionsToDelete.size()));
                if (gridPane.getRowIndex(child) >= actionsToDelete.get(actionsToDelete.size() - 1)) {
                    //int desplazamientos = gridPane.getRowIndex(child)-rowKeepIndex;


                    gridPane.setRowIndex(child, gridPane.getRowIndex(child) - actionsToKeep[i-1]);
                }
            }
            /// Otra prueba

            for (int i = 1; i < actionsToKeep.length; i++){
                if (actionsToKeep[i]==0 ){
                    gridPane.setRowIndex(child, 0);
                } else  {
                    gridPane.setRowIndex(child, gridPane.getRowIndex(child) - actionsToKeep[i-1]);
                }
            }
        }*/
        //gridPane.getRowConstraints().removeAll(actionsToKeep);
        //gridPane.getChildren().clear();

        /*
        for (Integer index : actionsToKeep)
        {
            for (Node child : nodesToKeep){
                if (gridPane.getRowIndex(child) == index){
                    gridPane.addRow(index,child);
                }
            }
        }*/


        gridIndex = gridIndex - actionsToDelete.size();



        // Repartir nuevos índices entre filas restantes
    }

    public void deletePanel()
    {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Confirmar Eliminación");
       alert.setHeaderText("Se perderán todos los datos");

       Optional<ButtonType> result = alert.showAndWait();
       if (result.get() == ButtonType.OK)
       {
           deleteSelectedTab();
       } else {
           // ... user chose CANCEL or closed the dialog
       }
   }

   public void deleteSelectedTab()
   {
       if (tabActions.isSelected())
       {
           gridPaneTrialList.getChildren().remove(0, gridPaneTrialList.getChildren().size());
           actionsRowIndex = 0;
       }

       if (tabValidation.isSelected())
       {
           gridPaneValidationList.getChildren().remove(0, gridPaneValidationList.getChildren().size());
           validationRowIndex = 0;
       }
   }

   public void deleteAllTabs()
   {
       gridPaneTrialList.getChildren().remove(0, gridPaneTrialList.getChildren().size());
       actionsRowIndex = 0;
       gridPaneValidationList.getChildren().remove(0, gridPaneValidationList.getChildren().size());
       validationRowIndex = 0;
   }

   public void processTable()
   {
       if (tabActions.isSelected())
       {
           procesedActionList.clear();
           goThroughTable("Actions");
           executeTest(procesedActionList, "");
       }
       if (tabValidation.isSelected())
       {
           procesedValidationList.clear();
           goThroughTable("Validations");
           executeTest(procesedValidationList,"");
       }
   }
    // TODO: Tasks and tests cant be launched from MainController thread
    //  Use Task or Platform.runLater to achieve this, and get this code concurrent
    //  with protected methods, working out the logic part out of this
   protected void executeTest(List<Action> actionList, String trialName)
   {

       Task<Void> task = new Task<Void>() {
           @Override
           protected Void call() throws Exception {

               // TODO: This checkbox has no value. You create UI object to store values.
               //  Plain wrong
               //CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();

               Platform.runLater(new Runnable() {
                   @Override
                   public void run() {
                       WebDriver driver = getWebDriver();
                       driver.get(H2DAO.getWeb());
                       TitledPane trial = new TitledPane();
                       Label titledPaneName = new Label();



                       //Image image = new Image(getClass().getResourceAsStream("sharp_delete_black_24dp.png"));
                       //buttonClose.setGraphic(new ImageView(image));
                       //borderPane.prefWidthProperty().bind(scene.widthProperty().subtract(40));
                       if (trialName == "")
                       {
                           CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
                           titledPaneName.setText(selectedTrial.getText());
                           //trial.setText(selectedTrial.getText());
                       }else {
                           titledPaneName.setText(trialName);
                           //trial.setText(trialName);
                       }

                       HBox contentPane = new HBox();
                       contentPane.setAlignment(Pos.CENTER);
                       contentPane.setPadding(new Insets(0, 30, 0, 10));
                       contentPane.minWidthProperty().bind(trial.widthProperty());

                       HBox region = new HBox();
                       region.setMaxWidth(Double.MAX_VALUE);
                       HBox.setHgrow(region, Priority.ALWAYS);

                       // Add our nodes to the contentPane



                       Button buttonClose = new Button("X");

                       buttonClose.setOnAction(new EventHandler<ActionEvent>() {
                           @Override
                           public void handle(ActionEvent event) {
                               accordionComprobationList.getPanes().remove(trial);
                           }
                       });

                       contentPane.getChildren().addAll(
                               titledPaneName,
                               region,
                               buttonClose
                       );
                       trial.setGraphic(contentPane);




                       GridPane grid = new GridPane();
                       grid.setVgap(2);
                       if (tabActions.isSelected()) {
                           for (int i = 0; i < actionList.size(); i++) {
                               Action currentAction = actionList.get(i);
                               grid.add(new Label("Action " + i + ":"), 0, i);
                               grid.add(new Label(" " + currentAction.executeAction(driver)), 1, i);
                           }
                       }
                       if (tabValidation.isSelected()){
                           for (int i = 0; i < actionList.size(); i++) {
                               Action currentValidation = actionList.get(i);
                               grid.add(new Label("Validation " + i + ":"), 0, i);
                               grid.add(new Label(" " + currentValidation.executeAction(driver)), 1, i);
                           }
                       }
                       trial.setContent(grid);
                       accordionComprobationList.getPanes().add(trial);
                   }
               });
               return null;
           }
       };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

   }

    @Nullable
    private WebDriver getWebDriver() {
        WebDriver driver = null;
        if (H2DAO.getBrowser().equals("Firefox") && H2DAO.isHeadless().equals("false"))
        {
            driver = SeleniumDAO.initializeFirefoxDriver();
        }
        if (H2DAO.getBrowser().equals("Chrome") && H2DAO.isHeadless().equals("false"))
        {
            driver = SeleniumDAO.initializeChromeDriver();
        }
        if (H2DAO.getBrowser().equals("Firefox") && H2DAO.isHeadless().equals("true"))
        {
            driver = SeleniumDAO.initializeFirefoxHeadlessDriver();
        }
        if (H2DAO.getBrowser().equals("Chrome") && H2DAO.isHeadless().equals("true"))
        {
            driver = SeleniumDAO.initializeChromeDriver();
        }
        return driver;
    }

    // TODO: Take settings into account to select the way to work with browsers
   public void executeTestHeadless(){
       WebDriver driver = SeleniumDAO.initializeFirefoxHeadlessDriver();
       driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

       TitledPane trial = new TitledPane();

       CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
       trial.setText(""+selectedTrial.getText());
       GridPane grid = new GridPane();
       grid.setVgap(2);
       for(int i = 0; i < actionList.size(); i++)
       {
          Action currentAction = actionList.get(i);


          grid.add(new Label(" "+currentAction.executeAction(driver)),1,i);
          trial.setContent(grid);
          accordionComprobationList.getPanes().add(trial);
       }
   }

   // Close app method
   public void totalClose()
   {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       //DialogPane dialog = alert.getDialogPane();
       //dialog.getStylesheets().add(getClass().getResource("/css/darcula.css").toExternalForm());
       alert.setTitle("¿Nos dejas?");
       alert.setHeaderText("Se perderán todos los cambios no guardados");
       Optional<ButtonType> result = alert.showAndWait();

       if (result.get() == ButtonType.OK)
       {
           System.out.println("Adiós mundo cruel");
           System.exit(0);
       }
       else
       {
           System.out.println("Muerte esquivada una vez más");
       }
   }

    public void newTest()
    {
        bottomButtons.setDisable(false);
        TextInputDialog dialog = new TextInputDialog("dialtest");
        dialog.setTitle("Nueva prueba");
        dialog.setHeaderText("");
        dialog.setContentText("Por favor introduzca el nombre de la prueba:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            //procesedActionList.clear();                                                     // Limpiar lista con las acciones de la tabla
            //goThroughTable("Actions");                                                      // Recorrer la tabla e introduce en procesedActionList las acciones
            //if(!procesedActionList.isEmpty()) {
                H2DAO.createTrial(result.get());                                                // Introducir nuevo trial con su nombre en trials
                //String id =  H2DAO.getTrialID(result.get());                                    // Obtener id del nuevo trial
                //H2DAO.modifyTest(procesedActionList, id, 0);                           // Insertar todas las acciones referentes al nuevo test en la tabla trials_actions
                //procesedValidationList.clear();                                                 // Limpiar lista con las valideaciones de la tabla
                //goThroughTable("Validations");                                                  // Recorrer la tabla e introduce en procesedValidationList las validaciones
                //H2DAO.modifyTest(procesedValidationList, id, 1);                       // Insertar todas las validaciones referentes al nuevo test en la tabla trials_actions
                poblateTestList();
                testList.getSelectionModel().selectLast();
            /*} else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Debe de haber una acción asociada al test");
                alert.setContentText("Contacta con tu administrador :)");
                alert.showAndWait();

             */
            //}

        }else{
            bottomButtons.setDisable(true);
        }

    }

    public void modifyTest()
    {
        boolean trialmodified = false;
        CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
        if(selectedTrial == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No hay ningún test seleccionado");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();
        } else {
            String trialName = testList.getSelectionModel().getSelectedItem().getText();
            String id = H2DAO.getTrialID(trialName);
            if (tabActions.isSelected()) {
                H2DAO.deleteTrialActions(id);
                procesedActionList.clear();
                goThroughTable("Actions");
                H2DAO.saveTrial(procesedActionList, id, 0);
            }
            if (tabValidation.isSelected()) {
                H2DAO.deleteTrialValidations(id);
                procesedValidationList.clear();
                goThroughTable("Validations");
                H2DAO.saveTrial(procesedValidationList, id, 1);
            }
            trialmodified = true;
        }

        if (trialmodified) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cambios comfirmados");
            alert.setHeaderText("Cambios efectuados con éxito");
            alert.showAndWait();
        }
    }

    public void saveTest()
    {
        bottomButtons.setDisable(false);
        TextInputDialog dialog = new TextInputDialog("dialtest");
        dialog.setTitle("Nueva prueba");
        dialog.setHeaderText("");
        dialog.setContentText("Por favor introduzca el nombre de la prueba:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
        {
            boolean trialmodified = false;
            String id = H2DAO.getTrialID(result.get());

            H2DAO.deleteTrialActions(id);
            procesedActionList.clear();
            goThroughTable("Actions");
            H2DAO.saveTrial(procesedActionList, id, 0);


            H2DAO.deleteTrialValidations(id);
            procesedValidationList.clear();
            goThroughTable("Validations");
            H2DAO.saveTrial(procesedValidationList, id, 1);

            trialmodified = true;


            if (trialmodified)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Test Importado");
                alert.setHeaderText("Test importado correctamente");
                alert.showAndWait();
            }
        }else{
            bottomButtons.setDisable(true);
        }
    }

    public void goThroughTable(String table)
    {

        int iterator = 0;
        int rowIndex = 0;
        GridPane gridPane = new GridPane();

        if(table.equals("Actions")){
            rowIndex = actionsRowIndex;
            gridPane = gridPaneTrialList;
        }

        if(table.equals("Validations"))
        {
            rowIndex = validationRowIndex;
            gridPane = gridPaneValidationList;
        }


        while(iterator< rowIndex)
        {
            int i = 0;
            int j = 0;
            for(Node child : gridPane.getChildren())
            {
                if (gridPane.getRowIndex(child) == iterator)
                {

                    if (child instanceof ComboBox && i == 0)
                    {
                        comboBoxActionType = ((ComboBox) child).getValue().toString();
                        i++;
                    } else if(child instanceof ComboBox && i == 1)
                    {
                        comboBoxSelectElementBy = ((ComboBox) child).getValue().toString();
                        i++;
                    } else if(child instanceof ComboBox && i == 2)
                    {
                        comboBoxSelectPlaceBy = ((ComboBox) child).getValue().toString();
                    }
                    if (child instanceof TextField && j == 0)
                    {
                        textFieldFirstValueArgs = ((TextField) child).getText();
                        j++;
                    } else if(child instanceof TextField && j == 1)
                    {
                        textFieldSecondValueArgs = ((TextField) child).getText();

                    }
                }
            }
            Action currentAction = new Action(comboBoxActionType,comboBoxSelectElementBy,textFieldFirstValueArgs,comboBoxSelectPlaceBy,textFieldSecondValueArgs);
            resetFields();

            if (table.equals("Actions")) {
                actionList.add(currentAction);
                procesedActionList.add(currentAction);
            }
            if (table.equals("Validations"))  {
                validationList.add(currentAction);
                procesedValidationList.add(currentAction);
            }
            iterator++;

        }
    }

    private void resetFields() {
        comboBoxActionType= "";
        comboBoxSelectElementBy = "";
        textFieldFirstValueArgs = "";
        comboBoxSelectPlaceBy = "";
        textFieldSecondValueArgs = "";
    }

    public void poblateTestList()
    {
        testList.getItems().remove(0, testList.getItems().size());
        ObservableList<CheckBox> checkBoxesList = FXCollections.observableArrayList();
        for (String trial: H2DAO.getTrials())
        {
            checkBoxesList.add(new CheckBox(trial));
        }
        testList.getItems().addAll(checkBoxesList);
    }

    public void getSelectedTrialActions()
    {
       ObservableList<CheckBox> trialsSelected = testList.getSelectionModel().getSelectedItems();

       for(CheckBox trial: trialsSelected)
       {
           String trialName = trial.getText();
           ArrayList<Action> trialActions = H2DAO.getActions(trialName);
           for(Action actionOfTrial : trialActions)
           {
               ActionController actionController = new ActionController();
               actionController.setAction(gridPaneTrialList, actionsRowIndex,actionOfTrial.getActionTypeS(),actionOfTrial.getSelectElementByS(),actionOfTrial.getFirstValueArgsS(),
                       actionOfTrial.getSelectPlaceByS(), actionOfTrial.getSecondValueArgsS());
               //Action action = new Action(gridPaneTrialList, actionsRowIndex,actionOfTrial.getActionTypeS(),actionOfTrial.getSelectElementByS(),
               //                             actionOfTrial.getFirstValueArgsS(),actionOfTrial.getSelectPlaceByS(),actionOfTrial.getSecondValueArgsS());
               //actionList.add(action);
               actionsRowIndex++;
           }
       }
    }

    public void getSelectedTrialValidations(){
        ObservableList<CheckBox> trialsSelected = testList.getSelectionModel().getSelectedItems();

        for(CheckBox trial: trialsSelected)
        {
            String trialName = trial.getText();
            ArrayList<Action> trialActions = H2DAO.getValidations(trialName);
            for(Action validation : trialActions)
            {
                ActionController actionController = new ActionController();
                actionController.setAction(gridPaneTrialList, actionsRowIndex,validation.getActionTypeS(),validation.getSelectElementByS(),
                         validation.getFirstValueArgsS(),validation.getSelectPlaceByS(),validation.getSecondValueArgsS());
                //Action action = new Action(gridPaneValidationList, validationRowIndex,validation.getActionTypeS(),validation.getSelectElementByS(),
                //        validation.getFirstValueArgsS(),validation.getSelectPlaceByS(),validation.getSecondValueArgsS());
                //validationList.add(action);
                validationRowIndex++;
            }
        }
    }

    public void runSelectedTrials()
    {
        for(CheckBox trial : testList.getItems())
        {
            if(trial.isSelected())
            {
                String trialName = trial.getText();
                ArrayList<Action> actions = H2DAO.getActions(trial.getText());
                executeTest(actions, trialName);
            }
        }
    }

    public void deleteSelectedTrial()
    {
        ObservableList<CheckBox> trials = testList.getItems();
        for (CheckBox trial : trials)
           {
               if (trial.isSelected())
               {
                   String id = H2DAO.getTrialID(trial.getText());
                   H2DAO.deleteTrialActions(id);
                   H2DAO.deleteTrialValidations(id);
                   H2DAO.deleteTrial(id);
               }
           }
           poblateTestList();
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

    private int getColCount(GridPane pane) {
        int numCols = pane.getColumnConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer colIndex = GridPane.getColumnIndex(child);
                if(colIndex != null){
                    numCols = Math.max(numCols,colIndex+1);
                }
            }
        }
        return numCols;
    }

    private void saveToCSV(ArrayList<Action> actions,ArrayList<Action> validations,File file) throws IOException {
            Writer writer = null;
            try {
                //File file = new File("/home/david/git_docs/"+name+".csv");
                writer = new BufferedWriter(new FileWriter(file));

               //writer.write(trialName);
               //writer.write(System.lineSeparator());
                writer.write(columnsHeadersCSV);

                for (Action action : actions)
                {
                    writer.write(System.lineSeparator());
                    writer.write("" + action.getActionTypeS() + ","
                            + action.getSelectElementByS() + ","
                            + action.getFirstValueArgsS() + ","
                            + action.getSelectPlaceByS() + ","
                            + action.getSecondValueArgsS() + ","
                            + "A");
                }
                for (Action validation : validations)
                {
                    writer.write(System.lineSeparator());
                    writer.write("" + validation.getActionTypeS() + ","
                            + validation.getSelectElementByS() + ","
                            + validation.getFirstValueArgsS() + ","
                            + validation.getSelectPlaceByS() + ","
                            + validation.getSecondValueArgsS() + ","
                            + "V");
                }
                System.out.println("FUNCIONA");

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                writer.flush();
                writer.close();
            }
    }

    public void exportTest()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("DialTest.csv");
        File file = fileChooser.showSaveDialog(stageSettings);


        for(CheckBox trial : testList.getItems())
        {
            if(trial.isSelected())
            {
                ArrayList<Action> actions = H2DAO.getActions(trial.getText());
                ArrayList<Action> validations = H2DAO.getValidations(trial.getText());
                try {
                    saveToCSV(actions,validations,file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Aviso de ninguno seleccionado
        }
    }

    public void importTest()
    {
        deleteAllTabs();

        boolean headerOk = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //File file = fileChooser.showOpenDialog(stageSettings);
        List<File> files = fileChooser.showOpenMultipleDialog(stageSettings);

        try {
            if (files!=null) {

                for (File file : files) {

                    Scanner inputStream = new Scanner(file);
                    while (inputStream.hasNext()) {
                        String data = inputStream.next();
                        //String action = data.substring()
                        if (data.equals(columnsHeadersCSV))
                        {
                            headerOk = true;
                        }
                        if (headerOk) {

                            String[] values = data.split(",");
                            //System.out.println(data);
                            if (values[5].equals("A")) {
                                ActionController actionController = new ActionController();
                                actionController.setAction(gridPaneTrialList, actionsRowIndex, values[0], values[1], values[2], values[3], values[4]);
                                Action act = new Action( values[0], values[1], values[2], values[3], values[4]);
                                actionList.add(act);
                                actionsRowIndex++;
                            }
                            if (values[5].equals("V")) {
                                ActionController actionController = new ActionController();
                                actionController.setAction(gridPaneValidationList, validationRowIndex, values[0], values[1], values[2], values[3], values[4]);
                                Action act = new Action(values[0], values[1], values[2], values[3], values[4]);
                                validationList.add(act);
                                validationRowIndex++;
                            }
                        }
                    }
                    if(headerOk)
                    {
                        //saveTest();
                        //deleteAllTabs();
                        inputStream.close();
                    }else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setHeaderText("Se ha producido un error durante la importación del test");
                        alert.setContentText("El fichero no contienen las columnas correctas");
                        alert.showAndWait();
                    }
                }
            }

            } catch(FileNotFoundException e){
                e.printStackTrace();
            }


    }

    public static void dragAndDrop(GridPane gridParent, ComboBox dragItem)
    {
        rowIndexDrop = 0;
        rowIndexDrag = 0;
        draguedChildList.clear();
        movedChilds.clear();



        dragItem.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = dragItem.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(comboBoxFormat, " ");
                db.setContent(content);
                rowIndexDrag = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                System.out.println(rowIndexDrag);
                for (Node child : gridParent.getChildren()){
                    if (gridParent.getRowIndex(child) == rowIndexDrag)
                    {
                        draguedChildList.add(child);
                    }
                }
                event.consume();

            }
        });

        dragItem.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                gridParent.getChildren().removeAll(draguedChildList);
                gridParent.getRowConstraints().remove(rowIndexDrag);
                event.consume();
            }
        });

        dragItem.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                StackPane intento = new StackPane();
                if(event.getPickResult().getClass().isInstance(intento))
                {
                    System.out.println("Tomaaaa");
                    Node nodo = event.getPickResult().getIntersectedNode().getParent();
                    System.out.println(nodo.toString());
                }
                rowIndexDrop = gridParent.getRowIndex(event.getPickResult().getIntersectedNode());
                /*boolean success = false;
                if (db.hasString()) {
                    //target.setText(db.getString());
                    success = true;
                }*/


                for (Node child : gridParent.getChildren()) {
                    if(gridParent.getRowIndex(child) >= rowIndexDrop){
                        gridParent.setRowIndex(child, gridParent.getRowIndex(child)+1);
                        //movedChilds.add(child);
                        //gridParent.getChildren().remove(child);

                    }
                }

                event.setDropCompleted(true);
                System.out.println("Tomaaa con to mi node " + rowIndexDrop);



                event.consume();
            }
        });

        dragItem.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                if (event.getTransferMode() == TransferMode.MOVE) {

                }
                /*for (Node child : gridParent.getChildren()) {
                    System.out.println("El rowIndex donde voy a dejar esta mierda es: " + GridPane.getRowIndex(child));
                    if(GridPane.getRowIndex(child) == rowIndexDrop){
                        System.out.println("La madre del: " + child);
                        gridParent.getChildren().remove(child);
                    }
                }*/
                for (Node item : draguedChildList){
                    gridParent.addRow(rowIndexDrop, item);
                    gridParent.setRowIndex(item, rowIndexDrop);
                    //gridParent.setRowIndex(item, gridParent.getRowIndex(item));
                }
                /*for (Node item : movedChilds){
                    gridParent.addRow(gridParent.getRowIndex(item)+1, item);
                }*/
                //gridParent.addColumn(rowIndexDrop, dragItem);

                event.consume();
            }
        });
    }

    public static void setTheme(String theme)
    {

        sceneSettings.getStylesheets().clear();
        if (theme.equals("darcula"))
        {
            sceneSettings.getStylesheets().add("/css/darcula.css");
        }
        if (theme.equals("modena"))
        {
            //StyleManager.getInstance().addUserAgentStylesheet(getClass().getResource("/style.css").toString());
            Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        }
    }

    public static void closeSettings()
    {
        stageSettings.close();
    }




}
