package com.wh.pattern.pipeline;

import java.util.concurrent.TimeUnit;

public abstract class AbsPipe<IN, OUT> implements Pipe<IN, OUT> {

    protected volatile Pipe<?, ?> nextPipe;

    protected volatile PipeContext pipeContext;

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        this.nextPipe = nextPipe;
    }

    @Override
    public void process(IN input) throws InterruptedException {
        OUT out = null;
        try {
            out = doProcess(input);
            if (nextPipe != null) {
                if (out != null) {
                    ((Pipe<IN, ?>) nextPipe).process((IN) out);
                }
            }
        } catch (PipeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(PipeContext pipeContext) {
        this.pipeContext = pipeContext;
    }

    @Override
    public void shutdown(long timeout, TimeUnit timeUnit) {

    }

    public abstract OUT doProcess(IN input) throws PipeException;
}
