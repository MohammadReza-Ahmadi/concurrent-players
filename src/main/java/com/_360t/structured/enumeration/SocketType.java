package com._360t.structured.enumeration;

/**
 * <h3 style="color:#55A3C4"> Socket Type Enum </h3>
 * <p style="color:#3F7A14">
 * This enum is used to handle Socket type which will be SERVER or CLIENT
 *
 * @author MohammadReza Ahmadi
 * @since 9/9/2020
 */

public enum SocketType {
    SERVER,
    CLIENT;

    public boolean isServer() {
        return this.equals(SERVER);
    }

    public boolean isClient() {
        return this.equals(CLIENT);
    }
}
