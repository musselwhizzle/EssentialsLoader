package com.therealjoshua.essentials.bitmaploader.binders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;

public class SingleViewBinder implements BitmapLoader.Callback {
	private BitmapLoader loader;
	private BitmapLoader.Cancelable quitable;
	
	private Animation animation;
	public Animation getAnimation() { return animation; }
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	private Drawable faultDrawable;
	private Drawable loadingDrawable;
	
	public SingleViewBinder(BitmapLoader loader) {
		this.loader = loader;
	}
	
	public Drawable getFaultDrawable() {
		return faultDrawable;
	}
	
	public Drawable getLoadingDrawable() {
		return loadingDrawable;
	}
	
	public void setFaultDrawable(Drawable faultDrawable) {
		this.faultDrawable = faultDrawable;
	}
	
	public void setFaultResource(Context context, int resId) {
		faultDrawable = context.getResources().getDrawable(resId);
	}
	
	public void setLoadingDrawable(Drawable faultDrawable) {
		this.loadingDrawable = faultDrawable;
	}
	
	public void setLoadingResource(Context context, int resId) {
		loadingDrawable = context.getResources().getDrawable(resId);
	}
	
	public Cancelable load(String uri) {
		return load(uri, null, null);
	}
	
	public Cancelable load(String uri, BitmapFactory.Options options) {
		return load(uri, options, null);
	}
	
	public Cancelable load(String uri, BitmapFactory.Options options, Rect outPadding) {
		quitable = loader.load(uri, this, options, outPadding);
		return quitable;
	}
	
	public void cancel() {
		if (quitable != null) quitable.cancel();
	}

	@Override
	public void onSuccess(Bitmap bitmap, BitmapLoader.BitmapSource source, BitmapLoader.LoadRequest request) {
	}

	@Override
	public void onError(Throwable error, BitmapLoader.ErrorSource source, BitmapLoader.LoadRequest request) {
	}
	
}