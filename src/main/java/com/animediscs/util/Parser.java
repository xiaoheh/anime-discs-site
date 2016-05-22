package com.animediscs.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Parser {

    private static Logger logger = LogManager.getLogger(Parser.class);

    public static Date parseDate(SimpleDateFormat dateFormat, String dateText) {
        try {
            return dateFormat.parse(dateText);
        } catch (ParseException e) {
            logger.warn("不能解析该日期, 错误信息为: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static int parseNumber(String number) {
        int result = 0;
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (Character.isDigit(ch)) {
                result *= 10;
                result += ch - '0';
            }
        }
        return result;
    }

}
