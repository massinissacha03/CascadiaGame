package fr.uge.cascadia.animal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/**
 * Represents an animal token in the game, encapsulating an {@link Animal} type.
 * @param animal is an animal contained in the token
 */
public record AnimalToken(Animal animal) {


	/**
	 * Creates an Animal Token
	 */
	public AnimalToken{
		Objects.requireNonNull(animal);
	}



	/**
	 * Creates a bag of animal tokens.
	 * 
	 * Generates a list of 100 tokens with predefined proportions of animals
	 * and shuffles it for random selection.
	 * 
	 * @return A shuffled list of animal tokens.
	 */

	public static List <AnimalToken> createBagOfAnimalTokens() {
		List <AnimalToken> BagOfAnimalTokens = new ArrayList <> ();

		int i = 0;
		while (i != 100) {
			if (i < 20) {
				BagOfAnimalTokens.add(new AnimalToken(Animal.Elk));

			} else if (i < 40 && i >= 20) {
				BagOfAnimalTokens.add(new AnimalToken(Animal.Buzzard));

			} else if (i < 60 && i >= 40) {
				BagOfAnimalTokens.add(new AnimalToken(Animal.Fox));
			} else if (i < 80 && i >= 60) {
				BagOfAnimalTokens.add(new AnimalToken(Animal.Bear));
			} else {
				BagOfAnimalTokens.add(new AnimalToken(Animal.Salmon));
			}

			i++;

		}
		Collections.shuffle(BagOfAnimalTokens); 

		return BagOfAnimalTokens;

	}

	@Override
	public String toString() {
		return "token : " + animal;
	}

}