package com._360t.structured.model.template;

import com._360t.structured.enumeration.SubscriptionStatus;
import com._360t.structured.model.CompleteMessage;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;


/**
 * <h3 style="color:#55A3C4"> An implementation of the template method for the message exchange process </h3>
 * <p style="color:#3F7A14">
 * The main class of the program that manages the flow of messages exchanging
 * between different classes of processor, publishers and subscribers.
 * Of course, according to the template design pattern, it does not interfere in the messaging business and only controls the flow.
 *
 * <p style="color:#3F7A14">
 * This is an abstract class that, in addition to its main method,
 * provides several abstract methods to its subclasses, which they implement and the order in which they are called is the responsibility of this class.
 * @author MohammadReza Ahmadi
 * @since 9/17/2020
 */

public abstract class MessageProcessTemplate {
    /**
    * value of each publisher subscription status which extends this class.
     * type of this field is SubscriptionStatus which has different values.
     * @see SubscriptionStatus
    * */
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.RUNNING;

    /**
     * @return CountDownLatch of each concrete subClass. It is used to control the number of message exchanging for each subClass.
     * @see CountDownLatch
     */
    protected abstract CountDownLatch getCountDownLatch();

    /**
     * This is a concrete Subscription instance which passed to each Subscriber instance of this Publisher instance.
     * Subscribers can send their new request to their Publisher through the methods of this class.
     *
     * @return SubscriptionModel of each subscriber
     * @see SubscriptionModel
     */
    protected abstract SubscriptionModel getSubscriberSubscription();

    /**
     * Actually it a subclass of Function interface,
     * which could be passed by setter method of each processor instance and do the message transformation.
     * In this application there are two concrete class of the Function interface type and do the transformation
     * for both sender and receiver player.
     * @see com._360t.structured.service.SenderMessageTransformer
     * @see com._360t.structured.service.ReceiverMessageTransformer
     *
     * @return Function instance of each processor instance.
     */
    protected Function<MessageModel, MessageModel> getMessageTransformer() {
        return null;
    }

    /**
     * This method is used to performing any activity before sending a message from any Publisher instance to its Subscriber instance.
     * For example, in both ServerSocketPublisherSubscriber and ClientSocketPublisherSubscriber,
     * this method performs reading socket streams operations.
     * @see com._360t.structured.service.ServerSocketPublisherSubscriber
     * @see com._360t.structured.service.ClientSocketPublisherSubscriber
     *
     * @param message
     * @return
     */
    protected abstract MessageModel beforeSendMessage(MessageModel message);

    /**
     * In this method message prepared by the sender player send to the receiver player through the processor classes.
     * @param message sent message instance
     */
    protected abstract void sendMessage(MessageModel message);

    /**
     * In this method message sent by the sender player will be received by the receiver player through the processor classes.
     * @param message receiving message instance
     */
    protected abstract void receiveMessage(MessageModel message);

    /**
     * This method is used to perform any other activity after sending the message.
     * This method is called by the main method of the MessageProcessTemplate class in the method calling process flow.
     * @param message received message instance
     */
    protected void afterReceiveMessage(MessageModel message) {
    }

    /**
     * This method is used to change the subscription status to the STOPPED status.
     * @see SubscriptionStatus
     */
    protected final void stopSubscriptionStatus() {
        this.subscriptionStatus = SubscriptionStatus.STOPPED;
    }

    /**
     * This method is used for interrupting exchanging each message for a defined milliseconds.
     */
    protected abstract void delay();

    /**
     * This method is called when each of processor or publisher complete its exchanging based on requested message number.
     */
    protected abstract void onComplete();

    /**
     * This method act as a template method and handle process flow of receiving message.
     * @param message receiving message instance
     */
    protected void handleReceiveMessage(MessageModel message) {
        receiveMessage(message);
        afterReceiveMessage(message);
        handleSendMessage(message);
    }

    /**
     * This method act as a template method and handle process flow fo sending message.
     * @param message sent message instance
     */
    protected void handleSendMessage(MessageModel message) {
        if (isComplete()) {
            return;
        }
        countDown();
        message = beforeSendMessage(message);
        if(CompleteMessage.getInstance().equals(message)){
            onComplete();
            return;
        }

        if (getMessageTransformer() != null) {
            sendMessage(getMessageTransformer().apply(message));
        } else {
            sendMessage(message);
        }
    }

    /**
     * This method is called when a cycle of sending and receiving a message is completed.
     * In fact, if the relevant processor has a CountDownLatch, the value of which is reduced by one.
     * And if the relevant processor does not have a countDownLatch, the number of requests in its subscriberSubscription is reduced by one.
     */
    public final void countDown() {
        if (getCountDownLatch() != null) {
            getCountDownLatch().countDown();
            if (getCountDownLatch().getCount() == 0) {
                subscriptionStatus = SubscriptionStatus.COMPLETE;
            }
            return;
        }

        if (getSubscriberSubscription().getRequestCount() == 0) {
            subscriptionStatus = SubscriptionStatus.COMPLETE;
            isComplete();
        }
        getSubscriberSubscription().countDownRequest();
    }

    /**
     * This method return True, if the exchanging cycle of each processor is completed, if not it returns False.
     * @return boolean value.
     */
    public boolean isComplete() {
        if (subscriptionStatus.isComplete()) {
            onComplete();
            return true;
        }
        return false;
    }

    /**
     * This is a helper method to indicates the SubscriptionStatus of each processor.
     * @return boolean value.
     */
    public boolean isNotStopped() {
        return subscriptionStatus.isNotStopped();
    }
}
