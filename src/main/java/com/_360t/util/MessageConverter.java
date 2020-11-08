package com._360t.util;

/**
 * <h3 style="color:#55A3C4"> Message Converter which convert object message to String message and vise versa </h3>
 * <p style="color:#3F7A14">
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

import com._360t.structured.enumeration.MessageType;
import com._360t.structured.model.ReceiveMessage;
import com._360t.structured.model.SendMessage;
import com._360t.structured.model.template.MessageModel;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public final class MessageConverter {

    /**
     * @param messageStr is String value of message object should be convert to MessageModel
     * @return MessageModel which is converted from message string value
     */
    public static MessageModel getStringAsMessage(String messageStr) {
        MessageModel messageModel = null;
        boolean isTypeResolved = false;
        messageStr = messageStr.replaceAll("\\[", "").replaceAll("]", "");
        for (String fieldValue : messageStr.split(",")) {
            try {
                if (fieldValue.split("=").length > 1) {
                    if (!isTypeResolved) {
                        messageModel = resolveMessageType(fieldValue);
                        isTypeResolved = true;
                        continue;
                    }

                    String fName = fieldValue.split("=")[0].trim();
                    String fValue = fieldValue.split("=")[1];

                    if(fName.equals("value") && fValue!=null && !fValue.isEmpty())
                        messageModel.setValue(fValue);
                    if(fName.equals("title") && fValue!=null && !fValue.isEmpty())
                        messageModel.setTitle(fValue);
                    if(fName.equals("suffix") && fValue!=null && !fValue.isEmpty())
                        messageModel.setSuffix(fValue);
                    if(fName.equals("trafficNumber") && fValue!=null && !fValue.isEmpty() && !fValue.equals("null"))
                        messageModel.setTrafficNumber(Long.valueOf(fValue));

//                    System.out.println(messageModel.getAsString());

//                    BeanUtils.setProperty(messageModel, fieldValue.split("=")[0].trim(), fieldValue.split("=")[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messageModel;
    }

    /**
     * @param messageType is passed to resolve related MessageModel
     * @return MessageModel which will be resolved using messageType input parameter
     */
    private static MessageModel resolveMessageType(String messageType) {
        MessageType type = MessageType.resolve(messageType.split("=")[1].trim());
        MessageModel messageModel = null;
        if (type.isSendMessage()) {
            messageModel = new SendMessage(null);
        } else if (type.isReceiveMessage()) {
            messageModel = new ReceiveMessage(null);
        }
        return messageModel;
    }
}
