import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.chart.LineChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Q3Helper implements Observer{
    float p;
    String[] args;
    long[] times;

    public Q3Helper(String[] args, float p){
        this.p = p;
        this.args = args;
        int n_exp = (int)(((0.95f - 0.05f) / 0.05f));
        times = new long[n_exp + 1];
        PlottingObserver observer = Q3.doSTuff(args, p);
        observer.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        String message[] = ((String) arg).split(" ");
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        if(message[0].equals("Time")) {
            float observedP = Float.parseFloat(message[1]);
            if(observedP!=p){
                System.err.println("Something went wrong. Observed p different from what was expected.");
            }
            long time = Long.parseLong(message[2]);
            int pos = (int) ((p -0.05f)/(0.05f));
            times[pos] = time;
            System.out.println("p= " + df.format(observedP) + "Time: " + time);
        }
        else if(message[0].equals("Done")) {
            float observedP = Float.parseFloat(message[1]);
            if(observedP!=p){
                System.out.println(p + " " + observedP);
                System.err.println("Something went wrong. Observed p different from what was expected.");
            }
            p += 0.05f;
            if (p < 1) {
                PlottingObserver observer = Q3.doSTuff(args, p);
                observer.addObserver(this);
            }
            else{
                LineChart_AWT chart = new LineChart_AWT(
                        "Running time" ,
                        "Rumour spreading rumour time", times);
                chart.displayAndSave();
            }
        }
    }
}

class LineChart_AWT extends ApplicationFrame {

    private JFreeChart lineChart;
    public LineChart_AWT( String applicationTitle , String chartTitle, long[] times ) {
        super(applicationTitle);
        XYDataset dataset = createDataset(times);
        lineChart = ChartFactory.createScatterPlot(
                "Rumour Spreading Running Time",
                "p", "Time", dataset);
        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }
    public void displayAndSave(){
        this.pack( );
        RefineryUtilities.centerFrameOnScreen( this );
        this.setVisible( true );

        File directory = new File("plots");
        if (! directory.exists()){
            directory.mkdir();
        }

        File imageFile = new File("plots/rumourtime.png");
        int width = 640;
        int height = 480;

//        try {
//            ChartUtilities.saveChartAsPNG(imageFile, lineChart, width, height);
//        } catch (IOException ex) {
//            System.err.println(ex);
//        }

    }

    private XYDataset createDataset(long[] times) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series1 = new XYSeries("Time", false);
        float p = 0.05f;
        for(long time: times){
            series1.add(p, time);
            p += 0.05f;
        }
        dataset.addSeries(series1);
        return dataset;
    }
}