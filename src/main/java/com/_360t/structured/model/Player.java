package com._360t.structured.model;

import com._360t.structured.enumeration.PlayerRole;
import com._360t.structured.log.LogParamColor;
import com._360t.structured.log.LogUtil;
import com._360t.structured.log.config.LogParam;
import com._360t.structured.model.template.MessageModel;
import com._360t.structured.model.template.PlayerModel;
import com._360t.util.AppUtil;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <h3 style="color:#55A3C4"> Player class that sends and receives messages </h3>
 * <p style="color:#3F7A14">
 * This is the concrete Player class in the program, that there will be at least two instances of that.
 * The main methods of this class are send and receive message.
 * @see PlayerModel
 *
 * @author MohammadReza Ahmadi
 * @since 9/11/2020
 */

public class Player implements PlayerModel {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * Defines role of every player. Its type is PlayerRole.
     * @see PlayerRole
     */
    private final PlayerRole role;

    public Player(PlayerRole role) {
        this.role = role;
    }

    /**
     * This method is used to return the each Player role.
     * @return Role value of type PlayerRole.
     * @see PlayerRole
     */
    @Override
    public PlayerRole getRole() {
        return role;
    }

    /**
     * This method is the implementation of sending message behaviour.
     * @param message Message instance which is created to send to the other player.
     */
    @Override
    public void sendMessage(MessageModel message) {
        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} sends message:{1} ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{role.toString(),message.getAsString(),Thread.currentThread().getId(),AppUtil.getProcessId()});
    }

    /**
     * This method is the implementation of receiving message behaviour.
     * @param message Message instance which is received from the other player.
     */
    @Override
    public void receiveMessage(MessageModel message) {
        logger.setLevel(Level.FINE);
        logger.log(Level.FINE, "{0} receives message:{1} ::: Thread-id:[{2}], Process-id:[{3}]", new Object[]{role.toString(),message.getAsString(),Thread.currentThread().getId(),AppUtil.getProcessId()});
    }
}
