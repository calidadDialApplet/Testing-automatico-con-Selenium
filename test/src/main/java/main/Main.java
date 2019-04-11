package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Optional;


// main.Main entrypoint, GUI initialization, etc.

public class Main extends Application {

        public static void main(String[] args) {
                launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception{
                Parent root = FXMLLoader.load(getClass().getResource("/gui/Main.fxml"));
                primaryStage.setTitle("WEB UI Tester");
                primaryStage.setScene(new Scene(root, 800, 480));
                primaryStage.show();
                primaryStage.setOnCloseRequest( event -> {
                        event.consume();
                        alertClose();
                });
        }

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

        public static void loginWebClient(String name, String password, int tlfOption, WebDriver driver){
                WebElement usernameWebClient = SeleniumDAO.selectElementBy("id","userName",driver);
                usernameWebClient.sendKeys(name);

                WebElement passWebClient = SeleniumDAO.selectElementBy("id","passwordBridge",driver);
                passWebClient.sendKeys(password);

                Select tlf = SeleniumDAO.findSelectElementBy("id","selectType",driver);
                SeleniumDAO.selectOption("index",""+tlfOption, tlf);
                //tlf.selectByIndex(1);

                WebElement entryWebClient = SeleniumDAO.selectElementBy("id","checklogin",driver);
                SeleniumDAO.click(entryWebClient);

                WebElement enter = driver.findElement(By.id("login"));
                enter.click();
        }

        public static void alertClose(){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("¿Nos dejas?");
                alert.setHeaderText("Se perderán todos los cambios no guardados");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK)
                {
                        System.out.println("Adiós mundo cruel");
                        System.exit(0);
                }
                else
                {
                        System.out.println("Muerte esquivada una vez más");
                }
        }
}

