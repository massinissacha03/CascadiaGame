package fr.uge.cascadia.score;

import fr.uge.cascadia.board.Board;



/**
 * A sealed interface for scoring cards. Each scoring card implements a specific 
 * calculation strategy for determining the score of a particular animal or condition 
 * on the game board.
 */
public sealed interface ScoringCard permits ElkScoring , BearScoring , BuzzardScoring , SalmonScoring , FoxScoring  {
 
	
	 /**
     * Calculates the score based on the board.
     *
     * @param board The game board used for score calculation.
     * @return The calculated score.
     */
	int calculate(Board board);
}
