package sg.com.yahoo.ryanlouck.orbital2015;

public class Player {
	
	private int playerID;
	private int numResources;
	private int numTurns;
	private int numTerritoriesOwned;
	
	public Player(int ID, int resources){
		playerID = ID;
		numResources = resources;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getNumResources() {
		return numResources;
	}
	
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
