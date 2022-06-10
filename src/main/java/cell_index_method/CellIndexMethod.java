package cell_index_method;

import entity.Entity;
import entity.Obstacle;
import entity.Particle;
import entity.Wall;
import lombok.Data;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class CellIndexMethod {

    private final Map<CellCoordinates, Cell> cellMap;
    private Set<CellCoordinates> currentOccupiedCells;
    private boolean periodicBorder;
    private final int cellsPerRow;
    private final int cellsPerColumn;
    private final int negativeCellsPerColumn;
    private final double cellSideLength;
    private final CIMConfig config;
    private final List<Particle> particles;
    private final List<Obstacle> obstacles;
    private final List<Wall> walls;

    public CellIndexMethod(CIMConfig config) {
        this.config = config;
        this.cellSideLength = config.getMaxParticleRadius() * 2;
        this.cellsPerRow = calculateCellsPerRow();
        this.negativeCellsPerColumn = calculateCellsPerColumn(10.0);
        this.cellsPerColumn = calculateCellsPerColumn();
        this.cellMap = new HashMap<>();
        this.currentOccupiedCells = new HashSet<>();


        for(int i = cellsPerColumn - 1; i >= 0; i--)
            for(int j = -cellsPerRow/2; j < cellsPerRow/2; j++)
                cellMap.put(new CellCoordinates(i, j), new Cell(i, j, new ArrayList<>()));

        for(int i=-1; i>-negativeCellsPerColumn; i--)
            for(int j = -cellsPerRow/2; j < cellsPerRow/2; j++)
                cellMap.put(new CellCoordinates(i, j), new Cell(i, j, new ArrayList<>()));



        this.particles = spawnParticles();
        this.obstacles = spawnObstacles();
        this.walls = spawnWalls();
    }

    public List<Wall> spawnWalls(){
        List<Wall> walls = new ArrayList<>();
        double minY = this.obstacles.stream().min(Comparator.comparingDouble(Entity::getY)).get().getY();
        List<Obstacle> firstRowObstacles = this.obstacles.stream().filter((o) -> o.getY() == minY).collect(Collectors.toList());
        for(Obstacle obstacle : firstRowObstacles){
            int row = (int) Math.floor(obstacle.getY() / this.cellSideLength);
            int column = (int) Math.floor(obstacle.getX() / this.cellSideLength);
            Wall wall = new Wall(obstacle.getX(), obstacle.getY(), 10.0);
            walls.add(wall);
            for(int i=0; i<16; i++){
                Cell cell = cellMap.get(new CellCoordinates(row+i, column));
                cell.addWall(wall);
            }
        }
        return walls;
    }

    public List<Particle> spawnParticles() {
        List<Particle> particles = new ArrayList<>();
        particles.add(new Particle(0.0, 65.0, 0.0, 0.0, 0.01, false));
        particles.add(new Particle(-60.0, -10.0, 0.0, 0.0, 0.01, 0.00001, false, true));
        particles.add(new Particle(-60.0, 70.0, 0.0, 0.0, 0.01, 0.00001, false, true));
        particles.add(new Particle(60.0, -10.0, 0.0, 0.0, 0.01, 0.00001, false, true));
        particles.add(new Particle(60.0, 70.0, 0.0, 0.0, 0.01, 0.00001, false, true));
        return particles;
    }

    public static List<Obstacle> spawnObstacles(){
        List<Obstacle> obstacles = new ArrayList<>();
        double d = 5.4;
        double d2 = 4.7;
        double currentX;
        for(int i=0; i<14; i++){
            if(i%2 == 0){
                currentX = -58.0 - d;
            }else{
                currentX = -58.0 + d/2 - d;
            }
            for(int j=0; j<22; j++){
                double x = currentX + d;
                double y =  i*d2;
                Obstacle obstacle = new Obstacle(x,y);
                obstacles.add(obstacle);
                int row = (int) Math.floor(y / this.cellSideLength);
                int column = (int) Math.floor(x / this.cellSideLength);
                Cell cell = cellMap.get(new CellCoordinates(row, column));
                obstacle.setCell(cell);
                cell.addObstacle(obstacle);
                currentOccupiedCells.add(new CellCoordinates(row, column));
                currentX += d;
            }
        }
        return obstacles;
    }

    public Map<Particle, NeighbourWrapper> calculateNeighbours() {
        Map<Particle, NeighbourWrapper> neighboursMap = new HashMap<>();

        for(Cell cell : cellMap.values()){
            List<Cell> neighbourCells = calculateNeighbourCells(cell.getRow(), cell.getColumn())
                    .stream().map(cellMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<Particle> onlyParticleEntities = getParticleEntities(cell.getEntityList());

            for (Particle particle : onlyParticleEntities) {
                NeighbourWrapper currentParticleNeighbours = neighboursMap.getOrDefault(particle, new NeighbourWrapper());
                for(Cell neighbourCell : neighbourCells) {

                    //Identify all neighbours, they can be walls, obstacles or particles
                    List<Entity> neighbours = neighbourCell.getEntityList()
                            .stream()
                            .filter((current) -> !current.isFixed())
                            .filter(current -> !current.equals(particle))
                            .filter((current) -> Entity.distance(particle, current) <= 0.0)
                            .collect(Collectors.toList())
                            .stream().distinct().collect(Collectors.toList());

                    neighbours.forEach((neighbour) -> {
                        if(neighbour.getType().equals(Entity.EntityType.PARTICLE)) {
                            NeighbourWrapper neighbourNeighbours = neighboursMap.getOrDefault((Particle) neighbour, new NeighbourWrapper());
                            neighbourNeighbours.add(particle);
                            neighboursMap.put((Particle) neighbour, neighbourNeighbours);
                        }
                    });
                    currentParticleNeighbours.addAll(neighbours);
                }
                neighboursMap.put(particle, currentParticleNeighbours);
            }
        }
        return neighboursMap;
    }

    private List<Particle> getParticleEntities(List<Entity> entityList){
        return entityList.stream().map((entity) -> {
            if(entity.getType().equals(Entity.EntityType.PARTICLE))
                return (Particle) entity;
            else return null;
        }).collect(Collectors.toList());
    }

    private int calculateCellsPerRow(){
        double L = config.getAreaWidth();
        int possibleM = (int) Math.floor(L / this.cellSideLength);
        return possibleM == 0 ? 1 : possibleM;
    }

    private int calculateCellsPerColumn(){
        double L = config.getAreaHeight() - 10.0;
        int possibleM = (int) Math.floor(L / this.cellSideLength);
        return possibleM == 0 ? 1 : possibleM;
    }

    private int calculateCellsPerColumn(double L){
        int possibleM = (int) Math.floor(L / this.cellSideLength);
        return possibleM == 0 ? 1 : possibleM;
    }

    private List<CellCoordinates> calculateNeighbourCells(int i, int j){
        int M = this.cellsPerColumn;
        List<CellCoordinates> cellCoordinates = new ArrayList<>();
        //Add itself as neighbour
        cellCoordinates.add(new CellCoordinates(i,j));
        boolean lastCol = j + 1 == M, firstRow = i == 0, lastRow = i + 1 == M;

        if(!lastCol) {
            cellCoordinates.add(new CellCoordinates(i, j + 1)); //Derecha
            if(!lastRow)
                cellCoordinates.add(new CellCoordinates(i + 1, j + 1)); // Diagonal abajo
            if(!firstRow)
                cellCoordinates.add(new CellCoordinates(i - 1, j + 1)); // Diagonal arriba
        }
        if(!firstRow) {
            cellCoordinates.add(new CellCoordinates(i - 1, j)); //  Arriba
        }
        return cellCoordinates;
    }

    public void updateParticles(List<Particle> particles){
        Set<CellCoordinates> currentOccupiedCells = new HashSet<>();
        for(Particle particle : particles.stream().filter(particle -> !particle.isFixed()).collect(Collectors.toList())) {
            int row = (int) Math.floor(particle.getY() / this.cellSideLength);
            int column = (int) Math.floor(particle.getX() / this.cellSideLength);
            try {
                Cell cell = cellMap.get(new CellCoordinates(row, column));
                particle.setCell(cell);
                cell.addParticle(particle);
                currentOccupiedCells.add(new CellCoordinates(row, column));
            } catch (Exception e){
                //                    FileWriter.printPositions(0,particles);
                System.out.println("Wrong dt. entity.Particle with id:" + particle.getId());
                System.out.println(row + " " + column);
                System.out.println(particle.getY() + " " + particle.getX());
                System.exit(0);
            }
        }
        this.currentOccupiedCells = currentOccupiedCells;
    }

    public void clear(){
        cellMap.values().forEach(Cell::clearParticles);
    }

}
