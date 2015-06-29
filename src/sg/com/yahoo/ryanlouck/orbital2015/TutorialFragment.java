package sg.com.yahoo.ryanlouck.orbital2015;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TutorialFragment extends DialogFragment {
	
	private int num;
	private String[] tut;
	
	public TutorialFragment(int number, Activity act){
		num = number;
		tut = act.getResources().getStringArray(R.array.tutorial_messages);
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(getResources().getString(R.string.tutorial_window));
        builder.setMessage(tut[num]);
        
        builder.setNegativeButton(R.string.close_tut, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
        
        return builder.create();
	}
}
