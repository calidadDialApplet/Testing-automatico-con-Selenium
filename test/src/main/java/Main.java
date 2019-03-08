import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

        Button button;

        public static void main(String[] args) {
            // Lanzar interfaz again
                launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception{
                primaryStage.setTitle("DialTest");

                button = new Button();
                button.setText("Click me");

                StackPane layout = new StackPane();
                layout.getChildren().add(button);

                Scene scene = new Scene(layout, 300, 250);
                primaryStage.setScene(scene);
                primaryStage.show();
        }
}

