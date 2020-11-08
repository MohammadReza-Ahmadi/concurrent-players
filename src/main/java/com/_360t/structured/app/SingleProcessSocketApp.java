package com._360t.structured.app;

import com._360t.simple.PlayerUtil;
import com._360t.structured.config.JarFromPomConfig;
import com._360t.structured.enumeration.PlayerRole;
import com._360t.structured.model.Player;
import com._360t.structured.model.SendMessage;
import com._360t.structured.model.template.MessageModel;
import com._360t.structured.model.template.PlayerModel;
import com._360t.structured.model.template.ProcessorModel;
import com._360t.structured.service.*;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3 style="color:#55A3C4"> Single Process Socket Application that main method of this class should be called to run the program </h3>
 * <p style="color:#3F7A14">
 * This class is created to run the Player application in the single process mode.
 * The main idea behind this application class is able to define independent instance of different class types
 * to provide a unified application instance to execute the player application.
 * The player application architecture is based on Observer design pattern by the help of template method
 * and also delegation design pattern.
 *
 * <p style="color:#3F7A14">
 * Combination of these three pattern make it possible to assemble variety of Processors, Publishers and Subscribers
 * with each other and make a player messaging application without any coupling between different classes and also
 * keep coherence of unified desired business rolls.
 *
 * <p style="color:#3F7A14">
 * It is simple to create a new player application by a different subscription implementation in Processor, Publisher or Subscriber class.
 * For example it is possible to make a new Processor class by the another implementation of IPC or Inter Process Communication.
 * Imagine we could have a Processor class which utilizing File and new IO channel file to implementing IPC.
 * Or another example is possibility to implement udp socket connection.
 *
 *  <p style="color:#3F7A14">
 *  Finally, all of above implementations are possible by assembling different types of classes
 *  like: PlayerModel, MessageModel, ProcessorModel, SubscriptionModel, MessageOperationDelegation and MessageProcessTemplate
 *  without any obligations to make a change to any of the super classes or change the implementation of concrete classes.
 *
 *  By the help of these possibilities couple of application classes is created.
 *  Application classes like: TwoProcessSocketApp and SingleProcessSubmissionApp.
 *
 * @see PlayerModel
 * @see MessageModel
 * @see ProcessorModel
 * @see com._360t.structured.model.template.SubscriptionModel
 * @see com._360t.structured.model.template.MessageOperationDelegation
 * @see com._360t.structured.model.template.MessageProcessTemplate
 *
 * @author MohammadReza Ahmadi
 * @since 9/10/2020
 */

public class SingleProcessSocketApp extends App {
    private static Logger logger = Logger.getLogger(SingleProcessSocketApp.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        int port = 2020;
        String initiatorMessage = "Hi-360Tw";
        int messageNumber = 10;
        int delayMilliSeconds = 1000;


        if ((port = PlayerUtil.getArgsIntValue(args, 0, port)) > 0) {
        }
        if ((initiatorMessage = PlayerUtil.getArgsStringValue(args, 1, initiatorMessage)) != null) {
        }
        if ((messageNumber = PlayerUtil.getArgsIntValue(args, 2, messageNumber)) > 0) {
        }
        if ((delayMilliSeconds = PlayerUtil.getArgsIntValue(args, 3, delayMilliSeconds)) > 0) {
        }

        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} is started in single processes mode by parameters: port:[{1}], message:[{2}], messageNumber:[{3}] and delayMilliSeconds:[{4}] :::", new Object[]{SingleProcessSocketApp.class.getSimpleName(), port, initiatorMessage,messageNumber, delayMilliSeconds});


        /** definition and instantiation of initiator and receiver CountDownLatch instances based on the number of requested messages*/
        CountDownLatch initiatorCountDownLatch = new CountDownLatch(messageNumber);
        CountDownLatch receiverCountDownLatch = new CountDownLatch(messageNumber);

        /** definition and instantiation of initiator and receiver instances*/
        PlayerModel initiator = new Player(PlayerRole.INITIATOR);
        PlayerModel receiver = new Player(PlayerRole.RECEIVER);

        /** definition and instantiation SendMessage instance*/
        MessageModel sendMessage = new SendMessage(initiatorMessage);

        /** definition and instantiation of initiatorMessageTransformer and receiverMessageTransformer instances*/
        Function<MessageModel, MessageModel> initiatorTransformer = new SenderMessageTransformer(receiverCountDownLatch);
        Function<MessageModel, MessageModel> receiverTransformer = new ReceiverMessageTransformer(receiverCountDownLatch);

        /** definition and instantiation of ClientSocketPublisherSubscriber instance*/
        ProcessorModel clientSocketPublisherSubscriber = new ClientSocketPublisherSubscriber(port, delayMilliSeconds);

        /** definition and instantiation of receiverProcessor instance*/
        ProcessorModel receiverProcessor = new PlayerProcessor(receiver, receiverTransformer, delayMilliSeconds);

        /** assemble every different instances with each other by the help of composition and observer patterns*/
        clientSocketPublisherSubscriber.subscribe(receiverProcessor);
        receiverProcessor.setCountDownLatch(receiverCountDownLatch);
        receiverProcessor.subscribe(clientSocketPublisherSubscriber);

        /** definition and instantiation of ServerSocketPublisherSubscriber instance*/
        ProcessorModel serverClientSocketPublisherSubscriber = new ServerSocketPublisherSubscriber(port, delayMilliSeconds);

        /** definition and instantiation of initiatorProcessor instance*/
        ProcessorModel initiatorProcessor = new PlayerProcessor(initiator, initiatorTransformer, delayMilliSeconds);

        /** assemble every different instances with each other by the help of composition and observer patterns*/
        initiatorProcessor.subscribe(serverClientSocketPublisherSubscriber);
        serverClientSocketPublisherSubscriber.subscribe(initiatorProcessor);
        initiatorProcessor.setCountDownLatch(initiatorCountDownLatch);

        /** send first initiator message by the help of submit method */
        initiatorProcessor.submit(sendMessage);

        /** Wait for the main thread of the program to complete the processes.*/
        try {
            initiatorCountDownLatch.await();
            receiverCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
