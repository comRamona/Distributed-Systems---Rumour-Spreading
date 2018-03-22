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
    private BlockingQueue<Rumour> networkQueue;
    private Random rg;
    private AtomicBoolean hasRumour;

    public boolean hasRumour() {
        return hasRumour.get();
    }

    public void setHasRumour(boolean hasRumour) {
        if(hasRumour){
            setChanged();
            notifyObservers(new Rumour(String.valueOf(System.nanoTime()), id));
        }
        this.hasRumour.set(hasRumour);
    }
    public List<Integer> getNeighbours(){
        return neighbours;
    }

    public String getId(){
        return id.toString();
    }

    public NodeProcess(Integer id, List<Integer> neighbours, BlockingQueue<Rumour> networkQueue){
         this.id = id;
         this.neighbours = neighbours;
         this.networkQueue = networkQueue;
         rg = new Random(System.currentTimeMillis());
         this.hasRumour = new AtomicBoolean(false);
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                sleep(900 + rg.nextInt(200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(hasRumour.get()) {

                int index = rg.nextInt(neighbours.size());
                Integer neighbourId = neighbours.get(index);
                networkQueue.add(new Rumour("Secret from" + id, neighbourId));
                System.out.println("Sent from " + id + " to " + neighbourId + " at " + System.nanoTime());

            }
        }
    }
    public void sendRumour(){
        if(hasRumour.compareAndSet(false, true)){
            long time = System.nanoTime();
            setChanged();
            notifyObservers(new Rumour(String.valueOf(time), id));
            System.out.println("Received at " + System.nanoTime() + " by " + id);
        }
    }


}
