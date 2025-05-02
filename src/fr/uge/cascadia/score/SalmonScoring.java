package fr.uge.cascadia.score;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Tile;

import java.util.ArrayList;

import java.util.Objects;
/**
 * Represents the scoring logic for the Salmon animal in the game.
 * Different scoring rules apply based on the card type A,B,C,D
 *
 * @param cardType the faunaCard A B C D chosen to calculate the score 
 */
public record SalmonScoring ( CardType cardType)  implements ScoringCard {

	
	

	/**
	 * Creates a Fox Scoring Card
	 */
	public SalmonScoring{
		Objects.requireNonNull(cardType) ; 
	}


	
	
	/**
	 * Calculates the score for the salmon card based on the given board and card type.
	 * 
	 * @param board The board used for scoring.
	 * @return The calculated score.
	 */
	
	@Override 
	public int calculate(Board board) {
		Objects.requireNonNull(board); 
		return switch (cardType) {
		case A -> calculateSizeBasedScoreA(board);
		case B -> calculateSizeBasedScoreB(board);
		case C -> calculateSizeWithBonusScoreC(board);
		case D -> calculateLineWithAdjacentBonusD(board);
		default ->  calculateSizeBasedScoreA(board);
		};
	}




	private int calculateSizeBasedScoreA(Board board) {

		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Salmon) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Salmon, visited);
					score += assignGroupSalamonPointsA(groupSize);
				}
			}
		}
		return score;
	}




	private int assignGroupSalamonPointsA(int groupSize) {
		return switch (groupSize) {
		case 1 -> 2;
		case 2 -> 5;
		case 3 -> 8 ; 
		case 4 -> 12 ; 
		case 5 -> 16 ; 
		case 6 -> 20 ; 
		default-> groupSize >= 7 ? 25 : 0 ; 
		};
	}


	private int calculateSizeBasedScoreB(Board board) {
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];       }
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Salmon) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Salmon, visited);
					score += assignGroupSalamonPointsB(groupSize);
				}
			}
		}
		return score;
	}

	private int assignGroupSalamonPointsB(int groupSize) {
		return switch (groupSize) {
		case 1 -> 2;
		case 2 -> 4;
		case 3 -> 9 ; 
		case 4 -> 11 ; 
		default-> groupSize >= 5 ? 17 : 0 ; 
		};
	}


	private int calculateSizeWithBonusScoreC(Board board) {
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Salmon) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Salmon, visited);
					score += assignGroupSalamonPointsC(groupSize);
				}
			}
		}
		return score;
	}

	private int assignGroupSalamonPointsC(int groupSize) {
		return switch (groupSize) {
		case 3 -> 10;
		case 2 -> 12;
		default-> groupSize >= 5 && groupSize < 3 ? 15 : 0 ; 
		};
	}


	private int calculateLineWithAdjacentBonusD(Board board) {
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];        }
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Salmon) {
					score++;
					visited[y][x] = false;
					if(grid.get(y).get(x+1) != null && grid.get(y).get(x+1).hasAnimalToken() && grid.get(y).get(x+1).getAssignedAnimalToken().animal() != Animal.Salmon  ) {
						score++;	}
					if(grid.get(y+1).get(x)!= null && grid.get(y+1).get(x).hasAnimalToken() && grid.get(y+1).get(x).getAssignedAnimalToken().animal() != Animal.Salmon  ) {	score++;	}
					if(grid.get(y).get(x-1) != null && grid.get(y).get(x-1).hasAnimalToken() && grid.get(y).get(x-1).getAssignedAnimalToken().animal() != Animal.Salmon  ) {
						score++;	}
					if(grid.get(y-1).get(x) != null && grid.get(y-1).get(x).hasAnimalToken() && grid.get(y-1).get(x).getAssignedAnimalToken().animal() != Animal.Salmon  ) {
						score++;
					}} }        }        
		return score;}





}