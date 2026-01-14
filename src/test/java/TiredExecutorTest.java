import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import scheduling.*;

public class TiredExecutorTest {

    @Test
    public void testSubmit() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        AtomicInteger counter = new AtomicInteger(0);
        
        executor.submit(() -> counter.incrementAndGet());
        
        // Give it some time to run since submit is asynchronous
        long startTime = System.currentTimeMillis();
        while (counter.get() < 1 && System.currentTimeMillis() - startTime < 1000) {
            Thread.sleep(10);
        }
        
        assertEquals(1, counter.get());
        executor.shutdown();
    }

    @Test
    public void testSubmitAll() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(4);
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
                counter.incrementAndGet();
            });
        }
        
        executor.submitAll(tasks); // Should block until all finished
        
        assertEquals(10, counter.get());
        executor.shutdown();
    }

    @Test
    public void testGetWorkerReport() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        String report = executor.getWorkerReport();
        
        assertNotNull(report);
        assertTrue(report.contains("Worker Report:"));
        assertTrue(report.contains("Worker 0:"));
        assertTrue(report.contains("Worker 1:"));
        
        executor.shutdown();
    }
}
