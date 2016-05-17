package com.animediscs.util;

import org.apache.logging.log4j.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;

public abstract class Helper {

    private static Logger logger = LogManager.getLogger(Helper.class);

    public static void waitFor(Object object) {
        synchronized (object) {
            try {
                object.wait(1000);
            } catch (InterruptedException e) {
                logger.catching(Level.WARN, e);
            }
        }
    }

    public static void nodify(Object object) {
        synchronized (object) {
            object.notify();
        }
    }

    public static <T, R> R nullSafeGet(T t, Function<T, R> mapper) {
        return t == null ? null : mapper.apply(t);
    }

    public static List<String> readAllLines(String path) {
        try {
            return Files.readAllLines(new File(path).toPath());
        } catch (IOException e) {
            logger.catching(Level.WARN, e);
            throw new RuntimeException("打开文件错误: " + path, e);
        }
    }

}
