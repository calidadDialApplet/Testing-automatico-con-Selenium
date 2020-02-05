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

        if (args.length == 0) {
            for (String testName : tests.keySet()) {
                tests.get(testName).check();
            }
        } else if (tests.containsKey(args[0])) {
            results = tests.get(args[0]).check();
            for (String name: results.keySet()){
                String key = name.toString();
                String value = results.get(name).toString();
                System.out.println(key + " " + value);
            }
        }
        else {

        }
    }
}
