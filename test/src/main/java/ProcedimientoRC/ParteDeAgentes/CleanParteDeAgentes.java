package ProcedimientoRC.ParteDeAgentes;

import Utils.CleanTest;
import Utils.DriversConfig;
import exceptions.MissingParameterException;
import org.ini4j.Wini;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CleanParteDeAgentes extends CleanTest {
    static String adminName;
    static String adminPassword;
    static String url;
    static String groupName;
    static String groupName1y2;
    static String groupName3;
    static String agentName1;
    static String agentName2;
    static String agentName3;
    static String agentName4;
    static String agentName5;
    static String agentPassword;
    static String pilotAgentName1;
    static String pilotAgentName2;
    static String agentCoordName1;
    static String agentCoordName6;
    static String coordinatorPassword;
    static String headless;
    static String csvPath;
    static String serviceID;

    static WebDriver firefoxDriver;
    static WebDriverWait firefoxWaiting;

    public CleanParteDeAgentes(Wini commonIni) {super(commonIni);}

    @Override
    public HashMap<String, List<String>> getRequiredParameters() {
        HashMap<String, List<String>> requiredParameters = new HashMap<>();
        requiredParameters.put("General", new ArrayList<>(Arrays.asList("url", "headless")));
        requiredParameters.put("Admin", new ArrayList<>(Arrays.asList("adminName", "adminPassword")));
        requiredParameters.put("Group", new ArrayList<>(Arrays.asList("groupName", "groupName1y2", "groupName3")));
        requiredParameters.put("Agent", new ArrayList<>(Arrays.asList("agentName1", "agentName2", "agentName3", "agentName4", "agentName5",
                "agentPassword", "pilotAgentName1", "pilotAgentName2")));
        requiredParameters.put("Coordinator", new ArrayList<>(Arrays.asList("agentCoordName1", "agentCoordName6", "coordinatorPassword")));
        requiredParameters.put("CSV", new ArrayList<>(Arrays.asList("csvPath")));
        requiredParameters.put("Service", new ArrayList<>(Arrays.asList("serviceID")));

        return requiredParameters;
    }

    @Override
    public void clean() throws MissingParameterException {
        super.checkParameters();

        url = commonIni.get("General", "url");
        headless = commonIni.get("General", "headless");
        adminName = commonIni.get("Admin", "adminName");
        adminPassword = commonIni.get("Admin", "adminPassword");

        groupName = commonIni.get("Group", "groupName");
        groupName1y2 = commonIni.get("Group", "groupName1y2");
        groupName3 = commonIni.get("Group", "groupName3");

        agentName1 = commonIni.get("Agent", "agentName1");
        agentName2 = commonIni.get("Agent", "agentName2");
        agentName3 = commonIni.get("Agent", "agentName3");
        agentName4 = commonIni.get("Agent", "agentName4");
        agentName5 = commonIni.get("Agent", "agentName5");
        agentPassword = commonIni.get("Agent", "agentPassword");
        pilotAgentName1 = commonIni.get("Agent", "pilotAgentName1");
        pilotAgentName2 = commonIni.get("Agent", "pilotAgentName2");

        agentCoordName1 = commonIni.get("Coordinator", "agentCoordName1");
        agentCoordName6 = commonIni.get("Coordinator", "agentCoordName6");
        coordinatorPassword = commonIni.get("Coordinator", "coordinatorPassword");

        csvPath = commonIni.get("CSV", "csvPath");
        serviceID = commonIni.get("Service", "serviceID");

        firefoxDriver = DriversConfig.headlessOrNot("true");
        firefoxWaiting = new WebDriverWait(firefoxDriver, 6);
    }
}
