package com.ucsc.tutionplatform.core;

import com.ucsc.tutionplatform.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralised WebDriver lifecycle manager.
 *
 * <p>All browser configuration (browser name, headless mode, arguments,
 * and preferences) is read from {@code config.properties} via
 * {@link ConfigReader}.  System properties ({@code -Dbrowser},
 * {@code -Dheadless}) take priority when supplied on the command line.
 *
 * <p>Supported browsers: <b>Chrome</b>, <b>Firefox</b>, <b>Edge</b>.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Creates a new {@link WebDriver} for the configured browser, stores it in
     * a {@link ThreadLocal}, and – when not running headless – maximises the
     * window.
     *
     * <p>Configuration is read from {@code config.properties}:
     * <ul>
     *   <li>{@code browser} – chrome | firefox | edge</li>
     *   <li>{@code headless} – true | false</li>
     *   <li>{@code <browser>.arguments} – comma-separated CLI arguments</li>
     *   <li>{@code <browser>.headless.arguments} – extra args when headless</li>
     *   <li>{@code <browser>.prefs} – comma-separated key=value preferences</li>
     * </ul>
     */
    public static void openBrowser() {
        String browserName = resolveProperty("browser", "chrome").toLowerCase().trim();
        boolean headless   = Boolean.parseBoolean(resolveProperty("headless", "false"));

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
            case "chrome"  -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge"    -> createEdgeDriver(headless);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browserName
                            + ". Supported values: chrome, firefox, edge");
        };
    }

    // =========================================================================
    // Private – browser-specific driver creation
    // =========================================================================

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = parsePrefsAsObjects("chrome.prefs");
        if (!prefs.isEmpty()) {
            options.setExperimentalOption("prefs", prefs);
        }

        addArguments(options, "chrome.arguments");
        if (headless) {
            addArguments(options, "chrome.headless.arguments");
        }

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        for (Map.Entry<String, String> entry : parsePrefs("firefox.prefs").entrySet()) {
            String value = entry.getValue();
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                options.addPreference(entry.getKey(), Boolean.parseBoolean(value));
            } else {
                options.addPreference(entry.getKey(), value);
            }
        }

        addArguments(options, "firefox.arguments");
        if (headless) {
            addArguments(options, "firefox.headless.arguments");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        Map<String, Object> prefs = parsePrefsAsObjects("edge.prefs");
        if (!prefs.isEmpty()) {
            options.setExperimentalOption("prefs", prefs);
        }

        addArguments(options, "edge.arguments");
        if (headless) {
            addArguments(options, "edge.headless.arguments");
        }

        return new EdgeDriver(options);
    }

    // =========================================================================
    // Private – config helpers
    // =========================================================================

    /**
     * Returns the system property if set, otherwise falls back to
     * {@link ConfigReader}.
     */
    private static String resolveProperty(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        return (systemValue != null) ? systemValue
                                     : ConfigReader.getProperty(key, defaultValue);
    }

    /**
     * Reads a comma-separated list of arguments from config and adds them
     * to the given browser options.
     */
    private static void addArguments(ChromeOptions options, String configKey) {
        for (String arg : splitCsv(configKey)) {
            options.addArguments(arg);
        }
    }

    private static void addArguments(FirefoxOptions options, String configKey) {
        for (String arg : splitCsv(configKey)) {
            options.addArguments(arg);
        }
    }

    private static void addArguments(EdgeOptions options, String configKey) {
        for (String arg : splitCsv(configKey)) {
            options.addArguments(arg);
        }
    }

    /**
     * Parses a comma-separated list of {@code key=value} pairs from the given
     * config key.  Boolean-looking values are stored as {@link Boolean}.
     */
    private static Map<String, Object> parsePrefsAsObjects(String configKey) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, String> entry : parsePrefs(configKey).entrySet()) {
            String value = entry.getValue();
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                result.put(entry.getKey(), Boolean.parseBoolean(value));
            } else {
                result.put(entry.getKey(), value);
            }
        }

        return result;
    }

    /**
     * Parses a comma-separated list of {@code key=value} pairs into a map.
     */
    private static Map<String, String> parsePrefs(String configKey) {
        Map<String, String> prefs = new HashMap<>();

        for (String token : splitCsv(configKey)) {
            int eq = token.indexOf('=');
            if (eq > 0) {
                prefs.put(token.substring(0, eq).trim(), token.substring(eq + 1).trim());
            }
        }

        return prefs;
    }

    /**
     * Splits a comma-separated config value into a trimmed list,
     * filtering out blanks.
     */
    private static List<String> splitCsv(String configKey) {
        String raw = ConfigReader.getProperty(configKey, "");
        if (raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

