package fr.uge.cascadia.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import fr.uge.cascadia.Player;
import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.board.Shelf;
import fr.uge.cascadia.tile.HexagoTile;
import fr.uge.cascadia.tile.Tile;

/**
 * Represents the view for hexagonal tiles and manages the drawing of the game board,
 * shelf, and graphical elements on the screen for hexagonal based boards.
 *
 * @param xOrigin  The x-coordinate of the board's top-left corner.
 * @param yOrigin  The y-coordinate of the board's top-left corner.
 * @param tileSize The size of each tile in pixels.
 * @param viewUtils Utility class for rendering images and tokens.
 */

public record HexagoView(int xOrigin, int yOrigin, int tileSize , ViewUtils viewUtils   ) implements GameView {
	
	
	
	/**
     * Constructor for @code HexagoView.
     */
    public HexagoView {
    	Objects.requireNonNull(viewUtils); 
    }
	
    
    
    
    /**
     * Draws the game board for the given player, including tiles and valid positions.
     *
     * @param graphics The Graphics2D object used for drawing.
     * @param player   The player whose board is being drawn.
     */
    @Override
	public void drawBoard(Graphics2D graphics, Player player) {
		Objects.requireNonNull(graphics); 
		Objects.requireNonNull(player); 

		var grid = player.getBoard().getGrid();
		var validPositions = player.getBoard().getValidPositions();
		int hexRadius = tileSize / 2;
		for (int row = 0; row < grid.size(); row++) {
			for (int col = 0; col < grid.get(row).size(); col++) {
				Position center = getHexagonCenter(col, row);
				Color color = new Color(222, 184, 135);
				if (validPositions.contains(new Position(col, row))) {
					color = new Color(0, 255, 0, 150); 
				}
				drawHexagon(graphics, center.x(), center.y(), hexRadius, color);
				var tile = grid.get(row).get(col);
				if (tile != null) {
					drawTile(graphics, tile, center.x() - hexRadius, center.y() - hexRadius, hexRadius * 2, hexRadius * 2);
				}
			}
		}
	}



	private Position getHexagonCenter(int col, int row) {
		int hexRadius = tileSize / 2;
		int hexWidth = (int) (Math.sqrt(3) * hexRadius); 
		int centerX = xOrigin + col * hexWidth;
		int centerY = yOrigin + row * (3 * hexRadius / 2);
		if (row % 2 != 0) {
			centerX += hexWidth / 2;
		}
		return new Position(centerX, centerY);
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

	    // Dessiner les tuiles dans la colonne de gauche
	    var tiles = shelf.getTiles();
	    int j = 0;
	    for (var tile : tiles) {
	        graphics.draw(new Rectangle2D.Float(0, j+tileSize, tileSize, tileSize));
	       if (tile!=null) drawTile(graphics, tile, 0, j+tileSize, tileSize, tileSize);
	        j += tileSize;
	    }
	    // Dessiner les jetons dans la colonne de droite
	    var tokens = shelf.getTokens();
	    j = 0;
	    for (var token : tokens) {
	        if (token!=null) viewUtils.drawToken(graphics, token, tileSize , j+tileSize, tileSize, tileSize);
	        j += tileSize;
	    }
	}





	private void drawTile(Graphics2D graphics, Tile tile, float x, float y, float dimX, float dimY) {
		Objects.requireNonNull(tile);
		Objects.requireNonNull(graphics);
		viewUtils.drawImage(graphics, viewUtils.tileToImage(tile , true), x, y, dimX, dimY , ((HexagoTile) tile).getRotationIndex()*60);
		float centerX = x + dimX / 2;
		float centerY = y + dimY / 2;
		if (tile.hasAnimalToken()) {
			viewUtils.drawToken(graphics, tile.getAssignedAnimalToken(), centerX - dimX / 4, centerY - dimY / 4, dimX / 2, dimY / 2);
		} else {
			float imageWidth = dimX / 3; 
			float imageHeight = dimY / 3;
			switch (tile.getCompatibleAnimals().size()) {
			case 1 ->   viewUtils.drawImage(graphics, viewUtils.tokenToImage(new AnimalToken(tile.getCompatibleAnimals().get(0))), centerX - imageWidth / 2, centerY - imageHeight / 2, imageWidth, imageHeight , 0);       
			case 2 -> { viewUtils.drawImage(graphics, viewUtils.tokenToImage(new AnimalToken(tile.getCompatibleAnimals().get(0))), centerX - imageWidth, centerY - imageHeight / 2, imageWidth, imageHeight , 0);
			viewUtils.drawImage(graphics, viewUtils.tokenToImage(new AnimalToken(tile.getCompatibleAnimals().get(1)))    , centerX, centerY - imageHeight / 2, imageWidth, imageHeight , 0); }
			case 3 -> {
				viewUtils.drawImage(graphics, viewUtils.tokenToImage(new AnimalToken(tile.getCompatibleAnimals().get(0))), centerX - imageWidth, centerY, imageWidth, imageHeight , 0);
				viewUtils.drawImage(graphics,  viewUtils.tokenToImage(new AnimalToken(tile.getCompatibleAnimals().get(1))), centerX, centerY, imageWidth, imageHeight , 0);
				viewUtils.drawImage(graphics, viewUtils.tokenToImage(new AnimalToken(tile.getCompatibleAnimals().get(2))), centerX - imageWidth / 2, centerY - imageHeight, imageWidth, imageHeight , 0);
			}
			default -> { }}}}

	@Override
	public Position fromScreenToRealCoordinates(int x, int y, Board board) {
		Objects.requireNonNull(board); 
		int hexRadius = tileSize / 2;
		Position closestPoint = null;
		double minDistance = Double.MAX_VALUE;
		for (int row = 0; row < board.getGrid().size(); row++) {
			for (int col = 0; col < board.getGrid().get(0).size(); col++) {

				Position center = getHexagonCenter(col, row);

				double distance = Math.hypot(x - center.x(), y - center.y());

				if (distance < hexRadius && distance < minDistance) {
					minDistance = distance;
					closestPoint = new Position(col, row);
				}
			}
		}
		return closestPoint;
	}

	@Override
	public Position getCoupleFromScreen(int x, int y) {

		int realY = (y - tileSize) / tileSize;
		int realX = x / tileSize;

		return new Position(realX, realY);
	}


	private  void drawHexagon(Graphics2D graphics, int x, int y, int radius , Color color) {
		Objects.requireNonNull(graphics); 
		Objects.requireNonNull(color); 

		Polygon hexagon = createHexagon(x, y, radius);
		graphics.setColor(color); 
		graphics.fill(hexagon);

		graphics.setColor(Color.GRAY);
		graphics.draw(hexagon);
	}



	private Polygon createHexagon(int x, int y, int radius) {
		int[] xPoints = new int[6];
		int[] yPoints = new int[6];
		for (int i = 0; i < 6; i++) {
			xPoints[i] = x + (int) (radius * Math.cos(Math.toRadians(i * 60 + 30)));
			yPoints[i] = y + (int) (radius * Math.sin(Math.toRadians(i * 60 + 30)));
		}
		return new Polygon(xPoints, yPoints, 6);
	}





}
