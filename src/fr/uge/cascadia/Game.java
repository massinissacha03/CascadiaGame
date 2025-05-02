package fr.uge.cascadia;

import java.awt.Color;
import java.io.IO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.forax.zen.Application;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.controller.ControllerGraphic;
import fr.uge.cascadia.controller.ControllerTerminal;
import fr.uge.cascadia.controller.GameManager;
import fr.uge.cascadia.score.*;
import fr.uge.cascadia.success.GameSuccess;
import fr.uge.cascadia.success.ScenarioSuccess;
import fr.uge.cascadia.tile.Tile;
import fr.uge.cascadia.tile.TileType;
import fr.uge.cascadia.view.GameView;
import fr.uge.cascadia.view.HexagoView;
import fr.uge.cascadia.view.ImageLoader;
import fr.uge.cascadia.view.SquareView;
import fr.uge.cascadia.view.ViewUtils;




/**
 * The main class representing the Cascadia game. It manages players, tiles, tokens, and scoring strategy.
 * 
 * @author Massinissa
 */
public class Game {
	/**
	 * The list of players participating in the game.
	 */
	private final List<Player> players = new ArrayList<>();
	/**
	 * The shared shelf used during the game to hold available tiles and tokens.
	 */
	private final Shelf shelf = new Shelf();

	/**
	 * The bag containing all the tiles used in the game.
	 */
	private final List<Tile> bagOfTiles;

	/**
	 * The bag containing all the animal tokens used in the game.
	 */
	private final List<AnimalToken> bagOfTokenAnimal = AnimalToken.createBagOfAnimalTokens();

	/**
	 * The type of tiles used in the game, which can be either square or hexagonal.
	 */
	private final TileType tileType;

	/**
	 * The strategy used for scoring during the game.
	 */
	private final ScoringStrategy scoringStrategy;

	/**
	 * Constructor for the Game class. Initializes the players, the type of tiles, and the scoring strategy.
	 *
	 * @param numberOfPlayers the number of players participating in the game.
	 * @param tileType the type of tiles used in the game 
	 * @param scoringStrategy the scoring strategy to be used during the game.
	 * @throws NullPointerException if tileType or scoringStrategy is null.
	 */
	private Game(int numberOfPlayers, TileType tileType, ScoringStrategy scoringStrategy) {
		this.tileType = Objects.requireNonNull(tileType);
		this.scoringStrategy = Objects.requireNonNull(scoringStrategy);

		initializePlayers(numberOfPlayers);
		bagOfTiles = Tile.createTileBag(tileType);
	}

	/**
	 * Initializes the players for the game.
	 * 
	 * @param numberOfPlayers the number of players to be added to the game.  
	 **/
	private void initializePlayers(int numberOfPlayers) {

		for (int i = 0; i < numberOfPlayers; i++) {
			players.add(askForPlayer(i));
		}
	}

	private Player askForPlayer(int n) {
		String name = IO.readln("Veuillez entrer le nom du joueur numéro " + (n + 1) + " :");
		Objects.requireNonNull(name, "Le nom du joueur ne peut pas être null.");
		return new Player(name, tileType, n * 3);
	}


	/**
	 * Prompts the user to enter the number of players participating in the game.
	 * The number must be between 1 and 4.
	 *
	 * @return the valid number of players.
	 */
	private static int inputNbplayers() {
		int numberOfPlayers = 0;
		while (numberOfPlayers <= 0||  numberOfPlayers>4) {
			try {
				numberOfPlayers = Integer.parseInt(IO.readln("Combien de joueurs participeront ? (1 à 4) :"));
				if (numberOfPlayers <= 0 || numberOfPlayers>4) {
					System.out.println("Veuillez entrer un nombre valide.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Veuillez entrer un nombre valide.");
			}
		}
		return numberOfPlayers;
	}



	/**
	 * Prompts the user to enter the tileType ( hexagonal / Square ) 
	 *
	 * @return the valid type of the tile.
	 */
	private static TileType inputTileType() {
		while (true) {
			String tileChoice = IO.readln("Choisissez un type de tuiles :\n1 -> Tuiles Carrées\n2 -> Tuiles Hexagonales\nVotre choix : ");
			if ("1".equals(tileChoice)) {
				return TileType.Square;
			} else if ("2".equals(tileChoice)) {
				System.out.println("Le mode hexagonal est uniquement disponible en mode graphique.");
				return TileType.Hexagonal;
			}
			System.out.println("Choix invalide. Veuillez recommencer.");
		}
	}


	/**
	 * Prompts the user to choose game mode between terminal and graphical
	 *
	 * @return false (for terminal) / true (for graphical)
	 */

	private static boolean chooseMode(TileType tileType) {
		if (tileType == TileType.Hexagonal) {
			return true; // Mode graphique obligatoire pour Hexagonal
		}

		while (true) {
			String modeChoice = IO.readln("Choisissez un mode de jeu :\n1 -> Terminal\n2 -> Graphique\nVotre choix : ");
			if ("1".equals(modeChoice)) {
				return false;
			} else if ("2".equals(modeChoice)) {
				return true;
			}
			System.out.println("Choix invalide. Veuillez recommencer.");
		}
	}


	/**
	 * Prompts the user to choose a scoring Strategy 
	 *
	 * @return a valid ScoringStrategy
	 */
	private static ScoringStrategy chooseScoringStrategy() {
		while (true) {
			String variant = IO.readln(
					"Choisissez une variante pour calculer le score :\n" +
							"1 -> Famille\n" +
							"2 -> Intermédiaire\n" +
							"3 -> Cartes Faune\n" +
							"Votre choix :"
					);
			switch (variant) {
			case "1":
				return new VariantScoring("famille");
			case "2":
				return new VariantScoring("intermediaire");
			case "3":
				return initializeFaunaScoring();
			default:
				System.out.println("Choix invalide. Veuillez recommencer.");
			}
		}
	}



	/**
	 * Prompts the user to choose a Fauna Card 
	 *
	 * @return a valid ScoringStrategy
	 */
	private static FaunaScoring initializeFaunaScoring() {
		Map<Animal, CardType> animalCardMap = new HashMap<>();
		for (Animal animal : Animal.values()) {
			boolean validInput = false;
			while (!validInput) {
				String cardTypeInput = IO.readln(
						"Choisissez une carte pour l'animal " + animal.name() + " (A, B, C, D) :").toUpperCase();
				if (cardTypeInput.matches("[ABCD]")) {
					animalCardMap.put(animal, CardType.valueOf(cardTypeInput));
					validInput = true;
				} else {
					System.out.println("Type de carte invalide. Veuillez choisir entre A, B, C ou D.");
				}
			}
		}
		return new FaunaScoring(animalCardMap);
	}


	/**
	 * Initializes the game based on the user's inputs
	 * @return a fully initialized Game object 
	 */
	private static Game initializeGame() {
		int numberOfPlayers = inputNbplayers();
		TileType tileType = inputTileType();
		ScoringStrategy scoringStrategy = chooseScoringStrategy();
		return new Game(numberOfPlayers, tileType, scoringStrategy);
	}



	/**
	 * Runs the game in terminal mode.
	 * At the end of the game, performs final score processing and displays results.
	 */
	private void startTerminalMode() {
		GameManager gameManager = new GameManager(players, bagOfTiles, bagOfTokenAnimal, shelf);
		ControllerTerminal terminalController = new ControllerTerminal(gameManager);
		terminalController.startGame();
		endGameProcessing(); 
	}



	/**
	 * Runs the game in graphical mode.
	 * At the end of the game, performs final score processing and displays results.
	 */
	private void startGraphicalMode() {
		GameManager gameManager = new GameManager(players, bagOfTiles, bagOfTokenAnimal, shelf);
		Application.run(Color.WHITE, context -> {
			int tileSize = 70;
			ImageLoader imagLoader = new ImageLoader(); 
			ViewUtils viewUtils = new ViewUtils(imagLoader); 
			GameView view = (tileType == TileType.Hexagonal)
					? new HexagoView(3 * tileSize, tileSize, tileSize ,viewUtils)
							: new SquareView(3 * tileSize, tileSize, tileSize , viewUtils); 

			ControllerGraphic graphicController = new ControllerGraphic(gameManager, context, view);
			graphicController.startGame();

			endGameProcessing(); // Appel de la méthode commune

			context.renderFrame(graphics -> graphicController.displayEndScreen(players));
		});
	}

	/**
	 * Processes the end of the game.
	 * Calculates the scores for all players, applies bonus points, assigns surnames based on scores,
	 * Stocks the achievements of the winner. 
	 */
	private void endGameProcessing() {
		for (Player player : players) {
			player.setScoringStrategy(scoringStrategy);
			player.calculateScore();
		}

		Score.calculateBonusPoints(players);

		players.forEach(player ->   {          
			player.attributeSurname(); 
			System.out.println("Le score du joueur " + player.getName() + " est : " + player.getScore());
		});

		stockSuccess(players,tileType.toString(),scoringStrategy);
	}





	/**
	 * Records the achievements and successes of the game.
	 *
	 * @param players A list of players participating in the game.
	 * @param gameMode The mode of the game (e.g., "Hexagonal" or "Square").
	 * @param scoringStrategy The scoring strategy used during the game.
	 * @throws IllegalStateException if no players are found.
	 */
	private void stockSuccess(List<Player> players, String gameMode, ScoringStrategy scoringStrategy) {
		Objects.requireNonNull(players); 
		Objects.requireNonNull(gameMode); 
		Objects.requireNonNull(scoringStrategy); 
		Player winner = players.stream()
				.max((p1, p2) -> Integer.compare(p1.getScore().getTotalPoints(), p2.getScore().getTotalPoints()))
				.orElseThrow(() -> new IllegalStateException("Aucun joueur trouvé"));

		GameSuccess gameSuccess = new GameSuccess(winner) ; 
		gameSuccess.stockSuccess(gameMode ,scoringStrategy ,winner ) ; 
		switch(scoringStrategy) {
		case FaunaScoring funaScoring-> {    	
			ScenarioSuccess scenarioSuccess = new ScenarioSuccess(winner, funaScoring.getAnimalCardMap()) ;
			scenarioSuccess.stockSuccess(gameMode ,scoringStrategy ,winner ) ; 

		}
		default -> {}
		}
	}




	   /**
	    * The main method to start the game.
	    * 
	    * @param args Spurious arguments.
	    */
	public static void main(String[] args) {
		Game game = initializeGame();
		boolean isGraphical = chooseMode(game.tileType);

		if (isGraphical) {
			game.startGraphicalMode();
		} else {
			game.startTerminalMode();
		}





	}

}
