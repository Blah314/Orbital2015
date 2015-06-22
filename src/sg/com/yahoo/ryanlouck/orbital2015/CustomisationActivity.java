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
	private boolean diceLike = false;
	private String levelName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customisation);
		Bundle details = getIntent().getExtras();
		if(details != null){
			level = details.getInt("level");
			levelName = details.getString("levelName");
		}
		final TextView heading = (TextView) findViewById(R.id.heading);
		final Spinner diffSelect = (Spinner) findViewById(R.id.spinner1);
		final Button start = (Button) findViewById(R.id.start);
//		final TextView debug = (TextView) findViewById(R.id.debug);
		
		heading.setText(levelName);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.difficulty_levels, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		diffSelect.setAdapter(adapter);
		diffSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		    
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
		        diff = pos;
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		        diff = 0;
		    }
		});
		
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v){
				Intent gameLaunch = new Intent(getApplicationContext(), MapActivity.class);
				gameLaunch.putExtra("diff", diff);
				gameLaunch.putExtra("lvl", level);
				gameLaunch.putExtra("dice", diceLike);
				startActivity(gameLaunch);
			}
		});
	}
	
	public void checkBoxListener(View v){
		boolean checked = ((CheckBox) v).isChecked();
		
		switch(v.getId()){
		case R.id.checkBox1:
			diceLike = checked;
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
