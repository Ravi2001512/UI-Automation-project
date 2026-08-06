package com.ucsc.tutionplatform.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads {@code config.properties} from the test classpath once, at class-load time.
 * All accessors are thread-safe because {@link Properties} is loaded before any
 * test thread touches it.
 */
public final class ConfigReader {

    private static final String     CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES  = loadProperties();

    private ConfigReader() {
    }

    // -------------------------------------------------------------------------
    // Public accessors
    // -------------------------------------------------------------------------

    /**
     * Returns the value of {@code key}.
     *
     * @throws IllegalArgumentException if the key is absent
     */
    public static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException("Config property not found: " + key);
        }

        return value;
    }

    /**
     * Returns the value of {@code key}, or {@code defaultValue} if absent.
     * Passing {@code null} as the default is allowed and will be returned verbatim.
     */
    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Returns the integer value of {@code key}, or {@code defaultValue} if absent / blank.
     *
     * @throws IllegalArgumentException if the value cannot be parsed as an integer
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Config property must be an integer: " + key, exception);
        }
    }

    /**
     * Returns the boolean value of {@code key}, or {@code defaultValue} if absent / blank.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value.trim());
    }

    // -------------------------------------------------------------------------
    // Private – loading
    // -------------------------------------------------------------------------

    private static Properties loadProperties() {
        Properties properties = new Properties();
        ClassLoader classLoader = ConfigReader.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Config file not found in resources: " + CONFIG_FILE);
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read config file: " + CONFIG_FILE, exception);
        }
    }
}