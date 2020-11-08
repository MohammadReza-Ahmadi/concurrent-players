package com._360t.structured.app;

import com._360t.structured.log.config.LogConfig;


/**
 * <h3 style="color:#55A3C4"> A supper class of all Application classes </h3>
 * <p style="color:#3F7A14">
 * This is a supper class by a static block which each application class of program extends this class
 * and after running each of them this static block will be called doConfig() method to config root logger object of the program.
 * @see LogConfig#doConfig() method
 * @author MohammadReza Ahmadi
 * @since 9/11/2020
 */

public class App {

    /**
     * This is a static block which will be executed when an application class is ran.
     */
    static {
        LogConfig.doConfig();
    }
}
