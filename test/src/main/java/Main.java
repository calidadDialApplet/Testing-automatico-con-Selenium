import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Main entrypoint, GUI initialization, etc.

public class Main extends Application {



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
        public static void login(String name, String password, WebDriver driver){
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
}

