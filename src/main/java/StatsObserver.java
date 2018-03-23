import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsObserver implements  Observer{

    public AtomicInteger nNodesWithRumour;
    private int totalNodes;
    private List<Thread> threadList;
    private Queue<Integer> spreadNodes;
    public StatsObserver(int totalNodes, List<Thread> threadList) {
        this.totalNodes = totalNodes;
        nNodesWithRumour = new AtomicInteger(0);
        this.threadList = threadList;
        spreadNodes = new ConcurrentLinkedQueue<>();
    }
    @Override
    public void update(Observable o, Object arg) {
        nNodesWithRumour.incrementAndGet();
        Rumour rumour = (Rumour) arg;
        int id = rumour.getDestinationId();
        spreadNodes.add(id);
        System.out.println(id);
        if (nNodesWithRumour.get() >= totalNodes) {
            for (Thread thread : threadList) {
                thread.interrupt();
            }
            try (FileWriter writer = new FileWriter("log/output.txt")) {
                for (Integer i : spreadNodes) {
                    writer.write(i.toString() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("DONE");
        }

    }
}
