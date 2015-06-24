package sg.com.yahoo.ryanlouck.orbital2015;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MapActivity extends Activity {
	
	private float mx, my, currX, currY;
	private ScrollView vScroll;
	private HorizontalScrollView hScroll;
	private RelativeLayout map;
	private TextView res, obj, turnNo, phaseNo;
	private Button endPhase;
	
	private Game game;
	private ArrayList<String[]> territoryDetails;
	
	private int level;
	private int diff;
	private boolean diceLike;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// all the views
		vScroll = (ScrollView) findViewById(R.id.vScroll1);
		hScroll = (HorizontalScrollView) findViewById(R.id.hScroll1);
		map = (RelativeLayout) findViewById(R.id.map);
		res = (TextView) findViewById(R.id.textView1);
		obj = (TextView) findViewById(R.id.textView2);
		turnNo = (TextView) findViewById(R.id.textView3);
		endPhase = (Button) findViewById(R.id.button1);
		
		// getting the stuff passed from CustomisationActivity
		Bundle details = getIntent().getExtras();
		if(details != null){
			level = details.getInt("lvl");
			diff = details.getInt("diff");
			diceLike = details.getBoolean("dice");
		}
		
		startGame();
	}
	
	// Initialization method - creates the map and a new game
	public void startGame(){		
		territoryDetails = new ArrayList<String[]>();
		
		// loading up the map and getting the details of all territories
		AssetManager am = this.getAssets();
		try{
			InputStream is = am.open("maps/" + Integer.toString(level) + ".txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while(br.ready()){
				String[] currentTerritoryDetails = br.readLine().split(",");
				territoryDetails.add(currentTerritoryDetails);
			}
			is.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		for(int i = 1; i < territoryDetails.size(); i++){
			String[] tDetails = territoryDetails.get(i);
			
			// load up each territory, check its neighbours, then draw lines between their coordinates - to do)
			for(int j = 11; j < tDetails.length; j++){
				int neighbour = Integer.parseInt(tDetails[j]);
				if(neighbour < i) continue;
				String[] neighbourDetails = territoryDetails.get(neighbour);
				// draw line stuff goes here
			}
			
			// creates the territory buttons and puts them at their corresponding location
			Button territoryButton = new Button(this);
			territoryButton.setText(tDetails[1]);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
			params.leftMargin = Integer.parseInt(tDetails[2]);
			params.topMargin = Integer.parseInt(tDetails[3]);
			map.addView(territoryButton, params);		
		}
		
		// to do - create new game and assign buttons to the TerritoryActivity
		
		// to do - execute phase button
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
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
	
	// map scrolling code - it's MAGIC! Don't touch!
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currX = event.getX();
                currY = event.getY();
                vScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                hScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                mx = currX;
                my = currY;
                break;
            case MotionEvent.ACTION_UP:
                currX = event.getX();
                currY = event.getY();
                vScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                hScroll.scrollBy((int) (mx - currX), (int) (my - currY));
                break;
        }

        return true;
    }
}
