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

    public static double distanceFromRadius(Particle particle, Entity other) {
        if(other.getType().equals(EntityType.WALL)){
            return distance(particle, other) - particle.getRadius();
        }else {
            double xDistance = particle.getX() - other.getX();
            double yDistance = particle.getY() - other.getY();
            final double hypot = Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
            if(other.getType().equals(EntityType.PARTICLE)) {
                return hypot - ((Particle)particle).getRadius() - ((Particle)other).getRadius();
            }else{
                return hypot - ((Particle)particle).getRadius() - ((Obstacle)other).getRadius();
            }
        }
    }

    public enum EntityType {
        PARTICLE, OBSTACLE, WALL
    }

    public static double distance(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)) {
                double xDistance = entity.getX() - other.getX();
                double yDistance = entity.getY() - other.getY();
                return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
            }else if(other.getType().equals(EntityType.OBSTACLE)){
                double xDistance = entity.getX() - other.getX();
                double yDistance = entity.getY() - other.getY();
                return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
            } if(other.getType().equals(EntityType.WALL)){
                Wall wall = (Wall) other;
                if(wall.getWallType().equals(Wall.WallType.BIN_WALL)) {
                    if (entity.getY() <= wall.getY()) {
                        return Math.abs(entity.getX() - wall.getX());
                    } else{
                        double xDistance = entity.getX() - other.getX();
                        double yDistance = entity.getY() - other.getY();
                        return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
                    }
                } else if(wall.getWallType().equals(Wall.WallType.TOP_WALL)){
                    return Math.abs(entity.getY() - wall.getY());
                }else if(wall.getWallType().equals(Wall.WallType.RIGHT_AREA_WALL)){
                    return Math.abs(entity.getX() - wall.getX());
                }else if (wall.getWallType().equals(Wall.WallType.LEFT_AREA_WALL)){
                    return Math.abs(entity.getX() - wall.getX());
                }else {
                    return Math.abs(entity.getY() - wall.getY());
                }
            }
        }
        System.out.println("BUG");
        return 0.0;
    }

    public static double overlap(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)){
                double overlap = ((Particle) entity).getRadius() + ((Particle) other).getRadius() - distance(entity, other);
                return overlap<0 ? 0 : overlap;
            }else if(other.getType().equals(EntityType.OBSTACLE)){
                double overlap = ((Particle) entity).getRadius() + ((Obstacle) other).getRadius() - distance(entity, other);
                return overlap<0 ? 0 : overlap;
            }else if(other.getType().equals(EntityType.WALL)){
                Wall wall = (Wall) other;
                if(wall.getWallType().equals(Wall.WallType.BIN_WALL)) {
                    if (entity.getY() <= wall.getY()) {
                        return Math.abs(entity.getX() - wall.getX());
                    } else{
                        double xDistance = entity.getX() - other.getX();
                        double yDistance = entity.getY() - other.getY();
                        return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
                    }
                } else if(wall.getWallType().equals(Wall.WallType.TOP_WALL)){
                    return entity.getY() + ((Particle)entity).getRadius() - 70.0;
                }else if(wall.getWallType().equals(Wall.WallType.RIGHT_AREA_WALL)){
                    return entity.getX() + ((Particle)entity).getRadius() - 60.0;
                }else if (wall.getWallType().equals(Wall.WallType.LEFT_AREA_WALL)){
                    return 60 - entity.getX() - ((Particle)entity).getRadius();
                }else {
                    return 10 - ((Particle)entity).getY() - ((Particle)entity).getRadius();
                }
            }
        }
        return 0.0;
    }

    public static double overlapD1(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)){
                double relativeVelocityX = ((Particle)other).getVx() - ((Particle)entity).getVx();
                double relativeVelocityY = ((Particle)other).getVy() - ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = (other.getX() -entity.getX()) / distance;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;
                return relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
            }else if(other.getType().equals(EntityType.OBSTACLE)){
                double relativeVelocityX =   ((Particle)entity).getVx();
                double relativeVelocityY =  ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = (other.getX() -entity.getX()) / distance;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;
                return -1 * relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
            }else if(other.getType().equals(EntityType.WALL)){
                double relativeVelocityX =   ((Particle)entity).getVx();
                double relativeVelocityY =  ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = 0;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;
                return -1 * relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
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
