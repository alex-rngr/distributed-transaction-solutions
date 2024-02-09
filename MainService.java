import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainService {
   static final long TIME_OUT = 3000l; // 3 seconds timeout

    private List<Participant> participants;

    public MainService(List<Participant> participants) {
        this.participants = participants;
    }

    public Boolean sendRequest() {
        List<CompletableFuture<Boolean>> prepareFutures = participants.stream()
        .map(participants -> participants.requestCommit().orTimeout(TIME_OUT, TimeUnit.MILLISECONDS))
        .collect(Collectors.toList());

        return prepareFutures.stream().allMatch(CompletableFuture::join);
    }

    public void sendCommit() {
        List<CompletableFuture<Void>> commitFutures = participants.stream()
                .map(participants -> participants.commit().orTimeout(TIME_OUT, TimeUnit.MILLISECONDS))
                .collect(Collectors.toList());

        for (CompletableFuture<Void> completableFuture : commitFutures) {
            completableFuture.join();
        }    
    }

    public void sendAbort() {
        List<CompletableFuture<Void>> abortFutures = participants.stream()
                .map(participants -> participants.abort().orTimeout(TIME_OUT, TimeUnit.MILLISECONDS))
                .collect(Collectors.toList());

        for (CompletableFuture<Void> completableFuture : abortFutures) {
            completableFuture.join();
        }
    }

    public void shutdownAll() {
        for (Participant participant : participants) {
            participant.shutdown();
        }
    }
}