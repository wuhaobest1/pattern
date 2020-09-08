package com.wh.pattern.pipeline;

public class PipeException extends Exception {

    private static final long serialVersionUID = 8647786507719222800L;

    public final Pipe<?, ?> sourcePipe;

    public final Object input;

    public PipeException(Pipe<?, ?> sourcePipe, Object input, String message) {
        super(message);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }

    public PipeException(Pipe<?, ?> sourcePipe, Object input, String message, Throwable cause) {
        super(message, cause);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }
}
