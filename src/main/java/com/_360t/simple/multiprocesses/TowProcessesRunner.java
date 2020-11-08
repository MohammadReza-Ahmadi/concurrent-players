package com._360t.simple.multiprocesses;

import com._360t.simple.PlayerRole;
import com._360t.simple.PlayerUtil;
import com._360t.structured.config.JarFromPomConfig;
import com._360t.util.AppUtil;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * <h3 style="color:#55A3C4"> Two Process Runner which should be called only through main method of this class or SingleProcessCallFromJarApp main method </h3>
 * <p style="color:#3F7A14">
 * This class is used to define an endpoint of execution through main method.
 * This class will be created tow instance of player object and run each of then in its own java process.
 * The main method is able to analyze input string args value and extract sending parameters.
 * The Player instance will be created base on the input parameters and run into its java thread.
 *
 * @author MohammadReza Ahmadi
 * @since 9/3/2020
 */

public class TowProcessesRunner {

    public static void main(String[] args) throws Exception {
        int port = 2022;
        int messageNumber = 10;
        String initiatorMessage = "Hi 360T";
        int delayMilliSeconds = 1000;
        String jarFileName;

        port = PlayerUtil.getArgsIntValue(args, 0, port);
        messageNumber = PlayerUtil.getArgsIntValue(args, 1, messageNumber);
        initiatorMessage = PlayerUtil.getArgsStringValue(args, 2, initiatorMessage);

        jarFileName = JarFromPomConfig.getPropertyValue("simple.two.processes.app").concat(".jar");
        System.out.println("###### Simple TwoProcessesApp is running ... ######");

        final Process initiator = getPlayerProcess(Player.class, PlayerRole.INITIATOR, port, initiatorMessage, messageNumber, delayMilliSeconds,jarFileName);
        new Thread(() -> {
            printProcessMessage(initiator);
        }).start();

        final Process receiver = getPlayerProcess(Player.class, PlayerRole.RECEIVER, port, initiatorMessage, messageNumber, delayMilliSeconds,jarFileName);
        new Thread(() -> {
            printProcessMessage(receiver);
        }).start();


        /** Wait for both processes of the program to complete its job.*/
        initiator.waitFor();
        receiver.waitFor();

    }

    /**
     * getPlayerProcess method which receive couple of parameters and create a java process and run the specified runner class
     *
     * @param clazz             The class type of the specified runner class.
     * @param playerRole        The role of player.
     * @param messageNumber     The number of messages will be exchange between both players.
     * @param delayMilliSeconds The delly milliseconds which will be exist between every message transmission.
     * @param message           The initiator message value
     * @param jarFileName       The jar file name which this program will execute through that.
     * @return a java.lang.Process instance which responsible to executing passed runner class.
     * @throws IOException The exception is throws when an error is occurred.
     */
    private static Process getPlayerProcess(Class clazz, PlayerRole playerRole, int port, String message, int messageNumber, int delayMilliSeconds, String jarFileName) throws IOException {
        return new ProcessBuilder("java", "-cp",
                AppUtil.getTargetPath(),
                AppUtil.getFullyQualifiedName(clazz),
                playerRole.toString(),
                String.valueOf(port),
                message,
                String.valueOf(messageNumber),
                String.valueOf(delayMilliSeconds))
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
