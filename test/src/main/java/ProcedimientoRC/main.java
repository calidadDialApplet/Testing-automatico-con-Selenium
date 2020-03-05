package ProcedimientoRC;

import CheckListRafa.*;
import ProcedimientoRC.ParteDeAgentes.CleanParteDeAgentes;
import ProcedimientoRC.ParteDeShowflow.CleanParteDeShowflow;
import Utils.CleanTest;
import Utils.Color;
import Utils.Test;
import Utils.TestWithConfig;
import org.apache.commons.collections.map.HashedMap;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.util.*;

public class main {
    static Wini commonIni;
    static Wini selfIni;

    public static void main(String[] args) {

        Map<String, Test> tests = new HashMap<>();
        Map<String, CleanTest> testsToClean = new HashMap<>();
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

            tests.put("parteDeAgentes", new ParteDeAgentesTest(commonIni));
            tests.put("parteDeShowflow", new ParteDeShowflowTest(commonIni));
            tests.put("parteDeServicio", new ParteDeServicioTest(commonIni));

            testsToClean.put("parteDeAgentes", new CleanParteDeAgentes(commonIni));
            testsToClean.put("parteDeShowflow", new CleanParteDeShowflow(commonIni));


            String testsListAsString = commonIni.get("General", "testsList");
            String testsToCleanAsString = commonIni.get("Clean", "testsToClean");
            List<String> testsList;


            if(testsToCleanAsString != null)
            {
                List<String> testsToCleanList = Arrays.asList(testsToCleanAsString.split(","));
                for(String testToClean : testsToCleanList)
                {
                    testsToClean.get(testToClean).clean();
                }
            }


            //The tests are given by console
            if (args.length == 3 || (args.length == 2 && !args[1].contains(".ini")))
                if(!args[1].contains(".ini"))  testsList = Arrays.asList(args[1].split(","));
                else testsList = Arrays.asList(args[2].split(","));                //InicializationSettings has tests parameter with the tests to execute
            else if (testsListAsString != null)
                testsList = Arrays.asList(testsListAsString.split(","));
                //All tests are executed
            else
                testsList = new ArrayList<String>(tests.keySet());

            for (String testName : testsList)
            {
                if(tests.containsKey(testName))
                {
                    System.out.println(Color.CYAN + "Beggining test: " + testName + Color.RESET);
                    HashMap<String, String> results = tests.get(testName).check();
                    for (String name: results.keySet()){
                        String key = name;
                        String value = results.get(name);
                        if(value.contains("OK")) System.out.println(Color.CYAN + key + Color.RESET + " " + Color.GREEN + value + Color.RESET);
                        else if(value.contains("ERROR")) System.out.println(Color.CYAN + key + Color.RESET + " " + Color.RED + value + Color.RESET);

                    }

                }
                else
                {
                    System.err.println(Color.RED + "The arguments passed do not match with the tests" + Color.RESET);
                }
            }

        } catch(Exception e)
        {
            System.out.println(e.toString() + "\n" + Color.RED + "ERROR. The path of the inicialization file is incorrect" + Color.RESET + e.toString());
        }
    }
}
