package cell_index_method;

import lombok.Data;

import java.util.Objects;

@Data
public class CellCoordinates {
    private int row;
    private int column;

    public CellCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellCoordinates that = (CellCoordinates) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
