package com._360t.structured.log.config;

import com._360t.structured.log.LogParamColor;

/**
 * <h3 style="color:#55A3C4"> Costume log parameter to handle value and color of each log message  </h3>
 * <p style="color:#3F7A14">
 *
 * @author MohammadReza Ahmadi
 * @since 9/11/2020
 */

public class LogParam {
    private String value;
    private LogParamColor color;

    /**
     * @param value String of message value
     * @param color LogParamColor which handle value and color of the log message
     */
    public LogParam(String value, LogParamColor color) {
        this.value = value;
        this.color = color;
    }

    public static LogParam getParam(String value, LogParamColor color){
        return new LogParam(value,color);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LogParamColor getColor() {
        return color;
    }

    public void setColor(LogParamColor color) {
        this.color = color;
    }
}
