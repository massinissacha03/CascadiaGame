package fr.uge.cascadia.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.cascadia.Position;
import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.tile.Habitat;
import fr.uge.cascadia.tile.SquareTile;
import fr.uge.cascadia.tile.Tile;
import fr.uge.cascadia.tile.TileType;

/**
 * The Board class represents the game board for a player.
 * It manages the grid of tiles and tracks the tiles that have been placed.
 * This version handles both square and hexagonal tiles.
 * 
 * @author Massinissa
 */
public class Board {
	/**
	 * Represents the grid of tiles that make up the game board.
	 */
	private final ArrayList<ArrayList<Tile>> grid;

	/**
	 * The initial size of the board, used to create the grid.
	 */
	private final int initialSize;

	/**
	 * A map linking inserted tiles to their respective positions on the board.
	 */
	private final Map<Tile, Position> insertedTiles;

	/**
	 * Specifies the type of tiles used on the board (Square or Hexagonal).
	 */
	private final TileType type;

	/**
	 * Tracks the number of nature tokens currently available for the player.
	 */
	private int natureTokens;

	/**
	 * Tracks the total number of nature tokens gained by the player during the game.
	 */
	private int gainedNatureTokens;


	/**
	 * Constructs a new game board with a specified size and tile type.
	 *
	 * @param size The size of the board grid. Must be between 1 and 20.
	 * @param type The type of tiles to be used on the board (Square or Hexagonal).
	 * @throws IllegalArgumentException If the size is not a positive integer or exceeds 20.
	 * @throws NullPointerException If the tile type is null.
	 */

	public Board(int size, TileType type ) {
		if (size <= 0 || size > 20) {
			throw new IllegalArgumentException("size must be a positive integer and less than or equal to 5.");
		}
		Objects.requireNonNull(type, "TileType cannot be null");

		this.initialSize = size;
		this.type = type;
		this.grid = new ArrayList<>();
		this.insertedTiles = new HashMap<>();

		// Initialize the grid with null tiles
		for (int i = 0; i < size; i++) {
			ArrayList<Tile> row = new ArrayList<>();
			for (int j = 0; j < size; j++) {
				row.add(null);
			}
			grid.add(row);
		}
	}

	/**
	 * Gets the grid of the board.
	 * 
	 * @return the grid as an ArrayList of ArrayLists of Tiles.
	 */
	public ArrayList<ArrayList<Tile>> getGrid() {
		return grid;
	}

	/**
	 * Gets the map of inserted tiles and their positions.
	 * 
	 * @return a map with tiles as keys and their positions as values.
	 */
	public Map<Tile, Position> getInsertedTiles() {
		return insertedTiles;
	}

	/**
	 * Initializes the board by placing three initial tiles in the center of the grid.
	 *
	 * @param initialTilesIndex The index of the first tile to use from the bag of initial tiles.
	 * */
	public void initializeBoard(int initialTilesIndex) {
		List<Tile> initialTiles = new ArrayList<>(); 
		if (type.equals(TileType.Hexagonal)) {
			initialTiles = Tile.createHexagonalTileBag("initialTiles.txt"); 
		} 

		Tile t1 = type == TileType.Hexagonal ? initialTiles.get(initialTilesIndex) : new SquareTile(Habitat.Forests);
		Tile t2 = type == TileType.Hexagonal ? initialTiles.get(initialTilesIndex + 1) : new SquareTile(Habitat.Wetlands);
		Tile t3 = type == TileType.Hexagonal ? initialTiles.get(initialTilesIndex + 2) : new SquareTile(Habitat.Mountains);
		grid.get(initialSize / 2).set(initialSize / 2, t1);
		grid.get(initialSize / 2 + 1).set(initialSize / 2 - 1, t2);
		grid.get(initialSize / 2 + 1).set(initialSize / 2, t3);
		insertedTiles.put(t1, new Position(initialSize / 2, initialSize / 2));
		insertedTiles.put(t2, new Position(initialSize / 2 - 1, initialSize / 2 + 1));
		insertedTiles.put(t3, new Position(initialSize / 2, initialSize / 2 + 1));

	}





	/**
	 * Verifies if a position is within the bounds of the grid.
	 * 
	 * @param p the position to verify.
	 * @return true if the position is valid, false otherwise.
	 */
	public boolean isInBounds(Position p) {
		Objects.requireNonNull(p, "Point cannot be null");

		return p.y() >= 0 && p.y() < grid.size() && p.x() >= 0 && p.x() < grid.get(p.y()).size();
	}

	/**
	 * Retrieves a list of valid positions on the board where a new tile can be placed.
	 * A position is considered valid if:
	 * - It is adjacent to at least one already inserted tile.
	 * - It is not already occupied by another tile.
	 * - It is within the bounds of the board.
	 *
	 * @return A list of valid positions where a tile can be placed.
	 */
	public List<Position> getValidPositions() {
		List<Position> validPositions = new ArrayList<>();

		for (Position point : insertedTiles.values()) {
			List<Position> adjacentPoints = type == TileType.Hexagonal ? getHexagonalNeighbors(point) : getSquareNeighbors(point);

			for (var pos : adjacentPoints) {
				if (!insertedTiles.containsValue(pos) && !validPositions.contains(pos) && isInBounds(pos)) {
					validPositions.add(pos);
				}
			}
		}

		return validPositions;
	}


	/**
	 * Retrieves the neighboring positions of a given position on the board.
	 * The type of neighbors (hexagonal or square) depends on the tile type of the board.
	 *
	 * @param position The position for which neighbors are to be retrieved. Must not be null.
	 * @return A list of neighboring positions around the given point.
	 * @throws NullPointerException If the point is null.
	 */
	public List<Position> getNeighbors(Position position) {
		Objects.requireNonNull(position, "Le point ne peut pas être null.");

		return type == TileType.Hexagonal 
				? getHexagonalNeighbors(position) 
						: getSquareNeighbors(position);
	}





	/**
	 * Retrieves the neighboring positions for a square grid.
	 * The neighbors are the positions directly adjacent to the given position
	 * (up, down, left, right).
	 *
	 * @param position The central position for which neighbors are to be retrieved. Must not be null.
	 * @return A list of positions representing the neighbors in a square grid.
	 * @throws NullPointerException If the given position is null.
	 */
	public static List<Position> getSquareNeighbors(Position position) {
		Objects.requireNonNull(position); 
		return List.of(
				new Position(position.x() + 1, position.y()),
				new Position(position.x() - 1, position.y()),
				new Position(position.x(), position.y() + 1),
				new Position(position.x(), position.y() - 1)
				);
	}

	/**
	 * Retrieves the neighboring positions for a hexagonal grid.
	 * The neighbors are determined based on the current position in the grid
	 * and depend on whether the row number is even or odd.
	 *
	 * @param position The central position for which neighbors are to be retrieved. Must not be null.
	 * @return A list of positions representing the neighbors in a hexagonal grid.
	 * @throws NullPointerException If the given point is null.
	 */
	public static List<Position> getHexagonalNeighbors(Position position) {
		Objects.requireNonNull(position); 
		int col = position.x();
		int row = position.y();
		List<Position> neighbors = new ArrayList<>();
		neighbors.add(new Position(col, row - 1)); // Haut
		neighbors.add(new Position(col, row + 1)); // Bas
		neighbors.add(new Position(col - 1, row)); // Gauche
		neighbors.add(new Position(col + 1, row)); // Droite

		if (row % 2 == 0) { 
			neighbors.add(new Position(col - 1, row - 1)); // haut gauche
			neighbors.add(new Position(col - 1, row + 1)); // bas gauche
		} else {
			neighbors.add(new Position(col + 1, row - 1)); //  haut droite 
			neighbors.add(new Position(col + 1, row + 1)); //  bas gauche
		}
		return neighbors;
	}

	/**
	 * Determines if a specific position on the board is free (unoccupied).
	 * A position is considered free if it is within the grid bounds and
	 * does not already contain a tile.
	 *
	 * @param p The position to check. Must not be null.
	 * @return {@code true} if the position is within bounds and unoccupied, {@code false} otherwise.
	 * @throws NullPointerException If the provided position is null.
	 */
	private boolean isFreeCell(Position p) {
		Objects.requireNonNull(p, "posision cannot be null");
		return isInBounds(p) && grid.get(p.y()).get(p.x()) == null;
	}




	/**
	 * Places a tile on the board at the specified position.
	 * Expands the grid size if necessary after the tile is inserted.
	 *
	 * @param p The position to place the tile. Must not be null.
	 * @param tile The tile to insert. Must not be null.
	 * @return {@code true} if the tile was successfully inserted, {@code false} otherwise.
	 * @throws NullPointerException If the position or tile is null.
	 */
	public boolean insertTile(Position p, Tile tile) {
		Objects.requireNonNull(p, "position cannot be null");
		Objects.requireNonNull(tile, "Tile cannot be null");

		if (isFreeCell(p) && hasAdjacentTile(p)) {

			// Insert the tile into the grid
			grid.get(p.y()).set(p.x(), tile);
			insertedTiles.put(tile, p); // Add the tile to the map of inserted tiles

			changeGridSize(); //changerla taille de la grille s'il le faut 
			return true;
		} else {
			System.out.println(" Insertion of tile failed, try again at position " + p);
			return false;
		}
	}



	/**
	 * Increments the number of Nature Tokens for the player.
	 * Also increments the count of gained Nature Tokens used success tracking.
	 */
	public void addNatureToken() {
		natureTokens+=1; 
		gainedNatureTokens+=1 ; 

	}



	/**
	 * Decrements the number of Nature Tokens if the current count is greater than zero.
	 */
	public void subtractNatureToken() {
		if (natureTokens > 0) {
			natureTokens--;
		}

	}



	/**
	 * Returns the current number of Nature Tokens the player has.
	 *
	 * @return The number of Nature Tokens.
	 */
	public int getNatureTokens() {
		return natureTokens;
	}



	/**
	 * Returns the number of Nature Tokens gained.
	 *
	 * @return The number of Nature Tokens.
	 */
	public int getGainedNatureTokens() {
		return gainedNatureTokens;
	}

	/**
	 * Checks if a position has at least one adjacent tile.
	 * 
	 * @param p the position to check.
	 * @return true if there is an adjacent tile, false otherwise.
	 */
	private boolean hasAdjacentTile(Position p) {
		Objects.requireNonNull(p, "position cannot be null");
		List<Position> neighbors = type == TileType.Hexagonal ? getHexagonalNeighbors(p) : getSquareNeighbors(p);

		for (Position neighbor : neighbors) {
			if (isInBounds(neighbor) && grid.get(neighbor.y()).get(neighbor.x()) != null) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Inserts a token at a given position on the board.
	 * 
	 * @param p the position where the token should be placed.
	 * @param token the token to insert.
	 * @return true if the token was successfully inserted, false otherwise.
	 */
	public boolean insertToken(Position p, AnimalToken token) {
		Objects.requireNonNull(p, "position cannot be null");
		Objects.requireNonNull(token, "Token cannot be null");

		if (isInBounds(p)) {
			Tile tile = grid.get(p.y()).get(p.x());
			if (tile != null && tile.getCompatibleAnimals().contains(token.animal()) && !tile.hasAnimalToken()) {
				tile.setAssignedAnimalToken(token);
				System.out.println("Token inserted successfully at position: " + p);
				if (tile.hasNatureIcon()) {
					addNatureToken(); 
				}
				return true;
			}
		}
		System.err.println("Token insertion failed at position: " + p);
		return false;
	}

	/**
	 * Checks if there is at least one free place on the board for a given token.
	 *
	 * @param token the token to check.
	 * @return true if there is at least one free place for the token, false otherwise.
	 */
	public boolean freePlaceForToken(AnimalToken token) {
		Objects.requireNonNull(token, "Token cannot be null");

		for (Tile tile : insertedTiles.keySet()) {
			// Check if the tile is compatible with the token's animal and does not already have a token assigned
			if (tile.getCompatibleAnimals().contains(token.animal()) && !tile.hasAnimalToken()) {
				return true;
			}
		}
		return false;
	}





	/**
	 * Makes sure there is no tile in the borders by changing the dimensions of the board
	 * 
	 */
	private void changeGridSize() {
		for (var j: grid.get(0)) {
			if (j != null) {
				addRowTop(); // haut 
				addRowTop(); // haut       //j'ajoute deux fois pour garder la parité des des position
				return; }}
		for (var j: grid.get(grid.size() - 1)) {
			if (j != null) {
				addRowBottom(); // bas 
				addRowBottom(); // bas 
				return; }}
		for (var row: grid) {
			if (row.get(0) != null) {
				addColumnLeft(); // gauche
				addColumnLeft(); // gauche
				return; }}
		for (var row: grid) {
			if (row.get(row.size() - 1) != null) {
				addColumnRight(); // droite 
				addColumnRight(); // droite 
				return;		}} }




	/**
	 * Add a column in the left of the grid
	 * 
	 */
	private void addColumnLeft() {
		for (ArrayList < Tile > row: grid) {
			row.add(0, null);
		}
		// we must change the positions of every tile inserted in the board (x+1)
		insertedTiles.forEach((key, value) -> insertedTiles.put(key, new Position(value.x() + 1, value.y())));
	}
	/**
	 * Add a column in the left of the grid
	 * 
	 */
	private void addColumnRight() {
		for (ArrayList < Tile > row: grid) {
			row.add(null);
			// no need to change any position 
		}
	}
	/**
	 * Add a line in the top of the grid
	 * 
	 */
	private void addRowTop() {
		ArrayList < Tile > newRow = new ArrayList < > ();
		for (int i = 0; i < grid.get(0).size(); i++) {
			newRow.add(null);
		}
		grid.add(0, newRow);
		// we must change the positions of every tile inserted in the board (y+1)
		insertedTiles.forEach((key, value) -> insertedTiles.put(key, new Position(value.x(), value.y() + 1)));
	}

	/**
	 * Add a line in the bottom of the grid
	 * 
	 */
	private void addRowBottom() {
		ArrayList < Tile > newRow = new ArrayList < > ();
		for (int i = 0; i < grid.get(0).size(); i++) {
			newRow.add(null);
		}
		// no need to change any position 
		grid.add(newRow);
	}

	/**
	 * Displays the board in the terminal. Square tiles are displayed; hexagonal tiles are skipped.
	 */
	public void displayBoard() {
		if (type == TileType.Hexagonal ) return ; 
		for (ArrayList<Tile> row : grid) {
			StringBuilder[] lines = new StringBuilder[6];
			for (int l = 0; l < 6; l++) {
				lines[l] = new StringBuilder();
			}
			for (Tile tile : row) {
				String[] tileLines;
				if (tile != null) {
					tileLines = tile.toString().split("\n");
				} else {
					tileLines = Tile.printTuileNull().split("\n");
				}
				for (int l = 0; l < 6; l++) {
					lines[l].append(tileLines[l]).append(" ");
				}
			}
			for (StringBuilder line : lines) { System.out.println(line);	}
		}	}


	/**
	 * Getter of the tileType used 
	 * @return type , the type of the Tiles in the board 
	 */
	public TileType getType() {
		return type;
	}



	/**
	 * Recursively searches for the largest connected group of tiles that have the same animal token.
	 *
	 * @param x       The x-coordinate of the starting position.
	 * @param y       The y-coordinate of the starting position.
	 * @param animal  The animal to search for in the group.
	 * @param visited A 2D boolean array to track visited positions.
	 * @return The size of the largest connected group of tiles containing the specified animal.
	 */
	public int searchBiggestGroup(int x, int y, Animal animal, boolean[][] visited ) {
		if (y < 0 || y >= grid.size() || x < 0 || x >= grid.get(y).size() || visited[y][x]) {
			return 0;
		}
		Tile tile = grid.get(y).get(x);
		if (tile == null || !tile.hasAnimalToken() || tile.getAssignedAnimalToken().animal() != animal) {
			return 0;
		}
		visited[y][x] = true;
		return 1 +
				searchBiggestGroup(x + 1, y, animal, visited) +
				searchBiggestGroup(x - 1, y, animal, visited) +
				searchBiggestGroup(x, y + 1, animal, visited) +
				searchBiggestGroup(x, y - 1, animal, visited);
	}


	/**
	 * Counts the number of tiles on the board that have a token assigned to the specified animal.
	 *
	 * @param animal The animal to count on the board (must not be null).
	 * @return The total number of tiles containing a token of the specified animal.
	 * @throws NullPointerException If the provided animal is null.
	 */

	public int getAnimalCount(Animal animal) {
		Objects.requireNonNull(animal, "L'animal ne peut pas être null.");
		int count = 0;

		for (List<Tile> row : grid) { 
			for (Tile tile : row) { 
				if (tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == animal) {
					count++;
				}
			}
		}

		return count;
	}



	/**
	 * Checks if two specific animals are adjacent to each other on the board.
	 *
	 * @param board   The game board to check 
	 * @param animal1 The first animal to check 
	 * @param animal2 The second animal to check 
	 * @return {@code true} if the two animals are adjacent on the board, {@code false} otherwise.
	 * @throws NullPointerException If the board, animal1, or animal2 is null.
	 */

	public static boolean areAnimalsAdjacent(Board board, Animal animal1, Animal animal2) {
		Objects.requireNonNull(board); 
		Objects.requireNonNull(animal1); 
		Objects.requireNonNull(animal2); 
		var grid = board.getGrid();
		for (int row = 0; row < grid.size(); row++) {
			for (int col = 0; col < grid.get(row).size(); col++) {
				Tile currentTile = grid.get(row).get(col);
				if (currentTile == null) 		continue;
				if (isTokenAssignedToAnimal(currentTile, animal1) && isTokenAssignedToAnimal(currentTile, animal2)) {
					return true; }
				List<Position> neighbors = Board.getHexagonalNeighbors(new Position(col, row));
				for (Position neighbor : neighbors) {
					if (!board.isInBounds(neighbor)) continue;
					Tile neighborTile = grid.get(neighbor.y()).get(neighbor.x());
					if (neighborTile == null) continue;
					if ((isTokenAssignedToAnimal(currentTile, animal1) && isTokenAssignedToAnimal(neighborTile, animal2)) ||
							(isTokenAssignedToAnimal(currentTile, animal2) && isTokenAssignedToAnimal(neighborTile, animal1))) {
						return true; // Les animaux sont adjacents sur des tuiles voisines
					}}}}
		return false;}

	
	
	
	private static boolean isTokenAssignedToAnimal(Tile tile, Animal animal) {
		return tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == animal;
	}






}
