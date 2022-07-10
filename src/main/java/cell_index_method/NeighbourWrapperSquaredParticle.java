package cell_index_method;

import entity.Entity;
import entity.SquaredParticle;
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
public class NeighbourWrapperSquaredParticle {
    private List<SquaredParticle> particles;
    private List<Wall> walls;

    public NeighbourWrapperSquaredParticle() {
        this.particles = new ArrayList<>();
        this.walls = new ArrayList<>();
    }

    public void add(Entity entity){
        if(entity.getType().equals(Entity.EntityType.SQUARED_PARTICLE)) {
            particles.add((SquaredParticle) entity);
            return;
        }

        if(entity.getType().equals(Entity.EntityType.WALL)) {
            walls.add((Wall) entity);
            return;
        }

        System.out.println("Bug");
    }

    public static NeighbourWrapperSquaredParticle fromEntities(List<Entity> entities){
        List<SquaredParticle> particles = entities.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.SQUARED_PARTICLE)) {
                return (SquaredParticle) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Wall> walls = entities.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.WALL)) {
                return (Wall) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return new NeighbourWrapperSquaredParticle(particles, walls);
    }

    public void addAll(List<Entity> neighbours) {
        List<SquaredParticle> currentParticles = neighbours.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.SQUARED_PARTICLE)) {
                return (SquaredParticle) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Wall> currentWalls = neighbours.stream().map((entity) -> {
            if (entity.getType().equals(Entity.EntityType.WALL)) {
                return (Wall) entity;
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        this.particles.addAll(currentParticles);
        this.walls.addAll(currentWalls);
    }
}
