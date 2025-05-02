package fr.uge.cascadia.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Tile;

/**
 * Represents the scoring logic for the FOx animal in the game.
 * Different scoring rules apply based on the card type A,B,C,D
 *
 * @param cardType the faunaCard A B C D chosen to calculate the score 
 */
public record  FoxScoring (CardType cardType) implements ScoringCard  {




	/**
	 * Creates a Fox Scoring Card
	 */
	public FoxScoring {
		Objects.requireNonNull(cardType); 
	}

	
	
	
	/**
	 * Calculates the score for the fox card based on the given board and card type.
	 * 
	 * @param board The board used for scoring.
	 * @return The calculated score.
	 */
	
	@Override
	public int calculate(Board board) {
		Objects.requireNonNull(board); 
		return switch (cardType) {
		case A -> calculateIndividualScore(board);
		case B -> calculatePairScore(board);
		case C -> calculateDominantSpeciesScore(board);
		case D -> calculatePairDominantSpeciesScore(board);
		};
	}



	private int calculateIndividualScore(Board board) {
		int score = 0;
		for (int y = 0; y < board.getGrid().size(); y++) {
			for (int x = 0; x < board.getGrid().get(y).size(); x++) {
				Tile tile = board.getGrid().get(y).get(x);
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Fox) {
					score += countAdjacentSpecies(new Position(x, y), board);
				}
			}
		}
		return score;
	}


	private int countAdjacentSpecies(Position pos, Board board) {
		List<Position> neighbors = board.getNeighbors(pos);
		Set<Animal> uniqueSpecies = new HashSet<>();

		for (Position neighbor : neighbors) {
			Tile tile = board.getGrid().get(neighbor.x()).get(neighbor.y());
			if (tile != null && tile.hasAnimalToken()) {
				uniqueSpecies.add(tile.getAssignedAnimalToken().animal());
			}
		}
		return switch (uniqueSpecies.size()) {
		case 1 -> 3;
		case 2 -> 6;
		case 3 -> 9;
		case 4 -> 12;
		case 5 -> 15;
		default -> uniqueSpecies.size() >= 6 ? 20 : 0;
		};
	}


	
	private int calculatePairScore(Board board) {
		int score = 0;
		int nbPair = 0;
		for (int y = 0; y < board.getGrid().size(); y++) {
			for (int x = 0; x < board.getGrid().get(y).size(); x++) {
				Tile tile = board.getGrid().get(y).get(x);
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Fox) {
					nbPair += countSpeciesPairs(new Position(x, y), board);
				}
			}
		}
		score = assignPairPoints(nbPair);
		return score;
	}

	private int countSpeciesPairs(Position point, Board board) {
		List<Position> neighbors = board.getNeighbors(point);
		Map<Animal, Integer> speciesCount = new HashMap<>();

		for (Position neighbor : neighbors) {
			Tile tile =  board.getGrid().get(neighbor.x()).get(neighbor.y());
			if (tile != null && tile.hasAnimalToken()) {
				speciesCount.merge(tile.getAssignedAnimalToken().animal(), 1, Integer::sum);
			}
		}
		// Retourner le score en fonction du nombre de paires (2 animaux d'une espèce forment une paire)
		int pairs = 0;
		for (int count : speciesCount.values()) {
			pairs += count / 2;
		}
		return pairs ; // Chaque paire rapporte 4 points
	}

	private int assignPairPoints(int pairsCount) {
		return switch (pairsCount) {
		case 1 -> 3;
		case 2 -> 5;
		case 3 -> 7;
		default ->  0;
		};
	}


	private int calculateDominantSpeciesScore(Board board) {
		int score = 0;
		//        int maxcount= 0;
		for (int y = 0; y < board.getGrid().size(); y++) {
			for (int x = 0; x < board.getGrid().get(y).size(); x++) {
				Tile tile = board.getGrid().get(y).get(x);
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Fox) {
					score += countMostFrequentSpecies(new Position(x, y), board);
				}
			}
		}
		return score;
	}


	private int countMostFrequentSpecies(Position point, Board board) {
		List<Position> neighbors = board.getNeighbors(point);
		Map<Animal, Integer> speciesCount = new HashMap<>();

		for (Position neighbor : neighbors) {
			Tile tile =board.getGrid().get(neighbor.x()).get(neighbor.y());
			if (tile != null && tile.hasAnimalToken()) {
				speciesCount.merge(tile.getAssignedAnimalToken().animal(), 1, Integer::sum);
			}
		}

		// Trouver l'espèce la plus présente et retourner le score correspondant
		int maxCount = speciesCount.values().stream().max(Integer::compare).orElse(0);
		return maxCount ; // Chaque animal de l'espèce dominante rapporte 2 points
	}




	private int calculatePairDominantSpeciesScore(Board board) {
		int score = 0;
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		int countPair = 0;
		boolean[][] visited = new boolean[grid.size()][]; // Structure pour marquer les renards visités
		for (int i = 0; i < grid.size(); i++) visited[i] = new boolean[grid.get(i).size()];
		for (int y = 0; y < grid.size(); y++) {
			for (int x = 0; x < grid.get(y).size(); x++) {
				Tile tile = grid.get(y).get(x);
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == Animal.Fox) {
					Position fox1 = new Position(x, y);
					for (Position neighbor : board.getNeighbors(fox1)) {
						Tile neighborTile = grid.get(neighbor.x()).get(neighbor.y());
						if (!visited[neighbor.y()][neighbor.x()] && neighborTile != null && neighborTile.hasAnimalToken() && neighborTile.getAssignedAnimalToken().animal() == Animal.Fox) {
							Position fox2 = neighbor;
							visited[y][x] = true;
							visited[neighbor.y()][neighbor.x()] = true;
							countPair += countSpeciesPairsAroundFoxes(fox1, fox2, board);
							break;}					}				}			}		}
		score = assignPairPointsD(countPair);
		return score;	} 



	private int countSpeciesPairsAroundFoxes(Position fox1, Position fox2, Board board) {
		Map<Animal, Integer> speciesCount = new HashMap<>();
		ArrayList<ArrayList<Tile>> grid = board.getGrid();
		for (Position neighbor: board.getNeighbors(fox1)) {
			if (board.isInBounds(neighbor)) {
				Tile tile = grid.get(neighbor.y()).get(neighbor.x());
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() != Animal.Fox) {
					speciesCount.merge(tile.getAssignedAnimalToken().animal(), 1, Integer::sum);}}		}

		for (Position neighbor : board.getNeighbors(fox2)) {
			if (board.isInBounds(neighbor)) {
				Tile tile = grid.get(neighbor.y()).get(neighbor.x());
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() != Animal.Fox) {
					speciesCount.merge(tile.getAssignedAnimalToken().animal(), 1, Integer::sum);
				}
			}
		}
		int pairs = 0;
		for (int count : speciesCount.values()) pairs += count / 2; 		
		return pairs ; 
	}


	private int assignPairPointsD(int pairsCount) {
		return switch (pairsCount) {
		case 1 -> 5;
		case 2 -> 7;
		case 3 -> 9;
		case 4 -> 11;
		default ->  0;
		};
	}
}