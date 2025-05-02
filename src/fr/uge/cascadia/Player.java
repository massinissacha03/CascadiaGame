package fr.uge.cascadia;

import java.util.Objects;

import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.board.Board;
import fr.uge.cascadia.score.Score;
import fr.uge.cascadia.score.ScoringStrategy;
import fr.uge.cascadia.tile.Tile;
import fr.uge.cascadia.tile.TileType;

/**
 * The Player class represents a player in the game.
 * It handles all player-related data and actions.
 *
 * @author Massinissa
 */
public class Player {

	
	 /**
     * The name of the player.
     */
    private final String name;

    /**
     * The player's nickname based on their performance.
     */
    private String surname;

    /**
     * The player's game board, which tracks tiles and tokens.
     */
    private final Board board;

    /**
     * The player's score information, including points and bonuses.
     */
    private final Score score;

    /**
     * The player's majority habitat score, used for scoring bonuses.
     */
    private int majorityHabitat;

  
    /**
     * Creates a new player with a name, board type, and initializes the board.
     *
     * @param name The name of the player.
     * @param type The type of tiles used in the board (Square or Hexagonal).
     * @param initialTilesIndex The index for initial tiles when creating the board.
     */
    public Player(String name, TileType type, int initialTilesIndex) {
        Objects.requireNonNull(name, "Player name cannot be null.");
        Objects.requireNonNull(type, "Tile type cannot be null.");

        if (initialTilesIndex > 12 || initialTilesIndex % 3 != 0) {
            throw new IllegalArgumentException("`initialTilesIndex` must be one of the following values: {0, 3, 6, 9, 12}.");
        }

        this.name = name;
        board = new Board(5, type);
        this.board.initializeBoard(initialTilesIndex);
        this.score = new Score(board);
    }

    /**
     * Gets the name of the player.
     * @return a string representing the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the score of the player.
     *
     * @return a Score object representing the player's score.
     */
    public Score getScore() {
        return score;
    }

    /**
     * Sets the scoring strategy for the player's score calculation.
     *
     * @param strategy The scoring strategy to be used
     */
    public void setScoringStrategy(ScoringStrategy strategy) {
        Objects.requireNonNull(strategy, "Scoring strategy cannot be null.");
        score.setScoringStrategy(strategy);
    }

    /**
     * Calculates the player's score based on the current scoring strategy.
     */
    public void calculateScore() { //le score ici sans bonus
        score.calculateAnimalScores();

        score.calculateHabitatScores();
        
        

    }

    /**
     * Gets the player's board.
     *
     * @return The player's board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Makes the player insert a tile.
     *
     * @param p The position where the tile will be placed.
     * @param tile The tile to be placed.
     * @return True if the tile was successfully inserted, false otherwise.
     */
    public boolean playerInsertTile(Position p, Tile tile) {
        Objects.requireNonNull(p, "position cannot be null.");
        Objects.requireNonNull(tile, "Tile cannot be null.");

        return board.insertTile(p, tile);
    }

    /**
     * Makes the player insert a token.
     *
     * @param p The position where the token will be placed.
     * @param token The token to be placed.
     * @return True if the token was successfully inserted, false otherwise.
     */
    public boolean playerInsertToken(Position p, AnimalToken token) {
        Objects.requireNonNull(p, "position cannot be null.");
        Objects.requireNonNull(token, "Token cannot be null.");

        return board.insertToken(p, token);
    }
    
    
    
    /**
     * Adds to the majorityHabitat
     */
    public void addMajorityHabitat() {
    	majorityHabitat +=1 ; 
    }

    /**
     * Getter of the number of the number of majorities 
     * @return majorityHabitat
     */
    public int getMajorityHabitat() {
    	return majorityHabitat ; 
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string representing the player.
     */
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
    
    /**
     * Getter of the surname (nickname) of the player
     * @return surname (String) 
     */
    public  String getSurname() {
    	return surname ; 
    }
    
    
    

    /**
     * Assigns a surname (title) to the player based on their total score.

     * If the score is less than 60, no title is assigned.
     */
    
    public void attributeSurname() {
    	int points = score.getTotalPoints(); 
        switch (points / 10) { 
        case 6 -> surname= "<< Flâneur >>";
        case 7 -> surname = "<< Randonneur amateur >>";
        case 8 -> surname = "<< Trekkeur averti >>";
        case 9-> surname = "<< Garde forestier >>";
        case 10 -> surname = "<< Ranger d’élite ! >>";
            default -> surname = points >= 110 ? " <<Légende de la Forêt !!>>" : ""; 
        }; 
    }


}
