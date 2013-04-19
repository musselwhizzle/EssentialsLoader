package com.therealjoshua.essentialsloadersample1;

import com.therealjoshua.essentials.bitmaploader.BitmapLoaderLocator;

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
		btn.setText("Clear Cache");
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BitmapLoaderLocator.getBitmapLoader().clearCache();
			}
		});
		ll.addView(btn);
		
		for (int i=1; i<=3; i++) {
			btn = new Button(this);
			btn.setText("Sample " + i);
			btn.setId(i);
			btn.setOnClickListener(clickListener);
			ll.addView(btn);
		}
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case 1: startActivity(new Intent(MainActivity.this, Sample1Activity.class)); break;
				case 2: startActivity(new Intent(MainActivity.this, Sample2Activity.class)); break;
				case 3: startActivity(new Intent(MainActivity.this, Sample3Activity.class)); break;
			}
		}
	};

}
