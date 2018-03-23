import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        this(queue, nodeList, 900, 200, 0);
    }

    public Network(ConcurrentLinkedQueue<Rumour> queue, List<NodeProcess> nodeList, int delayFrom, int delayTo, double dropProbability) {
        this.messageQueue = queue;
        this.nodeList = nodeList;
        this.rg = new Random(System.currentTimeMillis());
        this.prg = new Random((System.currentTimeMillis()));
        this.delayFrom = delayFrom;
        this.delayTo = delayTo;
        timer = new Timer();
        this.dropProbability = 0;
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
                    if(schedule){
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