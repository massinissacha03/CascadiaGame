package fr.uge.cascadia.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;
/**
 * Represents a hexagonal tile in the game board.
 * Each tile has habitats, compatible animals, and can have an assigned animal token and can be ratated 
 *
 *@author massinissa
 *
 */
public final class HexagoTile implements Tile {
	/**
	 * The list of habitats present on the tile.
	 */
	private final List<Habitat> habitats;

	/**
	 * The list of animals compatible with this tile.
	 */
	private final List<Animal> compatibleAnimals;
	/**
	 * The animal token currently assigned to this tile 
	 */
	private AnimalToken assignedAnimalToken;


	/**
	 * The type of this tile, which is hexagonal.
	 */
	private final TileType type = TileType.Hexagonal;


	/**
	 * The current state of habitats after rotation.
	 */
	private List<Habitat> rotatedHabitats;

    /**
     * The rotation index of the tile which is its orientation.
     * This value changes when the tile is rotated.
     */
	private int rotationIndex = 0;

	
	
	/**
	 * creates hexagonal tile with the specified habitats and compatible animals.
	 * Validates the input and initializes the tile for rotation.
	 *
	 * @param habitats The list of habitats present on the tile (must contain 1 or 2 habitats).
	 * @param compatibleAnimals The list of animals compatible with this tile.
	 */
	
	public HexagoTile(List<Habitat> habitats, List<Animal> compatibleAnimals) {
		Objects.requireNonNull(habitats, "Habitats cannot be null");
		Objects.requireNonNull(compatibleAnimals, "Compatible animals cannot be null");

		if (habitats.size() < 1 || habitats.size() > 2) {
			throw new IllegalArgumentException("Hexagonal tiles must have one or two habitats.");
		}

		this.habitats = new ArrayList<>(habitats);
		this.compatibleAnimals = new ArrayList<>(compatibleAnimals);
		initializeRotation();


	}

	private void initializeRotation() {
		this.rotatedHabitats = calculateRotatedHabitats(0);
	}

	
	
	/**
	 * Calculates the rotated habitats for the tile based on the specified rotation index.
	 * If the tile has only one habitat, all six sides are filled with the same habitat.
	 * For two habitats, the rotation modifies their arrangement across the six sides.
	 *
	 * @param index The rotation index
	 * @return A list of habitats representing the rotated state of the tile.
	 */
	private List<Habitat> calculateRotatedHabitats(int index) {
		if (habitats.size() == 1) {
			Habitat singleHabitat = habitats.get(0);
			return List.of(singleHabitat, singleHabitat, singleHabitat, singleHabitat, singleHabitat, singleHabitat);
		}

		Habitat first = habitats.get(0);
		Habitat second = habitats.get(1);
		return switch (index % 6) {
		case 0 -> List.of(first, first, first, second, second, second);
		case 1 -> List.of(second, first, first, first, second, second);
		case 2 -> List.of(second, second, first, first, first, second);
		case 3 -> List.of(second, second, second, first, first, first);
		case 4 -> List.of(first, second, second, second, first, first);
		case 5 -> List.of(first, first, second, second, second, first);
		default -> throw new IllegalStateException("Unexpected rotation index.");
		};
	}


	/**
	 * Rotates the tile clockwise and updates habitats.
	 */
	@Override
	public void rotateClockwise() {
		rotationIndex = (rotationIndex + 1) % 6;

		rotatedHabitats = calculateRotatedHabitats(rotationIndex);

	}
	
	
	
	/**
	 * Rotates the tile counterclockwise and updates habitats.
	 */
	@Override
	public void rotateCounterClockwise() {
		rotationIndex = (rotationIndex - 1 + 6) % 6;
		rotatedHabitats = calculateRotatedHabitats(rotationIndex);

	}

	public List<Habitat> getRotatedHabitats() {
		return List.copyOf(rotatedHabitats) ;  
	}


	/**
	 * getter of the rotation index
	 * @return the index of rotation
	 */
	public int getRotationIndex() {
		return rotationIndex;
	}

	@Override
	public TileType getType() {
		return type;
	}

	@Override
	public List<Habitat> getHabitats() {
		return habitats;
	}

	@Override
	public List<Animal> getCompatibleAnimals() {
		return compatibleAnimals;
	}

	@Override
	public AnimalToken getAssignedAnimalToken() {
		return assignedAnimalToken;
	}

	@Override
	public void setAssignedAnimalToken(AnimalToken token) {
		Objects.requireNonNull(token, "Token cannot be null");
		if (assignedAnimalToken == null) {
			assignedAnimalToken = token;
		}
	}

	@Override
	public boolean hasAnimalToken() {
		return assignedAnimalToken != null;
	}



	@Override
	public boolean supportsRotation() {
		return true; 
	}



	public boolean hasNatureIcon() {
		return habitats.size() == 1 && compatibleAnimals.size() == 1 ;
	}



}
