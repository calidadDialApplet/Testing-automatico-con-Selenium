package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    private ScrollPane scrollPaneActionslList;

    @FXML
    private ScrollPane scrollPaneValidationsList;

    @FXML
    private AnchorPane anchorPaneTrial;

    @FXML
    private TabPane tabPaneParent;

    @FXML
    private Accordion accordionComprobationList;

    @FXML
    private MenuItem menuItemSettings;

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
            deleteAllTabs();
            getSelectedTrialActions();
            getSelectedTrialValidations();
        });

        poblateTestList();

    }

    public void openSettingsDialog()
    {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Settings.fxml"));
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.setTitle("Settings");
            Scene scene = new Scene(root,350,250);
            scene.getStylesheets().add("/css/darcula.css");
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addActionRow()
    {
        if(tabActions.isSelected())
        {
            Action newAction = new Action(gridPaneTrialList, actionsRowIndex);
            actionList.add(newAction);
            actionsRowIndex++;
        }
        if(tabValidation.isSelected())
        {
            Action newAction = new Action(gridPaneValidationList, validationRowIndex);
            validationList.add(newAction);
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
            List<Node> deleteNodes = new ArrayList<>();
            for (Node child : gridPaneTrialList.getChildren()) {
                if (gridPaneTrialList.getRowIndex(child) == actionsRowIndex - 1) {
                    deleteNodes.add(child);
                }
            }
            gridPaneTrialList.getChildren().removeAll(deleteNodes);
            if (actionsRowIndex > 0) {
                actionsRowIndex--;
            }
        }
        if(tabValidation.isSelected())
        {
            List<Node> deleteNodes = new ArrayList<>();
            for (Node child : gridPaneValidationList.getChildren()) {
                if (gridPaneValidationList.getRowIndex(child) == validationRowIndex - 1) {
                    deleteNodes.add(child);
                }
            }
            gridPaneValidationList.getChildren().removeAll(deleteNodes);
            if (validationRowIndex > 0) {
                validationRowIndex--;
            }
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
           executeTest(procesedActionList);
       }
       if (tabValidation.isSelected())
       {
           procesedValidationList.clear();
           goThroughTable("Validations");
           executeTest(procesedValidationList);
       }
   }
    // TODO: Tasks and tests cant be launched from MainController thread
    //  Use Task or Platform.runLater to achieve this, and get this code concurrent
    //  with protected methods, working out the logic part out of this
   public void executeTest(List<Action> actionList)
   {
       WebDriver driver = getWebDriver();
       // TODO: Variable
       //driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
       driver.get(H2DAO.getWeb());
       TitledPane trial = new TitledPane();

       // TODO: This checkbox has no value. You create UI object to store values.
       //  Plain wrong
       //CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();

       String selectedTrial = testList.getSelectionModel().getSelectedItem().getText();
       // TODO: selectedTrial does not work properly
       //  If you check without selecting row a Trial from checkables ListView,
       //  after execution always says this, instead of Trial name
       if (selectedTrial == null){
            trial.setText("Prueba sin guardar");
       }else{
            trial.setText(selectedTrial);
       }
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

    public void saveTest()
    {
        try
        {
            //executeTestHeadless();  // Comprobaciones

        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Se ha producido un error durante la comprobación del test");
            alert.setContentText("Contacta con tu administrador :)");
            alert.showAndWait();

            e.printStackTrace();
        }
        finally
        {

                TextInputDialog dialog = new TextInputDialog("dialtest");
                dialog.setTitle("Guau! ¿Estás guardando ya?");
                dialog.setHeaderText("Guardando la prueba");
                dialog.setContentText("Por favor introduzca el nombre de la prueba:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()){
                        procesedActionList.clear();                                                     // Limpiar lista con las acciones de la tabla
                        goThroughTable("Actions");                                                      // Recorrer la tabla e introduce en procesedActionList las acciones
                        if(!procesedActionList.isEmpty()) {
                            H2DAO.createTrial(result.get());                                                // Introducir nuevo trial con su nombre en trials
                            String id =  H2DAO.getTrialID(result.get());                                    // Obtener id del nuevo trial
                            H2DAO.saveTrial(procesedActionList, id, 0);                            // Insertar todas las acciones referentes al nuevo test en la tabla trials_actions
                            procesedValidationList.clear();                                                 // Limpiar lista con las valideaciones de la tabla
                            goThroughTable("Validations");                                                  // Recorrer la tabla e introduce en procesedValidationList las validaciones
                            H2DAO.saveTrial(procesedValidationList, id, 1);                        // Insertar todas las validaciones referentes al nuevo test en la tabla trials_actions
                            poblateTestList();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error");
                            alert.setHeaderText("Debe de haber una acción asociada al test");
                            alert.setContentText("Contacta con tu administrador :)");
                            alert.showAndWait();
                        }

                }


        }
    }

    public void modifyTrial()
    {
       String trialName = testList.getSelectionModel().getSelectedItem().getText();
       String id = H2DAO.getTrialID(trialName);
       if (tabActions.isSelected())
       {
           H2DAO.deleteTrialActions(id);
           procesedActionList.clear();
           goThroughTable("Actions");
           H2DAO.saveTrial(procesedActionList, id,0);
       }
       if (tabValidation.isSelected())
       {
           H2DAO.deleteTrialValidations(id);
           procesedValidationList.clear();
           goThroughTable("Validations");
           H2DAO.saveTrial(procesedValidationList, id,1);
       }
    }

    public void goThroughTable(String table)
    {

        int iterator = 0;
        int rowIndex = 0;
        GridPane gridPane = new GridPane();
        List<Action> list = new ArrayList<>();

        if(table.equals("Actions")){
            rowIndex = actionsRowIndex;
            gridPane = gridPaneTrialList;
            list = procesedActionList;
        }

        if(table.equals("Validations"))
        {
            rowIndex = validationRowIndex;
            gridPane = gridPaneValidationList;
            list = procesedValidationList;
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
            if (table.equals("Actions")) {
                procesedActionList.add(currentAction);
            }
            if (table.equals("Validations"))  {
                procesedValidationList.add(currentAction);
            }
            iterator++;

        }
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
               Action action = new Action(gridPaneTrialList, actionsRowIndex,actionOfTrial.getActionTypeString(),actionOfTrial.getSelectElementByString(),
                                            actionOfTrial.getFirstValueArgsString(),actionOfTrial.getSelectPlaceByString(),actionOfTrial.getSecondValueArgsString());
               actionList.add(action);
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
                Action action = new Action(gridPaneValidationList, validationRowIndex,validation.getActionTypeString(),validation.getSelectElementByString(),
                        validation.getFirstValueArgsString(),validation.getSelectPlaceByString(),validation.getSecondValueArgsString());
                validationList.add(action);
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
                ArrayList<Action> actions = H2DAO.getActions(trial.getText());
                executeTest(actions);
            }
        }
    }

    public void deleteSelectedTrial()
    {
       CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
       if(selectedTrial == null)
       {
           Alert alert = new Alert(Alert.AlertType.WARNING);
           alert.setTitle("Error");
           alert.setHeaderText("No hay ningún test seleccionado");
           alert.setContentText("Contacta con tu administrador :)");
           alert.showAndWait();
       } else {
           String id = H2DAO.getTrialID(selectedTrial.getText());
           H2DAO.deleteTrialActions(id);
           H2DAO.deleteTrial(id);
           poblateTestList();
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

    private void saveToCSV(ArrayList<Action> actions,ArrayList<Action> validations,String trialName) throws IOException {
            Writer writer = null;
            try {
                File file = new File("/home/david/git_docs/Table.csv");
                writer = new BufferedWriter(new FileWriter(file));

               //writer.write(trialName);
               //writer.write(System.lineSeparator());
                // writer.write("Action,FirstSelectBy,FirstValue,SecondSelectBy,SecondValue");

                for (Action action : actions)
                {
                    writer.write(System.lineSeparator());
                    writer.write("" + action.getActionTypeString() + ","
                            + action.getSelectElementByString() + ","
                            + action.getFirstValueArgsString() + ","
                            + action.getSelectPlaceByString() + ","
                            + action.getSecondValueArgsString());
                }
                for (Action validation : validations)
                {
                    writer.write(System.lineSeparator());
                    writer.write("" + validation.getActionTypeString() + ","
                            + validation.getSelectElementByString() + ","
                            + validation.getFirstValueArgsString() + ","
                            + validation.getSelectPlaceByString() + ","
                            + validation.getSecondValueArgsString());
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
        for(CheckBox trial : testList.getItems())
        {
            if(trial.isSelected())
            {
                ArrayList<Action> actions = H2DAO.getActions(trial.getText());
                ArrayList<Action> validations = H2DAO.getValidations(trial.getText());
                try {
                    saveToCSV(actions,validations,trial.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importTest()
    {
        String fileName = "/home/david/git_docs/Table.csv"; // Make dialog to ask file
        File file = new File(fileName);
        try {
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext())
            {
                String data = inputStream.next();
                //String action = data.substring()
                String[] values = data.split(",");
                //System.out.println(data);
                Action act = new Action(gridPaneTrialList, actionsRowIndex, values[0], values[1], values[2], values[3], values[4]);
                actionList.add(act);
                actionsRowIndex++;
            }
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
