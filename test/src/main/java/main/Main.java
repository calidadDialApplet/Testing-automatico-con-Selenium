package main;

import gui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import persistence.H2DAO;


// Main entrypoint

public class Main extends Application {

        private static Scene scene;
        private static boolean modified;
        private static boolean refreshTestList;

        public static void main(String[] args) { launch(args);}

        @Override
        public void start(Stage primaryStage) throws Exception{
                FXMLLoader loader = new FXMLLoader();
                Parent root = loader.load(getClass().getResource("/gui/Main.fxml"));
                primaryStage.setTitle("WEB UI Tester");
                MainController controller = loader.getController();
                scene = new Scene(root,1024,666); // Devil's height
                if (H2DAO.isDarkTheme())
                {
                        setTheme("darcula");
                }else {
                        setTheme("modena");
                }
                primaryStage.setScene(scene);
                primaryStage.show();
                primaryStage.setOnCloseRequest( event -> {
                        if (controller != null) {
                                event.consume();
                                controller.totalClose();
                        }
                });
        }

        public static void setTheme(String theme)
        {

                scene.getStylesheets().clear();
                if (theme.equals("darcula"))
                {
                        scene.getStylesheets().add("/css/darcula.css");
                }
                if (theme.equals("modena"))
                {
                        //scene.getStylesheets().add("/css/modena.css");
                        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
                }
        }


        public static boolean isModified()
        {
                return modified;
        }

        public static void setModified(boolean modified)
        {
                Main.modified = modified;
        }

        public static boolean isRefreshTestList() {
                return refreshTestList;
        }

        public static void setRefreshTestList(boolean refreshTestList) {
                Main.refreshTestList = refreshTestList;
                if (refreshTestList){
                    MainController mainController = new MainController();
                    System.out.println("PASSSSSSSSSSSSA");
                    mainController.fillTestList();
                }
        }
}

