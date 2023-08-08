import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CellIndexMethod {
    double L; // Cantidad de celdas vertical y horizontalmente
    int M;
    double cellSize;
    int numParticles;
    List<Particle> particles;
    List<Cell> grid;

    private double[] calculateCellSize(double L, double rc, double maxRadius) {
        int i = 2;
        double epsilon = 1e-6;
        while(true) {
            M = (int) (L/i);
            if (L % i < epsilon && M < L/(rc+maxRadius)) {
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

        double[] data = calculateCellSize(L, rc, maxRadius);
        this.M = (int) data[0];
        this.cellSize = data[1];
        this.particles = particles;
        this.numParticles = particles.size();

        System.out.println("M: " + M);
        System.out.println("cellSize: " + cellSize);

        // Inicializar la cuadrícula
        grid = new ArrayList<>();
        for (int i = 0; i < M * M; i++) {
            grid.add(new Cell());
        }

        // Colocar partículas en la cuadrícula
        for (Particle particle : particles) {
            int cellX = (int) Math.floor(particle.x / cellSize);
            int cellY = (int) Math.floor(particle.y / cellSize);
            int cellIndex = cellY * M + cellX;
            grid.get(cellIndex).addParticle(particle);
        }
    }

    public Map<Particle, List<Particle>> getNeighborParticles(double rc) {
        Map<Particle, List<Particle>> neighborsMap = new HashMap<>();

        for (int i = 0; i < grid.size(); i++) {
            for (Particle particle : grid.get(i).particles) {
                for (Particle particle2 : grid.get(i).particles) {
                    if (particle.id != particle2.id) {
                        neighborsMap.computeIfAbsent(particle, k -> new ArrayList<>()).add(particle2);
                    }
                }
            }
            int[] offsets = {i - M, i - M + 1, i + 1, i + M + 1 };
            for (Particle particle : grid.get(i).particles) {
                for (int offset : offsets) {
                    if (offset >= 0 && offset < grid.size()) {
                        for (Particle neighborParticle : grid.get(offset).particles) {
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
