package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Button newGameButton, continueButton, optionsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences settings = getSharedPreferences("options", 0);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("gameStarted", false);
	    editor.commit();
		
		// gets the references to the 3 main buttons
		newGameButton = (Button) findViewById(R.id.button2);
		continueButton = (Button) findViewById(R.id.button1);
		optionsButton = (Button) findViewById(R.id.button3);
		
		
		// newGameButton launches the level selection (to do - prompt when there is an already saved game)
		newGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newGameLaunch = new Intent(getApplicationContext(), NewGameActivity.class);
				startActivity(newGameLaunch);
			}
		});
		
		// continue game returns mapActivity to focus if it has been started
		continueButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				SharedPreferences settings = getSharedPreferences("options", 0);
//				boolean started = settings.getBoolean("gameStarted", false);
//				
//				if(started){
//					Intent gameLaunch = new Intent(getApplicationContext(), MapActivity.class);
//					gameLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(gameLaunch);
//				}
//				else{
				Context c = getApplicationContext();
				CharSequence text = getResources().getString(R.string.no_save_game);
				int duration = Toast.LENGTH_SHORT;
					
				Toast t = Toast.makeText(c, text, duration);
				t.show();
			}
		});
		
		// options button goes to options menu
		optionsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent optionsLaunch = new Intent(getApplicationContext(), OptionsActivity.class);
				startActivity(optionsLaunch);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
