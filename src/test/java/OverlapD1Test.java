import entity.Entity;
import entity.Particle;
import org.junit.jupiter.api.Test;

public class OverlapD1Test {
    @Test
    public void test(){
        Particle particle1 = new Particle(0.0, 0.0, 3.0, 0.0, 0.0, 0.5, false, false);
        Particle particle2 = new Particle(0.3, 0.0, -1.0, 2.0, 0.0, 0.5, false, false);
        System.out.println(Entity.overlapD1(particle1, particle2));
    }
}
