import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFiles {

    private static final int N = 200; // Total number of particles
    private static final double L = 20.0; // Width and height of the grid
    private static final double rc = 1; // Distance to be considered neighbors
    private static final double MAX_RADIUS = 0.25; // Maximum particle radius
    private static final  int numTimestamps = 5; // Timestamps quantity


    public static void writeStaticFile(int N, double L, double rc, double[] radii) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("static.txt"));
        writer.write(N + "\n");
        writer.write(L + "\n");
        writer.write(rc + "\n");
        for (double radius : radii) {
            writer.write(radius + "\n");
        }
        writer.close();
    }

    public static void writeDynamicFile(int numTimesteps, double[][] positions) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("dynamic.txt"));
        for (int t = 0; t < numTimesteps; t++) {
            double time = t;
            writer.write(time + "\n");
            for (double[] position : positions) {
                writer.write(position[0] + " " + position[1] + "\n");
            }
        }
        writer.close();
    }


    // Change numbers in this method to have other inputs
    public static void main(String[] args) {
        double[] radii = new double[N];

        // Radio initialization
        for (int i = 0; i < N; i++) {
            radii[i] = MAX_RADIUS; //Math.random() * MAX_RADIUS;
        }

        try {
            writeStaticFile(N, L, rc, radii);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] positions = new double[N][2];

        // Position initialization
        for (int t = 0; t < numTimestamps; t++) {
            for (int i = 0; i < N; i++) {
                positions[i][0] = Math.random() * L; // X position
                positions[i][1] = Math.random() * L; // Y position
            }
            try {
                writeDynamicFile(numTimestamps, positions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}