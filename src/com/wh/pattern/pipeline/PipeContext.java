package com.wh.pattern.pipeline;

public interface PipeContext {

    void handleError(PipeException e);

}
