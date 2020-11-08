package com._360t.simple.singleprocess;

import com._360t.simple.PlayerRole;

import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h3 style="color:#55A3C4"> Player class which act as sender and receiver of each message </h3>
 * <p style="color:#3F7A14">
 * This class is used to act as a concrete Player class which handle send and receive messages
 * This class also handle message transformation
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public class Player {
    private final BlockingQueue<String> sendBlockingQueue;
    private final BlockingQueue<String> receiveBlockingQueue;
    private final CountDownLatch countDownLatch;
    private final ReentrantLock locker = new ReentrantLock();
    private final PlayerRole playerRole;
    private final String message;
    private final long messageNumber;
    private int delayMilliSeconds;

    /**
     *
     * @param sendBlockingQueue is used as a message subscription to maintain sending message of each player
     * @param receiveBlockingQueue is used as a message subscription to maintain receiving message of each player
     * @param countDownLatch is used to control number of exchanging messages
     * @param playerRole is used to define each player role
     * @param initiatorMessage is a initiator message
     * @param delayMilliSeconds is used to make a delay between each message exchanging
     */
    public Player(BlockingQueue<String> sendBlockingQueue, BlockingQueue<String> receiveBlockingQueue, CountDownLatch countDownLatch, PlayerRole playerRole, String initiatorMessage, int delayMilliSeconds) {
        this.sendBlockingQueue = sendBlockingQueue;
        this.receiveBlockingQueue = receiveBlockingQueue;
        this.countDownLatch = countDownLatch;
        this.messageNumber = countDownLatch.getCount();
        this.playerRole = playerRole;
        this.message = initiatorMessage;
        this.delayMilliSeconds = delayMilliSeconds;
        System.out.println(String.format("<<%s>> is created base on parameter: {processId:[%s], initiatorMessage:[%s], messageNumber:[%d], delayMilliSeconds:[%d]}", playerRole.toString(), getProcessId(), initiatorMessage, messageNumber, delayMilliSeconds));
    }

    /**
     * @return String value of current process id
     */
    private static String getProcessId() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    /**
     * handle sending message of each player, sender or receiver
     */
    public void sendMessage() {
        locker.lock();
        try {
            long messageNum = (messageNumber - countDownLatch.getCount() + 1);
            System.out.println();
            System.out.println(String.format("%d- <<%s>> sent a message:{%s} :::: in threadId: %s and processId: %s ", messageNum, playerRole, message, Thread.currentThread().getId(), getProcessId()));
            sendBlockingQueue.put(message);
            String receivedMessage = receiveBlockingQueue.take();
            System.out.println(String.format("    <<%s>> received a message:{%s} :::: in threadId: %s and processId: %s ", playerRole, receivedMessage, Thread.currentThread().getId(), getProcessId()));
            Thread.sleep(delayMilliSeconds);
            locker.unlock();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * handle receiving message of each player, sender or receiver
     */
    public void receiveMessage() {
        locker.lock();
        try {
            String message = sendBlockingQueue.take();
            System.out.println(String.format("    <<%s>> received-1 a message:{%s} :::: in threadId: %s and processId: %s ", playerRole, message, Thread.currentThread().getId(), getProcessId()));
            countDownLatch.countDown();
            message = message + "-" + (messageNumber - countDownLatch.getCount());
            receiveBlockingQueue.put(message);
            System.out.println(String.format("    <<%s>> sent a message:{%s} :::: in threadId: %s and processId: %s ", playerRole, message, Thread.currentThread().getId(), getProcessId()));
            Thread.sleep(delayMilliSeconds);
            locker.unlock();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
