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

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public enum EntityType {
        PARTICLE, OBSTACLE, WALL
    }

    public static double distance(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)) {
                //TODO DISTANCE BETWEEN PARTICLES
            }else if(other.getType().equals(EntityType.OBSTACLE)){
                //TODO DISTANCE BETWEEN PARTICLE AND OBSTACLE
            }else if(other.getType().equals(EntityType.WALL)){
                //TODO DISTANCE BETWEEN PARTICLE AND WALL
            }
        }
        return 0.0;
    }

    public boolean isFixed(){
        if(getType().equals(EntityType.PARTICLE)){
            return ((Particle) this).isFixed();
        }else return false;
    }
}
