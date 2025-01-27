package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private final static Properties PROPERTIES = new Properties();
    private PropertiesUtil() {}

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
    static {
        loadProperties();
    }

}
