import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

public class Main {
    public static void log(String str) {
        System.out.println("[MainService] " + str);
    }

    public static void main(String[] args) {
        log("** Normal scenario **");
        normalScenario();
        log("\n** Transaction rollback scenario **");
        rollbackScenario();
        log("\n** Blocking scenario **");
        blockingTimeoutScenario();
    }
    
    public static void normalScenario() {
        // Deployment
        List<Participant> participants = new ArrayList<>();
        participants.add(new PersistentService());
        participants.add(new NotificationService());
        MainService mainService = new MainService(participants);
            
        // Transaction (full behavior)
        log("Sending commit request");
        boolean allAgree = mainService.sendRequest();
        
        if (!allAgree) {
            log("Not ready, sending rollback");
            mainService.sendAbort();
            log("Rollback complete");
            return;
        }

        log("All is ready, sending commit");
        mainService.sendCommit();

        log("Transaction completed");
         
        // Stop services
        mainService.shutdownAll();
    }

    public static void rollbackScenario() {
        // Deployment
        List<Participant> participants = new ArrayList<>();
        participants.add(new PersistentService(false));
        participants.add(new NotificationService());
        MainService mainService = new MainService(participants);
        
        // Transaction
        log("Sending commit request");
        boolean allAgree = mainService.sendRequest();
        
        if (!allAgree) {
            log("Not ready, sending rollback");
            mainService.sendAbort();
            log("Rollback complete");    
        }
        
        // The rest of the protocol is not pursued
        
        // Stop services
        mainService.shutdownAll();
    } 
    
    public static void blockingTimeoutScenario() {
        // Deployment
        List<Participant> participants = new ArrayList<>();
        participants.add(new PersistentService(5000l));
        participants.add(new NotificationService());
        MainService mainService = new MainService(participants);
        

        // Transaction
        log("Sending commit request");

        try {
            mainService.sendRequest();
        } catch(CompletionException e) {
            /*
             * One way of handling a blocking request is by 
             * using a timeout mecanism which triggers a 
             * rollback of the transaction assuming failure of the
             * non respondive participant
             */
            log("Timeout of commit request");
            mainService.sendAbort();
            log("Transaction rollback complete");
        }

        // The rest of the protocol is not pursued
        mainService.shutdownAll();
    }

}
