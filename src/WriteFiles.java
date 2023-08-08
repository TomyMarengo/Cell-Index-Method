import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFiles {
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
            double time = t; // Ejemplo: intervalo de tiempo de 0.1
            writer.write(time + "\n");
            for (double[] position : positions) {
                writer.write(position[0] + " " + position[1] + "\n");
            }
        }
        writer.close();
    }


    // Change numbers in this method to have other inputs
    public static void main(String[] args) {
        int N = 30; // Número total de partículas
        double L = 1000.0; // Longitud del lado del área de simulación
        double rc = 20;
        double[] radii = new double[N]; // Array para los radios de las partículas

        // Inicialización de radios para cada partícula (ejemplo: 30 partículas con radios aleatorios)
        for (int i = 0; i < N; i++) {
            radii[i] = Math.random() * 30; // Radios aleatorios entre 0.0 y 30
        }

        try {
            writeStaticFile(N, L, rc, radii);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numTimesteps = 5; // Número de intervalos de tiempo (ejemplo: 5 tiempos)
        double[][] positions = new double[N][2]; // Array para las posiciones X y Y de las partículas

        // Inicialización de posiciones para cada partícula (ejemplo: 5 tiempos con posiciones aleatorias)
        for (int t = 0; t < numTimesteps; t++) {
            for (int i = 0; i < N; i++) {
                positions[i][0] = Math.random() * L; // Posiciones X aleatorias dentro del área de simulación
                positions[i][1] = Math.random() * L; // Posiciones Y aleatorias dentro del área de simulación
            }
            try {
                writeDynamicFile(numTimesteps, positions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}