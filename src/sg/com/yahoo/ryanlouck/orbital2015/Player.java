package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;

public class Player implements Serializable {
	
	static final long serialVersionUID = 1; // use this as a version number
	private int playerID, numResources;
	private int resourceRes, attackRes, defRes;
	private boolean isActive;
	
	public Player(int ID, int resources){
		playerID = ID;
		numResources = resources;
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
	
	public boolean isActive(){
		return isActive;
	}
	
	// set functions for game to manipulate
	public void addResources(int resourcesToAdd){
		numResources += resourcesToAdd;
	}
	
	public void minusResources(int resourcesToMinus){
		numResources -= resourcesToMinus;
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
}
