package com._360t.structured.log.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com._360t.structured.log.LogParamColor.*;

/**
 * <h3 style="color:#55A3C4"> Custom LogFormatter handle console logs </h3>
 * <p style="color:#3F7A14">
 * This class is used to change log format of global logger.
 * There is fun feature in this class is that for every logger class it assigned a new color.
 *
 * @author MohammadReza Ahmadi
 * @since 9/7/2020
 */

public class LogFormatter extends Formatter {
    // Here you can configure the format of the output and
    // its color by using the ANSI escape codes defined above.
    // format is called for every console log message

    private Map<String, Integer> colorMap = new HashMap<>();

    /**
     * Append some color meta date to the log message.
     * @param record of type LogRecord which is formatted through this method
     * @return String log as result.
     */

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

/*
        builder.append(ANSI_WHITE.getCode());
        builder.append("[");
        builder.append(calcDate(record.getMillis()));
        builder.append("]");
*/

        builder.append(ANSI_CYAN.getCode());
        builder.append(" [");
        builder.append(record.getLevel().getName());
        builder.append("]");
        builder.append(" - ");

        builder.append(record.getMessage());

        Object[] params = record.getParameters();
        String log = builder.toString();
        if (params != null) {
            builder.append("\t");
            int c = 0;
            for (Object param : params) {
                StringBuilder b = new StringBuilder();
                if(c==0){
                    int code = getNextColorCode(param.toString());
//                    System.out.println("code="+(code+7));
                    String classParamColor = "\u001b[38;5;" + (code + 7) + "m";
                    b.append(classParamColor);
                } else {
                    b.append(ANSI_GREEN.getCode());
                    b.append("\u001b[38;5;122m");
                }
                b.append(param);
                b.append(ANSI_BLUE.getCode());
//                b.append("\u001b[38;5;122m");
                log = log.replace("{" + c + "}", b.toString());
                c++;
            }
        }
        builder = new StringBuilder(log);
        builder.append("\n");
        return builder.toString();
    }


    private String calcDate(long milliSeconds) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultDate = new Date(milliSeconds);
        return date_format.format(resultDate);
    }

    private Integer getNextColorCode(String className) {
        if (colorMap.get(className) != null)
            return colorMap.get(className);

        int code = new Random().nextInt(200);
        while (colorMap.containsValue(code))
            code = new Random().nextInt(200);

        colorMap.put(className, code);
        return code;
    }
}
