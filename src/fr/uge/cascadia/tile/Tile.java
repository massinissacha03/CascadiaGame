package fr.uge.cascadia.tile;
import java.util.List;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;

import java.nio.file.Files;
import java.nio.file.Path;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;



/**
 * Represents a tile in the game board.
 * Tiles can be Hexagonal or Square and are used to place animals and habitats.
 *
 * @author massinissa
 *
 */
public sealed interface Tile permits HexagoTile, SquareTile {
	/**
	 * Getter of TileType
	 * @return type of the tile
	 */
	public TileType getType();
	/**
	 * Getter of the habitats of the tile
	 * @return habitats
	 */
	public List<Habitat> getHabitats();
	/**
	 * Getter of the compatible animals with the tile 
	 * @return Compatible Animals with the tile 
	 */
	public List<Animal> getCompatibleAnimals();
	/**
	 * Getter of the assigned token to the tile
	 * @return assigned animal token 
	 */
	public AnimalToken getAssignedAnimalToken();
	/**
	 * Sets the animalToken in the tile
	 * @param token the animal token that is getting assigned to the tile
	 */
	public void setAssignedAnimalToken(AnimalToken token);
	/**
	 * checks if the tile has a token
	 * 
	 * @return true if the tile has an animal token , false if not
	 */

	public boolean hasAnimalToken();

	/**
	 * Creates a bag of tiles based on the specified tile type.
	 *
	 * @param type The type of the tile to create, either square or hexagonal.
	 * @return A list of {@link Tile} objects, shuffled and ready for gameplay.
	 */
	public static List<Tile> createTileBag(TileType type) {
		List<Tile> tileBag;

		if (type == TileType.Square) {
			tileBag = createSquareTileBag();
		} else if (type == TileType.Hexagonal) {
			tileBag = createHexagonalTileBag("hexagoTilesFile.txt");
		} else {
			throw new IllegalArgumentException("Unsupported tile type: " + type); 
		}

		Collections.shuffle(tileBag); //on mélange le sac
		return tileBag;
	}

	/**
	 * Creates a bag of square tiles.
	 * Each habitat type is equally represented, ensuring even distribution.
	 *
	 * @return A list of square {@link Tile} objects.
	 */
	private static List<Tile> createSquareTileBag() {
		List<Tile> tileBag = new ArrayList<>();
		Habitat[] habitats = Habitat.values();
		int tilesPerHabitat = 100 / habitats.length;

		for (Habitat habitat : habitats) {
			for (int i = 0; i < tilesPerHabitat; i++) {
				tileBag.add(new SquareTile(habitat));
			}
		}

		return tileBag;
	}

	/**
	 * Creates a bag of hexagonal tiles from a file.
	 *
	 * @param filePath The file containing tile data.
	 * @return A list of hexagonal {@link Tile} objects.
	 */

	public static List<Tile> createHexagonalTileBag(String filePath) {
		List<Tile> tileBag = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(Path.of(filePath)); 
			int lineNumber = 0; 
			for (String line : lines) {
				lineNumber++;
				line = line.trim(); 
				if (line.isEmpty()) continue; 
				try {	String[] parts = line.split(" ");
				int numHabitats = Integer.parseInt(parts[0]);
				List<Habitat> habitats = new ArrayList<>();
				for (int i = 0; i < numHabitats; i++)  habitats.add(Habitat.valueOf(parts[1 + i]));
				int numAnimals = Integer.parseInt(parts[1 + numHabitats]);
				List<Animal> compatibleAnimals = new ArrayList<>();
				for (int i = 0; i < numAnimals; i++) compatibleAnimals.add(Animal.valueOf(parts[2 + numHabitats + i]));
				Tile tile = new HexagoTile(habitats, compatibleAnimals);
				tileBag.add(tile);
				} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
					System.err.println("Erreur de format à la ligne " + lineNumber + ": " + line); }}
		} catch (IOException e) { System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());}
		return tileBag; // je ne mélange pas le sac pour avoir les vraies tuiles pour l'initialisation
	}




	/**

	 * Returns a string representation of a null tile that we need in board.
	 * 
	 * @return a string representing the null tile.
	 */
	public static String printTuileNull() {
		StringBuilder sb = new StringBuilder();

		sb.append("+-------------+\n");

		// Habitat vide
		sb.append("| ").append(SquareTile.centerText("Vide", 11)).append(" |\n");

		//  compatibleAnimal vides
		sb.append("| ").append(SquareTile.centerText("Vide", 11)).append(" |\n");
		sb.append("| ").append(SquareTile.centerText("Vide", 11)).append(" |\n");

		// Jeton vide
		sb.append("| ").append(SquareTile.centerText("token: Vide", 11)).append(" |\n");

		sb.append("+-------------+");

		return sb.toString();
	}


	/**
	 * Only the Hexagonal tile supports Rotation
	 * @return false 
	 */
	default boolean supportsRotation() {
		return false; 
	}

	/**
	 * Only the Hexagonal tile supports Rotation
	 * Rotates the tile clockwise.  
	 * @throws UnsupportedOperationException If rotation is not supported.
	 */

	default void rotateClockwise() {
		throw new UnsupportedOperationException("This tile does not support rotation");
	}


	/**
	 * Only the Hexagonal tile supports Rotation
	 * Rotates the tile clockwise.  
	 * @throws UnsupportedOperationException If rotation is not supported.
	 */

	default void rotateCounterClockwise() {
		throw new UnsupportedOperationException("This tile does not support rotation");
	}

	/**
	 * checks if a tile is (ideal) which means it has a nature icon 
	 * @return true if yes , false if not  
	 */
	boolean hasNatureIcon();
	/**
	 * Only the Hexagonal tile supports Rotation
	 * Gets the habitats state of the tile after rotation 
	 * @return list of habitats state of the tile after rotation in a
	 * @throws UnsupportedOperationException If rotation is not supported.

	 */
	default List<Habitat> getRotatedHabitats(){
		throw new UnsupportedOperationException("This tile does not support rotation");

	}



}

