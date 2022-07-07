package cell_index_method;

import entity.Entity;
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

    public NeighbourWrapper() {
        this.particles = new ArrayList<>();
        this.walls = new ArrayList<>();
    }

    public void add(Entity entity){
        if(entity.getType().equals(Entity.EntityType.PARTICLE)) {
            particles.add((Particle) entity);
            return;
        }

        if(entity.getType().equals(Entity.EntityType.WALL)) {
            walls.add((Wall) entity);
            return;
        }

        System.out.println("Bug");
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

        return new NeighbourWrapper(particles, walls);
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

        this.particles.addAll(currentParticles);
        this.walls.addAll(currentWalls);
    }
}
