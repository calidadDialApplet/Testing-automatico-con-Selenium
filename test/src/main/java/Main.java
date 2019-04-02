import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


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
}

