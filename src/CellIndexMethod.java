import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CellIndexMethod {
    double L; // Cantidad de celdas vertical y horizontalmente
    double M;
    int gridSize;
    int numParticles;
    List<Particle> particles;
    List<Cell> grid;

    private double[] calculateCellSize(double L, double rc, double maxRadius) {
        double epsilon = 1e-6;
        double M = L;
        int i = 2;

        while(true) {
            M = L/i;
            if ((L % i) < epsilon && M < L/(rc+maxRadius)) {
                break;
            }
            else {
                i++;
            }
        }

        return new double[]{M, i};
    }

    public CellIndexMethod(double L, double rc, List<Particle> particles) {
        this.L = L;
        double maxRadius = 0.0;
        for (Particle particle : particles) {
            if (particle.radius > maxRadius) {
                maxRadius = particle.radius;
            }
        }

        double[] cellSize = calculateCellSize(L, rc, maxRadius);
        this.M = cellSize[0];
        this.gridSize = (int)cellSize[1];
        this.particles = particles;
        this.numParticles = particles.size();

        // Inicializar la cuadrícula
        grid = new ArrayList<>();
        for (int i = 0; i < gridSize * gridSize; i++) {
            grid.add(new Cell());
        }

        // Colocar partículas en la cuadrícula
        for (Particle particle : particles) {
            int cellX = (int) (particle.x / M);
            int cellY = (int) (particle.y / M);
            int cellIndex = cellY * gridSize + cellX;
            grid.get(cellIndex).addParticle(particle);
        }
    }

    public Map<Particle, List<Particle>> getNeighborParticles(double rc) {
        Map<Particle, List<Particle>> neighborsMap = new HashMap<>();

        for (Particle particle : particles) {
            int cellX = (int) (particle.x / M);
            int cellY = (int) (particle.y / M);

            int[][] offsets = {
                    { -1, 0 }, // (i-1, j)
                    { -1, 1 }, // (i-1, j+1)
                    { 0, 1 },  // (i, j+1)
                    { 1, 1 }   // (i+1, j+1)
            };

            // Calcular celdas vecinas
            for (int[] offset : offsets) {
                int neighborX = cellX + offset[0];
                int neighborY = cellY + offset[1];

                // Verificar límites de la cuadrícula
                if (neighborX >= 0 && neighborX < gridSize && neighborY >= 0 && neighborY < gridSize) {

                    int neighborCellIndex = neighborY * gridSize + neighborX;
                    Cell neighborCell = grid.get(neighborCellIndex);

                    // Calcular distancias con partículas en la celda vecina
                    for (Particle neighborParticle : neighborCell.particles) {
                        double dx = particle.x - neighborParticle.x;
                        double dy = particle.y - neighborParticle.y;
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        double combinedRadius = Math.max(particle.radius, rc) + neighborParticle.radius;

                        // Verificar si están a una distancia rc o menos
                        if (distance - combinedRadius <= rc) {
                            neighborsMap.computeIfAbsent(particle, k -> new ArrayList<>()).add(neighborParticle);
                            neighborsMap.computeIfAbsent(neighborParticle, k -> new ArrayList<>()).add(particle);
                        }
                    }
                }
            }
        }

        ParticleVisualization visualization = new ParticleVisualization(L, M, particles, rc);

        return neighborsMap;
    }
    public static void main(String[] args) {
        int N = 0;
        double L = 0;
        double rc = 0;
        List<Double> radii = new ArrayList<>();
        List<List<Particle>> particlesByTimestep = new ArrayList<>();

        try {
            BufferedReader staticReader = new BufferedReader(new FileReader("static.txt"));
            N = Integer.parseInt(staticReader.readLine());
            L = Double.parseDouble(staticReader.readLine());
            rc = Double.parseDouble(staticReader.readLine());

            for (int i = 0; i < N; i++) {
                double radius = Double.parseDouble(staticReader.readLine());
                radii.add(radius);
            }
            staticReader.close();

            BufferedReader dynamicReader = new BufferedReader(new FileReader("dynamic.txt"));
            String line;
            int timestepCount = 0;

            while ((line = dynamicReader.readLine()) != null) {
                double time = Double.parseDouble(line);
                List<Particle> particles = new ArrayList<>();

                for (int i = 0; i < N; i++) {
                    line = dynamicReader.readLine();
                    String[] position = line.split(" ");
                    double x = Double.parseDouble(position[0]);
                    double y = Double.parseDouble(position[1]);
                    particles.add(new Particle(i, x, y, radii.get(i)));
                }

                particlesByTimestep.add(particles);
                timestepCount++;
            }
            dynamicReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Aquí ya tienes todos los datos necesarios para trabajar con las partículas
        // Por ejemplo, para acceder a las partículas en el tiempo t=0:
        List<Particle> particlesAtTimestep0 = particlesByTimestep.get(0);

        System.out.println(particlesAtTimestep0);

        CellIndexMethod cim = new CellIndexMethod(L, rc, particlesAtTimestep0);

        Map<Particle, List<Particle>> neighborParticlesMap = cim.getNeighborParticles(rc);

        System.out.println("Partículas vecinas:");
        for (Particle particle : neighborParticlesMap.keySet()) {
            List<Particle> neighbors = neighborParticlesMap.get(particle);
            System.out.println("Partícula " + particle.id + ": ");
            for (Particle neighbor : neighbors) {
                System.out.println("Vecino " + neighbor.id);
            }
        }
    }
}
