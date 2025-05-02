package fr.uge.cascadia.score;


import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.board.Board;


/**
 * A sealed interface for defining different scoring strategies.
 * Each strategy provides a specific way to calculate the score for a given animal on the board.
 */
public sealed interface ScoringStrategy permits FaunaScoring , VariantScoring  {
 
	 /**
     * Calculates the score for a specific animal on the given game board.
     *
     * @param board  board in to analyze 
     * @param animal the animal for which we calculate the score 
     * @return The calculated score for the animal.
     */
	public int calculateScore(Board board, Animal animal);
}
