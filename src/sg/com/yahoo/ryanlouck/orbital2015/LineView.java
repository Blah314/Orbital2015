package sg.com.yahoo.ryanlouck.orbital2015;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class LineView extends View {
	
	private Paint p = new Paint();
	private ArrayList<int[]> lines;
	
    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineView(Context context) {
        super(context);
    }
    
    public void setMap(ArrayList<int[]> mapLines){
    	this.lines = mapLines;
    	p.setColor(Color.WHITE);
    }
    
    protected void onDraw(Canvas c){
    	if(lines != null){
    		Iterator<int[]> lineIterator = lines.iterator();
    		while(lineIterator.hasNext()){
    			int[] l = lineIterator.next();
    			c.drawLine(l[0], l[1], l[2], l[3], p);
    		}
    	}
    }
}
