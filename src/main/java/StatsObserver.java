import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsObserver implements Observer {

    public AtomicInteger nNodesWithRumour;
    private int totalNodes;
    private List<Thread> threadList;
    private ConcurrentLinkedQueue<Integer> spreadNodes;
    public StatsObserver(int totalNodes, List<Thread> threadList) {
        this.totalNodes = totalNodes;
        nNodesWithRumour = new AtomicInteger(0);
        this.threadList = threadList;
        spreadNodes = new ConcurrentLinkedQueue<>();
    }
    @Override
    public void update(Observable o, Object arg) {
        nNodesWithRumour.incrementAndGet();
        Rumour rumour = ((Rumour) arg);
        spreadNodes.add(rumour.getDestinationId());
        System.out.println(rumour.getMessage() + " " + rumour.getDestinationId());
        System.out.println(nNodesWithRumour + " " + totalNodes);
        if (nNodesWithRumour.get() >= totalNodes) {
            for (Thread thread : threadList) {
                thread.interrupt();
            }
        }
        try(FileWriter writer = new FileWriter("src/output.txt")){
        for(Integer i: spreadNodes) {
            writer.write(i.toString() + "\n");
        }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
