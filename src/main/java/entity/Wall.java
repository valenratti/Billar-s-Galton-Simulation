package entity;

import lombok.Data;

@Data
public class Wall extends Entity{
    private double length;
    private WallType wallType;

    public Wall(double x, double y, double length, WallType wallType) {
        super(x, y);
        this.type = EntityType.WALL;
        this.length = length;
        this.wallType = wallType;
    }

    public enum WallType{
        LEFT_AREA_WALL, RIGHT_AREA_WALL, TOP_WALL, BOTTOM_WALL
    }
}
