package com._360t.structured.model;

import com._360t.structured.model.template.SubscriptionModel;

/**
 * <h3 style="color:#55A3C4"> Implementation of Flow.Subscription interface </h3>
 * <p style="color:#3F7A14">
 * This class is used to act as a Subscription for exchanging messages and control the number of requests of message.
 * @see SubscriptionModel
 *
 * @author MohammadReza Ahmadi
 * @since 9/11/2020
 */

public class MessageSubscription implements SubscriptionModel {
    private long requestCount = 0;

    /**
     * This method is used to increase the number of Subscriber requests.
     * @param n The number of request that send by Subscriber class.
     */
    @Override
    public void request(long n) {
        requestCount += n;
    }

    /**
     * This method is used to rest the number of Subscriber requests.
     */
    @Override
    public void cancel() {
        requestCount = 0;
    }

    /**
     * This method is used to decrease the count of publisher countDownLatch.
     * @return count of countDownLatch after decrease its count by one.
     */
    @Override
    public long countDownRequest() {
        if (requestCount > 0)
            requestCount--;
        return requestCount;
    }

    /**
     * This method is used to return the number of requests.
     * @return count of countDownLatch.
     */
    @Override
    public long getRequestCount() {
        return requestCount;
    }
}
