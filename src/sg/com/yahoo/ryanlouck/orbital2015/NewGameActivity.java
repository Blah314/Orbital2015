package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class NewGameActivity extends Activity {
	
	// this activity basically handles level select
	private int numLevels;
	private View.OnClickListener levelButtonLoader;
	private int selectedLevel = 0;
	private int selectedPack = 0;
	private ArrayList<String[]> levelDetails;
	private Hashtable<Integer, PorterDuffColorFilter> ColorMap;
	private Hashtable<Button, Integer> LevelMap;
	private String[] awards;
	private int[] levelAwards;
	
	private TextView levelDetailsView;
	private Button startButton;
	private Spinner packSelect;
	private ViewGroup levels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// colours to display the achievement levels for each level
		ColorMap = new Hashtable<Integer, PorterDuffColorFilter>();
		ColorMap.put(0, new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY));
		ColorMap.put(1, new PorterDuffColorFilter(Color.rgb(205, 127, 50), PorterDuff.Mode.OVERLAY));
		ColorMap.put(2, new PorterDuffColorFilter(Color.rgb(192, 192, 192), PorterDuff.Mode.OVERLAY));
		ColorMap.put(3, new PorterDuffColorFilter(Color.rgb(255, 215, 0), PorterDuff.Mode.OVERLAY));
		ColorMap.put(4, new PorterDuffColorFilter(Color.rgb(229, 228, 226), PorterDuff.Mode.OVERLAY));
		
		LevelMap = new Hashtable<Button, Integer>();
		awards = new String[]{"Bronze", "Silver", "Gold", "Platinum"};
		
		// levelDetailsView displays level details when a level button is clicked
		levelDetailsView = (TextView) findViewById(R.id.levelText);
		startButton = (Button) findViewById(R.id.startGameButton);
		packSelect = (Spinner) findViewById(R.id.packSpinner);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.level_packs, 
			android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		packSelect.setAdapter(adapter);
		
		packSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		    
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
		        selectedPack = pos;
		        loadPack();
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		    	selectedPack = 0;
		    }
		});	
			
		levelDetails = new ArrayList<String[]>();
		
		// read levelDetails from file
		AssetManager am = this.getAssets();
		try{
			InputStream is = am.open("levelDetails.txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while(br.ready()){
				String[] currentLevelDetails = br.readLine().split(",");
				levelDetails.add(currentLevelDetails);
			}
			is.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		numLevels = levelDetails.size() - 1;
		
		// getting the highest award achieved for each level so far
		levelAwards = new int[numLevels + 1];
		SharedPreferences settings = getSharedPreferences("levels", 0);
		
		for(int i = 1; i <= numLevels; i++){
			levelAwards[i] = settings.getInt("level" + Integer.toString(i), 0);
		}
		
		// levels is the view containing all the level buttons
		levels = (ViewGroup) (findViewById(R.id.buttonScroller).findViewById(R.id.levelButtons));
		
		// levelButtonLoader makes the buttons load up the details for their appropriate level
		levelButtonLoader = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedLevel = LevelMap.get(v);
				if(levelAwards[selectedLevel] != 0){
					levelDetailsView.setText(levelDetails.get(selectedLevel)[2] + ":\n" + 
						levelDetails.get(selectedLevel)[6] + "\n\nYou have achieved the " + 
						awards[levelAwards[selectedLevel] - 1] + " medal for this level.");
				}
				else{
					levelDetailsView.setText(levelDetails.get(selectedLevel)[2] + ":\n" + 
						levelDetails.get(selectedLevel)[6] + 
						"\n\nYou have not earned any medal for this level yet."); 
				}
			}
		};
		
		// startButton code - launches the customization screen if a level is selected		
		startButton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				if(selectedLevel == 0){
					levelDetailsView.setText("Select a Level above first.");
				}
				else{
					Intent customisationLaunch = new Intent(getApplicationContext(), CustomisationActivity.class);
					customisationLaunch.putExtra("level", selectedLevel);
					customisationLaunch.putExtra("levelDetails", levelDetails.get(selectedLevel));
					customisationLaunch.putExtra("award", levelAwards[selectedLevel]);
					startActivity(customisationLaunch);
				}
			}
		});
		
		loadPack();
	}
	
	public void loadPack(){
		
		levels.removeAllViews();
		
		// setting the buttons text to the levelName as well as assigning levelButtonLoader to them
		// only loads levels that are in the current pack
		for(int i = 1; i <= numLevels; i++){
			if(Integer.parseInt(levelDetails.get(i)[1]) == selectedPack){
				Button b = new Button(this);
				b.setText(levelDetails.get(i)[2]);
				b.getBackground().setColorFilter(ColorMap.get(levelAwards[i]));
				b.setOnClickListener(levelButtonLoader);
				LevelMap.put(b, i);
				levels.addView(b);
			}
		}
	}
	
	public void onPause(){
		super.onPause();
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
}
