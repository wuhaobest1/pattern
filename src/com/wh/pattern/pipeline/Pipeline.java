package com.wh.pattern.pipeline;

public interface Pipeline<IN, OUT> extends Pipe<IN, OUT> {

    void addPipe(Pipe<?, ?> pipe);

}
