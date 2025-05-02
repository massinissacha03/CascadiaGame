package fr.uge.cascadia.success;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.animal.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * Represents the achievements of a player in a regular game session.
 * This class is responsible for evaluating the player's progress
 * and determining which achievements (successes) they have completed.
 * @param player The player whose achievements will be evaluated.

 */
public record GameSuccess(Player player) implements SuccessManager {

	/**
	 * creates a new `GameSuccess` instance for a given player.
	 *
	 */
	public GameSuccess {
		Objects.requireNonNull(player);
	}






	/**
	 * Checks which achievements the player has completed based on the provided success map.
	 *
	 * @param successMap A map where the keys are success IDs and the values are success descriptions.
	 * @return A list of strings representing the completed successes in the format "ID - Description".
	 */
	public List<String> checkSuccesses(Map<Integer, String> successMap) {
		Objects.requireNonNull(successMap, "La map des succès ne peut pas être null.");
		List<String> completedSuccesses = new ArrayList<>();

		successMap.forEach((id, description) -> {
			if (isSuccessAchieved(id)) {
				completedSuccesses.add(id + " - " + description);
			}
		});

		return completedSuccesses;
	}

	private boolean isSuccessAchieved(int id) {
		return switch (id) {
		case 1, 2, 3, 4, 5, 6, 7 -> checkScoreSuccess(id);
		case 8 -> SuccessUtils.hasNoNatureTokensLeft(player);
		case 9, 10, 11, 12, 13 -> checkNoAnimalSuccess(id);
		case 14 -> SuccessUtils.hasMoreThanXOfOneAnimal(player, 10);
		case 15 -> SuccessUtils.hasMajorityForThreeHabitats(player);
		case 16, 17, 18 -> checkHabitatScoreSuccess(id);
		case 19, 20, 21 -> checkWildlifeScoreSuccess(id);
		case 22, 23 -> checkNatureTokenSuccess(id);
		case 24 -> SuccessUtils.hasNoCompletedKeystoneHabitats(player);
		case 25 -> SuccessUtils.hasMaxWildlifeTypes(player, 3);
		default -> false;
		};
	}

	private boolean checkScoreSuccess(int id) {
		int requiredScore = switch (id) {
		case 1 -> 80;
		case 2 -> 85;
		case 3 -> 90;
		case 4 -> 95;
		case 5 -> 100;
		case 6 -> 105;
		case 7 -> 110;
		default -> throw new IllegalStateException("ID non géré : " + id);
		};
		return SuccessUtils.minimumScore(player, requiredScore);
	}

	private boolean checkNoAnimalSuccess(int id) {
		return switch (id) {
		case 9 -> SuccessUtils.hasNoAnimal(player, Animal.Bear);
		case 10 -> SuccessUtils.hasNoAnimal(player, Animal.Elk);
		case 11 -> SuccessUtils.hasNoAnimal(player, Animal.Salmon);
		case 12 -> SuccessUtils.hasNoAnimal(player, Animal.Buzzard);
		case 13 -> SuccessUtils.hasNoAnimal(player, Animal.Fox);
		default -> throw new IllegalStateException("ID non géré : " + id);
		};
	}

	private boolean checkHabitatScoreSuccess(int id) {
		int requiredPoints = switch (id) {
		case 16 -> 5;
		case 17 -> 12;
		case 18 -> 15;
		default -> throw new IllegalStateException("ID non géré : " + id);
		};
		return switch (id) {
		case 16 -> SuccessUtils.hasMinScoreForAllHabitats(player, requiredPoints);
		case 17, 18 -> SuccessUtils.hasMinScoreForSingleHabitat(player, requiredPoints);
		default -> false;
		};
	}

	private boolean checkWildlifeScoreSuccess(int id) {
		int requiredPoints = switch (id) {
		case 19 -> 10;
		case 20 -> 20;
		case 21 -> 30;
		default -> throw new IllegalStateException("ID non géré : " + id);
		};
		return switch (id) {
		case 19 -> SuccessUtils.hasMinPointsForAllWildlife(player, requiredPoints);
		case 20 -> SuccessUtils.hasMinPointsForTwoWildlife(player, requiredPoints);
		case 21 -> SuccessUtils.hasMinPointsForSingleWildlife(player, requiredPoints);
		default -> false;
		};
	}

	private boolean checkNatureTokenSuccess(int id) {
		int requiredTokens = switch (id) {
		case 22 -> 5;
		case 23 -> 10;
		default -> throw new IllegalStateException("ID non géré : " + id);
		};
		return SuccessUtils.hasAtLeastNatureTokens(player, requiredTokens);
	}
}
