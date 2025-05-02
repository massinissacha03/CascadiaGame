package fr.uge.cascadia.success;

import fr.uge.cascadia.*;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Habitat;

import java.util.*;

/**
 * Represents the scenarios and their associated success conditions for a player.
 * This class is responsible for validating whether specific scenarios have been achieved
 * based on predefined conditions.
 *
 * @param player     The player whose progress is being evaluated.
 * @param usedCards  A map associating animals to scoring cards the player used.
 */
public record ScenarioSuccess(Player player, Map<Animal, CardType> usedCards) implements SuccessManager {



	/**
	 * Checks which scenario successes the player has achieved based on a map of success IDs and descriptions.
	 *
	 * @param successMap A map where keys are success IDs and values are descriptions of the successes.
	 * @return A list of strings representing the successes achieved, formatted as "ID - Description".
	 */
	@Override
	public List<String> checkSuccesses(Map<Integer, String> successMap) {
		Objects.requireNonNull(successMap); 
		List<String> completedSuccesses = new ArrayList<>();

		successMap.forEach((id, description) -> {
			if (isScenarioSuccess(id)) {
				completedSuccesses.add(id + " - " + description);
			}
		});

		return completedSuccesses;
	}

	private boolean isScenarioSuccess(int id) {
		return switch (id) {
		case 1, 2, 3, 4 -> checkScoreAndSpecificCard(id);
		case 5 -> checkScoreAndNatureTokens(85, 3);
		case 6 -> checkTokensForAnimals(85, 4, scenario6Cards());
		case 7 -> checkWildlifePointsAndCards(90, Animal.Buzzard, 20, scenario7Cards());
		case 8 -> checkNatureTokensAndAdjacency(90, 5, Animal.Elk, Animal.Bear, scenario8Cards());
		case 9 -> checkPointsForAnimalsAndHabitats(90, 10, 5, scenario9Cards());
		case 10 -> checkWildlifeAndHabitatsComplex(95, 60, 7, 3, scenario10Cards());
		case 11 -> checkBearPointsAndAdjacency(95, 30, Habitat.Forests, Habitat.Rivers, scenario11Cards());
		case 12 -> checkMaxSalmonAndHabitat(95, 12, Habitat.Forests, Animal.Elk, scenario12Cards());
		case 13 -> checkElkBuzzardAndAdjacency(100, Animal.Elk, 5, Animal.Buzzard, scenario13Cards());
		case 14 -> checkHabitatAdjacencyAndPoints(100, 35, Habitat.Rivers, Habitat.Wetlands, scenario14Cards());
		case 15 -> checkComplexFinalScenario(100, 5, 3, 7, 2, 20, scenario15Cards());
		default -> false;
		};
	}

	// Vérifie un score minimum et la carte spécifique requise
	private boolean checkScoreAndSpecificCard(int id) {
		int minScore = switch (id) {
		case 1, 2, 3 -> 80;
		case 4 -> 85;
		default -> throw new IllegalArgumentException("Scénario invalide");
		};

		CardType requiredCard = switch (id) {
		case 1 -> CardType.A;
		case 2 -> CardType.B;
		case 3 -> CardType.C;
		case 4 -> CardType.D;
		default -> throw new IllegalArgumentException("Carte invalide");
		};

		return SuccessUtils.minimumScore(player, minScore) && usedCards.containsValue(requiredCard);
	}

	// Vérifie un score minimum et un nombre minimal de jetons Nature
	private boolean checkScoreAndNatureTokens(int minScore, int tokensThreshold) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasAtLeastNatureTokens(player, tokensThreshold);
	}

	// Vérifie les jetons pour les animaux
	private boolean checkTokensForAnimals(int minScore, int tokenThreshold, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				cardMap.entrySet().stream()
				.allMatch(entry -> player.getBoard().getAnimalCount(entry.getKey()) >= tokenThreshold &&
				usedCards.get(entry.getKey()) == entry.getValue());
	}

	// Vérifie les points de faune et les cartes
	private boolean checkWildlifePointsAndCards(int minScore, Animal targetAnimal, int minPoints, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinPointsForSingleWildlife(player, minPoints) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les jetons Nature et l'adjacence des animaux
	private boolean checkNatureTokensAndAdjacency(int minScore, int tokensThreshold, Animal animal1, Animal animal2, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasAtLeastNatureTokens(player, tokensThreshold) &&
				!Board.areAnimalsAdjacent(player.getBoard(), animal1, animal2) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les points pour les animaux et les habitats
	private boolean checkPointsForAnimalsAndHabitats(int minScore, int animalPoints, int habitatPoints, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinPointsForAllWildlife(player, animalPoints) &&
				SuccessUtils.hasMinScoreForAllHabitats(player, habitatPoints) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les conditions complexes pour les habitats et la faune
	private boolean checkWildlifeAndHabitatsComplex(int minScore, int wildlifePoints, int habitatPoints, int habitatCount, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinPointsForAllWildlife(player, wildlifePoints) &&
				SuccessUtils.hasMinScoreForSingleHabitat(player, habitatPoints) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les points d'ours et l'adjacence des habitats
	private boolean checkBearPointsAndAdjacency(int minScore, int bearPoints, Habitat habitat1, Habitat habitat2, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinPointsForSingleWildlife(player, bearPoints) &&
				!SuccessUtils.areHabitatsNotAdjacent(player, habitat1, habitat2) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie que le saumon est au max et que l'habitat est valide
	private boolean checkMaxSalmonAndHabitat(int minScore, int habitatPoints, Habitat habitat, Animal animal, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinScoreForSingleHabitat(player, habitatPoints) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les points pour l'élan et le busard
	private boolean checkElkBuzzardAndAdjacency(int minScore, Animal elk, int buzzardPoints, Animal buzzard, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinPointsForSingleWildlife(player, buzzardPoints) &&
				!Board.areAnimalsAdjacent(player.getBoard(), elk, buzzard) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les points et l'adjacence des habitats
	private boolean checkHabitatAdjacencyAndPoints(int minScore, int habitatPoints, Habitat habitat1, Habitat habitat2, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasMinScoreForSingleHabitat(player, habitatPoints) &&
				SuccessUtils.areHabitatsNotAdjacent(player, habitat1, habitat2) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie les conditions complexes du dernier scénario
	private boolean checkComplexFinalScenario(int minScore, int tokensThreshold, int habitatCount, int habitatPoints, int wildlifeCount, int wildlifePoints, Map<Animal, CardType> cardMap) {
		return SuccessUtils.minimumScore(player, minScore) &&
				SuccessUtils.hasAtLeastNatureTokens(player, tokensThreshold) &&
				SuccessUtils.hasMinScoreForAllHabitats(player, habitatPoints) &&
				hasUsedCorrectWildlifeCards(cardMap);
	}

	// Vérifie que les cartes utilisées correspondent aux cartes attendues
	private boolean hasUsedCorrectWildlifeCards(Map<Animal, CardType> expectedCards) {
		return expectedCards.entrySet().stream()
				.allMatch(entry -> usedCards.get(entry.getKey()) == entry.getValue());
	}





	private Map<Animal, CardType> scenario6Cards() { return Map.of(
			Animal.Bear, CardType.D,
			Animal.Elk, CardType.B,
			Animal.Salmon, CardType.C,
			Animal.Buzzard, CardType.C,
			Animal.Fox, CardType.B) ; }

	private Map<Animal, CardType> scenario7Cards() { return Map.of(
			Animal.Bear, CardType.C,
			Animal.Elk, CardType.B,
			Animal.Salmon, CardType.B,
			Animal.Buzzard, CardType.A,
			Animal.Fox, CardType.D
			);  }
	private Map<Animal, CardType> scenario8Cards() { return Map.of(
			Animal.Bear, CardType.C,
			Animal.Elk, CardType.B,
			Animal.Salmon, CardType.B,
			Animal.Buzzard, CardType.A,
			Animal.Fox, CardType.D
			) ;  }
	private Map<Animal, CardType> scenario9Cards() { return  Map.of(
			Animal.Bear, CardType.C,
			Animal.Elk, CardType.A,
			Animal.Salmon, CardType.D,
			Animal.Buzzard, CardType.C,
			Animal.Fox, CardType.B
			); }
	private Map<Animal, CardType> scenario10Cards() { return Map.of(
			Animal.Bear, CardType.C,
			Animal.Elk, CardType.B,
			Animal.Salmon, CardType.D,
			Animal.Buzzard, CardType.B,
			Animal.Fox, CardType.B
			);}
	private Map<Animal, CardType> scenario11Cards() {return  Map.of(
			Animal.Bear, CardType.B,
			Animal.Elk, CardType.A,
			Animal.Salmon, CardType.A,
			Animal.Buzzard, CardType.C,
			Animal.Fox, CardType.A
			);  }
	private Map<Animal, CardType> scenario12Cards() {return  Map.of(
			Animal.Bear, CardType.A,
			Animal.Elk, CardType.B,
			Animal.Salmon, CardType.A,
			Animal.Buzzard, CardType.A,
			Animal.Fox, CardType.C
			); }
	private Map<Animal, CardType> scenario13Cards() {return Map.of(
			Animal.Bear, CardType.D,
			Animal.Elk, CardType.C,
			Animal.Salmon, CardType.C,
			Animal.Buzzard, CardType.B,
			Animal.Fox, CardType.C
			); }
	private Map<Animal, CardType> scenario14Cards() {return  Map.of(
			Animal.Bear, CardType.A,
			Animal.Elk, CardType.C,
			Animal.Salmon, CardType.B,
			Animal.Buzzard, CardType.A,
			Animal.Fox, CardType.D) ;  }

	private Map<Animal, CardType> scenario15Cards() {return Map.of(
			Animal.Bear, CardType.A,
			Animal.Elk, CardType.D,
			Animal.Salmon, CardType.A,
			Animal.Buzzard, CardType.D,
			Animal.Fox, CardType.A
			); }
}
