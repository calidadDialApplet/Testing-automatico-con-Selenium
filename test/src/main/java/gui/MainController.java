package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

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
    private  ListView<CheckBox> testList;

    @FXML
    private GridPane gridPaneTrialList;

    private boolean firstTime;
    private boolean firstTimeDragAndDrop;

    private List<Trial> trialList;
    private List<Trial> procesedList;

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
        trialList = new ArrayList<>();
        procesedList = new ArrayList<>();
        // My try to get the ListView expanded to fit parent AnchorPane
        testList.setScaleX(100);
        testList.setScaleY(100);
    }

    public void addActionRow()
    {
            Trial newaction = new Trial(gridPaneTrialList,rowIndex);
            trialList.add(newaction);
            rowIndex++;
    }

   public void deleteActionRow()
   {
       List<Node> deleteNodes = new ArrayList<>();
       for (Node child : gridPaneTrialList.getChildren())
       {
           if(gridPaneTrialList.getRowIndex(child) == rowIndex-1)
           {
               deleteNodes.add(child);
           }
       }
       gridPaneTrialList.getChildren().removeAll(deleteNodes);
       rowIndex--;
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
       }
   }
    public void processTable()
    {
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
             Trial currentAction = new Trial(comboBoxActionType,comboBoxSelectElementBy,textFieldFirstValueArgs,comboBoxSelectPlaceBy,textFieldSecondValueArgs);
             procesedList.add(currentAction);
             iterator++;
             i = 0;
             j = 0;
        }
            executeTest(procesedList);
    }

    public void executeTest(List<Trial> procesedTable)
    {
        WebDriver driver = SeleniumDAO.initializeDriver();
        driver.get("http://pruebas7.dialcata.com/dialapplet-web/");
        for(int i = 0; i < procesedTable.size(); i++)
        {
            Trial currentAction = procesedTable.get(i);
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
}
