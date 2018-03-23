import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Q3 {

    public static PlottingObserver doSTuff(String[] args, float p) {
        if (args.length <= 1) {
            throw new IllegalArgumentException("Provide filename and start node");
        }
        String fileName = args[1];
        int startNode = 0;
        try {
            startNode = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Argument" + args[2] + " must be an integer.");
        }
        int delayFrom = 900;
        int delayTo = 1100;

        if (args.length >= 5) {
            try {
                delayFrom = Integer.parseInt(args[3]);
                delayTo = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                System.err.println("Wrong delay format, expected two integers. Using default of 900-1100");
            }
        }
        if (delayTo <= delayFrom) {
            System.err.println("Delay should be positive. Using default.");
        }
        final int delayFromFinal = delayFrom;
        final int delayPeriodFinal = delayTo - delayFrom;

        File directory = new File("log");
        if (!directory.exists())
            directory.mkdir();

        ConcurrentLinkedQueue<Rumour> networkQueue = new ConcurrentLinkedQueue<>();
        List<NodeProcess> nodeList = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach((String line) -> {
                String[] nodes = line.split(":");
                if (nodes.length != 2) {
                    throw new IllegalArgumentException();
                }
                String node = nodes[0];
                String[] neighbours = (nodes[1].split(","));
                List<Integer> integerStream = Arrays.stream(neighbours).
                        map(Integer::parseInt).collect(Collectors.toList());
                NodeProcess newNode = new NodeProcess(Integer.parseInt(node), integerStream, networkQueue, delayFromFinal, delayPeriodFinal);
                nodeList.add(newNode);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService executor = Executors.newFixedThreadPool(nodeList.size() + 1);
        Network network = new Network(networkQueue, nodeList, delayFromFinal, delayPeriodFinal, p);
        Thread networkThread = new Thread(network);
        executor.submit(networkThread);
        for (NodeProcess node : nodeList) {
            Thread t = new Thread(node);
            executor.submit(t);
        }
        PlottingObserver observer = new PlottingObserver(nodeList.size(), executor, p);
        for (NodeProcess node : nodeList) {
            node.addObserver(observer);
        }
        nodeList.get(startNode).receiveRumour();
        return observer;

    }
}
