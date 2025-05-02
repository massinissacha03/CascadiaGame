package fr.uge.cascadia.score;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Habitat;
import fr.uge.cascadia.tile.TileType;

import java.util.*;

/**
 * The Score class manages the calculation of points for animals, habitats, and bonuses.
 *
 * @author massinissa
 */
public class Score {
	private final Board board;
	private ScoringStrategy scoringStrategy;

	private final Map<Habitat, Integer> habitatScores = new EnumMap<>(Habitat.class);
	private final Map<Animal, Integer> animalScores = new EnumMap<>(Animal.class);
	private int bonusPoints = 0;




	/**
	 * Initializes a Score object for a given board.
	 *
	 * @param board The game board. Cannot be null.
	 */
	public Score(Board board) {
		this.board = Objects.requireNonNull(board, "Le plateau ne peut pas être null.");
	}




	/**
	 * Sets the scoring strategy for calculating animal points.
	 *
	 * @param scoringStrategy The scoring strategy. Cannot be null.
	 * @throws NullPointerException If the strategy is null.
	 */
	public void setScoringStrategy(ScoringStrategy scoringStrategy) {
		this.scoringStrategy = Objects.requireNonNull(scoringStrategy);
	}

	/**
	 * Calculates scores for all animals using the defined strategy.
	 *
	 */
	public void calculateAnimalScores() {
		if (scoringStrategy == null) {
			throw new IllegalStateException("La stratégie de scoring doit être définie avant de calculer les points.");
		}

		animalScores.clear();
		for (Animal animal : Animal.values()) {
			int points = scoringStrategy.calculateScore(board, animal);
			animalScores.put(animal, points);
		}
	}



	/**
	 * Calculates scores for habitats based on the largest connected area.
	 */
	public void calculateHabitatScores() {
		habitatScores.clear();
		HabitatAnalyzer analyzer = board.getType() == TileType.Hexagonal
				? new HexagoHabitatAnalyzer(board)
						: new SquareHabitatAnalyzer(board);

		Map<Habitat, Integer> calculatedScores = analyzer.calculateHabitatScores(board);
		habitatScores.putAll(calculatedScores);
	}


	/**
	 * Calculates bonus points for habitats based on majorities.
	 *
	 * @param players The list of players. Cannot be null.
	 * @throws NullPointerException If the player list is null.
	 */
	public static void calculateBonusPoints(List<Player> players) {
		Objects.requireNonNull(players);

		if (players.size() == 1) {
			calculateSoloBonus(players.get(0));
		} else {
			for (Habitat habitat : Habitat.values()) {
				calculateHabitatBonus(players, habitat);
			}
		}
	}

	private static void calculateSoloBonus(Player player) {
		int bonus = 0;

		for (Map.Entry<Habitat, Integer> entry : player.getScore().getHabitatScores().entrySet()) {
			if (entry.getValue() >= 7) {
				bonus += 2;
			}
		}

		player.getScore().addBonusPoints(bonus);
	}

	private static void calculateHabitatBonus(List<Player> players, Habitat habitat) {
		int maxScore = players.stream()
				.mapToInt(player -> player.getScore().getHabitatScores().getOrDefault(habitat, 0))
				.max()
				.orElse(0);

		List<Player> firstPlacePlayers = players.stream()
				.filter(player -> player.getScore().getHabitatScores().getOrDefault(habitat, 0) == maxScore)
				.toList();

		if (players.size() == 2) {
			assignTwoPlayerBonus(firstPlacePlayers);
		} else if (players.size() >= 3) {
			assignMultiPlayerBonus(players, firstPlacePlayers, habitat);
		}
	}

	private static void assignTwoPlayerBonus(List<Player> firstPlacePlayers) {
		if (firstPlacePlayers.size() > 1) {
			firstPlacePlayers.forEach(player -> {
				player.getScore().addBonusPoints(1);
				player.addMajorityHabitat();
			}); // Égalité : 1 point chacun.
		} else {
			Player winner = firstPlacePlayers.get(0);
			winner.getScore().addBonusPoints(2); // Seul gagnant : 2 points.
			winner.addMajorityHabitat();
		}
	}



	private static void assignMultiPlayerBonus(List<Player> players, List<Player> firstPlacePlayers, Habitat habitat) {
		int maxScore = players.stream().mapToInt(player -> player.getScore().getHabitatScores().getOrDefault(habitat, 0)).max().orElse(0);
		int secondMaxScore = players.stream()
				.mapToInt(player -> player.getScore().getHabitatScores().getOrDefault(habitat, 0))
				.filter(score -> score < maxScore).max()
				.orElse(0);
		List<Player> secondPlacePlayers = players.stream()
				.filter(player -> player.getScore().getHabitatScores().getOrDefault(habitat, 0) == secondMaxScore)
				.toList();
		if (firstPlacePlayers.size() > 1) {
			firstPlacePlayers.forEach(player -> {
				player.getScore().addBonusPoints(2); // Égalité : 2 points chacun.
				player.addMajorityHabitat();   });
		} else {
			Player winner = firstPlacePlayers.get(0);
			winner.getScore().addBonusPoints(3); // Seul gagnant : 3 points.
			winner.addMajorityHabitat(); }
		if (firstPlacePlayers.size() == 1 && secondPlacePlayers.size() == 1) {
			secondPlacePlayers.get(0).getScore().addBonusPoints(1); // Deuxième place : 1 point.
		}
	}




	/**
	 * adds points to the bonus 
	 * @param points the points to add to the bonus
	 */
	public void addBonusPoints(int points) {
		bonusPoints += points;
	}




	/**
	 * Getter of the total points (score) 
	 * @return the total points
	 */
	public int getTotalPoints() {

		return animalScores.values().stream().mapToInt(Integer::intValue).sum()
				+ habitatScores.values().stream().mapToInt(Integer::intValue).sum()
				+ bonusPoints
				+ board.getNatureTokens(); // Jetons nature non utilisés.
	}
	/**
	 * Getter of the score of habitats only  
	 * @return a map of habitats as a key and points as values
	 */
	public Map<Habitat, Integer> getHabitatScores() {
		return habitatScores;
	}

	
	
	/**
	 * Getter of the score of animals only 
	 * @return a map of habitats as a key and points as values
	 */
	public Map<Animal, Integer> getAnimalScores() {
		return animalScores;
	}


	/**
	 * Getter of the bonus in the score  only 
	 * @return bonus points
	 */
	public int getBonusPoints() {
		return bonusPoints;
	}



	/**
	 * used to print the score 
	 * @return  a string of score details
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n === Score Détail ===\n");
		sb.append("Scores des animaux :\n");
		animalScores.forEach((animal, score) -> 
		sb.append(" - ").append(animal).append(" : ").append(score).append(" points\n"));

		sb.append("\nScores des habitats :\n");
		habitatScores.forEach((habitat, score) -> 
		sb.append(" - ").append(habitat).append(" : ").append(score).append(" points\n"));

		// Points de bonus
		sb.append("\nPoints de bonus : ").append(bonusPoints).append(" points\n");

		// Jetons nature non utilisés
		sb.append("Jetons nature non utilisés : ").append(board.getNatureTokens()).append(" points\n");

		// Total
		sb.append("\nScore Total : ").append(getTotalPoints()).append(" points\n");

		return sb.toString();
	}





}
