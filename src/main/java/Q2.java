import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class Q2 {
    public static void main(String[] args) {

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        Graph graph = new SingleGraph("test");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("stylesheet", "graph {padding : 50px;}"
                + "node {size: 100px; fill-mode: plain;}"
                + "node.red {fill-color: red;}"
                + "node.green {fill-color: green;}");
        String fileName = "src/q1.txt";
        int startNode = 2;
        BlockingQueue<Rumour> networkQueue = new LinkedBlockingQueue<>();
        List<NodeProcess> nodeList = new ArrayList<>();
        List<Node> vizNodes = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach((String line) -> {
                String[] nodes = line.split(":");
                if(nodes.length != 2){
                    throw new IllegalArgumentException();
                }
                String node = nodes[0];
                String[] neighbours = (nodes[1].split(","));
                List<Integer> integerStream = Arrays.stream(neighbours).
                        map(Integer::parseInt).collect(Collectors.toList());
                NodeProcess newNode = new NodeProcess(Integer.parseInt(node), integerStream, networkQueue);
                nodeList.add(newNode);
                Node n = graph.addNode(node);
                vizNodes.add(n);
                n.addAttribute("ui.class", "red");
                n.addAttribute("ui.label", node);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
//        graph.addEdge("01","0","1");
//        //graph.addEdge("10","1","0");
//        Edge e= vizNodes.get(1).getEdgeBetween("0");
//        if(e==null)
//            System.out.println("Null");
//        else
//            System.out.println(e.toString());
        for(NodeProcess n: nodeList){
            for(Integer neighb: n.getNeighbours()){
                Edge edgeBetween = vizNodes.get(Integer.parseInt(n.getId())).getEdgeBetween(neighb.toString());
                if(edgeBetween==null) {
                    graph.addEdge(n.getId() + neighb.toString(), n.getId(), neighb.toString());
                }
            }
        }
        graph.display();
        Network network = new Network(networkQueue, nodeList);
        Thread networkThread = new Thread(network);
        networkThread.start();
        List<Thread> nodeThreads = new LinkedList<>();
        nodeThreads.add(networkThread);
        VizObserver observer = new VizObserver(nodeList.size(), nodeThreads, vizNodes);
        for(NodeProcess node :nodeList){
            node.addObserver(observer);
            Thread t = new Thread(node);
            t.start();
            nodeThreads.add(t);
        }
        nodeList.get(startNode).setHasRumour(true);
    }
}
