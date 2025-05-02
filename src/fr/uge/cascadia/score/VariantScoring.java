package fr.uge.cascadia.score;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.tile.Tile;

import java.util.ArrayList;
import java.util.Objects;
/**
 * The VariantScoring class calculates the score of a specific variant for a given animal.
 * It supports different scoring methods based on the variant type.
 * the variant can be (famille / intermediaire) 
 *
 * @param variant variant card of scoring 
 */
public record  VariantScoring (String variant) implements ScoringStrategy {


	
	
	/**
	 * Creates a variant scoring card
	 */
	public VariantScoring {
		Objects.requireNonNull(variant); 
	    if (!variant.equals("famille") && !variant.equals("intermediaire") ) {
	        throw new IllegalArgumentException("Invalid variant: " + variant + ". Only 'famille' and 'intermediaire' is supported.");
	    }
		
		
	}
	
	
	
	
	

    /**
     * Calculates the score for a specific animal on the board with the specified variant.
     *
     * @param board the board in which we calculate the score 
     * @param animal the animal for which we calculate the score 
     * @return The calculated score for the animal.
     */
    @Override
    public int calculateScore(Board board, Animal animal ) {
        Objects.requireNonNull(board); 
        Objects.requireNonNull(animal); 
    	int score = 0;
        ArrayList<ArrayList<Tile>> grid = board.getGrid();
        boolean[][] visited = new boolean[grid.size()][];
        for (int i = 0; i < grid.size(); i++) {
            visited[i] = new boolean[grid.get(i).size()];    }
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(y).size(); x++) {
                Tile tile = grid.get(y).get(x);
                if (!visited[y][x] && tile != null && tile.hasAnimalToken() && tile.getAssignedAnimalToken().animal() == animal) {
                    int groupSize = board.searchBiggestGroup(x, y, animal, visited);
                    score += assignVariantPoint(groupSize , variant);
                }
            }
        }
        return score;
    }
   
    private int assignVariantPoint(int groupSize  , String variant){
    	Objects.requireNonNull(variant) ; 
    	return variant.equals("famille") ? assignFamilyPoints(groupSize) : assignIntermediatePoints(groupSize) ; 
    	
    }

    private int assignIntermediatePoints(int groupSize) {
        return switch (groupSize) {
            case 2 -> 5;
            case 3 -> 8;
            default -> groupSize >= 4 ? 12 : 0;
        };
    }

    private int assignFamilyPoints(int groupSize) {
        return switch (groupSize) {
            case 1 -> 2;
            case 2 -> 5;
            default -> groupSize>=3 ?9:0;
        };
    }
    
    
    

}
