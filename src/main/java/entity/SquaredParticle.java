package entity;

import cell_index_method.Cell;
import lombok.Data;
import utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class SquaredParticle extends Entity {
//    private static Long currentId = 0L;

    //    private Long id;
    private double sideLength;
    private double vx;
    private double vy;
    private double mass;
    private static final double k = 1e10;
    private double ax; //acceleration x
    private double ay; //acceleration y
    private double pressure;
    private Cell cell;
    private boolean isFixed;
    private double torque;
    private double angleAcceleration;
    private List<Pair> vertexList;

    public enum VertexType {
        LEFT_DOWN, LEFT_UP, RIGHT_UP, RIGHT_DOWN
    }



    public SquaredParticle(double x, double y, double vx, double vy, double mass, double sideLength, boolean idDisposable, boolean isFixed) {
        super(x,y);
        this.type = EntityType.SQUARED_PARTICLE;
        this.sideLength = sideLength;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.ax = 0;
        this.ay = 0;
        this.isFixed = isFixed;
        this.torque = 0;
        this.angleAcceleration = 0;
        this.vertexList = new ArrayList<>();
        initVertexList();
    }

    public double getRelativeVelocityModule(SquaredParticle other) {
        return Math.hypot(this.vx - other.getVx(), this.vy - other.getVy());
    }

    public double getTangencialRelativeVelocity(SquaredParticle other) {
        // TODO: check, now is a squared particle

        double distance = Entity.distance(this, other);
        double normalizedXDistance = (other.getX() - this.getX()) / distance;
        double normalizedYDistance = (other.getY() - this.getY()) / distance;
        Pair tangencial = new Pair(-normalizedYDistance, normalizedXDistance);
        double relativeVelocityX = this.getVx() - other.getVx();
        double relativeVelocityY = this.getVy() - other.getVy();

        return relativeVelocityX * tangencial.getX() + relativeVelocityY * tangencial.getY();
    }

    public double getTangencialRelativeVelocity(Wall wall) {
        // TODO: check, now is a squared particle

        if(this.getX() < wall.getX())
            return -vy;
        else
            return vy;
    }

    public double getVelocityModule() {
        return Math.hypot(getVx(), getVy());
    }

    public void updateState(double x, double y, double vx, double vy) {
        setX(x);
        setY(y);
        setVx(vx);
        setVy(vy);
    }

    public double getKineticEnergy() {
        return 0.5 * this.mass * Math.pow(getVelocityModule() , 2);
    }

//    public double getDistance(SquaredParticle p){
//        return this.getPosition().distance(p.getPosition()).getModule();
//    }

    public static double distance(SquaredParticle p1, SquaredParticle p2, double length) {
        // TODO: check, now is a squared particle

        double y = Math.abs(p2.getY() - p1.getY());
        double x = Math.abs(p2.getX() - p1.getX());
        double h = Math.hypot(x, y);
        return h - p1.getSideLength() - p2.getSideLength();
    }

//    public double getOverlap(SquaredParticle p){
//        //ξij = Ri + Rj - |rj - ri|
//        double overlapSize = this.getSideLength() + p.getSideLength() - this.getDistance(p);
//        return (overlapSize < 0)? 0 : overlapSize;
//    }
//
//    public double getRelativeVelocity(SquaredParticle p, Vector tangencial){
//        Vector v = this.getVelocity().subtract(p.getVelocity());
//        return v.getX() * tangencial.getX() + v.getY() * tangencial.getY();
//    }

    private void initVertexList() {
        final double halfL = sideLength / 2;

        Pair v1 = new Pair(x - halfL, y - halfL);   // left down
        Pair v2 = new Pair(x - halfL, y + halfL);   // left up
        Pair v3 = new Pair(x + halfL, y + halfL);   // right up
        Pair v4 = new Pair(x + halfL, y - halfL);   // right down

        vertexList.add(v1);
        vertexList.add(v2);
        vertexList.add(v3);
        vertexList.add(v4);
    }

    public List<Pair> getVertexPositionList() {
        // TODO: Considerar el torque
        // FIXME: Update list instead
        final double halfL = sideLength / 2;

        Pair v1 = new Pair(x - halfL, y + halfL);   // left up
        Pair v2 = new Pair(x - halfL, y - halfL);   // left down
        Pair v3 = new Pair(x + halfL, y + halfL);   // right up
        Pair v4 = new Pair(x + halfL, y - halfL);   // right down

        return Arrays.asList(v1, v2, v3, v4);
    }

    public double getRadius(){
        return sideLength * Math.sqrt(2) / 2;
    }

    public void rotateBy(Double angle){
        List<Pair> tempVertexPositions = this.getVertexPositionList()
                .stream().map((pair) -> new Pair(pair.getX() - x, pair.getY() - y))
                .collect(Collectors.toList());

        for(Pair pair : tempVertexPositions) {
            double realPart = Math.cos(angle) * pair.getX() - Math.sin(angle) * pair.getY();
            double imaginaryPart = Math.cos(angle) * pair.getY() + Math.sin(angle) * pair.getX();
            pair.setX(realPart + this.x);
            pair.setY(imaginaryPart + this.y);
        }
        this.vertexList = tempVertexPositions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SquaredParticle squaredParticle = (SquaredParticle) o;

        return id.equals(squaredParticle.id);
    }

    @Override
    public String toString() {
        return "SquaredParticle{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
