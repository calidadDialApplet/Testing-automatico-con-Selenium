package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import persistence.H2DAO;


import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;


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
    private ArrayList<Variable> variables = new ArrayList<>();
    private int variableTableIndex = 0;

    private String trialID;
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\S+\\}");


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println("MY TRIAL ID: " + trialID);
        fillGrid();
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

    public ArrayList<String> variablesName()
    {
        ArrayList<String> variablesName = new ArrayList<>();

        for (Node hbox : gridPaneVariableTable.getChildren())                                   // Number of rows to delete
        {
            if (hbox instanceof HBox)
            {
                int i=0;
                for (Node child : ((HBox) hbox).getChildren())
                {
                    if (child instanceof TextField && i == 0)
                    {
                        variablesName.add(((TextField) child).getText());
                        i++;
                    } else {
                        i = 0;
                    }
                }
            }
        }

        return variablesName;
    }

    public void deleteAllVariables()
    {
        gridPaneVariableTable.getChildren().clear();
        variableTableIndex = 0;
    }

    public void saveVariables()
    {
        H2DAO.deleteTrialVariables(trialID);
       int iterator = 0;
       String variable = "";
       String value = "";


       ArrayList<String> failedVariables = new ArrayList<>();

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

           Variable currentVariable = new Variable(trialID, variable, value);
           /*if (currentVariable.getVariableName().matches(VARIABLE_PATTERN.toString()) && !H2DAO.variableExists(currentVariable))
           {
               //variables.add(currentVariable);
               H2DAO.saveVariable(currentVariable);
           }*/
           checkVariablesFormat(failedVariables, currentVariable);
           variable = "";
           value = "";
           iterator++;
       }

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
       }else {

           //H2DAO.deleteTrialVariables(trialID);
           //H2DAO.saveTrialVariables(variables,trialID);
           MainController.closeStage("Variables");
       }

    }

    private void checkVariablesFormat(ArrayList<String> failedVariables, Variable variable)
    {
        if (variable.getVariableName().matches(String.valueOf(VARIABLE_PATTERN)))
        {
            if (!H2DAO.variableExists(variable)) {
                H2DAO.saveVariable(variable);

            } else {
                failedVariables.add("Variable " + variable.getVariableName() + " Fallo: Variable existente");
            }

        } else {
            failedVariables.add("Variable " + variable.getVariableName() + " Fallo: Formato");
        }
    }

    public void fillGrid()
    {
        ArrayList<Variable> variables = H2DAO.getTrialVariables(trialID);
        for (Variable variable : variables)
        {
            addVariable(variable.getVariableName(),variable.getValue());
        }
    }

    public void setTrialID(String trialID)
    {
        this.trialID = trialID;
    }

    public String getTrialID()
    {
        return trialID;
    }

}
