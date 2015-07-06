package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Game implements Serializable {
	
	static final long serialVersionUID = 1; // use this as a version number
	private int numPlayers, currPlayerID;
	private HashMap<Integer, Player> playersMap = new HashMap<Integer, Player>();
	private boolean isDice;
	private double diff;
	private HashMap<Integer, Territory> territoriesMap = new HashMap<Integer, Territory>();
	private Random rand = new Random();

	public Game(int diff, boolean diceLike, int numPlayersToAdd, int[] startingResources, 
			boolean fromSave, int[] savedTerrs, ArrayList<String[]> mapDetails) {
		
		// sets AI resource generation based on selected difficulty
		switch(diff){
		case 0:
			this.diff = 0.7;
			break;
		case 1:
			this.diff = 1;
			break;
		case 2:
			this.diff = 1.3;
			break;
		}
		
		isDice = diceLike;
		this.numPlayers = numPlayersToAdd;
		Iterator<String[]> territoryIterator = mapDetails.iterator();
		if(!fromSave) territoryIterator.next(); // removes invalid first row
		int[] startingTerritories = new int[numPlayers + 1];
		Arrays.fill(startingTerritories, 0);
		
		// create territories as well as setting the players initial starting territories.		
		while (territoryIterator.hasNext()) {
			String[] currTerritory = territoryIterator.next();
			Territory tempTerritory = new Territory(currTerritory);
			startingTerritories[tempTerritory.getOwner()] += 1;
			territoriesMap.put(tempTerritory.getId(), tempTerritory);
		}
		
		// creating players with the number of starting territories determined
		for (int i = 1; i <= numPlayers; i++) {
			if(fromSave){
				playersMap.put(i, new Player(i, startingResources[i-1], savedTerrs[i-1]));
			}
			else{
				playersMap.put(i, new Player(i, startingResources[i-1], startingTerritories[i]));
			}
		}
		
	}
	
	// get functions for use by other classes
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
	public void startPlayerTurn(int playerID, boolean addRes) {
		currPlayerID = playerID;
		Player player = playersMap.get(playerID);
		int playerNumTerrOwned = player.getNumTerritoriesOwned();
		
		// resets conquered attribute for each territory
		if(addRes){
			for(int i = 1; i <= territoriesMap.size(); i++){
				Territory t = territoriesMap.get(i);
				t.setConquered(false);
			}
		}

		// Updates new number of turns for player
//		int lowerBound = (playerNumTerrOwned / 2); // Number of turns player
//													// gets...
//		int upperBound = playerNumTerrOwned + 1; // is between half of
//													// numTerrowned and
//													// numTerrowned
//		player.setNumTurns(rand.nextInt(upperBound - lowerBound) + lowerBound + 1); // Between
//																				// upbound(exclusive)
//																				// and
//																				// lowbound(inclusive)
		
		if(currPlayerID != 1){ // AI resource adding and movement
			int res = (int) (playerNumTerrOwned * 10 * diff);
			player.addResources(res); 
			AIMoves(player);
			turnEnds();
		}
		else if(addRes){ // player resource adding
			player.addResources(playerNumTerrOwned * 10);
		}
	}
	
	// unit adding execution code
	public void executeTerritoryAddUnits(int playerID, int territoryID,
			int numUnits) {

		Player currPlayer = playersMap.get(playerID);
		territoriesMap.get(territoryID).addUnits(numUnits);
		currPlayer.minusResources(numUnits*10);
//		currPlayer.minusNumTurns();
//		
//		if (currPlayer.isTurnEnded() & playerID != 1) {
//			turnEnds();
//		}
	}

	//attack execution code
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
		
		int numUnitsA = terrA.getNumUnits();
		int numUnitsB = terrB.getNumUnits();
		
//		int numUnitsARevised = (int) Math.floor(terrA.getNumUnits()
//				* (1 + (Math.random() / 10.0)));
//		int numUnitsBRevised = (int) Math.floor(terrB.getNumUnits()
//				* (1 + (Math.random() / 10.0)));
		 //If the attacker has more units
		
		terrA.setNumUnits(numUnitsA - numUnits);
		
		// dice-like logic
		if(isDice){
			
			// keep fighting till one person's units reaches 0
			while(numUnits != 0 & numUnitsB != 0){
				int rollAttack = rand.nextInt(6);
				int rollDef = rand.nextInt(6);
				
				// attacker only wins if his roll is higher
				if(rollAttack > rollDef){
					numUnitsB--;
				}
				else{
					numUnits--;
				}
			}
			
			// defender wins
			if(numUnits == 0){
				terrB.setNumUnits(numUnitsB);
			}
			
			// attacker wins
			else{
				terrB.setNumUnits(numUnits);
				terrB.setOwner(terrA.getOwner());
				terrB.setConquered(true);
				
				playerA.setNumTerritoriesOwned(playerA.getNumTerritoriesOwned() + 1); // Adjusts number of territories
//				playerA.addTerritoryID(territory2ID); //owned per player
			
				if(playerID2 != 0){ // player 0 is the neutral party - no player representing it
					playerB.setNumTerritoriesOwned(playerB.getNumTerritoriesOwned() - 1);
//					playerB.removeTerritoryID(territory2ID);
				}	
			}
		}
		
		// non dice-like logic
		else{
			// attacker wins
			if (numUnits > numUnitsB) {
				int finalNumUnits = numUnits - numUnitsB; // number of suriving units
															
				terrB.setNumUnits(finalNumUnits); // Set number of units left
				terrB.setOwner(terrA.getOwner()); // Change owner to winner of fight
				terrB.setConquered(true);
			
				playerA.setNumTerritoriesOwned(playerA.getNumTerritoriesOwned() + 1); // Adjusts number of territories
//				playerA.addTerritoryID(territory2ID); //owned per player
			
				if(playerID2 != 0){ // player 0 is the neutral party - no player representing it
					playerB.setNumTerritoriesOwned(playerB.getNumTerritoriesOwned() - 1);
//					playerB.removeTerritoryID(territory2ID);
				}	
			}
		
			// defender wins
			else{
				int finalNumUnits = numUnitsB - numUnits;
				terrB.setNumUnits(finalNumUnits);
			}
		
//			playersMap.get(playerID1).minusNumTurns();
		
//			if (playerA.isTurnEnded() & playerID1 != 1) {
//				turnEnds();
//			}
		}
	}
	
	// execute movement
	public void executeMoveUnits(int playerID, int territory1ID,
			int territory2ID, int numUnitsToMove) {
		Territory A = territoriesMap.get(territory1ID);
		Territory B = territoriesMap.get(territory2ID);
		A.setNumUnits(A.getNumUnits() - numUnitsToMove);
		B.setNumUnits(B.getNumUnits() + numUnitsToMove);
//		playersMap.get(playerID).minusNumTurns();
//		if (playersMap.get(playerID).isTurnEnded() & playerID != 1) {
//			turnEnds();
//		}
	}
	
	// called by end turn button or AI finishing - causes the next player's turn to start
	public void turnEnds() {
		currPlayerID++;
		if(currPlayerID > numPlayers){
			currPlayerID = 1;
		}
		startPlayerTurn(currPlayerID, true);
	}
	
	// AI function
	public void AIMoves(Player player) {
		
		ArrayList<Territory> owned = new ArrayList<Territory>();
		int limit = player.getNumResources() / 10;
		
		// find territories that I have
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			if(t.getOwner() == player.getPlayerID()){
				owned.add(t);
			}
		}
		
		Collections.shuffle(owned);
		Iterator<Territory> tIterator = owned.iterator();
		
		// go through each territory and do stuff
		while(tIterator.hasNext()){
			Territory t = tIterator.next();
			
			ArrayList<Integer> n = t.getNeighbourIDs();
			Collections.shuffle(n);
			Iterator<Integer> nI = n.iterator();
			
			while(nI.hasNext()){
				int currTerr = nI.next();
				Territory neighbour = territoriesMap.get(currTerr);
				
				// enemy territory next door - add armies here if possible
				if(neighbour.getOwner() != player.getPlayerID()){
					int addAmt = rand.nextInt(limit);
					if(addAmt != 0) executeTerritoryAddUnits(player.getPlayerID(), t.getId(), addAmt);
					limit -= addAmt;
					
					// if will win - attack neighbouring territory
					// if will lose - 1/3 chance to attack
					
					int chooseAtk = rand.nextInt(3);
					
					if(neighbour.getNumUnits() < t.getNumUnits()){
						executeTerritoryAttack(player.getPlayerID(), neighbour.getOwner(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
					
					else if(chooseAtk == 0){
						executeTerritoryAttack(player.getPlayerID(), neighbour.getOwner(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
				}
				
				// friendly territory next door - see if it has hostile neighbours
				else{
					
					boolean frontLine = false;
					ArrayList<Integer> n2 = neighbour.getNeighbourIDs();
					Collections.shuffle(n2);
					Iterator<Integer> n2I = n2.iterator();
					
					while(n2I.hasNext()){
						int next = n2I.next();
						Territory n3 = territoriesMap.get(next);
						
						// check if this neighbour of neighbour is hostile
						if(n3.getOwner() != player.getPlayerID()){
							frontLine = true;
						}
					}
					
					// move all armies to neighbour
					if(frontLine){
						executeMoveUnits(player.getPlayerID(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
				}
			}
			
		}
		
//		int rscSplit1;
//		int rscSplit2;
//		ArrayList<Territory> tempStorage = new ArrayList<Territory>();
//		for (int i = 1; i <= territoriesMap.size(); i++) { // Adds to temp storage
//			Territory currTerr = territoriesMap.get(i);
//			if (currTerr.getOwner() == player.getPlayerID()) {
//				tempStorage.add(currTerr);
//			}
//		}
//		do {
//			boolean executedAttack = false;
//			//ADDS RESOURCES IF AVAILABLE TO RANDOM 2 TERRITORIES IT HAS OR IF ONLY HAS 1, ADD TO 1
//			if (player.getNumResources() > 0) {
//
//				if (player.getNumTerritoriesOwned() > 1) {
//					// Use up all resources on 2 random territories it has.
//					if (player.getNumResources() % 2 == 0) { // Split resources for 2 terr
//						rscSplit1 = player.getNumResources() / 2;
//						rscSplit2 = rscSplit1;
//					} else {
//						rscSplit1 = player.getNumResources() / 2;
//						rscSplit2 = rscSplit1 + 1;
//					}																
//					ArrayList<Territory> rscTempStorage = new ArrayList<Territory>();
//					rscTempStorage = (ArrayList<Territory>) tempStorage.clone();
//					int indexRandom = rand.nextInt(player.getNumTerritoriesOwned()); // Get random index									
//					Territory chosen = rscTempStorage.get(indexRandom);
//					executeTerritoryAddUnits(player.getPlayerID(),chosen.getId(), rscSplit1 / 10);
//					rscTempStorage.remove(indexRandom);
//					indexRandom = rand.nextInt(player.getNumTerritoriesOwned() - 1);
//					chosen = rscTempStorage.get(indexRandom);
//					executeTerritoryAddUnits(player.getPlayerID(),chosen.getId(), rscSplit2 / 10);
//				}
//				else {
//					for (int i = 1; i <= territoriesMap.size(); i++) {
//						Territory currTerr = territoriesMap.get(i);
//						if (currTerr.getOwner() == player.getPlayerID()) {
//							executeTerritoryAddUnits(player.getPlayerID(),currTerr.getId(), player.getNumResources() / 10);
//						}
//					}
//				}
//			}
//
//			//EXECUTE ATTACK IF POSSIBLE
//			for ( int i = 0; i < player.getNumTerritoriesOwned(); i++){  //Loops through all owned territories it has
//				Territory currTerr = tempStorage.get(i);
//				ArrayList<Integer> AtkTempStorage = new ArrayList<Integer>();
//				AtkTempStorage = (ArrayList<Integer>) currTerr.getNeighbourIDs().clone();  //Stores neighbor IDs for curr terr
//				for ( int j = 0; j < AtkTempStorage.size(); j++){  //Loops through all its neighbors
//					if ( currTerr.getNumUnits() >= territoriesMap.get(AtkTempStorage.get(j)).getNumUnits()){
//						executeTerritoryAttack(player.getPlayerID(), territoriesMap.get(AtkTempStorage.get(j)).getOwner(),
//								currTerr.getId(), AtkTempStorage.get(j), currTerr.getNumUnits() );
//						executedAttack = true;
//					}					
//				}
//			}
//			if ( !executedAttack ){
//				break;
//			}
//			else{
//				executedAttack = false;
//			}
//			
//
//		} while (!player.isTurnEnded());
		
	}
	
	public void setTerritoriesConq(boolean[] values){
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			t.setConquered(values[i-1]);
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		if(diff == 0.7){
			sb.append("0");
		}
		else if(diff == 1){
			sb.append("1");
		}
		else if(diff == 1.3){
			sb.append("2");
		}	
		sb.append(",");
		
		sb.append(Boolean.toString(isDice));
		sb.append(",");
		
		sb.append(Integer.toString(numPlayers));
		sb.append(",");
		
		for(int i = 1; i <= playersMap.size(); i++){
			sb.append(playersMap.get(i).getNumResources());
			sb.append(",");
		}
		
		sb.append(true);
		
		sb.append("\n");
		
		for(int i = 1; i <= playersMap.size(); i++){
			sb.append(Integer.toString(playersMap.get(i).getNumTerritoriesOwned()));
			if(i != playersMap.size()) sb.append(",");
		}
		
		sb.append("\n");
		
		for(int i = 1; i <= territoriesMap.size(); i++){
			sb.append(Boolean.toString(territoriesMap.get(i).isConq()));
			if(i != territoriesMap.size()) sb.append(",");
		}
		
		sb.append("\n");
		
		for(int i = 1; i <= territoriesMap.size(); i++){
			sb.append(territoriesMap.get(i).toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}