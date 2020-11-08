package com._360t.structured.model.template;

/**
 * <h3 style="color:#55A3C4"> The template interface, defines the two methods of sending and receiving messages </h3>
 * <p style="color:#3F7A14">
 * This interface extended by {@link PlayerModel} and each concrete player class should be implemented send and receive methods.
 * @see com._360t.structured.model.Player
 *
 * @author MohammadReza Ahmadi
 * @since 9/17/2020
 */

public interface MessageOperationDelegation {

    /**
     * This method accepts each class of the MessageModel type and provides it to the Player class to be processed
     * when player wants to send a new message.
     * @param messageModel
     * @see MessageModel
     * @see PlayerModel
     */
    void sendMessage(MessageModel messageModel);

    /**
     * This method accepts each class of the MessageModel type and provides it to the Player class to be processed
     * when player receives a new message.
     * @param messageModel
     */
    void receiveMessage(MessageModel messageModel);
}
