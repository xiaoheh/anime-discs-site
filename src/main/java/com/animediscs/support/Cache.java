package com.animediscs.support;

public class Cache<T> {

    private long timeout;
    private long invalid;
    private T value;

    public Cache(long timeout) {
        this.timeout = timeout;
    }

    public synchronized T update(Supplier<T> supplier) throws Exception {
        long current = System.currentTimeMillis();
        if (value == null || current > invalid) {
            invalid = current + timeout;
            value = supplier.get();
        }
        return value;
    }

    public interface Supplier<T> {

        T get() throws Exception;

    }

}
