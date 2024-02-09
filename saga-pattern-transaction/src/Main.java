import java.util.LinkedList;

public class Main {

    public static void log(String str) {
        System.out.println("[MainService] " + str);
    }

    public static void main(String...Args) {
        System.out.println("***** NORMAL SCENARIO *****");
        normalScenario();
        System.out.println("\n***** ROLLBACK SCENARIO *****");
        rollbackScenario();
        System.out.println("\n***** READ BEFORE TRANSACTION SCENARIO *****");
        readBeforeTransactionScenario();
        System.out.println("\n***** FORWARD RECOVERY SCENARIO *****");
        forwardRecoveryScenario();
    }

    public static void normalScenario() {
        //Deployment
        LinkedList<String> persistentList = new LinkedList<>();
        LinkedList<String> notificationList = new LinkedList<>();

        LinkedList<SagaParticipant> participantList = new LinkedList<>();
        participantList.add(new SagaPersistentService(persistentList, 0));
        participantList.add(new SagaNotificationService(notificationList, true));
        Orchestrator orchestrator = new Orchestrator(participantList);

        // Transaction (full behavior)
        log("Starting Transaction");

        boolean allComplete = orchestrator.executeTransaction(0);

        if(!allComplete) {
            log("All transaction not completed, sending rollback");
            orchestrator.compensatingTransactions();
            log("Rollback complete");
            return;
        }

        log("Transaction completed");

    }

    public static void rollbackScenario() {
        //Deployment
        LinkedList<String> persistentList = new LinkedList<>();
        LinkedList<String> notificationList = new LinkedList<>();

        LinkedList<SagaParticipant> participants = new LinkedList<>();
        participants.add(new SagaPersistentService(persistentList, 0));
        participants.add(new SagaNotificationService(notificationList, false));
        Orchestrator orchestrator = new Orchestrator(participants);

        // Transaction
        log("Starting Transaction");
        boolean isComplete = orchestrator.executeTransaction(0);

        if (!isComplete) {
            System.out.println();
            log("All transaction not completed, sending rollback");
            orchestrator.compensatingTransactions();
            log("Rollback complete");

            System.out.println();

            //Idempotence
            log("IdemPotent property");
            orchestrator.compensatingTransactions();
            log("Rollback complete");
            return;
        }

        // The rest of the protocol is not pursued

    }

    public static void readBeforeTransactionScenario() {
        //Deployment
        LinkedList<String> persistentList = new LinkedList<>();
        LinkedList<String> notificationList = new LinkedList<>();

        LinkedList<SagaParticipant> participants = new LinkedList<>();
        participants.add(new SagaPersistentService(persistentList, 5000));
        participants.add(new SagaNotificationService(notificationList, true));
        Orchestrator orchestrator = new Orchestrator(participants);

        //Read the PersistentService Queue while writing
        ListObserver listObserver = new ListObserver(persistentList);

        Thread thread = new Thread(listObserver);
        thread.start();

        // Transaction
        log("Starting Transaction");
        boolean isComplete = orchestrator.executeTransaction(0);

        if (!isComplete) {
            System.out.println();
            log("All transaction not completed, sending rollback");
            orchestrator.compensatingTransactions();
            log("Rollback complete");
            return;
        }

        // The rest of the protocol is not pursued

    }

    public static void forwardRecoveryScenario() {
        //Deployment
        LinkedList<String> persistentList = new LinkedList<>();
        LinkedList<String> notificationList = new LinkedList<>();

        LinkedList<SagaParticipant> participants = new LinkedList<>();
        participants.add(new SagaPersistentService(persistentList, 0));
        participants.add(new SagaNotificationService(notificationList, true));
        Orchestrator orchestrator = new Orchestrator(participants);

        // Transaction
        log("Starting Transaction");
        boolean isComplete = orchestrator.executeTransaction(6000);

        if (!isComplete) {
            System.out.println();
            log("All transaction not completed, sending rollback");
            orchestrator.compensatingTransactions();
            log("Rollback complete");
            return;
        }

        // The rest of the protocol is not pursued

    }

}
