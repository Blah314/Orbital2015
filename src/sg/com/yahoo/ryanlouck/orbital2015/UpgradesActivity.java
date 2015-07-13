package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpgradesActivity extends Activity {
	
	private Button res, atk, def;
	private TextView resAmt, costRes, costAtk, costDef;
	private int resources, resLevel, atkLevel, defLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upgrades);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		resAmt = (TextView) findViewById(R.id.resAmt);
		costRes = (TextView) findViewById(R.id.resLevel);
		costAtk = (TextView) findViewById(R.id.atkLevel);
		costDef = (TextView) findViewById(R.id.defLevel);
		res = (Button) findViewById(R.id.resButton);
		atk = (Button) findViewById(R.id.atkButton);
		def = (Button) findViewById(R.id.defButton);
		
		Bundle details = getIntent().getExtras();
		Player p = (Player) details.getSerializable("player");
		
		resources = p.getNumResources();
		int[] resLevels = p.getResLevels();
		resLevel = resLevels[0];
		atkLevel = resLevels[1];
		defLevel = resLevels[2];
		
		resAmt.setText("Resources:\n" + Integer.toString(resources));
		costRes.setText("Current Level: " + Integer.toString(resLevel) + "\nUpgrade Cost: " + Integer.toString(25 * (resLevel + 1)));
		costAtk.setText("Current Level: " + Integer.toString(atkLevel) + "\nUpgrade Cost: " + Integer.toString(25 * (atkLevel + 1)));
		costDef.setText("Current Level: " + Integer.toString(defLevel) + "\nUpgrade Cost: " + Integer.toString(25 * (defLevel + 1)));
		
		res.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(resources < 25 * (resLevel + 1)){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.upgrade_reject);
					int duration = Toast.LENGTH_SHORT;
					
					Toast t = Toast.makeText(c, text, duration);
					t.show();
				}
				else{
					int cost = 25 * (resLevel + 1);
					Intent backToMap = new Intent(getApplicationContext(), MapActivity.class);
					backToMap.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					backToMap.putExtra("isupgrade", true);
					backToMap.putExtra("cost", cost);
					backToMap.putExtra("upgrade", 1);
					startActivity(backToMap);
				}
				
			}
		});
		
		atk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(resources < 25 * (atkLevel + 1)){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.upgrade_reject);
					int duration = Toast.LENGTH_SHORT;
					
					Toast t = Toast.makeText(c, text, duration);
					t.show();
				}
				else{
					int cost = 25 * (atkLevel + 1);
					Intent backToMap = new Intent(getApplicationContext(), MapActivity.class);
					backToMap.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					backToMap.putExtra("isupgrade", true);
					backToMap.putExtra("cost", cost);
					backToMap.putExtra("upgrade", 2);
					startActivity(backToMap);
				}
				
			}
		});
		
		def.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(resources < 25 * (defLevel + 1)){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.upgrade_reject);
					int duration = Toast.LENGTH_SHORT;
					
					Toast t = Toast.makeText(c, text, duration);
					t.show();
				}
				else{
					int cost = 25 * (defLevel + 1);
					Intent backToMap = new Intent(getApplicationContext(), MapActivity.class);
					backToMap.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					backToMap.putExtra("isupgrade", true);
					backToMap.putExtra("cost", cost);
					backToMap.putExtra("upgrade", 3);
					startActivity(backToMap);
				}
				
			}
		});
	}
	
	protected void onStop(){
		super.onStop();
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upgrades, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
