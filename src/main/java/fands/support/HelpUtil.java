package fands.support;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Function;

@Service
public abstract class HelpUtil {

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

}
