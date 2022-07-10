package entity;

import cell_index_method.Cell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.GFG;
import utils.Pair;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity {
    private static Long currentId = 0L;
    protected Long id;
    protected double x;
    protected double y;
    protected EntityType type;
    protected Cell cell;
    private final static double DISK_RADIUS = 1e-3; // FIXME: TBD

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
        this.id = currentId++;
    }

    public static double distanceFromRadius(Particle particle, Entity other) {
        if(other.getType().equals(EntityType.WALL))
            return distance(particle, other) - particle.getRadius();

        double xDistance = particle.getX() - other.getX();
        double yDistance = particle.getY() - other.getY();
        final double hypot = Math.hypot(Math.abs(xDistance), Math.abs(yDistance));

        return hypot - ((Particle)particle).getRadius() - ((Particle)other).getRadius();
    }

    public static double distanceFromRadius(SquaredParticle particle, Entity other) {
        List<List<Particle>> disks = overlappedDisksList(particle, other);
        assert disks != null;

        Particle first = new Particle();
        Particle second = new Particle();
        double minDis = 1e10;
        for (List<Particle> pairs : disks) {
            Particle particle1 = pairs.get(0);
            Particle particle2 = pairs.get(1);
            double distance = distance(particle1, particle2);
            if (distance <= minDis) {
                first = particle1;
                second = particle2;
                minDis = distance;
            }
        }
        if(other.getType().equals(EntityType.SQUARED_PARTICLE)) {
            double toReturn = minDis - first.getRadius() - second.getRadius();
//            System.out.println(toReturn);
            return toReturn;
        }else{
            double toReturn = distance(first, other) - first.getRadius();
//            System.out.println(toReturn);
            return toReturn;
        }
    }

    public enum EntityType {
        PARTICLE, SQUARED_PARTICLE, WALL    //TODO: Fix methods for squared particles
    }

    public static double distance(Entity entity, Entity other) {
        if(entity.getType().equals(EntityType.PARTICLE) || entity.getType().equals(EntityType.SQUARED_PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE) || other.getType().equals(EntityType.SQUARED_PARTICLE)) {
                double xDistance = entity.getX() - other.getX();
                double yDistance = entity.getY() - other.getY();
                return Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
            }

            if(other.getType().equals(EntityType.WALL)) {
                Wall wall = (Wall) other;

                if(wall.getWallType().equals(Wall.WallType.BOTTOM_WALL))
                    return Math.abs(entity.getY() - wall.getY());

                if(wall.getWallType().equals(Wall.WallType.RIGHT_AREA_WALL))
                    return Math.abs(entity.getX() - wall.getX());

                if (wall.getWallType().equals(Wall.WallType.LEFT_AREA_WALL))
                    return Math.abs(entity.getX() - wall.getX());

                return Math.abs(entity.getY() - wall.getY());
            }
        }
        System.out.println("BUG");
        return 0.0;
    }

    public static double overlap(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)) {
                double overlap = ((Particle) entity).getRadius() + ((Particle) other).getRadius() - distance(entity, other);
                return overlap<0 ? 0 : overlap;
            }

            if(other.getType().equals(EntityType.WALL)){
                Wall wall = (Wall) other;

                if(wall.getWallType().equals(Wall.WallType.RIGHT_AREA_WALL)) {
                    System.out.println("overlap right wall " + (entity.getX() + ((Particle)entity).getRadius() - 0.3));
                    return entity.getX() + ((Particle)entity).getRadius() - 0.3;
                }

                if (wall.getWallType().equals(Wall.WallType.LEFT_AREA_WALL)) {
                    if(entity.getX() < wall.getX()) {
                        System.out.println("overlap left wall " + (((Particle)entity).getRadius() + distance(entity,wall)));
                        return ((Particle)entity).getRadius() + distance(entity,wall);
                    }
                    System.out.println("overlap left wall " + (((Particle)entity).getRadius() - distance(entity,wall)) );
                    return ((Particle)entity).getRadius() - distance(entity,wall);
                }

                if(wall.getWallType().equals(Wall.WallType.BOTTOM_WALL)){
                    System.out.println("overlap bottom wall " + (((Particle)entity).getRadius() - distance(entity,wall)) );
                    return ((Particle)entity).getRadius() - distance(entity,wall);
                }

                return Math.abs(0.1 + ((Particle)entity).getY() - ((Particle)entity).getRadius());
            }
        }

        System.out.println("Bug");
        return 0.0;
    }

    /* Para el caso entre dos particulas:
    *  Retorna una lista de 8 listas de Particle (con 2 Particle cada una).
    *  Las primeras 4 listas: disk1 es el vertice de la p1 y disk2 el punto en el edge de p2.
    *  Las ultimas 4 listas: disk1 es el punto en el edge de p1 y disk2 el vertice de la p2.
    *  Puede darse el caso que los ambos disks sean de los vertices (de cada una).
    *
    *  Para el caso con una pared, retorna una lista de 4 elementos.
    *  Donde disk1 es el vertice de la particula y disk2 el punto de contacto en la pared.
    *  Puede darse el caso que sea un vertice de la pared.
    * */
    public static List<List<Particle>> overlappedDisksList(SquaredParticle squaredParticle, Entity other) {
        List<Pair> vertexList = squaredParticle.getVertexPositionList();

        if (other.getType().equals(EntityType.SQUARED_PARTICLE)) {
            List<Pair> vertexListOther = ((SquaredParticle) other).getVertexPositionList();

            List<List<Pair>> aux1 = vertexAndEdgeAtMinDistance(vertexList, vertexListOther);
            List<List<Pair>> aux2 = vertexAndEdgeAtMinDistance(vertexListOther, vertexList);
            aux1.addAll(aux2);

            if(aux1.size() != 8)
                System.out.println("Something is wrong, list size should be 8.");

            return getDisksList(squaredParticle, other, aux1);
        }

        if (other.getType().equals(EntityType.WALL)) {
            List<Pair> vertexListOther = ((Wall) other).getVertexList();  // FIXME: check bottom wall case
            List<List<Pair>> aux1 = vertexAndEdgeAtMinDistance(vertexList, vertexListOther);

            if(aux1.size() != 4)
                System.out.println("Something is wrong, list size should be 4.");

            return getDisksList(squaredParticle, other, aux1);
        }

        System.out.println("overlappedDisks BUG");
        return null;    // shouldn't happen
    }

    // retorna una lista de listas de 4 pares: los puntos del edge, el vertice y la distancia entre los mismos
    private static List<List<Pair>> vertexAndEdgeAtMinDistance(List<Pair> vertexList, List<Pair> vertexListOther) {
        List<List<Pair>> lists = new ArrayList<>(4);

        for(Pair vertex : vertexList) {
            double minD = 1e10; // infinite
            Pair lineP1 = new Pair(0, 0), lineP2 = new Pair(0, 0), v = new Pair(0, 0);

            if(vertexListOther.size() == 2){
                Pair p1 = vertexListOther.get(0);
                Pair p2 = vertexListOther.get(1);
                double d = GFG.minDistance(p1, p2, vertex);
                lists.add(Arrays.asList(p1, p2, vertex, new Pair(d, 0)));
            }else {
                // LEFT_DOWN, LEFT_UP, RIGHT_UP, RIGHT_DOWN
                for (int i = 0; i < SquaredParticle.VertexType.values().length; i++) {
                    try {
                        Pair p1 = vertexListOther.get(i);
                        Pair p2 = vertexListOther.get((i + 1) % 4);
                        double d = GFG.minDistance(p1, p2, vertex);

                        if (d < minD) {
                            minD = d;
                            lineP1 = p1;
                            lineP2 = p2;
                            v = vertex;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("here");
                    }
                }
                lists.add(Arrays.asList(lineP1, lineP2, v, new Pair(minD, 0)));
            }
        }

        return lists;
    }

    private static List<List<Particle>> getDisksList(SquaredParticle particle, Entity other, List<List<Pair>> lists) {
        List<List<Particle>> disksList = new ArrayList<>();

        for(List<Pair> list : lists) {
            double minD = list.get(3).getX();
            Pair edgeP1 = list.get(0);
            Pair edgeP2 = list.get(1);
            Pair v = list.get(2);

            Pair p = (Utils.distance(v, edgeP1) < Utils.distance(v, edgeP2)) ? edgeP1 : edgeP2;
            Particle vParticle = new Particle(v.getX(), v.getY(), particle.getVx(), particle.getVy(), particle.getMass(), DISK_RADIUS, false, false);

            if (Double.compare(Utils.distance(v, p), minD) == 0) {   // El punto mas cercano es otro vertice
                double vx = 0, vy = 0, mass = 0;
                if(other.getType().equals(EntityType.SQUARED_PARTICLE)){
                    vx = ((SquaredParticle)other).getVx();
                    vy = ((SquaredParticle)other).getVy();
                    mass = ((SquaredParticle)other).getMass();
                }
                disksList.add(Arrays.asList(vParticle, new Particle(p.getX(), p.getY(), vx, vy, mass, DISK_RADIUS, false, false)));
            }
            else {
                double d = Math.sqrt(Math.pow(Utils.distance(v, edgeP1), 2) - Math.pow(minD, 2));    // distance between edge vertex and point to be found
                Pair contactPoint = GFG.getPoint(edgeP1, edgeP2, d);   // punto buscado en el edge
                double vx = 0, vy = 0, mass = 0;
                if(other.getType().equals(EntityType.SQUARED_PARTICLE)){
                    vx = ((SquaredParticle)other).getVx();
                    vy = ((SquaredParticle)other).getVy();
                    mass = ((SquaredParticle)other).getMass();
                }
                disksList.add(Arrays.asList(vParticle, new Particle(contactPoint.getX(), contactPoint.getY(), vx, vy, mass, DISK_RADIUS, false, false)));
            }
        }

        return disksList;
    }

    public static double overlapD1(Entity entity, Entity other){
        if(entity.getType().equals(EntityType.PARTICLE)) {
            if(other.getType().equals(EntityType.PARTICLE)) {
                double relativeVelocityX = ((Particle)other).getVx() - ((Particle)entity).getVx();
                double relativeVelocityY = ((Particle)other).getVy() - ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = (other.getX() -entity.getX()) / distance;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;
                double projectedOther = ((Particle)other).getVx() * normalizedXDistance + ((Particle)other).getVy() * normalizedYDistance;
                double projectedEntity = ((Particle)entity).getVx() * normalizedXDistance + ((Particle)entity).getVy() * normalizedYDistance;
                double valueBefore = relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
                double valueAfter = projectedOther - projectedEntity;

                return -(projectedOther - projectedEntity);
            }

            if(other.getType().equals(EntityType.WALL)) {
                double relativeVelocityX =   ((Particle)entity).getVx();
                double relativeVelocityY =  ((Particle)entity).getVy();
                double distance = Entity.distance(entity,other);
                double normalizedXDistance = 0;
                double normalizedYDistance = (other.getY() - entity.getY()) / distance;

                return relativeVelocityX * normalizedXDistance + relativeVelocityY * normalizedYDistance;
            }
        }

        System.out.println("Bug");
        return 0.0;
    }

    public boolean isFixed(){
        if(getType().equals(EntityType.PARTICLE)){
            return ((Particle) this).isFixed();
        }else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
