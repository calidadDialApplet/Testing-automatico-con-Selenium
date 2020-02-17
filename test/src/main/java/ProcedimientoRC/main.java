package ProcedimientoRC;

import CheckListRafa.*;
import Utils.Test;
import Utils.TestWithConfig;
import org.apache.commons.collections.map.HashedMap;
import org.ini4j.Wini;

import java.io.File;
import java.util.*;

public class main {
    static Wini ini;
    public static void main(String[] args) {

        /*Map<String, TestWithConfig> tests = new HashMap<>();
        Map<String, String> results = new HashMap<>();

        // Add some vehicles.
        tests.put("parteDeAgentes", new ParteDeAgentesTest());
        tests.put("parteDeShowflow", new ParteDeShowflowTest());

        if (args.length == 0) {
            for (String testName : tests.keySet()) {
                tests.get(testName).check();
            }
        } else if (tests.containsKey(args[0])) {
            results = tests.get(args[0]).check();
            for (String name: results.keySet()){
                String key = name.toString();
                String value = results.get(name).toString();
                if(value.contains("OK")) System.out.println(key + " " + "\u001B[32m" + value + "\u001B[0m");
                else if(value.contains("ERROR")) System.out.println(key + " " + "\u001B[31m" + value + "\u001B[0m");

            }
        }
        else {

        }*/


        Map<String, Test> tests = new HashMap<>();
        if (args.length == 0)
        {
            System.out.println("\u001B[31m" + "ERROR. You need to pass at least one argument with the path of the inicialization file" + "\u001B[0m");
            System.exit(0);
        }

        try
        {
            ini = new Wini(new File(args[0]));
            tests.put("parteDeAgentes", new CallsTest(ini));
            tests.put("parteDeShowflow", new CordinatorTest(ini));


            String testsListAsString = ini.get("Test", "testsList");
            List<String> testsList;

            if (testsListAsString != null)
                testsList = Arrays.asList(testsListAsString.split(","));
            else if (args.length == 2)
                testsList = Arrays.asList(args[1].split(","));
            else
                testsList = new ArrayList<String>(tests.keySet());

            for (String testName : testsList)
            {
                if(tests.containsKey(testName))
                {
                    System.out.println("\u001B[36m" + "Beggining test: " + testName + "\u001B[0m");
                    HashMap<String, String> results = tests.get(testName).check();
                    for (String name: results.keySet()){
                        String key = name.toString();
                        String value = results.get(name).toString();
                        if(value.contains("OK")) System.out.println("\u001B[36m" + key + "\u001B[0m" + " " + "\u001B[32m" + value + "\u001B[0m");
                        else if(value.contains("ERROR")) System.out.println("\u001B[36m" + key + "\u001B[0m" + " " + "\u001B[31m" + value + "\u001B[0m");

                    }

                }
                else
                {
                    System.err.println("\u001B[31m" + "The arguments passed do not match with the tests" + "\u001B[0m");
                }
            }

        } catch(Exception e)
        {
            System.out.println("\u001B[31m" + "ERROR. The path of the inicialization file is incorrect" + "\u001B[0m\n" + e.toString());
        }
    }
}
