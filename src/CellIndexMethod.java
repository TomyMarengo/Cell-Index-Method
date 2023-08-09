import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Set;

public class CellIndexMethod {
    double L; // Grid is L in height and width
    int N; // Number of particles
    int M; // Grid is MxM
    double rc; // Distance from the circumference particle to be considered a neighbor
    double cellSize; // Grid is M*cellSize = L in height and width, cellSize is divisor of L
    boolean periodicOutline;
    Set<Particle> particles;
    List<Cell> grid;
    Map<Particle, Set<Particle>> neighborMap;
    Map<Particle, Particle> mirrorMap;

    private double[] calculateCellSize(double L, double rc, Set<Particle> particles) {
        double minRadius = particles.stream().findFirst().get().radius;
        for (Particle particle : particles) {
            if (particle.radius < minRadius) {
                minRadius = particle.radius;
            }
        }
        
        int i = 2;
        double epsilon = 1e-6;
        while(true) {
            M = (int) (L/i);
            if (L % i < epsilon && M < L/(rc+minRadius*2)) {
                break;
            }
            else {
                i++;
            }
        }

        return new double[]{M, i};
    }

    public CellIndexMethod(double L, double rc, Set<Particle> particles, boolean periodicOutline) {
        this.L = L;
        this.N = particles.size();
        this.rc = rc;
        this.particles = new TreeSet<>(particles);
        this.periodicOutline = periodicOutline;

        //Calculate best M option
        double[] data = calculateCellSize(L, rc, particles);
        this.M = (int) data[0];
        this.cellSize = data[1];

        // Initialize grid and cells
        grid = new ArrayList<>();
        for (int i = 0; i < M * M; i++) {
            grid.add(new Cell());
        }

        // Places particles in each cell
        for (Particle particle : particles) {
            int cellX = (int) Math.floor(particle.x / cellSize);
            int cellY = (int) Math.floor(particle.y / cellSize);
            int cellIndex = cellY * M + cellX;
            grid.get(cellIndex).addParticle(particle);
        }

        // Mirror cells if periodicOutline is true
        if (periodicOutline) {
            mirrorMap = new HashMap<>();

            //Top
            for (int i = 0; i < M; i++) {
                grid.add(i, new Cell());
                for (Particle particle : grid.get(M*(M-1)+(i+1)*2-1).particles) {
                    double newX = particle.x;
                    double newY = 0 - (L - particle.y);
                    Particle newParticle = new Particle(particle.id+"E", newX, newY, particle.radius);
                    grid.get(i).particles.add(newParticle);
                    this.particles.add(newParticle);
                    mirrorMap.put(newParticle, particle);
                }
            }

            //Top Right
            grid.add(M, new Cell());
            for (Particle particle : grid.get(M*(M-1)+M+1).particles) {
                double newX = L + particle.x;
                double newY = 0 - (L - particle.y);
                Particle newParticle = new Particle(particle.id+"E2", newX, newY, particle.radius);
                grid.get(M).particles.add(newParticle);
                this.particles.add(newParticle);
                mirrorMap.put(newParticle, particle);
            }

            //Right
            for (int i = M+1; i < (M+1) * (M+1); i+=M+1) {
                grid.add(i+M, new Cell());
                for (Particle particle : grid.get(i).particles) {
                    double newX = L + particle.x;
                    double newY = particle.y;
                    Particle newParticle = new Particle(particle.id+"E", newX, newY, particle.radius);
                    grid.get(i+M).particles.add(newParticle);
                    this.particles.add(newParticle);
                    mirrorMap.put(newParticle, particle);
                }
            }

            //Bottom
            for (int i = 0; i < M; i++) {
                grid.add(new Cell());
            }

            //Bottom Right
            grid.add(new Cell());
            for (Particle particle : grid.get(M+1).particles) {
                double newX = L + particle.x;
                double newY = L + particle.y;
                Particle newParticle = new Particle(particle.id+"E2", newX, newY, particle.radius);
                grid.get(grid.size()-1).particles.add(newParticle);
                this.particles.add(newParticle);
                mirrorMap.put(newParticle, particle);
            }
        }

        // Visualization class, when instantiated, draw the grid (not more needed to be done)
        ParticleVisualization visualization = new ParticleVisualization(grid, L, M, rc, periodicOutline);
    }

    public void calculateNeighborParticles() {
        neighborMap = new HashMap<>();

        for (int i = 0; i < grid.size(); i++) {

            // Add particles within same cell as neighbors
            for (Particle particle : grid.get(i).particles) {
                if(!particle.id.contains("E")) {
                    for (Particle particle2 : grid.get(i).particles) {
                        if (!particle.id.equals(particle2.id)) {
                            neighborMap.computeIfAbsent(particle, k -> new TreeSet<>()).add(particle2);
                        }
                    }
                }
            }

            // Neighbors cells to be calculated: top, top right, right, bottom right
            int[] offsets;
            if(periodicOutline) {
                offsets = new int[]{i - M - 1, i - M + 1 - 1, i + 1, i + M + 1 + 1};
            }
            else {
                offsets = new int[]{i - M, i - M + 1, i + 1, i + M + 1};
            }

            // Calculate distance between neighbor particles in neighbor cells
            for (Particle particle : grid.get(i).particles) {
                for (int offset : offsets) {
                    if (offset >= 0 && offset < grid.size()) {
                        for (Particle neighborParticle : grid.get(offset).particles) {
                            double dx = particle.x - neighborParticle.x;
                            double dy = particle.y - neighborParticle.y;
                            double distance = Math.sqrt(dx * dx + dy * dy);
                            double combinedRadius = particle.radius + neighborParticle.radius;
                            // Distance is calculated and then subtracted the combinedRadius
                            if (distance - combinedRadius <= rc) {
                                if(!particle.id.contains("E")) {
                                    neighborMap.computeIfAbsent(particle, k -> new TreeSet<>()).add(neighborParticle);
                                }
                                else if(!neighborParticle.id.contains("E")){
                                    neighborMap.computeIfAbsent(mirrorMap.get(particle), k -> new TreeSet<>()).add(neighborParticle);
                                }
                                if(!neighborParticle.id.contains("E")) {
                                    neighborMap.computeIfAbsent(neighborParticle, k -> new TreeSet<>()).add(particle);
                                }
                                else {
                                    neighborMap.computeIfAbsent(mirrorMap.get(neighborParticle), k -> new TreeSet<>()).add(particle);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    public void calculateNeighborParticlesNaive() {
        neighborMap = new HashMap<>();

        // Calculate distance between neighbor particles in neighbor cells
        for (Particle particle : particles) {
            if(!particle.id.contains("E")) {
                for (Particle neighborParticle : particles) {
                    if (particle.id != neighborParticle.id) {
                        double dx = particle.x - neighborParticle.x;
                        double dy = particle.y - neighborParticle.y;
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        double combinedRadius = particle.radius + neighborParticle.radius;

                        // Distance is calculated and then subtracted the combinedRadius
                        if (distance - combinedRadius <= rc) {
                            neighborMap.computeIfAbsent(particle, k -> new TreeSet<>()).add(neighborParticle);
                        }
                    }
                }
            }
        }
    }

    public void writeOutputToFile(long elapsedTime, String title) {
        try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write( title + " Method" + "\n");
            // Escribir M y N en el archivo
            writer.write("M: " + M + "\n");
            writer.write("N: " + N + "\n");
            // Escribir el tiempo de ejecución en el archivo
            writer.write("Tiempo de ejecución: " + elapsedTime + " ms\n");

            // Escribir los vecinos de cada partícula en el archivo
            for (Map.Entry<Particle, Set<Particle>> entry : neighborMap.entrySet()) {
                Particle particle = entry.getKey();
                Set<Particle> neighbors = entry.getValue();

                // Escribir el ID de la partícula y sus vecinos en el formato deseado
                writer.write(particle.id + ": ");
                for (Particle neighbor : neighbors) {
                    writer.write(neighbor.id + " ");
                }
                writer.write("\n");
            }
            System.out.println("Resultados del método " + title + " escritos en output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendOutputToFile(long elapsedTime, String title) {
        try (FileWriter fileWriter = new FileWriter("output.txt", true);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            writer.write("\n" + title + " Method" + "\n");
            // Escribir M y N en el archivo
            writer.write("M: " + M + "\n");
            writer.write("N: " + N + "\n");
            // Escribir el tiempo de ejecución en el archivo
            writer.write("Tiempo de ejecución: " + elapsedTime + " ms\n");

            // Escribir los vecinos de cada partícula en el archivo
            for (Map.Entry<Particle, Set<Particle>> entry : neighborMap.entrySet()) {
                Particle particle = entry.getKey();
                Set<Particle> neighbors = entry.getValue();

                // Escribir el ID de la partícula y sus vecinos en el formato deseado
                writer.write(particle.id + ": ");
                for (Particle neighbor : neighbors) {
                    writer.write(neighbor.id + " ");
                }
                writer.write("\n");
            }

            writer.newLine(); // Agregar una línea en blanco entre los resultados
            System.out.println("Resultados del método " + title + " agregados en output.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int N = 0;
        double L = 0;
        double rc = 0;
        List<Double> radii = new ArrayList<>();
        List<Set<Particle>> particlesByTimestamp = new ArrayList<>();

        try {
            // Read static file
            BufferedReader staticReader = new BufferedReader(new FileReader("static.txt"));
            N = Integer.parseInt(staticReader.readLine());
            L = Double.parseDouble(staticReader.readLine());
            rc = Double.parseDouble(staticReader.readLine());

            for (int i = 0; i < N; i++) {
                double radius = Double.parseDouble(staticReader.readLine());
                radii.add(radius);
            }
            staticReader.close();

            // Read dynamic file
            BufferedReader dynamicReader = new BufferedReader(new FileReader("dynamic.txt"));
            String line;
            int timestampCount = 0;

            while ((line = dynamicReader.readLine()) != null) {
                double time = Double.parseDouble(line);
                Set<Particle> particles = new TreeSet<>();

                for (int i = 0; i < N; i++) {
                    line = dynamicReader.readLine();
                    String[] position = line.split(" ");
                    double x = Double.parseDouble(position[0]);
                    double y = Double.parseDouble(position[1]);
                    particles.add(new Particle(Integer.toString(i), x, y, radii.get(i)));
                }

                particlesByTimestamp.add(particles);
                timestampCount++;
            }
            dynamicReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Call CellIndexMethod with first Timestamp
        Set<Particle> particlesAtTimestamp0 = particlesByTimestamp.get(0);
        CellIndexMethod cim = new CellIndexMethod(L, rc, particlesAtTimestamp0, true);

        long startTime = System.currentTimeMillis();
        cim.calculateNeighborParticles();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // Write output.txt
        cim.writeOutputToFile(elapsedTime, "Normal");

        // Call CellIndexMethodNaive with first Timestamp
        startTime = System.currentTimeMillis();
        cim.calculateNeighborParticlesNaive();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;

        // Write output.txt
        cim.appendOutputToFile(elapsedTime, "Naive");
    }
}
