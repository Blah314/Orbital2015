package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class Territory {
	
	private String name;
	private int id, owner;
	private int[] armies;
	private ArrayList<Integer> neighbours;
	private Hashtable<Integer,int[]> attacks;
	
	public Territory(String[] details){
		// to do - assign all territory info from the details
	}
	
	// clears all set attacks on the territory at the end of every turn
	public void turnStart(){
		attacks.clear();
	}
	
	// accessors for use to display on the buttons
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public int getOwner(){
		return owner;
	}
	
	public int[] getArmies(){
		return armies;
	}
	
	public ArrayList<Integer> getNeighbours(){
		return neighbours;
	}
	
	// set an attack on a neighbour with a certain number of armies
	public void setAttack(int neighbour, int[] armies){
		if(!neighbours.contains(neighbour)){
			// shouldn't happen
		}
		else{
			attacks.put(neighbour, armies);
		}
	}
	
	// to be called after the game calculate the outcome of a phase
	public void update(int owner, int[] armies){
		
	}
}
