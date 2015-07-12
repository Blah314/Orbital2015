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
	private ArrayList<StringBuilder> log;
	private StringBuilder logTurn;
	private HashMap<Integer, Player> playersMap = new HashMap<Integer, Player>();
	private boolean isDice, capital;
	private double diff;
	private HashMap<Integer, Territory> territoriesMap = new HashMap<Integer, Territory>();
	private Random rand = new Random();
	private HashMap<Integer, String> AINames = new HashMap<Integer, String>();

	public Game(int diff, boolean diceLike, boolean capitals, int numPlayersToAdd, int[] startingResources, 
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
		case 3:
			this.diff = 1.5;
			break;
		}
		
		isDice = diceLike;
		capital = capitals;
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
		
		// creates a new log and a new log line
		log = new ArrayList<StringBuilder>();
		logTurn = new StringBuilder();
		
		// names for the AI players
		AINames.put(2, "Kenneth");
		AINames.put(3, "Nicholas");
		AINames.put(4, "Ryan");
		AINames.put(5, "QX");
	}
	
	// get functions for use by other classes
	public HashMap<Integer, Territory> getTerritories() {
		return territoriesMap;
	}

	public HashMap<Integer, Player> getPlayers() {
		return playersMap;
	}
	
	public boolean isCapital(){
		return capital;
	}

	public int getCurrPlayer() {
		return currPlayerID;
	}
	
	// called when a player's capital is conquered
	public void deactivatePlayer(int player){
		Player p = playersMap.get(player);
		p.deactivate();
	}

	// This method is called when a new player needs to start his turn
	public void startPlayerTurn(int playerID, boolean addRes) {
		currPlayerID = playerID;
		Player player = playersMap.get(playerID);
		int playerNumTerrOwned = player.getNumTerritoriesOwned();
		
		// resets conquered attribute for each territory
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			t.setConquered(false);
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
			int res = (int) (playerNumTerrOwned * 10 * diff * player.getResMod());
			player.addResources(res);
			logTurn.append("AI " + AINames.get(currPlayerID) + " gained " + 
			Integer.toString(res) + " resources from " + Integer.toString(playerNumTerrOwned) + 
			" territories.\n");
			AIMoves(player);
			turnEnds();
		}
		else if(addRes){ // player resource adding
			int res = (int) (playerNumTerrOwned * 10 * player.getResMod());
			player.addResources(res);
			logTurn.append("You gained " + Integer.toString(res) + " resources from " 
			+ Integer.toString(playerNumTerrOwned) + " territories.\n");
		}
	}
	
	// unit adding execution code
	public void executeTerritoryAddUnits(int playerID, int territoryID,
			int numUnits) {

		Player currPlayer = playersMap.get(playerID);
		territoriesMap.get(territoryID).addUnits(numUnits);
		currPlayer.minusResources(numUnits*10);
		
		if(playerID == 1){
			logTurn.append("You added " + Integer.toString(numUnits) + " units to " + 
			territoriesMap.get(territoryID).getName() + ".\n");
		}
		
		else{
			logTurn.append("AI " + AINames.get(playerID) + " added " + Integer.toString(numUnits) + 
			" units to " + territoriesMap.get(territoryID).getName() + ".\n");
		}
		
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
		int numUnitsA = terrA.getNumUnits();
		int numUnitsB = terrB.getNumUnits();
		
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
				
				if(playerID1 == 1){
					logTurn.append("You failed to conquer " + terrB.getName() + ".\n");
				}
				
				else{
					logTurn.append("AI " + AINames.get(playerID1) + " failed to conquer " + 
					terrB.getName() + ".\n");
				}
			}
			
			// attacker wins
			else{
				terrB.setNumUnits(numUnits);
				terrB.setOwner(playerID1);
				terrB.setConquered(true);
				
				if(playerID1 == 1){
					logTurn.append("You conquered " + terrB.getName() + ".\n");
				}
				
				else{
					logTurn.append("AI " + AINames.get(playerID1) + " conquered " + terrB.getName() + 
					".\n");
				}
				
				if(terrB.isCapital() & capital){
					deactivatePlayer(playerID2);
					terrB.capitalConquered();
					if(playerID2 == 1){
						logTurn.append("You lost your capital. Game over. :(\n");
					}
					else{
						logTurn.append("AI " + AINames.get(playerID2) + 
						" lost their capital. They are eliminated.\n");
					}
				}
				
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
				int finalNumUnits = numUnits - numUnitsB; // number of surviving units
															
				terrB.setNumUnits(finalNumUnits); // Set number of units left
				terrB.setOwner(terrA.getOwner()); // Change owner to winner of fight
				terrB.setConquered(true);
				
				if(playerID1 == 1){
					logTurn.append("You conquered " + terrB.getName() + ".\n");
				}
				
				else{
					logTurn.append("AI " + AINames.get(playerID1) + " conquered " + terrB.getName() + 
					".\n");
				}
				
				if(terrB.isCapital() & capital){
					deactivatePlayer(playerID2);
					terrB.capitalConquered();
					
					if(playerID2 == 1){
						logTurn.append("You lost your capital. Game over. :(\n");
					}
					else{
						logTurn.append("AI " + AINames.get(playerID2) + 
						" lost their capital. They are eliminated.\n");
					}
				}
			
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
				
				if(playerID1 == 1){
					logTurn.append("You failed to conquer " + terrB.getName() + ".\n");
				}
				
				else{
					logTurn.append("AI " + AINames.get(playerID1) + " failed to conquer " + 
					terrB.getName() + ".\n");
				}
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
		
		if(playerID == 1){
			logTurn.append("You moved " + Integer.toString(numUnitsToMove) + " units from " + 
			A.getName() + " to " + B.getName() + ".\n");
		}
		
		else{
			logTurn.append("AI " + AINames.get(playerID) + " moved " + Integer.toString(numUnitsToMove) + 
			" units from " + A.getName() + " to " + B.getName() + ".\n");
		}
//		playersMap.get(playerID).minusNumTurns();
//		if (playersMap.get(playerID).isTurnEnded() & playerID != 1) {
//			turnEnds();
//		}
	}
	
	// called by end turn button or AI finishing - causes the next player's turn to start
	public void turnEnds() {
		currPlayerID++;
		
		// everyone has taken a turn - update the log and create a new line for the next turn
		if(currPlayerID > numPlayers){ 
			currPlayerID = 1;
			log.add(logTurn);
			logTurn = new StringBuilder();
		}
		
		// add a blank line to the log
		else{
			logTurn.append("\n");
		}
		startPlayerTurn(currPlayerID, true);
	}
	
	// AI function
	public void AIMoves(Player player) {
		
		if(!player.isActive()) return; // dead AIs don't move
		
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
					int addAmt = rand.nextInt(limit + 1);
					if(addAmt != 0) executeTerritoryAddUnits(player.getPlayerID(), t.getId(), addAmt);
					limit -= addAmt;
					
					// if will win - attack neighbouring territory
					// if will lose - 1/3 chance to attack
					
					int chooseAtk = rand.nextInt(3);
					
					if(neighbour.getNumUnits() < t.getNumUnits()){
						executeTerritoryAttack(player.getPlayerID(), neighbour.getOwner(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
					
					else if(chooseAtk == 0 & t.getNumUnits() != 0){
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
					if(frontLine & t.getNumUnits() != 0){
						executeMoveUnits(player.getPlayerID(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
				}
			}			
		}
	}
	
	// used during game resume to set conquered status of territories
	public void setTerritoriesConq(boolean[] values){
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			t.setConquered(values[i-1]);
		}
	}
	
	// used during game resume to set research levels of players
	public void setPlayerResearch(String[] values){
		for(int i = 0; i < values.length; i++){
			Player p = playersMap.get(i / 3 + 1);
			p.setResearch(Integer.parseInt(values[i]), Integer.parseInt(values[i+1]), Integer.parseInt(values[i+2]));
		}
	}
	
	// shows the current game log. Log is not saved, so it will be lost on quit.
	public String gameLog(){
		StringBuilder totalLog = new StringBuilder();
		
		for(int i = 0; i < log.size(); i++){
			totalLog.insert(0, "\n----------\n");
			totalLog.insert(0, "Turn End.");
			totalLog.insert(0, log.get(i).toString());
			totalLog.insert(0, "Turn Start.\n");
		}
		
		return totalLog.toString();
	}
	
	// used for game saving
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		/* game details occupy row 1
		 * 0 - difficulty
		 * 1 - diceLike
		 * 2 - numPlayers
		 * 3 onwards - player territory counts
		 */
		
		if(diff == 0.7){
			sb.append("0");
		}
		else if(diff == 1){
			sb.append("1");
		}
		else if(diff == 1.3){
			sb.append("2");
		}
		else if(diff == 1.5){
			sb.append("3");
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
		
		// player details on row 2
		for(int i = 1; i <= playersMap.size(); i++){
			sb.append(Integer.toString(playersMap.get(i).getNumTerritoriesOwned()));
			if(i != playersMap.size()) sb.append(",");
		}
		
		sb.append("\n");
		
		// territory conquered status on row 3
		for(int i = 1; i <= territoriesMap.size(); i++){
			sb.append(Boolean.toString(territoriesMap.get(i).isConq()));
			if(i != territoriesMap.size()) sb.append(",");
		}
		
		sb.append("\n");
		
		// player research status on row 4
		for(int i = 1; i <= playersMap.size(); i++){
			sb.append(playersMap.get(i).toString() + ",");
		}
		
		sb.append(true + "\n");
		
		// territory info on the rest of the rows
		for(int i = 1; i <= territoriesMap.size(); i++){
			sb.append(territoriesMap.get(i).toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}