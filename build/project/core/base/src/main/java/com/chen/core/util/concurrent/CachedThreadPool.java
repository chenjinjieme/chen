package com.chen.core.util.concurrent;

import com.chen.core.lang.Runnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void submit(Runnable runnable) {
        executorService.submit(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}