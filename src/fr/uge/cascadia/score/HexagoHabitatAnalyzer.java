package fr.uge.cascadia.score;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import fr.uge.cascadia.Position;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Habitat;
import fr.uge.cascadia.tile.Tile;



/**
 * The HexagoHabitatAnalyzer class implements the HabitatAnalyzer interface
 * and provides functionality for analyzing habitats on a hexagonal board.
 *
 * @param board The game board being analyzed. Must not be null.
 */
public record HexagoHabitatAnalyzer(Board board) implements HabitatAnalyzer {


	/**
	 * Creates an Hexagonal Anlayzer
	 */
	public HexagoHabitatAnalyzer{
		Objects.requireNonNull(board); 
	}
	/**
	 * Explores a connected set of tiles on the hexagonal board with the specified habitat,
	 * starting at the given row and column. The exploration uses a breadth-first search (BFS)
	 * algorithm to calculate the size of the connected region.
	 *
	 * @param row     The starting row index of the exploration.
	 * @param col     The starting column index of the exploration.
	 * @param habitat The habitat type to search for. Must not be null.
	 * @param visited A 2D array indicating which tiles have already been visited. Must not be null.
	 * @return The size of the connected set of tiles that share the specified habitat.
	 */
	@Override
	public int exploreSet(int row, int col, Habitat habitat, boolean[][] visited) {
		Objects.requireNonNull(habitat); 
		Objects.requireNonNull(visited); 
		int size = 0;
		Queue<Position> queue = new LinkedList<>();
		queue.add(new Position(col, row));
		visited[row][col] = true;
		while (!queue.isEmpty()) {
			Position current = queue.poll();
			size++;
			Tile currentTile = board.getGrid().get(current.y()).get(current.x());
			List<Habitat> currentTileSides = currentTile.getRotatedHabitats();
			List<Position> neighbors = Board.getHexagonalNeighbors(current);
			for (int side = 0; side < neighbors.size(); side++) {
				Position neighbor = neighbors.get(side);
				if (isValidNeighbor(current, neighbor, side, habitat, visited, currentTileSides)) {
					visited[neighbor.y()][neighbor.x()] = true;
					queue.add(neighbor);
				}}}
		return size;	}


	/**
	 * Checks if two habitats are adjacent to each other on the board.
	 * The adjacency can occur either within the same tile or between neighboring tiles.
	 *
	 * @param board    Board we searching in
	 * @param habitat1 the first habitat
	 * @param habitat2 the second habitat that we look if it is adjacent to the first one 
	 * @return {@code true} if the two habitats are adjacent, {@code false} otherwise.
	 */
	public  boolean twoAdjacentHabitats(Board board, Habitat habitat1, Habitat habitat2) {
		Objects.requireNonNull(board); 
		Objects.requireNonNull(habitat1); 
		Objects.requireNonNull(habitat2); 
		var grid = board.getGrid();
		for (int row = 0; row < grid.size(); row++) {
			for (int col = 0; col < grid.get(row).size(); col++) {
				Tile currentTile = grid.get(row).get(col);
				if (currentTile == null)  continue ; 
				if (currentTile.getHabitats().contains(habitat1) && currentTile.getHabitats().contains(habitat2))      return true;
				List<Position> neighbors = Board.getHexagonalNeighbors(new Position(col, row));
				for (int side = 0; side < neighbors.size(); side++) {
					Position neighbor = neighbors.get(side);
					if (!board.isInBounds(neighbor)) continue;
					Tile neighborTile = grid.get(neighbor.y()).get(neighbor.x());
					if (neighborTile == null)   continue;
					List<Habitat> currentTileSides = currentTile.getHabitats();
					List<Habitat> neighborTileSides = neighborTile.getHabitats();
					int[] indices = calculateSideIndices(col, row, neighbor.x(), neighbor.y());
					int currentSideIndex = indices[0];
					int neighborSideIndex = indices[1];
					if ((currentTileSides.get(currentSideIndex) == habitat1 &&   neighborTileSides.get(neighborSideIndex) == habitat2)|| (currentTileSides.get(currentSideIndex) == habitat2 &&  neighborTileSides.get(neighborSideIndex) == habitat1)) return true;}}} 
		return false;}




	private boolean isValidNeighbor(Position current, Position neighbor, int side, Habitat habitat, boolean[][] visited, List<Habitat> currentTileSides) {
		if (!board.isInBounds(neighbor) || visited[neighbor.y()][neighbor.x()]) {
			return false;
		}
		Tile neighborTile = board.getGrid().get(neighbor.y()).get(neighbor.x());
		if (neighborTile == null) {
			return false;
		}
		List<Habitat> neighborTileSides = neighborTile.getRotatedHabitats();
		int[] indices = calculateSideIndices(current.x(), current.y(), neighbor.x(), neighbor.y());
		int currentSideIndex = indices[0];
		int neighborSideIndex = indices[1];
		return currentTileSides.get(currentSideIndex) == habitat &&
				neighborTileSides.get(neighborSideIndex) == habitat;
	}


	private int[] calculateSideIndices(int currentX, int currentY, int neighborX, int neighborY) {
		int dx = neighborX - currentX;
		int dy = neighborY - currentY;
		boolean isEvenRow = (currentY % 2 == 0); // ça dépend de la la ligne si paire ou impaire 
		if (dx == -1 && dy == 0)  return new int[]{1, 0};
		if (dx == 1 && dy == 0)  return new int[]{4, 1};
		if (isEvenRow) {
			if (dx == -1 && dy == -1) return new int[]{2, 5};
			if (dx == -1 && dy == 1)  return new int[]{0, 3};
			if (dx == 0 && dy == -1)  return new int[]{3, 0};
			if (dx == 0 && dy == 1)    return new int[]{5, 2};
		} else {
			if (dx == 1 && dy == -1) return new int[]{3, 0};
			if (dx == 1 && dy == 1)  return new int[]{5, 2};
			if (dx == 0 && dy == -1) return new int[]{5, 2};
			if (dx == 0 && dy == 1) return new int[]{0, 3};
		}
		throw new IllegalArgumentException("Invalid neighbor coordinates! Current: (" + currentX + ", " + currentY + "), Neighbor: (" + neighborX + ", " + neighborY + ")");
	}


}
