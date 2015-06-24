package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class Game {
	
	private int diff, turnNumber, phaseNumber, numPlayers;
	private int[] resources;
	private boolean diceLike, over;
	private Hashtable<Integer,Territory> territories;
	
	public Game(int diff, boolean diceLike, int numPlayers, int[] startingResources, ArrayList<String[]> mapDetails){
		this.diff = diff;
		this.diceLike = diceLike;
		this.numPlayers = numPlayers;
		this.resources = startingResources.clone();
		turnNumber = 0;
		phaseNumber = 0;
		over = false;
		territories = new Hashtable<Integer,Territory>();
		Iterator<String[]> territoryIterator = mapDetails.iterator();
		while(territoryIterator.hasNext()){
			String[] currTerritory = territoryIterator.next();
			Territory newTerritory = new Territory(currTerritory);
			territories.put(newTerritory.getId(), newTerritory);
		}
	}
	
	// phase 0 - add armies phase - player adds armies based on resources
	
	// phase 1 - attack phase - player orders attacks on enemy territories
	
	// phase 2 - upgrade phase - player does any upgrades desired
	
	// to do - check if game is over after each turn
}
