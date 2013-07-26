package com.therealjoshua.essentialsloadersample1;

import java.io.File;

import com.therealjoshua.essentials.bitmaploader.Locator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ScrollView scroll = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		scroll.addView(ll);
		setContentView(scroll);
		
		Button btn;
		
		btn = new Button(this);
		btn.setText("Clear Cache");
		btn.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Locator.getBitmapLoader().clearCache();
				// really shouldn't do this on the UI thread...but hey it's an example
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					HttpResponseCache httpCache = HttpResponseCache.getInstalled();
					if (httpCache != null) {
						File dir = new File(getCacheDir(), "HttpCache");
						deleteFile(dir);
					}
				}
			}
		});
		ll.addView(btn);
		
		for (int i=1; i<=6; i++) {
			btn = new Button(this);
			btn.setText("Sample " + i);
			btn.setId(i);
			btn.setOnClickListener(clickListener);
			ll.addView(btn);
		}
	}
	
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteFile(child);
			}
		}
		file.delete();
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case 1: startActivity(new Intent(MainActivity.this, Sample1Activity.class)); break;
				case 2: startActivity(new Intent(MainActivity.this, Sample2Activity.class)); break;
				case 3: startActivity(new Intent(MainActivity.this, Sample3Activity.class)); break;
				case 4: startActivity(new Intent(MainActivity.this, Sample4Activity.class)); break;
				case 5: startActivity(new Intent(MainActivity.this, Sample5Activity.class)); break;
				case 6: startActivity(new Intent(MainActivity.this, Sample6Activity.class)); break;
			}
		}
	};

}
