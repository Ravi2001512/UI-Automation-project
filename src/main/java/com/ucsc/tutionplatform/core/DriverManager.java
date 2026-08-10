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

public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void openBrowser() {
        String browserName = resolveProperty("browser", "chrome").toLowerCase().trim();
        boolean headless   = Boolean.parseBoolean(resolveProperty("headless", "false"));

        quitDriver();

        WebDriver driver = createDriver(browserName, headless);
        setDriver(driver);

        if (!headless) {
            try {
                driver.manage().window().maximize();
            } catch (Exception e) {
                System.err.println("Warning: Unable to maximize browser window: " + e.getMessage());
            }
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
        if (driver == null || isSessionClosed(driver)) {
            return null;
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.err.println("Warning: Error quitting driver: " + e.getMessage());
        } finally {
            DRIVER.remove();
        }
    }

    private static boolean isSessionClosed(WebDriver driver) {
        try {
            driver.getTitle();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

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

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        Map<String, Object> prefs = parsePrefsAsObjects("chrome.prefs");
        if (!prefs.isEmpty()) {
            options.setExperimentalOption("prefs", prefs);
        }

        addArguments(options, "chrome.arguments");
        if (headless) {
            options.addArguments("--headless=new");
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
            options.addArguments("-headless");
            addArguments(options, "firefox.headless.arguments");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        Map<String, Object> prefs = parsePrefsAsObjects("edge.prefs");
        if (!prefs.isEmpty()) {
            options.setExperimentalOption("prefs", prefs);
        }

        addArguments(options, "edge.arguments");
        if (headless) {
            options.addArguments("--headless=new");
            addArguments(options, "edge.headless.arguments");
        }

        return new EdgeDriver(options);
    }

    private static String resolveProperty(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        return (systemValue != null) ? systemValue
                : ConfigReader.getProperty(key, defaultValue);
    }

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