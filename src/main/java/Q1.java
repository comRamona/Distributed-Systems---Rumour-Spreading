import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Q1 {
    public static void main(String[] args) {

        String fileName = "src/q1.txt";
        int startNode = 2;
        BlockingQueue<Rumour> networkQueue = new LinkedBlockingQueue<>();
        List<NodeProcess> nodeList = new ArrayList<>();
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
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        Network network = new Network(networkQueue, nodeList);
        Thread networkThread = new Thread(network);
        networkThread.start();
        List<Thread> nodeThreads = new LinkedList<>();
        nodeThreads.add(networkThread);
        StatsObserver observer = new StatsObserver(nodeList.size(), nodeThreads);
        for(NodeProcess node :nodeList){
            node.addObserver(observer);
            Thread t = new Thread(node);
            t.start();
            nodeThreads.add(t);
        }
        nodeList.get(startNode).setHasRumour(true);
    }
}
