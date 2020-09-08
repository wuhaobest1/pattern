package com.wh.pattern.pipeline;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken {

    protected volatile boolean toShutdown = false;
    public final AtomicInteger reservations = new AtomicInteger(0);

    private final Queue<WeakReference<TerminateAble>> coordinatedThreads;

    public TerminationToken() {
        coordinatedThreads = new ConcurrentLinkedQueue<WeakReference<TerminateAble>>();
    }

    public boolean isToShutdown() {
        return toShutdown;
    }

    protected void setToShutdown(boolean toShutdown) {
        this.toShutdown = true;
    }

    protected void register(TerminateAble thread) {
        coordinatedThreads.add(new WeakReference<TerminateAble>(thread));
    }

    protected void notifyThreadTermination(TerminateAble thread) {
        WeakReference<TerminateAble> wrThread;
        TerminateAble otherThread;
        while (null != (wrThread = coordinatedThreads.poll())) {
            otherThread = wrThread.get();
            if (null != otherThread && otherThread != thread) {
                otherThread.terminate();
            }
        }
    }

}
