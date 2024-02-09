import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public class SagaNotificationService implements SagaParticipant {

    private boolean _isSucceed;

    LinkedList<String> _strList;

    private static final String LOG_NAME = "[NotificationService] ";

    public SagaNotificationService(LinkedList<String> strList_, Boolean isSucceed_) {
        this._strList = strList_;
        this._isSucceed = isSucceed_;
    }

    @Override
    public boolean firstLocalTransaction(long timeout) throws TimeoutException {
        //Execution of the first local transaction that needs the First SagaPrimaryService Transaction
        log("Execution of the first transaction");

        //Transaction logic in order to invoke forwardRecovery
        if(timeout > 5000) {
            log("Execution failed");
            throw new TimeoutException("Timeout Exception");
        }

        //Transaction logic in order to fail the transaction on purpose and then rollback
        if(!_isSucceed) {
            //Transaction logic
            log("Execution failed");
            return false;
        }

        //Transaction logic
        _strList.add("First Local Transaction");
        log("Execution completed");
        log("Database content: \n" + _strList.toString());

        return true;
    }

    @Override
    public void rollBack() {
        //Rollback logic
        _strList.clear();
        log("Rollback complete");
        log("Database content: " + _strList.toString());
    }

    private void log(String str) {
        System.out.println(LOG_NAME + str);
    }

}
