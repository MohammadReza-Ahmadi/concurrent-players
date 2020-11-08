package com._360t.structured.model;

import com._360t.structured.enumeration.MessageType;
import com._360t.structured.model.template.MessageModel;


/**
 * <h3 style="color:#55A3C4"> A special implementation of the message class for sending from the ClientSocket to the ServerSocket as the termination message.  </h3>
 * <p style="color:#3F7A14">
 * In this player application this message class only used in socket intercommunication in single process.
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public class CompleteMessage implements MessageModel {
    private String title;
    private String suffix;
    private String value;
    private Long trafficNumber;

    private CompleteMessage() {
    }

    /**
     * @return CompleteMessage instance by setting specified title.
     * @see MessageModel
     */
    public static CompleteMessage getInstance(){
        CompleteMessage completeMessage = new CompleteMessage();
        completeMessage.setTitle("COMPLETE");
        return completeMessage;
    }

    @Override
    public MessageType getType() {
        return MessageType.RECEIVE;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTrafficNumber() {
        return trafficNumber;
    }

    public void setTrafficNumber(Long trafficNumber) {
        this.trafficNumber = trafficNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((MessageModel) obj).getTitle());
    }
}
