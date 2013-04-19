package com.therealjoshua.essentials.bitmaploader.binders;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;

public class BackgroundBinder extends GroupViewBinder<View> {
	
	public BackgroundBinder(BitmapLoader loader) {
		super(loader);
	}
	
	@Override
	public Cancelable load(View view, String uri, BitmapFactory.Options options, Rect outPadding) {
		cancel(view);
		setBackground(view, getLoadingDrawable(view));
		return super.load(view, uri, options, outPadding);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setBackground(View view, Drawable drawable) {
		if (view == null) return;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
	}

	@Override
	public void onSuccess(View view, Bitmap bitmap, BitmapSource source, LoadRequest request) {
		if (view == null) return;
		BitmapDrawable d = new BitmapDrawable(view.getContext().getResources(), bitmap);
		setBackground(view, d);
	}

	@Override
	public void onError(View  view, Throwable error, ErrorSource source, LoadRequest request) {
		setBackground(view, getFaultDrawable(view));
	}
	
}