package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class OptionsActivity extends Activity {
	
	private int territorySize;
	
	private Spinner tSize;
	private Button t, save;
	private LinearLayout optionsMaster;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences settings = getSharedPreferences("options", 0);
	    territorySize = settings.getInt("tSize", 1);
	    tSize = (Spinner) findViewById(R.id.territorySpinner);
	    t = (Button) findViewById(R.id.territoryDemo);
	    save = (Button) findViewById(R.id.saveButton);
	    optionsMaster = (LinearLayout) findViewById(R.id.optionsMaster);
	    
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.territory_sizes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		tSize.setAdapter(adapter);
		tSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		    
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				switch(pos){
				case 0:
					territorySize = 1;
					t.setHeight(70);
					t.setWidth(70);
					optionsMaster.invalidate();
					break;
				case 1:
					territorySize = 3;
					t.setHeight(210);
					t.setWidth(210);
					optionsMaster.invalidate();
					break;
				case 2:
					territorySize = 5;
					t.setHeight(350);
					t.setWidth(350);
					optionsMaster.invalidate();
					break;
				}
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		        territorySize = 1;
		    }
		});
		
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent backLaunch = new Intent(getApplicationContext(), MainActivity.class);
    			backLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(backLaunch);
			}
		});
	}
	
    @Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences("options", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("tSize", territorySize);

      // Commit the edits!
      editor.commit();
      this.finish();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
