package com.animediscs.util;

public abstract class Format {

    public static String formatTimeout(long timestamp) {
        long timeout = System.currentTimeMillis() - timestamp;
        StringBuilder builder = new StringBuilder();
        timeout = appendTimeout(builder, timeout, "%d天 ", 86400000L);
        timeout = appendTimeout(builder, timeout, "%d时 ", 3600000L);
        timeout = appendTimeout(builder, timeout, "%02d分 ", 60000L);
        appendTimeout(builder, timeout, "%02d秒", 1000L);
        return builder.toString();
    }

    private static long appendTimeout(StringBuilder builder, long timeout, String format, long count) {
        if (timeout >= count) {
            builder.append(String.format(format, timeout / count));
        } else if (format.charAt(1) == '0') {
            builder.append(String.format(format, 0));
        }
        return timeout % count;
    }

    public static String formatError(Throwable t) {
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }

}
