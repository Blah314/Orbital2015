package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private Button newGameButton, continueButton, optionsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		
		// to do - continue game button (need game to work first)
		
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
