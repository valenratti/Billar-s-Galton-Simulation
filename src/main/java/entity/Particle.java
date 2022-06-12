package entity;

import cell_index_method.Cell;
import lombok.Data;
import utils.Pair;

import java.util.Objects;

@Data
public class Particle extends Entity{
//    private static Long currentId = 0L;

//    private Long id;
    private double radius;
    private double vx;
    private double vy;
    private double mass;
    private static final double k = 1e10;
    private double ax; //acceleration x
    private double ay; //acceleration y
    private double pressure;
    private Cell cell;
    private boolean isFixed;
    private boolean reachedBin = false;

    public Particle(double x, double y, double vx, double vy, double mass, boolean idDisposable) {
        super(x,y);
        this.type = EntityType.PARTICLE;
//        this.id = idDisposable ? null : currentId++;
        this.radius = 0.006;
        this.vx = vx;
        this.vy = vy;
        this.mass = 0.01;
        this.ax = 0;
        this.ay = 0;
        this.isFixed = false;
    }

    public Particle(double x, double y, double vx, double vy, double mass, double radius, boolean idDisposable, boolean isFixed) {
        super(x,y);
        this.type = EntityType.PARTICLE;
//        this.id = idDisposable ? null : currentId++;
        this.radius = 0.006;
        this.vx = vx;
        this.vy = vy;
        this.mass = 0.01;
        this.ax = 0;
        this.ay = 0;
        this.isFixed = isFixed;
    }

    public double getRelativeVelocityModule(Particle other){
        return Math.hypot(this.vx - other.getVx(), this.vy - other.getVy());
    }

    public double getTangencialRelativeVelocity(Particle other){
        double distance = Entity.distance(this, other);
        double normalizedXDistance = (other.getX() - this.getX()) / distance;
        double normalizedYDistance = (other.getY() - this.getY()) / distance;
        Pair tangencial = new Pair(-normalizedYDistance, normalizedXDistance);
        double relativeVelocityX = this.getVx() - other.getVx();
        double relativeVelocityY = this.getVy() - other.getVy();
        return relativeVelocityX * tangencial.getX() + relativeVelocityY * tangencial.getY();
    }

    public double getTangencialRelativeVelocity(Obstacle other){
        double distance = Entity.distance(this, other);
        double normalizedXDistance = (other.getX() - this.getX()) / distance;
        double normalizedYDistance = (other.getY() - this.getY()) / distance;
        Pair tangencial = new Pair(-normalizedYDistance, normalizedXDistance);
        double relativeVelocityX = this.getVx();
        double relativeVelocityY = this.getVy();
        return relativeVelocityX * tangencial.getX() + relativeVelocityY * tangencial.getY();
    }

    public double getTangencialRelativeVelocity(Wall wall) {
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

//    public double getDistance(Particle p){
//        return this.getPosition().distance(p.getPosition()).getModule();
//    }

    public static double distance(Particle p1, Particle p2, double length) {
        double y = Math.abs(p2.getY() - p1.getY());
        double x = Math.abs(p2.getX() - p1.getX());
        double h = Math.hypot(x, y);
        return h - p1.getRadius() - p2.getRadius();
    }

//    public double getOverlap(Particle p){
//        //ξij = Ri + Rj - |rj - ri|
//        double overlapSize = this.getRadius() + p.getRadius() - this.getDistance(p);
//        return (overlapSize < 0)? 0 : overlapSize;
//    }
//
//    public double getRelativeVelocity(Particle p, Vector tangencial){
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

        Particle particle = (Particle) o;

        return id.equals(particle.id);
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
