package fr.uge.cascadia.score;

import java.util.Objects;

import fr.uge.cascadia.Position;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Habitat;
import fr.uge.cascadia.tile.Tile;

/**
 * Implementation of HabitatAnalyzer for square tiles board
 * This class provides methods to analyze habitats and their adjacency on square boards.
 * 
 * 
 * @param board , the square tiles board that is being analyzed
 */

public record SquareHabitatAnalyzer(Board board) implements HabitatAnalyzer {
	
	
	
	
	
	
	 /**
     * Explores a connected set of tiles containing a specific habitat starting at a given position.
     *
     * @param row      The row of the starting position.
     * @param col      The column of the starting position.
     * @param habitat  The habitat to explore.
     * @param visited  A boolean 2D array tracking visited positions.
     * @return The size of the connected set of tiles containing the habitat.
     */
	@Override
	public int exploreSet(int row, int col, Habitat habitat, boolean[][] visited) {
		if (!board.isInBounds( new Position(row, col)) || visited[row][col]) {
			return 0;
		}
		Tile tile = board.getGrid().get(row).get(col);
		if (tile == null || !tile.getHabitats().contains(habitat)) {
			return 0;
		}
		visited[row][col] = true;
		int size = 1;
		for (Position neighbor :Board.getSquareNeighbors(new Position(col, row))) {
			size += exploreSet(neighbor.y(), neighbor.x(), habitat, visited);
		}
		return size;
	}





    /**
     * Checks if two habitats are adjacent to each other on the board.
     *
 	 * @param board    Board we searching in
	 * @param habitat1 the first habitat
	 * @param habitat2 the second habitat that we look if it is adjacent to the first one 
	 * @return {@code true} if the two habitats are adjacent, {@code false} otherwise.
	 */
	public boolean twoAdjacentHabitats(Board board ,  Habitat habitat1, Habitat habitat2) {
		Objects.requireNonNull(board); 
		Objects.requireNonNull(habitat1); 
		Objects.requireNonNull(habitat2); 
		var grid = board.getGrid();
		for (int row = 0; row < grid.size(); row++) {
			for (int col = 0; col < grid.get(row).size(); col++) {
				Tile currentTile = grid.get(row).get(col);
				if (currentTile == null)  continue;                    
				if (currentTile.getHabitats().contains(habitat1) && currentTile.getHabitats().contains(habitat2)) return true;
				for (Position neighbor : Board.getSquareNeighbors(new Position(col, row))) {
					if (!board.isInBounds(new Position(neighbor.y(), neighbor.x()))) continue;
					Tile neighborTile = grid.get(neighbor.y()).get(neighbor.x());
					if (neighborTile == null) continue;
					if (currentTile.getHabitats().contains(habitat1) && neighborTile.getHabitats().contains(habitat2)) {
						return true;
					}
					if (currentTile.getHabitats().contains(habitat2) &&neighborTile.getHabitats().contains(habitat1)) return true;		
				}}	}
		return false;
	}}
