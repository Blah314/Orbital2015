package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.Hashtable;

public class Game {
	
	private int diff;
	private boolean diceLike;
	private int numTerritories;
	private int numPlayers;
	private int turnNumber;
	private Territory[] territories;
	
	public Game(int diff, boolean diceLike, int numPlayers, int[][] mapDetails){
		this.diff = diff;
		this.diceLike = diceLike;
		this.numPlayers = numPlayers;
	}
}
