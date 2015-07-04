package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

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
	private boolean resumed;
	private ScrollView vScroll;
	private HorizontalScrollView hScroll;
	private ViewGroup map;
	private TextView res, obj, turnsLeft;
	private Button endTurn;
	
	private String[] levelDetails;
	private ArrayList<String[]> territoryDetails;
	
	private int level, diff, numTerritories, turnNum;
	private boolean diceLike, over;
	private Game game;
	private HashMap<Integer, Territory> territories;
	private HashMap<Integer, Player> players;
	
	public Hashtable<Integer,Integer> ColorMap;
	
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
		ColorMap = new Hashtable<Integer,Integer>();
		ColorMap.put(0,Color.GRAY);
		ColorMap.put(1,Color.BLUE);
		ColorMap.put(2,Color.RED);
		ColorMap.put(3,Color.GREEN);
		ColorMap.put(4,Color.YELLOW);
		
		over = false;
		
		// retrieving preferred button scale
		SharedPreferences settings = getSharedPreferences("options", 0);
		buttonScale = settings.getInt("tSize", 1);
		
		// checking if the game was resumed
		Bundle details = getIntent().getExtras();
		resumed = details.getBoolean("resumed", false);
		
		if(!resumed){
			startNewGame();
		}
		else{
			continueGame();
		}
	}
	
	// Initialization method - creates the map and a new game
	public void startNewGame(){
		
		// getting the stuff passed from CustomisationActivity or ContinueGameButton
		Bundle details = getIntent().getExtras();
		level = details.getInt("lvl", 1);
		diff = details.getInt("diff", 0);
		diceLike = details.getBoolean("dice", false);
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
		
		setFields();
		loadTerritoryButtons();
		
		// create new game	
		int[] startingRes = new int[levelDetails.length - 8];
		for(int i = 8; i < levelDetails.length; i++){
			startingRes[i-8] = Integer.parseInt(levelDetails[i]);
		}
		
		game = new Game(diff, diceLike, Integer.parseInt(levelDetails[5]), startingRes, false, startingRes, territoryDetails);
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
		int numPlayers = details.getInt("numPlayers", 2);
		int[] res = details.getIntArray("res");
		int[] terr = details.getIntArray("terr");
		territoryDetails = (ArrayList<String[]>) details.getSerializable("rest");
		
//		for(int i = 0; i < territoryDetails.size(); i++){
//			String[] t = territoryDetails.get(i);
//			for(String s : t){
//				System.out.print(s);
//			}
//			System.out.println();
//		}
		
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
		
		setFields();
		loadTerritoryButtons();
		
		game = new Game(diff, diceLike, numPlayers, res, true, terr, territoryDetails);
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
		
		obj.setText("Objective: Defeat all opponents.");
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
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(70*buttonScale, 70*buttonScale);
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
	
	// updates the values on each button and the text fields at the end of every move
	public void update(){
		territories = game.getTerritories();
		boolean won, lost;
		
		won = true;
		lost = true;
		
		for(int i = 0; i < numTerritories; i++){
			Button b = (Button) map.getChildAt(i);
			Territory t = territories.get(i+1);
			int owner = t.getOwner();
			if(owner != 1){
				won = false;
			}
			else{
				lost = false;
			}
			b.setText(t.getAbbrvName() + "\n" + t.getNumUnits());
			b.getBackground().setColorFilter(new PorterDuffColorFilter(ColorMap.get(t.getOwner()),PorterDuff.Mode.OVERLAY));
			
			res.setText("Resources:\n" + players.get(1).getNumResources());
			turnsLeft.setText("Turn Number:\n" + Integer.toString(turnNum));
		}
		
		if(won){
			final FragmentManager fm = getFragmentManager();
			EndGameFragment win = new EndGameFragment(true);
			win.show(fm, "endGame");
			over = true;
		}
		
		if(lost){
			final FragmentManager fm = getFragmentManager();
			EndGameFragment lose = new EndGameFragment(false);
			lose.show(fm, "endGame");
			over = true;
		}
	}
	
	public void onNewIntent(Intent intent){
		Bundle details = intent.getExtras();
		if(details == null) return;
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
		
		// game saving code
		if(!over){
			String gameSave = game.toString();
			FileOutputStream fos;
			try{
				fos = openFileOutput("savegame",Context.MODE_PRIVATE);
				fos.write((Integer.toString(turnNum) + "," + Integer.toString(level) + "\n").getBytes());
				fos.write(gameSave.getBytes());
				fos.flush();
				fos.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void onResume(){
		super.onResume();
		update();
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
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_settings:
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
