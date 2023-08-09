import java.text.DecimalFormat;
import java.util.Objects;

public class Particle implements Comparable<Particle> {
    String id;
    double x;
    double y;
    double radius;

    public Particle(String id, double x, double y, double radius) {
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
        int idComparison = this.id.compareTo(otherParticle.id);
        if (idComparison != 0) {
            return idComparison;
        }
        int xComparison = Double.compare(this.x, otherParticle.x);
        if (xComparison != 0) {
            return xComparison;
        }
        return Double.compare(this.y, otherParticle.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Particle particle = (Particle) obj;
        return Objects.equals(id, particle.id) &&
                Objects.equals(x, particle.x) &&
                Objects.equals(y, particle.y);
    }
}
