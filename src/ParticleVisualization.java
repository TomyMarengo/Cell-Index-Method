import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParticleVisualization extends JPanel {
    private double L;
    private double M;
    private List<Cell> grid;
    private Map<Particle, Set<Particle>> neighborMap;
    private double rc;
    private boolean periodicOutline;
    private JFrame frame;
    private int width, height;
    private Particle selectedParticle;


    public ParticleVisualization(List<Cell> grid, Map<Particle, Set<Particle>> neighborMap, double L, double M, double rc, boolean periodicOutline) {
        this.L = L;
        this.M = M;
        this.rc = rc;
        this.grid = grid;
        this.periodicOutline = periodicOutline;
        this.neighborMap = neighborMap;

        this.frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Enlarge grid if it has mirrored cells
        if(periodicOutline) {
            this.width = (int)(L+L/M);
            this.height = (int)(L+2*(L/M));
        }
        else {
            this.width = (int) L;
            this.height = (int) L;
        }

        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.add(this);
        frame.setVisible(true);

        // Agregar un MouseAdapter para detectar clics del mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    private void handleMouseClick(int mouseX, int mouseY) {
        double minDistance = Double.MAX_VALUE;
        selectedParticle = null;
        int offsetY = 0;
        if (periodicOutline) {
            offsetY += (int) (L / M);
        }

        for (Cell cell : grid) {
            for (Particle particle : cell.particles) {
                int x = (int) particle.x;
                int y = (int) particle.y + offsetY;
                int radius = (int) particle.radius;

                double distanceSquared = Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2);

                // Verificar si el clic del mouse está dentro de la partícula
                if (distanceSquared <= radius * radius && radius < minDistance) {
                    selectedParticle = particle;
                    minDistance = radius;
                }
            }
        }
        if (selectedParticle != null) {
            repaint();
        }
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

        for (int i = 0; i < width; i+=cellSize) {
            for (int j = 0; j < height; j+=cellSize) {
                g.drawRect(i, j, cellSize, cellSize);
            }
        }

        // Draws mirrored cells in other color
        if(periodicOutline) {
            g.setColor(Color.BLACK);
            for (int i = 0; i < width; i+=cellSize) {
                g.drawRect(i, 0, cellSize, cellSize);
            }
            for (int i = 0; i < height; i+=cellSize) {
                g.drawRect(width - cellSize, i, cellSize, cellSize);
            }
            for (int i = 0; i < width; i+=cellSize) {
                g.drawRect(i, height - cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawParticles(Graphics g) {
        g.setColor(Color.PINK);

        // Adds a positive offset to Y coordinate to particles because we need to draw one row in top
        // So the particles are now one cellSize below
        int offsetY = 0;
        if (periodicOutline) {
            offsetY += (int) (L / M);
        }

        // Draw circle particles
        for (Cell cell : grid) {
            for (Particle particle : cell.particles) {
                int x = (int) (particle.x - particle.radius);
                int y = (int) (particle.y - particle.radius);
                g.fillOval(x, y + offsetY, (int) particle.radius * 2, (int) particle.radius * 2);
            }
        }

        if (selectedParticle != null && !selectedParticle.id.contains("E")) {
            g.setColor(Color.CYAN);
            for (Particle particle : neighborMap.get(selectedParticle)) {
                if (!particle.id.contains("E")) {
                    int x = (int) (particle.x - particle.radius);
                    int y = (int) (particle.y - particle.radius);
                    g.fillOval(x, y + offsetY, (int) particle.radius * 2, (int) particle.radius * 2);
                }
            }

            g.setColor(Color.BLUE);
            // Cambiar el color de la partícula seleccionada y sus vecinas
            int x = (int) (selectedParticle.x - selectedParticle.radius);
            int y = (int) (selectedParticle.y - selectedParticle.radius);
            g.fillOval(x, y + offsetY, (int) selectedParticle.radius * 2, (int) selectedParticle.radius * 2);
        }

        // Draw their ids
        g.setColor(Color.BLACK);
        for (Cell cell : grid) {
            for (Particle particle : cell.particles) {
                int idX = (int) (particle.x + particle.radius);
                int idY = (int) (particle.y - particle.radius);
                g.drawString(String.valueOf(particle.id), idX, idY + offsetY);
            }
        }
    }

    private void drawRC(Graphics g) {
        g.setColor(Color.RED);

        int offsetY = 0;
        if (periodicOutline) {
            offsetY += (int) (L / M);
        }

        for (Cell cell : grid) {
            for (Particle particle : cell.particles) {
                int radius = (int) (particle.radius + rc);
                int x = (int) (particle.x - radius);
                int y = (int) (particle.y - radius);
                g.drawOval(x, y + offsetY, radius * 2, radius * 2);
            }
        }
    }
}