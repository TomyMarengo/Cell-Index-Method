import java.util.ArrayList;
import java.util.List;

public class Cell {

    List<Particle> particles;

    public Cell() {
        particles = new ArrayList<>();
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }
}

