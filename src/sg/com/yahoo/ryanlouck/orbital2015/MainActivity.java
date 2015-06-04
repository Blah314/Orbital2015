package sg.com.yahoo.ryanlouck.orbital2015;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends Activity {
	
	private Button newGameButton, continueButton, optionsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		newGameButton = (Button) findViewById(R.id.button2);
		continueButton = (Button) findViewById(R.id.button1);
		optionsButton = (Button) findViewById(R.id.button3);
		
		newGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newGameLaunch = new Intent(getApplicationContext(), NewGameActivity.class);
				startActivity(newGameLaunch);
			}
		});
		
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
