package com._360t.structured.apprunner;

import com._360t.simple.PlayerUtil;
import com._360t.structured.app.App;
import com._360t.structured.app.TwoProcessSocketApp;
import com._360t.structured.enumeration.PlayerRole;
import com._360t.structured.model.Player;
import com._360t.structured.model.template.MessageModel;
import com._360t.structured.model.template.PlayerModel;
import com._360t.structured.model.template.ProcessorModel;
import com._360t.structured.service.ClientSocketPublisherSubscriber;
import com._360t.structured.service.PlayerProcessor;
import com._360t.structured.service.ReceiverMessageTransformer;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3 style="color:#55A3C4"> Socket Application Receiver Player runner class that main method of this class should be called to run the Receiver Player app </h3>
 * <p style="color:#3F7A14">
 * This class is created to run the Receiver Player application in the its java process.
 * The main idea behind this application class is able to define independent instance of different class types
 * to provide a unified application instance to execute the receiver player application.
 * The receiver player application architecture is based on Observer design pattern by the help of template method
 * and also delegation design pattern.
 *
 * <p style="color:#3F7A14">
 * Combination of these three patterns make it possible to assemble variety of Processors, Publishers and Subscribers
 * with each other and make a receiver player messaging application without any coupling between different classes and also
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

public class TwoProcessReceiverSocketRunner extends App {
    private static Logger logger = Logger.getLogger(TwoProcessSocketApp.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        int port = 2020;
        int messageNumber = 2;
        int delayMilliSeconds = 1000;

        /** check four input args parameter to assign correct value to the related variable
         * and change the application behaviour based on input parameters.
         * This block of code is useful when a application class call the main method of this class.
         * Application class example are: SingleProcessSocketApp and TwoProcessSocketApp
         * */
        if ((port = PlayerUtil.getArgsIntValue(args, 0, port)) > 0) {
        }
        if ((messageNumber = PlayerUtil.getArgsIntValue(args, 2, messageNumber)) > 0) {
        }
        if ((delayMilliSeconds = PlayerUtil.getArgsIntValue(args, 3, delayMilliSeconds)) > 0) {
        }

        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} is started in two processes mode by parameters: port:[{1}], messageNumber:[{2}] and delayMilliSeconds:[{3}] :::", new Object[]{TwoProcessReceiverSocketRunner.class.getSimpleName(), port, messageNumber, delayMilliSeconds});

        /** definition and instantiation of receiver instance*/
        PlayerModel receiver = new Player(PlayerRole.RECEIVER);

        /** definition and instantiation of receiver CountDownLatch instance based on the number of requested messages*/
        CountDownLatch receiverCountDownLatch = new CountDownLatch(messageNumber);

        /** definition and instantiation of receiverMessageTransformer instance*/
        Function<MessageModel, MessageModel> receiverTransformer = new ReceiverMessageTransformer(receiverCountDownLatch);

        /** assemble every different instances with each other by the help of composition and observer patterns*/
        ProcessorModel clientSocketPublisherSubscriber = new ClientSocketPublisherSubscriber(port, delayMilliSeconds);
        /** definition and instantiation of receiverProcessor instance*/
        ProcessorModel receiverProcessor = new PlayerProcessor(receiver, receiverTransformer, receiverCountDownLatch, delayMilliSeconds);
        receiverProcessor.subscribe(clientSocketPublisherSubscriber);
        clientSocketPublisherSubscriber.subscribe(receiverProcessor);


        /** Wait for the main thread of the program to complete the process.*/
        try {
            receiverCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }
    }
}
