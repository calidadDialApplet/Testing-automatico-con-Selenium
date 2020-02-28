package Utils;

import exceptions.MissingParameterException;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TestWithConfig extends Test {
    protected Wini commonIni;
    //protected Wini selfIni;

    public TestWithConfig(Wini commonIni) {
        this.commonIni = commonIni;
    }
    public HashMap<String, List<String>> getRequiredParameters() { return new HashMap<>(); }

    public void checkParameters() throws MissingParameterException {
        for(Map.Entry<String, List<String>> entry : getRequiredParameters().entrySet())
        {
            if(commonIni.containsKey(entry.getKey()))
            {
                for(String parameter : entry.getValue())
                {
                    String res = commonIni.get(entry.getKey(), parameter);
                    if(res == null) throw new MissingParameterException("Missing '" + parameter + "' parameter in section '" + entry.getKey() + "'");
                }
            } else {
                throw new MissingParameterException("Missing section '" + entry.getKey() + "'");
            }
        }
    }
}
