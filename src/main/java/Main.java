import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static boolean showLastResult = true;

    static CoordinateSystem coordinateSystem;
    static List<Location> coordinates;

    private static double fromLong(double longitude) {
        return (180 + Math.toDegrees(longitude)) / 360;
    }

    private static double fromLat(double latitude) {
        return (90 - Math.toDegrees(latitude)) / 180;
    }

    static double[][] matrix;
    static Solution minimum = new Solution(null, Double.MAX_VALUE);

    public static void main(String[] args) throws IOException {
        try(var coords = Files.lines(Path.of("coords.txt"))) {
            coordinates = coords.map(s -> s.split(","))
                    .map(arr -> new Location(Integer.parseInt(arr[0]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2])))
                    .toList();
            coordinateSystem = createCoordinateSystem();
            matrix = createDistanceMatrix(coordinates);
        }
        SwingUtilities.invokeLater(() -> {
            View gui = new View();
            if(!showLastResult) {
                for (int i = 0; i < 4; i++)
                    new Thread(() -> minPathAlgorithm(gui)).start();
            }
            else {
                minimum = new Solution(Arrays.stream("0, 55, 53, 58, 59, 120, 60, 61, 65, 64, 66, 72, 112, 71, 77, 79, 76, 111, 78, 75, 73, 69, 92, 84, 81, 80, 110, 85, 83, 109, 86, 88, 90, 94, 100, 103, 102, 105, 96, 101, 107, 104, 99, 98, 95, 108, 89, 87, 82, 74, 67, 63, 115, 56, 52, 54, 50, 47, 45, 43, 42, 41, 38, 36, 30, 28, 21, 19, 117, 18, 118, 20, 17, 15, 16, 23, 27, 29, 31, 33, 35, 37, 40, 106, 114, 68, 113, 70, 34, 25, 26, 116, 24, 10, 119, 5, 6, 1, 3, 2, 4, 14, 11, 8, 7, 9, 12, 13, 22, 32, 48, 39, 44, 46, 51, 49, 57, 93, 97, 91, 62, 0".split(", ")).map(Integer::parseInt).toList(), 1784.493783807264);
                gui.repaint();
            }
        });
    }

    private static CoordinateSystem createCoordinateSystem() {
        var tmp = new CoordinateSystem(900,900);
        coordinates.forEach(location -> tmp.add(new CoordinateSystem.Point(fromLat(location.latitude), fromLong(location.longitude))));
        tmp.normalize();
        return tmp;
    }

    private static void minPathAlgorithm(View gui) {
        while (true) {
            var solution = tsp();
            if (solution.distance < minimum.distance) {
                minimum = solution;
                System.out.println(minimum);
                try (var writer = Files.newBufferedWriter(Path.of("Solution.txt"), StandardOpenOption.APPEND)) {
                    writer.write(minimum.toString());
                    writer.newLine();
                    writer.flush();
                    gui.repaint();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static double[][] createDistanceMatrix(List<Location> list) {
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
