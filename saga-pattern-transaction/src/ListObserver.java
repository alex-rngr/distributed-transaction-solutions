import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ListObserver implements Runnable {

    private LinkedList<String> _list;

    private boolean _staticCondition = true;

    public ListObserver(LinkedList<String> list_) {
        this._list = list_;
    }

    @Override
    public void run() {
        while(_staticCondition) {
            if(!_list.isEmpty()) {
                log("Observation of element : " + _list.getLast());
                return;
            }
        }
    }

    private void log(String str) {
        System.out.println("[ListObserver] " + str);
    }
}
