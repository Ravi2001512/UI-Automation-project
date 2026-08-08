package com.ucsc.tutionplatform.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralised WebDriver lifecycle manager.
 *
 * <p>Supports <b>Chrome</b>, <b>Firefox</b>, and <b>Edge</b>.
 * The browser is selected via the {@code -Dbrowser} system property
 * (defaults to {@code "chrome"}).  Headless mode is controlled by
 * {@code -Dheadless=true}.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Creates a new {@link WebDriver} for the requested browser, stores it in a
     * {@link ThreadLocal}, and – when not running headless – maximises the window.
     *
     * <p>Supported browser values (case-insensitive): {@code chrome},
     * {@code firefox}, {@code edge}.
     */
    public static void openBrowser() {
        String browserName = System.getProperty("browser", "chrome").toLowerCase().trim();
        boolean headless   = Boolean.parseBoolean(System.getProperty("headless", "false"));

        WebDriver driver = createDriver(browserName, headless);
        setDriver(driver);

        if (!headless) {
            driver.manage().window().maximize();
        }
    }

    public static void setDriver(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized for the current thread");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }

    // =========================================================================
    // Private – driver factory
    // =========================================================================

    private static WebDriver createDriver(String browserName, boolean headless) {
        return switch (browserName) {
            case "chrome"  -> new ChromeDriver(buildChromeOptions(headless));
            case "firefox" -> new FirefoxDriver(buildFirefoxOptions(headless));
            case "edge"    -> new EdgeDriver(buildEdgeOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browserName
                            + ". Supported values: chrome, firefox, edge");
        };
    }

    // =========================================================================
    // Private – browser-specific options
    // =========================================================================

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // Suppress Chrome's password-manager prompts
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        options.addPreference("signon.rememberSignons", false);

        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }

        return options;
    }

    private static EdgeOptions buildEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        // Suppress Edge's password-manager prompts (Chromium-based, same prefs)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        return options;
    }
}
