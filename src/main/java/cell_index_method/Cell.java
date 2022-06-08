package cell_index_method;

import entity.Entity;
import entity.Particle;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Cell {
    private int row;
    private int column;
    private List<Entity> entityList;

    public Cell(int row, int column, List<Entity> entityList) {
        this.row = row;
        this.column = column;
        this.entityList = entityList;
    }

    public void addParticle(Particle particle){
        if(!particle.getCell().equals(this))
            throw new RuntimeException("entity.Particle cell differs from current cell");

        this.entityList.add(particle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row && column == cell.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
