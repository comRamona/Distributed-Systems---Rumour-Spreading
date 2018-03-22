import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Q1 {
    public static void main(String[] args) {
        if(args.length == 0){
            throw new IllegalArgumentException("Provide filename and start node");
        }
        String fileName = args[0];
        int startNode = 0;
        try {
            startNode = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Argument" + args[1] + " must be an integer.");
        }

        ConcurrentLinkedQueue<Rumour> networkQueue = new ConcurrentLinkedQueue<>();
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
