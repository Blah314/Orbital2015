package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TerritoryActivity extends Activity {
	
	private TextView tName;
	private TableLayout actionButtons;
	
	private int res, limit;
	private Territory t;
	private Game g;
	private Hashtable<Integer, PorterDuffColorFilter> ColorTable = new Hashtable<Integer, PorterDuffColorFilter>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_territory);
		
		Bundle details = getIntent().getExtras();
		if(details != null){
			t = (Territory) details.getSerializable("territory");
			res = details.getInt("res");
			g = (Game) details.getSerializable("game");
			limit = res / 10;
		}
		
		tName = (TextView) findViewById(R.id.territoryName);
		actionButtons = (TableLayout) findViewById(R.id.actionButtons);
		
		tName.setText(t.getName());
		
		ColorTable.put(0, new PorterDuffColorFilter(Color.GRAY,PorterDuff.Mode.OVERLAY));
		ColorTable.put(1, new PorterDuffColorFilter(Color.BLUE,PorterDuff.Mode.OVERLAY));
		ColorTable.put(2, new PorterDuffColorFilter(Color.RED,PorterDuff.Mode.OVERLAY));
		ColorTable.put(3, new PorterDuffColorFilter(Color.GREEN,PorterDuff.Mode.OVERLAY));
		ColorTable.put(4, new PorterDuffColorFilter(Color.YELLOW,PorterDuff.Mode.OVERLAY));
		
		displayActions();
	}
	
	public void onResume(){
		super.onResume();
		Bundle details = getIntent().getExtras();
		if(details != null){
			t = (Territory) details.getSerializable("territory");
			res = details.getInt("res");
			g = (Game) details.getSerializable("game");
			limit = res / 10;
		}
		
		tName.setText(t.getName());
		tName.setGravity(17);
		
		displayActions();
	}
	
	public void displayActions(){
		
		actionButtons.removeAllViews();	
		
		if(t.getOwner() != 1){
			TextView rejected = new TextView(this);
			rejected.setText(getResources().getString(R.string.reject));
			actionButtons.addView(rejected);
			return;
		}
		
		TableRow head1 = new TableRow(this);
		TextView unitHead = new TextView(this);
		unitHead.setText(getResources().getString(R.string.unit_head));
		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 2;
		head1.addView(unitHead, params);
		actionButtons.addView(head1);
		
		TableRow addUnit = new TableRow(this);
		Button btn = new Button(this);
		TextView desc = new TextView(this);
		
		btn.setText(getResources().getString(R.string.unit1_name));
		desc.setText(getResources().getString(R.string.unit1_desc));
		
		final FragmentManager fm = getFragmentManager();
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActionFragment addDialog = new ActionFragment(limit, t.getNumUnits(), 0, t.getId(), true, false);
				addDialog.show(fm, "add");
			}
		});
		
		addUnit.addView(btn);
		addUnit.addView(desc);
		
		actionButtons.addView(addUnit);
		
		TableRow head2 = new TableRow(this);
		TextView attackHead = new TextView(this);
		attackHead.setText(getResources().getString(R.string.attack_head));
		head2.addView(attackHead, params);
		actionButtons.addView(head2);
		
		ArrayList<Integer> neighbours = t.getNeighbourIDs();
		Iterator<Integer> n = neighbours.iterator();
		while(n.hasNext()){
			int nextNeighbour = n.next();
			TableRow neighbourAttack = new TableRow(this);
			Button b = new Button(this);
			TextView d = new TextView(this);
			final Territory tNext = g.getTerritories().get(nextNeighbour);
			b.setText(tNext.getAbbrvName());
			b.getBackground().setColorFilter(ColorTable.get(tNext.getOwner()));
			if(tNext.getOwner() == t.getOwner()){
				d.setText(getResources().getString(R.string.move_desc));
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ActionFragment moveDialog = new ActionFragment(limit, t.getNumUnits(), tNext.getId(), t.getId(), false, true);
						moveDialog.show(fm, "move");
					}
				});
			}
			else{
				d.setText(getResources().getString(R.string.attack_desc));
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ActionFragment attackDialog = new ActionFragment(limit, t.getNumUnits(), tNext.getId(), t.getId(), false, false);
						attackDialog.show(fm, "attack");
					}
				});
			}
			d.setEllipsize(null);
			d.setMaxLines(5);
			d.setHorizontallyScrolling(false);
			d.setGravity(17);
			neighbourAttack.addView(b);
			neighbourAttack.addView(d);
			actionButtons.addView(neighbourAttack);
		}
		
		actionButtons.setColumnShrinkable(1, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.territory, menu);
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
