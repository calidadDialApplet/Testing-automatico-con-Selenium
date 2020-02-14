package Utils;

import org.ini4j.Wini;

public abstract class TestWithConfig extends Test {
    protected Wini ini;

    public TestWithConfig(Wini ini) {
        this.ini = ini;
    }
}
