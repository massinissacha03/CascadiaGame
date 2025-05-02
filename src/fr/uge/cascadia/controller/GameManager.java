package fr.uge.cascadia.controller;

import fr.uge.cascadia.*;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.tile.Tile;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;


/**
 * The GameManager class manages the logic and flow of the Cascadia game.
 * It handles players, tiles, animal tokens, and the shelf used during gameplay.
 * 
 * @param players      The list of players in the game..
 * @param bagOfTiles   The bag of tiles available for the game.
 * @param bagOfTokens  The bag of animal tokens available for the game.
 * @param shelf        The shelf containing the tiles and tokens available to players.
 */
public record GameManager(List<Player> players, List<Tile> bagOfTiles, List<AnimalToken> bagOfTokens, Shelf shelf) {



	/**
	 * creates a new GameManager
	 * @param players      The list of players.
	 * @param bagOfTiles   The bag of tiles for the game.
	 * @param bagOfTokens  The bag of animal tokens for the game.
	 * @param shelf        The shelf used to hold tiles and tokens during gameplay.
	 * @throws NullPointerException If any of the arguments is null.
	 */
	public GameManager {
		Objects.requireNonNull(players);
		Objects.requireNonNull(bagOfTiles);
		Objects.requireNonNull(bagOfTokens);
		Objects.requireNonNull(shelf);
	}



	/**
	 * Executes the main game loop, iterating through player turns.
	 * The loop handles the creation of the shelf, updating the view,
	 * and managing player turns.
	 *
	 * @param gameInterface The interface to interact with the game (terminal or graphical).
	 */

	public void gameLoop(GameInterface gameInterface) {
		int count = 1;
		while (count <= 20) {
			for (Player player : players) {
				shelf.makeShelf(bagOfTiles, bagOfTokens);

				gameInterface.draw(shelf, player);

				gameInterface.displayMessage("Tour " + count + "/20", Color.GREEN, 1000, 15, 15);
				sleep(1000); 
				oneTurn(gameInterface, player);
				
				gameInterface.draw(shelf, player);
			}
			count++;
		}
	}


	/**
	 * Handles the case where three identical tokens are present in the shelf during a player's turn.
	 * asks the user to decide whether to replace the tokens, and updates the shelf accordingly.
	 *
	 * @param gameInterface The interface used to interact with the game (graphical or terminal).
	 * @param player The current player performing the action.
	 */

	private void handleSimilarTokens(GameInterface gameInterface, Player player) {
		if (shelf.sameAnimalTokens() == 3) {
			boolean replace = gameInterface.makeUserAnswer("Replace tokens?");
			if (shelf.replaceThreeTokens(bagOfTokens, replace)) {
				gameInterface.draw(shelf, player);
			}
		}
	}



	/**
	 * Executes a turn where the player uses a nature token to perform a special action.
	 * The player can select a tile and a token for placement on their board.
	 *
	 * @param gameInterface The interface used to interact with the game
	 * @param player 
	 */
	private void useNatureTokenTurn(GameInterface gameInterface, Player player){
		gameInterface.draw(shelf, player);
		int firstIndex = gameInterface.askForTileId(shelf); 
		Tile firstTile = shelf.getTileById(firstIndex); 
		if (tileInsertionLoop(gameInterface, player, firstTile)) {
			shelf.removeTile(firstIndex);
			gameInterface.draw(shelf, player);
			while (!tileRotationLoop(gameInterface, player, firstTile)) {
				gameInterface.draw(shelf, player);}
			int secondIndex = gameInterface.askForTokenId(shelf); 
			if (tokenInsertionLoop(gameInterface, player, secondIndex)) {
				shelf.removeToken(secondIndex);

				gameInterface.draw(shelf, player);
				sleep(1000);
				removeExtraTileAndToken(gameInterface , player); 
				gameInterface.draw(shelf, player);
				sleep(1000);
			}
		}
	}

	/**
	 * Executes a standard turn for the player. 
	 * The player selects a tile and token pair from the shelf and attempts to place them on their board.
	 *
	 * @param gameInterface The interface used to interact with the game
	 * @param player 
	 */
	private void simpleturn(GameInterface gameInterface, Player player) {
		gameInterface.draw(shelf, player);
		int index = gameInterface.askForPair(shelf);
		Tile tile = shelf.getTileById(index); 
		if (tileInsertionLoop(gameInterface, player, tile)) {
			shelf.removeTile(index);
			gameInterface.draw(shelf, player);
			while (!tileRotationLoop(gameInterface, player, tile)) {
				gameInterface.draw(shelf, player);
			}
			if (tokenInsertionLoop(gameInterface, player, index)) {
				shelf.removeToken(index);
				gameInterface.draw(shelf, player);

				removeExtraTileAndToken(gameInterface , player); 
				gameInterface.draw(shelf, player);
				sleep(2500); 
			}
		}
	}

	/**
	 * Executes a single turn for the specified player.
	 * The player may decide to use a Nature Token or perform a standard turn.
	 * Handles the case of three identical tokens on the shelf at the beginning of the turn.
	 *
	 * @param gameInterface The interface used to interact with the game
	 * @param player The player whose turn is being executed.
	 */
	private void oneTurn(GameInterface gameInterface, Player player) {
		handleSimilarTokens(gameInterface, player);
		gameInterface.draw(shelf, player);

		if ( player.getBoard().getNatureTokens() > 0  ) {
			String choice = gameInterface.askToUseNatureToken();
			if (choice.equalsIgnoreCase("T")) {
				useNatureTokenTurn(gameInterface, player);
				player.getBoard().subtractNatureToken();
				return;
			} else if (choice.equalsIgnoreCase("J")) {
				player.getBoard().subtractNatureToken();
				replaceTokensUsingNatureToken(gameInterface, player);
				simpleturn(gameInterface , player) ; 
				return;
			} else {
				simpleturn(gameInterface, player); 
				return ; 
			}
		} else 	simpleturn(gameInterface, player); 
	}



	private void replaceTokensUsingNatureToken(GameInterface gameInterface, Player player) {
		boolean replacing=true;
		while (!shelf.hasNoTken()) {
			if (replacing == false ) break;
			gameInterface.draw(shelf, player);
			int tokenId = gameInterface.askForTokenId(shelf);
			AnimalToken tokenToReplace = shelf.getTokenById(tokenId);
			if (tokenToReplace != null) {
				shelf.removeToken(tokenId);
				bagOfTokens.add(tokenToReplace);
			}
			gameInterface.draw(shelf, player);
			replacing = gameInterface.makeUserAnswer("Continue replacing tokens?");
		} 
		shelf.completeTokens(bagOfTokens);
		gameInterface.draw(shelf, player);
	}

	/**
	 * Removes the last tile and token from the shelf in a solo mode 
	 *
	 * @param gameInterface The interface used to interact with the game 
	 * @param player
	 */
	private void removeExtraTileAndToken(GameInterface gameInterface, Player player) {
		if (players.size() == 1) {
			shelf.removeLastTile();
			shelf.removeLastToken();

		}}



	private void sleep(int mseconds) {
		try {
			Thread.sleep(mseconds);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	
	
	/**
	 *  asks the player to choose a position and attempts to insert a tile at that position.
	 *
	 * @param gameInterface The interface used to interact with the game 
	 * @param player
	 * @param tile 
	 * @return True when the tile is successfully inserted.
	 */
	private boolean tileInsertionLoop(GameInterface gameInterface, Player player, Tile tile) {
		while (true) {
			Position position = gameInterface.askForPosition(player);

			if (player.playerInsertTile(position, tile)) {
				System.out.println(("Tile inserted successfully at (" + position.x() + ", " + position.y() + ")."));
				bagOfTiles.remove(tile);

				return true;
			}
		}
	}

	
	
	
	/**
	 *  asks the player to choose a position and attempts to insert a token at that position.
	 *
	 * @param gameInterface The interface used to interact with the game 
	 * @param player
	 * @param tokenId  
	 * @return True whether the token is inserted or not 
	 */
	private boolean tokenInsertionLoop(GameInterface gameInterface, Player player, int tokenId) {
		AnimalToken token =  shelf.getTokenById(tokenId);
		while (true) {
			if (player.getBoard().freePlaceForToken(token)) {
				Position position = gameInterface.askForPosition(player);
				if (player.playerInsertToken(position, token)) {
					System.out.println("Token inserted successfully at (" + position.x() + ", " + position.y() + ").");
					bagOfTokens.remove(token);
					shelf.removeToken(tokenId);
					gameInterface.draw(shelf, player);

					return true;
				} 
			} else {
				sleep(2500);
				System.out.println("no tile is compatible with that token " + token);
				bagOfTokens.remove(token);
				return true;
			}}
	}
	/**
	 * Handles the rotation of a tile by listening to player input.
	 * The player can rotate the tile left, right, or confirm its placement.
	 *
	 * @param gameInterface The interface used to interact with the game 
	 * @param player
	 * @param tile The tile to be rotated.
	 * @return True when the rotation is finalized, false if the tile is rotated.
	 */
	private boolean tileRotationLoop(GameInterface gameInterface, Player player, Tile tile) {
		if (!tile.supportsRotation()) return true;
		while (true) {
			var event =  gameInterface.context().pollOrWaitEvent(10);
			if (event == null) continue;
			switch (event) {
			case PointerEvent e:
				continue;
			case KeyboardEvent e:
				if (e.action() == KeyboardEvent.Action.KEY_PRESSED) {
					if (e.key() == KeyboardEvent.Key.LEFT) {
						tile.rotateCounterClockwise(); //rotation 
						return false;
					} else if (e.key() == KeyboardEvent.Key.RIGHT) {
						tile.rotateClockwise(); 
						return false;
					} else if (e.key() == KeyboardEvent.Key.SPACE) {
						return true;	}
				}
				break;
			default:
				break;		}}	}}
