package com._360t.structured.service;

import com._360t.structured.constant.AppConstants;
import com._360t.structured.enumeration.SubscriptionStatus;
import com._360t.structured.model.MessageSubscription;
import com._360t.structured.model.template.MessageModel;
import com._360t.structured.model.template.MessageProcessTemplate;
import com._360t.structured.model.template.ProcessorModel;
import com._360t.structured.model.template.SubscriptionModel;
import com._360t.util.AppUtil;
import com._360t.util.MessageConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <h3 style="color:#55A3C4"> Implement Java Socket functionality in the form of a Flow.Processor class </h3>
 * <p style="color:#3F7A14">
 * This class is one of the implementations of the ProcessorModel interface that has inherited the behavior of class MessageTemplate at the same time.
 * By inheriting from MessageTemplate class, the whole process of sending and receiving messages
 * is controlled by MessageTemplate class and the main activity of ClientSocketPublisherSubscriber class
 * is to exchange messages by the help of java Socket class.
 * @see MessageProcessTemplate
 * @see ProcessorModel
 * @see java.net.Socket
 *
 * @author MohammadReza Ahmadi
 * @since 9/10/2020
 */

public class ClientSocketPublisherSubscriber extends MessageProcessTemplate implements ProcessorModel {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final int port;
    private final int delayMilliSeconds;
    private CountDownLatch countDownLatch;
    private SubscriptionModel subscriberSubscription;
    private Socket socket;
    private PrintWriter output;
    private Scanner input;
    private Flow.Subscriber subscriber;
    private Flow.Subscription subscription;
    private SubscriptionStatus subscriptionStatus;

    /**
     * Constructor of this class which calls the initiateSocket() method to initialize a java socket in requested port and send first message.
     * @param port int value which could be sent by the runner class to initializing a java socket on this port number.
     * @param delayMilliSeconds int value which could be sent by the runner class to interrupt the sending messages.
     */
    public ClientSocketPublisherSubscriber(int port, int delayMilliSeconds) {
        this.port = port;
        this.delayMilliSeconds = delayMilliSeconds;
        this.subscriberSubscription = new MessageSubscription();
        this.subscriptionStatus = SubscriptionStatus.RUNNING;
        new Thread(this::initiateSocket).start();
    }

    /**
     * Initializing java Socket and send first message.
     */
    private void initiateSocket() {
        try {
            this.socket = new Socket(AppConstants.HOST, port);
            output = new PrintWriter(socket.getOutputStream(), true);
            this.input = new Scanner(socket.getInputStream());

            String messageString = readSocket();
            MessageModel message = MessageConverter.getStringAsMessage(messageString);
            sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MessageProcessTemplate#getCountDownLatch() method
     * @return countDownLatch of processor.
     */
    @Override
    protected CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * Set the value of countDownLatch.
     * This field is not define as a constructor parameter because to maintain class independence.
     * @param countDownLatch
     */
    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    /**
     * @see MessageProcessTemplate#getSubscriberSubscription() method
     * @return Subscription of publisher.
     */
    @Override
    protected SubscriptionModel getSubscriberSubscription() {
        return subscriberSubscription;
    }

    /**
     * @see MessageProcessTemplate#receiveMessage(MessageModel)  method
     * @param message receiving message instance
     */
    protected void receiveMessage(MessageModel message) {
        logReceiveMessage(message);
        subscription.request(1);
    }

    /**
     * @see MessageProcessTemplate#sendMessage(MessageModel) method
     * @param message sent message instance
     */
    @Override
    protected void sendMessage(MessageModel message) {
        delay();
        logSendMessage(message);
        //noinspection unchecked
        subscriber.onNext(message);
    }

    /**
     * @see ProcessorModel#submit(MessageModel) method
     * @param message MessageModel instance which is sent by player.
     */
    @Override
    public void submit(MessageModel message) {
        sendMessage(message);
    }

    /**
     * @see Flow.Publisher method
     * @param subscriber Subscriber instance which is subscribe itself on this Publisher class.
     */
    @Override
    public void subscribe(Flow.Subscriber subscriber) {
        this.subscriber = subscriber;
        subscriber.onSubscribe(subscriberSubscription);
    }

    /**
     * @see Flow.Subscriber
     * @param subscription Subscription instance of Publisher class which will be send to the Subscriber instance after done its subscription.
     */
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    /**
     * @see Flow.Subscriber
     * @param messageModel MessageModel instance which will be received when a Publisher wants to provide data for its Subscriber class.
     */
    @Override
    public void onNext(MessageModel messageModel) {
        handleReceiveMessage(messageModel);
    }

    /**
     * @see MessageProcessTemplate#afterReceiveMessage(MessageModel) method
     * @param message received message instance
     */
    @Override
    protected void afterReceiveMessage(MessageModel message) {
        writeSocket(message);
    }

    /**
     * @see MessageProcessTemplate#beforeSendMessage(MessageModel) method
     * @param message MessageModel instance which will be produce to sending to other Player.
     * @return
     */
    @Override
    protected MessageModel beforeSendMessage(MessageModel message) {
        if (isComplete()) {
            return message;
        }
        String messageString = readSocket();
        return MessageConverter.getStringAsMessage(messageString);
    }

    /**
     * @see MessageProcessTemplate#delay() method
     * Sleep the executing thread of the program for the given delayMilliSeconds.
     */
    @Override
    public void delay() {
        try {
            Thread.sleep(delayMilliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param throwable Throwable parameter which is received when an error occurred during the message processing.
     */
    @Override
    public void onError(Throwable throwable) {
        defaultOnError(throwable, logger);
    }

    /**
     * This method is used to complete message exchanging and close the resources like socket connection.
     */
    @Override
    public void onComplete() {
        if (isNotStopped()) {
            delay();
            logger.setLevel(Level.FINE);
            logger.log(Level.FINE, "{0} job is completed. ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{this.getClass().getSimpleName(),Thread.currentThread().getId(),AppUtil.getProcessId()});
            stopSubscriptionStatus();
            close();
            subscriber.onComplete();
        }
    }

    private String readSocket(){
        String messageString;
        while (input.hasNextLine()){
            messageString = input.nextLine();
            logger.setLevel(Level.FINE);
            logger.log(Level.FINE, "{0} reads socket message:{1} ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{this.getClass().getSimpleName(),messageString,Thread.currentThread().getId(),AppUtil.getProcessId()});
            return messageString;
        }
        return null;
    }

    private void writeSocket(MessageModel messageModel) {
        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} writes socket message:{1} ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{this.getClass().getSimpleName(),messageModel.getAsString(),Thread.currentThread().getId(),AppUtil.getProcessId()});
        output.println(messageModel.getAsString());
    }

    @SuppressWarnings("unchecked")
    private void logSendMessage(MessageModel messageModel) {
        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} sends message:{1} ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{this.getClass().getSimpleName(),messageModel.getAsString(),Thread.currentThread().getId(),AppUtil.getProcessId()});
    }

    private void logReceiveMessage(MessageModel messageModel) {
        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} receives message:{1} ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{this.getClass().getSimpleName(),messageModel.getAsString(),Thread.currentThread().getId(),AppUtil.getProcessId()});
    }

    private void close() {
        try {
            delay();
            input.close();
            output.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
