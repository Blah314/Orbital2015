package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;

public class Player implements Serializable {
	
	private int playerID;
	private int numResources;
	private int numTurns;
	private int numTerritoriesOwned;
//	private ArrayList<Integer> territoriesOwnedID = new ArrayList<Integer>();
	
	public Player(int ID, int resources, int startingTerritories){
		playerID = ID;
		numResources = resources;
		numTerritoriesOwned = startingTerritories;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getNumResources() {
		return numResources;
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
	
	public int getNumTerritoriesOwned(){
		return numTerritoriesOwned;
	}
	
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
}
