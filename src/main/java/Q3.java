import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Q3 {
    public PlottingObserver observer;
    public Q3(String[] args, double p) {
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
        if (delayFrom == 0 || delayTo <= delayFrom) {
            System.err.println("Delay should be positive. Using default.");
        }
        final int delayFromFinal = delayFrom;
        final int delayPeriodFinal = delayTo - delayFrom;

        File directory = new File("log");
        if (!directory.exists()) {
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
            Network network = new Network(networkQueue, nodeList, delayFromFinal, delayPeriodFinal, p);
            Thread networkThread = new Thread(network);
            networkThread.start();
            List<Thread> nodeThreads = new LinkedList<>();
            nodeThreads.add(networkThread);
            this.observer = new PlottingObserver(nodeList.size(), nodeThreads, p);
            for (NodeProcess node : nodeList) {
                node.addObserver(observer);
                Thread t = new Thread(node);
                t.start();
                nodeThreads.add(t);
            }
            nodeList.get(startNode).setHasRumour(true);

        }
    }

}
