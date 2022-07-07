package integrator;

import cell_index_method.NeighbourWrapper;
import entity.Entity;
import entity.Particle;
import entity.Wall;
import utils.Pair;

import java.util.List;

public class GranularMediaForce implements System {

    private Particle particle;
    private NeighbourWrapper neighbourWrapper;
    private double kn;
    private double kt;
    private double gamma;

    public GranularMediaForce(Particle particle, NeighbourWrapper neighbourWrapper) {
        this.particle = particle;
        this.neighbourWrapper = neighbourWrapper;
        this.kn = 1e+4;//N/m
        this.kt = 2 * kn; //N/m
        this.gamma = 10; //kg/s
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Override
    public Pair getForce() {
        if(neighbourWrapper.getWalls().stream().anyMatch((wall) -> wall.getWallType().equals(Wall.WallType.BOTTOM_WALL))) {
            particle.setVx(0.0);
            particle.setVy(0.0);
        }
        Pair forceFromParticles = forceFromParticles(neighbourWrapper.getParticles());
        Pair forceFromWalls = forceFromWalls(neighbourWrapper.getWalls());
        Pair gravityForce = new Pair(0.0,-9.8 * particle.getMass());
        return forceFromParticles.add(forceFromWalls).add(gravityForce);
    }

    Pair forceFromParticles(List<Particle> particles){
        Pair force = new Pair(0.0, 0.0);
        for(Particle neighbour : particles) {
            double overlapSize = Entity.overlap(particle, neighbour);
            if(overlapSize > 0) {
                double tangencialRelativeVelocity = particle.getTangencialRelativeVelocity(neighbour);
                double overlapD1 = Entity.overlapD1(particle, neighbour);
//                java.lang.System.out.println("OverlapD1 PARTICLES : " + overlapD1);
                double normalForce = -kn * overlapSize - gamma * overlapD1; //TODO: Agregar nueva resta de la ecuacion
//                double normalForce = -kn * overlapSize; //TODO: Agregar nueva resta de la ecuacion
                double tangencialForce = -kt * overlapSize * tangencialRelativeVelocity;
                double distance = Entity.distance(particle, neighbour);
                double normalizedXDistance = (neighbour.getX() - particle.getX()) / distance;
                double normalizedYDistance = (neighbour.getY() - particle.getY()) / distance;
                force.add(new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                        normalForce * normalizedYDistance + tangencialForce * normalizedXDistance));
            }
        }
        return force;
    }

    Pair forceFromWalls(List<Wall> walls){
        Pair force = new Pair(0.0, 0.0);
        for(Wall wall : walls) {
            double overlap = Entity.overlap(particle, wall);
            double overlapD1 = Entity.overlapD1(particle,wall);
//            java.lang.System.out.println("OverlapD1 WALLS : " + overlapD1);
            double relativeVelocity = particle.getTangencialRelativeVelocity(wall);
            double normal = -kn * overlap - gamma * overlapD1;
            double tangencial = -kt*overlap*relativeVelocity;
            switch (wall.getWallType()) {
                case TOP_WALL:
                    force.add(new Pair(tangencial, normal));
                    break;
                case BOTTOM_WALL:
                    force.add(new Pair(-tangencial, -normal));
                    break;
                case RIGHT_AREA_WALL:
                    force.add(new Pair(normal, -tangencial));
                    break;
                case LEFT_AREA_WALL:
                    force.add(new Pair(-normal, tangencial));
                    break;
            }

        }
        return force;
    }

}
