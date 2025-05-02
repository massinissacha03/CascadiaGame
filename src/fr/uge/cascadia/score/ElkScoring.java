package fr.uge.cascadia.score;

import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Tile;
import fr.uge.cascadia.tile.TileType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;



/**
 * Represents the scoring logic for the Elk animal in the game.
 * Different scoring rules apply based on the card type A,B,C,D
 *
 * @param cardType the faunaCard A B C D chosen to calculate the score 
 */
public record ElkScoring(CardType cardType) implements ScoringCard {

	/**
	 * Creates an Elk Scoring Card
	 */
	public ElkScoring {
		 Objects.requireNonNull(cardType) ; 
	}
	
	
	
	/**
	 * Calculates the score for the elk card based on the given board and card type.
	 * 
	 * @param board The board used for scoring.
	 * @return The calculated score.
	 */

	@Override 
	public int calculate(Board board) {
		return switch (cardType) {
		case A -> calculateLineScore(board  );
		case B -> calculateFormationScore(board);
		case C -> calculateGroupSizeScore(board);
		case D ->calculateCircleScore (board);
		};
	}
	private int calculateCircleScore(Board board) {
		int totalScore = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][grid.get(0).size()];
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				if (!visited[y][x] && isAnimalTile(grid, x, y, Animal.Elk)) {
					List<Position> group = exploreConnectedGroup(grid, visited, x, y, Animal.Elk, board);
					if (isCircularGroup(group, board)) {
						int groupSize = group.size();
						int groupScore = assignCirclePoints(groupSize);
						totalScore += groupScore;
					}	            }	        }	    }
		return totalScore;
	}

	
	
	
	
	/**
	 * Vérifie si un groupe de tuiles forme un cercle ou semi-cercle valide.
	 */
	private boolean isCircularGroup(List<Position> group, Board board) {
		if (group.size() < 4) 			return false;

		for (Position point : group) {
			int neighborsInGroup = 0;
			for (Position neighbor : board.getNeighbors(point)) {
				if (group.contains(neighbor)) {
					neighborsInGroup++;
				}
			}
			if (neighborsInGroup < 2) {
				return false;
			}
		}
		return group.size() <= 6; // Maximum pour un cercle complet
	}

	/**
	 * Explore les tuiles connectées à partir d'un point donné.
	 */
	private List<Position> exploreConnectedGroup(ArrayList<ArrayList<Tile>> grid, boolean[][] visited, int startX, int startY, Animal animal, Board board) {
		List<Position> group = new ArrayList<>();
		Deque<Position> stack = new ArrayDeque<>();
		stack.push(new Position(startX, startY));
		while (!stack.isEmpty()) {
			Position current = stack.pop();
			if (!isValidPosition(grid, current.x(), current.y()) || visited[current.y()][current.x()]) {
				continue;}
			Tile tile = grid.get(current.y()).get(current.x());
			if (tile == null || !tile.hasAnimalToken() || tile.getAssignedAnimalToken().animal() != animal) {
				continue;			}
			visited[current.y()][current.x()] = true;
			group.add(current);
			// Ajouter les voisins à la pile
			for (Position neighbor : board.getNeighbors(current)) {
				if (isValidPosition(grid, neighbor.x(), neighbor.y()) && !visited[neighbor.y()][neighbor.x()]) {
					stack.push(neighbor);
				}}}
		return group;}

	
	
	/**
	 * Attribue des points en fonction de la taille du cercle.
	 */
	private int assignCirclePoints(int groupSize) {
		return switch (groupSize) {
		case 1 -> 2;
		case 2 -> 5;
		case 3 -> 8;
		case 4 -> 12;
		case 5 -> 16;
		case 6 -> 21;
		default -> 0;
		};
	}



	private int calculateGroupSizeScore(Board board) {
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][];
		for (int i = 0; i < grid.size(); i++) {
			visited[i] = new boolean[grid.get(i).size()];
		}
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Elk) {
					int groupSize = board.searchBiggestGroup(x, y, Animal.Elk, visited);
					score += assignGroupElkPoints(groupSize);
				}
			}
		}

		return score;
	}


	private int assignGroupElkPoints(int groupSize) {
		return switch (groupSize) {
		case 1 -> 2;
		case 2 -> 4;
		case 3 -> 7 ; 
		case 4 -> 10 ; 
		case 5 -> 14 ; 
		case 6 -> 18 ; 
		case 7 -> 23 ; 
		default-> groupSize >= 8 ? 28 : 0 ; 
		};
	}








	/*________________carte B_______________________*/

	private int calculateFormationScore(Board board) {
		int totalScore = 0;
		// Obtenir la grille
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		boolean[][] visited = new boolean[grid.size()][grid.get(0).size()];
		// Parcourir chaque tuile
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				if (!visited[y][x] && isAnimalTile(grid, x, y, Animal.Elk)) {
					// Vérifier les groupes possibles à partir de cette tuile
					int groupSize = exploreGroup(grid, visited, x, y, Animal.Elk, board.getType());
					totalScore += assignGroupScore(groupSize);
					System.out.println("Groupe détecté : Taille " + groupSize);
				}
			}
		}
		return totalScore;
	}
	private int exploreGroup(ArrayList<ArrayList<Tile>> grid, boolean[][] visited, int x, int y, Animal animal, TileType type) {
		List<Position> group = new ArrayList<>();
		exploreRecursive(grid, visited, x, y, animal, group, type);
		return group.size();
	}

	private void exploreRecursive(ArrayList<ArrayList<Tile>> grid, boolean[][] visited, int x, int y, Animal animal, List<Position> group, TileType type) {
		if (!isValidPosition(grid, x, y) || visited[y][x] || !isAnimalTile(grid, x, y, animal)) {
			return;
		}
		visited[y][x] = true;
		group.add(new Position(x, y));

		// Obtenir les voisins (hexagonal ou carré)
		List<Position> neighbors = type == TileType.Hexagonal
				? Board.getHexagonalNeighbors(new Position(x, y))
						: Board.getSquareNeighbors(new Position(x, y));
		for (Position neighbor : neighbors) {
			exploreRecursive(grid, visited, neighbor.x(), neighbor.y(), animal, group, type);
		}
	}
	private int assignGroupScore(int groupSize) {
		return switch (groupSize) {
		case 1 -> 2;
		case 2 -> 5;
		case 3 -> 9;
		case 4 -> 13;
		default -> 0;
		};
	}




	/*______________________________carte A_____________________________ */
	private int calculateLineScore(Board board) {
		int totalScore = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		TileType type = board.getType();
		boolean[][] visited = new boolean[grid.size()][grid.get(0).size()];
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				if (!visited[y][x] && isAnimalTile(grid, x, y, Animal.Elk)) {
					int maxLineLength = exploreLineInAllDirections(grid, visited, x, y, type, Animal.Elk);
					if (maxLineLength > 0) {
						totalScore += assignLineScore(maxLineLength);
					}
				}
			}
		}
		return totalScore;
	}

	/**
	 * Explore la plus longue ligne à partir d'une tuile dans toutes les directions.
	 */
	private int exploreLineInAllDirections(ArrayList<ArrayList<Tile>> grid, boolean[][] visited, int startX, int startY, TileType type, Animal animal) {
		int maxLineLength = 0;
		for (Direction direction : Direction.values()) {
			int lineLength = exploreSingleDirection(grid, visited, startX, startY, direction, type, animal);
			if (lineLength > maxLineLength) {
				maxLineLength = lineLength;
			}
		}
		if (maxLineLength > 0) {
			markLongestLineAsVisited(grid, visited, startX, startY, type, animal, maxLineLength);
		}
		return maxLineLength;
	}

	/**
	 * Explore une ligne dans une direction donnée et retourne sa longueur.
	 */
	private int exploreSingleDirection(ArrayList<ArrayList<Tile>> grid, boolean[][] visited, int startX, int startY, Direction direction, TileType type, Animal animal) {
		int x = startX;
		int y = startY;
		int lineLength = 0;
		while (isValidPosition(grid, x, y) && isAnimalTile(grid, x, y, animal) && !visited[y][x]) {
			lineLength++;
			switch (direction) {
			case VERTICAL -> y++;
			case HORIZONTAL -> x++;
			case DIAGONAL_TOP_RIGHT -> {
				if (type == TileType.Hexagonal && y % 2 == 0) {
					x--;}
				y--;}
			case DIAGONAL_TOP_LEFT -> {
				if (type == TileType.Hexagonal && y % 2 != 0) {
					x++;
				}
				y--;
			}	}}
		return lineLength;
	}












	/**
	 * Marque toutes les tuiles d'une ligne comme visitées.
	 */
	private void markLongestLineAsVisited(ArrayList<ArrayList<Tile>> grid, boolean[][] visited, int startX, int startY, TileType type, Animal animal, int lineLength) {
		int x = startX;
		int y = startY;

		for (int i = 0; i < lineLength; i++) {
			if (isValidPosition(grid, x, y)) {
				visited[y][x] = true;
			}

			x++;
		}
	}

	/**
	 * Vérifie si une tuile contient l'animal spécifié.
	 */
	private boolean isAnimalTile(ArrayList<ArrayList<Tile>> grid, int x, int y, Animal animal) {
		if (!isValidPosition(grid, x, y)) {
			return false;
		}

		Tile tile = grid.get(y).get(x);
		return tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == animal;
	}

	/**
	 * Vérifie si une position est valide dans la grille.
	 */
	private boolean isValidPosition(ArrayList<ArrayList<Tile>> grid, int x, int y) {
		return y >= 0 && y < grid.size() && x >= 0 && x < grid.get(y).size();
	}

	/**
	 * Enumération des directions.
	 */
	private enum Direction {
		VERTICAL,
		HORIZONTAL,
		DIAGONAL_TOP_RIGHT,
		DIAGONAL_TOP_LEFT
	}

	/**
	 * Attribue un score en fonction de la longueur d'une ligne.
	 */
	private int assignLineScore(int lineLength) {
		System.out.println("Longueur de ligne : " + lineLength);
		return switch (lineLength) {
		case 1 -> 2;
		case 2 -> 5;
		case 3 -> 9;
		case 4 -> 13;
		default -> 0;
		};
	}






}

