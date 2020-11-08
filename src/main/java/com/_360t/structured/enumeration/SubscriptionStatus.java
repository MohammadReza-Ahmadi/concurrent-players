package com._360t.structured.enumeration;

/**
 * <h3 style="color:#55A3C4"> Subscription Status Enum </h3>
 * <p style="color:#3F7A14">
 * This enum is used to handle Subscription Status which will be RUNNING,COMPLETE, STOPPED and UNDEFINED
 *
 * @author MohammadReza Ahmadi
 * @since 9/9/2020
 */

public enum SubscriptionStatus {
    RUNNING,
    COMPLETE,
    STOPPED,
    UNDEFINED;

    public static SubscriptionStatus resolve(String status) {
        for (SubscriptionStatus value : SubscriptionStatus.values()) {
            if (value.toString().equals(status))
                return value;
        }
        return UNDEFINED;
    }

    public boolean isRunning() {
        return this.equals(RUNNING);
    }

    public boolean isComplete() {
        return this.equals(COMPLETE);
    }

    public boolean isStopped() {
        return this.equals(STOPPED);
    }

    public boolean isNotStopped() {
        return !isStopped();
    }

}
