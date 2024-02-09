import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

/**
 * @author Fabien BORYS
 * This class represents the orchestrator of the SAGA pattern. It knows all the microservices and the order that they should follow.
 */

public class Orchestrator {

    private final LinkedList<SagaParticipant> _participantList;

    /**
     * Constructor of the Orchestrator
     * @param participantList_ List with microservices
     */
    public Orchestrator(LinkedList<SagaParticipant> participantList_)
    {
        _participantList = participantList_;
    }

    /**
     * Executes the transactions
     */
    public boolean executeTransaction(long timeout) {

        for(SagaParticipant participant: _participantList) {
            try {
                //If it doesn't work, either retry or return false
                if(!participant.firstLocalTransaction(timeout)) {
                    return false;
                }
            }
            catch (TimeoutException e) {
                //Retry the transaction
                try {
                    System.out.println("[Orchestrator]: Forward Recovery...");
                    participant.firstLocalTransaction(0);
                }
                //Give Up
                catch (TimeoutException ex) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * RollBack the transactions
     */
    public void compensatingTransactions() {
        for (SagaParticipant participant : _participantList) {
            participant.rollBack();
        }
    }

}
