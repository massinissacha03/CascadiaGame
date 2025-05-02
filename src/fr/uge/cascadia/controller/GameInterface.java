package fr.uge.cascadia.controller;

import fr.uge.cascadia.*;
import fr.uge.cascadia.board.Shelf;

import java.util.List;

import com.github.forax.zen.ApplicationContext;


import java.awt.Color;
/**
 * Interface representing the essential operations of a game controller, 
 * used for terminal and graphical implementations.
 *
 *	@author massinissa 
 */
public sealed interface GameInterface permits ControllerTerminal , ControllerGraphic {

	/**
	 * Asks the player for a position on the board.
	 *
	 * @param player asked to choose. 
	 * @return The selected position.
	 */
	public Position askForPosition(Player player);


	/**
	 * Asks the player to select a pair (tile and token) from the shelf.
	 *
	 * @param shelf The shelf containing tiles and tokens
	 * @return The ID of the selected pair.
	 */
	public int askForPair(Shelf shelf);

	/**
	 * Displays a message on the game view
	 *
	 * @param message The message to display 
	 * @param color   The color of the message 
	 * @param x       The x-coordinate position of the message.
	 * @param y       The y-coordinate position of the message.
	 * @param size    The font size of the message.
	 */

	public void displayMessage(String message, Color color , int x , int y , int size) ; 




	/**
	 * Draws the current state of the game, including the shelf and the player's board and informations
	 *
	 * @param shelf  The shelf containing the tiles and tokens to display
	 * @param player The player whose board and information are to be displayed 
	 */

	public void draw(Shelf shelf, Player player);

	/**
	 * Provides the application context for graphical implementations.
	 *
	 * @return The application context.
	 * @throws UnsupportedOperationException if the implementation does not support this operation.
	 */
	public default ApplicationContext context() {
		throw new UnsupportedOperationException("This operation is not supported for this implementation.");
	}



	/**
	 * Asks the player whether they want to use a Nature Token and specifies the available options.
	 *
	 * @return A string representing the player's choice:
	 *         - "T" if the player chooses to select a unique tile and a unique token.
	 *         - "J" if the player chooses to replace the tokens. 
	 *         - "" if the player choose not to use is.
	 */
	public String askToUseNatureToken();

	/**
	 * makes the user to select a Tile ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected token.
	 */
	public int askForTileId(Shelf shelf);


	/**
	 * makes the user to select a token ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected token.
	 */
	public int askForTokenId(Shelf shelf);



	/**
	 * Displays the end screen with the players' scores and waits for the user to quit
	 *
	 * @param players The list of players whose scores are displayed
	 */
	public void displayEndScreen(List<Player> players);


	/**
	 * Displays a question to the user and waits for a Yes (Y) or No (N) response.
	 *
	 * @param question The question to display to the user.
	 * @return True if the user responds with "Y", False if the user responds with "N".
	 */
	public boolean makeUserAnswer(String question);




}
