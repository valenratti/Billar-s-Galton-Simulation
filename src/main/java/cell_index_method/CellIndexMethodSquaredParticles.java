package cell_index_method;

import entity.Entity;
import entity.Particle;
import entity.SquaredParticle;
import entity.Wall;
import lombok.Data;
import utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class CellIndexMethodSquaredParticles {

    private final Map<CellCoordinates, Cell> cellMap;
    private Set<CellCoordinates> currentOccupiedCells;
    private boolean periodicBorder;
    private final int cellsPerRow;
    private final int cellsPerColumn;
    private final int negativeCellsPerColumn;
    private final double cellSideLength;
    private final CIMConfig config;
    private final List<SquaredParticle> particles;
    private final List<Wall> walls;
    private final Integer particlesN;

    public CellIndexMethodSquaredParticles(CIMConfig config) throws IOException {
        this.particlesN = config.getTotalParticles();
        this.config = config;
        this.cellSideLength = config.getMaxParticleRadius() * 2;
        this.cellsPerRow = calculateCellsPerRow();
        this.negativeCellsPerColumn = calculateCellsPerColumn(0.1);
        this.cellsPerColumn = calculateCellsPerColumn();
        this.cellMap = new HashMap<>();
        this.currentOccupiedCells = new HashSet<>();

        double currentY = 0.0;
        while (currentY< 1.0) {
            double currentX = 0.0;
            while (currentX < 0.3) {
                int row = (int) Math.floor((currentY + cellSideLength / 100) / this.cellSideLength);
                int column = (int) Math.floor((currentX + cellSideLength / 100) / this.cellSideLength);
                cellMap.put(new CellCoordinates(row, column), new Cell(row, column, new ArrayList<>()));
                currentX += cellSideLength;
            }
            currentY+= cellSideLength;
        }

        this.particles = spawnParticles();
        this.walls = spawnWalls();
    }

    private List<Wall> spawnWalls(){
        List<Wall> walls = new ArrayList<>();

//        int topCellRow = (int) Math.floor((4.0 - 0.0006/4) / this.cellSideLength);

        int leftWallColumn = cellMap.keySet().stream().min(Comparator.comparingInt(CellCoordinates::getColumn)).get().getColumn();
        Wall leftWall = new Wall(0.0, 1.0, 1.0, Wall.WallType.LEFT_AREA_WALL);

        int rightWallColumn = cellMap.keySet().stream().max(Comparator.comparingInt(CellCoordinates::getColumn)).get().getColumn();
        Wall rightWall = new Wall(0.3, 1.0, 1.0, Wall.WallType.RIGHT_AREA_WALL);

//        int topWallRow = cellMap.keySet().stream().max(Comparator.comparingInt(CellCoordinates::getRow)).get().getRow();
//        Wall topWall = new Wall(-0.6, 5.0, 1.2, Wall.WallType.TOP_WALL);

        int bottomWallRow = (int) Math.floor((0.1 + cellSideLength / 100) / this.cellSideLength);
        Wall bottomWall = new Wall(0.0, 0.1, 0.3, Wall.WallType.BOTTOM_WALL);

        Set<Integer> allRows = cellMap.keySet().stream().map(CellCoordinates::getRow).sorted().collect(Collectors.toSet());
        Set<Integer> allColumns = cellMap.keySet().stream().map(CellCoordinates::getColumn).sorted().collect(Collectors.toSet());

        for(Integer row : allRows){
            Cell leftCell = cellMap.get(new CellCoordinates(row,leftWallColumn));
            Cell rightCell = cellMap.get(new CellCoordinates(row,rightWallColumn));
            leftCell.addWall(leftWall);
            rightCell.addWall(rightWall);
        }

        for(Integer column : allColumns){
            Cell bottomCell = cellMap.get(new CellCoordinates(bottomWallRow,column));
            bottomCell.addWall(bottomWall);
        }

        return walls;
    }

    private List<SquaredParticle> spawnParticles() {
        List<SquaredParticle> squaredParticles = new ArrayList<>();
        int bottomRow = (int) Math.floor((0.5 + cellSideLength / 100) / this.cellSideLength);
        List<CellCoordinates> possibleCells = cellMap.keySet().stream().filter((cell) -> cell.getRow() >= bottomRow).collect(Collectors.toList());
        Collections.shuffle(possibleCells);

        for(int i=0; i< particlesN; i++){
            CellCoordinates coords = possibleCells.get(i);
            double particleX = coords.getColumn() * cellSideLength + (cellSideLength / 2); // centered
            double particleY = coords.getRow() * cellSideLength + cellSideLength / 2; //centered
            double randLength = Utils.rand(7.07 * 10e-3, 0.0106);
            SquaredParticle particle = new SquaredParticle(particleX, particleY, 0.0, 0.0, 0.01, randLength, false, false);
            Cell cell = cellMap.get(coords);
            cell.addSquaredParticle(particle);
            particle.setCell(cell);
            squaredParticles.add(particle);
        }
        return squaredParticles;
    }

    public Map<SquaredParticle, NeighbourWrapper> calculateNeighbours() {
        Map<SquaredParticle, NeighbourWrapper> neighboursMap = new HashMap<>();

        for(Cell cell : currentOccupiedCells.stream().map(cellMap::get).collect(Collectors.toList())){
            List<Cell> neighbourCells = calculateNeighbourCells(cell.getRow(), cell.getColumn())
                    .stream().map(cellMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<SquaredParticle> onlyParticleEntities = getParticleEntities(cell.getEntityList());

            for (SquaredParticle particle : onlyParticleEntities) {
                NeighbourWrapper currentParticleNeighbours = neighboursMap.getOrDefault(particle, new NeighbourWrapper());
                for(Cell neighbourCell : neighbourCells) {

                    //Identify all neighbours, they can be walls or particles
                    List<Entity> neighbours = neighbourCell.getEntityList()
                            .stream()
                            .filter((current) -> !current.isFixed())
//                            .filter((current) -> current.getY() != 0.0)
                            .filter(current -> !current.equals(particle))
                            .filter((current) -> Entity.distanceFromRadius(particle, current) <= 0.0)
                            .collect(Collectors.toList())
                            .stream().distinct().collect(Collectors.toList());

                    neighbours.forEach((neighbour) -> {
                        if(neighbour.getType().equals(Entity.EntityType.PARTICLE)) {
                            NeighbourWrapper neighbourNeighbours = neighboursMap.getOrDefault((Particle) neighbour, new NeighbourWrapper());
                            neighbourNeighbours.add(particle);
                            neighboursMap.put((SquaredParticle) neighbour, neighbourNeighbours);
                        }
                    });
                    currentParticleNeighbours.addAll(neighbours);
                }
                neighboursMap.put(particle, currentParticleNeighbours);
            }
        }
        return neighboursMap;
    }

    private List<SquaredParticle> getParticleEntities(List<Entity> entityList){
        return entityList.stream().map((entity) -> {
            if(entity.getType().equals(Entity.EntityType.SQUARED_PARTICLE))
                return (SquaredParticle) entity;
            else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private int calculateCellsPerRow(){
        double L = config.getAreaWidth();
        int possibleM = (int) Math.floor(L / this.cellSideLength);
        return possibleM == 0 ? 1 : possibleM;
    }

    private int calculateCellsPerColumn(){
        double L = config.getAreaHeight() - 0.1;
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

    public void updateParticles(){
        Set<CellCoordinates> currentOccupiedCells = new HashSet<>();
        for(SquaredParticle particle : particles.stream().filter(particle -> !particle.isFixed()).collect(Collectors.toList())) {
            int row = (int) Math.floor(particle.getY() / this.cellSideLength);
            int column = (int) Math.floor(particle.getX() / this.cellSideLength);
            try {
                Cell cell = cellMap.get(new CellCoordinates(row, column));
                particle.setCell(cell);
                cell.addSquaredParticle(particle);
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
