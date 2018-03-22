import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GraphTests {

    private static void writeGraphToFile(Graph graph){
        String graphAdj = "";
        boolean firstLine = true;
        for(Node n: graph.getEachNode()){
            if(!firstLine){
                graphAdj += "\n";
            }
            else {
                firstLine = false;
            }
            graphAdj += n.getIndex()+":";
            Iterator<Node> neighborNodeIterator = n.getNeighborNodeIterator();
            boolean isFirst = true;
            while(neighborNodeIterator.hasNext()){
                Node neigh = neighborNodeIterator.next();
                if(isFirst){
                    graphAdj += neigh.getIndex();
                    isFirst = false;
                }
                else {
                    graphAdj += "," + neigh.getIndex();
                }
            }
        }

        try(FileWriter writer = new FileWriter("src/"+graph.toString()+".txt")){
            writer.write(graphAdj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Graph getFlowerSnark(){
        Graph graph = new SingleGraph("flowersnark");
        Generator gen = new FlowerSnarkGenerator();
        gen.addSink(graph);
        gen.begin();

        for(int i=0; i<100; i++) {
            gen.nextEvents();
        }
        gen.end();
        return graph;
    }
    private static  Graph getWattsStrogatzGraph(){
        Graph graph = new SingleGraph("wattsstrogatz");
        Generator gen = new WattsStrogatzGenerator(20, 2, 0.5);

        gen.addSink(graph);
        gen.begin();
        while(gen.nextEvents()) {}
        gen.end();
       return graph;

    }

    private static  Graph getFullyConnected(){
        Graph graph = new SingleGraph("fullyconnected");

        Generator gen = new FullGenerator();
        gen.addSink(graph);
        gen.begin();
        for(int i=0; i<100; i++) {
            gen.nextEvents();
        }
        gen.end();
        //graph.display(true);
        return graph;
    }

    private static  Graph getGrid(){
        Graph graph = new SingleGraph("grid");
        Generator gen = new GridGenerator();
        gen.addSink(graph);
        gen.begin();

        for(int i=0; i<10; i++) {
            gen.nextEvents();
        }
        gen.end();
        //graph.display(false);
        return graph;
    }

    public static void main(String[] args) {
        writeGraphToFile(getWattsStrogatzGraph());
        writeGraphToFile(getFullyConnected());
        writeGraphToFile(getGrid());
        writeGraphToFile(getFlowerSnark());

    }
}
