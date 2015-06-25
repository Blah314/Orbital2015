package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Game {
	
	private int diff, numPlayers, currPlayerID;
	private HashMap<Integer,Player> playersMap = new HashMap<Integer,Player>();
	private boolean isDice, gameOver;
	private HashMap<Integer,Territory> territoriesMap = new HashMap<Integer,Territory>();
	
	public Game(int diff, boolean diceLike, int numPlayers, int[] startingResources, ArrayList<String[]> mapDetails){
		
		this.diff = diff;
		isDice = diceLike;
		this.numPlayers = numPlayers;
		for ( int i = 0; i<numPlayers; i++){		
			playersMap.put(i,new Player(i,startingResources[i]));
		}
		Iterator<String[]> territoryIterator = mapDetails.iterator();
		while(territoryIterator.hasNext()){
			String[] currTerritory = territoryIterator.next();
			Territory tempTerritory = new Territory(currTerritory);
			territoriesMap.put(tempTerritory.getId(), tempTerritory);
		}
	}
	

	// This method is called when a new player needs to start his turn 
	public void startPlayerTurn(int playerID){
		currPlayerID = playerID;
		Player player = playersMap.get(playerID);	
		int playerNumTerrOwned = player.getNumTerritoriesOwned();
		
		//Updates new number of turns for player
		Random rand = new Random();
		int lowerBound = playerNumTerrOwned / 2;     //Number of turns player gets...
		int upperBound = playerNumTerrOwned + 1;	//is between half of numTerrowned and numTerrowned			
		player.setNumTurns(rand.nextInt(upperBound-lowerBound) + lowerBound ); //Between upbound(exclusive) and lowbound(inclusive)
		
		//Updates new number of resources for player
		player.addResources(playerNumTerrOwned*10); // Each unit costs 1 resource to build
	}
	
	public void executeTerritoryAddUnits(int playerID, int territoryID, int numUnits){
		
		Player currPlayer = playersMap.get(playerID);
		territoriesMap.get(territoryID).addUnits(numUnits);
		currPlayer.minusResources(numUnits);
		currPlayer.minusNumTurns();	
		if ( currPlayer.isTurnEnded() ){
			turnEnds();
		}
	}
	
	public void executeTerritoryAttack(int playerID1, int playerID2, int territory1ID, int territory2ID, int numUnits){
		Player playerA = playersMap.get(playerID1); //Owner of the attacking territory
		Player playerB = playersMap.get(playerID2); //Owner of the territory being attacked
		Territory terrA = territoriesMap.get(territory1ID); //Attacking territory
		Territory terrB = territoriesMap.get(territory2ID);	//Being attacked territory	
		//RNG to make it less boring
		int numUnitsA = (int) Math.floor(terrA.getNumUnits() * ( 1 + (Math.random()/10.0) ) );  
		int numUnitsB = (int) Math.floor(terrB.getNumUnits() * ( 1 + (Math.random()/10.0) ) );
		//If the attacker has more units
		if ( numUnitsA > numUnitsB ){
			int finalNumUnits = numUnitsA - numUnitsB;    //Saves the number of surviving units
			if (finalNumUnits > terrA.getNumUnits()){     //In the case where end units are 
				finalNumUnits = terrA.getNumUnits();	  //more than at start, set to numUnits at start
			}			
			terrB.setNumUnits(finalNumUnits);   //Set number of units left after the fight on terrB
			terrB.setOwner(terrA.getOwner());   //Change owner to winner of fight
			playerA.setNumTerritoriesOwned(playerA.getNumTerritoriesOwned() + 1);  //Adjusts number of territories
			playerB.setNumTerritoriesOwned(playerB.getNumTerritoriesOwned() - 1);  //owned per player
		}
		//If defender has more units
		else if ( numUnitsB > numUnitsA ){		
			int finalNumUnits = numUnitsB - numUnitsA;			
			if ( finalNumUnits > terrB.getNumUnits()){
				finalNumUnits = terrB.getNumUnits();
			}			
			terrB.setNumUnits(finalNumUnits);
		}			
		playersMap.get(playerID1).minusNumTurns();
		if ( playerA.getNumTerritoriesOwned() == territoriesMap.size()){ //Player conquered all
			// TODO     player has won
		}
		if ( playerA.isTurnEnded() ){
			turnEnds();
		}
	}
	
	public void executeMoveUnits(int playerID, int territory1ID, int territory2ID, int numUnitsToMove){
		Territory A = territoriesMap.get(territory1ID);
		Territory B = territoriesMap.get(territory2ID);	 
		A.setNumUnits(A.getNumUnits() - numUnitsToMove);
		B.setNumUnits(B.getNumUnits() + numUnitsToMove);
		playersMap.get(playerID).minusNumTurns();
		if ( playersMap.get(playerID).isTurnEnded() ){
			turnEnds();
		}
	}
	
	public void turnEnds(){
		startPlayerTurn( (currPlayerID + 1) % (numPlayers));  //Selects next player
	}
	
	/*public int getGameWinner(){
	*	 Set<Integer> players = playersMap.keySet(); 
	*	 for(Integer playerID: players){
	*	     if ( playersMap.get(playerID).getNumTerritoriesOwned() == territoriesMap.size()){
	*	    	 return playerID;
	*	     }
	*    } 
	*	return 999;   to add future methods of winning games
	}
	*/
		
}
