package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;
import com.therealjoshua.essentials.bitmaploader.Locator;

/*
 * Shows working with the bitmap loader directly
 */
public class Sample3Activity extends Activity {
	
	private BitmapLoader loader;
	private Cancelable cancelable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// a bitmaploader that uses the master memory cache but no disk cache
		loader = new BitmapLoader(this, Locator.getMemoryCache(), null);
		setContentView(new MyRemoteView(this));
	}
	
	private class MyRemoteView extends View {
		
		private Bitmap bitmap;
		
		public MyRemoteView(Context context) {
			super(context);
			loadImage();
			setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					loadImage();
				}
			});
		}
		
		public void loadImage() {
			int position = (int)Math.floor( Math.random() * Images.ANIMAL_IMAGES.length );
			if (cancelable != null) cancelable.cancel();
			cancelable = loader.load(Images.ANIMAL_IMAGES[position], new BitmapLoader.Callback() {
				@Override
				public void onError(Throwable error, ErrorSource source, LoadRequest request) {
					
				}
				
				@Override
				public void onSuccess(Bitmap bitmap, BitmapSource source, LoadRequest request) {
					MyRemoteView.this.bitmap = bitmap;
					requestLayout();
					invalidate();
				}
			});
		}
		
		/*
		 * this is not production code
		 */
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			if (bitmap == null) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			} else {
				setMeasuredDimension(bitmap.getWidth(), bitmap.getHeight());
			}
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (bitmap != null) {
				canvas.drawBitmap(bitmap, 0, 0, null);
			}
		}
		
	}
	
}