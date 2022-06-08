package entity;

import lombok.Data;

@Data
public class Obstacle extends Entity{
    private double radius;

    public Obstacle(double x, double y, double radius) {
        super(x, y);
        this.radius = radius;
    }
}
