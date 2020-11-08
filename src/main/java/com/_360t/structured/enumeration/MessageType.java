package com._360t.structured.enumeration;

/**
 * <h3 style="color:#55A3C4"> Message Type Enum </h3>
 * <p style="color:#3F7A14">
 * This enum is used to handle message type which will be SEND or RECEIVE
 *
 * @author MohammadReza Ahmadi
 * @since 9/9/2020
 */

public enum MessageType {
    SEND,
    RECEIVE;

    public boolean isSendMessage() {
        return this.equals(SEND);
    }

    public boolean isReceiveMessage() {
        return this.equals(RECEIVE);
    }

    public static MessageType resolve(String messageType) {
        for (MessageType value : MessageType.values()) {
            if (value.toString().equals(messageType))
                return value;
        }

        throw new IllegalArgumentException("MessageType" + messageType + " was not resolved!");
    }


}
