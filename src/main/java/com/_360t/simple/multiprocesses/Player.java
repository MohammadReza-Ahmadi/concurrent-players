package com._360t.simple.multiprocesses;

import com._360t.simple.PlayerUtil;
import com._360t.structured.enumeration.PlayerRole;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * <h3 style="color:#55A3C4"> Player class which act as sender and receiver of each message </h3>
 * <p style="color:#3F7A14">
 * This class is used to act as a concrete Player class which handle send and receive messages
 * This class also handle message transformation
 *
 * @author MohammadReza Ahmadi
 * @since 9/3/2020
 */

public class Player {
    private final String host = "localhost";
    private final PlayerRole playerRole;
    private final String initiatorMessage;
    private final int messageNumber;
    private final int delayMilliSeconds;
    private final CountDownLatch countDownLatch;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private ServerSocket serverSocket;
    private Socket socket;


    public Player(PlayerRole playerRole, int port, String initiatorMessage, int messageNumber, int delayMilliSeconds) throws IOException, InterruptedException {
        System.out.println(String.format("<<%s>> is created base on parameter: {processId:[%s], port:[%d], initiatorMessage:[%s], messageNumber:[%d], delayMilliSeconds:[%d]}", playerRole.toString(), getProcessId(), port, initiatorMessage, messageNumber, delayMilliSeconds));

        if (playerRole.isInitiator()) {
            this.serverSocket = new ServerSocket(port);
            this.socket = serverSocket.accept();

        } else if (playerRole.isReceiver()) {
            this.serverSocket = null;
            this.socket = new Socket(host, port);

        } else {
            this.serverSocket = null;
            this.socket = null;
        }

        this.playerRole = playerRole;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

        this.initiatorMessage = initiatorMessage;
        this.messageNumber = messageNumber;
        this.countDownLatch = new CountDownLatch(messageNumber);
        this.delayMilliSeconds = delayMilliSeconds;
    }

    public static void main(String[] args) throws Exception {
        PlayerRole playerRole;
        int port = 0;
        String initiatorMessage;
        int messageNumber;
        int delayMilliSeconds;

        if ((playerRole = PlayerUtil.getPlayerRole(args, 0)).isNotUndefined() &&
                (port = PlayerUtil.getArgsIntValue(args, 1)) > 0 &&
                (messageNumber = PlayerUtil.getArgsIntValue(args, 3)) > 0 &&
                (delayMilliSeconds = PlayerUtil.getArgsIntValue(args, 4)) > 0) {

            initiatorMessage = PlayerUtil.getArgsStringValue(args, 2);
            final Player player = new Player(playerRole, port, initiatorMessage, messageNumber, delayMilliSeconds);

            if (player.playerRole.isInitiator())
                player.sendMessage();

            if (player.playerRole.isReceiver())
                player.receiveMessage();

            return;
        }

        throw new IllegalArgumentException(String.format("Passed arguments are not valid! playerRole:[%s] & port:[%d]", playerRole.toString(), port));
    }


    private void sendMessage() throws IOException, InterruptedException {
        String message = initiatorMessage;
        Thread.sleep(delayMilliSeconds);
        long messageNum = (messageNumber - countDownLatch.getCount()) + 1;
        if (playerRole.isReceiver()) {
            message = String.format("%s-%d", initiatorMessage, messageNum);
            countDownLatch.countDown();
            System.out.print("    ");
        } else {
            System.out.println();
            System.out.print(messageNum + "- ");
        }

        System.out.println(String.format("<<%s>> sent a message:{%s} :::: in threadId: %s and processId: %s ", playerRole.toString(), message, Thread.currentThread().getId(), getProcessId()));
        dataOutputStream.writeUTF(message);
        if(countDownLatch.getCount()==0)
            return;
        receiveMessage();
    }


    private void receiveMessage() throws IOException, InterruptedException {
        String message = dataInputStream.readUTF();
        System.out.println(String.format("    <<%s>> received a message:{%s} :::: in threadId: %s and processId: %s ", playerRole.toString(), message, Thread.currentThread().getId(), getProcessId()));

        if (playerRole.isInitiator())
            countDownLatch.countDown();

        if (countDownLatch.getCount() <= 0) {
            closeProgram();
        } else {
            sendMessage();
        }
    }

    public void closeProgram() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
//        if (playerRole.isReceiver())
            socket.close();
//        if (playerRole.isInitiator())
            serverSocket.close();
        System.out.println("\n Program execution is done. ");
    }

    private String getProcessId() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

}
