package jh.slm.cegis;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	static private MyClass myClass = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (myClass != null)
			myClass.InitViews(this);

		myClass = MyClass.Instance(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onReset(View view){
		myClass.onReset();
	}

	public void onPC(View view){
		myClass.onPC();
	}

	public void onSwap(View view){
		myClass.selection = myClass.onSwap(myClass.selection);
	}

	public void onOn_Off(View view){
		myClass.on_off = myClass.onOn_Off(myClass.on_off);
	}
	public void onFS(View view){
		myClass.fs = myClass.onFS(myClass.fs);
	}
	public void onMLN(View view){
		myClass.mln = myClass.onMLN(myClass.mln);
	}

}
