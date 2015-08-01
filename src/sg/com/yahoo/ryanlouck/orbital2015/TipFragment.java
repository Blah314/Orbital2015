package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.Random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TipFragment extends DialogFragment {
	
	private String[] tips;
	private Random r = new Random();
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        tips = getResources().getStringArray(R.array.tips);
        
        builder.setTitle(getResources().getString(R.string.totd));
        
        int i = r.nextInt(tips.length);
        builder.setMessage(tips[i]);
        
        builder.setPositiveButton(R.string.close_regions, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	dialog.cancel();
                }
            });
        
        return builder.create();
	}
}
