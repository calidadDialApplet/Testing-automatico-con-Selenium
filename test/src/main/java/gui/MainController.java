package gui;


import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

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
       /*Trial lastAction = trialList.get(trialList.size());
        switch (lastAction.getActionType().getValue().toString()){
            case "Id":
                gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size()-3, gridPaneTrialList.getChildren().size()-1);
                break;
            case "DragAndDrop":
                gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size()-5, gridPaneTrialList.getChildren().size()-1);
                break;
            case "Selector":
                gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size()-3, gridPaneTrialList.getChildren().size()-1);
                break;
            default:
                break;
        }*/
       //gridPaneTrialList.getChildren().remove(gridPaneTrialList.getChildren().size()-1);

   }

   public void deleteAll(){
        gridPaneTrialList.getChildren().remove(0, gridPaneTrialList.getChildren().size());
        rowIndex = 0;
   }

}
