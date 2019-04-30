package persistence;

public class settingsObject {
    private final String web;
    private final boolean headless;
    private final boolean darktheme;
    private final String browser;

    public settingsObject(String web, boolean headless, String browser, boolean darktheme) {
        this.web = web;
        this.headless = headless;
        this.browser = browser;
        this.darktheme = darktheme;
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

    public boolean isDarktheme() {
        return darktheme;
    }
}
