package jh.slm.commonaclasses;

import jh.slm.interfaces.ScalingListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SpectrumView  extends View implements ScalingListener{
	Matrix matrix;
	Paint penBlackNoFill;
	Paint penBlack;
	Paint penWhite;
	Paint pen;
	Rect rect;
	MyBaseClass myClass;
	int[] rmsSpectrum;
	int[] leqSpectrum;
	int cursor;
	float cursorRef = 0;
	int noSpectralLines;
	int height;
	int width;
	public double xScale;
	int buttom;
	int range;
	double yScale;
	double yTop;
	int lineWidth1;
	int lineWidth2;
	String[] octaves = new String[]{"31.5 Hz", "63 Hz", "125 Hz", "250 Hz", "500 Hz", "1000 Hz", "2000 Hz", "4000 Hz", "8000 Hz"};
	String[] thirdOctaves = new String[]{"25 Hz", "31.5 Hz", "40 Hz", "50 Hz", "63 Hz", "80 Hz", "100 Hz", "125 Hz", "160 Hz", "200 Hz", "250 Hz", "315 Hz", "400 Hz", "500 Hz", "630 Hz", "800 Hz", "1000 Hz", "1250 Hz", "1600 Hz", "2000 Hz", "2500 Hz", "3150 Hz", "4000 Hz", "5000 Hz", "6300 Hz", "8000 Hz", "10000 Hz"};
	String[] octFreq;
	
	public SpectrumView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	public SpectrumView(Context context, AttributeSet attrs){
		super(context, attrs);

		penBlackNoFill = new Paint(); penBlackNoFill.setColor(Color.BLACK); penBlackNoFill.setStyle(Paint.Style.STROKE); penBlackNoFill.setStrokeWidth(0); 
		penBlack = new Paint(); penBlack.setColor(Color.BLACK); penBlack.setStyle(Paint.Style.FILL); penBlack.setStrokeWidth(0);
		penWhite = new Paint(); penWhite.setColor(Color.WHITE); penWhite.setStyle(Paint.Style.FILL); penWhite.setStrokeWidth(0);
		rect = new Rect();
	}

	public SpectrumView(Context context){
		super(context);
	}

	public void Connect(MyBaseClass myClass, int[] rmsSpectrum, int[] leqSpectrum, int noSpectralLines, int buttom, int range){
		this.buttom = buttom;
		this.range = range;
		this.myClass = myClass;
		this.noSpectralLines = noSpectralLines;
		this.rmsSpectrum = rmsSpectrum;
		this.leqSpectrum = leqSpectrum;
		octFreq = noSpectralLines == 9 ? octaves : thirdOctaves;
		setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int motionEvent = event.getActionMasked();
				switch(motionEvent){
				case MotionEvent.ACTION_DOWN:
					cursorRef = event.getX();
//					Log.v("jens", "down"+String.valueOf(cursorRef));
					cursor = (int)((cursorRef-1)/xScale);
					break;
				case MotionEvent.ACTION_MOVE:
					float x = event.getX();
//					Log.v("jens", "move"+String.valueOf(x));
					cursor = (int)(((x-cursorRef)*0.5+cursorRef-1)/xScale);
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return true;
			}
		});
	}

	public void ScalingHappened(int buttom, int range){
		this.buttom = buttom;
		this.range = range;
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

		getDrawingRect(rect);
		height = rect.height();
		width = rect.width();
		xScale = (double)width / noSpectralLines;
		yScale = (double)height / range;
		yTop = (buttom + range) * yScale;
		lineWidth1 = (int)xScale - 1;
		lineWidth2 = (int)xScale - 2;
		
		lineWidth1 = noSpectralLines < 50 ? lineWidth1 : (int)Math.ceil(xScale);
		lineWidth2 = noSpectralLines < 50 ? lineWidth2 : (int)Math.ceil(xScale);

		int yRms;
		int p1x;
		int p1y;
		int p2x;
		int p2y;
		int yLeq;
		int heightLeq;
		int r1x;
		int r1y = Integer.MAX_VALUE;
		int r2x;
		int r2y;

		for (int i = 0; i < noSpectralLines; i++)
		{
			int x = (int)(i * xScale) + 1;

			if (leqSpectrum != null)
			{
				yLeq = (int)(yTop - leqSpectrum[i] * yScale);
				heightLeq = height - yLeq;
				r1x = x;
				r1y = yLeq;
				r2x = x + lineWidth1;
				r2y = yLeq + heightLeq;
				canvas.drawRect(r1x, r1y, r2x, r2y, penBlack);
				cursor = cursor < 0 ? 0 : cursor;
				cursor = cursor > leqSpectrum.length - 1 ? leqSpectrum.length - 1 : cursor;
				myClass.SetCursorLeqValue(leqSpectrum[cursor]);
			}
			else
				myClass.SetCursorLeqValue(Short.MIN_VALUE);

			if (rmsSpectrum != null)
			{
				yRms = (int)(yTop - rmsSpectrum[i] * yScale);
				p1x = x;
				p1y = yRms;
				p2x = p1x + lineWidth2;
				p2y = p1y;
				if (p2y < r1y)
					pen = penBlack;
				else
					pen = penWhite;
				canvas.drawLine(p1x, p1y, p2x, p2y, pen);
				cursor = cursor < 0 ? 0 : cursor;
				cursor = cursor > octFreq.length - 1 ? octFreq.length - 1 : cursor;
				myClass.SetCursorFrequency(octFreq[cursor]);
			}
			else
				myClass.SetCursorFrequency(Double.toString(cursor*10)+" Hz");

			canvas.drawLine((float)(cursor * xScale + 1 + lineWidth1 / 2), rect.bottom, (float)(cursor * xScale + 1 + lineWidth1 / 2), rect.top, penBlack);
		}

		rect.top += 1;
		rect.left += 1;
		rect.right -= 1;
		rect.bottom -=1;
		canvas.drawRect(rect, penBlackNoFill);
	}
}
