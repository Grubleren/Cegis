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
import android.view.View;

public class Xaxis extends View{

	public enum XaxisType
	{
		Cpb,
		Lin
	}

	private List<ScalingListener> listeners = new ArrayList<ScalingListener>();

	public int nTics = 4;
	XaxisType type;
	double startFreq;
	double endFreq;
	Rect rect;
	float height;
	float width;
	int size;
	Paint penBlack;
	String[] cpbFreqs = new String[]{"31.5","63","125","250","500","1 k","2 k","4 k","8 k"};
	String[] linFreqs = new String[]{"0","1 k","2 k","3 k","4 k"};

	public Xaxis(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	public Xaxis(Context context, AttributeSet attrs){
		super(context, attrs);

	}
	public Xaxis(Context context) {
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

	public void Init(XaxisType type, double startFreq, double endFreq){
		this.type = type;
		this.startFreq = startFreq;
		this.endFreq = endFreq;
		size = getResources().getDimensionPixelSize(R.dimen.myFontSize);
		penBlack = new Paint(); penBlack.setColor(Color.BLACK); penBlack.setStyle(Paint.Style.FILL); penBlack.setStrokeWidth(1); penBlack.setTextSize(size);
		rect = new Rect();
	}

	@Override
	protected void onDraw(Canvas canvas){
		if (rect == null)
			return;

		super.onDraw(canvas);
		getDrawingRect(rect);
		height = rect.height();
		width = rect.width();

		if (type == XaxisType.Cpb){
			for (int i = 0; i < cpbFreqs.length; i++){
				float x = width /54 * 3 + width /9*i;
				canvas.drawLine(x, height/3, x, 0, penBlack);
				canvas.drawText(cpbFreqs[i], x, height, penBlack);
			}
		}
		else if (type == XaxisType.Lin){
			for (int i = 0; i < linFreqs.length; i++){
				float x = width /(linFreqs.length-1)*i;
				if (i != linFreqs.length -1){
					canvas.drawLine(x, height/3, x, 0, penBlack);
					canvas.drawText(linFreqs[i], x, height, penBlack);
				}
				else{
					canvas.drawLine(x, height/3, x-1, 0, penBlack);
					canvas.drawText(linFreqs[i], x-2*size, height, penBlack);
				}
			}

		}
	}
}
