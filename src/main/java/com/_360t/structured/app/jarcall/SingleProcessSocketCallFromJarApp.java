package com._360t.structured.app.jarcall;

import com._360t.simple.PlayerUtil;
import com._360t.structured.app.App;
import com._360t.structured.app.SingleProcessSocketApp;
import com._360t.structured.apprunner.TwoProcessInitiatorSocketRunner;
import com._360t.structured.apprunner.TwoProcessReceiverSocketRunner;
import com._360t.structured.config.JarFromPomConfig;
import com._360t.structured.enumeration.PlayerRole;
import com._360t.structured.model.Player;
import com._360t.structured.model.SendMessage;
import com._360t.structured.model.template.MessageModel;
import com._360t.structured.model.template.PlayerModel;
import com._360t.structured.model.template.ProcessorModel;
import com._360t.structured.service.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3 style="color:#55A3C4"> Single Process Socket Application that should be called through the jar file </h3>
 * <p style="color:#3F7A14">
 * This class is created to run the Player application in the single process mode.
 * The main idea behind this application class is able to define independent instance of different class types
 * to provide a unified application instance to execute the player application.
 * The player application architecture is based on Observer design pattern by the help of template method
 * and also delegation design pattern.
 *
 * <p style="color:#3F7A14">
 * Combination of these three pattern make it possible to assemble variety of Processors, Publishers and Subscribers
 * with each other and make a player messaging application without any coupling between different classes and also
 * keep coherence of unified desired business rolls.
 *
 * <p style="color:#3F7A14">
 * It is simple to create a new player application by a different subscription implementation in Processor, Publisher or Subscriber class.
 * For example it is possible to make a new Processor class by the another implementation of IPC or Inter Process Communication.
 * Imagine we could have a Processor class which utilizing File and new IO channel file to implementing IPC.
 * Or another example is possibility to implement udp socket connection.
 *
 *  <p style="color:#3F7A14">
 *  Finally, all of above implementations are possible by assembling different types of classes
 *  like: PlayerModel, MessageModel, ProcessorModel, SubscriptionModel, MessageOperationDelegation and MessageProcessTemplate
 *  without any obligations to make a change to any of the super classes or change the implementation of concrete classes.
 *
 *  By the help of these possibilities couple of application classes is created.
 *  Application classes like: TwoProcessSocketApp and SingleProcessSubmissionApp.
 *
 * @see PlayerModel
 * @see MessageModel
 * @see ProcessorModel
 * @see com._360t.structured.model.template.SubscriptionModel
 * @see com._360t.structured.model.template.MessageOperationDelegation
 * @see com._360t.structured.model.template.MessageProcessTemplate
 *
 * @author MohammadReza Ahmadi
 * @since 9/10/2020
 */

public class SingleProcessSocketCallFromJarApp extends App {
    private static Logger logger = Logger.getLogger(SingleProcessSocketCallFromJarApp.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        int port = 2020;
        String initiatorMessage = "Hi-360T";
        int messageNumber = 10;
        int delayMilliSeconds = 1000;
        String jarFileName;


        /** check four input args parameter to assign correct value to the related variable
         * and change the application behaviour based on input parameters.
         * This block of code is useful when this application is called through the java jar file.
         * You could run this class without any input parameter which means all default above parameters
         * value will be passed to the runner class of this application class.
         * */
        if ((port = PlayerUtil.getArgsIntValue(args, 0, port)) > 0) {
        }
        if ((initiatorMessage = PlayerUtil.getArgsStringValue(args, 1, initiatorMessage)) != null) {
        }
        if ((messageNumber = PlayerUtil.getArgsIntValue(args, 2, messageNumber)) > 0) {
        }
        if ((delayMilliSeconds = PlayerUtil.getArgsIntValue(args, 3, delayMilliSeconds)) > 0) {
        }
        if ((jarFileName = PlayerUtil.getArgsStringValue(args, 4)).isEmpty()) {
            jarFileName = JarFromPomConfig.getPropertyValue("structured.two.processes.app").concat(".jar");
        }

        Process singleProcessSocketAppProcess = null;
        try {
            logger.setLevel(Level.FINE);
            logger.log(Level.FINE, "{0} is started in single processes mode by parameters: port:[{1}], message:[{2}], messageNumber:[{3}] and delayMilliSeconds:[{4}] :::", new Object[]{SingleProcessSocketCallFromJarApp.class.getSimpleName(), port, initiatorMessage, messageNumber, delayMilliSeconds});

            /** create a java process as the singleProcessSocketAppProcess and run the SingleProcessSocketApp into that process */
            singleProcessSocketAppProcess = getPlayerProcess(SingleProcessSocketApp.class, port, initiatorMessage, messageNumber, delayMilliSeconds, jarFileName);
            Process finalSingleProcessSocketAppProcess = singleProcessSocketAppProcess;
            new Thread(() -> {
                printProcessMessage(finalSingleProcessSocketAppProcess);
            }).start();

            finalSingleProcessSocketAppProcess.waitFor();

        } catch (IOException e) {
            singleProcessSocketAppProcess.destroy();
            e.printStackTrace();
        } finally {
            singleProcessSocketAppProcess.destroy();
        }

    }


    /**
     * getPlayerProcess method which receive couple of parameters and create a java process and run the specified runner class
     *
     * @param clazz             The class type of the specified runner class
     * @param port              The specified port which ServerSocket and Socket instances is initiated based on.
     * @param message           The initiator message value
     * @param messageNumber     The number of messages will be exchange between both players.
     * @param delayMilliSeconds The delly milliseconds which will be exist between every message transmission.
     * @param jarFileName       The jar file name which this program will execute through that.
     * @return a java.lang.Process instance which responsible to executing passed runner class.
     * @throws IOException The exception is throws when an error is occurred.
     */
    private static Process getPlayerProcess(Class clazz, int port, String message, int messageNumber, int delayMilliSeconds, String jarFileName) throws IOException {
        String jarFileDirectory = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        /** resolve jar file location in the host operation system*/
        jarFileDirectory = jarFileDirectory.split("([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.jar)")[0];

        /** resolve jar file name based on input parameter name or JarFromConfig property file which is created from maven pom file*/
        jarFileName = (jarFileName == null || jarFileName.isEmpty()) ? JarFromPomConfig.getPropertyValue("structured.single.processes.app").concat(".jar") : jarFileName;

        return new ProcessBuilder("java", "-cp",
                jarFileName,
                clazz.getName(),
                String.valueOf(port),
                message,
                String.valueOf(messageNumber),
                String.valueOf(delayMilliSeconds))
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
