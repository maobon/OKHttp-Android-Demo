package com.demo.okhttp;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SerialExecutor implements Executor {

    private Queue<Runnable> tasks = new ArrayDeque<>();
    private Executor executor;
    private Runnable active;

    public SerialExecutor() {
        this.executor = new MyExecutor();
    }

    @Override
    public synchronized void execute(final Runnable r) {

        tasks.offer(new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } finally {
                    nextScheduleTask();
                }
            }
        });

        if (active == null) {
            nextScheduleTask();
        }
    }

    private synchronized void nextScheduleTask() {
        active = tasks.poll();
        if (active != null) {
            executor.execute(active);
        } else {
            if (callback != null)
                callback.onComplete();
        }
    }

    public interface Callback {

        void onComplete();
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    //
    private static class MyExecutor implements Executor {

        @Override
        public void execute(@NotNull Runnable command) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(command);
        }
    }
}
