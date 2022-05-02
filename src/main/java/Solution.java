import java.util.List;

public class Solution {

    List<Integer> sequence;
    double distance;

    public Solution(List<Integer> sequence, double distance) {
        this.sequence = sequence;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return sequence.toString().replaceAll("\\[]", "") + "\n" + distance;
    }
}
