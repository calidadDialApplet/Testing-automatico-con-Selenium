package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import persistence.H2DAO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class ImportController implements Initializable {

    @FXML
    ListView<CheckBox> listViewTrials;

    @FXML
    CheckBox newTrial;

    int listViewRowIndex = 0;
    File file;

    private ArrayList<Action> actionList;
    private ArrayList<Action> validationList;
    private ArrayList<Variable> variablesList;

    String trialColumnsHeadersCSV = "Action,FirstSelectBy,FirstValue,SecondSelectBy,SecondValue,Validation";
    String variablesColumnsHeadersCSV = "TrialID,VariableName,Value";
    String newTrialName;

    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\S+\\}");

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        fillGrid();
    }

    private void fillGrid()
    {
        for (String trial : H2DAO.getTrials())
        {
            listViewTrials.getItems().add(listViewRowIndex, new CheckBox(trial));
            listViewRowIndex++;
        }
    }

    public void closeVariables()
    {
        MainController.closeStage("Import");
    }

    public void importTrial()
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




        if (newTrial.isSelected())
        {
            newTrial();
            String id = H2DAO.getTrialID(newTrialName);
            H2DAO.saveTrial(actionList, id, 0);
            H2DAO.saveTrial(validationList, id, 1);
            setVariablesTrialAndSave(variablesList, id);

        }
        for (CheckBox trialSelected : listViewTrials.getItems())
        {
            if (trialSelected.isSelected())
            {

            }
        }
    }

    private void setVariablesTrialAndSave(ArrayList<Variable> variablesList, String newTrialId)
    {
        for (Variable variable : variablesList)
        {
            variable.setVariableTrial(newTrialId);
            H2DAO.saveVariable(variable);
        }
    }

    private void newTrial() {
        //bottomButtons.setDisable(false);
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
                newTrialName = result.toString();
                if (!checkTrialName(result.get())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Nombre de trial repetido");
                    alert.setContentText("Contacta con tu administrador :)");
                    alert.showAndWait();
                } else {
                    H2DAO.createTrial(result.get());
                    //poblateTestList();
                    //testList.getSelectionModel().selectLast();
                }


            } else {
                //bottomButtons.setDisable(true);
            }
        }
    }

    private void importCSVTrial(File file) {
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
                        //ActionController actionController = new ActionController();
                        //actionController.setAction(gridPaneTrialList, actionsRowIndex, values[0], values[1], values[2], values[3], values[4]);
                        Action act = new Action( values[0], values[1], values[2], values[3], values[4]);
                        actionList.add(act);
                        //actionsRowIndex++;
                    }
                    if (values[5].equals("true")) {
                        //ActionController actionController = new ActionController();
                        //actionController.setAction(gridPaneValidationList, validationRowIndex, values[0], values[1], values[2], values[3], values[4]);
                        Action act = new Action(values[0], values[1], values[2], values[3], values[4]);
                        validationList.add(act);
                        //validationRowIndex++;
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
                            //H2DAO.saveTrialVariables(variablesList, lastTrialVariable);
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
                /*if (checkVariablesFormat(actionList) && checkVariablesFormat(validationList))
                {
                    saveTest();
                    poblateTestList();
                } else {
                    // Aviso formato de variables
                    deleteAllTabs();
                }*/
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


            if (actions != null) {
                for (int i = 0; i < actions.size(); i++) {
                    org.json.simple.JSONObject action = (org.json.simple.JSONObject) actions.get(i);
                    /*ActionController actionController = new ActionController();
                    actionController.setAction(gridPaneTrialList, actionsRowIndex, action.get("actionTypeS").toString(), action.get("selectElementByS").toString(),
                            action.get("firstValueArgsS").toString(), action.get("selectPlaceByS").toString(), action.get("secondValueArgsS").toString());*/
                    Action act = new Action(action.get("actionTypeS").toString(), action.get("selectElementByS").toString(),
                            action.get("firstValueArgsS").toString(), action.get("selectPlaceByS").toString(), action.get("secondValueArgsS").toString());
                    actionList.add(act);
                    //actionsRowIndex++;

                }
            }

            if (validations != null) {
                for (int i = 0; i < validations.size(); i++) {
                    org.json.simple.JSONObject validation = (org.json.simple.JSONObject) validations.get(i);
                    ActionController actionController = new ActionController();
                    /*actionController.setAction(gridPaneValidationList, validationRowIndex, validation.get("actionTypeS").toString(), validation.get("selectElementByS").toString(),
                            validation.get("firstValueArgsS").toString(), validation.get("selectPlaceByS").toString(), validation.get("secondValueArgsS").toString());*/
                    Action act = new Action(validation.get("actionTypeS").toString(), validation.get("selectElementByS").toString(),
                            validation.get("firstValueArgsS").toString(), validation.get("selectPlaceByS").toString(), validation.get("secondValueArgsS").toString());
                    validationList.add(act);
                    //validationRowIndex++;

                }
            }

            if (variables != null) {
                for (int i = 0; i < variables.size(); i++) {
                    org.json.simple.JSONObject variable = (org.json.simple.JSONObject) variables.get(i);
                    Variable var = new Variable(variable.get("variableTrial").toString(), variable.get("variableName").toString(), variable.get("value").toString());
                    checkVariables(failedVariables, var);

                }
            }

            if (checkVariablesFormat(actionList) && checkVariablesFormat(validationList) && failedVariables.size() == 0)
            {
                //saveTest();
                //poblateTestList();

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
                //deleteAllTabs();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFile(File file) {
        this.file = file;
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

    private void checkVariables(ArrayList<String> failedVariables, Variable variable)
    {

        if (variable.getVariableName().matches(String.valueOf(VARIABLE_PATTERN)))
        {
            if (H2DAO.trialExist(variable.getVariableTrial())){

                if (!H2DAO.variableExists(variable)) {

                    //H2DAO.saveVariable(variable);
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

    private boolean checkTrialName(String name)
    {
        boolean nameOk = true;
        List<String> trialNames = new ArrayList<>();
        for (String trialName : H2DAO.getTrials()) {
            trialNames.add(trialName);
        }
        if (trialNames.contains(name)){
            nameOk = false;
        }

        return  nameOk;
    }
}
