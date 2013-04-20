package com.therealjoshua.essentialsloadersample1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.binders.FadeImageViewBinder;

/*
 * loading an image from the sdcard
 */
public class Sample5Activity extends Activity {
	
	private Button loadBtn;
	private ImageView imageView;
	private String uri = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		loadBtn = new Button(this);
		loadBtn.setEnabled(false);
		loadBtn.setText("Load from Disk");
		loadBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BitmapLoader loader = new BitmapLoader(Sample5Activity.this, null, null);
				FadeImageViewBinder binder = new FadeImageViewBinder(loader);
				String fUri = "file://" + uri; // give the path a protocol
				binder.setLoadingDrawableResId(R.drawable.gray_k02);
				binder.setFaultDrawableResId(R.drawable.error_k02);
				binder.load(imageView, fUri);
			}
		});
		ll.addView(loadBtn);
		
		imageView = new ImageView(this);
		ll.addView(imageView);
		setContentView(ll);
		
		// install a bitmap to the filing system.
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sd_img, null);
				File file = new File(getCacheDir().getAbsolutePath() + "/sd_img");
				uri = file.getAbsolutePath();
				try {
					FileOutputStream stream = new FileOutputStream(file);
					bm.compress(CompressFormat.JPEG, 70, stream);
					stream.flush();
					stream.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				loadBtn.setEnabled(true);
			};
		}.execute();
		
		
	}
	
}