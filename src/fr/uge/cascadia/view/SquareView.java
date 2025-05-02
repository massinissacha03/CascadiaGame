package fr.uge.cascadia.view;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.tile.Tile;




/**
 * Represents the view for square tiles and manages the drawing of the game board,
 * shelf, and graphical elements on the screen for square-based boards.
 *
 * @param xOrigin  The x-coordinate of the board's top-left corner.
 * @param yOrigin  The y-coordinate of the board's top-left corner.
 * @param tileSize The size of each tile in pixels.
 * @param viewUtils Utility class for rendering images and tokens.
 */
public record SquareView(int xOrigin, int yOrigin, int tileSize , ViewUtils viewUtils) implements GameView {

	/**
	 * Constructor for SquareView.

	 */
	public SquareView {
		Objects.requireNonNull(viewUtils); 
	}


	/**
	 * Draws the game board for a player, including tiles and valid positions.
	 *
	 * @param graphics The Graphics2D object used for drawing.
	 * @param player The player whose board is being drawn.
	 * @throws NullPointerException if graphics or player is null.
	 */
	@Override
	public void drawBoard(Graphics2D graphics, Player player) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(graphics);

		drawBoardBackground(graphics, player);
		highlightValidPositions(graphics, player);
		drawInsertedTiles(graphics, player);
	}




	/**
	 * Draws the background of the game board
	 *
	 * @param graphics The Graphics2D object used for drawing.
	 * @param player The player whose board background is being drawn.
	 */
	private void drawBoardBackground(Graphics2D graphics, Player player) {
		var grid = player.getBoard().getGrid();
		int boardHeight = grid.size(); // nb de lignes
		int boardWidth = grid.get(0).size(); // nb de colonnes
		graphics.setColor(Color.WHITE);
		graphics.fillRect(xOrigin, yOrigin, boardWidth * tileSize, boardHeight * tileSize);

		for (int row = 0; row < boardHeight; row++) {
			for (int col = 0; col < boardWidth; col++) {
				int x = xOrigin + col * tileSize;
				int y = yOrigin + row * tileSize;
				graphics.setColor(Color.BLACK);
				graphics.fill(new Rectangle2D.Float(x, y, tileSize, tileSize));
				graphics.setColor(Color.WHITE);
				graphics.draw(new Rectangle2D.Float(x, y, tileSize, tileSize));
			}
		}
	}



	/**
	 * Highlights the valid positions on the game board where tiles can be placed.
	 *
	 * @param graphics The Graphics2D object used for drawing.
	 * @param player 
	 */
	private void highlightValidPositions(Graphics2D graphics, Player player) {
		var validPositions = player.getBoard().getValidPositions();

		for (Position position : validPositions) {
			int x = xOrigin + position.x() * tileSize;
			int y = yOrigin + position.y() * tileSize;
			graphics.setColor(new Color(255, 0, 0, 50)); // Couleur pour cases valides
			graphics.fill(new Rectangle2D.Float(x, y, tileSize, tileSize));
		}
	}
	
	
	/**
	 * Draws the tiles placed on the player's board.
	 *
	 * @param graphics The Graphics2D object used for drawing.
	 * @param player The player whose tiles are being drawn.
	 */
	private void drawInsertedTiles(Graphics2D graphics, Player player) {
		var tiles = player.getBoard().getInsertedTiles();

		for (var tile : tiles.keySet()) {
			int x = xOrigin + tiles.get(tile).x() * tileSize;
			int y = yOrigin + tiles.get(tile).y() * tileSize;
			drawTile(graphics, tile, x, y, tileSize, tileSize);
		}
	}


	
	
	/**
    * Draws the shelf for the player, including tiles and tokens.
    *
    * @param graphics The Graphics2D object used for drawing.
    * @param shelf The shelf containing tiles and tokens.
    */
	@Override
	public void drawShelf(Graphics2D graphics, Shelf shelf) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(shelf);
		graphics.setColor(Color.WHITE);
		var tiles = shelf.getTiles();
		int j = 0;
		for (var tile : tiles) {
			graphics.draw(new Rectangle2D.Float(0, j+tileSize, tileSize, tileSize));
			if (tile!=null) drawTile(graphics, tile, 0, j+tileSize, tileSize, tileSize);
			j += tileSize;
		}
		var tokens = shelf.getTokens();
		j = 0;
		for (var token : tokens) {
			if (token!=null) viewUtils.drawToken(graphics, token, tileSize , j+tileSize, tileSize, tileSize);
			j += tileSize;
		}
	}


	 /**
     * Draws a single tile on the board, including any associated animal tokens.
     *
     * @param graphics The Graphics2D object used for drawing.
     * @param tile The tile to be drawn.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param dimX The width of the tile in pixels.
     * @param dimY The height of the tile in pixels.
     */
	private void drawTile(Graphics2D graphics, Tile tile, float x, float y, float dimX, float dimY) {
		Objects.requireNonNull(tile);
		Objects.requireNonNull(graphics);

		BufferedImage tileImage = viewUtils.tileToImage(tile, false);
		viewUtils.drawImage(graphics, tileImage, x, y, dimX, dimY, 0);

		float centerX = x + dimX / 2;
		float centerY = y + dimY / 2;

		if (tile.hasAnimalToken()) {
			viewUtils.drawToken(graphics, tile.getAssignedAnimalToken(), centerX - dimX / 4, centerY - dimY / 4, dimX / 2, dimY / 2);
		} else {
			List<Animal> compatibleAnimals = tile.getCompatibleAnimals();
			if (compatibleAnimals.size() == 2) {
				BufferedImage animalImage1 = viewUtils.tokenToImage(new AnimalToken(compatibleAnimals.get(0)));
				BufferedImage animalImage2 = viewUtils.tokenToImage(new AnimalToken(compatibleAnimals.get(1)));
				viewUtils.drawImage(graphics, animalImage1, centerX - dimX / 2, centerY - dimY / 4, dimX / 3, dimY / 3, 0);
				viewUtils.drawImage(graphics, animalImage2, centerX + dimX / 6, centerY - dimY / 4, dimX / 3, dimY / 3, 0);
			}}}


	@Override
	public Position getCoupleFromScreen(int x, int y) {

		int realY = (y - tileSize) / tileSize;
		int realX = x / tileSize;

		return new Position(realX, realY);
	}


	@Override
	public Position fromScreenToRealCoordinates(int x, int y, Board board) {
		return new Position((x-xOrigin)/tileSize , (y-yOrigin)/tileSize) ; 
	}


}