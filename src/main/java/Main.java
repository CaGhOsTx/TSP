import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static class Distance {
        public static double between(Location a, Location b){
            final double radius = 6371;

            double lat_diff = (a.latitude - b.latitude);
            double long_diff = (a.longitude - b.longitude);

            double a1 = Math.pow(Math.sin(lat_diff / 2), 2) + Math.pow(Math.sin(long_diff / 2), 2) * Math.cos(a.latitude)
                    * Math.cos(b.latitude);
            return radius* 2 * Math.asin(Math.sqrt(a1));
        }
    }

    static class Location {
        int id;
        double latitude, longitude;

        public Location(int id, double latitude, double longitude) {
            this.id = id;
            this.latitude = Math.toRadians(latitude);
            this.longitude = Math.toRadians(longitude);
        }



        @Override
        public String toString() {
            return "Location{" +
                    "id=" + id +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    static double[][] matrix;
    static Solution minimum = new Solution(null, Double.MAX_VALUE);

    public static void main(String[] args) throws IOException {
        try(var coords = Files.lines(Path.of("coords.txt"))) {
            var list = coords.map(s -> s.split(","))
                    .map(arr -> new Location(Integer.parseInt(arr[0]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2])))
                    .toList();
            matrix = createMatrix(list);
            List<Thread> threads = new ArrayList<>();
            try (var writer = Files.newBufferedWriter(Path.of("Solution.txt"))) {
                for (int i = 0; i < 4; i++) {
                    threads.add(new Thread(() -> {
                        while (true) {
                            var solution = tsp();
                            if (solution.distance < minimum.distance) {
                                minimum = solution;
                                System.out.println(minimum);
                                try {
                                    writer.write(minimum.toString());
                                    writer.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (Thread.interrupted())
                                return;
                        }
                    }));
                    threads.get(i).start();
                }
                Scanner sc = new Scanner(System.in);
                while (sc.hasNextLine()) {
                    Thread.onSpinWait();
                }
                threads.forEach(Thread::interrupt);
                System.out.println("Stopping program");
            }
        }
    }

    private static double[][] createMatrix(List<Location> list) {
        var matrix = new double[121][121];
        for (int i = 0; i < 121; i++)
            for (int j = 0; j < 121; j++)
                matrix[i][j] = Distance.between(list.get(i), list.get(j));
        return matrix;
    }

    public static Solution tsp() {
        var sequence = greedy();
        sequence.add(0);
        double totalDistance = sumDistances(sequence);
        totalDistance = removeCrossOvers(totalDistance, sequence);
        return new Solution(sequence, totalDistance);
    }

    private static ArrayList<Integer> greedy() {
        var visited = new HashSet<Integer>();
        var sequence = new ArrayList<Integer>();
        var noisy = noisify(matrix);
        sequence.add(0);
        visited.add(0);
        for (int i = 0;;) {
            double localMin = Double.MAX_VALUE;
            int localMinIndex = -1;
            for (int j = 0; j < noisy[i].length; j++) {
                if(j != i && !visited.contains(j) && noisy[i][j] < localMin) {
                    localMin = noisy[i][j];
                    localMinIndex = j;
                }
            }
            if((i = localMinIndex) == -1) break;
            sequence.add(localMinIndex);
            visited.add(localMinIndex);
        }
        return sequence;
    }

    private static double removeCrossOvers(double totalDistance, ArrayList<Integer> sequence) {
        for(int i = 1; i < sequence.size() - 1; i++) {
            for(int j = i; j < sequence.size() - 1; j++) {
                reverse(sequence.subList(i, j));
                double distance = sumDistances(sequence);
                if(distance >= totalDistance)
                    reverse(sequence.subList(i, j));
                else {
                    totalDistance = distance;
                }
            }
        }
        return totalDistance;
    }

    private static double[][] noisify(double[][] matrix) {
        var r = ThreadLocalRandom.current();
        double[][] noisy = new double[121][121];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                noisy[i][j] = matrix[i][j] + r.nextInt(0, 20);
        return noisy;
    }

    private static double sumDistances(List<Integer> list) {
        double distance = 0;
        for(int i = 1; i < list.size(); i++) {
            distance += matrix[list.get(i - 1)][list.get(i)];
        }
        return distance;
    }


    public static <T> void reverse(List<T> list) {
        for(int i = 0; i < list.size() / 2; i++)
            Collections.swap(list, i, list.size() - i - 1);
    }

}
