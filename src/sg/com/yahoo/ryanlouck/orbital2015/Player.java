package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;

public class Player implements Serializable {
	
	static final long serialVersionUID = 1; // use this as a version number
	private int playerID, numResources, numTurns, numTerritoriesOwned;
//	private ArrayList<Integer> territoriesOwnedID = new ArrayList<Integer>();
	
	public Player(int ID, int resources, int startingTerritories){
		playerID = ID;
		numResources = resources;
		numTerritoriesOwned = startingTerritories;
	}
	
	// get functions for use by other classes
	public int getPlayerID() {
		return playerID;
	}

	public int getNumResources() {
		return numResources;
	}
	
	public int getNumTerritoriesOwned(){
		return numTerritoriesOwned;
	}
	
	public int getNumTurns(){
		return numTurns;
	}
	
	public boolean isTurnEnded(){
		if ( numTurns == 0){
			return true;
		}
		else{
			return false;
		}		
	}
	
	// set functions for game to manipulate
	public void addResources(int resourcesToAdd){
		numResources += resourcesToAdd;
	}
	
	public void minusResources(int resourcesToMinus){
		numResources -= resourcesToMinus;
	}
	
	public void minusNumTurns(){
		numTurns--;
	}
	
	public void setNumTerritoriesOwned(int numTerr){
		numTerritoriesOwned = numTerr;
	}
	
	public void setNumTurns(int newNumTurns){
		numTurns = newNumTurns;
	}
	
//	public void addTerritoryID(int ID){
//		territoriesOwnedID.add(ID);
//	}
	
//	public void removeTerritoryID(int ID){
//		territoriesOwnedID.remove(ID);
//	}
	
//	public int getTerritoryID(int index){
//		return territoriesOwnedID.get(index);		
//	}
}
