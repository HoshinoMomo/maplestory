package net.sf.odinms.server.timer;

import net.sf.odinms.server.Randomizer;
import net.sf.odinms.tools.FileoutputUtil;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Timer {

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    protected String file;
    protected String name;

    public void start() {
        if (scheduledThreadPoolExecutor != null && !scheduledThreadPoolExecutor.isShutdown() && !scheduledThreadPoolExecutor.isTerminated()) {
            return;
        }
        file = "logs/" + name + "_.rtf";
        final ThreadFactory thread = new ThreadFactory() {

            private final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                final Thread t = new Thread(r);
                t.setName(name + threadNumber.getAndIncrement());
                return t;
            }
        };

        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3, thread);
        scheduledThreadPoolExecutor.setKeepAliveTime(10, TimeUnit.MINUTES);
        scheduledThreadPoolExecutor.allowCoreThreadTimeOut(true);
        scheduledThreadPoolExecutor.setCorePoolSize(4);
        scheduledThreadPoolExecutor.setMaximumPoolSize(8);
        scheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }

    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
        if (scheduledThreadPoolExecutor == null) {
            return null;
        }
        return scheduledThreadPoolExecutor.scheduleAtFixedRate(new LoggingSaveRunnable(r, file), delay, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime) {
        if (scheduledThreadPoolExecutor == null) {
            return null;
        }
        return scheduledThreadPoolExecutor.scheduleAtFixedRate(new LoggingSaveRunnable(r, file), 0, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        if (scheduledThreadPoolExecutor == null) {
            return null;
        }
        return scheduledThreadPoolExecutor.schedule(new LoggingSaveRunnable(r, file), delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
        return schedule(r, timestamp - System.currentTimeMillis());
    }

    private static class LoggingSaveRunnable implements Runnable {

        Runnable r;
        String file;

        public LoggingSaveRunnable(final Runnable r, final String file) {
            this.r = r;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                r.run();
            } catch (Throwable t) {
                FileoutputUtil.outputFileError(file, t);
                //t.printStackTrace(); //mostly this gives un-needed errors... that take up a lot of space
            }
        }
    }
}
