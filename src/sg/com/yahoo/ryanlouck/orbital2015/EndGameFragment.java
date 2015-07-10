package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class EndGameFragment extends DialogFragment {
	
	private boolean win;
	private int medalWon;
	private String[] awards;
	
	public EndGameFragment(boolean won, int medal){
		win = won;
		medalWon = medal;
		awards = new String[]{"None", "Bronze", "Silver", "Gold", "Platinum"};
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // win dialog
        if(win){
        	builder.setTitle(getResources().getString(R.string.win));
        	if(medalWon != 0){
        		builder.setMessage(getResources().getString(R.string.win_text) + 
        		"\nYou won the " + awards[medalWon] + " medal.");
        	}
        	else{
        		builder.setMessage(getResources().getString(R.string.win_text));
        	}
        	builder.setPositiveButton(R.string.exit_win, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	getActivity().getApplicationContext().deleteFile("savegame");
                	Intent backLaunch = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                	backLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    				startActivity(backLaunch);
                }
            });
        }
        
        // lose dialog
        else{
        	builder.setTitle(getResources().getString(R.string.lose));
        	builder.setMessage(getResources().getString(R.string.lose_text));
        	builder.setPositiveButton(R.string.exit_lose, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	getActivity().getApplicationContext().deleteFile("savegame");
                	Intent backLaunch = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                	backLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    				startActivity(backLaunch);    				
                }
            });
        }
        
        return builder.create();
	}
	
	// return to mainActivity even if you close it otherwise
	public void onDismiss(DialogInterface dialog){
		Intent backLaunch = new Intent(getActivity().getApplicationContext(), MainActivity.class);
    	backLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(backLaunch);
	}
}
