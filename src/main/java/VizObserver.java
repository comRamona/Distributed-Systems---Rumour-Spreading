import org.graphstream.graph.Node;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class VizObserver implements Observer {

    public AtomicInteger nNodesWithRumour;
    private int totalNodes;
    private List<Thread> threadList;
    private ConcurrentLinkedQueue<Integer> spreadNodes;
    private List<Node> vizNodeList;

    public VizObserver(int totalNodes, List<Thread> threadList, List<Node> vizNodeList) {
        this.totalNodes = totalNodes;
        nNodesWithRumour = new AtomicInteger(0);
        this.threadList = threadList;
        this.vizNodeList = vizNodeList;
        spreadNodes = new ConcurrentLinkedQueue<>();
    }
    @Override
    public void update(Observable o, Object arg) {
        nNodesWithRumour.incrementAndGet();
        Rumour rumour = ((Rumour) arg);
        int id = rumour.getDestinationId();
        spreadNodes.add(id);
        vizNodeList.get(id).setAttribute("ui.class","green");
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
