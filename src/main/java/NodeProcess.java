import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

/**
 * Simulates an individual process which runs the push protocol every delay_interval seconds.
 * It selects a random neighbour and sends the rumour using the network.
 */
public class NodeProcess extends Observable implements Runnable{

    private Integer id;
    private List<Integer> neighbours;
    private Queue<Rumour> networkQueue;
    private Random rg;
    private Boolean hasRumour;
    private int delayFrom;
    private int delayTo;

    public List<Integer> getNeighbours(){
        return neighbours;
    }

    public String getId(){
        return id.toString();
    }

    public NodeProcess(Integer id, List<Integer> neighbours, ConcurrentLinkedQueue<Rumour> networkQueue, int delayFrom, int delayTo){
        this.id = id;
        this.neighbours = neighbours;
        this.networkQueue = networkQueue;
        rg = new Random(2018);
        this.hasRumour = false;
        this.delayFrom = delayFrom;
        this.delayTo = delayTo;
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                sleep(delayFrom + rg.nextInt(delayTo));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(hasRumour) {
                int index = rg.nextInt(neighbours.size());
                Integer neighbourId = neighbours.get(index);
                networkQueue.add(new Rumour("Rumour from" + id, neighbourId));
            }
        }
    }
    public void receiveRumour(){
        if(hasRumour == false){
            hasRumour = true;
            long time = System.nanoTime();
            setChanged();
            notifyObservers(new Rumour(String.valueOf(time), id));
        }
    }
}
