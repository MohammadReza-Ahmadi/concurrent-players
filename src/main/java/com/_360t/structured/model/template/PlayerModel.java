package com._360t.structured.model.template;

import com._360t.structured.enumeration.PlayerRole;
import com._360t.structured.model.template.MessageModel;

/**
 * <h3 style="color:#55A3C4"> Define the structure of the player model </h3>
 * <p style="color:#3F7A14">
 * This class act as a template model for all players and
 * also it's a deligation interface which deligate
 * sendMessage and receiveMessage method calls to
 * @see MessageOperationDelegation
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public interface PlayerModel extends MessageOperationDelegation {

    /**
     *
     * @return role of the current player
     */
    PlayerRole getRole();
}
