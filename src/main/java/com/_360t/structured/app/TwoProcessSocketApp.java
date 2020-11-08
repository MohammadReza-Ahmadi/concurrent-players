package com._360t.structured.app;

import com._360t.simple.PlayerUtil;
import com._360t.structured.apprunner.TwoProcessInitiatorSocketRunner;
import com._360t.structured.apprunner.TwoProcessReceiverSocketRunner;
import com._360t.structured.config.JarFromPomConfig;
import com._360t.structured.model.template.MessageModel;
import com._360t.structured.model.template.PlayerModel;
import com._360t.structured.model.template.ProcessorModel;
import com._360t.util.AppUtil;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3 style="color:#55A3C4"> Two Processes Socket Application that main method of this class should be called to run the program </h3>
 * <p style="color:#3F7A14">
 * This class is created to run the Player application in the two processes mode.
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

public class TwoProcessSocketApp extends App {
    private static Logger logger = Logger.getLogger(TwoProcessSocketApp.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        int port = 2020; //The specified port which ServerSocket and Socket instances is initiated based on.
        String initiatorMessage = "Hi-360T"; // The initiator message value
        int messageNumber = 10; //The number of messages will be exchange between both players.
        int delayMilliSeconds = 1000; //The delly milliseconds which will be exist between every message transmission.

        Process initiatorProcess = null;
        Process receiverProcess = null;
        try {
            logger.setLevel(Level.FINE);
            logger.log(Level.FINE, "{0} is started in two processes mode by parameters: port:[{1}], message:[{2}], messageNumber:[{3}] and delayMilliSeconds:[{4}] :::", new Object[]{TwoProcessSocketApp.class.getSimpleName(), port, initiatorMessage,messageNumber, delayMilliSeconds});

            /** create a java process as the initiatorProcess and run the TwoProcessInitiatorSocketRunner into that process */
            initiatorProcess = getPlayerProcess(TwoProcessInitiatorSocketRunner.class, port, initiatorMessage, messageNumber, delayMilliSeconds);
            Process finalInitiatorProcess = initiatorProcess;
            new Thread(() -> {
                printProcessMessage(finalInitiatorProcess);
            }).start();

            /** create a java process as the receiverProcess and run the TwoProcessInitiatorSocketRunner into that process */
            receiverProcess = getPlayerProcess(TwoProcessReceiverSocketRunner.class, port, initiatorMessage, messageNumber, delayMilliSeconds);
            Process finalReceiverProcess = receiverProcess;
            new Thread(() -> {
                printProcessMessage(finalReceiverProcess);
            }).start();

            /** Wait for both processes of the program to complete its job.*/
            initiatorProcess.waitFor();
            receiverProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            initiatorProcess.destroy();
            receiverProcess.destroy();
        }
    }


    /**
     * getPlayerProcess method which receive couple of parameters and create a java process and run the specified runner class
     * @param clazz The class type of the specified runner class
     * @param port The specified port which ServerSocket and Socket instances is initiated based on.
     * @param message The initiator message value.
     * @param messageNumber The number of messages will be exchange between both players.
     * @param delayMilliSeconds The delly milliseconds which will be exist between every message transmission.
     * @return a java.lang.Process instance which responsible to executing passed runner class.
     * @throws IOException The exception is throws when an error is occurred.
     */
    private static Process getPlayerProcess(Class clazz, int port, String message, int messageNumber, int delayMilliSeconds) throws IOException {
        return new ProcessBuilder("java", "-cp",
                AppUtil.getTargetPath(),
                AppUtil.getFullyQualifiedName(clazz),
                String.valueOf(port),
                message,
                String.valueOf(messageNumber),
                String.valueOf(delayMilliSeconds))
                .inheritIO()
                .start();
    }

    private static void printProcessMessage(Process process) {
        try (Scanner scanner = new Scanner(process.getInputStream())) {
            while (scanner.hasNext())
                System.out.println(scanner.nextLine());
        }
    }
}
