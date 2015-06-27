
package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import android.util.Log;

public class Territory implements Serializable {
	
	private String name, abbrvName;
	private int id;
	private int owner;
	private int numUnits;
	private ArrayList<Integer> neighbourIDs;
	private Hashtable<Integer,Integer> attacks;  //<NeighbourID to attack, numUnits used to attack>
	
	
	/* Assumes String[] comes in the form ( name,id,owner,numUnit1,
	 * neighbour1,neighbour2...neighbourLast)Additional details to be added if needed
	 * 
	 * Some minor changes to accomodate current file format
	 */
	public Territory(String[] details){		
		neighbourIDs = new ArrayList<Integer>();
		
		name = details[1];
		abbrvName = details[2];
		id = Integer.parseInt(details[0]);
		owner = Integer.parseInt(details[8]);
		numUnits = Integer.parseInt(details[9]);  		
		for ( int i = 12; i < details.length; i++){
			neighbourIDs.add(Integer.parseInt(details[i]));
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
	
	public String getAbbrvName(){
		return abbrvName;
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
