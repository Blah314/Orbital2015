
package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.Serializable;
import java.util.ArrayList;

public class Territory implements Serializable {
	
	static final long serialVersionUID = 1; // use this as a version number
	private String name, abbrvName;
	private int id, owner, numUnits;
	private ArrayList<Integer> neighbourIDs;
	private boolean recentlyConquered;
	private String[] baseDetails;
	
	// Territory detail format is in mapformat.txt	
	public Territory(String[] details){
		this.baseDetails = details.clone();
		neighbourIDs = new ArrayList<Integer>();
		recentlyConquered = false;
		
		name = details[1];
		abbrvName = details[2];
		id = Integer.parseInt(details[0]);
		owner = Integer.parseInt(details[8]);
		numUnits = Integer.parseInt(details[9]);  		
		for (int i = 12; i < details.length; i++){
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
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 12; i++){
			switch(i){
			case 0:
				sb.append(Integer.toString(id));
				break;
			case 1:
				sb.append(name);
				break;
			case 2:
				sb.append(abbrvName);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:	
				sb.append(baseDetails[i]);
				break;
			case 8:
				sb.append(Integer.toString(owner));
				break;
			case 9:
				sb.append(Integer.toString(numUnits));
				break;
			case 10:
			case 11:
				sb.append("0");
				break;
			}
			sb.append(",");
		}
		
		for(int j = 0; j < neighbourIDs.size(); j++){
			sb.append(Integer.toString(neighbourIDs.get(j)));
			if(j != neighbourIDs.size() - 1) sb.append(",");
		}
		
		return sb.toString();
	}
}
