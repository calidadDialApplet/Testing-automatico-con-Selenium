package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Menu menuFile;

    @FXML
    private MenuItem menuItemImport;

    @FXML
    private MenuItem menuItemExport;

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private Menu menuHelp;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private Button buttonPlay;

    @FXML
    private Label labelStatus;

    @FXML
    void initialize() {
        assert menuFile != null : "fx:id=\"menuFile\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemImport != null : "fx:id=\"menuItemImport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemExport != null : "fx:id=\"menuItemExport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuHelp != null : "fx:id=\"menuHelp\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemAbout != null : "fx:id=\"menuItemAbout\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonPlay != null : "fx:id=\"buttonPlay\" was not injected: check your FXML file 'Main.fxml'.";
        assert labelStatus != null : "fx:id=\"labelStatus\" was not injected: check your FXML file 'Main.fxml'.";

    }
}
