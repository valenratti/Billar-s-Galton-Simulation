package integrator;

import entity.Particle;
import utils.Pair;

public interface System {
    void setParticle(Particle particle);
    Pair getForce();
}
