package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Game {

	private int diff, numPlayers, currPlayerID;
	private HashMap<Integer, Player> playersMap = new HashMap<Integer, Player>();
	private boolean isDice, gameOver;
	private HashMap<Integer, Territory> territoriesMap = new HashMap<Integer, Territory>();
	private Random rand = new Random();

	public Game(int diff, boolean diceLike, int numPlayersToAdd,
			int[] startingResources, ArrayList<String[]> mapDetails) {

		this.diff = diff;
		isDice = diceLike;
		this.numPlayers = numPlayersToAdd;
		for (int i = 0; i < numPlayers; i++) {
			playersMap.put(i, new Player(i, startingResources[i]));
		}
		Iterator<String[]> territoryIterator = mapDetails.iterator();
		territoryIterator.next(); // removes invalid first row
		while (territoryIterator.hasNext()) {
			String[] currTerritory = territoryIterator.next();
			Territory tempTerritory = new Territory(currTerritory);
			territoriesMap.put(tempTerritory.getId(), tempTerritory);
			// Player test = playersMap.get( tempTerritory.getOwner() ); //Add
			// territoryID to the player...
			// Log.d("TESTING","TERR OWNER IS: " + tempTerritory.getOwner() +
			// "PLAYER ID IS "+test.getPlayerID() +"NUM PLAYER IS " +
			// this.numPlayers);
			// .addTerritoryID(tempTerritory.getId()); //to make him own it
		}
	}

	public HashMap<Integer, Territory> getTerritories() {
		return territoriesMap;
	}

	public HashMap<Integer, Player> getPlayers() {
		return playersMap;
	}

	public int getCurrPlayer() {
		return currPlayerID;
	}

	// This method is called when a new player needs to start his turn
	public void startPlayerTurn(int playerID) {
		currPlayerID = playerID;
		Player player = playersMap.get(playerID);
		int playerNumTerrOwned = player.getNumTerritoriesOwned();

		// Updates new number of turns for player
		int lowerBound = playerNumTerrOwned / 2; // Number of turns player
													// gets...
		int upperBound = playerNumTerrOwned + 1; // is between half of
													// numTerrowned and
													// numTerrowned
		player.setNumTurns(rand.nextInt(upperBound - lowerBound) + lowerBound); // Between
																				// upbound(exclusive)
																				// and
																				// lowbound(inclusive)

		// Updates new number of resources for player
		player.addResources(playerNumTerrOwned * 10); // Each unit costs 1
														// resource to build
	}

	public void executeTerritoryAddUnits(int playerID, int territoryID,
			int numUnits) {

		Player currPlayer = playersMap.get(playerID);
		territoriesMap.get(territoryID).addUnits(numUnits);
		currPlayer.minusResources(numUnits);
		currPlayer.minusNumTurns();
		if (currPlayer.isTurnEnded()) {
			turnEnds();
		}
	}

	public void executeTerritoryAttack(int playerID1, int playerID2,
			int territory1ID, int territory2ID, int numUnits) {
		Player playerA = playersMap.get(playerID1); // Owner of the attacking
													// territory
		Player playerB = playersMap.get(playerID2); // Owner of the territory
													// being attacked
		Territory terrA = territoriesMap.get(territory1ID); // Attacking
															// territory
		Territory terrB = territoriesMap.get(territory2ID); // Being attacked
															// territory
		// RNG to make it less boring
		int numUnitsA = (int) Math.floor(terrA.getNumUnits()
				* (1 + (Math.random() / 10.0)));
		int numUnitsB = (int) Math.floor(terrB.getNumUnits()
				* (1 + (Math.random() / 10.0)));
		// If the attacker has more units
		if (numUnitsA > numUnitsB) {
			int finalNumUnits = numUnitsA - numUnitsB; // Saves the number of
														// surviving units
			if (finalNumUnits > terrA.getNumUnits()) { // In the case where end
														// units are
				finalNumUnits = terrA.getNumUnits(); // more than at start, set
														// to numUnits at start
			}
			terrB.setNumUnits(finalNumUnits); // Set number of units left after
												// the fight on terrB
			terrB.setOwner(terrA.getOwner()); // Change owner to winner of fight
			playerA.setNumTerritoriesOwned(playerA.getNumTerritoriesOwned() + 1); // Adjusts
																					// number
																					// of
																					// territories
			// playerA.addTerritoryID(territory2ID); //owned per player
			playerB.setNumTerritoriesOwned(playerB.getNumTerritoriesOwned() - 1);
			// playerB.removeTerritoryID(territory2ID);
		}
		// If defender has more units
		else if (numUnitsB > numUnitsA) {
			int finalNumUnits = numUnitsB - numUnitsA;
			if (finalNumUnits > terrB.getNumUnits()) {
				finalNumUnits = terrB.getNumUnits();
			}
			terrB.setNumUnits(finalNumUnits);
		}
		playersMap.get(playerID1).minusNumTurns();
		if (playerA.getNumTerritoriesOwned() == territoriesMap.size()) { // Player
																			// conquered
																			// all
			// TODO player has won
		}
		if (playerA.isTurnEnded()) {
			turnEnds();
		}
	}

	public void executeMoveUnits(int playerID, int territory1ID,
			int territory2ID, int numUnitsToMove) {
		Territory A = territoriesMap.get(territory1ID);
		Territory B = territoriesMap.get(territory2ID);
		A.setNumUnits(A.getNumUnits() - numUnitsToMove);
		B.setNumUnits(B.getNumUnits() + numUnitsToMove);
		playersMap.get(playerID).minusNumTurns();
		if (playersMap.get(playerID).isTurnEnded()) {
			turnEnds();
		}
	}

	public void turnEnds() {
		startPlayerTurn((currPlayerID + 1) % (numPlayers)); // Selects next
		// player
	}

	public void AIMoves(Player player) {
		int rscSplit1;
		int rscSplit2;
		ArrayList<Territory> tempStorage= new ArrayList<Territory>();
		for (int i = 0; i < territoriesMap.size(); i++) { // Adds to temp storage
			Territory currTerr = territoriesMap.get(i);
			if (currTerr.getOwner() == player.getPlayerID()) {
				tempStorage.add(currTerr);
			}
		}
		do {
			//ADDS RESOURCES IF AVAILABLE TO RANDOM 2 TERRITORIES IT HAS OR IF ONLY HAS 1, ADD TO 1
			if (player.getNumResources() > 0) {

				if (player.getNumTerritoriesOwned() > 1) {
					// Use up all resources on 2 random territories it has.
					if (player.getNumResources() % 2 == 0) { // Split resources for 2 terr
						rscSplit1 = player.getNumResources() / 2;
						rscSplit2 = rscSplit1;
					} else {
						rscSplit1 = player.getNumResources() / 2;
						rscSplit2 = rscSplit1 + 1;
					}																
					ArrayList<Territory> rscTempStorage = new ArrayList<Territory>();
					rscTempStorage = (ArrayList<Territory>) tempStorage.clone();
					int indexRandom = rand.nextInt(player.getNumTerritoriesOwned()); // Get random index									
					Territory chosen = rscTempStorage.get(indexRandom);
					executeTerritoryAddUnits(player.getPlayerID(),chosen.getId(), rscSplit1);
					rscTempStorage.remove(indexRandom);
					indexRandom = rand.nextInt(player.getNumTerritoriesOwned() - 1);
					chosen = rscTempStorage.get(indexRandom);
					executeTerritoryAddUnits(player.getPlayerID(),chosen.getId(), rscSplit1);
				}
				else {
					for (int i = 0; i < territoriesMap.size(); i++) {
						Territory currTerr = territoriesMap.get(i);
						if (currTerr.getOwner() == player.getPlayerID()) {
							executeTerritoryAddUnits(player.getPlayerID(),currTerr.getId(), player.getNumResources());
						}
					}
				}
			}

			//EXECUTE ATTACK IF POSSIBLE
			for ( int i = 0; i < player.getNumTerritoriesOwned(); i++){  //Loops through all owned territories it has
				Territory currTerr = tempStorage.get(i);
				ArrayList<Integer> AtkTempStorage = new ArrayList<Integer>();
				AtkTempStorage = (ArrayList<Integer>)currTerr.getNeighbourIDs().clone();  //Stores neighbor IDs for curr terr
				for ( int j = 0; j < AtkTempStorage.size(); j++){  //Loops through all its neighbors
					if ( currTerr.getNumUnits() >= territoriesMap.get(AtkTempStorage.get(j)).getNumUnits()){
						executeTerritoryAttack(player.getPlayerID(), territoriesMap.get(AtkTempStorage.get(j)).getOwner(),
								currTerr.getId(), AtkTempStorage.get(j), currTerr.getNumUnits() );
					}					
				}
			}

		} while (!player.isTurnEnded());

	}
}

/*
 * public int getGameWinner(){ Set<Integer> players = playersMap.keySet();
 * for(Integer playerID: players){ if (
 * playersMap.get(playerID).getNumTerritoriesOwned() == territoriesMap.size()){
 * return playerID; } } return 999; to add future methods of winning games }
 */