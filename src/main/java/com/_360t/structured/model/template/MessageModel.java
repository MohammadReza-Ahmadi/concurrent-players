package com._360t.structured.model.template;

import com._360t.structured.enumeration.MessageType;

/**
 * <h3 style="color:#55A3C4"> Define the structure of the message model </h3>
 * <p style="color:#3F7A14">
 * This interface is used to define basic properties of every concrete message object by the help of get and set methods.
 * With the help of this interface, the message is defined as an concrete class
 * that has several special fields and allows the property of a message to be separated and concat each other whenever it needs.
 * Two concrete classes which implement the MessageModel are:
 * @see com._360t.structured.model.SendMessage
 * @see com._360t.structured.model.ReceiveMessage
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public interface MessageModel {

    /**
     * @see MessageType MessageType
     * @return messageType that could be one of SEND or RECEIVE type.
     *
     */
    MessageType getType();

    /**
     * @return title of the message
     * */
    String getTitle();

    /**
     * setter method of the title of message
     * @param title of the message
     * */
    void setTitle(String title);

    /**
     *
     * @return suffix of the message
     */
    String getSuffix();

    /**
     *
     * @param suffix of the message
     */
    void setSuffix(String suffix);

    /**
     * The value of this field is created by concatenating two fields, title and suffix.
     * This is done by classes SenderMessageTransformer and ReceiverMessageTransformer.
     * This field contains the actual value of the message sent between the sender and receiver of the message.
     * {@link com._360t.structured.service.SenderMessageTransformer} SenderMessageTransformer
     * {@link com._360t.structured.service.ReceiverMessageTransformer} ReceiverMessageTransformer
     * @return actual value of a message.
     */
    String getValue();

    /**
     * setter method the value of a message
     * @param value is the actual value of a message
     */
    void setValue(String value);

    /**
     * Returns the value of the message traffic number,
     * which indicates the number of round-trip cycles of the message between the sender and receiver of the message.
     * @return trafficNumber
     */
    Long getTrafficNumber();

    /**
     * Setter method the trafficNumber of every round-trip cycles which
     * calculate and set by SenderMessageTransformer and ReceiverMessageTransformer.
     * @see com._360t.structured.service.SenderMessageTransformer SenderMessageTransformer
     * @see com._360t.structured.service.ReceiverMessageTransformer ReceiverMessageTransformer
     * @param trafficNumber
     */
    void setTrafficNumber(Long trafficNumber);

    /**
     * Convert a message object as string which representing all properties's status.
     * This method uses the default interface method which introduced in Java 8.
     * @return String value of the message.
     */
    default String getAsString() {
        return "[type=" + getType() +
                ", value=" + getValue() +
                ", title=" + getTitle() +
                ", suffix=" + getSuffix() +
                ", trafficNumber=" + getTrafficNumber() + "]";
    }

}
