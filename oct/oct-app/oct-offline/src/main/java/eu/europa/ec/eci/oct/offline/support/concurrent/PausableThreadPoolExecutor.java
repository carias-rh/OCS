package eu.europa.ec.eci.oct.offline.support.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: micleva
 * @created: 11/18/11
 * @project OCT
 */
public class PausableThreadPoolExecutor extends ThreadPoolExecutor {
    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    public PausableThreadPoolExecutor(int threadsNr, int threadPriority) {
        super(threadsNr, threadsNr, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        // bring the thread priority between min and max priority
        final int normalizedThreadPriority = Math.max(Thread.MIN_PRIORITY, Math.min(threadPriority, Thread.MAX_PRIORITY));

        this.setThreadFactory(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(false);
                if (thread.getPriority() != normalizedThreadPriority) {
                    thread.setPriority(normalizedThreadPriority);
                }
                return thread;
            }
        });
    }

    protected void beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable);
        pauseLock.lock();
        try {
            while (isPaused) unpaused.await();
        } catch (InterruptedException ie) {
            thread.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}
