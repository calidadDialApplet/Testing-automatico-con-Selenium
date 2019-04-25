package persistence;

public class settingsObject {
    private final String web;
    private final boolean headless;
    private final String browser;

    public settingsObject(String web, boolean headless, String browser) {
        this.web = web;
        this.headless = headless;
        this.browser = browser;
    }

    public String getWeb() {
        return web;
    }

    public boolean isHeadless() {
        return headless;
    }

    public String getBrowser() {
        return browser;
    }
}
