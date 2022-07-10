package cell_index_method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIMConfig {
    private Double areaHeight; //L
    private Double areaWidth; //W
    private Integer totalParticles; //N
    private Double minParticleRadius;
    private Double maxParticleRadius;
    private Double particleMass;
    private Double openWidth;
}
