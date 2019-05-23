package gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;


public class VariablesController {

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


    public void initialize(URL location, ResourceBundle resources)
    {


    }

    public void saveVariable()
    {

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
        deleteVariable.setPadding(new Insets(5,0,0,0));

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

    public void goThroughTable()
    {
       int iterator = 0;
       String variable = "";
       String value = "";

        HashMap<String, String> variables = new HashMap<>();

       while (iterator < variableTableIndex)
       {
           int i = 0;
           for (Node child : gridPaneVariableTable.getChildren())
           {
                if (child instanceof TextField && i == 0)
                {
                    variable = ((TextField) child).getText();
                    i++;
                }

                if (child instanceof TextField && i == 1)
                {
                    value = ((TextField) child).getText();
                    i++;
                }

           }
           variables.put(variable,value);
           // deleteVariables (trial)
           // saveVariables (variables, trial)
           iterator++;
       }
    }

    public void getVariables()
    {

    }

}
