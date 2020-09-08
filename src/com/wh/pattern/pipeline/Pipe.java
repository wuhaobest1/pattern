package com.wh.pattern.pipeline;

import java.util.concurrent.TimeUnit;

public interface Pipe<IN, OUT> {

    void setNextPipe(Pipe<?, ?> nextPipe);

    void process(IN input) throws InterruptedException;

    void init(PipeContext pipeContext);

    void shutdown(long timeout, TimeUnit timeUnit);

}
