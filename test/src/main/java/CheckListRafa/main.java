package CheckListRafa;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class main {
    public static void main(String[] args) {

        Map<String, Test> tests = new HashMap<>();

        // Add some vehicles.
        tests.put("calls", new CallsTest());
        tests.put("coordinator", new CordinatorTest());
        tests.put("report", new ReportTest());
        tests.put("exportReport", new ExportReportTest());

        if (args.length == 0) {
            for (String testName : tests.keySet()) {
                tests.get(testName).check();
            }
        } else if (tests.containsKey(args[0])) {
            tests.get(args[0]).check();
        }
        else {

        }
    }
}
