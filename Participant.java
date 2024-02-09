import java.util.concurrent.CompletableFuture;

public interface Participant {

    public CompletableFuture<Boolean> requestCommit();
    public CompletableFuture<Void> commit();
    public CompletableFuture<Void> abort();

    public void shutdown();

}