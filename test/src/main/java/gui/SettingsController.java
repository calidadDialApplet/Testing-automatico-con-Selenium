package gui;


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import org.apache.commons.validator.routines.UrlValidator;
import persistence.H2DAO;
import persistence.settingsObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private ChoiceBox choiceBoxBrowser;

    @FXML
    private TextField textFieldWeb;

    @FXML
    private CheckBox checkBoxHeadLess;

    @FXML
    private CheckBox checkBoxDarkTheme;

    @FXML
    private Button buttonCancel;

    private static Stage stageGlobalVariables;
    private static Scene sceneGlobalVariables;
    private ArrayList<String> browsers = new ArrayList<>(Arrays.asList("Firefox","Chrome"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // TODO: Initialize with default data or data found in BBDD
        //       Save data after modify and "Save"

        choiceBoxBrowser.setItems(FXCollections.observableArrayList(browsers));
        textFieldWeb.setText(H2DAO.getWeb());
        if (H2DAO.getBrowser().equals("Firefox"))
        {
            choiceBoxBrowser.getSelectionModel().select(0);
        }
        if (H2DAO.getBrowser().equals("Chrome"))
        {
            choiceBoxBrowser.getSelectionModel().select(1);
        }

        if (H2DAO.isHeadless().equals("true")){
            checkBoxHeadLess.setSelected(true);
        } else{
            checkBoxHeadLess.setSelected(false);
        }

        if (H2DAO.isDarkTheme()){
            checkBoxDarkTheme.setSelected(true);
        } else{
            checkBoxDarkTheme.setSelected(false);
        }

        checkBoxDarkTheme.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
               if (newValue){
                   Main.setTheme("darcula");
                   MainController.setTheme("Settings","darcula");
               } else {
                   Main.setTheme("modena");
                   MainController.setTheme("Settings","modena");
               }

            }
        });
    }

    public void closeSettings()
    {
        MainController.closeStage("Settings");
    }

    public void saveSettings()
    {
        UrlValidator defaultValidator = new UrlValidator();
        if (defaultValidator.isValid(textFieldWeb.getText()))
        {
            settingsObject  settings = new settingsObject(textFieldWeb.getText(), checkBoxHeadLess.isSelected(), choiceBoxBrowser.getValue().toString(), checkBoxDarkTheme.isSelected());
            H2DAO.saveSettings(settings);
            MainController.closeStage("Settings");
        } else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("La URL no es valida");
            alert.setContentText("Estas tonto o que? :)");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(choiceBoxBrowser.getScene().getWindow());
            alert.show();


        }

    }

    public void openGlobalVariables()
    {
        try {

            GlobalVariablesController globalVariablesController = new GlobalVariablesController();



            String globalID = "-1";
            //variablesController.setTrialID(globalID);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GlobalVariables.fxml"));
            loader.setController(globalVariablesController);
            Parent root = loader.load();
            stageGlobalVariables = new Stage();
            stageGlobalVariables.setResizable(false);
            stageGlobalVariables.initModality(Modality.APPLICATION_MODAL);
            stageGlobalVariables.setAlwaysOnTop(true);
            stageGlobalVariables.setTitle("Global Variables");
            sceneGlobalVariables = new Scene(root, 600, 400);
            if (H2DAO.isDarkTheme()) {
                setTheme("darcula");
            } else {
                setTheme("modena");
            }
            stageGlobalVariables.initOwner(buttonCancel.getScene().getWindow());
            stageGlobalVariables.setScene(sceneGlobalVariables);
            stageGlobalVariables.show();



        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeStage()
    {
        stageGlobalVariables.close();
    }

    public static void setTheme(String theme)
    {
        sceneGlobalVariables.getStylesheets().clear();
        if (theme.equals("darcula")) {
            sceneGlobalVariables.getStylesheets().add("/css/darcula.css");
        }
        if (theme.equals("modena")) {
            Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        }
    }
}
