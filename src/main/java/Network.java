import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Network class used to pass messages between threads. It uses a timer task to sim
 */
public class Network implements Runnable {
    private ConcurrentLinkedQueue<Rumour> messageQueue;
    private List<NodeProcess> nodeList;
    private Random rg;
    private Random prg;
    private int delayFrom;
    private int delayTo;
    private Timer timer;
    private double dropProbability;

    public Network(ConcurrentLinkedQueue<Rumour> queue, List<NodeProcess> nodeList) {
        this(queue, nodeList, 900, 200, 0);
    }

    public Network(ConcurrentLinkedQueue<Rumour> queue, List<NodeProcess> nodeList, int delayFrom, int delayTo) {
        this(queue, nodeList, delayFrom, delayTo, 0);
    }

    public Network(ConcurrentLinkedQueue<Rumour> queue, List<NodeProcess> nodeList, int delayFrom, int delayTo, double dropProbability) {
        this.messageQueue = queue;
        this.nodeList = nodeList;
        this.rg = new Random(2018);
        this.prg = new Random(2018);
        this.delayFrom = delayFrom;
        this.delayTo = delayTo;
        timer = new Timer();
        this.dropProbability = dropProbability;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
                Rumour rumour = messageQueue.poll();
                if (rumour != null) {
                    boolean schedule = true;
                    if (dropProbability != 0) {
                        double d = prg.nextDouble();
                        if (d < dropProbability) {
                            schedule = false;
                        }
                    }
                    if(schedule) {
                    long delay = this.delayFrom + rg.nextInt(this.delayTo);
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                        Integer destinationId = rumour.getDestinationId();
                                        nodeList.get(destinationId).receiveRumour();
                                    }
                            }, delay);
                        }

                }
    }
        timer.cancel();
    }
}