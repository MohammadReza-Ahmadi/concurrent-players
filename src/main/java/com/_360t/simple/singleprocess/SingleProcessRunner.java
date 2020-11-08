package com._360t.simple.singleprocess;

import com._360t.simple.PlayerRole;
import com._360t.simple.PlayerUtil;

import java.util.concurrent.*;

/**
 * <h3 style="color:#55A3C4"> Single Process Runner which should be called only through main method of this class or SingleProcessCallFromJarApp main method </h3>
 * <p style="color:#3F7A14">
 * This class is used to define an endpoint of execution through main method.
 * The main method is able to analyze input string args value and extract sending parameters.
 * The Player instance will be created base on the input parameters and run into its java thread.
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */


public class SingleProcessRunner {

    public static void main(String[] args) throws Exception {
        int threadNumber = 2;
        int messageNumber = 10;
        int delayMilliSeconds = 1000;
        String initiatorMessage = "Hi 360T";

        threadNumber = PlayerUtil.getArgsIntValue(args, 0, threadNumber);
        messageNumber = PlayerUtil.getArgsIntValue(args, 1, messageNumber);
        delayMilliSeconds = PlayerUtil.getArgsIntValue(args, 2, delayMilliSeconds);
        initiatorMessage = PlayerUtil.getArgsStringValue(args, 3, initiatorMessage);

        System.out.println("###### SingleProcessApp is running ... ######");
        BlockingQueue<String> sendBlockingQueue = new ArrayBlockingQueue<>(messageNumber);
        BlockingQueue<String> receiveBlockingQueue = new ArrayBlockingQueue<>(messageNumber);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch countDownLatch = new CountDownLatch(messageNumber);

        Player sender = new Player(sendBlockingQueue, receiveBlockingQueue, countDownLatch, PlayerRole.INITIATOR, initiatorMessage, delayMilliSeconds);
        Player receiver = new Player(sendBlockingQueue, receiveBlockingQueue, countDownLatch, PlayerRole.RECEIVER, initiatorMessage, delayMilliSeconds);

        for (int i = 1; i <= messageNumber; i++) {
            executorService.execute(new Thread(sender::sendMessage));
            executorService.execute(new Thread(receiver::receiveMessage));
        }

        countDownLatch.await();
        executorService.shutdown();
        if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            System.out.println("\n Program execution is done. ");
        }
    }
}


