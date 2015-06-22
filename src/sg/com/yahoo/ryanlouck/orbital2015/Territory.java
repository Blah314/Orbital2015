package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class Territory {
	
	private String name;
	private int id;
	private int owner;
	private ArrayList<Integer> neighbours;
	private int armies;
	private Hashtable<Integer,Integer> attacks;
	
	public Territory(int ID, String NAME, ArrayList<Integer> details){
		this.id = ID;
		this.name = NAME;
		Iterator<Integer> detailsList = details.iterator();
		int count = 0;
		while(detailsList.hasNext()){
			if(count == 0){
				owner = detailsList.next();
			}
			else if(count == 1){
				armies = detailsList.next();
			}
			else{
				neighbours.add(detailsList.next());
			}
			count++;
		}
	}
	
	public void turnStart(){
		attacks.clear();
	}
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public int getOwner(){
		return owner;
	}
	
	public int getArmies(){
		return armies;
	}
	
	public ArrayList<Integer> getNeighbours(){
		return neighbours;
	}
	
	public void setAttack(int neighbour, int armies){
		if(!neighbours.contains(neighbour)){
			// shouldn't happen
		}
		else{
			attacks.put(neighbour, armies);
		}
	}
	
	public void update(int owner, int armies){
		this.owner = owner;
		this.armies = armies;
	}
}
