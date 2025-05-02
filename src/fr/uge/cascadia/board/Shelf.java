package fr.uge.cascadia.board;

import java.util.*;
import java.util.stream.Collectors;

import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.tile.Tile;



/**
 * The Shelf class represents a storage system for tiles and animal tokens.
 * Each tile and token is assigned a unique ID for tracking.
 * The shelf uses two maps:
 * - `tileMap`: Stores tiles associated with their unique IDs.
 * - `tokenMap`: Stores animal tokens associated with their unique IDs.
 * 
 * 
 * 
 * This class provides methods to add, retrieve, and manage tiles and tokens.
 *
 *	@author Massinissa
 **/



public class Shelf {
	/* 
	 *Stores tiles with their associated unique IDs.
	 * */

	private final Map<Integer, Tile> tileMap = new LinkedHashMap<>();

	/*Stores animal tokens with their associated unique IDs.
	 */
	private final Map<Integer, AnimalToken> tokenMap = new LinkedHashMap<>();



	private int nextId = 0; 

    /**
     * Default constructor for the Shelf class.
     * Initializes the shelf with default settings.
     */
	public Shelf() {}

	
	/**
	 * getter of the size of the shelf 
	 * @return size of the shelf type int 
	 */
	public int size() {
		return tileMap.size();
	}

	/**
	 * Fills the shelf with pairs of tiles and animal tokens.
	 * 
	 * - Null tiles in the shelf are replaced with random tiles from the bag.
	 * - Null tokens in the shelf are replaced with random animal tokens from the bag.
	 * - Additional pairs of tiles and tokens are added until the shelf contains 4 pairs.
	 * - If all tokens on the shelf are of the same type, they are replaced to ensure variety.
	 * 
	 * @param bagOfTiles        The list of available tiles. Cannot be null.
	 * @param bagOfTokenAnimals The list of available animal tokens. Cannot be null.
	 */

	public void makeShelf(List<Tile> bagOfTiles, List<AnimalToken> bagOfTokenAnimals) {
		Objects.requireNonNull(bagOfTiles, "The bag of tiles cannot be null.");
		Objects.requireNonNull(bagOfTokenAnimals, "The bag of token animals cannot be null.");
		Random random = new Random();
		for (var entry : tileMap.entrySet()) {
			if (entry.getValue() == null && !bagOfTiles.isEmpty()) { 
				Tile tile = bagOfTiles.remove(random.nextInt(bagOfTiles.size()));
				tileMap.put(entry.getKey(), tile); // Remplace la tuile null par une nouvelle tuile
			}
		}
		for (var entry : tokenMap.entrySet()) {
			if (entry.getValue() == null && !bagOfTokenAnimals.isEmpty()) {
				AnimalToken token = bagOfTokenAnimals.remove(random.nextInt(bagOfTokenAnimals.size()));
				tokenMap.put(entry.getKey(), token); // Remplace le token null par un nouveau token
			}
		}
		while (tileMap.size() < 4 && !bagOfTiles.isEmpty() && !bagOfTokenAnimals.isEmpty()) {
			Tile tile = bagOfTiles.remove(random.nextInt(bagOfTiles.size()));
			AnimalToken token = bagOfTokenAnimals.remove(random.nextInt(bagOfTokenAnimals.size()));
			add(tile, token);         }
		while (sameAnimalTokens() == 4) replaceTokensInShelf(bagOfTokenAnimals, random);

	}





	/**
	 * Removes the last non-null token from the right and shifts the remaining tokens 
	 * to maintain order, filling the first positions with null values.
	 * 
	 */
	public void removeLastToken() { //cette méthode sert à gérer le mode solo 
		boolean foundNonNull = false;
		for (int id = tokenMap.size() - 1; id >= 0; id--) {
			if (tokenMap.get(id) != null) {
				tokenMap.put(id, null); // Remplace par null
				foundNonNull = true;
				break;}}
		if (!foundNonNull) return;		
		Map<Integer, AnimalToken> reorderedMap = new HashMap<>();
		int nullIndex = 0;
		for (int id = 0; id < tokenMap.size(); id++) {
			if (tokenMap.get(id) == null) {
				reorderedMap.put(nullIndex++, null);}}
		for (int id = 0; id < tokenMap.size(); id++) {
			if (tokenMap.get(id) != null) reorderedMap.put(nullIndex++, tokenMap.get(id));
		}
		tokenMap.clear();
		tokenMap.putAll(reorderedMap);
	}

	/**
	 * Removes the last non-null tile from the right and shifts the remaining tile 
	 * to maintain order, filling the first positions with null values.
	 * 
	 */
	public void removeLastTile() {
		boolean foundNonNull = false;
		for (int id = tileMap.size() - 1; id >= 0; id--) {
			if (tileMap.get(id) != null) {
				tileMap.put(id, null); // Remplace par null
				foundNonNull = true;
				break;	}}
		if (!foundNonNull) return;
		Map<Integer, Tile> reorderedMap = new HashMap<>();
		int nullIndex = 0;
		for (int id = 0; id < tileMap.size(); id++) {
			if (tileMap.get(id) == null) {
				reorderedMap.put(nullIndex++, null);}}
		for (int id = 0; id < tileMap.size(); id++) {
			if (tileMap.get(id) != null) {
				reorderedMap.put(nullIndex++, tileMap.get(id));}}
		tileMap.clear();
		tileMap.putAll(reorderedMap);
	}





	/**
	 * Checks if there are 3 or 4 identical animal tokens in the shelf.
	 *
	 * @return The frequency of identical tokens (3 or 4), oe else -1 .
	 */
	public int sameAnimalTokens() {
		// Filtre les valeurs nulles de la tokenMap
		var filteredTokens = tokenMap.values().stream()
				.filter(Objects::nonNull) 
				.collect(Collectors.toList());

		Map<AnimalToken, Long> tokenFrequency = filteredTokens.stream()
				.collect(Collectors.groupingBy(token -> token, Collectors.counting()));

		for (var frequency : tokenFrequency.values()) {
			if (frequency == 4 || frequency == 3) {
				return frequency.intValue();
			}
		}
		return -1;
	}

	/**
	 * Adds a new pair of tile and animal token to the shelf.
	 *
	 * @param tile  The tile to add (cannot be null).
	 * @param token The animal token to add (cannot be null).
	 */
	public void add(Tile tile, AnimalToken token) {
		Objects.requireNonNull(tile, "Tile cannot be null.");
		Objects.requireNonNull(token, "Token cannot be null.");
		tileMap.put(nextId, tile);
		tokenMap.put(nextId, token);
		nextId++;
	}

	/**
	 * Replaces a tile with `null` in the shelf using its ID.
	 *
	 * @param id The ID of the tile to replace with null.
	 */

	public void removeTile(int id) {
		if (tileMap.containsKey(id)) {
			tileMap.put(id, null); // Remplace la tuile par null
		}
	}

	/**
	 * Replaces a token with `null` in the shelf using its ID.
	 *
	 * @param id The ID of the token to replace with null.
	 */

	public void removeToken(int id) {
		if (tokenMap.containsKey(id)) {
			tokenMap.put(id, null); // Remplace le token par null
		}
	}


	/**
	 * Replaces all tokens in the shelf if 4 identical tokens are detected.
	 *
	 * @param bagOfTokenAnimals The list of tokens available for replacement.
	 * @param random            An instance of Random for selecting tokens randomly.
	 */

	private void replaceTokensInShelf(List<AnimalToken> bagOfTokenAnimals, Random random) {
		Objects.requireNonNull(bagOfTokenAnimals);
		Objects.requireNonNull(random);
		for (Map.Entry<Integer, AnimalToken> entry : tokenMap.entrySet()) {
			if (!bagOfTokenAnimals.isEmpty()) {
				AnimalToken oldToken = entry.getValue();
				AnimalToken newToken = bagOfTokenAnimals.remove(random.nextInt(bagOfTokenAnimals.size()));
				tokenMap.put(entry.getKey(), newToken);
				bagOfTokenAnimals.add(oldToken);
			}
		}
	}

	/**
	 * Fills the missing tokens in the shelf with random tokens from the bag.
	 *
	 * @param bagOfTokens The bag of available tokens.
	 */

	public void completeTokens(List<AnimalToken> bagOfTokens) {
		Objects.requireNonNull(bagOfTokens, "Le sac de jetons d'animaux ne peut pas être null.");
		Random random = new Random();

		for (Map.Entry<Integer, AnimalToken> entry : tokenMap.entrySet()) {
			if (entry.getValue() == null && !bagOfTokens.isEmpty()) {
				AnimalToken newToken = bagOfTokens.remove(random.nextInt(bagOfTokens.size()));
				tokenMap.put(entry.getKey(), newToken);
			}
		}
	}

	/**
	 * Fills the missing tiles in the shelf with random tokens from the bag.
	 *
	 * @param bagOfTiles The bag of available tiles.
	 */

	public void completeTiles(List<Tile> bagOfTiles) {
		Objects.requireNonNull(bagOfTiles, "Le sac de tuiles ne peut pas être null.");
		Random random = new Random();

		for (Map.Entry<Integer, Tile> entry : tileMap.entrySet()) {
			if (entry.getValue() == null && !bagOfTiles.isEmpty()) {
				Tile newTile = bagOfTiles.remove(random.nextInt(bagOfTiles.size()));
				tileMap.put(entry.getKey(), newTile);
			}
		}
	}

	/**
	 * Returns an list of tiles in the shelf.
	 *
	 * @return A list of tiles.
	 */
	public List<Tile> getTiles() {
		return new ArrayList<>(tileMap.values());
	}

	/**
	 * Returns an  list of tokens in the shelf.
	 *
	 * @return A list of tokens.
	 */
	public List<AnimalToken> getTokens() {
		return new ArrayList<>(tokenMap.values());
	}

	/**
	 * Returns a tile based on its ID.
	 *
	 * @param id The ID of the tile.
	 * @return The tile associated to the given ID.
	 */
	public Tile getTileById(int id) {
		return tileMap.get(id);
	}

	/**
	 * Returns a tile based on its ID.
	 *
	 * @param id The ID of the tile.
	 * @return The token associated to the given ID.
	 */
	public AnimalToken getTokenById(int id) {
		return tokenMap.get(id);
	}




	/**
	 * Replaces 3 identical tokens.
	 *
	 * @param bagOfTokens Bag of available tokens.
	 * @param replace     a player's  decision
	 * @return True if the tokens were replaced, otherwise False.
	 */
	public boolean replaceThreeTokens(List<AnimalToken> bagOfTokens, boolean replace) {
		Objects.requireNonNull(bagOfTokens);
		if (!replace) return false; 
		Map<AnimalToken, Long> tokenFrequency = tokenMap.values().stream().collect(Collectors.groupingBy(token -> token, Collectors.counting()));
		// Trouver les tokens avec une réptition de 3
		var tokensToReplace = tokenFrequency.entrySet().stream().filter(entry -> entry.getValue() == 3).map(Map.Entry::getKey).toList();

		if (tokensToReplace.isEmpty() || bagOfTokens.isEmpty()) return false; // Rien à remplacer ou sac vide
		Random random = new Random();
		for (Map.Entry<Integer, AnimalToken> entry : tokenMap.entrySet()) {  		// Remplacer les tokens identiques
			AnimalToken currentToken = entry.getValue();
			if (tokensToReplace.contains(currentToken) && !bagOfTokens.isEmpty()) {
				AnimalToken newToken;
				do {					
					if (bagOfTokens.isEmpty()) return false; 
					newToken = bagOfTokens.remove(random.nextInt(bagOfTokens.size()));
				} while (Collections.frequency(tokenMap.values(), newToken) == 3);
				bagOfTokens.add(currentToken); // Remet l'ancien token dans le sac
				tokenMap.put(entry.getKey(), newToken);   }} 
		return true;	}



	/**
	 * Checks if the shelf contains no tokens.
	 *
	 * @return True if all tokens are null, otherwise False.
	 */

	public boolean hasNoTken() {
		for (var token : tokenMap.values()) {
			if (token !=null )return false ;  
		}
		return true;
	}




	@Override
	public String toString() {
		if (tileMap.values().contains(null)) {
			return "";
		}

		if (tokenMap.values().contains(null)) {
			return "";
		}
		var sb = new StringJoiner("\n");
		var tiles = new ArrayList<>(tileMap.keySet());
		for (int id : tiles) {
			String tile = tileMap.get(id).toString(); 
			String token = tokenMap.get(id).toString();
			sb.add("ID: " + id + "       Tile: " + tile + "       Token: " + token);
		}

		return sb.toString();
	}






}

