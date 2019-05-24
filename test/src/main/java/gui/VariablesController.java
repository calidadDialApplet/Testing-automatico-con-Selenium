package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import persistence.H2DAO;


import java.net.URL;
import java.util.*;


public class VariablesController implements Initializable {

    @FXML
    private Button buttonAddVariable;

    @FXML
    private Button buttonCancel;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonDeleteAll;

    @FXML
    private GridPane gridPaneVariableTable;

    private HBox newVariable = new HBox();
    private TextField variable = new TextField();
    private TextField value = new TextField();
    private CheckBox deleteVariable = new CheckBox();
    private int variableTableIndex = 0;

    private String trialID;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        poblateGrid();
    }

    public VariablesController(String trialID) {
        this.trialID = trialID;
    }

    public VariablesController() {
    }

    public void closeVariables()
    {
        MainController.closeStage("Variables");
    }

    public void addVariable()
    {
        newVariable = new HBox();

        newVariable.setFillHeight(true);
        newVariable.setMinWidth(2000);
        newVariable.setSpacing(10);


        variable = new TextField();
        value = new TextField();
        deleteVariable = new CheckBox();
        newVariable.setMargin(deleteVariable, new Insets(4,0,0,0));
        newVariable.setPadding(new Insets(0,0,0,100));
        newVariable.getChildren().addAll(variable,value,deleteVariable);
        
        gridPaneVariableTable.addRow(variableTableIndex,newVariable);
        variableTableIndex++;
        //System.out.println(trialID);


    }

    public void addVariable(String variableName, String variableValue)
    {
        newVariable = new HBox();
        newVariable.setFillHeight(true);
        newVariable.setMinWidth(2000);
        newVariable.setSpacing(10);

        variable = new TextField(variableName);
        value = new TextField(variableValue);
        deleteVariable = new CheckBox();
        newVariable.setMargin(deleteVariable, new Insets(4,0,0,0));
        newVariable.setPadding(new Insets(0,0,0,100));

        newVariable.getChildren().addAll(variable,value,deleteVariable);
        gridPaneVariableTable.addRow(variableTableIndex,newVariable);
        variableTableIndex++;
    }

    public void deleteSelectedVariables()
    {
        List<Node> nodesToDelete = new ArrayList<>();
        int iterations = 0;
        int rowToDelete = -1;

        for (Node hbox : gridPaneVariableTable.getChildren())                                   // Number of rows to delete
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
            for (Node hbox : gridPaneVariableTable.getChildren())
            {
                if (hbox instanceof  HBox){
                    for (Node child : ((HBox) hbox).getChildren())
                    {
                        if (child instanceof CheckBox && ((CheckBox)child).isSelected())
                        {
                            rowToDelete = gridPaneVariableTable.getRowIndex(child.getParent());
                            break;
                        }
                    }
                }


            }

            for (Node child : gridPaneVariableTable.getChildren())
            {
                if (rowToDelete != -1) {                                    // Fila para borrar
                    if (gridPaneVariableTable.getRowIndex(child) == rowToDelete)
                    {                                                       // Hijo de la fila a borrar
                        child.setVisible(false);
                        nodesToDelete.add(child);
                    }else if (gridPaneVariableTable.getRowIndex(child) > rowToDelete){
                        gridPaneVariableTable.setRowIndex(child,gridPaneVariableTable.getRowIndex(child)-1);
                    }
                }
            }
            gridPaneVariableTable.getChildren().removeAll(nodesToDelete);
        }
        variableTableIndex = variableTableIndex - iterations;
    }

    public void deleteAllVariables()
    {
        gridPaneVariableTable.getChildren().clear();
        variableTableIndex = 0;
    }

    public void saveVariables()
    {
       int iterator = 0;
       String variable = "";
       String value = "";

       ArrayList<Variable> variables = new ArrayList<>();

       while (iterator < variableTableIndex)
       {
           int i = 0;
           for (Node child : gridPaneVariableTable.getChildren())
           {
               if (child instanceof HBox) {

                   for (Node childHbox : ((HBox) child).getChildren()) {

                       if (gridPaneVariableTable.getRowIndex(childHbox.getParent()) == iterator) {
                           if (childHbox instanceof TextField && i == 0) {
                               variable = ((TextField) childHbox).getText();
                               i++;
                           } else if (childHbox instanceof TextField && i == 1) {
                               value = ((TextField) childHbox).getText();
                               i++;
                           }
                       }
                   }
               }
           }

           Variable currentVariable = new Variable("86", variable, value);
           variables.add(currentVariable);
           variable = "";
           value = "";
           iterator++;
       }

        H2DAO.deleteTrialVariables("86");
        H2DAO.saveTrialVariables(variables,"86");
    }

    public void poblateGrid()
    {
        ArrayList<Variable> variables = H2DAO.getTrialVariables("86");

        for (Variable variable : variables)
        {
            addVariable(variable.getVariableName(),variable.getValue());
        }
    }

    public void setTrialID(String trialID)
    {
        this.trialID = trialID;
    }

}
