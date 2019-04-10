package gui;


import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
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
    private  ListView<CheckBox> testList;

    @FXML
    private GridPane gridPaneTrialList;

    @FXML
    private ScrollPane scrollPaneTrialList;

    private List<Action> actionList;
    private List<Action> procesedList;

    private int rowIndex = 0;

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
        procesedList = new ArrayList<>();

        // My try to get the ListView expanded to fit parent AnchorPane
        //testList.setScaleX(100);
        //testList.setScaleY(100);
        //testList.prefWidthProperty().bind(scrollPaneTrialList.widthProperty());
        testList.prefHeightProperty().bind(scrollPaneTrialList.heightProperty());

        poblateTestList();

    }

    public void addActionRow()
    {
            Action newAction = new Action(gridPaneTrialList,rowIndex);
            actionList.add(newAction);
            rowIndex++;
    }

    public void deleteActionRow()
    {
         List<Node> deleteNodes = new ArrayList<>();
         for (Node child : gridPaneTrialList.getChildren()) {
             if (gridPaneTrialList.getRowIndex(child) == rowIndex - 1) {
                 deleteNodes.add(child);
             }
         }
         gridPaneTrialList.getChildren().removeAll(deleteNodes);
         if(rowIndex>0)
         {
             rowIndex--;
         }
    }

    public void deleteAll()
    {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Confirmar Eliminación");
       alert.setHeaderText("Se perderán todos los datos");

       Optional<ButtonType> result = alert.showAndWait();
       if (result.get() == ButtonType.OK)
       {
           gridPaneTrialList.getChildren().remove(0, gridPaneTrialList.getChildren().size());
           rowIndex = 0;
       } else {
           // ... user chose CANCEL or closed the dialog
           //getSelectedTrials();
           runSelectedTrials();
       }
   }
   public void processTable()
   {
        procesedList.clear();
        goThroughTable();
        executeTest(procesedList);
   }

   public void executeTest(List<Action> actionList)
   {
        WebDriver driver = SeleniumDAO.initializeDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        for(int i = 0; i < actionList.size(); i++)
        {
            Action currentAction = actionList.get(i);
            currentAction.executeTrial(driver);
        }
   }

   public void executeTestHeadless(){
       WebDriver driver = SeleniumDAO.initializeHeadLessDriver();
       driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
       for(int i = 0; i < actionList.size(); i++)
       {
           Action currentAction = actionList.get(i);
           currentAction.executeTrial(driver);
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
        boolean ok = false;
        try
        {
            //executeTestHeadless();  // Comprobaciones
             ok = true;
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
            if(ok)
            {
                TextInputDialog dialog = new TextInputDialog("dialtest");
                dialog.setTitle("Guau! ¿Estás guardando ya?");
                dialog.setHeaderText("Guardando la prueba");
                dialog.setContentText("Por favor introduzca el nombre de la prueba:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()){
                    H2DAO.createTrial(result.get());                                                // Introducir nuevo trial con su nombre en trials
                    String id =  H2DAO.getTrialId(result.get());                                    // Obtener id del nuevo trial
                    procesedList.clear();                                                           // Limpiar lista con las acciones de la tabla
                    goThroughTable();                                                               // Recorrer la tabla e introduce en procesedList las acciones
                    H2DAO.saveTrial(procesedList, id);                                              // Insertar todas las acciones referentes al nuevo test en la tabla trials_actions

                }
            }

        }
    }

    public void goThroughTable(){
        int iterator = 0;
        while(iterator<rowIndex)
        {
            int i = 0;
            int j = 0;
            for(Node child : gridPaneTrialList.getChildren())
            {
                if (gridPaneTrialList.getRowIndex(child) == iterator)
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
            procesedList.add(currentAction);
            iterator++;
            i = 0;
            j = 0;
        }
    }

    public void poblateTestList()
    {
        ObservableList<CheckBox> checkBoxesList = FXCollections.observableArrayList();
        for (String trial: H2DAO.getTrials())
        {
            checkBoxesList.add(new CheckBox(trial));
        }
        testList.getItems().addAll(checkBoxesList);
    }

    public void getSelectedTrials()
    {
       ObservableList<CheckBox> trialsSelected = testList.getSelectionModel().getSelectedItems();

       for(CheckBox trial: trialsSelected)
       {
           String trialName = trial.getText();
           ArrayList<Action> trialActions = H2DAO.getActions(trialName);
           for(Action action : trialActions)
           {
               actionList.add(action);
               rowIndex++;
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
}
