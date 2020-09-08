package com.wh.pattern.pipeline;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class WorkThreadPipeDecorator<IN, OUT> implements Pipe<IN, OUT> {

    protected final BlockingQueue<IN> workQueue;
    protected final Set<AbstractTerminateAbleThread> workerThreads = new HashSet<AbstractTerminateAbleThread>();
    protected final TerminationToken terminationToken = new TerminationToken();

    private final Pipe<IN, OUT> delegate;

    public WorkThreadPipeDecorator(Pipe<IN, OUT> delegate, int workerCount) {
        this(new SynchronousQueue<IN>(), delegate, workerCount);
    }

    public WorkThreadPipeDecorator(BlockingQueue<IN> workQueue, Pipe<IN, OUT> delegate, int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount should be positive!");
        }

        this.workQueue = workQueue;
        this.delegate = delegate;
        for (int i = 0; i < workerCount; i++) {
            workerThreads.add(new AbstractTerminateAbleThread() {

                @Override
                protected void doRun() throws Exception {
                    try {
                        dispatch();
                    } finally {
                        terminationToken.reservations.decrementAndGet();
                    }
                }
            });
        }
    }

    private void dispatch() throws InterruptedException {
        IN input = workQueue.take();
        delegate.process(input);
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        workQueue.put(input);
        terminationToken.reservations.incrementAndGet();
    }

    @Override
    public void init(PipeContext pipeCtx) {
        delegate.init(pipeCtx);
        for (AbstractTerminateAbleThread thread : workerThreads) {
            thread.start();
        }
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        for (AbstractTerminateAbleThread thread : workerThreads) {
            thread.terminate();
            try {
                thread.join(TimeUnit.MILLISECONDS.convert(timeout, unit));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        delegate.shutdown(timeout, unit);
    }
}
