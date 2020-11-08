package com._360t.structured.service;

import com._360t.structured.model.template.MessageModel;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * @author MohammadReza Ahmadi
 * 9/10/2020, 7:37 PM
 */

public class SubmissionPublisherSubscriber extends SubmissionPublisher<MessageModel> implements Flow.Subscriber<MessageModel> {
    //Initiator subscription
//    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
//        this.subscription = subscription;
        //request first message form initiator
//        subscription.request(1);
    }

    @Override
    public void onNext(MessageModel messageModel) {
//        System.out.println(String.format("<<%s>> onNext called by message:[%s]",this.getClass().getSimpleName(),message.getAsString()));
        //request for another message form initiator
//        subscription.request(1);
        //send message to receiver
        submit(messageModel);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(String.format("<<%s>> onError called !!",this.getClass().getSimpleName()));
    }

    @Override
    public void onComplete() {
        System.out.println(String.format("<<%s>> onComplete called !!",this.getClass().getSimpleName()));
    }
}
