package com._360t.structured.enumeration;

/**
 * <h3 style="color:#55A3C4"> Player Role Enum </h3>
 * <p style="color:#3F7A14">
 * This enum is used to handle Player Role type which will be INITIATOR, RECEIVER and UNDEFINED
 *
 * @author MohammadReza Ahmadi
 * @since 9/3/2020
 */

public enum PlayerRole {
    INITIATOR,
    RECEIVER,
    UNDEFINED;

    public boolean isInitiator() {
        return this.equals(INITIATOR);
    }

    public boolean isReceiver() {
        return this.equals(RECEIVER);
    }

    public boolean isSameRole(PlayerRole playerRole) {
        return this.equals(playerRole);
    }

    public boolean isNotUndefined() {
        return !this.equals(UNDEFINED);
    }
}
