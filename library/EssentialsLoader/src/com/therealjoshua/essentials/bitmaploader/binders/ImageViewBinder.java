package com.therealjoshua.essentials.bitmaploader.binders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;

public class ImageViewBinder extends GroupViewBinder<ImageView> {
	
	public ImageViewBinder(BitmapLoader loader) {
		super(loader);
	}
	
	@Override
	public Cancelable load(ImageView imageView, String url, BitmapFactory.Options options, Rect outPadding) {
		cancel(imageView);
		if (imageView == null) return null;
		imageView.setImageDrawable(getLoadingDrawable(imageView));
		return super.load(imageView, url, options, outPadding);
	}

	@Override
	public void onBitmap(ImageView imageView, Bitmap bitmap, 
			BitmapLoader.BitmapSource source, BitmapLoader.LoadRequest request) {
		imageView.setImageBitmap(bitmap);
	}

	@Override
	public void onError(ImageView imageView, String url, Throwable error, BitmapLoader.ErrorSource source) {
		imageView.setImageDrawable(getFaultDrawable(imageView));
	}
	
	
}