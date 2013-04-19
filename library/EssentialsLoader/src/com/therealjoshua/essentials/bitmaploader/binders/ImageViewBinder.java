package com.therealjoshua.essentials.bitmaploader.binders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;

public class ImageViewBinder extends GroupViewBinder<ImageView> {
	
	public ImageViewBinder(BitmapLoader loader) {
		super(loader);
	}
	
	@Override
	public Cancelable load(ImageView imageView, String uri, BitmapFactory.Options options, Rect outPadding) {
		cancel(imageView);
		if (imageView == null) return null;
		imageView.setImageDrawable(getLoadingDrawable(imageView));
		return super.load(imageView, uri, options, outPadding);
	}

	@Override
	public void onSuccess(ImageView imageView, Bitmap bitmap, 
			BitmapSource source, LoadRequest request) {
		imageView.setImageBitmap(bitmap);
	}

	@Override
	public void onError(ImageView imageView, Throwable error, 
			ErrorSource source, LoadRequest request) {
		imageView.setImageDrawable(getFaultDrawable(imageView));
	}
	
	
}