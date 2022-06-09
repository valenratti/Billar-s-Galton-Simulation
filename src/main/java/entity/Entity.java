package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity {
    protected double x;
    protected double y;
    protected EntityType type;


    public enum EntityType {
        PARTICLE, OBSTACLE, WALL
    }

    public static double distance(Entity entity, Entity other){
        //TODO
        return 0.0;
    }
}
