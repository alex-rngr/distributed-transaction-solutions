import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NotificationService implements Participant {
    private static final Lock lock = new ReentrantLock();

    // Instance level executor
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CompletableFuture<Boolean> requestCommit() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executor.execute(() -> {
            log("Preparing");
            boolean lockAcquired = lock.tryLock();
            // Logic of the transaction

            future.complete(lockAcquired);
        });
        return future;
    }
    
    public CompletableFuture<Void> commit() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.execute(() -> {
            log("Committing");
            lock.unlock();
            future.complete(null);
        });
        return future;
    }
    
    public CompletableFuture<Void> abort() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.execute(() -> {
            log("Rollback");
            // Rollback logic
            lock.unlock();
            future.complete(null);
        });
        return future;
    }

    private void log(String str) {
        System.out.println("[NotificationService] " + str);
    }

      
    public void shutdown() {
        executor.shutdown();
    }
}
