
package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;
import java.util.ArrayList;

public class Territory implements Serializable {
	
	static final long serialVersionUID = 1; // use this as a version number
	private String name, abbrvName;
	private int id, owner, numUnits;
	private ArrayList<Integer> neighbourIDs;
	private boolean recentlyConquered;
	
	// Territory detail format is in mapformat.txt	
	public Territory(String[] details){		
		neighbourIDs = new ArrayList<Integer>();
		recentlyConquered = false;
		
		name = details[1];
		abbrvName = details[2];
		id = Integer.parseInt(details[0]);
		owner = Integer.parseInt(details[8]);
		numUnits = Integer.parseInt(details[9]);  		
		for ( int i = 12; i < details.length; i++){
			neighbourIDs.add(Integer.parseInt(details[i]));
		}	
	}
	
	// get functions for use by other classes
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
	
	public boolean isConq(){
		return recentlyConquered;
	}
	
	public ArrayList<Integer> getNeighbourIDs(){
		return neighbourIDs;
	}
	
	// set functions for game to manipulate	
	public void setConquered(boolean setting){
		recentlyConquered = setting;
	}
	
	public void addUnits(int unit){
		numUnits += unit;			
	}
	
	public void setOwner(int ownerID){
		owner = ownerID;
	}	
	
	public void setNumUnits(int toSetUnits){
		numUnits = toSetUnits;
	}
}
