package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;

public class Player implements Serializable {
	
	static final long serialVersionUID = 1; // use this as a version number
	private int playerID, numResources, numTurns, numTerritoriesOwned;
	private int resourceRes, attackRes, defRes;
	private boolean isActive;
//	private ArrayList<Integer> territoriesOwnedID = new ArrayList<Integer>();
	
	public Player(int ID, int resources, int startingTerritories){
		playerID = ID;
		numResources = resources;
		numTerritoriesOwned = startingTerritories;
		isActive = true;
		resourceRes = 0;
		attackRes = 0;
		defRes = 0;
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
	
	public boolean isActive(){
		return isActive;
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
	
	public void deactivate(){
		isActive = false;
	}
	
	public void setResearch(int res, int atk, int def){
		resourceRes = res;
		attackRes = atk;
		defRes = def;
	}
	
	public int[] getResLevels(){
		return new int[]{resourceRes, attackRes, defRes};
	}
	
	public double getResMod(){
		double base = 1;
		base += (0.05)*resourceRes;
		return base;
	}
	
	public int getAtkMod(){
		int base = 100;
		base -= attackRes*2;
		return base;
	}
	
	public int getDefMod(){
		int base = 100;
		base -= defRes*3;
		return base;
	}
	
	public void research(int type){
		switch(type){
		case 1:
			resourceRes++;
			break;
		case 2:
			attackRes++;
			break;
		case 3:
			defRes++;
			break;
		}
	}
	
	public String toString(){
		return (Integer.toString(resourceRes) + "," + Integer.toString(attackRes) + "," + Integer.toString(defRes));
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
