package ProcedimientoRC;

import CheckListRafa.*;
import org.apache.commons.collections.map.HashedMap;

import java.util.HashMap;
import java.util.Map;

public class main {
    public static void main(String[] args) {

        Map<String, Test> tests = new HashMap<>();
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

        }
    }
}
