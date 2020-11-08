package com._360t.util;

import com._360t.structured.enumeration.PlayerRole;

import java.lang.management.ManagementFactory;

/**
 * <h3 style="color:#55A3C4"> App Util class </h3>
 * <p style="color:#3F7A14">
 * This util class is used to handle different types of utility methods like
 * get processId, get main method args value as string or int.
 *
 * @author MohammadReza Ahmadi
 * @since 9/9/2020
 */

public class AppUtil {

    public static String getProcessId() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    public static String getEmptyStringIfNull(String str) {
        return str == null ? "" : str;
    }

    public static String getFullyQualifiedName(Class clazz) {
        return clazz.getName();
    }

    public static String getTargetPath() {
        return "target/classes";
    }

    public static String getFullTargetPathOfClass(Class clazz) {
        return getFullTargetPathWithoutClassName(clazz).split(clazz.getName())[0];
    }

    public static String getFullTargetPathWithoutClassName(Class clazz) {
        String path = (getTargetPath() + "/" + getFullyQualifiedName(clazz));
        return path.substring(0, path.lastIndexOf(".")).replaceAll("\\.", "/");
    }

    public static String getFullSrcPathOfClass(Class clazz) {
        return ("src/main/java/" + getFullyQualifiedName(clazz)).replaceAll("\\.", "/") + ".java";
    }

    public static PlayerRole getPlayerRole(String[] args, int argNumber) {
        final String playerRole = getArgsStringValue(args, argNumber);
        if (playerRole.equals(PlayerRole.INITIATOR.toString()))
            return PlayerRole.INITIATOR;

        if (playerRole.equals(PlayerRole.RECEIVER.toString()))
            return PlayerRole.RECEIVER;

        return PlayerRole.UNDEFINED;
    }

    public static boolean isArgumentEmpty(String[] args, int argNumber) {
        return args == null || args.length <= argNumber || args[argNumber].trim().isEmpty();
    }

    public static boolean isArgumentNotEmpty(String[] args, int argNumber) {
        return !isArgumentEmpty(args, argNumber);
    }

    public static String getArgsStringValue(String[] args, int argNumber) {
        return getArgsStringValue(args, argNumber, "");
    }

    public static String getArgsStringValue(String[] args, int argNumber, String defaultValue) {
//        if (args != null && args.length > argNumber)
        if (isArgumentNotEmpty(args, argNumber))
            return args[argNumber];
        return defaultValue;
    }

    public static int getArgsIntValue(String[] args, int argNumber) throws Exception {
        return getArgsIntValue(args, argNumber, -1);
    }

    public static int getArgsIntValue(String[] args, int argNumber, int defaultValue) throws Exception {
//        if (args != null && args.length > argNumber) {
//            if (!args[argNumber].trim().isEmpty()) {
        if (isArgumentNotEmpty(args, argNumber)) {
            try {
                return Integer.parseInt(args[argNumber].trim());
            } catch (NumberFormatException e) {
                throw new Exception(e);
            }
//            }
        }
        return defaultValue;
    }
}
