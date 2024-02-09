import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public class SagaPersistentService implements SagaParticipant {

    LinkedList<String> _strList;

    private static final String LOG_NAME = "[PersistentService] ";

    private final long _localTransactionDuration;

    public SagaPersistentService(LinkedList<String> strList_, long localTransactionDuration_) {
        this._strList = strList_;
        this._localTransactionDuration = localTransactionDuration_;
    }

    @Override
    public boolean firstLocalTransaction(long timeout) {
        //Execution of the first local transaction
        log("Execution of the first transaction");
        _strList.add("First Local Transaction");

        //Transaction logic
        try {
            Thread.sleep(_localTransactionDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log("Execution completed");
        log("Database content: \n" + _strList.toString());

        return true;
    }

    @Override
    public void rollBack() {
        //Rollback logic
        _strList.clear();
        log("RollBack completed");
        log("Database content: " + _strList.toString());
    }

    private void log(String str) {
        System.out.println(LOG_NAME + str);
    }
}
