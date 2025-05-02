package fr.uge.cascadia.score;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Habitat;
import fr.uge.cascadia.tile.Tile;
/**
 * An HabitatAnalyzer interface that provides methods for analyzing habitats on a game board.
 * It is implemented by habitat-specific analyzers such as HexagoHabitatAnalyzer and SquareHabitatAnalyzer.
 */
public sealed interface HabitatAnalyzer permits HexagoHabitatAnalyzer, SquareHabitatAnalyzer {




	/**
	 * Explores a connected set of tiles on the board with the given habitat, starting at the specified row and column.
	 * This method is implemented differently based on the specific board hexagonal or square.
	 *
	 * @param row     The starting row.
	 * @param col     The starting column.
	 * @param habitat The habitat type to search for.
	 * @param visited A 2D array marking visited tiles.
	 * @return The size of the connected set of tiles.
	 */
	public int exploreSet(int row, int col, Habitat habitat, boolean[][] visited);




	/**
	 * Calculates the largest connected sets for all habitats on the given board.
	 * This method is shared across implementations and does not depend on the board shape.
	 *
	 * @param board board to be analyzed
	 * @return A map associating each habitat with its largest connected set size.
	 */
	public default Map<Habitat, Integer> calculateHabitatScores(Board board) {
		Objects.requireNonNull(board); 

		Map<Habitat, Integer> habitatScores = new HashMap<>();
		int rows = board.getGrid().size();
		int cols = board.getGrid().get(0).size();
		for (Habitat habitat : Habitat.values()) {
			boolean[][] visited = new boolean[rows][cols];
			int largestSetSize = findLargestSet(board , habitat, visited);
			habitatScores.put(habitat, largestSetSize);
		}
		return habitatScores;
	}




	private int findLargestSet(Board board , Habitat habitat, boolean[][] visited) {


		int largestSet = 0;

		for (int row = 0; row < board.getGrid().size(); row++) {
			for (int col = 0; col < board.getGrid().get(row).size(); col++) {
				if (!visited[row][col] && isTileWithHabitat(board , row, col, habitat)) {
					largestSet = Math.max(largestSet, exploreSet(row, col, habitat, visited));
				}
			}
		}

		return largestSet;
	}


	private boolean isTileWithHabitat(Board board , int row, int col, Habitat habitat) {
		Objects.requireNonNull(board); 
		Tile tile = board.getGrid().get(row).get(col);
		return tile != null && tile.getHabitats().contains(habitat); 


	}





}
