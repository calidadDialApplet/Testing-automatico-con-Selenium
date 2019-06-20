package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.jetbrains.annotations.Nullable;


import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import persistence.H2DAO;
import sun.nio.cs.ext.ISO_8859_11;


import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.ISO_8859_1;


public class MainController implements Initializable {

    @FXML
    private Button buttonPlay;

    @FXML
    private Button buttonAdd;

    @FXML
    private  Button buttonDelete;

    @FXML
    private Button buttonDeleteAll;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonDeleteTrial;

    @FXML
    private Button buttonModifyTrial;

    @FXML
    private Button buttonAddTrial;

    @FXML
    private Button buttonPlayTrials;

    @FXML
    private Button buttonCopy;

    @FXML
    private Button buttonPaste;

    @FXML
    private Button buttonVariables;

    @FXML
    private  Button buttonClone;

    @FXML
    private ListView<CheckBox> testList;

    @FXML
    private GridPane gridPaneTrialList;

    @FXML
    private GridPane gridPaneValidationList;

    @FXML
    private TabPane tabPaneParent;

    @FXML
    private Tab tabActions;

    @FXML
    private Tab tabValidation;

    @FXML
    private HBox bottomButtons;

    @FXML
    private Accordion accordionComprobationList;

    @FXML
    private Label labelStatus;

    @FXML
    private Spinner spinner;


    private static Scene sceneSettings;
    private static Scene sceneVariables;
    private static Stage stageSettings;
    private static Stage stageVariables;

    private List<Action> actionList;
    private List<Action> validationList;
    private List<Action> procesedActionList;
    private List<Action> procesedValidationList;
    private List<Action> copiedActionList;
    private ArrayList<Variable> variablesList;

    private int actionsRowIndex = 0;
    private int validationRowIndex = 0;
    private int copiedActions = 0;


    private static Integer rowIndexDrag;
    private static Integer rowIndexDrop;
    private static List<CheckBox> draggedChildList = new ArrayList<>();;
    private static List<CheckBox> movedChilds = new ArrayList<>();;

    String comboBoxActionType = "";
    String comboBoxSelectElementBy = "";
    String textFieldFirstValueArgs = "";
    String comboBoxSelectPlaceBy = "";
    String textFieldSecondValueArgs = "";
    String trialColumnsHeadersCSV = "Action,FirstSelectBy,FirstValue,SecondSelectBy,SecondValue,Validation";
    String variablesColumnsHeadersCSV = "TrialID,VariableName,Value";
    String globalVariableHeader = "VariableName, Value";

    private static DataFormat checkBoxFormat = new DataFormat();

    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\S+\\}");
    private static Pattern GLOBAL_VARIABLE_PATTERN = Pattern.compile("\\€\\{\\S+\\}");


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //tableColumnTestCol.setCellValueFactory( (param) -> new SimpleStringProperty( param.getValue().toString()));
        actionList = new ArrayList<>();
        validationList = new ArrayList<>();
        copiedActionList = new ArrayList<>();
        procesedActionList = new ArrayList<>();
        procesedValidationList = new ArrayList<>();
        variablesList = new ArrayList<>();

        SpinnerValueFactory<Integer> executions = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5,1);
        spinner.setValueFactory(executions);


        setTooltips();



        /*final ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(50, 50);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        //rootPane.setCenter(new Label("done"));
                    }
                });
            }
        }).start();*/

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

        // TODO: Has to be done with:
        //            for (int i = 0; i < numColumns; i++)
        //            {
        //            ColumnConstraints colConstraint = new ColumnConstraints();
        //            colConst.setPercentWidth(100.0 / numColumns);
        //            gridPaneTrialList.getColumnConstraints().add(colConstraint);
        //            }

        tabPaneParent.widthProperty().addListener(((observable, oldValue, newValue) ->
        {
                tabPaneParent.setTabMinWidth(tabPaneParent.getWidth() / 2);
                tabPaneParent.setTabMinWidth(tabPaneParent.getWidth() / 2);
        }));

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


        testList.getSelectionModel().selectedItemProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
        {
            System.out.println(Main.isModified());
                if (Main.isModified())
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Guardar trial modificado");
                    alert.setHeaderText("Los cambios no guardados se perderán\n" +
                                        "¿Quieres guardar?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK)
                    {
                        modifyTestListener(oldValueSelect);
                        Main.setModified(false);
                        deleteAllTabs();
                        getSelectedTrialActions();
                        getSelectedTrialValidations();
                    } else {
                        Main.setModified(false);
                        bottomButtons.setDisable(false);
                        deleteAllTabs();
                        getSelectedTrialActions();
                        getSelectedTrialValidations();
                    }
                }else {
                    bottomButtons.setDisable(false);
                    deleteAllTabs();
                    getSelectedTrialActions();
                    getSelectedTrialValidations();
                }
        });



        if (testList.getSelectionModel().selectedItemProperty().getValue() == null)
        {
            bottomButtons.setDisable(true);
        }


        poblateTestList();
        dragAndDrop();
    }

    private void setTooltips() {
        Tooltip addButtonTooltip = new Tooltip();
        addButtonTooltip.setText("Añade una acción");
        addButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonAdd.setTooltip(addButtonTooltip);

        Tooltip deleteButtonTooltip = new Tooltip();
        deleteButtonTooltip.setText("Elimina las acciones checkeadas");
        deleteButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonDelete.setTooltip(deleteButtonTooltip);

        Tooltip deleteAllButtonTooltip = new Tooltip();
        deleteAllButtonTooltip.setText("Elimina el contenido de la tabla");
        deleteAllButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonDeleteAll.setTooltip(deleteAllButtonTooltip);

        Tooltip saveButtonTooltip = new Tooltip();
        saveButtonTooltip.setText("Guarda la tabla");
        saveButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonSave.setTooltip(saveButtonTooltip);

        Tooltip playButtonTooltip = new Tooltip();
        playButtonTooltip.setText("Ejecuta el contenido de la tabla");
        playButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonPlay.setTooltip(playButtonTooltip);

        Tooltip copyButtonTooltip = new Tooltip();
        copyButtonTooltip.setText("Copia las acciones checkeadas");
        copyButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonCopy.setTooltip(copyButtonTooltip);

        Tooltip pasteButtonTooltip = new Tooltip();
        pasteButtonTooltip.setText("Pega las acciones copiadas");
        pasteButtonTooltip.setStyle("-fx-text-fill: orange;");
        buttonPaste.setTooltip(pasteButtonTooltip);

        Tooltip playTrialsTooltip = new Tooltip();
        playTrialsTooltip.setText("Ejecuta las pruebas checkeadas");
        playTrialsTooltip.setStyle("-fx-text-fill: orange;");
        buttonPlayTrials.setTooltip(playTrialsTooltip);

        Tooltip deleteTrialsTooltip = new Tooltip();
        deleteTrialsTooltip.setText("Elimina las pruebas checkeadas");
        deleteTrialsTooltip.setStyle("-fx-text-fill: orange;");
        buttonDeleteTrial.setTooltip(deleteTrialsTooltip);

        Tooltip addTrialTooltip = new Tooltip();
        addTrialTooltip.setText("Crear una prueba vacía");
        addTrialTooltip.setStyle("-fx-text-fill: orange;");
        buttonAddTrial.setTooltip(addTrialTooltip);

        Tooltip modifyTrialNameTooltip = new Tooltip();
        modifyTrialNameTooltip.setText("Modifica el nombre de la prueba seleccionada");
        modifyTrialNameTooltip.setStyle("-fx-text-fill: orange;");
        buttonModifyTrial.setTooltip(modifyTrialNameTooltip);

        Tooltip variablesTooltip = new Tooltip();
        variablesTooltip.setText("Variables de la prueba seleccionada");
        variablesTooltip.setStyle("-fx-text-fill: orange;");
        buttonVariables.setTooltip(variablesTooltip);

        Tooltip cloneTooltip = new Tooltip();
        cloneTooltip.setText("Clona la prueba seleccionada");
        cloneTooltip.setStyle("-fx-text-fill: orange;");
        buttonClone.setTooltip(cloneTooltip);
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
            sceneSettings = new Scene(root,370,270);
            if (H2DAO.isDarkTheme()){
                setTheme("Settings","darcula");
            }else {
                setTheme("Settings","modena");
            }
            //scene.getStylesheets().add("/css/darcula.css");
            stageSettings.setScene(sceneSettings);
            stageSettings.show();


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

            VariablesController variablesController = new VariablesController();
            if (testList.getSelectionModel().getSelectedItem() == null)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("No hay ningún test seleccionado");
                alert.setContentText("No lo ves o que? :)");
                alert.showAndWait();
            }else {
                variablesController.setTrialID(H2DAO.getTrialID(testList.getSelectionModel().getSelectedItem().getText())); // Separa vista y logica!

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Variables.fxml"));
                loader.setController(variablesController);
                Parent root = loader.load();
                stageVariables = new Stage();
                stageVariables.setResizable(false);
                stageVariables.initModality(Modality.APPLICATION_MODAL);
                stageVariables.setAlwaysOnTop(true);
                stageVariables.setTitle("Variables");
                sceneVariables = new Scene(root, 600, 400);
                if (H2DAO.isDarkTheme()) {
                    setTheme("Variables", "darcula");
                } else {
                    setTheme("Variables", "modena");
                }

                stageVariables.setScene(sceneVariables);
                stageVariables.show();

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addActionRow()
    {
        Main.setModified(true);
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
            deleteSelectedActions(gridPaneTrialList);
            reorderIndex(gridPaneTrialList);
            actionsRowIndex = getRowCount(gridPaneTrialList);
            Main.setModified(true);
        }
        if(tabValidation.isSelected())
        {
           deleteSelectedActions(gridPaneValidationList);
           reorderIndex(gridPaneValidationList);
           validationRowIndex = getRowCount(gridPaneValidationList);
           Main.setModified(true);
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
           Main.setModified(true);
       }

       if (tabValidation.isSelected())
       {
           gridPaneValidationList.getChildren().remove(0, gridPaneValidationList.getChildren().size());
           validationRowIndex = 0;
           Main.setModified(true);
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
           if (checkVariablesFormat(procesedActionList))
           {
               if (checkWaitingForTime(procesedActionList))
               {
                   executeTest(procesedActionList, "");
               }else {
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setTitle("Error");
                   alert.setHeaderText("Algun tiempo de los waiting for no es un entero");
                   alert.setContentText("Estas tonto o que? :)");
                   alert.initModality(Modality.APPLICATION_MODAL);
                   alert.initOwner(testList.getScene().getWindow());
                   alert.show();
               }
           } else {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setTitle("Error");
               alert.setHeaderText("Alguna de las variables no tiene el formato adecuado");
               alert.setContentText("Estas tonto o que? :)");
               alert.initModality(Modality.APPLICATION_MODAL);
               alert.initOwner(testList.getScene().getWindow());
               alert.show();
           }
       }
       if (tabValidation.isSelected())
       {
           procesedValidationList.clear();
           goThroughTable("Validations");
           if (checkVariablesFormat(procesedValidationList))
           {
               if (checkWaitingForTime(procesedValidationList))
               {
                   executeTest(procesedValidationList, "");
               } else {
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setTitle("Error");
                   alert.setHeaderText("Algun tiempo de los waiting for no es un entero");
                   alert.setContentText("Estas tonto o que? :)");
                   alert.initModality(Modality.APPLICATION_MODAL);
                   alert.initOwner(testList.getScene().getWindow());
                   alert.show();
               }
           }else {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setTitle("Error");
               alert.setHeaderText("Alguna de las variables no tiene el formato adecuado");
               alert.setContentText("Estas tonto o que? :)");
               alert.initModality(Modality.APPLICATION_MODAL);
               alert.initOwner(testList.getScene().getWindow());
               alert.show();
           }

       }
   }
    // TODO: Tasks and tests cant be launched from MainController thread
    //  Use Task or Platform.runLater to achieve this, and get this code concurrent
    //  with protected methods, working out the logic part out of this
   protected void executeTest(List<Action> actionList, String trialName)
   {


       ArrayList<String> results = new ArrayList<>();


       Task<Void> task = new Task<Void>() {
           @Override
           protected Void call() throws Exception {
                       String nameOfTrial = null;

                       WebDriver driver = getWebDriver();
                       driver.get(H2DAO.getWeb());
                       ArrayList<Variable> variables;

                       if (trialName == "")
                       {
                           CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
                           //titledPaneName.setText(selectedTrial.getText());
                           variables = H2DAO.getTrialVariables(H2DAO.getTrialID(selectedTrial.getText()));
                           nameOfTrial = selectedTrial.getText();
                           //trial.setText(selectedTrial.getText());

                       }else {
                           //titledPaneName.setText(trialName);
                           variables = H2DAO.getTrialVariables(H2DAO.getTrialID(trialName));
                           nameOfTrial = trialName;
                           //trial.setText(trialName);
                       }




                       if (tabActions.isSelected()) {
                           for (int i = 0; i < actionList.size(); i++) {
                               Action currentAction = actionList.get(i);
                               //grid.add(new Label("Action " + i + ":"), 0, i);
                               //grid.add(new Label(" " + currentAction.executeAction(driver, variables,nameOfTrial, Thread.currentThread())), 1, i);
                               results.add("Action "+ i + ": " +currentAction.executeAction(driver, variables, nameOfTrial, Thread.currentThread()));
                           }
                       }
                       if (tabValidation.isSelected()){
                           for (int i = 0; i < actionList.size(); i++) {
                               Action currentValidation = actionList.get(i);
                               //grid.add(new Label("Validation " + i + ":"), 0, i);
                               //grid.add(new Label(" " + currentValidation.executeAction(driver, variables,nameOfTrial, Thread.currentThread())), 1, i);
                               results.add("Validation "+ i + ": " +currentValidation.executeAction(driver, variables, nameOfTrial, Thread.currentThread()));
                           }
                       }

                       synchronized (results){
                           results.notify();
                       }
                       //notify();
                       //Thread.currentThread().interrupt();
               return null;
           }
       };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

       synchronized (results)
       {
           if (results.size() == 0)
           {
               try {
                   results.wait();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
       fillgrid(results, trialName);
       /*try {
           wait();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }*/


   }

   private void fillgrid(ArrayList<String> results, String trialName)
   {
       HBox contentPane = new HBox();
       TitledPane trial = new TitledPane();
       Label titledPaneName = new Label();
       String nameOfTrial = null;

       contentPane.setAlignment(Pos.CENTER);
       contentPane.setPadding(new Insets(0, 30, 0, 10));
       contentPane.minWidthProperty().bind(trial.widthProperty());

       HBox region = new HBox();
       region.setMaxWidth(Double.MAX_VALUE);
       HBox.setHgrow(region, Priority.ALWAYS);


       if (trialName == "")
       {
           CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
           titledPaneName.setText(selectedTrial.getText());
           //variables = H2DAO.getTrialVariables(H2DAO.getTrialID(selectedTrial.getText()));
           nameOfTrial = selectedTrial.getText();
           trial.setText(selectedTrial.getText());

       }else {
           titledPaneName.setText(trialName);
           //variables = H2DAO.getTrialVariables(H2DAO.getTrialID(trialName));
           nameOfTrial = trialName;
           trial.setText(trialName);
       }

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

       int i = 0;
       for (String result : results)
       {
           grid.addRow(i, new Label(result));
           i++;
       }

       trial.setContent(grid);
       accordionComprobationList.getPanes().add(trial);
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
        TextInputDialog dialog = new TextInputDialog("Prueba");
        dialog.setTitle("Nueva prueba");
        dialog.setHeaderText("");
        dialog.setContentText("Por favor introduzca el nombre de la prueba sin '_' :");

        Optional<String> result = dialog.showAndWait();

        if (result.toString().contains("_"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El nombre de la prueba contiene '_'");
            alert.setContentText("Estas tonto o que? :)");
            alert.show();
        } else {
            if (result.isPresent()) {
                if (!checkTrialName(result.get())) {
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


            } else {
                bottomButtons.setDisable(true);
            }
        }

    }

    private boolean checkTrialName(String name)
    {
        boolean nameOk = true;
        List<String> trialNames = new ArrayList<>();
        for (CheckBox item : testList.getItems()) {
            trialNames.add(item.getText());
        }
        if (trialNames.contains(name)){
            nameOk = false;
        }

        return  nameOk;
    }

    public void modifyTest()
    {
        Main.setModified(false);
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
                procesedActionList.clear();
                goThroughTable("Actions");
                if (checkVariablesFormat(procesedActionList))
                {
                    if (checkWaitingForTime(procesedActionList)) {
                        H2DAO.deleteTrialActions(id);
                        H2DAO.saveTrial(procesedActionList, id, 0);
                        trialmodified = true;
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Algun tiempo de los waiting for no es un entero");
                        alert.setContentText("Estas tonto o que? :)");
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(testList.getScene().getWindow());
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Alguna de las variables no tiene el formato adecuado");
                    alert.setContentText("Estas tonto o que? :)");
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(testList.getScene().getWindow());
                    alert.show();
                }

            }
            if (tabValidation.isSelected()) {
                procesedValidationList.clear();
                goThroughTable("Validations");
                if (checkVariablesFormat(procesedValidationList))
                {
                    if (checkWaitingForTime(procesedValidationList))
                    {
                        H2DAO.deleteTrialValidations(id);
                        H2DAO.saveTrial(procesedValidationList, id, 1);
                        trialmodified = true;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Algun tiempo de los waiting for no es un entero");
                        alert.setContentText("Estas tonto o que? :)");
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(testList.getScene().getWindow());
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Alguna de las variables no tiene el formato adecuado");
                    alert.setContentText("Estas tonto o que? :)");
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(testList.getScene().getWindow());
                    alert.show();
                }

            }

        }

        if (trialmodified) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cambios comfirmados");
            alert.setHeaderText("Cambios efectuados con éxito");
            alert.showAndWait();
        }
        // ALERTA
    }

    public void modifyTestListener(CheckBox trial)
    {

        boolean trialmodified = false;
        //CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
        /*if(selectedTrial == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No hay ningún test seleccionado");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();
        } else {*/
            String trialName = trial.getText();
            String id = H2DAO.getTrialID(trialName);
            if (tabActions.isSelected()) {
                procesedActionList.clear();
                goThroughTable("Actions");
                if (checkVariablesFormat(procesedActionList))
                {
                    if (checkWaitingForTime(procesedActionList))
                    {
                        H2DAO.deleteTrialActions(id);
                        H2DAO.saveTrial(procesedActionList, id, 0);
                        trialmodified = true;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Algun tiempo de los waiting for no es un entero");
                        alert.setContentText("Estas tonto o que? :)");
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(testList.getScene().getWindow());
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Alguna de las variables no tiene el formato adecuado");
                    alert.setContentText("Estas tonto o que? :)");
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(testList.getScene().getWindow());
                    alert.show();
                }

            }
            if (tabValidation.isSelected()) {
                procesedValidationList.clear();
                goThroughTable("Validations");
                if (checkVariablesFormat(procesedValidationList))
                {
                    if (checkWaitingForTime(procesedValidationList))
                    {
                        H2DAO.deleteTrialValidations(id);
                        H2DAO.saveTrial(procesedValidationList, id, 1);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Algun tiempo de los waiting for no es un entero");
                        alert.setContentText("Estas tonto o que? :)");
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(testList.getScene().getWindow());
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Alguna de las variables no tiene el formato adecuado");
                    alert.setContentText("Estas tonto o que? :)");
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(testList.getScene().getWindow());
                    alert.show();
                }

            }

        //}

        if (trialmodified) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cambios comfirmados");
            alert.setHeaderText("Cambios efectuados con éxito");
            alert.showAndWait();
        }
        // ALERTA
    }
    public void saveTest()
    {
        bottomButtons.setDisable(false);
        TextInputDialog dialog = new TextInputDialog("Prueba");
        dialog.setTitle("Nueva prueba");
        dialog.setHeaderText("");
        dialog.setContentText("Por favor introduzca el nombre de la prueba sin '_':");

        Optional<String> result = dialog.showAndWait();

        if (result.toString().contains("_"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El nombre de la prueba contiene '_'");
            alert.setContentText("Contacta con tu administrador :)");
            alert.show();
        } else if (!checkTrialName(result.toString()))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nombre de trial repetido");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();
        }else {
            if (result.isPresent()) {
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


                if (trialmodified) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Test Importado");
                    alert.setHeaderText("Test importado correctamente");
                    alert.showAndWait();
                }
            } else {
                bottomButtons.setDisable(true);
            }
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
            if (unique)
            {
                textFieldSecondValueArgs = Utils.generateUniqueID(textFieldSecondValueArgs);
                unique = false;
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

    private boolean checkVariablesFormat(List<Action> actions)
    {
        boolean formatOk = true;
        for (Action action : actions)
        {
            if (action.getActionTypeS().equals("ReadFrom"))
            {
                if (!action.getSecondValueArgsS().equals("")) {
                    if (!action.getSecondValueArgsS().matches(String.valueOf(VARIABLE_PATTERN))) {
                        formatOk = false;
                    }
                }
            }
        }

        return formatOk;
    }

    private boolean checkWaitingForTime(List<Action> actions)
    {
        boolean timeOk = true;
        for (Action action : actions)
        {
            if (action.getActionTypeS().equals("Waiting For"))
            {
                try{
                    if (Double.isNaN(Integer.parseInt(action.getSecondValueArgsS())))
                    {
                        timeOk = false;
                    }
                }catch (NumberFormatException e){
                    return false;
                }
            }
        }
        return timeOk;
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
           ArrayList<Action> trialActions = H2DAO.getTrialActions(trialName);
           for(Action actionOfTrial : trialActions)
           {
               ActionController actionController = new ActionController();
               actionController.setAction(gridPaneTrialList, actionsRowIndex,actionOfTrial.getActionTypeS(),actionOfTrial.getSelectElementByS(),actionOfTrial.getFirstValueArgsS(),
                       actionOfTrial.getSelectPlaceByS(), actionOfTrial.getSecondValueArgsS());
               //System.out.println(actionOfTrial.toString());
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
            ArrayList<Action> trialActions = H2DAO.getTrialValidations(trialName);
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
        ArrayList<Thread> threads = new ArrayList<>();
        int times = Integer.parseInt(spinner.getValue().toString());
        for(CheckBox trial : testList.getItems())
        {
            if(trial.isSelected())
            {
                String trialName = trial.getText();
                ArrayList<Action> actions = H2DAO.getTrialActions(trial.getText());
                for (int i = 0; i < times; i++)
                {
                    //Thread thread = new Thread(() -> );
                    //threads.add(thread);
                    //thread.start();
                    executeTest(actions, trialName);
                }
                /*for (Thread thread : threads){
                    thread.start();
                }*/
                //ExecutorService service = Executors.newCachedThreadPool();
                //service.invokeAll(threads);
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
                   H2DAO.deleteTrialVariables(id);
                   H2DAO.deleteTrial(id);
               }
           }
           poblateTestList();
    }

    public void modifyTrialName()
    {
        CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();

        if (selectedTrial == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Debe haber un test seleccionado");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();

        }else {

            TextInputDialog dialog = new TextInputDialog("Prueba");
            dialog.setTitle("Modificando nombre de la prueba");
            dialog.setHeaderText("");
            dialog.setContentText("Por favor introduzca el nuevo nombre de la prueba sin '_':");
            Optional<String> result = dialog.showAndWait();

            if (result.toString().contains("_"))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("El nombre de la prueba contiene '_'");
                alert.setContentText("Contacta con tu administrador :)");
                alert.show();
            } else {

                if (result.isPresent()) {
                    if (!checkTrialName(result.get())) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Nombre de trial repetido");
                        alert.setContentText("Contacta con tu administrador :)");
                        alert.showAndWait();
                    } else {
                        H2DAO.updateTrialName(selectedTrial.getText(), result.get());
                        selectedTrial.setText(result.get());
                    }
                }
            }
        }
    }

    public void cloneTest()
    {
        CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();

        if (selectedTrial == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Debe haber un test seleccionado");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();

        }else {

            ArrayList<Action> actions = H2DAO.getTrialActions(selectedTrial.getText());
            ArrayList<Action> validations = H2DAO.getTrialValidations(selectedTrial.getText());
            ArrayList<Variable> variables = H2DAO.getTrialVariables(H2DAO.getTrialID(selectedTrial.getText()));

            String clonedName = selectedTrial.getText().concat(" clon");
            H2DAO.createTrial(clonedName);
            String clonedID = H2DAO.getTrialID(clonedName);
            H2DAO.saveTrial(actions, clonedID, 0);
            H2DAO.saveTrial(validations, clonedID, 1);
            H2DAO.saveTrialVariables(variables, clonedID);

            poblateTestList();
        }

    }

    public void copyActions()
    {
        copiedActionList.clear();
        copiedActions = 0;
        int rowToCopy = -1;

        GridPane gridPane = new GridPane();

        if (tabActions.isSelected())
        {
            gridPane = gridPaneTrialList;
        }
        if (tabValidation.isSelected())
        {
            gridPane = gridPaneValidationList;
        }

        for (Node hbox : gridPane.getChildren())
        {
            if (hbox instanceof  HBox){
                for (Node child : ((HBox) hbox).getChildren())
                {
                    if (child instanceof CheckBox && ((CheckBox)child).isSelected())
                    {
                        rowToCopy = gridPane.getRowIndex(child.getParent());
                        break;
                    }
                }
            }

            int i = 0;
            int j = 0;
            if (rowToCopy != -1)
            {
                if (gridPane.getRowIndex(hbox) == rowToCopy)
                {
                    if (hbox instanceof HBox){
                        for (Node hboxChild : ((HBox) hbox).getChildren())
                        {
                            if (hboxChild instanceof ComboBox && i == 0)
                            {
                                comboBoxActionType = ((ComboBox) hboxChild).getValue().toString();
                                i++;
                            } else if(hboxChild instanceof ComboBox && i == 1)
                            {
                                comboBoxSelectElementBy = ((ComboBox) hboxChild).getValue().toString();
                                i++;
                            } else if(hboxChild instanceof ComboBox && i == 2)
                            {
                                comboBoxSelectPlaceBy = ((ComboBox) hboxChild).getValue().toString();
                            }

                            if (hboxChild instanceof TextField && j == 0)
                            {
                                textFieldFirstValueArgs = ((TextField) hboxChild).getText();
                                j++;
                            } else if (hboxChild instanceof TextField && j ==1)
                            {
                                textFieldSecondValueArgs = ((TextField) hboxChild).getText();

                            }

                            if(hboxChild instanceof StackPane && j == 1)
                            {
                                for (Node stackChild : ((StackPane) hboxChild).getChildren())
                                {
                                    if (stackChild instanceof TextField)
                                    {
                                        textFieldSecondValueArgs = ((TextField) stackChild).getText();
                                    }
                                }

                            }
                        }
                    }
                    Action action = new Action(comboBoxActionType, comboBoxSelectElementBy, textFieldFirstValueArgs, comboBoxSelectPlaceBy, textFieldSecondValueArgs);
                    copiedActionList.add(action);
                    resetFields();
                }
            }
        }
    }

    public void pasteActions()
    {
        ActionController actionController = new ActionController();
        if (tabActions.isSelected())
        {
            for (Action action : copiedActionList)
            {

                actionController.setAction(gridPaneTrialList, actionsRowIndex, action.getActionTypeS(),
                        action.getSelectElementByS(), action.getFirstValueArgsS(),
                        action.getSelectPlaceByS(),action.getSecondValueArgsS());
                actionList.add(action);
                actionsRowIndex++;
            }
            Main.setModified(true);
        }

        if (tabValidation.isSelected())
        {
            for (Action action : copiedActionList)
            {

                actionController.setAction(gridPaneValidationList, validationRowIndex, action.getActionTypeS(),
                        action.getSelectElementByS(), action.getFirstValueArgsS(),
                        action.getSelectPlaceByS(),action.getSecondValueArgsS());
                validationList.add(action);
                validationRowIndex++;
            }
            Main.setModified(true);
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

    private void saveTrialToCSV(ArrayList<Action> actions, ArrayList<Action> validations, File file) throws IOException
    {
            Writer writer = null;
            try {
                //File file = new File("/home/david/git_docs/"+name+".csv");
                writer = new BufferedWriter(new FileWriter(file));

               //writer.write(trialName);
               //writer.write(System.lineSeparator());
                writer.write(trialColumnsHeadersCSV);

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

                System.out.println("TEST EXPORTADOS");

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                writer.flush();
                writer.close();
            }
    }

    private void saveGlobalVariablesToCSV(ArrayList<Global_Variable> global_variables, File file) throws IOException {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(globalVariableHeader);

            for (Global_Variable global_variable : global_variables)
            {
                writer.write(System.lineSeparator());
                writer.write("" + global_variable.getVariableName() + ","
                        + global_variable.getValue() + ","
                );
            }


            System.out.println("VARIABLES GLOBALES EXPORTADAS");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }


    public void exportTestJSON(ArrayList<Action> actions, ArrayList<Action> validations, ArrayList<Variable> variables, File file)
    {

        try {
            JSONObject jsonObject = new JSONObject();


            jsonObject.put("Validations", validations);

            jsonObject.put("Variables", variables);

            jsonObject.put("Actions", actions);


            System.out.println("TEST EXPORTADOS");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void exportGlobalVariablesJSON(ArrayList<Global_Variable> global_variables, File file)
    {
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("GlobalVariables", global_variables);
            System.out.println("Variables Globales exportadas");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), ISO_8859_1);
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.flush();
            /*FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();*/

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void saveVariablesToCSV(ArrayList<Variable> variables, File file) throws IOException
    {
        Writer writer = null;
        try {

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(variablesColumnsHeadersCSV);

            for (Variable variable : variables)
            {
                writer.write(System.lineSeparator());
                writer.write("" + variable.getVariableTrial() + ","
                        + variable.getVariableName() + ","
                        + variable.getValue());
            }

            System.out.println("VARIABLES EXPORTADAS");
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }

    }


    public void exportTrial()
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
            DirectoryChooser dirChooser = new DirectoryChooser();
            File defaultOrigin = new File(System.getProperty("user.home"));
            dirChooser.setInitialDirectory(defaultOrigin);
            File folder = dirChooser.showDialog(stageSettings);


            //File variablesile = new File(folder.getAbsolutePath() + "nombrenuevoFIle1");
            //File pruebas;

            try {

                if (folder != null) {
                    for (CheckBox trial : testList.getItems()) {
                        if (trial.isSelected()) {

                            String trialID = H2DAO.getTrialID(trial.getText());

                            File trialFile = new File(folder.getAbsolutePath().concat("/"+trial.getText()+"_"+trialID)+".csv");
                            File trialJSONFile = new File(folder.getAbsolutePath().concat("/"+trial.getText()+"_"+trialID)+".json");
                            File variablesFile = new File(folder.getAbsolutePath().concat("/"+trial.getText()+"_"+"Variables"+"_"+trialID+".csv"));

                            ArrayList<Action> actions = H2DAO.getTrialActions(trial.getText());
                            ArrayList<Action> validations = H2DAO.getTrialValidations(trial.getText());
                            ArrayList<Variable> variables = H2DAO.getTrialVariables(H2DAO.getTrialID(trial.getText()));

                            saveTrialToCSV(actions, validations, trialFile);
                            saveVariablesToCSV(variables,variablesFile);
                            exportTestJSON(actions, validations, variables, trialJSONFile);

                        }
                        // Aviso de ninguno seleccionado
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    public void exportGlobalVariables()
    {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File defaultOrigin = new File(System.getProperty("user.home"));
        dirChooser.setInitialDirectory(defaultOrigin);
        File folder = dirChooser.showDialog(stageSettings);


        try {

            if (folder != null) {

                File globalVariableCSV = new File(folder.getAbsolutePath().concat("/globalVariables.csv"));
                File globalVariablesJSON = new File(folder.getAbsolutePath().concat("/globalVariables.json"));

                ArrayList<Global_Variable> global_variables = H2DAO.getGlobalVariables();

                saveGlobalVariablesToCSV(global_variables, globalVariableCSV);
                exportGlobalVariablesJSON(global_variables, globalVariablesJSON);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importTrial()
    {
        deleteAllTabs();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        List<File> files = fileChooser.showOpenMultipleDialog(stageSettings);

        if (files != null){

            for (File file : files)
            {
                String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                if (fileExtension.equals("json"))
                {
                    importJSONTrial(file);
                }
                if (fileExtension.equals("csv"))
                {
                    importCSVTrial(file);
                }
            }
        } else {
            return;
        }
    }

    public void importCSVTrial(File file)
    {
        String header = "";
        boolean firstRead = true;
        String lastTrialVariable = "";

        ArrayList<String> failedVariables = new ArrayList<>();

        try {
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                String data = inputStream.nextLine();
                if (data.equals(trialColumnsHeadersCSV))
                {
                    header = "Trial";
                }
                if (data.equals(variablesColumnsHeadersCSV))
                {
                    header = "Variables";
                    continue;
                }
                if (header.equals("Trial"))
                {

                    String[] values = data.split(",");
                    if (values[5].equals("false")) {
                        ActionController actionController = new ActionController();
                        actionController.setAction(gridPaneTrialList, actionsRowIndex, values[0], values[1], values[2], values[3], values[4]);
                        Action act = new Action( values[0], values[1], values[2], values[3], values[4]);
                        actionList.add(act);
                        actionsRowIndex++;
                    }
                    if (values[5].equals("true")) {
                        ActionController actionController = new ActionController();
                        actionController.setAction(gridPaneValidationList, validationRowIndex, values[0], values[1], values[2], values[3], values[4]);
                        Action act = new Action(values[0], values[1], values[2], values[3], values[4]);
                        validationList.add(act);
                        validationRowIndex++;
                    }
                }


                if (header.equals("Variables"))
                {
                    String[] values = data.split(",");

                    if (firstRead && !values[0].equals("TrialID")){

                        Variable variable = new Variable(values[0], values[1], values[2]);
                        checkVariables(failedVariables, variable);

                        lastTrialVariable = values[0];
                        firstRead = false;
                    } else {
                        if (values[0].equals(lastTrialVariable))
                        {
                            Variable variable = new Variable(values[0], values[1], values[2]);
                            checkVariables(failedVariables, variable);

                            lastTrialVariable = values[0];
                        } else {
                            H2DAO.saveTrialVariables(variablesList, lastTrialVariable);
                            variablesList.clear();

                            Variable variable = new Variable(values[0], values[1], values[2]);
                            checkVariables(failedVariables, variable);

                            lastTrialVariable = values[0];
                        }
                    }
                }
            }
            if(header.equals("Trial"))
            {
                if (checkVariablesFormat(actionList) && checkVariablesFormat(validationList))
                {
                    saveTest();
                    poblateTestList();
                } else {
                    // Aviso formato de variables
                    deleteAllTabs();
                }
                inputStream.close();
            }else if (header.equals("Variables"))
            {
                //H2DAO.saveTrialVariables(variablesList,lastTrialVariable);
                if (failedVariables.size() > 0)
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText("Se ha producido un error durante la importación de las variables");
                    String error = "";
                    for (String failedVariable : failedVariables)
                    {
                        error = error.concat(failedVariable+"\n");
                    }
                    alert.setContentText(error);
                    alert.showAndWait();
                }
                variablesList.clear();
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Se ha producido un error durante la importación del test");
                alert.setContentText("El fichero no contienen las columnas correctas");
                alert.showAndWait();
            }

        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
    
    public void importGlobalVariables()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        List<File> files = fileChooser.showOpenMultipleDialog(stageSettings);

        if (files != null){

            for (File file : files)
            {
                String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                if (fileExtension.equals("json"))
                {
                    importJSONGlobalVariables(file);
                }
                if (fileExtension.equals("csv"))
                {
                    importCSVGlobalVariables(file);
                }
            }
        } else {
            return;
        }   
    }

    private void importCSVGlobalVariables(File file)
    {
        ArrayList<String> failedVariables = new ArrayList<>();
        boolean header = false;

        try
        {
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext())
            {
                String data = inputStream.nextLine();

                if (data.equals(globalVariableHeader))
                {
                    header = true;
                    continue;
                }

                if (header)
                {
                    String[] values = data.split(",");

                    Global_Variable global_variable = new Global_Variable(values[0], values[1]);
                    checkGlobalVariables(failedVariables, global_variable);
                }
            }
            if (failedVariables.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Se ha producido un error durante la importación de las variables");
                String error = "";
                for (String failedVariable : failedVariables) {
                    error = error.concat(failedVariable + "\n");
                }
                alert.setContentText(error);
                alert.showAndWait();
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    private void importJSONGlobalVariables(File file)
    {
        JSONParser parser = new JSONParser();
        ArrayList<String> failedVariables = new ArrayList<>();

        try
        {
            FileReader reader = new FileReader(file);
            Object object = parser.parse(reader);


            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) object;


            org.json.simple.JSONArray globalVariables = (org.json.simple.JSONArray) jsonObject.get("GlobalVariables");

            for (int i = 0; i < globalVariables.size(); i++)
            {
                org.json.simple.JSONObject globalVariable = (org.json.simple.JSONObject) globalVariables.get(i);
                Global_Variable var = new Global_Variable(globalVariable.get("variableName").toString(), globalVariable.get("value").toString());
                checkGlobalVariables(failedVariables, var);
            }

            if (failedVariables.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Se ha producido un error durante la importación de las variables");
                String error = "";
                for (String failedVariable : failedVariables) {
                    error = error.concat(failedVariable + "\n");
                }
                alert.setContentText(error);
                alert.showAndWait();
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkVariables(ArrayList<String> failedVariables, Variable variable)
    {

        if (variable.getVariableName().matches(String.valueOf(VARIABLE_PATTERN)))
        {
            if (H2DAO.trialExist(variable.getVariableTrial())){

                if (!H2DAO.variableExists(variable)) {

                    H2DAO.saveVariable(variable);
                } else {
                    failedVariables.add("Variable " + variable.getVariableName() + " Fallo: Variable existente");
                }
            }else {
                failedVariables.add("Variable " + variable.getVariableName() + " Fallo: No existe trial");
            }
        } else {
            failedVariables.add("Variable " + variable.getVariableName() + " Fallo: Formato");
        }
    }

    private void checkGlobalVariables(ArrayList<String> failedVariables, Global_Variable global_variable)
    {
        if (global_variable.getVariableName().matches(String.valueOf(GLOBAL_VARIABLE_PATTERN)))
        {
            if (!H2DAO.globalVariableExists(global_variable))
            {
                H2DAO.saveGlobalVariable(global_variable);
            }else {
                failedVariables.add("Variable " + global_variable.getVariableName() + " Fallo: Variable existente");
            }
        }else {
            failedVariables.add("Variable " + global_variable.getVariableName() + " Fallo: Formato");
        }
    }

    public void importJSONTrial(File file)
    {

        JSONParser parser = new JSONParser();
        ArrayList<String> failedVariables = new ArrayList<>();

        try {
            FileReader reader = new FileReader(file);
            Object object = parser.parse(reader);


            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) object;


            org.json.simple.JSONArray actions = (org.json.simple.JSONArray) jsonObject.get("Actions");
            org.json.simple.JSONArray validations = (org.json.simple.JSONArray) jsonObject.get("Validations");
            org.json.simple.JSONArray variables = (org.json.simple.JSONArray) jsonObject.get("Variables");


            for (int i = 0; i < actions.size(); i++)
            {
                org.json.simple.JSONObject action = (org.json.simple.JSONObject) actions.get(i);
                ActionController actionController = new ActionController();
                actionController.setAction(gridPaneTrialList, actionsRowIndex, action.get("actionTypeS").toString(), action.get("selectElementByS").toString(),
                        action.get("firstValueArgsS").toString(), action.get("selectPlaceByS").toString(), action.get("secondValueArgsS").toString());
                Action act = new Action(action.get("actionTypeS").toString(), action.get("selectElementByS").toString(),
                        action.get("firstValueArgsS").toString(), action.get("selectPlaceByS").toString(), action.get("secondValueArgsS").toString());
                actionList.add(act);
                actionsRowIndex++;

            }

            for (int i = 0; i < validations.size(); i++)
            {
                org.json.simple.JSONObject validation = (org.json.simple.JSONObject) validations.get(i);
                ActionController actionController = new ActionController();
                actionController.setAction(gridPaneValidationList, validationRowIndex, validation.get("actionTypeS").toString(), validation.get("selectElementByS").toString(),
                        validation.get("firstValueArgsS").toString(), validation.get("selectPlaceByS").toString(), validation.get("secondValueArgsS").toString());
                Action act = new Action(validation.get("actionTypeS").toString(), validation.get("selectElementByS").toString(),
                        validation.get("firstValueArgsS").toString(), validation.get("selectPlaceByS").toString(), validation.get("secondValueArgsS").toString());
                validationList.add(act);
                validationRowIndex++;

            }


            for (int i = 0; i < variables.size(); i++)
            {
                org.json.simple.JSONObject variable = (org.json.simple.JSONObject) variables.get(i);
                Variable var = new Variable(variable.get("variableTrial").toString(), variable.get("variableName").toString(), variable.get("value").toString());
                checkVariables(failedVariables, var);

            }

            if (checkVariablesFormat(actionList) && checkVariablesFormat(validationList) && failedVariables.size() == 0)
            {
                saveTest();
                poblateTestList();

                System.out.println("JSON IMPORTADO");
            } else {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Se ha producido un error durante la importación de las variables");
                //alert.initOwner(buttonSave.getScene().getWindow());
                String error = "";
                for (String failedVariable : failedVariables)
                {
                    error = error.concat(failedVariable+"\n");
                }
                alert.setContentText(error);
                alert.showAndWait();

                // Aviso formato de variables
                deleteAllTabs();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static void reorderIndex(GridPane gridPane)
    {
        for (Node child : gridPane.getChildren())
        {
            if (child instanceof HBox)
            {
                for (Node hboxChild : ((HBox) child).getChildren())
                {
                    if (hboxChild instanceof Label) {
                        ((Label) hboxChild).setText("# "+ gridPane.getRowIndex(child));
                        break;
                    }
                }
            }
        }
    }

    public void dragAndDrop()
    {
        for (CheckBox child : testList.getItems()) //Poblate test List
        {
            child.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    rowIndexDrop = -1;
                    rowIndexDrag = 0;
                    draggedChildList.clear();
                    movedChilds.clear();

                    Dragboard db = child.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(checkBoxFormat, " ");
                    db.setContent(content);


                    rowIndexDrag = testList.getItems().indexOf(event.getPickResult().getIntersectedNode().getParent());

                    System.out.println("RowIndexDrag =  "+rowIndexDrag);


                    if (rowIndexDrag == -1){
                        event.consume();
                    }else {
                        for (CheckBox child : testList.getItems())
                        {
                            if (testList.getItems().indexOf(child) == rowIndexDrag)
                            {
                                draggedChildList.add(child);
                            }
                        }
                    }


                    event.consume();

                }
            });

            child.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    if (event.getPickResult().getIntersectedNode() != null){
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }

                    event.consume();
                }
            });


            child.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {

                    rowIndexDrop = testList.getItems().indexOf(event.getPickResult().getIntersectedNode().getParent());


                    System.out.println("DragIndex = "+ rowIndexDrag);
                    System.out.println("DropIndex = "+rowIndexDrop);


                    System.out.println(draggedChildList.toString());


                    if  (rowIndexDrop != null && rowIndexDrag != null) {


                        //System.out.println(testList.getItems().get(rowIndexDrag).toString());
                        //testList.getItems().remove(rowIndexDrag); // Te cargas uno de mas



                        if (rowIndexDrag >= 0 || rowIndexDrag < testList.getItems().size())                    // Si es la cabeza o medio...
                        {
                            for (CheckBox child : testList.getItems()){                                        // Reducir el rowIndex de las que estan por debajo de la seleccionada -1
                                //System.out.println(testList.getItems().indexOf(child));
                                if (testList.getItems().indexOf(child) > rowIndexDrag)
                                {
                                    //System.out.println(testList.getItems().indexOf(child));
                                    testList.getItems().set(testList.getItems().indexOf(child)-1, child);
                                    //System.out.println(testList.getItems().indexOf(child));
                                }
                            }
                        }

                        testList.getItems().removeAll(draggedChildList);
                        testList.getItems().remove(testList.getItems().size()-1);


                                                                                       // Insertar en cabeza o medio
                        for (CheckBox child : testList.getItems()) {
                            //System.out.println("Indice del checkbox = "+testList.getItems().indexOf(child));

                            if (testList.getItems().indexOf(child) >= rowIndexDrop)
                            {
                                //testList.getItems().set(testList.getItems().indexOf(child)+1, child);
                                movedChilds.add(child);
                                //testList.getItems().remove(child);
                            }
                        }
                        testList.getItems().removeAll(movedChilds);

                        testList.getItems().add(rowIndexDrop, draggedChildList.get(0));

                    } else{
                        event.consume();
                    }

                    System.out.println("Tomaaa con to mi node " + rowIndexDrop);

                    event.consume();
                }
            });

            child.setOnDragDone(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {

                    if (event.getTransferMode() == TransferMode.MOVE)
                    {
                        testList.getItems().addAll(movedChilds);
                    }

                    event.consume();
                }
            });
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
