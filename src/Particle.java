import java.text.DecimalFormat;
public class Particle {
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
}
