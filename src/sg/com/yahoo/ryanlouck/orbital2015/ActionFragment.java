package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActionFragment extends DialogFragment {
	
	private int requested;
	private int limit;
	private int numArmies;
	private int target, origin;
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
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SeekBar armySlider = new SeekBar(this.getActivity());
        final TextView num = new TextView(this.getActivity());
        
        armySlider.setProgress(requested);
        num.setText(Integer.toString(requested));
        
        if(isAdd){
        	armySlider.setMax(limit);
        	builder.setTitle(getResources().getString(R.string.add_num));
        }
        
        else{
        	armySlider.setMax(numArmies);
        	if(isFriendly) builder.setTitle(getResources().getString(R.string.move_num));
        	else builder.setTitle(getResources().getString(R.string.attack_num));
        }
        
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
        l.addView(armySlider);
        l.addView(num);
        
        builder.setView(l);
        
        if(isAdd){
        	builder.setPositiveButton(R.string.add_units, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			Intent moveLaunch = new Intent(getActivity().getApplicationContext(), MapActivity.class);
        			moveLaunch.putExtra("add", true);
        			moveLaunch.putExtra("request", requested);
        			moveLaunch.putExtra("target", target);
        			moveLaunch.putExtra("origin", origin);
        			moveLaunch.putExtra("attack", false);
        			moveLaunch.putExtra("move", false);
        			moveLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        			System.out.println(requested);
    				startActivity(moveLaunch);
        		}
        	});
        }
        else if(isFriendly){
        	builder.setPositiveButton(R.string.move, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
