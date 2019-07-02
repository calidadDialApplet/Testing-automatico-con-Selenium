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

        // TODO: Garbage clean
        public static void loginDialappletWeb(String name, String password, WebDriver driver){
                WebElement user = driver.findElement(By.id("adminusername"));
                user.sendKeys(name);
                WebElement pass = driver.findElement(By.id("adminpassword"));
                pass.sendKeys(password);
                WebElement loggin = driver.findElement(By.id("login"));
                loggin.click();
                try{
                     WebElement error = driver.findElement(By.className("mensajer-error"));
                }catch (Exception e){
                        System.out.println(e);
                }
        }

        // TODO: Garbage clean
        public static void loginWebClient(String name, String password, int tlfOption, WebDriver driver){
                WebElement usernameWebClient = SeleniumDAO.selectElementBy("id","userName",driver);
                usernameWebClient.sendKeys(name);

                WebElement passWebClient = SeleniumDAO.selectElementBy("id","passwordBridge",driver);
                passWebClient.sendKeys(password);

                Select tlf = SeleniumDAO.findSelectElementBy("id","selectType",driver);
                //SeleniumDAO.selectOption("index",""+tlfOption, tlf);
                //tlf.selectByIndex(1);

                WebElement entryWebClient = SeleniumDAO.selectElementBy("id","checklogin",driver);
                SeleniumDAO.click(entryWebClient);

                WebElement enter = driver.findElement(By.id("login"));
                enter.click();
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

