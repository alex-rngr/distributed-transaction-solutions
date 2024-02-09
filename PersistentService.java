import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PersistentService implements Participant {
    private static final Lock lock = new ReentrantLock();

    // Instance level executor
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private long localTransactionDuration = 0;
    private boolean isReady = true; 

    public PersistentService() {
    }

    public PersistentService(boolean isReady) {
        this.isReady = isReady;
    }

    public PersistentService(Long localTransactionDuration) {
        this.localTransactionDuration = localTransactionDuration;
    }

    public CompletableFuture<Boolean> requestCommit() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executor.execute(() -> {
            log("Preparing");
            boolean lockAcquired = lock.tryLock();
            
            // Logic of the transaction
            try {
                Thread.sleep(localTransactionDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            future.complete(lockAcquired && isReady);
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
        System.out.println("[PersistentService] " + str);
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
