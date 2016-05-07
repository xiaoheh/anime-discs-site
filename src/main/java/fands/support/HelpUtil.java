package fands.support;

import fands.model.disc.DiscType;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

@Service
public abstract class HelpUtil {

    private static ThreadLocal<DecimalFormat> numberFormat = ThreadLocal.withInitial(() -> {
        return new DecimalFormat("###,###");
    });

    private static ThreadLocal<SimpleDateFormat> updateFormat = ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    });

    private final static ThreadLocal<SimpleDateFormat> discFormat = ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyy/MM/dd");
    });


    public static String formatNumber(String format, int number) {
        return number == -1 ? "无数据" : String.format(format, formatNumber(number));
    }

    private static String formatNumber(int number) {
        return numberFormat.get().format(number);
    }

    public static String formatSakura(String format, int number, int width) {
        return number == -1 ? "无数据" : String.format(format, formatSakura(number, width));
    }

    private static String formatSakura(int number, int width) {
        String string = formatNumber(number);
        StringBuilder builder = new StringBuilder(width);
        builder.append("***,***", 7 - width, 7 - string.length());
        builder.append(string);
        return builder.toString();
    }

    public static String formatUpdate(Date date) {
        return updateFormat.get().format(date);
    }

    public static String formatDisc(Date date) {
        return discFormat.get().format(date);
    }

    public static String formatTimeout(Date date) {
        return date == null ? "从未更新" : formatTimeout(date.getTime());
    }

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

    public static DiscType parseType(String icon) {
        switch (icon) {
            case "★":
                return DiscType.BD;
            case "○":
                return DiscType.DVD;
            case "◎":
                return DiscType.BOX;
            default:
                return null;
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

    public static <T, R> R nullSafeGet(T t, Function<T, R> mapper, R r) {
        return findNotNull(nullSafeGet(t, mapper), r);
    }

    public static <T, R> R nullSafeGet(R r, T t, Function<T, R> mapper) {
        return findNotNull(r, nullSafeGet(t, mapper));
    }

    public static <T, R> R nullSafeGet(T t, Function<T, R> mapper) {
        return t == null ? null : mapper.apply(t);
    }

    public static <T> T findNotNull(T... values) {
        return Arrays.stream(values).filter(t -> t != null).findFirst().orElse(null);
    }

    public static RuntimeException newError(String format, Object... args) {
        return new RuntimeException(String.format(format, args));
    }

    public static RuntimeException newError(Throwable cause, String format, Object... args) {
        return new RuntimeException(String.format(format, args), cause);
    }

}
