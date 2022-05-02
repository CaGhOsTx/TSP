import javax.swing.*;
import java.awt.*;
import java.util.List;

public class View extends JFrame {
    static JTextField sequence = new JTextField();
    static JTextField distance = new JTextField();
    View() {
        setVisible(true);
        setSize(1920,1080);
        setLayout(new GridLayout());
        JPanel panel = new JPanel();
        panel.add(new JLabel("Travelling salesman problem"));
        panel.add(sequence);
        panel.add(distance);
        Main.coordinateSystem.setOffset((int) (getWidth() * 0.1), (int) (getHeight() * 0.1));
        Main.coordinateSystem.normalize();
        add(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        sequence.setText("Sequence: " + Main.minimum.sequence);
        distance.setText(String.format("min distance: %.3fkm",Main.minimum.distance));
        Graphics2D g2 = (Graphics2D) g.create();
        setGraphicsRenderHints(g2);
        List<Integer> sequence = Main.minimum.sequence;
        if(sequence != null) {
            var points = Main.coordinateSystem.get();
            drawMaynooth(g2, sequence, points);
            for (int i = 1; i < sequence.size() - 1; i++)
                drawRest(g2, sequence, points, i);
        }
    }

    private void setGraphicsRenderHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private void drawRest(Graphics2D g2, List<Integer> sequence, List<CoordinateSystem.Point> points, int i) {
        g2.setColor(Color.GREEN);
        g2.drawLine(
                (int) (points.get(sequence.get(i)).x + 2),
                (int) (points.get(sequence.get(i)).y + 2),
                (int) (points.get(sequence.get(i + 1)).x + 2),
                (int) (points.get(sequence.get(i + 1)).y + 2)
        );
        g2.setColor(Color.BLACK);
        g2.fillOval((int) points.get(sequence.get(i)).x, (int) points.get(sequence.get(i)).y, 5, 5);
    }

    private void drawMaynooth(Graphics2D g2, List<Integer> sequence, List<CoordinateSystem.Point> points) {
        g2.setColor(Color.RED);
        g2.fillOval((int) points.get(0).x, (int) points.get(0).y, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("Maynooth", (int) points.get(0).x, (int) points.get(0).y - 5);
        g2.setColor(Color.GREEN);
        g2.drawLine(
                (int) (points.get(0).x + 5),
                (int) (points.get(0).y + 5),
                (int) (points.get(sequence.get(1)).x + 2),
                (int) (points.get(sequence.get(1)).y + 2)
        );
    }
}
