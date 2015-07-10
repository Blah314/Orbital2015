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
import android.widget.Button;
import android.widget.TextView;

public class NewGameActivity extends Activity {
	
	// this activity basically handles level select
	private int numLevels;
	private View.OnClickListener levelButtonLoader;
	private int selectedLevel = 0;
	private ArrayList<String[]> levelDetails;
	private Hashtable<Integer, PorterDuffColorFilter> ColorMap;
	private String[] awards;
	private int[] levelAwards;
	
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
		
		awards = new String[]{"Bronze", "Silver", "Gold", "Platinum"};
		
		// levelDetailsView displays level details when a level button is clicked
		final TextView levelDetailsView = (TextView) findViewById(R.id.levelText);
		final Button startButton = (Button) findViewById(R.id.startGameButton);
		
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
		levelAwards = new int[numLevels];
		SharedPreferences settings = getSharedPreferences("levels", 0);
		
		for(int i = 0; i < numLevels; i++){
			levelAwards[i] = settings.getInt("level" + Integer.toString(i), 0);
		}
		
		// levels is the view containing all the level buttons
		final ViewGroup levels = (ViewGroup) (findViewById(R.id.buttonScroller).findViewById(R.id.levelButtons));
		
		// levelButtonLoader makes the buttons load up the details for their appropriate level
		levelButtonLoader = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int ID = 0;
				for(int i = 0; i <= numLevels; i++){
					if(v == levels.getChildAt(i)){
						ID = i;
						selectedLevel = ID + 1;
					}
				}
				if(levelAwards[ID] != 0){
					levelDetailsView.setText(levelDetails.get(selectedLevel)[2] + ":\n" + 
						levelDetails.get(selectedLevel)[6] + "\n\nYou have achieved the " + 
						awards[levelAwards[ID] - 1] + " medal for this level.");
				}
				else{
					levelDetailsView.setText(levelDetails.get(selectedLevel)[2] + ":\n" + 
						levelDetails.get(selectedLevel)[6] + 
						"\n\nYou have not earned any medal for this level yet."); 
				}
			}
		};
		
		// setting the buttons text to the levelName as well as assigning levelButtonLoader to them
		for(int i = 1; i <= numLevels; i++){
			Button b = new Button(this);
			b.setText(levelDetails.get(i)[2]);
			b.getBackground().setColorFilter(ColorMap.get(levelAwards[i-1]));
			b.setOnClickListener(levelButtonLoader);
			levels.addView(b);		
		}
		
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
					customisationLaunch.putExtra("award", levelAwards[selectedLevel - 1]);
					startActivity(customisationLaunch);
				}
			}
		});
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
