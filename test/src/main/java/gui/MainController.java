package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.layout.GridPane;


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

    private int rowIndex = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //tableColumnTestCol.setCellValueFactory( (param) -> new SimpleStringProperty( param.getValue().toString()));
        trialList = new ArrayList<>();
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
        String comboBoxActionType = "";
        String comboBoxSelectElementBy = "";
        String textFieldFirstValueArgs = "";
        String comboBoxSelectPlaceBy = "";
        String textFieldSecondValueArgs = "";
        /*
        List<String> comboBoxList = new ArrayList<>();
        List<String> textFieldList = new ArrayList<>();

        textFieldList.add(textFieldFirstValueArgs);
        textFieldList.add(textFieldSecondValueArgs);

        comboBoxList.add(comboBoxActionType);
        comboBoxList.add(comboBoxSelectElementBy);
        comboBoxList.add(comboBoxSelectPlaceBy);
        */
        List<Node> rowTrial = new ArrayList<>();
        while(iterator<rowIndex)
        {
             for(Node child : gridPaneTrialList.getChildren())
             {
                if (gridPaneTrialList.getRowIndex(child) == iterator)
                {

                    if (child instanceof ComboBox)
                    {
                        String result = ((ComboBox) child).getValue().toString();
                        System.out.println(result);
                    }
                    if (child instanceof TextField)
                    {

                        String result = ((TextField) child).getText();
                        System.out.println(result);
                    }
                }
             }
             //System.out.println(comboBoxActionType);
             //System.out.println(comboBoxSelectElementBy);
             //System.out.println(comboBoxSelectPlaceBy);
             //System.out.println(textFieldFirstValueArgs);
             //System.out.println(textFieldSecondValueArgs);
             //currentAction.executeTrial();
                     iterator++;
        }
        Trial currentAction = new Trial("Click","Id","","Id","");
        currentAction.executeTrial();
        System.out.println("Funciona");
    }
}
