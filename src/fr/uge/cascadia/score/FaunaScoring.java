package fr.uge.cascadia.score;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.CardType;
import fr.uge.cascadia.board.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



/**
 * The `FaunaScoring` class implements the `ScoringStrategy` for scoring animals based on their assigned cards.
 * It uses a map to associate each animal with its scoring card.
 *
 *@author massinissa
 *
 */

public final class FaunaScoring implements ScoringStrategy {
    private final Map<Animal, ScoringCard> animalStrategies;
    private final Map<Animal, CardType> animalCardMap ; 
   
    
    /**
     * Constructs a new `FaunaScoring` object with the provided mapping of animals to card types.
     *
     * @param animalCardMap A map associating each animal with its corresponding card type.
     * @throws NullPointerException if the animalCardMap is null.
     */
    public FaunaScoring(Map<Animal, CardType> animalCardMap) {
        Objects.requireNonNull(animalCardMap, "La map des cartes animales ne peut pas être null.");
        this.animalStrategies = new HashMap<>();
        this.animalCardMap = animalCardMap ; 
        for (Map.Entry<Animal, CardType> entry : animalCardMap.entrySet()) {
            Animal animal = entry.getKey();
            CardType cardType = entry.getValue();

            switch (animal) {
                case Bear -> animalStrategies.put(animal, new BearScoring(cardType));
                case Fox -> animalStrategies.put(animal, new FoxScoring(cardType));
                case Salmon -> animalStrategies.put(animal, new SalmonScoring(cardType));
                case Buzzard -> animalStrategies.put(animal, new BuzzardScoring(cardType));
                case Elk -> animalStrategies.put(animal, new ElkScoring(cardType));
                default -> throw new IllegalArgumentException("Animal non pris en charge : " + animal);
            }
        }
    }

    
    
    
    /**
     * Calculates the score for a specific animal on the given board.
     *
     * @param board  The game board.
     * @param animal The animal to calculate the score for.
     * @return The score for the given animal.
     * @throws NullPointerException     if the board or animal is null.
     * @throws IllegalArgumentException if no scoring strategy is defined for the animal.
     */
    @Override
    public int calculateScore(Board board, Animal animal) {
        Objects.requireNonNull(board, "Le plateau de jeu ne peut pas être null.");
        Objects.requireNonNull(animal, "L'animal ne peut pas être null.");

        ScoringCard card = animalStrategies.get(animal);
        if (card == null) {
            throw new IllegalArgumentException("Aucune stratégie de scoring définie pour l'animal : " + animal);
        }

        return card.calculate(board);
    }
    
    
    /**
     * Getter of all the cards used by animal 
     * @return Map of animal , card 
     */
    public Map <Animal, CardType> getAnimalCardMap(){
		return Map.copyOf(animalCardMap);
    	
    }
    
}
