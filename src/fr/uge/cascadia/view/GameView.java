package fr.uge.cascadia.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.controller.ControllerGraphic;
import fr.uge.cascadia.tile.Habitat;



/**
 * The `GameView` interface defines methods for rendering game elements on the screen, 
 * interacting with the graphical interface, and managing visual feedback for the player.
 *
 * Implementations include support for both hexagonal and square views of the game.
 */
public sealed interface GameView permits HexagoView, SquareView {


	/**
	 * Draws the game board for the specified player.
	 * 
	 * @param graphics the Graphics2D object used for rendering graphical elements on the screen.
	 * @param player  the player for who we draw the board
	 */
	void drawBoard(Graphics2D graphics, Player player);


	/**
	 * Draws the shelf containing tiles and tokens.
	 *
	 * @param graphics The Graphics2D context used for rendering.
	 * @param shelf    The shelf to draw.
	 */
	void drawShelf(Graphics2D graphics, Shelf shelf);


	/**
	 * Converts screen coordinates into logical shelf coordinates.
	 *
	 * @param x The x-coordinate on the screen.
	 * @param y The y-coordinate on the screen.
	 * @return The logical position on the shelf corresponding to the screen coordinates.
	 */
	Position getCoupleFromScreen(int x, int y); 




	/**
	 * Converts screen coordinates into logical board coordinates.
	 *
	 * @param x     The x-coordinate on the screen.
	 * @param y     The y-coordinate on the screen.
	 * @param board The game board.
	 * @return The logical position on the board corresponding to the screen coordinates.
	 */
	Position fromScreenToRealCoordinates(int x, int y, Board board);




	/**
	 * Displays a custom message on the screen in the specified color and position.
	 *
	 * @param context The application context for rendering.
	 * @param message The message to display.
	 * @param color   The color of the message text.
	 * @param x       The x-coordinate of the message.
	 * @param y       The y-coordinate of the message.
	 * @param size    The font size of the message.
	 */
	default void displayMessage(ApplicationContext context, String message, Color color, int x, int y , int size) {
		Objects.requireNonNull(context, "The context cannot be null.");
		Objects.requireNonNull(message, "The message cannot be null.");
		Objects.requireNonNull(color, "The color cannot be null.");

		context.renderFrame(graphics -> {
			graphics.setColor(color);
			graphics.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, size));
			graphics.drawString(message, x, y);
		});
	}





	/**
	 * Displays the end screen with final scores for all players.
	 *
	 * @param context The application context for rendering.
	 * @param players The list of players to display scores for.
	 */
	default void drawEndScreen(ApplicationContext context, List<Player> players) {
		Objects.requireNonNull(context, "The context cannot be null.");
		Objects.requireNonNull(players, "The list of players cannot be null.");
		context.renderFrame(graphics -> {
			int screenWidth = context.getScreenInfo().width();
			int screenHeight = context.getScreenInfo().height();
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, screenWidth, screenHeight);
			graphics.setColor(Color.WHITE);
			graphics.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
			graphics.drawString("Scores finaux", screenWidth / 2 - 100, 50);
			int sectionHeight = 20 * 12 + 20; 
			for (int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				int columnX = (i % 2 == 0) ? 50 : screenWidth / 2 + 50;;
				int yPosition = i<2 ?100 :100 +  2*sectionHeight  ;
				drawPlayerDetails(graphics, player, columnX, yPosition, 20); }
			graphics.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 20));
			graphics.setColor(Color.WHITE);
			graphics.drawString("Appuyez sur 'Q' pour quitter", screenWidth / 2 - 100, screenHeight - 30);
		});}



	/**
	 * Draws the details of a specific player, including their name, score, and token details.
	 *
	 * @param graphics   The Graphics2D context used for rendering.
	 * @param player     The player whose details are being drawn.
	 * @param x          The x-coordinate for the player details.
	 * @param y          The y-coordinate for the player details.
	 * @param lineHeight The height between each line of text.
	 */
	private void drawPlayerDetails(Graphics2D graphics, Player player, int x, int y, int lineHeight) {
		graphics.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
		graphics.drawString("Joueur : " + player.getName() + "  " +player.getSurname() + "  "  +" 			score total: " +player.getScore().getTotalPoints(), x, y);
		y += lineHeight;
		graphics.drawString("Scores des animaux :", x, y);
		y += lineHeight;
		for (Map.Entry<Animal, Integer> entry : player.getScore().getAnimalScores().entrySet()) {
			graphics.drawString("- " + entry.getKey() + " : " + entry.getValue() + " points", x + 20, y);
			y += lineHeight;
		}
		graphics.drawString("Scores des habitats :", x, y);
		y += lineHeight;
		for (Map.Entry<Habitat, Integer> entry : player.getScore().getHabitatScores().entrySet()) {
			graphics.drawString("- " + entry.getKey() + " : " + entry.getValue() + " points", x + 20, y);
			y += lineHeight;
		}
		graphics.drawString("Points de bonus : " + player.getScore().getBonusPoints() + " points", x, y);
		y += lineHeight;
		graphics.drawString("Jetons nature non utilisés : " + player.getBoard().getNatureTokens() + " points", x, y);
	}


	/**
	 * Draws the game screen including the board and shelf for the specified player.
	 *
	 * @param context The application context for rendering.
	 * @param shelf   The shelf to render.
	 * @param player  The player whose board and shelf are displayed.
	 * @param game    The controller managing the game's graphical logic.
	 */
	default void draw(ApplicationContext context, Shelf shelf, Player player , ControllerGraphic game ) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(shelf);
		Objects.requireNonNull(player);
		Objects.requireNonNull(game);
		var screenInfo = context.getScreenInfo();
		var width = screenInfo.width();
		var height = screenInfo.height();

		context.renderFrame(graphics -> graphics.fillRect(0, 0, width, height));

		displayMessage(context , "Plateau de : " , Color.white , 50 , 15 , 15); 
		displayMessage(context , player.getName() , Color.white , 50 , 30 , 15); 
		displayMessage(context , "jetons nature : "+ player.getBoard().getNatureTokens() , Color.red , 600 , 15 , 14); 

		context.renderFrame(graphics -> drawBoard(graphics, player));
		context.renderFrame(graphics -> drawShelf(graphics, shelf));
	}
}
