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
	private int numPlayers, currPlayerID, numRegions;
	private ArrayList<StringBuilder> log;
	private StringBuilder logTurn;
	private HashMap<Integer, Player> playersMap = new HashMap<Integer, Player>();
	private boolean isDice, capital, regions;
	private double diff;
	private HashMap<Integer, Territory> territoriesMap = new HashMap<Integer, Territory>();
	private Random rand = new Random();
	private HashMap<Integer, String> AINames = new HashMap<Integer, String>();

	public Game(int diff, boolean diceLike, boolean capitals, boolean regions, int numPlayersToAdd, 
			int[] startingResources, boolean fromSave, int[] savedTerrs, int numRegions, 
			ArrayList<String[]> mapDetails) {
		
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
		this.regions = regions;
		this.numPlayers = numPlayersToAdd;
		this.numRegions = numRegions;
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
			int res = (int) (playerNumTerrOwned * 10 * diff * player.getResMod());
			player.addResources(res);
			if(regions) player.addResources(checkRegions(player.getPlayerID()));
			AIMoves(player);
			turnEnds();
		}
		else if(addRes){ // player resource adding
			int res = (int) (playerNumTerrOwned * 10 * player.getResMod());
			player.addResources(res);
			logTurn.append("You gained " + Integer.toString(res) + " resources from " 
			+ Integer.toString(playerNumTerrOwned) + " territories.\n");
			if(regions){
				int bonus = checkRegions(1);
				player.addResources(bonus);
				logTurn.append("You gained " + Integer.toString(bonus) + " bonus resources from holding regions.\n");
			}	
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
			
		// keep fighting till one person's units reaches 0
		while(numUnits != 0 & numUnitsB != 0){
			
			// diceLike logic
			if(isDice){
				int rollAttack = rand.nextInt(6);
				int rollDef = rand.nextInt(6);
				
				// attacker only wins if his roll is higher
				if(rollAttack > rollDef){
					// check playerB's army survival chances - generate a random number from 1 to 100
					int survivalVal = rand.nextInt(100) + 1;
					
					// playerB's army survives due to upgrades
					if(playerID2 != 0 && survivalVal > playerB.getDefMod()){
						continue;
					}
					
					// playerB's army does not survive - see if playerA can get a 2x kill
					else{
						int doubleKillVal = rand.nextInt(100) + 1;
						if(doubleKillVal > playerA.getAtkMod()){ // playerA gets a 2x kill
							numUnitsB -= 2;
							if(numUnitsB < 0){
								numUnitsB = 0;
							}
						}
						else{ // normal kill
							numUnitsB--;
						}
					}
				}
				
				else{ // attacker lost
					numUnits--;
				}
			}
			
			// non-diceLike logic
			else{
				// check playerB's army survival chances - generate a random number from 1 to 100
				int survivalVal = rand.nextInt(100) + 1;
				
				// playerB's army survives due to upgrades
				if(playerID2 != 0 && survivalVal > playerB.getDefMod()){
					continue;
				}
				
				// playerB's army does not survive - see if playerA can get a 2x kill
				else{
					int doubleKillVal = rand.nextInt(100) + 1;
					if(doubleKillVal > playerA.getAtkMod()){ // playerA gets a 2x kill
						numUnitsB -= 2;
						numUnits--;
						if(numUnitsB < 0){
							numUnitsB = 0;
						}
					}
					else{ // normal kill
						numUnitsB--;
						numUnits--;
					}
				}
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
//			playerA.addTerritoryID(territory2ID); //owned per player
			
			if(playerID2 != 0){ // player 0 is the neutral party - no player representing it
				playerB.setNumTerritoriesOwned(playerB.getNumTerritoriesOwned() - 1);
//				playerB.removeTerritoryID(territory2ID);
			}	
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
		ArrayList<Territory> frontLine = new ArrayList<Territory>();
		ArrayList<Territory> danger = new ArrayList<Territory>();		
		int limit = player.getNumResources() / 10;
		int partitions = 0;
		
		if(limit < 6){
			partitions = 1;
		}
		else if(limit < 15){
			partitions = 2;
		}
		else{
			partitions = 3;
		}
		
		// stage 1 - find territories that I have
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			if(t.getOwner() == player.getPlayerID()){
				owned.add(t);
			}
		}
		System.out.println("Executed Stage 1");
		
		Iterator<Territory> tIterator = owned.iterator();
		
		// stage 2 - find the territories that are bordering opponents and among those which are in danger
		while(tIterator.hasNext()){
			Territory t = tIterator.next();
			ArrayList<Integer> n = t.getNeighbourIDs();
			Iterator<Integer> nI = n.iterator();
			
			while(nI.hasNext()){
				int currTerr = nI.next();
				Territory neighbour = territoriesMap.get(currTerr);
				
				if(neighbour.getOwner() != player.getPlayerID()){
					
					if(!frontLine.contains(t)) frontLine.add(t);
					if(neighbour.getNumUnits() > t.getNumUnits() & neighbour.getOwner() != 0){
						if(!danger.contains(t)) danger.add(t);
					}
				}
			}			
		}
		System.out.println("Executed Stage 2");
		
		// stage 3 - for the territories in danger, add armies to defend/attack back
		Collections.shuffle(danger);
		Iterator<Territory> dangerTs = danger.iterator();
		
		while(dangerTs.hasNext()){			
			Territory t = dangerTs.next();
			if(partitions > 0) {
				int add = limit / partitions;
				executeTerritoryAddUnits(player.getPlayerID(), t.getId(), add);
				limit -= add;
				partitions--;
			}
		}
		System.out.println("Executed Stage 3");
		
		// stage 4 - if there are resources left, continue to reinforce frontLine territories	
		Collections.shuffle(frontLine);
		Iterator<Territory> frontLineTs = frontLine.iterator();
		
		while(frontLineTs.hasNext() & partitions > 0){
			Territory t = frontLineTs.next();
			int add = limit / partitions;
			executeTerritoryAddUnits(player.getPlayerID(), t.getId(), add);
			limit -= add;
			partitions--;
		}
		System.out.println("Executed Stage 4");
		
		// stage 5 - go through the frontLine territories and execute attacks
		Collections.shuffle(frontLine);
		frontLineTs = frontLine.iterator();
		while(frontLineTs.hasNext()){
			Territory t = frontLineTs.next();
			if(t.getNumUnits() == 0) continue;
			ArrayList<Integer> n = t.getNeighbourIDs();
			Collections.shuffle(n);
			Iterator<Integer> nI = n.iterator();
			
			while(nI.hasNext()){
				Territory neighbour = territoriesMap.get(nI.next());
				if(neighbour.getOwner() != t.getOwner()){
					if(t.getNumUnits() > neighbour.getNumUnits()){
						executeTerritoryAttack(player.getPlayerID(), neighbour.getOwner(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
					else{
						int attackChance = rand.nextInt(3);
						if(attackChance == 0 & t.getNumUnits() != 0) executeTerritoryAttack(player.getPlayerID(), neighbour.getOwner(), t.getId(), neighbour.getId(), t.getNumUnits());
					}
				}
			}
		}
		System.out.println("Executed Stage 5");
		
		// stage 6 - get new owned, frontLine and danger territories
		owned.clear();
		frontLine.clear();
		danger.clear();
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			if(t.getOwner() == player.getPlayerID()){
				owned.add(t);
			}
		}
		
		tIterator = owned.iterator();
		
		while(tIterator.hasNext()){
			Territory t = tIterator.next();
			ArrayList<Integer> n = t.getNeighbourIDs();
			Iterator<Integer> nI = n.iterator();
			
			while(nI.hasNext()){
				int currTerr = nI.next();
				Territory neighbour = territoriesMap.get(currTerr);
				
				if(neighbour.getOwner() != player.getPlayerID()){
					
					if(!frontLine.contains(t)) frontLine.add(t);
					if(neighbour.getNumUnits() > t.getNumUnits() & neighbour.getOwner() != 0){
						if(!danger.contains(t)) danger.add(t);
					}
				}
			}			
		}
		System.out.println("Executed Stage 6");
		
		// stage 7 - move any armies not in a frontLine territory back to the frontLine if 1 step away
		Collections.shuffle(owned);
		Iterator<Territory> newOwned = owned.iterator();
		
		while(newOwned.hasNext()){
			Territory t = newOwned.next();
			if(frontLine.contains(t)) continue;
			if(t.getNumUnits() == 0) continue;
			ArrayList<Integer> tN = t.getNeighbourIDs();
			Collections.shuffle(tN);
			Iterator<Integer> nI = tN.iterator();
			while(nI.hasNext()){
				Territory n = territoriesMap.get(nI.next());
				if(!frontLine.contains(n)){
					continue;
				}
				else{
					executeMoveUnits(player.getPlayerID(), t.getId(), n.getId(), t.getNumUnits());
				}
			}
		}
		System.out.println("Executed Stage 7");
	}
	
	// see how many regions a certain player owns, and then determines the number of bonus resources he gets
	public int checkRegions(int playerID){
		boolean[] ownedRegions = new boolean[numRegions];
		Arrays.fill(ownedRegions, true);
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			if(t.getOwner() != playerID){
				ownedRegions[t.getRegion() - 1] = false;
			}
		}
		
		int ownedCount = 0;
		for(boolean b : ownedRegions){
			if(b == true){
				ownedCount++;
			}
		}
		
		return ownedCount*10;
	}
	
	public ArrayList<Territory> getRegion(int region){
		ArrayList<Territory> regionTs = new ArrayList<Territory>();
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			if(t.getRegion() == region){
				regionTs.add(t);
			}
		}
		return regionTs;
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
		for(int i = 0; i < values.length; i += 3){
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
			sb.append(playersMap.get(i).toString());
			if(i != playersMap.size()) sb.append(",");
		}
		
		sb.append("\n");
		
		// territory info on the rest of the rows
		for(int i = 1; i <= territoriesMap.size(); i++){
			sb.append(territoriesMap.get(i).toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}