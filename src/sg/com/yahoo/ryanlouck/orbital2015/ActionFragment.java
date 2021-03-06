package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActionFragment extends DialogFragment {
	
	private int requested, limit, numArmies, target, origin;
	private boolean isAdd, isFriendly;
	
	public ActionFragment(int limit, int numArmies, int target, int origin, boolean isAdd, boolean isFriendly){
		this.limit = limit;
		this.numArmies = numArmies;
		this.target = target;
		this.origin = origin;
		this.isAdd = isAdd;
		this.isFriendly = isFriendly;
		this.requested = 0;
	}
	
	// create a dialog to ask the player about the action he selected
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SeekBar armySlider = new SeekBar(this.getActivity());
        final EditText num = new EditText(this.getActivity());
        final TextView limited = new TextView(this.getActivity());
        
        num.setInputType(InputType.TYPE_CLASS_NUMBER);
        
        armySlider.setProgress(requested);
        num.setText(Integer.toString(requested));
        
        // setting the title depending on the situation
        if(isAdd){
        	armySlider.setMax(limit);
        	limited.setText("You can add a maximum of " + Integer.toString(limit) + " units.");
        	builder.setTitle(getResources().getString(R.string.add_num));
        }
        
        else{
        	armySlider.setMax(numArmies);
        	limited.setText("You have " + Integer.toString(numArmies) + " units at your disposal.");
        	if(isFriendly) builder.setTitle(getResources().getString(R.string.move_num));
        	else builder.setTitle(getResources().getString(R.string.attack_num));
        }
        
        // seekbar is used to set requested
        armySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar armySlider) {
				// nothing here
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar armySlider) {
				// nothing here either
				
			}
			
			@Override
			public void onProgressChanged(SeekBar armySlider, int progress,
					boolean fromUser) {
				if(fromUser){
					requested = progress;
					num.setText(Integer.toString(requested));
				}				
			}
		});
        
        LinearLayout l = new LinearLayout(this.getActivity());
        l.setOrientation(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1,-2);
        l.addView(limited, params);
        l.addView(armySlider, params);
        l.addView(num, params);
        num.setGravity(17);
        limited.setGravity(17);
        
        builder.setView(l);
        
        // positive button configuration
        if(isAdd){
        	builder.setPositiveButton(R.string.add_units, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			requested = Integer.parseInt(num.getText().toString());
        			if(requested > limit){
        				requested = limit;
        			}
        			Intent moveLaunch = new Intent(getActivity().getApplicationContext(), MapActivity.class);
        			moveLaunch.putExtra("add", true);
        			moveLaunch.putExtra("request", requested);
        			moveLaunch.putExtra("target", target);
        			moveLaunch.putExtra("origin", origin);
        			moveLaunch.putExtra("attack", false);
        			moveLaunch.putExtra("move", false);
        			moveLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    				startActivity(moveLaunch);
        		}
        	});
        }
        else if(isFriendly){
        	builder.setPositiveButton(R.string.move, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	requested = Integer.parseInt(num.getText().toString());
        			if(requested > numArmies){
        				requested = numArmies;
        			}
                	Intent moveLaunch = new Intent(getActivity().getApplicationContext(), MapActivity.class);
        			moveLaunch.putExtra("add", false);
        			moveLaunch.putExtra("request", requested);
        			moveLaunch.putExtra("target", target);
        			moveLaunch.putExtra("origin", origin);
        			moveLaunch.putExtra("attack", false);
        			moveLaunch.putExtra("move", true);
        			moveLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    				startActivity(moveLaunch);
                }
            });
        }
        else{
        	builder.setPositiveButton(R.string.attack, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	requested = Integer.parseInt(num.getText().toString());
        			if(requested > numArmies){
        				requested = numArmies;
        			}
                	Intent moveLaunch = new Intent(getActivity().getApplicationContext(), MapActivity.class);
        			moveLaunch.putExtra("add", false);
        			moveLaunch.putExtra("request", requested);
        			moveLaunch.putExtra("target", target);
        			moveLaunch.putExtra("origin", origin);
        			moveLaunch.putExtra("attack", true);
        			moveLaunch.putExtra("move", false);
        			moveLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    				startActivity(moveLaunch);
                }
            });
        }
        
        // cancel button
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
        
        return builder.create();
    }
}
