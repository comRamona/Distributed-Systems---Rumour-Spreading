import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Network implements Runnable {
    private ConcurrentLinkedQueue<Rumour> messageQueue;
    private List<NodeProcess> nodeList;
    private Random rg;
    private int delayFrom;
    private int delayTo;
    private Timer timer;

    public Network(ConcurrentLinkedQueue<Rumour> queue, List<NodeProcess> nodeList) {
        this(queue, nodeList, 900, 200);
    }

    public Network(ConcurrentLinkedQueue<Rumour> queue, List<NodeProcess> nodeList, int delayFrom, int delayTo) {
        this.messageQueue = queue;
        this.nodeList = nodeList;
        this.rg = new Random(System.currentTimeMillis());
        this.delayFrom = delayFrom;
        this.delayTo = delayTo;
        timer = new Timer();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
                Rumour rumour = messageQueue.poll();
                if (rumour != null) {
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
        timer.cancel();
    }
}