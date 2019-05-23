package gui;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.Main;
import persistence.H2DAO;
import persistence.settingsObject;

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
        settingsObject  settings = new settingsObject(textFieldWeb.getText(), checkBoxHeadLess.isSelected(), choiceBoxBrowser.getValue().toString(), checkBoxDarkTheme.isSelected());
        H2DAO.saveSettings(settings);
        MainController.closeStage("Settings");
    }
}
