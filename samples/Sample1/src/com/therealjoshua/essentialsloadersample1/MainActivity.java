package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		setContentView(ll);
		
		Button btn;
		
		btn = new Button(this);
		btn.setText("Sample 1");
		btn.setId(1);
		btn.setOnClickListener(clickListener);
		ll.addView(btn);
		
		btn = new Button(this);
		btn.setText("Sample 2");
		btn.setId(2);
		btn.setOnClickListener(clickListener);
		ll.addView(btn);
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case 1: startActivity(new Intent(MainActivity.this, Sample1Activity.class)); break;
				case 2: startActivity(new Intent(MainActivity.this, Sample2Activity.class)); break;
			}
		}
	};

}
