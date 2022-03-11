package org.json;
import java.util.function.Function;

public class CustomFuture<T> implements Runnable {
    boolean finished = false;
    T result;
    Function<String, T> function;

    public CustomFuture(Function function) {
        this.function = function;
    }

    public T get() throws InterruptedException {
        while (true) {
            Thread.currentThread().sleep(50);
            if (finished) break;
        }
        return result;
    }

    @Override
    public void run() {
        result = function.apply("");
        finished = true;
    }
}
