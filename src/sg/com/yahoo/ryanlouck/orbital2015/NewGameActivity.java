package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.Gravity;

public class NewGameActivity extends Activity {
	
	private int numLevels;
	private Button[] levelButtons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		ViewGroup levels = (ViewGroup) (findViewById(R.id.buttonScroller).findViewById(R.id.levelButtons));
		numLevels = levels.getChildCount();
		levelButtons = new Button[numLevels];
		
		
		TextView debugCount = (TextView) findViewById(R.id.textView2);	
		debugCount.setText(Integer.toString(numLevels));
		
		for(int i = 0; i < numLevels; i++){
			Button b;
			View v = levels.getChildAt(i);
			if(v instanceof Button){
				b = (Button) v;
				levelButtons[i] = b;				
			}
		}
		

		for(int i = 0; i < numLevels; i++){
			LayoutInflater inflater = (LayoutInflater) NewGameActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final PopupWindow p = new PopupWindow();
			final View popUpView = inflater.inflate(R.layout.popup_level_details, (ViewGroup) findViewById(R.id.popUp));
			TextView levelDisplay = (TextView) findViewById(R.id.popUpLevelNumber);
			Button close = (Button) findViewById(R.id.closeButton);
			levelButtons[i].setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					p.showAtLocation(popUpView, Gravity.CENTER, 0, 0);
				}
			});
//			close.setOnClickListener(new View.OnClickListener() {		
//				@Override
//				public void onClick(View v) {
//					p.dismiss();
//				}
//			});
		}
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
