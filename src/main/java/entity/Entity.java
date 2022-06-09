package entity;

import cell_index_method.Cell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity {
    private static Long currentId = 0L;
    protected Long id;
    protected double x;
    protected double y;
    protected EntityType type;
    protected Cell cell;

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
        this.id = currentId++;
    }

    public enum EntityType {
        PARTICLE, OBSTACLE, WALL
    }

    public static double distance(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE) || other.getType().equals(EntityType.OBSTACLE)) {
                double xDistance = entity.getX() - other.getX();
                double yDistance = entity.getY() - other.getY();
                return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
            } else if(other.getType().equals(EntityType.WALL)){
                return Math.abs(entity.getX() - other.getX());
            }
        }
        System.out.println("BUG");
        return 0.0;
    }

    public static double overlap(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)){
                return ((Particle) entity).getRadius() + ((Particle) other).getRadius() - distance(entity, other);
            }else if(other.getType().equals(EntityType.OBSTACLE)){
                return ((Particle) entity).getRadius() + ((Obstacle) other).getRadius() - distance(entity, other);
            }else if(other.getType().equals(EntityType.WALL)){
                //TODO: CHECK
                Particle particle = (Particle) entity;
                return particle.getX() + particle.getRadius() - distance(entity, other);
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
