package com._360t.structured.model;

import com._360t.structured.enumeration.MessageType;
import com._360t.structured.enumeration.PlayerRole;
import com._360t.structured.model.template.MessageModel;

/**
 * <h3 style="color:#55A3C4"> This is an implemented class of the MessageModel interface as the SentMessage </h3>
 * <p style="color:#3F7A14">
 * This class is used to act as a SendMessage class which will be send by sender player (Initiator).
 * All the method of this class is explained in MessageModel interface.
 * @see MessageModel
 *
 * @author MohammadReza Ahmadi
 * @since 9/9/2020
 */

public class SendMessage implements MessageModel {
    private String title;
    private String suffix;
    private String value;
    private Long trafficNumber;

    public SendMessage(String title) {
        this.title = title;
        this.value = title;
        this.trafficNumber = 1L;
    }

    @Override
    public MessageType getType() {
        return MessageType.SEND;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuffix() {
        return suffix==null? "":suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public Long getTrafficNumber() {
        return trafficNumber;
    }

    public void setTrafficNumber(Long trafficNumber) {
        this.trafficNumber = trafficNumber;
    }
}
