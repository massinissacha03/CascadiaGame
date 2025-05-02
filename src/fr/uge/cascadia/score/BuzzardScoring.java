package fr.uge.cascadia.score;

import java.util.Objects;

import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Tile;

/**
 * Represents the scoring logic for the Buzzard animal in the game.
 * Different scoring rules apply based on the card type A,B,C,D
 *
 * @param cardType the faunaCard A B C D chosen to calculate the score 
 *  */
public  record  BuzzardScoring(CardType cardType) implements  ScoringCard {

	
	
	
	/**
	 * Creates a Buzzard Scoring card
	 */
	public BuzzardScoring{
		Objects.requireNonNull(cardType); 
	}



	/**
	 * Calculates the score for the Buzzard card based on the given board and card type.
	 * 
	 * @param board The board used for scoring.
	 * @return The calculated score.
	 */

	@Override 
	public int calculate(Board board) {
		Objects.requireNonNull(board); 
		return switch (cardType) {
		case A -> calculateIsolatedBuzzardsScoreA(board);
		case B -> calculateLineOfSightScoreB(board);
		case C -> calculateLinePointsScoreC(board);
		case D -> calculatePairsWithAnimalsScoreD(board);
		default ->  calculateIsolatedBuzzardsScoreA(board);
		};
	}




	private int calculateIsolatedBuzzardsScoreA(Board board) {
		int score = 0;
		int nbBuzzardIzolated = 0;
		var grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Buzzard) {
					if (isIsolatedBuzzard(x, y, visited, board)) {
						nbBuzzardIzolated += 1;
					}
				}
			}
		}
		score = assignGroupBuzzardPointsA(nbBuzzardIzolated);
		return score;}


	private boolean isIsolatedBuzzard(int x, int y, boolean[][] visited, Board board) {
		Position point = new Position(x,y) ;
		var grid = board.getGrid();
		var neighbors = board.getNeighbors(point);
		for (var neighbor : neighbors) {
			Tile tile = grid.get(neighbor.y()).get(neighbor.x());
			if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Buzzard) {
				return false;
			}
		}
		return true;
	}

	private int assignGroupBuzzardPointsA(int groupSize) {
		return switch (groupSize) {
		case 1 -> 2;
		case 2 -> 5;
		case 3 -> 8 ; 
		case 4 -> 11 ; 
		case 5 -> 14 ; 
		case 6 -> 18 ;
		case 7 -> 22 ;
		default-> groupSize >= 8 ? 28 : 0 ; 
		};
	}


	private int calculateLineOfSightScoreB(Board board) {
		int score = 0;
		int nbBuzzardIzolated = 0;
		var grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Buzzard) {
					if (hasNeighborInLineOfSight(x, y, board)) {
						nbBuzzardIzolated += 1; // Chaque buse avec au moins une voisine rapporte 3 points
					}
				}
			}
		}
		score = assignGroupBuzzardPointsB(nbBuzzardIzolated);
		return score;}

	private boolean hasNeighborInLineOfSight(int x, int y, Board board) {
		var grid = board.getGrid();
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;
				if (dx != 0 && dy != 0) continue; // Ignorer les diagonales

				int nx = x + dx;
				int ny = y + dy;

				while (ny >= 0 && ny < grid.size() && nx >= 0 && nx < grid.get(ny).size()) {
					Tile neighbor = grid.get(ny).get(nx);
					if (neighbor != null && neighbor.hasAnimalToken() && neighbor.getAssignedAnimalToken().animal() == Animal.Buzzard) {
						return true;
					}
					nx += dx;
					ny += dy;                }            }        }
		return false;
	}


	private int assignGroupBuzzardPointsB(int groupSize) {
		return switch (groupSize) {
		case 2 -> 5;
		case 3 -> 9;
		case 4 -> 12 ;  
		case 5 -> 16 ; 
		case 6 -> 20 ;
		case 7 -> 24 ;
		default-> groupSize >= 8 ? 28 : 0 ; 
		};
	}



	private int calculateLinePointsScoreC(Board board) {
		int score = 0;
		var grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];

		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}

		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Buzzard) {

					score += countLinesOfSight(x, y, board, visited) * 3;
				}
			}
		}

		return score;
	}


	private int countLinesOfSight(int x, int y, Board board, boolean[][] visited) {
		int count = 0;
		var grid = board.getGrid();
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;
				if (dx != 0 && dy != 0) continue; // Ignorer les diagonales
				int nx = x + dx;
				int ny = y + dy;
				while (ny >= 0 && ny < grid.size() && nx >= 0 && nx < grid.get(ny).size()) {
					Tile neighbor = grid.get(ny).get(nx);
					if (!visited[ny][nx] && neighbor != null && neighbor.hasAnimalToken() && neighbor.getAssignedAnimalToken().animal() == Animal.Buzzard) {
						visited[ny][nx] = true;
						count++;
						break;
					}
					nx += dx;
					ny += dy;                }           }
		}
		return count;    }




	private int calculatePairsWithAnimalsScoreD(Board board) {
		int score = 0;
		int nbAnimalDiversity = 0;
		var grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];  }
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Buzzard) {
					nbAnimalDiversity += countPairsWithAnimalDiversity(x, y, board, visited);
				}
			}
		}
		score = assignGroupBuzzardPointsD(nbAnimalDiversity);
		return score;
	}

	private int countPairsWithAnimalDiversity(int x, int y, Board board, boolean[][] visited) {
		int score = 0;
		var grid = board.getGrid();
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;
				if (dx != 0 && dy != 0) continue; // Ignorer les diagonales
				int nx = x + dx;
				int ny = y + dy;
				int diversity = 0;
				while (ny >= 0 && ny < grid.size() && nx >= 0 && nx < grid.get(ny).size()) {
					Tile neighbor = grid.get(ny).get(nx);
					if (!visited[ny][nx] && neighbor != null && neighbor.hasAnimalToken()) {
						visited[ny][nx] = true;
						if (neighbor.getAssignedAnimalToken().animal() == Animal.Buzzard) {
							score += diversity;
							break;
						} else  diversity++;}
					nx += dx;
					ny += dy;	}}  }
		return score;    }

	private int assignGroupBuzzardPointsD(int groupSize) {
		return switch (groupSize) {
		case 1 -> 4;
		case 2 -> 5;
		default-> groupSize >= 3 ? 9 : 0 ; 
		};
	}


}