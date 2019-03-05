package com.chen.core.util.function;

public interface Consumer3<T, E extends Throwable, F extends Throwable, G extends Throwable> {
    void accept(T t) throws E, F, G;
}
