package com.wh.pattern.pipeline;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolBasedPipeExample {

    public static void main(String[] args) {

        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.MINUTES,
                        new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

        final SimplePipeline<String, String> pipeLine = new SimplePipeline<String, String>();

        Pipe<String, String> asrPipe = new AbsPipe<String, String>() {
            @Override
            public String doProcess(String input) throws PipeException {
                String result = input + "->[asr, " + Thread.currentThread().getName() + "]";
                System.out.println(result);
                return result;
            }
        };

        pipeLine.addAsThreadBasedPipe(asrPipe, threadPoolExecutor);

        Pipe<String, String> nluPipe = new AbsPipe<String, String>() {
            @Override
            public String doProcess(String input) throws PipeException {
                String result = input + "->[nlu, " + Thread.currentThread().getName() + "]";
                System.out.println(result);
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return result;
            }
        };

        pipeLine.addAsThreadBasedPipe(nluPipe, threadPoolExecutor);

        Pipe<String, String> dmPipe = new AbsPipe<String, String>() {
            @Override
            public String doProcess(String input) throws PipeException {
                String result = input + "->[dm, " + Thread.currentThread().getName() + "]";
                System.out.println(result);

                try {
                    Thread.sleep(new Random().nextInt(200));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {
                threadPoolExecutor.shutdown();

                try {
                    threadPoolExecutor.awaitTermination(timeout, unit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        pipeLine.addAsThreadBasedPipe(dmPipe, threadPoolExecutor);
        pipeLine.init(pipeLine.newDefaultPipeContext());
//        int N = 10;
//        try {
//            for (int i = 0; i < N; i++) {
//                pipeLine.process("Task-" + i);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try {
            pipeLine.process("e Segment");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pipeLine.shutdown(10, TimeUnit.SECONDS);
    }
}
