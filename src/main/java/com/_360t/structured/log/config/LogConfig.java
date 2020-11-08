package com._360t.structured.log.config;

import java.util.logging.*;

/**
 * <h3 style="color:#55A3C4"> Config global logger object </h3>
 * <p style="color:#3F7A14">
 * The LogConfig class get root logger and create a new console handler by set a custom Formatter object called LogFormatter
 * and also it disables parent handlers.
 * {@link LogConfig#doConfig()} method is called in static block of App class.
 *
 *
 * @author MohammadReza Ahmadi
 * @since 9/11/2020
 */

public class LogConfig {

    /**
     * This method is used to create a console handler
     * and create a LogFormatter class that is a customized Formatter class {@link java.util.logging.Formatter}
     * and set created console handler to the rootLogger of all application Logger objects.
     *
     * @see LogFormatter
     */
    public static void doConfig() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler consoleHandler = new ConsoleHandler();
        Formatter formatter = new LogFormatter();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(Level.ALL);
        rootLogger.addHandler(consoleHandler);
        rootLogger.setUseParentHandlers(false);
/*
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setFormatter(formatter);
            handler.setLevel(Level.FINE);
        }
*/
    }
}
