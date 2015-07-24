package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {
	
	private float mx, my, currX, currY;
	private int buttonScale;
	private ScrollView vScroll;
	private HorizontalScrollView hScroll;
	private ViewGroup map;
	private TextView res, obj, turnsLeft;
	private Button endTurn;
	
	private String[] levelDetails;
	private ArrayList<String[]> territoryDetails;
	
	private int level, diff, numTerritories, turnNum, numRegions, numPlayers, objective, objectiveAmt;
	private boolean over, resumed;
	private boolean diceLike, armies, upgrades, capital, fow, regions, hardcore;
	private Game game;
	private HashMap<Integer, Territory> territories;
	private HashMap<Integer, Player> players;
	
	private Hashtable<Integer,PorterDuffColorFilter> ColorMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// all the views
		vScroll = (ScrollView) findViewById(R.id.vScroll1);
		hScroll = (HorizontalScrollView) findViewById(R.id.hScroll1);
		map = (ViewGroup) findViewById(R.id.map);
		res = (TextView) findViewById(R.id.resourceView);
		obj = (TextView) findViewById(R.id.objectiveView);
		turnsLeft = (TextView) findViewById(R.id.turnView);
		endTurn = (Button) findViewById(R.id.endTurnButton);
		
		// color definitions - more to come
		ColorMap = new Hashtable<Integer,PorterDuffColorFilter>();
		ColorMap.put(0,new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.OVERLAY));
		ColorMap.put(1,new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.OVERLAY));
		ColorMap.put(2,new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.OVERLAY));
		ColorMap.put(3,new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY));
		ColorMap.put(4,new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.OVERLAY));
		ColorMap.put(5,new PorterDuffColorFilter(Color.CYAN, PorterDuff.Mode.OVERLAY));
		ColorMap.put(6,new PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.OVERLAY));
		ColorMap.put(7,new PorterDuffColorFilter(Color.rgb(255,165,0), PorterDuff.Mode.OVERLAY));
		ColorMap.put(8,new PorterDuffColorFilter(Color.rgb(165,42,42), PorterDuff.Mode.OVERLAY));
		ColorMap.put(9,new PorterDuffColorFilter(Color.rgb(160,32,240), PorterDuff.Mode.OVERLAY));
		ColorMap.put(999, new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.OVERLAY)); // 999 is for fog of war
		
		over = false;
		
		// retrieving preferred button scale
		SharedPreferences settings = getSharedPreferences("options", 0);
		buttonScale = settings.getInt("tSize", 1);
		
		// checking if the game was resumed
		Bundle details = getIntent().getExtras();
		resumed = details.getBoolean("resumed", false);
		
		// decide which function to call
		if(!resumed){
			startNewGame();
		}
		else{
			continueGame();
		}
	}
	
	// Initialization method - creates the map and a new game
	public void startNewGame(){
		
		// getting the stuff passed from CustomisationActivity
		Bundle details = getIntent().getExtras();
		level = details.getInt("lvl", 1);
		diff = details.getInt("diff", 0);
		diceLike = details.getBoolean("dice", false);
		armies = details.getBoolean("armies", false);
		upgrades = details.getBoolean("upgrades", false);
		capital = details.getBoolean("capital", false);
		fow = details.getBoolean("fow", false);
		regions = details.getBoolean("regions", false);
		hardcore = details.getBoolean("hardcore", false);
		levelDetails = details.getStringArray("levelDetails");

		territoryDetails = new ArrayList<String[]>();
		turnNum = 1;
		
		// loading up the map and getting the details of all territories
		AssetManager am = this.getAssets();
		try{
			InputStream is = am.open("maps/" + Integer.toString(level) + ".txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while(br.ready()){
				String[] currentTerritoryDetails = br.readLine().split(",");
				territoryDetails.add(currentTerritoryDetails);
			}
			is.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		objective = Integer.parseInt(levelDetails[3]);
		objectiveAmt = Integer.parseInt(levelDetails[4]);
		
		setFields();
		loadTerritoryButtons();
		
		// create new game
		numPlayers = Integer.parseInt(levelDetails[5]);
		numRegions = Integer.parseInt(levelDetails[7]);
		int[] startingRes = new int[levelDetails.length - 8];
		for(int i = 8; i < levelDetails.length; i++){
			startingRes[i-8] = Integer.parseInt(levelDetails[i]);
		}
		
		game = new Game(false, numPlayers, numRegions,
			diff, diceLike, armies, capital, regions, 
			startingRes, territoryDetails);
		
		territories = game.getTerritories();
		numTerritories = territories.size();
		players = game.getPlayers();
		
		assignTerritoryButtons();
		setEndButton();
		
		game.startPlayerTurn(1, true);
		
		update();
	}
	
	// reads the data given by MainActivity and creates an in-progress game
	public void continueGame(){
		Bundle details = getIntent().getExtras();
		turnNum = details.getInt("turnNum", 1);
		level = details.getInt("lvl", 1);
		diff = details.getInt("diff", 1);
		diceLike = details.getBoolean("dice", false);
		fow = details.getBoolean("fow", false);
		capital = details.getBoolean("capital", false);
		upgrades = details.getBoolean("upgrades", false);
		hardcore = details.getBoolean("hardcore", false);
		numPlayers = details.getInt("numPlayers", 2);
		numRegions = details.getInt("numRegions", 3);
		regions = details.getBoolean("regions", false);
		int[] res = details.getIntArray("res");
		boolean[] terrConq = details.getBooleanArray("conq");
		String[] upgradeVals = details.getStringArray("resValues");
		territoryDetails = (ArrayList<String[]>) details.getSerializable("rest");
		
		// loading up the level details to get some basic info
		AssetManager am = this.getAssets();
		try{
			InputStream is = am.open("levelDetails.txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			int lineCount = 0;
			while(br.ready()){
				String[] tempLevel = br.readLine().split(",");
				if(lineCount == level){
					levelDetails = tempLevel;
				}
				lineCount++;
			}
			is.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		objective = Integer.parseInt(levelDetails[3]);
		objectiveAmt = Integer.parseInt(levelDetails[4]);
		
		setFields();
		loadTerritoryButtons();
		
		game = new Game(true, numPlayers, numRegions,
				diff, diceLike, armies, capital, regions, 
				res, territoryDetails);
		
		game.setTerritoriesConq(terrConq);
		game.setPlayerResearch(upgradeVals);
		territories = game.getTerritories();
		numTerritories = territories.size();
		players = game.getPlayers();
		
		assignTerritoryButtons();
		setEndButton();
		
		game.startPlayerTurn(1, false);
		
		update();
	}
	
	// sets the title and objective fields
	public void setFields(){
		getActionBar().setTitle(levelDetails[2]);
		
		switch(objective){
		case 0:
			obj.setText("Objective: Defeat all opponents.");
			break;
		case 1:
			obj.setText("Objective: Conquer " + Integer.toString(objectiveAmt) + " territories.");
			break;
		case 2:
			obj.setText("Objective: Survive for " + Integer.toString(objectiveAmt) + " turns.");
			break;
		case 3:
			obj.setText("Objective: Eliminate opponents in " + Integer.toString(objectiveAmt) + " turns.");
			break;
		}
		obj.setGravity(17);
	}
	
	// creates the territory buttons
	public void loadTerritoryButtons(){
		int i = resumed ? 0 : 1;
		for(; i < territoryDetails.size(); i++){
			String[] tDetails = territoryDetails.get(i);
			
			// creates the territory buttons and puts them at their corresponding location
			Button territoryButton = new Button(this);
			territoryButton.setText(tDetails[2]);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100*buttonScale, 100*buttonScale);
			params.leftMargin = Integer.parseInt(tDetails[3])*buttonScale;
			params.topMargin = Integer.parseInt(tDetails[4])*buttonScale;
			map.addView(territoryButton, params);
		}
	}
	
	// assigns the territory buttons
	public void assignTerritoryButtons(){
		for(int i = 0; i < numTerritories; i++){
			Button b = (Button) map.getChildAt(i);
			final Territory t = territories.get(i+1);
			b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(t.getOwner() != 1){
						Context c = getApplicationContext();
						CharSequence text = getResources().getString(R.string.reject);
						int duration = Toast.LENGTH_SHORT;
						
						Toast t = Toast.makeText(c, text, duration);
						t.show();
					}
					
					else if(t.isConq()){
						Context c = getApplicationContext();
						CharSequence text = getResources().getString(R.string.conquered_territory);
						int duration = Toast.LENGTH_SHORT;
						
						Toast t = Toast.makeText(c, text, duration);
						t.show();
					}
					
					else{
						Intent TerritoryLaunch = new Intent(getApplicationContext(), TerritoryActivity.class);
						TerritoryLaunch.putExtra("territory", t);
						TerritoryLaunch.putExtra("res", game.getPlayers().get(1).getNumResources());
						TerritoryLaunch.putExtra("game", game);
						startActivity(TerritoryLaunch);
					}
				}
			});
		}
	}
	
	// assigns the end turn button
	public void setEndButton(){
		endTurn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				turnNum++;
				game.turnEnds();
				update();
			}
		});
	}
	
	// updates the values (and colours) on each button and the text fields at the end of every move
	public void update(){
		territories = game.getTerritories();
		boolean won, lost;
		
		won = true;
		lost = true;
		int territoriesOwned = 0;
		
		// display with fog of war
		if(fow){
			boolean[] visibility = new boolean[numTerritories];
			Arrays.fill(visibility, false);
			
			// finds all territories that are visible to the player
			for(int i = 1; i <= numTerritories; i++){
				Territory t = territories.get(i);
				if(t.getOwner() == 1){
					visibility[i-1] = true;
					ArrayList<Integer> neighbours = t.getNeighbourIDs();
					Iterator<Integer> n = neighbours.iterator();
					while(n.hasNext()){
						int next = n.next();
						visibility[next - 1] = true;
					}
				}
			}
			
			// lights up visible territories + win/lose checking
			for(int i = 0; i < numTerritories; i++){
				Button b = (Button) map.getChildAt(i);
				Territory t = territories.get(i+1);
				int owner = t.getOwner();
				if(owner != 1 & owner != 0){
					won = false;
				}
				else if(owner == 1){
					lost = false;
					territoriesOwned++;
				}
				
				if(visibility[i]){
					if(t.isCapital() & capital){ // capitals get asterisks
						b.setText("*" + t.getAbbrvName() + "*" + "\n" + t.getNumUnits());
					}
					else{
						b.setText(t.getAbbrvName() + "\n" + t.getNumUnits());
					}
					b.getBackground().setColorFilter(ColorMap.get(t.getOwner()));
				}
				
				else { // invisible territories are black and have a ?
					b.setText(t.getAbbrvName() + "\n" + "?");
					b.getBackground().setColorFilter(ColorMap.get(999));
				}
			
				res.setText("Resources:\n" + players.get(1).getNumResources());
				turnsLeft.setText("Turn Number:\n" + Integer.toString(turnNum));
			}
		}
		
		// display without fog of war
		else{
			for(int i = 0; i < numTerritories; i++){
				Button b = (Button) map.getChildAt(i);
				Territory t = territories.get(i+1);
				int owner = t.getOwner();
				if(owner != 1 & owner != 0){
					won = false;
				}
				else if(owner == 1){
					lost = false;
					territoriesOwned++;
				}
				
				if(t.isCapital() & capital){ // capitals get asterisks
					b.setText("*" + t.getAbbrvName() + "*" + "\n" + t.getNumUnits());
				}
				else{
					b.setText(t.getAbbrvName() + "\n" + t.getNumUnits());
				}
				
				b.getBackground().setColorFilter(ColorMap.get(t.getOwner()));
			
				res.setText("Resources:\n" + players.get(1).getNumResources());
				turnsLeft.setText("Turn Number:\n" + Integer.toString(turnNum));
			}
		}
		
		// if you capital is destroyed - game over
		if(!game.getPlayers().get(1).isActive()){
			lost = true;
		}
		
		// if you have destroyed all other player's capitals - you win
		HashMap<Integer, Player> players = game.getPlayers();
		boolean eliminated = true;
		for(int i = 2; i <= players.size(); i++){
			Player p = players.get(i);
			if(p.isActive()) eliminated = false;
		}
		
		if(eliminated) won = true;
		
		// special objective win/lose checking
		if(objective == 1){
			if(territoriesOwned >= objectiveAmt){
				won = true;
			}
		}
		
		if(objective == 2){
			if(turnNum >= objectiveAmt){
				won = true;
			}
		}
		
		if(objective == 3){
			if(turnNum >= objectiveAmt){
				lost = true;
			}
		}
		
		// end game screens
		if(won){
			// check if current difficulty is higher than previously attained
			SharedPreferences settings = getSharedPreferences("levels", 0);
			int achievementLevel = settings.getInt("level" + Integer.toString(level), 0);
			boolean improved = false;
			if(achievementLevel < diff + 1){
				achievementLevel = diff + 1;
				improved = true;
			}
			
			// if there is an improvement - save it
			SharedPreferences.Editor editor = settings.edit();
		    editor.putInt("level" + Integer.toString(level), achievementLevel);
		    editor.commit();
		    
			final FragmentManager fm = getFragmentManager();
			EndGameFragment win = improved ? new EndGameFragment(true, diff + 1) : new EndGameFragment(true, 0);
			win.show(fm, "endGame");
			over = true;
		}
		
		if(!won & lost){
			final FragmentManager fm = getFragmentManager();
			EndGameFragment lose = new EndGameFragment(false, 0);
			lose.show(fm, "endGame");
			over = true;
		}
	}
	
	// handles actions from player
	public void onNewIntent(Intent intent){
		Bundle details = intent.getExtras();
		if(details == null) return;
		boolean isUpgrade = details.getBoolean("isupgrade", false);
		
		if(isUpgrade){
			int cost = details.getInt("cost", 0);
			int type = details.getInt("upgrade", 0);
			Player p = game.getPlayers().get(1);
			
			p.research(type);
			p.minusResources(cost);
		}
		
		else{
			
			boolean add = details.getBoolean("add", false);
			boolean move = details.getBoolean("move", false);
			boolean attack = details.getBoolean("attack", false);
			int target = details.getInt("target", 0);
			int requested = details.getInt("request", 0);
			int origin = details.getInt("origin", 0);
			
			if(add){
				game.executeTerritoryAddUnits(1, origin, requested);
			}
			else if(move){
				game.executeMoveUnits(1, origin, target, requested);
			}
			else if(attack){
				game.executeTerritoryAttack(1, territories.get(target).getOwner(), origin, target, requested);
			}
		}	
		
		update();
		
		// game saving code
		if(!over & !hardcore){
			String gameSave = game.toString();
			FileOutputStream fos;
			try{
				fos = openFileOutput("savegame",Context.MODE_PRIVATE);
				/*
				 * Line 0 contains global data
				 * 0 - turnNumber
				 * 1 - levelNumber
				 * 2 - hardcore
				 * 3 - fog of war
				 * 4 - capital
				 * 5 - upgrades
				 * 6 - numRegions
				 * 7 - regions
				 */
				fos.write((Integer.toString(turnNum) + "," + Integer.toString(level) + "," + 
						Boolean.toString(hardcore) + "," + Boolean.toString(fow) + "," + 
						Boolean.toString(capital) + "," + Boolean.toString(upgrades) + "," + 
						Integer.toString(numRegions) + "," + Boolean.toString(regions) + "\n").getBytes());
				// line 1 onwards contains the game save
				fos.write(gameSave.getBytes());
				fos.flush();
				fos.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	protected void onStop(){
		super.onStop();
		if(over){
			this.finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		
		// log button is pressed - display the log dialog or a message when fow is enabled
		case R.id.game_log:
			LogScreen ls;
			if(fow){
				ls = new LogScreen(getResources().getString(R.string.log_blocked));
			}
			else{
				ls = new LogScreen(game.gameLog());
			}
			FragmentManager fm = getFragmentManager();
			ls.show(fm, "log");
			return true;
		
		case R.id.research:
			if(upgrades){
				Player p = game.getPlayers().get(1);
				Intent upgradesLaunch = new Intent(getApplicationContext(), UpgradesActivity.class);
				upgradesLaunch.putExtra("player", p);
				startActivity(upgradesLaunch);
			}
			else{
				Context c = getApplicationContext();
				CharSequence text = getResources().getString(R.string.upgrades_off);
				int duration = Toast.LENGTH_SHORT;
				
				Toast t = Toast.makeText(c, text, duration);
				t.show();
			}
			return true;

		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// map scrolling code - it's MAGIC! Don't touch!
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currX = event.getX();
                currY = event.getY();
                vScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                hScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                mx = currX;
                my = currY;
                break;
            case MotionEvent.ACTION_UP:
                currX = event.getX();
                currY = event.getY();
                vScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                hScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                break;
        }

        return true;
    }
}
