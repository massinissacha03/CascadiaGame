package fr.uge.cascadia.score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Tile;



/**
 * Represents the scoring logic for the Bear animal in the game.
 * Different scoring rules apply based on the card type A,B,C,D
 *
 * @param cardType the faunaCard A B C D chosen to calculate the score 
 *  */
public record  BearScoring (CardType cardType)  implements ScoringCard {


	/**
	 * Creates a Bear Scoring card
	 */
	public BearScoring {
		Objects.requireNonNull(cardType) ;  

	}
	
	

    /**
     * Calculates the score for the Bear based on the board given and the card type.
     * 
     * @param board The board on which the score is calculated.
     * @return The calculated score for the Bear.
     */
	public int calculate(Board board) {
		Objects.requireNonNull(board); 
		return switch (cardType) {
		case A -> calculatePairsScore(board);
		case B -> calculateExactThreeScore(board);
		case C -> calculateMixedGroupScore(board);
		case D -> calculateLargeGroupsScore(board);
		};
	}



	/**
     * Calculates the score based on pairs of bears on the board.
     * @param board
     * 	 */
	private int calculatePairsScore(Board board) {
		Objects.requireNonNull(board); 
		int pairs = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Bear) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Bear, visited);
					if (groupSize ==2) pairs +=1 ; 
				}
			}
		}
		return assignPairPoints(pairs) ; 
	}



	private int assignPairPoints(int pairsCount) {
		return switch (pairsCount) {
		case 1 -> 4;
		case 2 -> 11;
		case 3 -> 19;
		default -> pairsCount > 3 ? 27 : 0;
		};
	}








	/**
     * Calculates the score based on groups of three bears on the board.
     * @param board
     * 	 */


	private int calculateExactThreeScore(Board board) {
		
		int points = 0; 
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];

		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}

		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Bear) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Bear, visited);
					if (groupSize ==3) points += 10 ; 
				}
			}
		}
		return points;
	}

	
	/**
     * Calculates the score depending on the size of bear groups
     * @param board
     * 	 */
	
	private int calculateMixedGroupScore(Board board) {
		Objects.requireNonNull(board); 
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		Set<Integer> groupSizes = new HashSet<>(); 
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Bear) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Bear, visited);
					score += assignBearFamillypPoint(groupSize);
					groupSizes.add(groupSize); }	}}
		if (groupSizes.contains(1) && groupSizes.contains(2) && groupSizes.contains(3)) {
			score += 3; // Bonus de 3 points
		}
		return score;
	}




	/**
     * Calculates the score depending on the size of bear groups
     * @param board
     * 	 */

	private int calculateLargeGroupsScore(Board board) {
		Objects.requireNonNull(board); 
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];

		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Bear) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Bear, visited);
					score += assignBearGroupPoint(groupSize);
				}
			}
		}
		return score;
	}



	private int assignBearFamillypPoint(int groupeSize) {
		return switch (groupeSize) {
		case 1-> 2;
		case 2 -> 5;
		case 3 -> 8;
		default ->  0;
		};
	}

	private int assignBearGroupPoint(int groupeSize) {
		return switch (groupeSize) {
		case 2 -> 5;
		case 3 -> 8;
		case 4 -> 13;
		default ->  0;
		};
	}


}
