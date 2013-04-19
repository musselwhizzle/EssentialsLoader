package com.therealjoshua.essentials.bitmaploader.binders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;

public class FadeImageViewBinder extends ImageViewBinder {
	private int transitionDuration = 250;
	
	public FadeImageViewBinder(BitmapLoader loader) {
		super(loader);
	}
	
	public int getTransitionDuration() { return transitionDuration; }
	
	public void setTransitionDuration(int transitionDuration) {
		this.transitionDuration = transitionDuration;
	}
	
	@Override
	public Cancelable load(ImageView imageView, String url, Options options, Rect outPadding) {
		return super.load(imageView, url, options, outPadding);
	}
	
	@Override
	public void onBitmap(ImageView imageView, Bitmap bitmap, BitmapLoader.BitmapSource source, BitmapLoader.LoadRequest request) {
		if (source == BitmapLoader.BitmapSource.MEMORY || !imageView.isShown()) {
			super.onBitmap(imageView, bitmap, source, request);
		} else {
			Drawable currentDrawable = imageView.getDrawable();
			if (currentDrawable == null) {
				currentDrawable = new ColorDrawable(android.R.color.transparent);
			}
			Drawable[] fades = new Drawable[2];
			fades[0] = currentDrawable;
			fades[1] = new BitmapDrawable(imageView.getContext().getResources(), bitmap);
			crossFade(fades, imageView);
		}
	}
	
	@Override
	public void onError(ImageView imageView, String url, Throwable error, BitmapLoader.ErrorSource source) {
		if (source != BitmapLoader.ErrorSource.EXTERNAL) {
			super.onError(imageView, url, error, source);
		}
		else {
			Drawable currentDrawable = imageView.getDrawable();
			Drawable faultDrawable = getFaultDrawable(imageView);
			if (currentDrawable == null) {
				currentDrawable = new ColorDrawable(android.R.color.transparent);
			}
			if (faultDrawable == null) {
				faultDrawable = new ColorDrawable(android.R.color.transparent);
			}
			Drawable[] fades = new Drawable[2];
			fades[0] = currentDrawable;
			fades[1] = faultDrawable;
			crossFade(fades, imageView);
		}
	}
	
	private void crossFade(Drawable[] fades, ImageView imageView) {
		TransitionDrawable transitionDrawable = new TransitionDrawable(fades);
		transitionDrawable.setCrossFadeEnabled(true);
		imageView.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(transitionDuration);
	}
	
}