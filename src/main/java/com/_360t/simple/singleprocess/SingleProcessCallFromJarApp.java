package com._360t.simple.singleprocess;

import com._360t.simple.PlayerUtil;
import com._360t.structured.app.jarcall.SingleProcessSocketCallFromJarApp;
import com._360t.structured.config.JarFromPomConfig;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3 style="color:#55A3C4"> Single Process App which should be called only through existing jar file of application </h3>
 * <p style="color:#3F7A14">
 * This class is used to define an endpoint of execution through main method.
 * The main method is able to analyze input string args value and extract sending parameters
 * and send its parameter to the runner class.
 * The runner class will be created base on the input parameters and run into its java process.
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public class SingleProcessCallFromJarApp {
    private static Logger logger = Logger.getLogger(SingleProcessCallFromJarApp.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        int threadNumber = 2;
        int messageNumber = 10;
        int delayMilliSeconds = 1000;
        String initiatorMessage = "Hi-360T";
        String jarFileName;

        threadNumber = PlayerUtil.getArgsIntValue(args, 0, threadNumber);
        messageNumber = PlayerUtil.getArgsIntValue(args, 1, messageNumber);
        delayMilliSeconds = PlayerUtil.getArgsIntValue(args, 2, delayMilliSeconds);
        initiatorMessage = PlayerUtil.getArgsStringValue(args, 3, initiatorMessage);
        if ((jarFileName = PlayerUtil.getArgsStringValue(args, 4)).isEmpty()) {
            jarFileName = JarFromPomConfig.getPropertyValue("simple.single.processes.app").concat(".jar");
        }


        Process singleProcessApll = null;
        try {
            logger.setLevel(Level.FINE);
            logger.log(Level.FINE, "{0} is started in single processes mode by parameters: message:[{1}], messageNumber:[{2}] and delayMilliSeconds:[{3}] :::", new Object[]{SingleProcessSocketCallFromJarApp.class.getSimpleName(), initiatorMessage, messageNumber, delayMilliSeconds});

            /** create a java process as the singleProcessApp and run the singleProcessRunner into that process */
            singleProcessApll = getPlayerProcess(SingleProcessRunner.class, threadNumber, messageNumber, delayMilliSeconds, initiatorMessage,jarFileName);
            Process finalSingleProcessApp = singleProcessApll;
            new Thread(() -> {
                printProcessMessage(finalSingleProcessApp);
            }).start();

            finalSingleProcessApp.waitFor();

        } catch (IOException e) {
            singleProcessApll.destroy();
            e.printStackTrace();
        } finally {
            singleProcessApll.destroy();
        }

    }

    /**
     * getPlayerProcess method which receive couple of parameters and create a java process and run the specified runner class
     *
     * @param clazz             The class type of the specified runner class.
     * @param threadNumber The number of thread which a executor is created.
     * @param messageNumber     The number of messages will be exchange between both players.
     * @param delayMilliSeconds The delly milliseconds which will be exist between every message transmission.
     * @param initiatorMessage           The initiator message value
     * @param jarFileName       The jar file name which this program will execute through that.
     * @return a java.lang.Process instance which responsible to executing passed runner class.
     * @throws IOException The exception is throws when an error is occurred.
     */

    private static Process getPlayerProcess(Class clazz, int threadNumber, int messageNumber, int delayMilliSeconds, String initiatorMessage, String jarFileName) throws IOException {
        String jarFileDirectory = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        /** resolve jar file location in the host operation system*/
        jarFileDirectory = jarFileDirectory.split("([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.jar)")[0];

        /** resolve jar file name based on input parameter name or JarFromConfig property file which is created from maven pom file*/
        jarFileName = (jarFileName == null || jarFileName.isEmpty()) ? JarFromPomConfig.getPropertyValue("structured.single.processes.app").concat(".jar") : jarFileName;

        return new ProcessBuilder("java", "-cp",
                jarFileName,
                clazz.getName(),
                String.valueOf(threadNumber),
                String.valueOf(messageNumber),
                String.valueOf(delayMilliSeconds),
                initiatorMessage)
                .directory(new File(jarFileDirectory))
                .inheritIO()
                .start();
    }

    /**
     * @param process of each player or processor execution which will be send to this method to read its input stream.
     */
    private static void printProcessMessage(Process process) {
        try (Scanner scanner = new Scanner(process.getInputStream())) {
            while (scanner.hasNext())
                System.out.println(scanner.nextLine());
        }
    }
}


