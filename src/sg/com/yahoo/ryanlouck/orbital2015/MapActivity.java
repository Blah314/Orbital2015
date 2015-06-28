package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
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
	private ScrollView vScroll;
	private HorizontalScrollView hScroll;
	private ViewGroup map;
	private TextView res, obj, turnsLeft;
	private Button endTurn;
	
	private String[] levelDetails;
	private ArrayList<String[]> territoryDetails;
	private ArrayList<int[]> lineCoords;
	
	private int level, diff, numTerritories;
	private boolean diceLike;
	private Game game;
	private HashMap<Integer, Territory> territories;
	private HashMap<Integer, Player> players;
	
	public Hashtable<Integer,Integer> ColorMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// all the views
		vScroll = (ScrollView) findViewById(R.id.vScroll1);
		hScroll = (HorizontalScrollView) findViewById(R.id.hScroll1);
		map = (ViewGroup) findViewById(R.id.map);
		res = (TextView) findViewById(R.id.textView1);
		obj = (TextView) findViewById(R.id.textView2);
		turnsLeft = (TextView) findViewById(R.id.textView3);
		endTurn = (Button) findViewById(R.id.button1);
		
		// getting the stuff passed from CustomisationActivity or ContinueGameButton
		Bundle details = getIntent().getExtras();
		if(details != null){
			level = details.getInt("lvl");
			diff = details.getInt("diff");
			diceLike = details.getBoolean("dice");
			levelDetails = details.getStringArray("levelDetails");
		}
		
		// color definitions - more to come
		ColorMap = new Hashtable<Integer,Integer>();
		ColorMap.put(0,Color.GRAY);
		ColorMap.put(1,Color.BLUE);
		ColorMap.put(2,Color.RED);
		ColorMap.put(3,Color.GREEN);
		ColorMap.put(4,Color.YELLOW);
		
		startGame();
	}
	
	// Initialization method - creates the map and a new game
	public void startGame(){
		territoryDetails = new ArrayList<String[]>();
		lineCoords = new ArrayList<int[]>();
		
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
			
		for(int i = 1; i < territoryDetails.size(); i++){
			String[] tDetails = territoryDetails.get(i);
			
			// load up each territory, check its neighbours, then note the lines that need to be drawn
			for(int j = 12; j < tDetails.length; j++){
				int neighbour = Integer.parseInt(tDetails[j]);
				if(neighbour < i) continue;
				String[] nDetails = territoryDetails.get(neighbour);
				int sX, sY, eX, eY;
				sX = Integer.parseInt(tDetails[3]);
				sY = Integer.parseInt(tDetails[4]);
				eX = Integer.parseInt(nDetails[3]);
				eY = Integer.parseInt(nDetails[4]);
				lineCoords.add(new int[]{sX, sY, eX, eY});
			}
			
			// creates the territory buttons and puts them at their corresponding location
			Button territoryButton = new Button(this);
			territoryButton.setText(tDetails[2]);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500,500);
			params.leftMargin = Integer.parseInt(tDetails[3]);
			params.topMargin = Integer.parseInt(tDetails[4]);
			map.addView(territoryButton, params);
		}
		
		// create the line view and draws them
//		LineView lines = new LineView(this);
//		lines.setMap(lineCoords);
//		lines.invalidate();
			
		// TODO cannot get lines to display in the map layout
		
//		map.addView(lines);
//		
//		lines.buildDrawingCache();		
//		Drawable l = new BitmapDrawable(lines.getDrawingCache());		
//		map.setBackgroundDrawable(l);
		
		getActionBar().setTitle(levelDetails[2]);
		
		obj.setText("Objective: Defeat all opponents.");
		obj.setGravity(17);
		
		// create new game	
		int[] startingRes = new int[levelDetails.length - 8];
		for(int i = 8; i < levelDetails.length; i++){
			startingRes[i-8] = Integer.parseInt(levelDetails[i]);
		}
		
		game = new Game(diff, diceLike, Integer.parseInt(levelDetails[5]), startingRes, territoryDetails);
		territories = game.getTerritories();
		numTerritories = territories.size();
		players = game.getPlayers();
		
		game.startPlayerTurn(1);
		
		update();
		
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
					
					else{
						if(!game.getPlayers().get(1).isTurnEnded()){
							Intent TerritoryLaunch = new Intent(getApplicationContext(), TerritoryActivity.class);
							TerritoryLaunch.putExtra("territory", t);
							TerritoryLaunch.putExtra("res", game.getPlayers().get(1).getNumResources());
							TerritoryLaunch.putExtra("game", game);
							startActivity(TerritoryLaunch);
						}
						else{
							Context c = getApplicationContext();
							CharSequence text = getResources().getString(R.string.out_of_turns);
							int duration = Toast.LENGTH_SHORT;
						
							Toast t = Toast.makeText(c, text, duration);
							t.show();
						}
					}
				}
			});
		}
		
		endTurn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				game.turnEnds();
				update();
			}
		});
	}
	
	// updates the values on each button and the text fields at the end of every move
	public void update(){
		territories = game.getTerritories();
		
		for(int i = 0; i < numTerritories; i++){
			Button b = (Button) map.getChildAt(i);
			Territory t = territories.get(i+1);
			b.setText(t.getAbbrvName() + "\n" + t.getNumUnits());
			b.getBackground().setColorFilter(new PorterDuffColorFilter(ColorMap.get(t.getOwner()),PorterDuff.Mode.OVERLAY));
			
			res.setText("Resources:\n" + players.get(1).getNumResources());
			turnsLeft.setText("Turns Left:\n" + players.get(1).getNumTurns());
		}
	}
	
	public void onPause(){
		super.onPause();
	}
	
	public void onNewIntent(Intent intent){
		Bundle details = intent.getExtras();
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
		if (id == R.id.action_settings) {
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
