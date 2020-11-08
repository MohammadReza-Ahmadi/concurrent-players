package com._360t.structured.model.template;

import java.util.concurrent.Flow;

/**
 * <h3 style="color:#55A3C4"> Simple     model interface of Subscription interface in Flow package.</h3>
 * <p style="color:#3F7A14">
 * This interface defines two methods, countDownRequest() and getRequestCount(),
 * in order to maintain the number of subscriber requests that are implemented by the concrete classes.
 * Already there is just one concrete class of this interface which implements its methods, it is called MessageSubscription.
 *
 * @see Flow.Subscription
 * @see com._360t.structured.model.MessageSubscription
 *
 * @author MohammadReza Ahmadi
 * @since 9/12/2020
 */


public interface SubscriptionModel extends Flow.Subscription {

    /**
     * This is used to count down the number of subscriber requests by one.
     * @return remain number of requested message count after count down by one.
     */
    long countDownRequest();

    /**
     * This is used to get the number of message count.
     * @return remain number of requested message count.
     */
    long getRequestCount();
}
