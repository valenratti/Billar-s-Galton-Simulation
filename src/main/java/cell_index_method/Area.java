package cell_index_method;

import entity.Particle;
import lombok.Data;

import java.util.List;

@Data
public class Area {
    private double width;
    private double height;
    private double rc;
    private List<Particle> particleList;
    private boolean periodicBorder;

    public Area(double width, double height, double exitWidth, List<Particle> particleList) {
        this.width = width;
        this.height = height;
        this.particleList = particleList;
        this.rc = 0;
        this.periodicBorder = false;
    }

    public void addParticle(Particle particle){
        particleList.add(particle);
    }
}
