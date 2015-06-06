package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NewGameActivity extends Activity {
	
	private int numLevels;
	private Button[] levelButtons;
	private View.OnClickListener levelButtonLoader;
	private int selectedLevel = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		final ViewGroup levels = (ViewGroup) (findViewById(R.id.buttonScroller).findViewById(R.id.levelButtons));
		numLevels = levels.getChildCount();
		levelButtons = new Button[numLevels];		
		
		final TextView levelDetails = (TextView) findViewById(R.id.textView2);	
		
		levelButtonLoader = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int ID = 0;
				for(int i = 0; i < numLevels; i++){
					if(v == levels.getChildAt(i)){
						ID = i;
						selectedLevel = i + 1;
					}
				}
				levelDetails.setText("Level " + Integer.toString(ID+1) + ":\n<level details here>");				
			}
		};
		
		for(int i = 0; i < numLevels; i++){
			Button b;
			View v = levels.getChildAt(i);
			if(v instanceof Button){
				b = (Button) v;
				levelButtons[i] = b;
				b.setOnClickListener(levelButtonLoader);
			}
		}
		
		final Button startButton = (Button) findViewById(R.id.startGameButton);
		
		startButton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				if(selectedLevel == 0){
					levelDetails.setText("Select a Level above first.");
				}
				else{
					Intent customisationLaunch = new Intent(getApplicationContext(), CustomisationActivity.class);
					customisationLaunch.putExtra("level", selectedLevel);
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
