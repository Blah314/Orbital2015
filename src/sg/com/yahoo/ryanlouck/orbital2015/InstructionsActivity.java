package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InstructionsActivity extends Activity {
	
	private TextView title, body, page;
	private Button prev, next;
	private String[] titles, bodies;
	private int pageNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		title = (TextView) findViewById(R.id.instructionTitle);
		body = (TextView) findViewById(R.id.instructionBody);
		page = (TextView) findViewById(R.id.pageNum);
		prev = (Button) findViewById(R.id.prevButton);
		next = (Button) findViewById(R.id.nextButton);
		
		titles = getResources().getStringArray(R.array.instruction_titles);
		bodies = getResources().getStringArray(R.array.instruction_messages);
		
		pageNum = 1;
		
		updateFields();
		
		// previous button
		prev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pageNum == 1){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.first_page);
					int duration = Toast.LENGTH_SHORT;
					
					Toast t = Toast.makeText(c, text, duration);
					t.show();
				}
				else{
					pageNum--;
					updateFields();
				}
			}
		});
		
		// next button
		next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pageNum == titles.length){
					Context c = getApplicationContext();
					CharSequence text = getResources().getString(R.string.last_page);
					int duration = Toast.LENGTH_SHORT;
					
					Toast t = Toast.makeText(c, text, duration);
					t.show();
				}
				else{
					pageNum++;
					updateFields();
				}
				
			}
		});
	}
	
	// update the page when the user clicks prev or next
	public void updateFields(){
		title.setText(titles[pageNum - 1]);
		body.setText(bodies[pageNum - 1]);
		page.setText(Integer.toString(pageNum) + "/" + Integer.toString(titles.length));
	}
	
	protected void onStop(){
		super.onStop();
		this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.instructions, menu);
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
