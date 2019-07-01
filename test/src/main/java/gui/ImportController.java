package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    Button buttonImport;

    @FXML
    CheckBox newTrial;

    int listViewRowIndex = 0;
    File file;
    private boolean error;

    private ArrayList<Action> actionList;
    private ArrayList<Action> validationList;
    private ArrayList<VariableNV> variablesNVList;


    String trialColumnsHeadersCSV = "Action,FirstSelectBy,FirstValue,SecondSelectBy,SecondValue,Validation";
    String variablesColumnsHeadersCSV = "VariableName,Value";
    String newTrialName;

    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\S+\\}");

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        error = false;
        actionList = new ArrayList<>();
        validationList = new ArrayList<>();
        variablesNVList = new ArrayList<>();
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

    public void closeImport()
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

        if (!error){
            if (newTrial.isSelected())
            {
                newTrial();
                String id = H2DAO.getTrialID(newTrialName);
                if (!id.equals("NULL"))
                {
                    if (checkActionsFormat(actionList) && checkActionsFormat(validationList))
                    {
                        H2DAO.saveTrial(actionList, id, 0);
                        H2DAO.saveTrial(validationList, id, 1);
                    }
                    setVariablesTrialAndSave(variablesNVList, id);
                }
                closeImport();


            }
            for (CheckBox trialSelected : listViewTrials.getItems())
            {
                if (trialSelected.isSelected())
                {
                    String id = H2DAO.getTrialID(trialSelected.getText());
                    if (checkActionsFormat(actionList) && checkActionsFormat(validationList))
                    {
                        H2DAO.saveTrial(actionList, id, 0);
                        H2DAO.saveTrial(validationList, id, 1);
                    }
                    setVariablesTrialAndSave(variablesNVList, id);
                }
            }
        }else{

        }


    }

    private void setVariablesTrialAndSave(ArrayList<VariableNV> variablesNVList, String newTrialId)
    {
        ArrayList<String> failedVariables = new ArrayList<>();
        ArrayList<Variable> variables = new ArrayList<>();
        for (VariableNV variableNV : variablesNVList)
        {
            variables.add(new Variable(newTrialId,variableNV.getVariableName(), variableNV.getValue()));
        }


        for (Variable variable : variables)
        {
            checkVariables(failedVariables,variable);
            //variable.setVariableTrial(newTrialId);
            //H2DAO.saveVariable(variable);
        }
        if (failedVariables.size() > 0)
        {
            error = true;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Se ha producido un error durante la importación de las variables en el test: "+H2DAO.getTrialName(newTrialId));
            alert.initOwner(listViewTrials.getScene().getWindow());
            String error = "";
            for (String failedVariable : failedVariables)
            {
                error = error.concat(failedVariable+"\n");
            }
            alert.setContentText(error);
            alert.showAndWait();
        }
    }

    private void newTrial() {
        //bottomButtons.setDisable(false);
        TextInputDialog dialog = new TextInputDialog("Prueba");
        dialog.setTitle("Nueva prueba");
        dialog.setHeaderText("");
        dialog.initOwner(buttonImport.getScene().getWindow());
        dialog.setContentText("Por favor introduzca el nombre de la prueba sin '_' :");

        Optional<String> result = dialog.showAndWait();

        if (result.toString().contains("_"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El nombre de la prueba contiene '_'");
            alert.setContentText("Estas tonto o que? :)");
            alert.initOwner(buttonImport.getScene().getWindow());
            alert.show();
        } else {
            if (result.isPresent()) {
                if (!checkTrialName(result.get())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Nombre de trial repetido");
                    alert.setContentText("Contacta con tu administrador :)");
                    alert.initOwner(buttonImport.getScene().getWindow());
                    alert.show();
                } else {
                    newTrialName = result.get();
                    H2DAO.createTrial(result.get());
                    //fillTestList();
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
                        Action act = new Action( values[0], values[1], values[2], values[3], values[4]);
                        actionList.add(act);
                    }
                    if (values[5].equals("true")) {
                        Action act = new Action(values[0], values[1], values[2], values[3], values[4]);
                        validationList.add(act);
                    }
                }


                if (header.equals("Variables"))
                {
                    String[] values = data.split(",");
                    if (!values[0].equals("VariableName"))
                    {
                        variablesNVList.add(new VariableNV(values[0], values[1]));
                    }
                }
            }
            if(header.equals("Trial"))
            {
                inputStream.close();
            }else if (header.equals("Variables"))
            {
                if (failedVariables.size() > 0)
                {
                    error = true;
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText("Se ha producido un error durante la importación de las variables");
                    alert.initOwner(buttonImport.getScene().getWindow());
                    String error = "";
                    for (String failedVariable : failedVariables)
                    {
                        error = error.concat(failedVariable+"\n");
                    }
                    alert.setContentText(error);
                    alert.show();
                }
            }else{
                error = true;
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Se ha producido un error durante la importación del test");
                alert.setContentText("El fichero no contienen las columnas correctas");
                alert.initOwner(buttonImport.getScene().getWindow());
                alert.show();
            }

        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void importJSONTrial(File file)
    {

        JSONParser parser = new JSONParser();
        //ArrayList<String> failedVariables = new ArrayList<>();

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
                    //ActionController actionController = new ActionController();
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
                    VariableNV variableNV = new VariableNV(variable.get("variableName").toString(), variable.get("value").toString());
                    variablesNVList.add(variableNV);
                    //Variable var = new Variable(variable.get("variableTrial").toString(), variable.get("variableName").toString(), variable.get("value").toString());
                    //checkVariables(failedVariables, var);

                }
            }

            /*if (checkActionsFormat(actionList) && checkActionsFormat(validationList) && failedVariables.size() == 0)
            {
                //saveTest();
                //fillTestList();

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
            }*/

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

    private boolean checkActionsFormat(List<Action> actions)
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

                    H2DAO.saveVariable(variable);
                } else {
                    failedVariables.add("Variable " + variable.getVariableName() + " Fallo: Variable existente");
                    error = true;
                }
            }else {
                failedVariables.add("Variable " + variable.getVariableName() + " Fallo: No existe trial");
                error = true;
            }
        } else {
            failedVariables.add("Variable " + variable.getVariableName() + " Fallo: Formato");
            error = true;
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
