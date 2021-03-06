package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

public class CustomisationActivity extends Activity {
	
	private int level = 0;
	private int diff = 0;
	private int achievementLevel;
	private boolean diceLike, capital, upgrades, armies, fow, regions, hardcore;
	private String[] levelDetails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customisation);
		
		// get the stuff the NewGameActivity passed over
		Bundle details = getIntent().getExtras();
		if(details != null){
			level = details.getInt("level", 1);
			levelDetails = details.getStringArray("levelDetails");
			achievementLevel = details.getInt("award");
		}
		
		diceLike = false;
		capital = false;
		upgrades = false;
		armies = false;
		fow = false;
		regions = false;
		hardcore = false;
		
		// all the elements on screen
		final TextView heading = (TextView) findViewById(R.id.customisationTitle);
		final Spinner diffSelect = (Spinner) findViewById(R.id.diffSpinner);
		final Button start = (Button) findViewById(R.id.startButton);
		
		heading.setText(levelDetails[2]);
		
		if(achievementLevel >= 3){
			// difficulty spinner code - extreme(0 - easy, 1 - medium, 2 - hard , 3 - impossible)
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.difficulty_levels_extra, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
			diffSelect.setAdapter(adapter);
		}
		else{
			// difficulty spinner code - normal (0 - easy, 1 - medium, 2 - hard)
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.difficulty_levels, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
			diffSelect.setAdapter(adapter);
		}
		
		diffSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		    
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
		        diff = pos;
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		        diff = 0;
		    }
		});
		
		// startButton launches the game with the selected settings.
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v){
				Intent gameLaunch = new Intent(getApplicationContext(), MapActivity.class);
				gameLaunch.putExtra("lvl", level);
				gameLaunch.putExtra("diff", diff);
				gameLaunch.putExtra("dice", diceLike);
//				gameLaunch.putExtra("armies", armies);
				gameLaunch.putExtra("upgrades", upgrades);
				gameLaunch.putExtra("capital", capital);
				gameLaunch.putExtra("fow", fow);
				gameLaunch.putExtra("regions", regions);
				gameLaunch.putExtra("hardcore", hardcore);
				gameLaunch.putExtra("levelDetails", levelDetails);
				startActivity(gameLaunch);
			}
		});
	}
	
	public void onPause(){
		super.onPause();
		this.finish();
	}
	
	// listener for all the checkboxes
	public void checkBoxListener(View v){
		boolean checked = ((CheckBox) v).isChecked();
		
		switch(v.getId()){
		case R.id.diceCheckbox:
			diceLike = checked;
			break;
		case R.id.capitalCheckBox:
			capital = checked;
			break;
		case R.id.upgradesCheckBox:
			upgrades = checked;
			break;
//		case R.id.armiesCheckBox:
//			armies = checked;
//			break;
		case R.id.fowCheckBox:
			fow = checked;
			break;
		case R.id.regionsCheckBox:
			regions = checked;
			break;
		case R.id.hardcoreCheckBox:
			hardcore = checked;
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customisation, menu);
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
