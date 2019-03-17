import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


// Create a user coordinator + agent. Format of the username CoordrcNverXYZ,
// where N is the number of the RC and XYZ is the version.
// Assign user to the telemarketing service we created before

public class Test_1_4 {
        public static void main(String[] args) {

            System.setProperty("webdriver.gecko.driver", "geckodriver");
            WebDriver driver = new FirefoxDriver();
            driver.get("http://pruebas7.dialcata.com/dialapplet-web/");

            WebElement user = driver.findElement(By.id("adminusername"));
            user.sendKeys("admin");

            WebElement pass = driver.findElement(By.id("adminpassword"));
            pass.sendKeys("admin");

            WebElement entry = driver.findElement(By.id("login"));
            entry.click();

            WebElement adminButton = driver.findElement(By.id("ADMIN"));
            adminButton.click();

            WebElement users = driver.findElement(By.xpath("/html/body/div[2]/div[1]/h3[2]"));
            users.click();

            WebElement configureUsers = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div/div[1]/p[1]/a"));
            configureUsers.click();

            WebElement createUser = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[2]/div[4]/div[1]/div/table/tbody/tr/td[2]/a"));
            createUser.click();


            String name ="CoordrcNver7816";
            String uniqueID = ""+Math.floor(1000 + Math.random() * 9999);
            uniqueID = uniqueID.substring(0,4);
            name = name.concat(uniqueID);
            WebElement username = driver.findElement(By.id("username"));
            username.sendKeys(name);

            WebElement userPass = driver.findElement(By.id("pswd"));
            userPass.sendKeys("contraseña1234");

            WebElement userPass2 = driver.findElement(By.id("pass2"));
            userPass2.sendKeys("contraseña1234");
            // Set coordinator role
            WebElement coordinator = driver.findElement(By.id("iscoordinator"));
            coordinator.click();
            // Set agent role
            WebElement agent = driver.findElement(By.id("isagent"));
            agent.click();
            // Click on submit button
            WebElement accept = driver.findElement(By.id("submit"));
            accept.click();
            // Configure tabs (default configuration)
            WebElement send = driver.findElement(By.name("send_tabs"));
            send.click();
            // Configure agent groups(default configuration)
            WebElement submit = driver.findElement(By.name("submit-page-one"));
            submit.click();
            // Configure agent services(DialappletDemoDavid service)
            WebElement service = driver.findElement(By.id("s94"));
            service.click();
            WebElement callerMode = driver.findElement(By.id("c336"));
            callerMode.click();
            WebElement submit2 = driver.findElement(By.name("submit-page-two"));
            submit2.click();
            // Configure agent groups as coordinator(default configuration)
            WebElement submit3 = driver.findElement(By.name("submit"));
            submit3.click();


            driver.navigate().to("http://pruebas7.dialcata.com/dialapplet-web/edit-user.php?username="+name+"&");
            WebElement agent2 = driver.findElement(By.id("isagent"));
            boolean firstCondition = agent2.isSelected();
            WebElement coordinator2 = driver.findElement(By.id("iscoordinator"));
            boolean secondCondition = coordinator2.isSelected();
            if(firstCondition && secondCondition) System.out.println("Prueba creación usuario agente y coordinador finalizada con éxito!");
            else System.out.println("Algo ha petado, repasar");

        }



}


