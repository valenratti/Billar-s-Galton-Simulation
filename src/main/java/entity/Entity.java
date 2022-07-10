package entity;

import cell_index_method.Cell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
        if(other.getType().equals(EntityType.WALL))
            return distance(particle, other) - particle.getRadius();

        double xDistance = particle.getX() - other.getX();
        double yDistance = particle.getY() - other.getY();
        final double hypot = Math.hypot(Math.abs(xDistance), Math.abs(yDistance));

        return hypot - ((Particle)particle).getRadius() - ((Particle)other).getRadius();
    }

    public static double distanceFromRadius(SquaredParticle particle, Entity other) {
        //TODO: Implementar
        return 0.0;
    }

    public enum EntityType {
        PARTICLE, SQUARED_PARTICLE, WALL    //TODO: Fix methods for squared particles
    }

    public static double distance(Entity entity, Entity other) {
        //TODO: Checkear que squared particle es igual que particle aca
        if(entity.getType().equals(EntityType.PARTICLE) || entity.getType().equals(EntityType.SQUARED_PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE) || other.getType().equals(EntityType.SQUARED_PARTICLE)) {
                double xDistance = entity.getX() - other.getX();
                double yDistance = entity.getY() - other.getY();
                return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
            }

            if(other.getType().equals(EntityType.WALL)) {
                Wall wall = (Wall) other;

                if(wall.getWallType().equals(Wall.WallType.TOP_WALL))
                    return Math.abs(entity.getY() - wall.getY());

                if(wall.getWallType().equals(Wall.WallType.RIGHT_AREA_WALL))
                    return Math.abs(entity.getX() - wall.getX());

                if (wall.getWallType().equals(Wall.WallType.LEFT_AREA_WALL))
                    return Math.abs(entity.getX() - wall.getX());

                return Math.abs(entity.getY() - wall.getY());
            }
        }
        System.out.println("BUG");
        return 0.0;
    }

    public static double overlap(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)) {
                double overlap = ((Particle) entity).getRadius() + ((Particle) other).getRadius() - distance(entity, other);
                return overlap<0 ? 0 : overlap;
            }

            if(other.getType().equals(EntityType.WALL)){
                Wall wall = (Wall) other;

                if(wall.getWallType().equals(Wall.WallType.TOP_WALL)) {
                    if(entity.getY() > wall.getY()) {
                        System.out.println("overlap top wall " + (((Particle)entity).getRadius() + distance(entity, wall)));
                        return ((Particle)entity).getRadius() + distance(entity, wall);
                    }

                    return ((Particle) entity).getRadius() - distance(entity, wall);
                }

                if(wall.getWallType().equals(Wall.WallType.RIGHT_AREA_WALL)) {
                    System.out.println("overlap right wall " + (entity.getX() + ((Particle)entity).getRadius() - 0.6));
                    return entity.getX() + ((Particle)entity).getRadius() - 0.6;
                }

                if (wall.getWallType().equals(Wall.WallType.LEFT_AREA_WALL)) {
                    if(entity.getX() < wall.getX()) {
                        System.out.println("overlap left wall " + (((Particle)entity).getRadius() + distance(entity,wall)));
                        return ((Particle)entity).getRadius() + distance(entity,wall);
                    }
                    System.out.println("overlap left wall " + (((Particle)entity).getRadius() - distance(entity,wall)) );
                    return ((Particle)entity).getRadius() - distance(entity,wall);
                }

                return Math.abs(0.1 + ((Particle)entity).getY() - ((Particle)entity).getRadius());
            }
        }

        System.out.println("Bug");
        return 0.0;
    }

    public static double overlapD1(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)) {
                double relativeVelocityX = ((Particle)other).getVx() - ((Particle)entity).getVx();
                double relativeVelocityY = ((Particle)other).getVy() - ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = (other.getX() -entity.getX()) / distance;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;
                double projectedOther = ((Particle)other).getVx() * normalizedXDistance + ((Particle)other).getVy() * normalizedYDistance;
                double projectedEntity = ((Particle)entity).getVx() * normalizedXDistance + ((Particle)entity).getVy() * normalizedYDistance;
                double valueBefore = relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
                double valueAfter = projectedOther - projectedEntity;

                return -(projectedOther - projectedEntity);
            }

            if(other.getType().equals(EntityType.WALL)) {
                double relativeVelocityX =   ((Particle)entity).getVx();
                double relativeVelocityY =  ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = 0;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;

                return relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
            }
        }

        System.out.println("Bug");
        return 0.0;
    }

    public boolean isFixed(){
        if(getType().equals(EntityType.PARTICLE)){
            return ((Particle) this).isFixed();
        }else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
