package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            double min = 0.5;
            double max = 1.5;
            double randomValue = Math.random() * (max - min) + min;
            workers[i] = new TiredThread(i, randomValue);
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        try {
            TiredThread worker = idleMinHeap.take();

            Runnable wrappedTask = () -> {
                try {
                    task.run();
                } finally {
                    idleMinHeap.add(worker);
                    inFlight.decrementAndGet();
                }
            };

            inFlight.incrementAndGet();
            worker.newTask(wrappedTask);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        Object lock = new Object();
        AtomicInteger TasksLeft = new AtomicInteger(0);

        for (Runnable task : tasks) {
            TasksLeft.incrementAndGet();
            Runnable wrappedTask = () -> {
                try {
                    task.run();
                } finally {
                    TasksLeft.decrementAndGet();
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            };
            submit(wrappedTask);

        }
        synchronized (lock) {

            while (TasksLeft.get() > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

            }
        }

    }

    public void shutdown() throws InterruptedException {
        for (TiredThread tiredThread : idleMinHeap) {
            tiredThread.shutdown();
        }
    }

    public synchronized String getWorkerReport() {
        String report = "Worker Report:\n";
        report += "--------------------------------------------------\n";
        for (TiredThread worker : workers) {
            report += "Worker " + worker.getWorkerId() + 
                      ": fatigue=" + String.format("%.2f", worker.getFatigue()) + 
                      ", timeUsed=" + String.format("%.3fms", worker.getTimeUsed() / 1_000_000.0) +
                      ", timeIdle=" + String.format("%.3fms", worker.getTimeIdle() / 1_000_000.0) + "\n";
        }
        return report;
    }
}
