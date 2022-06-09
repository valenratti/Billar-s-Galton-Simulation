package entity;

import lombok.Data;

@Data
public class Wall extends Entity{
    private double length;

    public Wall(double x, double y, double length) {
        super(x, y);
        this.type = EntityType.WALL;
        this.length = length;
    }

    public enum WallType{
        BIN_WALL, LEFT_AREA_WALL, RIGHT_AREA_WALL, TOP_WALL, BOTTOM_WALL
    }
}
