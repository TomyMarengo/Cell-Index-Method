import java.text.DecimalFormat;
import java.util.Objects;

public class Particle implements Comparable<Particle> {
    int id;
    double x;
    double y;
    double radius;

    public Particle(int id, double x, double y, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        return "{" + id + "} " + "x: " + decimalFormat.format(x) +
                ", y: " + decimalFormat.format(y) + " radius: " + decimalFormat.format(radius);
    }

    @Override
    public int compareTo(Particle otherParticle) {
        return Integer.compare(this.id, otherParticle.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Particle particle = (Particle) obj;
        return id == particle.id;
    }
}
