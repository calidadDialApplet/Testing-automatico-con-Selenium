import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

// Main entrypoint, GUI initialization, etc.

public class Main extends Application {

        Button button;

        public static void main(String[] args) {
                launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception{
                Parent root = FXMLLoader.load(getClass().getResource("gui/Main.fxml"));
                primaryStage.setTitle("WEB UI Tester");
                primaryStage.setScene(new Scene(root, 800, 480));
                primaryStage.show();
        }
}

