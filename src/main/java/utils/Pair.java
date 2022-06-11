package utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair {
    private double x;
    private double y;

    public Pair add(Pair other){
        this.x = x + other.getX();
        this.y = y + other.getY();
        return this;
    }
}
