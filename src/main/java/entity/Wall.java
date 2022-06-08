package entity;

import lombok.Data;

@Data
public class Wall {
    private double x;
    private double y;
    private double length;

    public enum WallType{
        BIN_WALL, LEFT_AREA_WALL, RIGHT_AREA_WALL, TOP_WALL, BOTTOM_WALL
    }
}
