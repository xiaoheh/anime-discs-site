package com.animediscs.util;

import com.animediscs.model.Disc;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
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

    public static int getSday(Disc disc) {
        long currentTime = System.currentTimeMillis();
        long releaseTime = disc.getRelease().getTime() - 3600000L;
        return (int) Math.floorDiv(releaseTime - currentTime, 86400000L);
    }

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(path));
        } catch (IOException e) {
            logger.printf(Level.WARN, "不能正确载入配置文件: %s", path);
            logger.catching(Level.WARN, e);
            throw new RuntimeException(e);
        }
        return properties;
    }

    public static String readText(File pathname) {
        try {
            return Files.readAllLines(pathname.toPath())
                    .stream()
                    .reduce((s1, s2) -> s1 + "\n" + s2)
                    .orElse("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeText(File pathname, String text) {
        pathname.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(pathname))) {
            writer.println(text);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
