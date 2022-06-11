package cell_index_method;

import entity.Entity;
import entity.Obstacle;
import entity.Particle;
import entity.Wall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class NeighbourWrapper {
    private List<Particle> particles;
    private List<Wall> walls;
    private List<Obstacle> obstacles;

    public NeighbourWrapper() {
        this.particles = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
    }

    public void add(Entity entity){
        if(entity.getType().equals(Entity.EntityType.PARTICLE)){
            particles.add((Particle) entity);
        }else if(entity.getType().equals(Entity.EntityType.WALL)){
            walls.add((Wall) entity);
        }else {
            obstacles.add((Obstacle) obstacles);
        }
    }

    public static NeighbourWrapper fromEntities(List<Entity> entities){
        List<Particle> particles = entities.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.PARTICLE)) {
                return (Particle) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Wall> walls = entities.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.WALL)) {
                return (Wall) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Obstacle> obstacles = entities.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.OBSTACLE)) {
                return (Obstacle) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return new NeighbourWrapper(particles, walls, obstacles);
    }

    public void addAll(List<Entity> neighbours) {
        List<Particle> currentParticles = neighbours.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.PARTICLE)) {
                return (Particle) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Wall> currentWalls = neighbours.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.WALL)) {
                return (Wall) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Obstacle> currentObstacles = neighbours.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.OBSTACLE)) {
                return (Obstacle) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        this.particles.addAll(currentParticles);
        this.walls.addAll(currentWalls);
        this.obstacles.addAll(currentObstacles);
    }
}
