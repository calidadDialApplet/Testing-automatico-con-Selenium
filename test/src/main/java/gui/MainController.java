package gui;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import main.SeleniumDAO;
import org.openqa.selenium.WebDriver;


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
    private ScrollPane scrollPaneTrialList;

    @FXML
    private AnchorPane anchorPaneTrial;

    @FXML
    private TabPane tabPaneParent;

    @FXML
    private Accordion accordionComprobationList;

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

        //tabPaneParent.setMinSize(1100,500);
        //tabPaneParent.setTabMinWidth(200);
        // My try to get the ListView expanded to fit parent AnchorPane
        //testList.setScaleX(100);
        //testList.setScaleY(100);
        //testList.prefWidthProperty().bind(scrollPaneTrialList.widthProperty());
        scrollPaneTrialList.setFitToWidth(true);
        //testList.prefHeightProperty().bind(scrollPaneTrialList.heightProperty());

        //tabPaneParent.setTabMinWidth(Math.round(tabPaneParent.getWidth()/2));

        if(!H2DAO.checkDB()){
            H2DAO.redoTables();
        }
        testList.getSelectionModel().selectedItemProperty().addListener((observableSelect, oldValueSelect, newValueSelect) ->
        {
            deleteAllTabs();
            getSelectedTrialActions();
            getSelectedTrialValidations();
        });

        poblateTestList();

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

   public void executeTest(List<Action> actionList)
   {
       WebDriver driver = SeleniumDAO.initializeDriver();
       driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

       TitledPane trial = new TitledPane();
       CheckBox selectedTrial = testList.getSelectionModel().getSelectedItem();
       trial.setText(""+selectedTrial.getText());
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
           for (int i = 0; i < validationList.size(); i++) {
               Action currentValidation = validationList.get(i);
               grid.add(new Label("Validation " + i + ":"), 0, i);
               grid.add(new Label(" " + currentValidation.executeAction(driver)), 1, i);
           }
       }
       trial.setContent(grid);
       accordionComprobationList.getPanes().add(trial);
   }

   public void executeTestHeadless(){
       WebDriver driver = SeleniumDAO.initializeHeadLessDriver();
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
                            String id =  H2DAO.getTrialId(result.get());                                    // Obtener id del nuevo trial
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
       String id = H2DAO.getTrialId(trialName);
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

    public void goThroughTable(String table){

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
            for(Action validationOfTrial : trialActions)
            {
                Action action = new Action(gridPaneValidationList, validationRowIndex,validationOfTrial.getActionTypeString(),validationOfTrial.getSelectElementByString(),
                        validationOfTrial.getFirstValueArgsString(),validationOfTrial.getSelectPlaceByString(),validationOfTrial.getSecondValueArgsString());
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
           String id = H2DAO.getTrialId(selectedTrial.getText());
           H2DAO.deleteTrialActions(id);
           H2DAO.deleteTrial(id);
           poblateTestList();
       }
    }

}
