package integrator;

import cell_index_method.NeighbourWrapperSquaredParticle;
import entity.Entity;
import entity.Particle;
import entity.SquaredParticle;
import entity.Wall;
import utils.GFG;
import utils.Pair;

import java.util.List;

public class GranularMediaForceSquaredParticle /*implements System*/ {

    private SquaredParticle particle;
    private NeighbourWrapperSquaredParticle neighbourWrapper;
    private final double kn;
    private final double kt;
    private final double gamma;

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
        Pair forceFromParticles = forceFromParticles(neighbourWrapper.getParticles());
        Pair forceFromWalls = forceFromWalls(neighbourWrapper.getWalls());
        Pair gravityForce = new Pair(0.0,-9.8 * particle.getMass());
        if(forceFromWalls.getX() > 0 || forceFromWalls.getY() > 0){
            java.lang.System.out.println("see");
        }

        return forceFromParticles.add(forceFromWalls).add(gravityForce);
    }

    private Pair forceFromParticles(Iterable<SquaredParticle> particles){
        Pair force = new Pair(0.0, 0.0);

        for(SquaredParticle neighbour : particles) {
            List<List<Particle>> disksList = Entity.overlappedDisksList(particle, neighbour);
            double torque = 0.0;
            for(List<Particle> disks : disksList) {
                Particle disk1 = disks.get(0), disk2 = disks.get(1);
                double overlapSize = Entity.overlap(disk1, disk2);
                if (overlapSize > 0 && overlapSize < 1000) {
                    double tangencialRelativeVelocity = disk1.getTangencialRelativeVelocity(disk2);  // TODO: check
                    double normalForce = -kn * overlapSize;
                    double tangencialForce = -kt * overlapSize * tangencialRelativeVelocity;
                    double distance = Entity.distance(disk1, disk2);
                    double normalizedXDistance = (disk2.getX() - disk1.getX()) / distance;
                    double normalizedYDistance = (disk2.getY() - disk1.getY()) / distance;
                    Pair appliedForce = new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                            normalForce * normalizedYDistance + tangencialForce * normalizedXDistance);
                    Pair overlapPoint = GFG.getPoint(new Pair(disk1.getX(), disk1.getY()), new Pair(disk2.getX(), disk2.getY()), disk1.getRadius() - overlapSize/2);
                    Pair centerMassToOverlapPointVector = new Pair(this.particle.getX() - overlapPoint.getX(), this.particle.getY() - overlapPoint.getY());
                    torque += centerMassToOverlapPointVector.getX() * appliedForce.getY() - centerMassToOverlapPointVector.getY() * appliedForce.getX();
                    force.add(appliedForce);
                }
            }
            double angularAcceleration = 6 * torque / (this.particle.getMass() * Math.pow(this.particle.getSideLength(),2));
            particle.setAngleAcceleration(angularAcceleration);
        }
        return force;
    }

    private Pair forceFromWalls(Iterable<Wall> walls){
        Pair force = new Pair(0.0, 0.0);

        for(Wall wall : walls) {
            List<List<Particle>> disksList = Entity.overlappedDisksList(particle, wall);

            assert disksList != null;
            for (List<Particle> disks : disksList) {
                Particle disk1 = disks.get(0);
                double overlap = Entity.overlap(disk1, wall);

                if (overlap > 0) {
                    double relativeVelocity = disk1.getTangencialRelativeVelocity(wall); // TODO: check
                    double normal = -kn * overlap;
                    double tangencial = -kt * overlap * relativeVelocity;

                    switch (wall.getWallType()) {
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
