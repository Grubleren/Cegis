package jh.slm.commonaclasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class MyEditText extends EditText {

	private MyEditTextListener myListener;


	public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyEditText(Context context) {
		super(context);
	}

	@Override
	public void onEditorAction(int actionCode) {
		if (actionCode == EditorInfo.IME_ACTION_DONE) {
			if (myListener != null) 
				myListener.onKey(this, this.getText().toString());
		}
	}

	public void setOnMyEditTextListener(MyEditTextListener listener) {
		myListener = listener;
	}


	public interface MyEditTextListener {
		public abstract void onKey(MyEditText ctrl, String text);
	}
}
