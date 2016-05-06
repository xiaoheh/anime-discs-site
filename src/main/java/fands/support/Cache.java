package fands.support;

public class Cache<T> {

    private long timeout;
    private long update;
    private T value;

    public Cache(long timeout) {
        this.timeout = timeout;
    }

    public synchronized T update(Supplier<T> supplier) throws Exception {
        long currentTime = System.currentTimeMillis();
        if (currentTime - update > timeout) {
            update = currentTime;
            value = supplier.get();
        }
        return value;
    }

    public interface Supplier<T> {

        T get() throws Exception;

    }

}
