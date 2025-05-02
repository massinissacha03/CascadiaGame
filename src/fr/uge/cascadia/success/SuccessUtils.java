package fr.uge.cascadia.success;

import fr.uge.cascadia.*;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.score.HexagoHabitatAnalyzer;
import fr.uge.cascadia.score.SquareHabitatAnalyzer;
import fr.uge.cascadia.tile.Habitat;
import fr.uge.cascadia.tile.TileType;

import java.util.Map;
import java.util.Objects;

/**
 * a class providing helper methods to evaluate player success conditions.
 *
 * This class defines a series of static methods for checking various success criteria,
 * such as achieving minimum scores, specific wildlife or habitat conditions, and other
 * gameplay-related achievements.
 *
 *
 *@author massinissa 
 */
public class SuccessUtils {

	/**
	 * Default constructor for the SuccessUtils class.

	 */
	public SuccessUtils() {
		// Default constructor
	}


	/**
	 * Checks if the player has a score greater than or equal to the specified minimum score.
	 *
	 * @param player   The player whose score is being checked.
	 * @param minScore The minimum required score.
	 * @return true if the player's total score is greater than or equal to the minimum score, false otherwise.
	 */
	public static boolean minimumScore(Player player, int minScore) {
		Objects.requireNonNull(player); 
		return player.getScore().getTotalPoints() >= minScore;
	}


	/**
	 * Checks if the player has no remaining Nature tokens.
	 *
	 * @param player The player to check.
	 * @return true if the player has no Nature tokens left, false otherwise.
	 */
	public static boolean hasNoNatureTokensLeft(Player player) {
		Objects.requireNonNull(player); 
		return player.getBoard().getNatureTokens() == 0; 
	}


	/**
	 * Checks if a player has no instances of the specified animal on their board.
	 *
	 * @param player the player whose board is being checked.
	 * @param animal the type of animal to check for on the player's board.
	 * @return {@code true} if the player has no instances of the specified animal, {@code false} otherwise.
	 */

	public static boolean hasNoAnimal(Player player, Animal animal) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");
		Objects.requireNonNull(animal, "L'animal ne peut pas être null.");
		return player.getBoard().getAnimalCount(animal) == 0; // Vérifie qu'il n'y a aucun animal de ce type
	}
	/**
	 * 
	 * 
	 * Checks if the player has at least the specified minimum total habitat points.
	 *
	 * @param player    the player to check
	 * @param minPoints the minimum required habitat points
	 * @return  tre} if the total habitat points are greater than or equal to {@code minPoints}, {@code false} otherwise
	 */
	public static boolean hasMinimumHabitatPoints(Player player, int minPoints) {
		int totalHabitatPoints = player.getScore().getHabitatScores().values().stream()
				.mapToInt(Integer::intValue)
				.sum();
		return totalHabitatPoints >= minPoints;
	}

	/**
	 * Checks if two specified habitats are not adjacent on the player's board.
	 *
	 * @param player    the player whose board will be checked
	 * @param habitat1  the first habitat to check
	 * @param habitat2  the second habitat to check
	 * @return  true if the habitats are not adjacent, false if they are adjacent
	 */

	public static boolean areHabitatsNotAdjacent(Player player, Habitat habitat1, Habitat habitat2) {
		if(player.getBoard().getType() == TileType.Hexagonal)
			return new HexagoHabitatAnalyzer(player.getBoard()).twoAdjacentHabitats(player.getBoard(), habitat2, habitat2) ; 
		else return new SquareHabitatAnalyzer(player.getBoard()).twoAdjacentHabitats(player.getBoard(), habitat2, habitat2) ;
	}





	/**
	 * Checks if the player has at least a specified number of Nature tokens.
	 *
	 * @param player          the player whose Nature tokens will be checked
	 * @param tokensThreshold the minimum number of Nature tokens required
	 * @return {@code true} if the player has at least the specified number of Nature tokens, {@code false} otherwise
	 */

	public static boolean hasAtLeastNatureTokens(Player player, int tokensThreshold) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");

		return player.getBoard().getNatureTokens() >= tokensThreshold;
	}



	/**
	 * Checks if the player has at least a minimum score for all habitats.
	 *
	 * @param player   the player whose habitat scores will be checked
	 * @param minScore the minimum score required for each habitat
	 * @return true if the player has at least the minimum score for all habitats, false otherwise
	 */

	public static boolean hasMinScoreForAllHabitats(Player player, int minScore) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");
		Map<Habitat, Integer> habitatScores = player.getScore().getHabitatScores();

		for (Habitat habitat : Habitat.values()) {
			if (habitatScores.getOrDefault(habitat, 0) < minScore) {
				return false;
			}
		}
		return true; 	}


	/**
	 * Checks if the player has at least the minimum score for a single habitat.
	 *
	 * @param player   the player whose habitat scores will be checked
	 * @param minScore the minimum score required for at least one habitat
	 * @return {@code true} if the player has at least the minimum score for a single habitat, {@code false} otherwise
	 */

	public static boolean hasMinScoreForSingleHabitat(Player player, int minScore) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");
		Map<Habitat, Integer> habitatScores = player.getScore().getHabitatScores();

		for (int score : habitatScores.values()) {
			if (score >= minScore) {
				return true; 
			}
		}
		return false; 	}




	/**
	 * Checks if the player has no perfect habitats.
	 *
	 * @param player the player whose perfect habitats will be checked
	 * @return {@code true} if the player has no completed perfect habitats, {@code false} otherwise
	 */
	public static boolean hasNoCompletedKeystoneHabitats(Player player) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");

		return player.getBoard().getGainedNatureTokens() == 0;

	}

	/**
	 * Checks if a player has a majority in at least 3 habitats.
	 *
	 * @param player The player (not null).
	 * @return {@code true} if the player has ≥ 3 majorities, {@code false} otherwise.
	 */

	public static boolean hasMajorityForThreeHabitats(Player player) {
		Objects.requireNonNull(player);

		return player.getMajorityHabitat() >= 3;
	}



	/**
	 * Checks if a player has at least the minimum score for all wildlife types.
	 *
	 * @param player The player (not null).
	 * @param minScore The minimum score required for each wildlife type.
	 * @return {@code true} if the player meets or exceeds the minimum score for all wildlife types, {@code false} otherwise.
	 */
	public static boolean hasMinPointsForAllWildlife(Player player, int minScore) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");
		Map<Animal, Integer> animalScores = player.getScore().getAnimalScores();

		for (Animal animal : Animal.values()) {
			if (animalScores.getOrDefault(animal, 0) < minScore) {
				return false; 
			}
		}
		return true; 
	}


	/**
	 * Checks if a player has at least the minimum score for at least two wildlife types.
	 *
	 * @param player The player (not null).
	 * @param minScore The minimum score required for each wildlife type.
	 * @return {@code true} if the player meets or exceeds the minimum score for at least two wildlife types, {@code false} otherwise.
	 */
	public static boolean hasMinPointsForTwoWildlife(Player player, int minScore) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");
		Map<Animal, Integer> animalScores = player.getScore().getAnimalScores();

		long count = animalScores.values().stream()
				.filter(score -> score >= minScore)
				.count();
		return count >= 2; 
	}

	/**
	 * Checks if a player has at least the minimum score for any single wildlife type.
	 *
	 * @param player The player (not null).
	 * @param minScore The minimum score required for a wildlife type.
	 * @return {@code true} if the player has a score greater than or equal to {@code minScore} for at least one wildlife type, {@code false} otherwise.
	 */

	public static boolean hasMinPointsForSingleWildlife(Player player, int minScore) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");
		Map<Animal, Integer> animalScores = player.getScore().getAnimalScores();

		return animalScores.values().stream()
				.anyMatch(score -> score >= minScore);
	}



	/**
	 * Checks if a player has more than a specified number of any single animal.
	 *
	 * @param player The player (not null).
	 * @param max The maximum threshold for a single animal type.
	 * @return {@code true} if the player has more than {@code max} of any single animal, {@code false} otherwise.
	 */
	public static boolean hasMoreThanXOfOneAnimal(Player player, int max) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");

		for (Animal animal : Animal.values()) {
			if (player.getBoard().getAnimalCount(animal) > max) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Checks if a player has scores for at most a specified number of distinct wildlife types.
	 *
	 * @param player The player (not null).
	 * @param maxTypes The maximum number of distinct wildlife types allowed.
	 * @return {@code true} if the player has scores for {@code maxTypes} or fewer distinct wildlife types, {@code false} otherwise.
	 */

	public static boolean hasMaxWildlifeTypes(Player player, int maxTypes) {
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");

		Map<Animal, Integer> animalScores = player.getScore().getAnimalScores();

		long distinctAnimalTypes = animalScores.values().stream()
				.filter(score -> score > 0) 
				.count();

		return distinctAnimalTypes <= maxTypes;
	}















}