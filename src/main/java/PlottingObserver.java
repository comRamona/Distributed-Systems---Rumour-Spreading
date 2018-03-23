
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class PlottingObserver extends Observable implements Observer {

    public AtomicInteger nNodesWithRumour;
    private int totalNodes;
    private List<Thread> threadList;
    private long startTime;
    private float p;
    public PlottingObserver(int totalNodes, List<Thread> threadList, float p) {
        System.out.println("Started");
        System.out.println("p=" + p);
        this.totalNodes = totalNodes;
        nNodesWithRumour = new AtomicInteger(0);
        this.threadList = threadList;
        startTime = System.currentTimeMillis();
        this.p = p;
    }
    @Override
    public void update(Observable o, Object arg) {
        int n = nNodesWithRumour.incrementAndGet();
        if(n==1){
            this.startTime = System.currentTimeMillis();
        }
        Rumour rumour = (Rumour) arg;
        int id = rumour.getDestinationId();
        System.out.println(id);
        if(nNodesWithRumour.get() >= totalNodes) {
            setChanged();
            notifyObservers("Time "+ p + " " + (System.currentTimeMillis() - startTime));
            for (Thread thread : threadList) {
                thread.interrupt();
            }
            setChanged();
            notifyObservers("Done "+ p );

        }

    }
}
