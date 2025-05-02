package fr.uge.cascadia.success;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.score.FaunaScoring;
import fr.uge.cascadia.score.ScoringStrategy;
import fr.uge.cascadia.score.VariantScoring;

/**
 * The {@code SuccessManager} interface defines methods for managing and checking player achievements
 * in different game scenarios.
 * 
 * This interface is designed to handle success-related operations, such as:
 * - Checking achievements based on predefined success maps.
 * - Reading and writing success data to/from files.
 * - Building detailed logs of player successes.
 */



public sealed interface SuccessManager permits ScenarioSuccess, GameSuccess {

	/**
	 * Checks the successes achieved by the player based on a map of success IDs and descriptions.
	 * 
	 * @param successMap A map where keys are success IDs and values are descriptions of the successes.
	 * @return A list of strings representing the successes achieved by the player.
	 * @throws NullPointerException If the {@code successMap} is null.
	 */
	List<String> checkSuccesses(Map<Integer, String> successMap);









	/**
	 * Stores the achievements of the winning player into a file, based on the game mode and scoring strategy.
	 *
	 * @param gameMode        The game mode (e.g., graphical or terminal).
	 * @param scoringStrategy The scoring strategy used in the game.
	 * @param winner          The winning player whose achievements are being recorded.
	 */
	default void stockSuccess(String gameMode, ScoringStrategy scoringStrategy, Player winner ) {
		Objects.requireNonNull(gameMode); 
		Objects.requireNonNull(winner); 
		Objects.requireNonNull(scoringStrategy); 
		Map<Integer, String> successMap = readSuccessMapFromFile("successNormal.txt");
		Map<Integer, String> scenarioMap = readSuccessMapFromFile("Scenarios.txt");
		switch (scoringStrategy) {
		case FaunaScoring coringStrategy-> {
			writeSuccessesToFile("ScenarioAchievements.txt", scenarioMap, gameMode, scoringStrategy, winner);

		}
		default -> {}

		}

		writeSuccessesToFile("GameAchievements.txt",successMap , gameMode, scoringStrategy, winner);
	}









	/**
	 * Reads a success map from a file and parses it into a {@code Map<Integer, String>}.
	 *
	 * @param filename The name of the file containing the success map.
	 * @return A map of success IDs and descriptions.
	 */

	default Map<Integer, String> readSuccessMapFromFile(String filename) {
		Objects.requireNonNull(filename); 

		try {
			List<String> lines = Files.readAllLines(Path.of(filename)); 
			return parseSuccessMap(lines);
		} catch (IOException e) {
			throw new UncheckedIOException("Erreur lors de la lecture du fichier " + filename, e);
		}
	}

	private Map<Integer, String> parseSuccessMap(List<String> lines) {
		Map<Integer, String> successMap = new HashMap<>();
		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) continue;

			String[] parts = line.split(" ", 2);
			int id = Integer.parseInt(parts[0]);
			String description = parts[1];
			successMap.put(id, description);
		}
		return successMap;
	}


	/**
	 * Writes the achievements of a player to a file.
	 *
	 * @param filename       the name of the final we rite in 
	 * @param successMap      A map of success IDs and descriptions.
	 * @param gameMode       graphical or terminal
	 * @param scoringStrategy The scoring strategy used in the game.
	 * @param player          The player whose achievements are being recorded.
	 * @throws UncheckedIOException If an error occurs while writing to the file.
	 */
	default void writeSuccessesToFile(String filename, Map<Integer, String> successMap, String gameMode, ScoringStrategy scoringStrategy, Player player) {
		Objects.requireNonNull(filename, "Le nom du fichier ne peut pas être null.");
		Objects.requireNonNull(successMap, "La map des succès ne peut pas être null.");
		Objects.requireNonNull(gameMode, "Le mode de jeu ne peut pas être null.");
		Objects.requireNonNull(scoringStrategy, "La stratégie de scoring ne peut pas être null.");
		Objects.requireNonNull(player, "Le joueur ne peut pas être null.");

		try {
			String content = buildSuccessFileContent(successMap, gameMode, scoringStrategy, player);
			Files.writeString(Path.of(filename), content , StandardOpenOption.APPEND, StandardOpenOption.CREATE); 
		} catch (IOException e) {
			throw new UncheckedIOException("Erreur lors de l'écriture des succès dans le fichier " + filename, e);
		}
	}

	private String buildSuccessFileContent(Map<Integer, String> successMap, String gameMode, ScoringStrategy scoringStrategy, Player player) {
		StringBuilder sb = new StringBuilder();
		appendHeader(sb);
		appendPlayerInfo(sb, player, gameMode, scoringStrategy);
		appendSuccesses(sb, successMap, player);
		appendFooter(sb);
		return sb.toString();
	}

	private void appendHeader(StringBuilder sb) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		sb.append("=== Succès enregistrés le : ").append(LocalDateTime.now().format(formatter)).append(" ===\n");
	}



	private void appendSuccesses(StringBuilder sb, Map<Integer, String> successMap, Player player) {
		List<String> successes = checkSuccesses(successMap);
		sb.append("Succès atteints :\n");
		if (successes.isEmpty()) {
			sb.append("  Aucun succès atteint.\n");
		} else {
			for (String success : successes) {
				sb.append("  - ").append(success).append("\n");
			}
		}
	}

	private void appendFooter(StringBuilder sb) {
		sb.append("===============================================\n\n");
	}




	private void appendPlayerInfo(StringBuilder sb, Player player, String gameMode, ScoringStrategy scoringStrategy) {
		sb.append("Nom du joueur : ").append(player.getName()).append("\n");
		sb.append("Mode du jeu : ").append(gameMode).append("\n");
		sb.append("Stratégie de scoring : ").append(scoringStrategy.getClass().getSimpleName()).append("\n");

		String usedCards = getUsedCardsDescription(scoringStrategy);
		sb.append("Cartes utilisées pour le calcul des scores : \n").append(usedCards);

		sb.append("Score total : ").append(player.getScore().getTotalPoints()).append("\n");
	}

	private String getUsedCardsDescription(ScoringStrategy scoringStrategy) {
		StringBuilder sb = new StringBuilder();

		switch (scoringStrategy) {
		case FaunaScoring faunaScoring -> {
			faunaScoring.getAnimalCardMap().forEach((animal, card) -> {
				sb.append("  - ").append(animal).append(": ").append(card).append("\n");
			});
		}
		case VariantScoring variantScoring -> {
			sb.append("  Carte utilisée pour tous les animaux : ").append(variantScoring.variant()).append("\n");
		}
		default -> sb.append("  Aucun détail sur les cartes pour cette stratégie.\n");
		}

		return sb.toString();
	}





}
