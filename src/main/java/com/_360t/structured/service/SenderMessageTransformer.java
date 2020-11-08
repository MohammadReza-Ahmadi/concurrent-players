package com._360t.structured.service;

import com._360t.structured.model.SendMessage;
import com._360t.structured.model.template.MessageModel;
import com._360t.util.AppUtil;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 * <h1>Message model</h1>
 * Define the structure of the message model
 * <p>
 * This interface is used to define basic properties of every concrete message object by the help of get and set methods.
 * With the help of this interface, the message is defined as an concrete class
 * that has several special fields and allows the property of a message to be separated and concat each other whenever it needs.
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public class SenderMessageTransformer implements Function<MessageModel, MessageModel> {
    private final CountDownLatch countDownLatch;
    private final long totalMessageNumber;

    public SenderMessageTransformer(CountDownLatch countDownLatch) {
        this.countDownLatch = new CountDownLatch((int) countDownLatch.getCount());
        this.totalMessageNumber = countDownLatch.getCount();
    }

    @Override
    public SendMessage apply(MessageModel message) {
        SendMessage sendMessage = new SendMessage(message.getTitle());
        sendMessage.setValue(message.getValue());
        sendMessage.setTrafficNumber(totalMessageNumber - countDownLatch.getCount() + 1);

        if (countDownLatch.getCount() < totalMessageNumber) {
            sendMessage.setSuffix(AppUtil.getEmptyStringIfNull(message.getSuffix()).concat(String.valueOf(totalMessageNumber - countDownLatch.getCount() + 1)));
            sendMessage.setValue(sendMessage.getTitle().concat(sendMessage.getSuffix()));
        }
        countDownLatch.countDown();
        return sendMessage;
    }
}
