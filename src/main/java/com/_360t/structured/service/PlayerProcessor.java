package com._360t.structured.service;

import com._360t.structured.enumeration.SubscriptionStatus;
import com._360t.structured.log.LogUtil;
import com._360t.structured.model.MessageSubscription;
import com._360t.structured.model.template.*;
import com._360t.util.AppUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author MohammadReza Ahmadi
 * 9/10/2020, 7:23 PM
 */

public class PlayerProcessor extends MessageProcessTemplate implements ProcessorModel {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final PlayerModel player;
    private final int delayMilliSeconds;
    private Function<MessageModel, MessageModel> messagesTransformer;
    private CountDownLatch countDownLatch;
    private SubscriptionModel subscriberSubscription;
    private Flow.Subscriber subscriber;
    private Flow.Subscription subscription;


    /**
     * Constructor of PlayerProcessor which instantiate a new PlayerProcessor class based on input parameters.
     * @param player PlayerModel type parameter which will be a instance of Player class.
     * @param delayMilliSeconds int value which could be sent by the runner class to interrupt the sending messages.
     */
    public PlayerProcessor(PlayerModel player, int delayMilliSeconds) {
        this.player = player;
        this.delayMilliSeconds = delayMilliSeconds;
        this.subscriberSubscription = new MessageSubscription();
    }

    /**
     * Constructor of PlayerProcessor which instantiate a new PlayerProcessor class based on input parameters.
     * @param player PlayerModel type parameter which will be a instance of Player class.
     * @param messagesTransformer Function type parameter which will be passed through the constructor to transforming sending messages.
     * @param delayMilliSeconds int value which could be sent by the runner class to interrupt the sending messages.
     */
    public PlayerProcessor(PlayerModel player, Function<MessageModel, MessageModel> messagesTransformer, int delayMilliSeconds) {
        this.player = player;
        this.messagesTransformer = messagesTransformer;
        this.delayMilliSeconds = delayMilliSeconds;
        this.subscriberSubscription = new MessageSubscription();
    }

    /**
     * Constructor of PlayerProcessor which instantiate a new PlayerProcessor class based on input parameters.
     * @param player PlayerModel type parameter which will be a instance of Player class.
     * @param messagesTransformer Function type parameter which will be passed through the constructor to transforming sending messages.
     * @param countDownLatch CountDownLatch type parameter which will be passed through the constructor to control number of exchanged messages.
     * @param delayMilliSeconds int value which could be sent by the runner class to interrupt the sending messages.
     */
    public PlayerProcessor(PlayerModel player, Function<MessageModel, MessageModel> messagesTransformer,CountDownLatch countDownLatch, int delayMilliSeconds) {
        this.player = player;
        this.messagesTransformer = messagesTransformer;
        this.delayMilliSeconds = delayMilliSeconds;
        this.countDownLatch = countDownLatch;
        this.subscriberSubscription = new MessageSubscription();
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
     * MessageProcessTemplate abstraction methods part
     */

    @Override
    protected Function<MessageModel, MessageModel> getMessageTransformer() {
        return messagesTransformer;
    }

    /**
     * @see MessageProcessTemplate#receiveMessage(MessageModel)  method
     * @param message receiving message instance
     */
    @Override
    protected void receiveMessage(MessageModel message) {
        player.receiveMessage(message);
        subscription.request(1);
    }

    /**
     * @see MessageProcessTemplate#beforeSendMessage(MessageModel) method
     * @param message MessageModel instance which will be produce to sending to other Player.
     * @return
     */
    @Override
    protected MessageModel beforeSendMessage(MessageModel message) {
        return message;
    }

    /**
     * @see MessageProcessTemplate#sendMessage(MessageModel) method
     * @param message sent message instance
     */
    @Override
    protected void sendMessage(MessageModel message) {
        player.sendMessage(message);
        //noinspection unchecked
        subscriber.onNext(message);
    }


    /**
     * @see ProcessorModel#submit(MessageModel) method
     * @param message MessageModel instance which is sent by player.
     */
    @Override
    public void submit(MessageModel message) {
        handleSendMessage(message);
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
//            delay();
            logger.setLevel(Level.FINE);
            logger.log(Level.FINE, "{0} job is completed. ::: Thread-id:[{1}], Process-id:[{2}]", new Object[]{player.getRole().toString(),Thread.currentThread().getId(), AppUtil.getProcessId()});
            stopSubscriptionStatus();
            subscriber.onComplete();
        }
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
}
