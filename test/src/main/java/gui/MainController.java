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
import main.Utils;
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
    private ListView<CheckBox> testList;

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
    private static Scene sceneVariables;
    private static Stage stageSettings;
    private static Stage stageVariables;

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


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //tableColumnTestCol.setCellValueFactory( (param) -> new SimpleStringProperty( param.getValue().toString()));
        actionList = new ArrayList<>();
        validationList = new ArrayList<>();
        procesedActionList = new ArrayList<>();
        procesedValidationList = new ArrayList<>();




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
                setTheme("Settings","darcula");
            }else {
                setTheme("Settings","modena");
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

    public void openVariablesDialog()
    {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Variables.fxml"));
            stageVariables = new Stage();
            stageVariables.setResizable(false);
            stageVariables.initModality(Modality.APPLICATION_MODAL);
            stageVariables.setAlwaysOnTop(true);
            stageVariables.setTitle("Variables");
            sceneVariables = new Scene(root,600,400);
            if (H2DAO.isDarkTheme()){
                setTheme("Variables","darcula");
            }else {
                setTheme("Variables","modena");
            }

            //VariablesController variablesController = new VariablesController(H2DAO.getTrialID(testList.getSelectionModel().getSelectedItem().getText()));
            //variablesController.setTrialID(H2DAO.getTrialID(testList.getSelectionModel().getSelectedItem().getText()));

            stageVariables.setScene(sceneVariables);
            stageVariables.showAndWait();

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
            actionController.addActionToGrid(gridPaneTrialList, actionsRowIndex);
            //Action newAction = new Action(actionController.getActionTypeString(), actionController.getSelectElementByString(),
            //      actionController.getFirstValueArgsString(),actionController.getSelectPlaceByString(), actionController.getSecondValueArgsString());
            //actionList.add(newAction);
            actionsRowIndex++;
        }
        if(tabValidation.isSelected())
        {
            ActionController actionController = new ActionController();
            actionController.addActionToGrid(gridPaneValidationList, validationRowIndex);
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
            deleteSelectedActions(gridPaneTrialList);
            actionsRowIndex = getRowCount(gridPaneTrialList);
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
           deleteSelectedActions(gridPaneValidationList);
           validationRowIndex = getRowCount(gridPaneValidationList);
        }
    }

    public void deleteSelectedActions(GridPane gridPane)
    {
        List<Node> nodesToDelete = new ArrayList<>();
        int iterations = 0;
        int rowToDelete = -1;

        for (Node hbox : gridPane.getChildren())                                   // Number of rows to delete
        {
            if (hbox instanceof HBox)
            {
                for (Node child : ((HBox) hbox).getChildren())
                {
                    if (child instanceof CheckBox && ((CheckBox)child).isSelected())
                    {
                        iterations++;
                    }
                }
            }
        }

        for (int i = 0; i < iterations; i++)
        {
            for (Node hbox : gridPane.getChildren())
            {
                if (hbox instanceof  HBox){
                    for (Node child : ((HBox) hbox).getChildren())
                    {
                        if (child instanceof CheckBox && ((CheckBox)child).isSelected())
                        {
                            rowToDelete = gridPane.getRowIndex(child.getParent());
                            break;
                        }
                    }
                }


            }

            for (Node child : gridPane.getChildren())
            {
                if (rowToDelete != -1) {                                    // Fila para borrar
                    if (gridPane.getRowIndex(child) == rowToDelete)
                    {                                                       // Hijo de la fila a borrar
                        child.setVisible(false);
                        nodesToDelete.add(child);
                    }else if (gridPane.getRowIndex(child) > rowToDelete){
                        gridPane.setRowIndex(child,gridPane.getRowIndex(child)-1);
                    }
                }
            }
            gridPane.getChildren().removeAll(nodesToDelete);
        }
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



                       Button buttonClose = new Button();
                       buttonClose.setStyle("-fx-background-color: transparent;");
                       Image image = new Image(getClass().getResource("/icons/sharp_delete_black_24dp.png").toString());
                       ImageView imageView = new ImageView(image);
                       imageView.setFitHeight(17);
                       imageView.setFitWidth(17);
                       buttonClose.setGraphic(imageView);


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

    public void newTrial()
    {
        bottomButtons.setDisable(false);
        TextInputDialog dialog = new TextInputDialog("dialtest");
        dialog.setTitle("Nueva prueba");
        dialog.setHeaderText("");
        dialog.setContentText("Por favor introduzca el nombre de la prueba:");

        Optional<String> result = dialog.showAndWait();

        List<String> trialNames = new ArrayList<>();
        for (CheckBox item : testList.getItems()){
            trialNames.add(item.getText());
        }
        if (result.isPresent()){
            if (trialNames.contains(result.get()))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Nombre de trial repetido");
                alert.setContentText("Contacta con tu administrador :)");
                alert.showAndWait();
            } else {
                H2DAO.createTrial(result.get());
                poblateTestList();
                testList.getSelectionModel().selectLast();
            }


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
                trialmodified = true;
            }
            if (tabValidation.isSelected()) {
                H2DAO.deleteTrialValidations(id);
                procesedValidationList.clear();
                goThroughTable("Validations");
                H2DAO.saveTrial(procesedValidationList, id, 1);
            }

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
            H2DAO.createTrial(result.get());                                                // Introducir nuevo trial con su nombre en trials
            String id = H2DAO.getTrialID(result.get());

            //H2DAO.deleteTrialActions(id);
            //procesedActionList.clear();
            goThroughTable("Actions");
            H2DAO.saveTrial(procesedActionList, id, 0);


            //H2DAO.deleteTrialValidations(id);
            //procesedValidationList.clear();
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
        boolean unique = false;
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
            for(Node hbox : gridPane.getChildren())
            {
                if (hbox instanceof HBox)
                {

                    for (Node child : ((HBox) hbox).getChildren())
                    {
                        if (gridPane.getRowIndex(child.getParent()) == iterator)
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
                            } else if (child instanceof TextField && j ==1)
                            {
                                textFieldSecondValueArgs = ((TextField) child).getText();

                            }

                            if(child instanceof StackPane && j == 1)
                            {
                                for (Node stackChild : ((StackPane) child).getChildren())
                                {
                                    if (stackChild instanceof TextField)
                                    {
                                        textFieldSecondValueArgs = ((TextField) stackChild).getText();
                                    }
                                }

                            }
                            if (child instanceof HBox)
                            {
                                for (Node hboxChild : ((HBox) child).getChildren())
                                {
                                    if (hboxChild instanceof CheckBox && ((CheckBox) hboxChild).isSelected())
                                    {
                                        unique = true;
                                    }
                                }
                            }
                        }
                    }
                }


            }
            if (unique){
                textFieldSecondValueArgs = Utils.generateUniqueID(textFieldSecondValueArgs);
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
                actionController.setAction(gridPaneValidationList, validationRowIndex,validation.getActionTypeS(),validation.getSelectElementByS(),
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

    public void modifyTrialName()
    {
        CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();

        List<String> trialNames = new ArrayList<>();
        for (CheckBox item : testList.getItems()){
            trialNames.add(item.getText());
        }

        if (selectedTrial == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Debe haber un test seleccionado");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();

        }else {

            TextInputDialog dialog = new TextInputDialog("DialTest");
            dialog.setTitle("Modificando nombre de la prueba");
            dialog.setHeaderText("");
            dialog.setContentText("Por favor introduzca el nuevo nombre de la prueba:");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (trialNames.contains(result.get()))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Nombre de trial repetido");
                    alert.setContentText("Contacta con tu administrador :)");
                    alert.showAndWait();
                }else {
                    H2DAO.updateTrialName(selectedTrial.getText(), result.get());
                    selectedTrial.setText(result.get());
                }
            }
        }
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
                            + "false");
                }
                for (Action validation : validations)
                {
                    writer.write(System.lineSeparator());
                    writer.write("" + validation.getActionTypeS() + ","
                            + validation.getSelectElementByS() + ","
                            + validation.getFirstValueArgsS() + ","
                            + validation.getSelectPlaceByS() + ","
                            + validation.getSecondValueArgsS() + ","
                            + "true");
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
        boolean oneSelected = false;
        for (CheckBox item : testList.getItems())
        {
            if (item.isSelected()){
                oneSelected = true;
            }
        }

        if (!oneSelected) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Debe haber un test checkeado");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();
        } else {

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("DialTest.csv");
            File file = fileChooser.showSaveDialog(stageSettings);

            if (file != null) {
                for (CheckBox trial : testList.getItems()) {
                    if (trial.isSelected()) {
                        ArrayList<Action> actions = H2DAO.getActions(trial.getText());
                        ArrayList<Action> validations = H2DAO.getValidations(trial.getText());
                        try {
                            saveToCSV(actions, validations, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // Aviso de ninguno seleccionado
                }
            }
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
                        String data = inputStream.nextLine();
                        //String action = data.substring()
                        if (data.equals(columnsHeadersCSV))
                        {
                            headerOk = true;
                        }
                        if (headerOk) {

                            String[] values = data.split(",");
                            //System.out.println(data);
                            if (values[5].equals("false")) {
                                ActionController actionController = new ActionController();
                                actionController.setAction(gridPaneTrialList, actionsRowIndex, values[0], values[1], values[2], values[3], values[4]);
                                Action act = new Action( values[0], values[1], values[2], values[3], values[4]);
                                actionList.add(act);
                                //procesedActionList.add(act);
                                actionsRowIndex++;
                            }
                            if (values[5].equals("true")) {
                                ActionController actionController = new ActionController();
                                actionController.setAction(gridPaneValidationList, validationRowIndex, values[0], values[1], values[2], values[3], values[4]);
                                Action act = new Action(values[0], values[1], values[2], values[3], values[4]);
                                validationList.add(act);
                                //procesedValidationList.add(act);
                                validationRowIndex++;
                            }
                        }
                    }
                    if(headerOk)
                    {
                        saveTest();
                        //deleteAllTabs();
                        poblateTestList();
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

    public String getSelectedTrialId()
    {
        String trialid = "";

        if (testList.getSelectionModel().getSelectedItem() == null){
            // ALERTA
        }else {
            String trial = testList.getSelectionModel().getSelectedItem().getText();
            trialid = H2DAO.getTrialID(trial);
        }
        return trialid;
    }

    public static String getPathOFC()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stageSettings);
        if (file == null){
            return "";
        }
        return file.getPath();

    }


    public static void setTheme(String stage,String theme)
    {

        if (stage.equals("Settings")) {
            sceneSettings.getStylesheets().clear();
            if (theme.equals("darcula")) {
                sceneSettings.getStylesheets().add("/css/darcula.css");
            }
            if (theme.equals("modena")) {
                Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
            }
        }

        if (stage.equals("Variables")) {
            sceneVariables.getStylesheets().clear();
            if (theme.equals("darcula")) {
                sceneVariables.getStylesheets().add("/css/darcula.css");
            }
            if (theme.equals("modena")) {
                Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
            }
        }
    }

    public static void closeStage(String stage)
    {
        if (stage.equals("Settings")){
            stageSettings.close();
        }
        if (stage.equals("Variables")){
            stageVariables.close();
        }
    }




}
