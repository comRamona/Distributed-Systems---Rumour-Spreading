
import java.text.DecimalFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlottingObserver extends Observable implements Observer {

    public AtomicInteger nNodesWithRumour;
    private int totalNodes;
    private ExecutorService executorService;
    private long startTime;
    private float p;
    public PlottingObserver(int totalNodes, ExecutorService executorService, float p) {
        this.totalNodes = totalNodes;
        nNodesWithRumour = new AtomicInteger(0);
        this.executorService = executorService;
        startTime = System.currentTimeMillis();
        this.p = p;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        System.out.println(df.format(p));
    }
    @Override
    public void update(Observable o, Object arg) {
        int n = nNodesWithRumour.incrementAndGet();
        if(n==1){
            this.startTime = System.currentTimeMillis();
        }
        Rumour rumour = (Rumour) arg;
        int id = rumour.getDestinationId();
        System.out.println(id);
        if(nNodesWithRumour.get() == totalNodes) {

            setChanged();
            notifyObservers("Time "+ p + " " + (System.currentTimeMillis() - startTime));
            executorService.shutdown();
            try {
                // Wait a while for existing tasks to terminate
                if (!executorService.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!executorService.awaitTermination(50, TimeUnit.MILLISECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                executorService.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }

            setChanged();
            notifyObservers("Done "+ p);

        }

    }
}
