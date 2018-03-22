import org.bouncycastle.crypto.prng.RandomGenerator;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class NodeProcess extends Observable implements Runnable{

    private Integer id;
    private List<Integer> neighbours;
    private Queue<Rumour> networkQueue;
    private Random rg;
    private Boolean hasRumour;
    private int delayFrom;
    private int delayTo;

    public boolean hasRumour() {
        return hasRumour;
    }

    public void setHasRumour(boolean hasRumour) {
        if(hasRumour){
            setChanged();
            notifyObservers(new Rumour(String.valueOf(System.nanoTime()), id));
        }
        this.hasRumour = hasRumour;
    }
    public List<Integer> getNeighbours(){
        return neighbours;
    }

    public String getId(){
        return id.toString();
    }

    public NodeProcess(Integer id, List<Integer> neighbours, Queue<Rumour> networkQueue){
        this(id,neighbours,networkQueue,900,200);
    }

    public NodeProcess(Integer id, List<Integer> neighbours, Queue<Rumour> networkQueue, int delayFrom, int delayTo){
        this.id = id;
        this.neighbours = neighbours;
        this.networkQueue = networkQueue;
        rg = new Random(System.currentTimeMillis());
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
                networkQueue.add(new Rumour("Secret from" + id, neighbourId));
            }
        }
    }

    public void receiveRumour(){
        if(hasRumour == false){
            System.out.println(id);
            hasRumour = true;
            long time = System.nanoTime();
            setChanged();
            notifyObservers(new Rumour(String.valueOf(time), id));
        }
    }


}
