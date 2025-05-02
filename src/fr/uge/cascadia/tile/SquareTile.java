package fr.uge.cascadia.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;
/**
 * Represents a square tile in the Cascadia game.
 * A square tile has a single habitat and supports a specific set of animals.
 *
 * @author massinissa
 */
public final class SquareTile implements Tile {
	
	/**
	 * The single habitat type associated with the tile.
	 */
	private final Habitat habitat;
	
	/**
	 * List of animals compatible with this tile.
	 */
	
	private final List<Animal> compatibleAnimals;
	
	/**
	 * The animal token currently assigned to the tile.
	 */
	private AnimalToken assignedAnimalToken;
	
	
	
	/**
	 * Specifies the tile type as square.
	 */
	private final TileType type = TileType.Square;

	
	/**
	 * creates a square tile with a given habitat.
	 * 
	 * @param habitat The habitat associated with the tile.
	 */
	
	public SquareTile(Habitat habitat) {
		Objects.requireNonNull(habitat);
		this.habitat = habitat;
		this.compatibleAnimals = createCompatibleAnimals();
	}

	
	/**
	 * Creates a list of two random animals compatible with this tile.
	 * 
	 * @return A list containing two different random animals.
	 */
	private List<Animal> createCompatibleAnimals() {
		Animal[] animals = Animal.values();
		List<Animal> selectedAnimals = new ArrayList<>();
		while (selectedAnimals.size() < 2) {
			Animal randomAnimal = animals[ThreadLocalRandom.current().nextInt(animals.length)];
			if (!selectedAnimals.contains(randomAnimal)) {
				selectedAnimals.add(randomAnimal);
			}
		}
		return selectedAnimals;
	}

	@Override
	public TileType getType() {
		return type;
	}

	@Override
	public List<Habitat> getHabitats() {
		return List.of(habitat);
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
		Objects.requireNonNull(token);
		this.assignedAnimalToken = token;

	}

	@Override
	public boolean hasAnimalToken() {
		return assignedAnimalToken != null;
	}


	@Override
	public boolean hasNatureIcon() {
		return false;
	}



	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("+-------------+\n");
		sb.append("| ").append(centerText(habitat.toString(), 11)).append(" |\n"); // Habitat

		//  compatibleAnimals
		sb.append("| ").append(centerText(compatibleAnimals.get(0).toString(), 11)).append(" |\n");
		sb.append("| ").append(centerText(compatibleAnimals.get(1).toString(), 11)).append(" |\n");

		// Jeton
		if (assignedAnimalToken != null) {
			sb.append("| ").append(centerText("token: " + assignedAnimalToken.animal().toString(), 11)).append(" |\n");
		} else {
			sb.append("| ").append(centerText("token: Aucun", 11)).append(" |\n");
		}

		sb.append("+-------------+");
		return sb.toString();
	}

	
	
	/**
	 * Centers the given text within a specified width by adding spaces on both sides.
	 *
	 * @param texte The text to be centered (not null).
	 * @param largeur The total width for centering the text.
	 * @return A centered string of the specified width
	 */

	public static String centerText(String texte, int largeur) {
		Objects.requireNonNull(texte); 
		if (texte.length() > largeur) {
			return texte.substring(0, largeur); // coupe le texte s'il est trop long
		}
		int paddingGauche = (largeur - texte.length()) / 2;
		int paddingDroite = largeur - texte.length() - paddingGauche;
		return " ".repeat(paddingGauche) + texte + " ".repeat(paddingDroite);
	}
}
