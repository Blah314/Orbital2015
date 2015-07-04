package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	
	private Button newGameButton, continueButton, optionsButton, instructionsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences settings = getSharedPreferences("options", 0);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("gameStarted", false);
	    editor.commit();
		
		// gets the references to the 3 main buttons
		newGameButton = (Button) findViewById(R.id.newGameButton);
		continueButton = (Button) findViewById(R.id.continueGameButton);
		optionsButton = (Button) findViewById(R.id.optionsButton);
		instructionsButton = (Button) findViewById(R.id.instructionsButon);
		
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
				ArrayList<String[]> saveDetails = new ArrayList<String[]>();
				try{
					FileInputStream fis = openFileInput("savegame");
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(isr);
					while(br.ready()){
						saveDetails.add(br.readLine().split(","));
					}
					br.close();
				}
				catch(FileNotFoundException fnfe){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.no_save_game);
					int duration = Toast.LENGTH_SHORT;
						
					Toast t = Toast.makeText(c, text, duration);
					t.show();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				Intent resumeLaunch = new Intent(getApplicationContext(), MapActivity.class);
				resumeLaunch.putExtra("resumed", true);
				
				String[] globalDetails = saveDetails.get(0);
				resumeLaunch.putExtra("turnNum", Integer.parseInt(globalDetails[0]));
				resumeLaunch.putExtra("lvl", Integer.parseInt(globalDetails[1]));
				
				String[] gameDetails = saveDetails.get(1);
				resumeLaunch.putExtra("diff", Integer.parseInt(gameDetails[0]));
				resumeLaunch.putExtra("dice", Boolean.parseBoolean(gameDetails[1]));
				resumeLaunch.putExtra("numPlayers", Integer.parseInt(gameDetails[2]));
				
				int[] resources = new int[Integer.parseInt(gameDetails[2])];
				for(int i = 0; i < resources.length; i++){
					resources[i] = Integer.parseInt(gameDetails[3 + i]);
				}
				
				resumeLaunch.putExtra("res", resources);
				
				String[] territoryOwned = saveDetails.get(2);
				int[] territoriesOwned = new int[Integer.parseInt(gameDetails[2])];
				for(int i = 0; i < territoriesOwned.length; i++){
					territoriesOwned[i] = Integer.parseInt(territoryOwned[i]);
				}
				
				resumeLaunch.putExtra("terr", territoriesOwned);
				
				ArrayList<String[]> tDetails = new ArrayList<String[]>();
				
				for(int i = 3; i < saveDetails.size(); i++){
					tDetails.add(saveDetails.get(i));
				}
				
				resumeLaunch.putExtra("rest", tDetails);
				
				startActivity(resumeLaunch);
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
		
		instructionsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent instLaunch = new Intent(getApplicationContext(), InstructionsActivity.class);
				startActivity(instLaunch);
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
