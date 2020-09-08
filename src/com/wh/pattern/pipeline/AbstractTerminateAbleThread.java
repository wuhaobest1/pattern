package com.wh.pattern.pipeline;

public abstract class AbstractTerminateAbleThread extends Thread {

    public final TerminationToken terminationToken;

    public AbstractTerminateAbleThread() {
        super();
        this.terminationToken = new TerminationToken();
    }

    public AbstractTerminateAbleThread(TerminationToken terminationToken) {
        super();
        this.terminationToken = terminationToken;
    }

    protected abstract void doRun() throws Exception;

    protected void doCleanup(Exception cause) {
    }

    protected void doTerminate() {
    }

    @Override
    public void run() {
        Exception ex = null;
        try {
            while (true) {
                /*
                 * 在执行线程的处理逻辑前先判断线程停止的标志。
                 */
                if (terminationToken.isToShutdown()
                        && terminationToken.reservations.get() <= 0) {
                    break;
                }
                doRun();
            }

        } catch (Exception e) {
            // Allow the thread to terminate in response of a interrupt invocation
            ex = e;
        } finally {
            doCleanup(ex);
        }
    }

    @Override
    public void interrupt() {
        terminate();
    }

    public void terminate() {
        terminationToken.setToShutdown(true);
        try {
            doTerminate();
        } finally {
            // 若无待处理的任务，则试图强制终止线程
            if (terminationToken.reservations.get() <= 0) {
                super.interrupt();
            }
        }
    }
}
