package jh.slm.commonaclasses;

import java.util.ArrayList;
import java.util.List;

import jh.slm.interfaces.ScalingListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Yaxis extends View{

    private List<ScalingListener> listeners = new ArrayList<ScalingListener>();

    public int nTics = 2;
    public float lowLevel;
	public float highLevel;
	public float range;
	Rect rect;
	float height;
	float width;
	Paint penBlack;

	public Yaxis(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	public Yaxis(Context context, AttributeSet attrs){
		super(context, attrs);

	}
	public Yaxis(Context context) {
		super(context);
	}

    public void addListener(ScalingListener listener) {
        listeners.add(listener);
    }
    
    void notifyNewScaling(int base, int range){
        for(ScalingListener listener : listeners){
            listener.ScalingHappened(base, range);
        }
    }

	public void Init(int lowLevel, int range){
		this.lowLevel = lowLevel / 100;
		this.range = range / 100;
		highLevel = this.lowLevel + this.range;
		nTics = Math.round(this.range / 10 + 1);
		int size = getResources().getDimensionPixelSize(R.dimen.myFontSize);
		penBlack = new Paint(); penBlack.setColor(Color.BLACK); penBlack.setStyle(Paint.Style.FILL); penBlack.setStrokeWidth(1); penBlack.setTextSize(size);
		rect = new Rect();
		setOnTouchListener(new View.OnTouchListener() {
			float y;
			float hl;
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int motionEvent = event.getActionMasked();
				switch(motionEvent){
				case MotionEvent.ACTION_DOWN:
					y = event.getY();
					hl = highLevel;
					break;
				case MotionEvent.ACTION_MOVE:
					float y1 = event.getY();
					if(y < view.getHeight() / 2){
						highLevel = Math.round((hl + (-y1 + y))) / 10 * 10;
						highLevel = highLevel > 200 ? 200 : highLevel;
						highLevel = highLevel < Yaxis.this.lowLevel +10 ? Yaxis.this.lowLevel +10 : highLevel;
						Yaxis.this.range = highLevel - Yaxis.this.lowLevel;
						nTics = Math.round((highLevel - Yaxis.this.lowLevel) / 10 + 1);
						notifyNewScaling(Math.round(Yaxis.this.lowLevel * 100), Math.round((highLevel - Yaxis.this.lowLevel) * 100));
					}
					else{
						highLevel = Math.round((hl + (-y1 + y))) / 10 * 10;
						highLevel = highLevel > 200 ? 200 : highLevel;
						highLevel = highLevel < -200 + Yaxis.this.range ? -200 + Yaxis.this.range : highLevel;
						Yaxis.this.lowLevel = highLevel - Yaxis.this.range;
						notifyNewScaling(Math.round(Yaxis.this.lowLevel * 100), Math.round(Yaxis.this.range) * 100);
					}
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return true;
			}
		});
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		if (rect == null)
			return;
		
		super.onDraw(canvas);
		getDrawingRect(rect);
		height = rect.height();
		width = rect.width();
	
		for (int i = 0; i < nTics; i++){
			float y = (height *6 / 7-2) / (nTics - 1) *i + height / 7+1;
			canvas.drawLine(width*2/3, y, width, y, penBlack);
			canvas.drawText(String.valueOf((int)(highLevel - 10 * i)), 0, y, penBlack);
		}
	}
}
