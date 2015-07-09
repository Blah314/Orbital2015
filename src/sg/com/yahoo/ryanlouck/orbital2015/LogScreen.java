package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class LogScreen extends DialogFragment {
	
	private String log;
	
	public LogScreen(String log){
		this.log = log;
	}
	
	// dialog that pops up when the user presses the log button on MapActivity
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	    builder.setTitle(getResources().getString(R.string.log_menu));
	    builder.setMessage(log);
	        
	    builder.setPositiveButton(R.string.close_log, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		dialog.cancel();   				
	        }
	    });
	        
	    return builder.create();
	}
}
