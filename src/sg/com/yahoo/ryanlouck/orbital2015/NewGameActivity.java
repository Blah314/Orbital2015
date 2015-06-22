package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		final TextView levelDetailsView = (TextView) findViewById(R.id.textView2);
		
		levelDetails = new ArrayList<String[]>();
		
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
		
		numLevels = levelDetails.size();
		final ViewGroup levels = (ViewGroup) (findViewById(R.id.buttonScroller).findViewById(R.id.levelButtons));
		levelButtonLoader = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int ID = 0;
				for(int i = 0; i < numLevels; i++){
					if(v == levels.getChildAt(i)){
						ID = i;
						selectedLevel = ID + 1;
					}
				}
				levelDetailsView.setText(levelDetails.get(selectedLevel)[2] + ": " + levelDetails.get(selectedLevel)[6]);				
			}
		};
		
		for(int i = 1; i < numLevels; i++){
			Button b = new Button(this);
			b.setText(levelDetails.get(i)[2]);
			b.setOnClickListener(levelButtonLoader);
			levels.addView(b);		
		}
		
//		for(int i = 0; i < numLevels; i++){
//			Button b;
//			View v = levels.getChildAt(i);
//			if(v instanceof Button){
//				b = (Button) v;
//				levelButtons[i] = b;
//				b.setOnClickListener(levelButtonLoader);
//			}
//		}
		
		final Button startButton = (Button) findViewById(R.id.startGameButton);
		
		startButton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				if(selectedLevel == 0){
					levelDetailsView.setText("Select a Level above first.");
				}
				else{
					Intent customisationLaunch = new Intent(getApplicationContext(), CustomisationActivity.class);
					customisationLaunch.putExtra("level", selectedLevel);
					customisationLaunch.putExtra("levelName", levelDetails.get(selectedLevel)[2]);
					startActivity(customisationLaunch);
				}
			}
		});
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
