import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParticleVisualization extends JPanel {
    private double L;
    private double M;
    private List<Particle> particles;
    private double rc;

    public ParticleVisualization(double L, double M, List<Particle> particles, double rc) {
        this.L = L;
        this.M = M;
        this.rc = rc;
        this.particles = particles;

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) L, (int) L);
        frame.setLocationRelativeTo(null);
        frame.add(this);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawParticles(g);
        drawRC(g);
    }

    private void drawGrid(Graphics g) {
        int cellSize = (int) (L / M);
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                int x = i * cellSize;
                int y = j * cellSize;
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    private void drawParticles(Graphics g) {
        g.setColor(Color.PINK);
        for (Particle particle : particles) {
            int x = (int) (particle.x - particle.radius);
            int y = (int) (particle.y - particle.radius);
            g.fillOval(x, y, (int) particle.radius * 2, (int) particle.radius * 2);
        }

        g.setColor(Color.BLACK);
        for (Particle particle : particles) {
            int idX = (int) (particle.x + particle.radius);
            int idY = (int) (particle.y - particle.radius);
            g.drawString(String.valueOf(particle.id), idX, idY);

        }
    }

    private void drawRC(Graphics g) {
        g.setColor(Color.RED);
        for (Particle particle : particles) {
            int radius = (int) (particle.radius + rc);
            int x = (int) (particle.x - radius);
            int y = (int) (particle.y - radius);
            g.drawOval(x, y, radius * 2, radius * 2);
        }
    }
}