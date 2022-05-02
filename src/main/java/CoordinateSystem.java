import java.util.ArrayList;
import java.util.List;

public final class CoordinateSystem {
    private final int width;
    private final int height;
    private int xOffset, yOffset;
    private final List<Point> points = new ArrayList<>();
    static class Point {
        double x,y;
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
    public CoordinateSystem(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static CoordinateSystem of(int width, int height, List<Point> points) {
        var cs = new CoordinateSystem(width, height);
        for(var p : points)
            cs.add(p);
        cs.normalize();
        return cs;
    }

    public void add(Point p) {
        points.add(p);
    }

    public void setOffset(int x, int y) {
        xOffset = x;
        yOffset = y;
    }

    public void normalize() {
        double minX = Double.MAX_VALUE, maxX = 0, minY = Double.MAX_VALUE, maxY = 0;
        for(Point p : points) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        double scale = Math.min(width, height) / Math.min(maxX - minX, maxY - minY);
        minX *= scale;
        minY *= scale;
        for(var p : points) {
            p.x *= scale;
            p.y *= scale;
            p.x -= minX - xOffset;
            p.y -= minY - yOffset;
        }
    }

    public List<Point> get() {
        return points;
    }
}
