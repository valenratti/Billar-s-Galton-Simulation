package integrator;

import cell_index_method.NeighbourWrapperSquaredParticle;
import entity.Entity;
import entity.Particle;
import entity.SquaredParticle;
import entity.Wall;
import utils.Pair;

import java.util.List;

public class GranularMediaForceSquaredParticle /*implements System*/ {

    private SquaredParticle particle;
    private NeighbourWrapperSquaredParticle neighbourWrapper;
    private double kn;
    private double kt;
    private double gamma;

    public GranularMediaForceSquaredParticle(SquaredParticle particle, NeighbourWrapperSquaredParticle neighbourWrapper) {
        this.particle = particle;
        this.neighbourWrapper = neighbourWrapper;
        this.kn = 1e+4;//N/m
        this.kt = 2 * kn; //N/m
        this.gamma = 10; //kg/s
    }

    public void setParticle(SquaredParticle particle) {
        this.particle = particle;
    }

    Pair getForce() {
        // TODO: CHECK
//        if(neighbourWrapper.getWalls().stream().anyMatch((wall) -> wall.getWallType().equals(Wall.WallType.BOTTOM_WALL))) {
//            particle.setVx(0.0);
//            particle.setVy(0.0);
//        }
        Pair forceFromParticles = forceFromParticles(neighbourWrapper.getParticles());
        Pair forceFromWalls = forceFromWalls(neighbourWrapper.getWalls());
        Pair gravityForce = new Pair(0.0,-9.8 * particle.getMass());

        return forceFromParticles.add(forceFromWalls).add(gravityForce);
    }

    private Pair forceFromParticles(List<SquaredParticle> particles){
        Pair force = new Pair(0.0, 0.0);

        for(SquaredParticle neighbour : particles) {
            List<List<Particle>> disksList = Entity.overlappedDisksList(particle, neighbour);

            for(List<Particle> disks : disksList) {
                Particle disk1 = disks.get(0), disk2 = disks.get(1);
                double overlapSize = Entity.overlap(disk1, disk2);

                if (overlapSize > 0) {
                    double tangencialRelativeVelocity = disk1.getTangencialRelativeVelocity(disk2);  // TODO: check
                    double normalForce = -kn * overlapSize;
                    double tangencialForce = -kt * overlapSize * tangencialRelativeVelocity;
                    double distance = Entity.distance(disk1, disk2);
                    double normalizedXDistance = (disk2.getX() - disk1.getX()) / distance;
                    double normalizedYDistance = (disk2.getY() - disk1.getY()) / distance;
                    force.add(new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                            normalForce * normalizedYDistance + tangencialForce * normalizedXDistance));
                }
            }
        }

        return force;
    }

    private Pair forceFromWalls(List<Wall> walls){
        Pair force = new Pair(0.0, 0.0);

        for(Wall wall : walls) {
            List<List<Particle>> disksList = Entity.overlappedDisksList(particle, wall);

            for (List<Particle> disks : disksList) {
                Particle disk1 = disks.get(0), disk2 = disks.get(1);
                double overlap = Entity.overlap(disk1, disk2);

                if (overlap > 0) {
                    double relativeVelocity = disk1.getTangencialRelativeVelocity(disk2); // TODO: check
                    double normal = -kn * overlap;
                    double tangencial = -kt * overlap * relativeVelocity;

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
            }
        }

        return force;
    }

}
