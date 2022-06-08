package integrator;

import entity.Particle;

public interface Integrator {
    void nextStep(final Particle particle);
}
