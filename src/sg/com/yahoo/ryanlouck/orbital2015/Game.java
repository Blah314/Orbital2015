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
	private boolean isDice, armies, capital, regions;
	private double diff;
	private HashMap<Integer, Territory> territoriesMap = new HashMap<Integer, Territory>();
	private Random rand = new Random();
	private HashMap<Integer, String> AINames = new HashMap<Integer, String>();

	public Game(boolean fromSave, int numPlayers, int numRegions, 
			int diff, boolean dice, boolean armies, boolean capitals, boolean regions,
			int[] startingResources, ArrayList<String[]> mapDetails) {
		
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
		
		isDice = dice;
		this.armies = armies;
		capital = capitals;
		this.regions = regions;
		this.numPlayers = numPlayers;
		this.numRegions = numRegions;
		Iterator<String[]> territoryIterator = mapDetails.iterator();
		if(!fromSave) territoryIterator.next(); // removes invalid first row
		
		// create territories	
		while (territoryIterator.hasNext()) {
			String[] currTerritory = territoryIterator.next();
			Territory tempTerritory = new Territory(currTerritory);
			territoriesMap.put(tempTerritory.getId(), tempTerritory);
		}
		
		// creating the players
		for (int i = 1; i <= numPlayers; i++) {
			playersMap.put(i, new Player(i, startingResources[i-1]));
		}
		
		// creates a new log and a new log line
		log = new ArrayList<StringBuilder>();
		logTurn = new StringBuilder();
		
		// names for the AI players
		AINames.put(2, "Kenneth");
		AINames.put(3, "Nicholas");
		AINames.put(4, "Ryan");
		AINames.put(5, "QX");
		AINames.put(6, "GQ");
		AINames.put(7, "Bili");
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
		
		// resets conquered attribute for each territory
		if(addRes){
			for(int i = 1; i <= territoriesMap.size(); i++){
				Territory t = territoriesMap.get(i);
				t.setConquered(false);
			}
		}
		
		if(currPlayerID != 1){ // AI resource adding and movement
			int[] res = addRes(currPlayerID);
			player.addResources(res[0]);
			if(regions) player.addResources(res[1]);
			AIMoves(player);
			turnEnds();
		}
		else if(addRes){ // player resource adding
			int[] res = addRes(currPlayerID);
			player.addResources(res[0]);
			logTurn.append("You gained " + Integer.toString(res[0]) + " resources from your territories.\n");
			if(regions){
				player.addResources(res[1]);
				logTurn.append("You gained " + Integer.toString(res[1]) + " bonus resources from holding regions.\n");
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
	
	// determines the number of resources a player gets at the end of every turn - using both territories owned and regions owned
	public int[] addRes(int playerID){
		int res = 0;
		boolean[] ownedRegions = new boolean[numRegions];
		Arrays.fill(ownedRegions, true);
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			if(t.getOwner() != playerID){
				ownedRegions[t.getRegion() - 1] = false;
			}
			else{
				System.out.println(t.getValue());
				res += t.getValue();
			}
		}
		
		int ownedCount = 0;
		for(boolean b : ownedRegions){
			if(b == true){
				ownedCount++;
			}
		}
		
		return new int[]{res, ownedCount*10};
	}
	
	// used during game resume to set conquered status of territories
	public void setTerritoriesConq(boolean[] values){
		for(int i = 1; i <= territoriesMap.size(); i++){
			Territory t = territoriesMap.get(i);
			t.setConquered(values[i-1]);
		}
	}
	
	// used during game resume to set research levels and activity status of players
	public void setPlayerResearch(String[] values){
		for(int i = 0; i < values.length; i += 4){
			Player p = playersMap.get(i / 4 + 1);
			if(values[i].equals("false")){
				p.deactivate();
			}
			p.setResearch(Integer.parseInt(values[i+1]), Integer.parseInt(values[i+2]), Integer.parseInt(values[i+3]));
		}
	}
	
	// returns all territories in a particular region
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
		
		// territory conquered status on row 2
		for(int i = 1; i <= territoriesMap.size(); i++){
			sb.append(Boolean.toString(territoriesMap.get(i).isConq()));
			if(i != territoriesMap.size()) sb.append(",");
		}
		
		sb.append("\n");
		
		// player research & activity status on row 3
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