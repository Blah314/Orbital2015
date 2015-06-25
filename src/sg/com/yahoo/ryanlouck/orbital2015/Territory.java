package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.Hashtable;

import android.util.Log;

public class Territory {
	
	private String name;
	private int id;
	private int owner;
	private int numUnits;
	private ArrayList<Integer> neighbourIDs;
	private Hashtable<Integer,Integer> attacks;  //<NeighbourID to attack, numUnits used to attack>
	
	
	/* Assumes String[] comes in the form ( name,id,owner,numUnit1,
	 * neighbour1,neighbour2...neighbourLast)Additional details to be added if needed
	 */
	public Territory(String[] details){
		
		name = details[0];
		id = Integer.parseInt(details[1]);
		owner = Integer.parseInt(details[2]);
		numUnits = Integer.parseInt(details[3]);  		
		for ( int i = details.length-1; i>3; i-- ){
			neighbourIDs.add(Integer.parseInt(details[i])); //Adds neighbourIDs to arrayList ( reverse order makes no difference)
		}
		
	}
	
	public void addUnits(int unit){
		numUnits += unit;			
	}
	
	// clears all set attacks on the territory at the start of every turn
	//public void turnStart(){
	//	attacks.clear();		
	//}
	
	// Accessors for use to display on the buttons
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public int getOwner(){
		return owner;
	}
	
	public int getNumUnits(){
		return numUnits;
	}
	
	public void setOwner(int ownerID){
		owner = ownerID;
	}	
	
	public void setNumUnits(int toSetUnits){
		numUnits = toSetUnits;
	}
	
	public ArrayList<Integer> getNeighbourIDs(){
		return neighbourIDs;
	}
	
	/* set an attack on a neighbour with a certain number of units
	 * public void setAttack(int neighbour, int numAttackUnits){
		if(!neighbourIDs.contains(neighbour)){
			Log.d("setAttackInTerritoryClass","Setting an attack on a territory which is not adjacent to this");
		}
		else {
			attacks.put(neighbour, numAttackUnits);
		}
	}	
	 
	// to be called after the game calculate the outcome of a phase
	public void update(int owner, int numUnits){
	
	}
	*/	
	}
