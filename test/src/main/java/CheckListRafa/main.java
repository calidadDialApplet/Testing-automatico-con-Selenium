package CheckListRafa;

import Utils.Test;
import exceptions.MissingParameterException;
import org.ini4j.Profile;
import org.ini4j.Wini;
import Utils.Color;

import java.io.File;
import java.util.*;

public class main {
    static Wini commonIni;
    static Wini selfIni;
    public static void main(String[] args) {

        Map<String, Test> tests = new HashMap<>();
        if (args.length == 0)
        {
            System.out.println(Color.RED + "ERROR. You need to pass at least one argument with the path of the inicialization file" + Color.RESET);
            System.exit(0);
        }

        try
        {
            commonIni = new Wini(new File(args[0]));

            //Override or add parameters to commonIni when a second .ini file is passed as argument
            if(args.length > 1 && args[1].contains(".ini"))
            {
                selfIni = new Wini(new File(args[1]));
                //Get the sections and parameters of the second .ini
                for(String sectionName : selfIni.keySet())
                {
                    Profile.Section section = selfIni.get(sectionName);
                    for(String parameterName : section.keySet())
                    {
                        commonIni.put(sectionName, parameterName, selfIni.get(sectionName, parameterName));
                    }
                }
            }

            tests.put("calls", new CallsTest(commonIni));
            tests.put("coordinator", new CordinatorTest(commonIni));
            tests.put("productivityAgents", new ProductivityAgentsTest(commonIni));
            tests.put("productivityExport", new ProductivityExportTest(commonIni));
            tests.put("oldRecordDownload", new OldRecordDownloadTest(commonIni));

            String testsListAsString = commonIni.get("General", "testsList");
            List<String> testsList;

            //The tests are given by console
            if (args.length == 3 || (args.length == 2 && !args[1].contains(".ini")))
                if(!args[1].contains(".ini"))  testsList = Arrays.asList(args[1].split(","));
                else testsList = Arrays.asList(args[2].split(","));
            //InicializationSettings has tests parameter with the tests to execute
            else if (testsListAsString != null)
            testsList = Arrays.asList(testsListAsString.split(","));
            //All tests are executed
            else
                testsList = new ArrayList<String>(tests.keySet());

            //Prints the results
            for (String testName : testsList)
            {
                if(tests.containsKey(testName))
                {
                    System.out.println(Color.CYAN + "Beggining test: " + testName + Color.RESET);
                    HashMap<String, String> results = tests.get(testName).check();
                    for (String name: results.keySet()){
                        String key = name.toString();
                        String value = results.get(name).toString();
                        if(value.contains("OK")) System.out.println(Color.CYAN + key + Color.RESET + " " + Color.GREEN + value + Color.RESET);
                        else if(value.contains("ERROR")) System.out.println(Color.CYAN + key + Color.RESET + " " + Color.RED + value + Color.RESET);
                        else if(value.contains("WARNING")) System.out.println(Color.CYAN + key + Color.RESET + " " + Color.PURPLE + value + Color.RESET);

                    }

                }
                else
                {
                    System.err.println(Color.RED + "The argument: '" + testName + "' from the attribute 'testsList' on the .ini file do not match with the test suite {calls, coordinator, productivityAgents, productivityExport, oldRecordDownload}" + Color.RESET);
                }
            }

        }catch (MissingParameterException e2) {
            System.out.println("\u001B[31m" + e2.getMessage() + "\u001B[0m\n");
        }
        catch(Exception e)
        {
            System.out.println("\u001B[31m" + "ERROR. The path of the inicialization file is incorrect" + "\u001B[0m\n" + e.toString());
        }





    }
}
