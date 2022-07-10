package entity;

import cell_index_method.Cell;
import lombok.Data;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Wall extends Entity{
    private double length;
    private WallType wallType;
    private List<Pair> vertexList = new ArrayList<>();
    private List<Cell> cells = new ArrayList<>();

    public Wall(double x, double y, double length, WallType wallType) {
        super(x, y);
        this.type = EntityType.WALL;
        this.length = length;
        this.wallType = wallType;
        initVertexList();
    }

    public enum WallType{
        LEFT_AREA_WALL, RIGHT_AREA_WALL, TOP_WALL, BOTTOM_WALL
    }

    private void initVertexList() {
        vertexList.add(new Pair(x, y));

        switch (wallType) {
            case LEFT_AREA_WALL:
            case RIGHT_AREA_WALL:
                vertexList.add(new Pair(x, y - length));
                break;
            case BOTTOM_WALL: // FIXME: check bottom wall case
                vertexList.add(new Pair(x + length, y));
                break;
            default:    // shouldn't happen
                throw new RuntimeException("initVertexList exception: wrong wall type");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Wall wall = (Wall) o;
        return Double.compare(wall.length, length) == 0 && wallType == wall.wallType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), length, wallType);
    }

    @Override
    public String toString() {
        return "Wall{" +
                "length=" + length +
                ", wallType=" + wallType +
                ", vertexList=" + vertexList +
                '}';
    }
}
