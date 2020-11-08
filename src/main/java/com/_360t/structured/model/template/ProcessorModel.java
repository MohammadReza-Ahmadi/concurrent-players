package com._360t.structured.model.template;

import com._360t.structured.log.LogParamColor;
import com._360t.structured.log.LogUtil;
import com._360t.structured.log.config.LogParam;
import com._360t.util.AppUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3 style="color:#55A3C4"> Define the overall structure of a message processor class </h3>
 * <p style="color:#3F7A14">
 * This class extends the behaviour of the Flow.Publisher and Flow.Subscriber interfaces, which were introduced in Java 9.
 * Basically, the main idea of the Player App is based on the Java 9 Flow Api package.
 * In essence, this class inherited two behaviours of a processor, publishing and subscribing
 * and inherited from both Publisher and Subscriber interfaces for clarity.
 * @see Flow.Publisher
 * @see Flow.Subscriber
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public interface ProcessorModel extends Flow.Publisher<MessageModel>, Flow.Subscriber<MessageModel> {

    /**
     * Accepts a CountDownLatch value that controls the number of incoming and outgoing messages per sending cycle.
     * @see CountDownLatch
     * @param countDownLatch
     */
    void setCountDownLatch(CountDownLatch countDownLatch);

    /**
     * Accepts a MessageModel value and when the game starts, the first message will be sent through this method.
     * Another implementation that can use this method is that this method is called several times in a loop and sends and receives a message each time.
     * @see MessageModel
     * @param message
     */
    void submit(MessageModel message);

    /**
     * This method is called through Subscription, Publisher or Subscriber classes when an error occurs,
     * and because it behaves the same in all child classes, it is included as a default method in this interface.
     * @param throwable An error object that was passed by the higher class when an exception throws.
     * @param logger Logger object which pass to this method to print its error log message.
     */
    default void defaultOnError(Throwable throwable, Logger logger) {
        logger.setLevel(Level.FINE);
        LogParam roleParam = LogParam.getParam(this.getClass().getSimpleName(), LogParamColor.ANSI_GREEN);
        LogParam messageParam = LogParam.getParam("error method is called !!", LogParamColor.ANSI_RED);
        logger.log(Level.FINE, "{0} {1}", LogUtil.putParams(roleParam, messageParam));
        throwable.printStackTrace();
        System.exit(1);
    }
}
