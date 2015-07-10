package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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
		
		// gets the references to the 3 main buttons
		newGameButton = (Button) findViewById(R.id.newGameButton);
		continueButton = (Button) findViewById(R.id.continueGameButton);
		optionsButton = (Button) findViewById(R.id.optionsButton);
		instructionsButton = (Button) findViewById(R.id.instructionsButon);
		
		// newGameButton launches the level selection (to do - prompt when there is an already saved game)
		newGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean fileFound = true;
				
				// check for saved game
				try{
					FileInputStream fis = openFileInput("savegame");
					fis.close();
				}
				
				// no saved game - start NewGameActivity
				catch(FileNotFoundException fnfe){
					Intent newGameLaunch = new Intent(getApplicationContext(), NewGameActivity.class);
					startActivity(newGameLaunch);
					fileFound = false;
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				if(fileFound){
					SaveGameDialogFragment sgdf = new SaveGameDialogFragment();
					FragmentManager fm = getFragmentManager();
					sgdf.show(fm, "overwrite");
				}
				
			}
		});
		
		// continue game loads the saved game data and resumes the game
		continueButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean fileFound = true;
				ArrayList<String[]> saveDetails = new ArrayList<String[]>();
				
				// try to find the savegame file
				try{
					FileInputStream fis = openFileInput("savegame");
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(isr);
					while(br.ready()){
						saveDetails.add(br.readLine().split(","));
					}
					br.close();
				}
				
				// file not found - raise a toast and set fileFound to false
				catch(FileNotFoundException fnfe){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.no_save_game);
					int duration = Toast.LENGTH_SHORT;
						
					Toast t = Toast.makeText(c, text, duration);
					t.show();
					fileFound = false;
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				// load details if file is found and start map activity
				if(fileFound){
					Intent resumeLaunch = new Intent(getApplicationContext(), MapActivity.class);
					resumeLaunch.putExtra("resumed", true);
				
					String[] globalDetails = saveDetails.get(0);
					resumeLaunch.putExtra("turnNum", Integer.parseInt(globalDetails[0]));
					resumeLaunch.putExtra("lvl", Integer.parseInt(globalDetails[1]));
					boolean hardcore;
					if(globalDetails[2].equals("true")){
						hardcore = true;
					}
					else{
						hardcore = false;
					}
					resumeLaunch.putExtra("hardcore", hardcore);
					
					boolean fow;
					if(globalDetails[3].equals("true")){
						fow = true;
					}
					else{
						fow = false;
					}
					resumeLaunch.putExtra("fow", fow);
					
					boolean capital;
					if(globalDetails[4].equals("true")){
						capital = true;
					}
					else{
						capital = false;
					}
					resumeLaunch.putExtra("capital", capital);
					
					boolean upgrades;
					if(globalDetails[5].equals("true")){
						upgrades = true;
					}
					else{
						upgrades = false;
					}
					resumeLaunch.putExtra("upgrades", upgrades);
				
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
					
					String[] terrConq = saveDetails.get(3);
					
					boolean[] territoriesConq = new boolean[terrConq.length];
					for(int i = 0; i < terrConq.length; i++){
						if(terrConq[i].equals("true")){
							territoriesConq[i] = true;
						}
						else{
							territoriesConq[i] = false;
						}
					}
					
					resumeLaunch.putExtra("conq", territoriesConq);
					
					String[] playerRes = saveDetails.get(4);
					
					resumeLaunch.putExtra("resValues", playerRes);
				
					ArrayList<String[]> tDetails = new ArrayList<String[]>();
				
					for(int i = 5; i < saveDetails.size(); i++){
						tDetails.add(saveDetails.get(i));
					}
				
					resumeLaunch.putExtra("rest", tDetails);
				
					startActivity(resumeLaunch);
				}
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
		
		// instructions button goes to instructions menu
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
		return super.onOptionsItemSelected(item);
	}
}
