import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public interface SagaParticipant {

    public boolean firstLocalTransaction(long timeout) throws TimeoutException;
    public void rollBack();


}
