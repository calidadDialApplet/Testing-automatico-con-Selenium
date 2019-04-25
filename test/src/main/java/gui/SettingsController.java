package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import persistence.H2DAO;
import persistence.settingsObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private ChoiceBox choiceBoxBrowser;

    @FXML
    private TextField textFieldWeb;

    @FXML
    private CheckBox checkBoxHeadLess;

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

    }

    public void closeSettings(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("¿Nos dejas?");
        alert.setHeaderText("Se perderán todos los cambios no guardados");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK)
        {
            System.out.println("Adiós mundo cruel");

        }
        else
        {
            System.out.println("Muerte esquivada una vez más");
        }
    }

    public void saveSettings(){
        settingsObject  settings = new settingsObject(textFieldWeb.getText(), checkBoxHeadLess.isSelected(), choiceBoxBrowser.getValue().toString());
        H2DAO.saveSettings(settings);
        System.out.println("saveSettings finalizado");
    }
}
