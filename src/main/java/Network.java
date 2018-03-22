import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class Network implements Runnable {
    private BlockingQueue<Rumour> messageQueue;
    private List<NodeProcess> nodeList;
    private Random rg;
    public Network(BlockingQueue<Rumour> queue, List<NodeProcess> nodeList){
        this.messageQueue = queue;
        this.nodeList = nodeList;
        this.rg = new Random(System.currentTimeMillis());
    }
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                Rumour rumour = messageQueue.take();
                if(rumour != null){
                    long delay = 900 + rg.nextInt(200);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Integer destinationId = rumour.getDestinationId();
                            nodeList.get(destinationId).sendRumour();
                            timer.cancel();
                        }
                    }, delay);

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
