package com._360t.structured.config;

import com._360t.structured.log.config.LogConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <h1>Config global logger object</h1>
 * The LogConfig class get root logger and create a new console handler by set a custom Formatter object called LogFormatter
 * and also it disables parent handlers.
 * <p>
 * {@link LogConfig#doConfig()} method is called in static block of App class.
 *
 * @author MohammadReza Ahmadi
 * @see com._360t.structured.model.template.MessageModel
 * @since 9/4/2020
 */

public class JarFromPomConfig {

    private static String configFileName = "jar-configs-from-pom.properties";
    private static Properties prop;


    public static String getPropertyValue(String key) {
        if (prop != null)
            return prop.getProperty(key);

            try (InputStream input = JarFromPomConfig.class.getClassLoader().getResourceAsStream(configFileName)) {
                if (input == null) {
                    System.out.println("Unable to find config.properties");
                }

                prop = new Properties();
                prop.load(input);

            } catch (IOException e) {
                e.printStackTrace();
            }

        return prop.getProperty(key);
    }
}
