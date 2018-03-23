import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import org.graphstream.stream.sync.SourceTime;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Repeatedly runs the rumour spreading algorithm. It is an observer class waiting for the algorithm to terminate.
 * Uppon termination, it increases the probability of dropping the message and rerun the experiment.
 * When p reaches 0.95 it plots the results and saves the plot in plots/rumourtime.png
 */
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
            System.out.println("p= " + df.format(observedP) + " Time: " + time);
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
                        "Running time" , times);
                JFreeChart freeChart = chart.getLineChart();
                chart.pack( );
                RefineryUtilities.centerFrameOnScreen( chart );
                chart.setVisible( true );

                File directory = new File("plots");
                if (! directory.exists()){
                    directory.mkdir();
                }

                File imageFile = new File("plots/rumourtime.png");
                int width = 800;
                int height = 600;

                try {
                    ChartUtilities.saveChartAsPNG(imageFile, freeChart, width, height);
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    private class LineChart_AWT extends ApplicationFrame {

        private JFreeChart lineChart;
        public LineChart_AWT( String applicationTitle, long[] times ) {
            super(applicationTitle);
            XYDataset dataset = createDataset(times);

            lineChart = ChartFactory.createScatterPlot(
                    "Rumour Spreading Running Time",
                    "Time","p", dataset, PlotOrientation.HORIZONTAL, false, true, false);
            ChartPanel chartPanel = new ChartPanel( lineChart );
            chartPanel.setPreferredSize( new java.awt.Dimension( 800 , 600 ) );
            setContentPane( chartPanel );

            XYPlot plot = (XYPlot) lineChart.getPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesLinesVisible(0, true);
            plot.setRenderer(renderer);
        }

        public JFreeChart getLineChart(){
            return lineChart;
        }

        private XYDataset createDataset(long[] times) {
            XYSeriesCollection dataset = new XYSeriesCollection();
            XYSeries series1 = new XYSeries("", false, true);
            float p = 0.05f;
            for(long time: times){
                series1.add(time, p);
                p += 0.05f;
            }
            dataset.addSeries(series1);
            return dataset;
        }
    }
}

