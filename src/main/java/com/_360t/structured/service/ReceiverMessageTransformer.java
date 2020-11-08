package com._360t.structured.service;

import com._360t.structured.model.ReceiveMessage;
import com._360t.structured.model.SendMessage;
import com._360t.structured.model.template.MessageModel;
import com._360t.util.AppUtil;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class ReceiverMessageTransformer implements Function<MessageModel,MessageModel> {
    private final CountDownLatch countDownLatch;
    private final long totalMessageNumber;

    public ReceiverMessageTransformer(CountDownLatch countDownLatch) {
        this.countDownLatch = new CountDownLatch((int) countDownLatch.getCount());
        this.totalMessageNumber = countDownLatch.getCount();
    }

    @Override
    public ReceiveMessage apply(MessageModel message) {
        ReceiveMessage receiveMessage = new ReceiveMessage(message.getTitle());
        receiveMessage.setSuffix(AppUtil.getEmptyStringIfNull(message.getSuffix()).concat(String.valueOf(totalMessageNumber - countDownLatch.getCount() + 1)));
        receiveMessage.setValue(message.getTitle().concat(receiveMessage.getSuffix()));
        receiveMessage.setTrafficNumber(message.getTrafficNumber());
        countDownLatch.countDown();
        return receiveMessage;
    }
}
