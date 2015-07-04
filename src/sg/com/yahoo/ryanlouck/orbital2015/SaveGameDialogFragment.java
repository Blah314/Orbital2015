package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class SaveGameDialogFragment extends DialogFragment {
	
	// dialog that pops up when the user already has a saved game
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(getResources().getString(R.string.overwrite));
        builder.setMessage(getResources().getString(R.string.overwrite_desc));
        
    	builder.setPositiveButton(R.string.y, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Intent newGameLaunch = new Intent(getActivity().getApplicationContext(), NewGameActivity.class);
				startActivity(newGameLaunch);    				
            }
        });
    	
    	builder.setNegativeButton(R.string.n, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	dialog.cancel();   				
            }
        });
        
        return builder.create();
	}
}
