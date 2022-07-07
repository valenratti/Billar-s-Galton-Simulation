package entity;

import cell_index_method.Cell;
import lombok.Data;
import utils.Pair;

import java.util.Objects;

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

    public SquaredParticle(double x, double y, double vx, double vy, double mass, boolean idDisposable) {
        super(x,y);
        this.type = EntityType.SQUARED_PARTICLE;
//        this.id = idDisposable ? null : currentId++;
        this.sideLength = 0.004;
        this.vx = vx;
        this.vy = vy;
        this.mass = 0.01;
        this.ax = 0;
        this.ay = 0;
        this.isFixed = false;
    }

    public SquaredParticle(double x, double y, double vx, double vy, double mass, double sideLength, boolean idDisposable, boolean isFixed) {
        super(x,y);
        this.type = EntityType.SQUARED_PARTICLE;
//        this.id = idDisposable ? null : currentId++;
        this.sideLength = 0.004;
        this.vx = vx;
        this.vy = vy;
        this.mass = 0.01;
        this.ax = 0;
        this.ay = 0;
        this.isFixed = isFixed;
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
