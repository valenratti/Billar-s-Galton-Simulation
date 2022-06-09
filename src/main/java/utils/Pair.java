package utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair {
    private double x;
    private double y;

    public Pair add(Pair other){
        return new Pair(this.x + other.getX(), this.y + other.getY());
    }
}
