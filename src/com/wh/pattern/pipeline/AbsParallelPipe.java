package com.wh.pattern.pipeline;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AbsParallelPipe<IN, OUT, V> extends AbsPipe<IN, OUT> {

    private final ExecutorService executorService;

    public AbsParallelPipe(BlockingQueue<IN> queue, ExecutorService executorService) {
        super();
        this.executorService = executorService;
    }

    protected abstract List<Callable<V>> buildTasks(IN input) throws Exception;

    protected abstract OUT combineResult(List<Future<V>> subTaskResult) throws Exception;

    protected List<Future<V>> invokeParallel(List<Callable<V>> tasks) throws Exception {
        return executorService.invokeAll(tasks);
    }

    @Override
    public OUT doProcess(IN input) throws PipeException {
        OUT out = null;
        try {
            out = combineResult(invokeParallel(buildTasks(input)));
        } catch (Exception e) {
            throw new PipeException(this, input, "Task faild", e);
        }
        return out;
    }
}
