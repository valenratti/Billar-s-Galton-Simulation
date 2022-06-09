package entity;

import lombok.Data;

@Data
public class Obstacle extends Entity{
    private double radius;

    public Obstacle(double x, double y) {
        super(x, y);
        this.type = EntityType.OBSTACLE;
        this.radius = 0.2;
    }

    @Override
    public String toString() {
        return "Obstacle{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
