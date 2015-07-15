package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RegionsScreen extends DialogFragment {
	
	private Game g;
	private int targetRegion;
	
	public RegionsScreen(Game g, int targetRegion){
		this.g = g;
		this.targetRegion = targetRegion;
	}
	
	// dialog that pops up when the user presses the regions button in TerritoryActivity
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
		ArrayList<Territory> Ts = g.getRegion(targetRegion);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < Ts.size(); i++){
			Territory t = Ts.get(i);
			sb.append(t.getName() + " (" + t.getAbbrvName() + ")\n");
		}
		
	    builder.setTitle(getResources().getString(R.string.regions_display));
	    builder.setMessage(getResources().getString(R.string.regions_desc) + "\n" + sb.toString());
	        
	    builder.setPositiveButton(R.string.close_regions, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		dialog.cancel();   				
	        }
	    });
	        
	    return builder.create();
	}
}
