import main.Main;
import main.SeleniumDAO;
import main.Utils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


// Create a user coordinator + agent. Format of the username CoordrcNverXYZ,
// where N is the number of the RC and XYZ is the version.
// Assign user to the telemarketing service we created before

public class Test_1_4 {
        public static void main(String[] args) {

            WebDriver driver = SeleniumDAO.initializeFirefoxDriver();
            driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

            Main.loginDialappletWeb("admin","admin",driver);
            // Click on Admin tab
            WebElement adminTab = SeleniumDAO.selectElementBy("id","ADMIN", driver);
            SeleniumDAO.click(adminTab);
            // Click on "Users" left menu
            WebElement users = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[1]/h3[2]",driver);
            SeleniumDAO.click(users);

            WebElement configureUsers = SeleniumDAO.selectElementBy("xpath", "/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a", driver);
            SeleniumDAO.click(configureUsers);

            WebElement createUser = SeleniumDAO.selectElementBy("xpath","/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a", driver);
            SeleniumDAO.click(createUser);


            String name ="CoordrcNver7816";
            Utils.generateUniqueID(name);
            //uniqueID = uniqueID.substring(0,4);
            //name = name.concat(uniqueID);

            WebElement username = SeleniumDAO.selectElementBy("id", "username", driver);
            username.sendKeys(name);

            WebElement userPass = SeleniumDAO.selectElementBy("id", "pswd", driver);
            userPass.sendKeys("contraseña1234");

            WebElement confirmUserPass = SeleniumDAO.selectElementBy("id","pass2", driver);
            confirmUserPass.sendKeys("contraseña1234");
            // Set coordinator role
            WebElement coordinator = SeleniumDAO.selectElementBy("id", "iscoordinator", driver);
            SeleniumDAO.click(coordinator);
            // Set agent role
            WebElement agent = SeleniumDAO.selectElementBy("id", "isagent", driver);
            SeleniumDAO.click(agent);
            // Click on submit button
            WebElement accept = SeleniumDAO.selectElementBy("id", "submit", driver);
            SeleniumDAO.click(accept);
            // Configure tabs (default configuration)
            WebElement send = SeleniumDAO.selectElementBy("name", "send_tabs", driver);
            SeleniumDAO.click(send);
            // Configure agent groups(default configuration)
            WebElement submit = SeleniumDAO.selectElementBy("name", "submit-page-one", driver);
            SeleniumDAO.click(submit);
            // Configure agent services(DialappletDemoDavid service)
            WebElement service = SeleniumDAO.selectElementBy("id", "s94", driver);
            SeleniumDAO.click(service);
            WebElement callerMode = SeleniumDAO.selectElementBy("id", "c336", driver);
            SeleniumDAO.click(callerMode);
            WebElement submitSecondPage = SeleniumDAO.selectElementBy("name", "submit-page-two",driver);
            SeleniumDAO.click(submitSecondPage);
            // Configure agent groups as coordinator(default configuration)
            WebElement submitThirdPage = SeleniumDAO.selectElementBy("name", "submit", driver);
            SeleniumDAO.click(submitThirdPage);


            driver.navigate().to("http://pruebas7.dialcata.com/dialapplet-web/edit-user.php?username="+name+"&");
            WebElement agentTest = SeleniumDAO.selectElementBy("id", "isagent", driver);
            boolean firstCondition = agentTest.isSelected();
            WebElement coordinatorTest = SeleniumDAO.selectElementBy("id", "iscoordinator", driver);
            boolean secondCondition = coordinatorTest.isSelected();
            if(firstCondition && secondCondition) System.out.println("Prueba creación usuario agente y coordinador finalizada con éxito!");
            else System.out.println("Algo ha petado, repasar");

        }



}


