package fr.uge.cascadia.controller;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.tile.Tile;

import java.awt.Color;
import java.io.IO;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
/**
 * A record representing the terminal controller for the game.
 * It handles interactions between the game logic using the terminal .
 *
 * @param gameManager The manager of the logic of the game.
 */
public record ControllerTerminal ( GameManager gameManager)  implements GameInterface {


	
	/**
	 * Creates a terminal Controller
	 */
	public ControllerTerminal {
		Objects.requireNonNull(gameManager); 
	}

	

	/**
	 * Starts the game loop for the terminal controller.
	 */
	public void startGame() {
		gameManager.gameLoop(this);
	}





	/**
	 * Asks the player for a position in the grid of the board
	 *
	 * @param player The player requesting the position.
	 * @return A valid position on the board.
	 */
	@Override
	public Position askForPosition(Player player) {
		int x = Integer.parseInt(IO.readln("Enter x: "));
		int y = Integer.parseInt(IO.readln("Enter y: "));
		return new Position(x, y);
	}



	/**
	 * asks the user to select a couple of (tile,token)  ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected token.
	 * @throws NullPointerException if the shelf is null.
	 */
	@Override
	public int askForPair(Shelf shelf) {
		List<Tile> tiles = shelf.getTiles();
		List<AnimalToken> tokens = shelf.getTokens(); 

		int index;
		do {
			index = Integer.parseInt(IO.readln("Choose a tile index (0 to " + (tiles.size() - 1) + "): "));
		} while (index < 0 || tiles.get(index)==null || tokens.get(index)==null);


		return index ; 
	}


	/**
	 * Asks the player whether they want to use a Nature Token and specifies the available options.
	 *
	 * @return A string representing the player's choice:
	 *         - "T" if the player chooses to select a unique tile and a unique token.
	 *         - "J" if the player chooses to replace the tokens. 
	 *         - "" if the player choose not to use is.
	 */
	@Override
	public String askToUseNatureToken() {
		String input = IO.readln("Appuyez sur 'T' pour utiliser un jeton Nature (choix tuile et jeton indépendants), 'J' pour remplacer des jetons que vous voulez , ou Entrée pour continuer.\n");
		if (input.equalsIgnoreCase("T") || input.equalsIgnoreCase("J")) {
			return input.toUpperCase(); // Retourne soit "T" soit "J"
		}
		return ""; // Pas d'utilisation de jeton nature
	}



	/**
	 * Waits for the user to select a Tile ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected tile.
	 */

	@Override
	public int askForTileId(Shelf shelf) {
		List<Tile> tiles = shelf.getTiles();
		IO.println("Tuiles disponibles :");
		for (int i = 0; i < tiles.size(); i++) {
			IO.println(i + ": " + tiles.get(i));
		}

		int index;
		do {
			index = Integer.parseInt(IO.readln("Choisissez l'index de la tuile (0-" + (tiles.size() - 1) + ") : "));
		} while (index < 0 || index >= tiles.size());

		return index;
	}


	/**
	 * asks for the user to select a token ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected token.
	 * @throws NullPointerException if the shelf is null.
	 */

	@Override
	public int askForTokenId(Shelf shelf) {
		List<AnimalToken> tokens = shelf.getTokens();
		IO.println("Jetons disponibles :");
		for (int i = 0; i < tokens.size(); i++) {
			IO.println(i + ": " + tokens.get(i));
		}

		int index;
		do {
			index = Integer.parseInt(IO.readln("Choisissez l'index du jeton (0-" + (tokens.size() - 1) + ") : "));
		} while (index < 0 || index >= tokens.size());

		return index;
	}


	/**
	 * Displays the end screen with the players' scores and waits for the user to quit
	 *
	 * @param players The list of players whose scores are displayed
	 */
	@Override
	public void displayEndScreen(List<Player> players) {
		System.out.println("\n=== Scores finaux ===");
		for (Player player : players) {
			System.out.println(player.getName() + " : " + player.getScore() );
		}
		System.out.println("Appuyez sur Entrée pour quitter...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Draws the current state of the game, including the shelf and the player's board and informations
	 *
	 * @param shelf  The shelf containing the tiles and tokens to display
	 * @param player The player whose board and information are to be displayed 
	 */

	@Override
	public void draw(Shelf shelf, Player player) {
		System.out.println("plateau du joueur : "+ player.getName() );
		if(!shelf.toString().equals("")) {

			player.getBoard().displayBoard();
			System.out.println(shelf);

		}

	}



	@Override
	public void displayMessage(String message, Color color, int x, int y, int size) {

		System.out.println(message+"\n");
	}


	/**
	 * Displays a question to the user and waits for a Yes (Y) or No (N) response.
	 *
	 * @param question The question to display to the user.
	 * @return True if the user responds with "Y", False if the user responds with "N".
	 */
	@Override
	public boolean makeUserAnswer(String question) {
		String answer = IO.readln(question + "Y(yes)/N(no)").trim();

		return answer.equalsIgnoreCase("y");
	}
}
