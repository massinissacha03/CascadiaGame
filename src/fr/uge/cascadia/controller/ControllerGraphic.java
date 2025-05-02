package fr.uge.cascadia.controller;

import fr.uge.cascadia.*;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.tile.Tile;
import fr.uge.cascadia.view.GameView;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.PointerEvent;
import com.github.forax.zen.KeyboardEvent;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

/**
 * A record representing the graphical controller for the game.
 * It handles interactions between the game logic, graphical context, and game view.
 *
 * @param gameManager The manger of the logic of the game.
 * @param context  The graphical application context.
 * @param view     The graphical view of the game.
 */

public record ControllerGraphic (  GameManager gameManager ,   ApplicationContext context , GameView view) implements GameInterface {


	/**
	 * Creates a Graphical COntroller
	 */
	public ControllerGraphic {
		Objects.requireNonNull(context); 
		Objects.requireNonNull(view); 
		Objects.requireNonNull(gameManager); 
	}



	/**
	 * Starts the game loop for the graphical controller.
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
		Objects.requireNonNull(player); 
		while(true) {
			var event = context.pollOrWaitEvent(10);
			if (event != null) {
				switch (event) {
				case PointerEvent pe ->{
					if(pe.action() == PointerEvent.Action.POINTER_DOWN){
						var location = pe.location();
						Position realPosition = view.fromScreenToRealCoordinates(location.x(), location.y(), player.getBoard());
						if (realPosition != null) {
							return realPosition; // Position valide retournée
						} 
					}
				} 
				default -> {}
				}
			}}
	}

	/**
	 * Displays a question to the user and waits for a Yes (Y) or No (N) response.
	 *
	 * @param question The question to display to the user.
	 * @return True if the user responds with "Y", False if the user responds with "N".
	 */
	@Override
	public boolean makeUserAnswer (String question ) {
		Objects.requireNonNull(question);
		view.displayMessage(context,question , Color.RED, 300, 10 , 14);
		view.displayMessage(context, "(Y for YES) / (N for NO)", Color.RED, 300, 25 , 14);
		while (true) {
			var event = context.pollOrWaitEvent(10);
			if (event !=null) {
				switch (event) {
				case KeyboardEvent ke  ->{ 
					if (ke.action() == KeyboardEvent.Action.KEY_PRESSED) {
						switch (ke.key() ) {
						case KeyboardEvent.Key.Y -> {return true ; } 
						case KeyboardEvent.Key.N -> {return false  ;} 
						default -> {}  }	}
				}
				default -> {}}
			}
		}
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
		Objects.requireNonNull(shelf); 
		List<AnimalToken> tokens = shelf.getTokens();
		while (true) {
			var event = context.pollOrWaitEvent(10);
			if( event != null ) {
				switch (event) {
				case  PointerEvent pe -> {
					if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
						var location = pe.location();
						int x = location.x();
						int y = location.y();
						Position realPosition = view.getCoupleFromScreen(x, y);
						if (realPosition != null && realPosition.y() >= 0 && realPosition.y() < shelf.size() && realPosition.x() == 1 && tokens.get(realPosition.y())!=null) {
							return realPosition.y();			}
					}
				}
				default -> {}
				}}}}





	/**
	 * asks the user to select a couple of (tile,token)  ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected token.
	 * @throws NullPointerException if the shelf is null.
	 */
	
	@Override
	public int askForPair(Shelf shelf) {
		Objects.requireNonNull(shelf); 
		while (true) {
			var event = context.pollOrWaitEvent(10); 
			if(event !=null) {
				switch (event) {
				case PointerEvent pe -> {
					if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
						var location = pe.location();
						int x = location.x();
						int y = location.y();
						Position realPosition = view.getCoupleFromScreen(x, y);
						if (realPosition != null && realPosition.y() >= 0 && realPosition.y() < shelf.size() && realPosition.x() == 0 ) {
							return realPosition.y();
						}
					}
				}
				default ->{}
				}}
		}}



	/**
	 * Waits for the user to select a Tile ID from the shelf.
	 *
	 * @param shelf The shelf containing the available tokens.
	 * @return The ID of the selected tile.
	 */
	
	@Override
	public int askForTileId(Shelf shelf) {
		Objects.requireNonNull(shelf); 
		List<Tile> tilesList = List.copyOf(shelf.getTiles());
		while (true) {
			var event = context.pollOrWaitEvent(10);
			if ( event != null ) {
				switch(event) {
				case PointerEvent pe -> {
					if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
						var location = pe.location();
						int x = location.x();
						int y = location.y();
						Position realPosition = view.getCoupleFromScreen(x, y);
						if (realPosition != null && realPosition.y() >= 0 && realPosition.y() < shelf.size() && realPosition.x() == 0 && tilesList.get(realPosition.y())!=null) {
							return realPosition.y();
						}
					}
				}
				default -> {}
				}			}}
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
		view.displayMessage(context, "use a nature Token ", Color.orange, 250, 10 , 12);
		view.displayMessage(context, "N (no)  , J(choose token to replace) , T(choose a tile and choose a token) " , Color.orange, 170, 25 , 12);
		while (true) {
			var event = context.pollOrWaitEvent(10);
			if (event!=null) {
				switch (event) {
				case KeyboardEvent ke-> {
					if ( ke.action() == KeyboardEvent.Action.KEY_PRESSED ){
						switch (ke.key() ) {
						case KeyboardEvent.Key.T -> {return "T";} 
						case KeyboardEvent.Key.J -> {return "J" ;  }
						case KeyboardEvent.Key.N -> { return "" ; }
						default -> {}
						}
					}
				}
				default -> {} 
				}
			}}}



	
	/**
	 * Displays a message on the game view
	 *
	 * @param message The message to display 
	 * @param color   The color of the message 
	 * @param x       The x-coordinate position of the message.
	 * @param y       The y-coordinate position of the message.
	 * @param size    The font size of the message.
	 */

	@Override
	public void displayMessage(String message, Color color , int x , int y , int size) {
		Objects.requireNonNull(message); 
		Objects.requireNonNull(color); 
		view.displayMessage(context, message, color, x,y , size);
	}



	/**
	 * Displays the end screen with the players' scores and waits for the user to quit
	 *
	 * @param players The list of players whose scores are displayed
	 */

	@Override
	public void displayEndScreen(List<Player> players) {
		Objects.requireNonNull(players, "La liste des joueurs ne peut pas être null.");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();}
		view.drawEndScreen(context, players); 
		while (true) {
			var event = context.pollOrWaitEvent(10);
			if (event!=null) {
				switch (event) {
				case KeyboardEvent ke -> {
					if (ke.action() == KeyboardEvent.Action.KEY_PRESSED && ke.key() == KeyboardEvent.Key.Q) {
						context.dispose() ; 		}
				}
				default ->{}
				}
			}
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
		Objects.requireNonNull(shelf); 
		Objects.requireNonNull(player); 

		view.draw(context, shelf, player , this);
	}

}
