/*
 * Copyright (c) 2012 Joshua Musselwhite
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.therealjoshua.essentials.bitmaploader.binders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;
import com.therealjoshua.essentials.bitmaploader.Locator;

/**
 * A binder used to associate the loaded image with an ImageView
 */
public class ImageViewBinder extends GroupViewBinder<ImageView> {
	
	public ImageViewBinder(Context context) {
		this(context, Locator.getBitmapLoader());
	}
	
	public ImageViewBinder(Context context, BitmapLoader loader) {
		super(context, loader);
	}
	
	@Override
	public Cancelable load(ImageView imageView, LoadRequest request) {
		cancel(imageView);
		if (imageView == null) return null;
		imageView.setImageDrawable(getLoadingDrawable());
		return super.load(imageView, request);
	}

	@Override
	public void onSuccess(ImageView imageView, Bitmap bitmap, 
			BitmapSource source, LoadRequest request) {
		imageView.setImageDrawable(getSuccessDrawable(bitmap));
	}

	@Override
	public void onError(ImageView imageView, Throwable error, 
			ErrorSource source, LoadRequest request) {
		imageView.setImageDrawable(getFaultDrawable());
	}
	
	/**
	 * Gets the drawable used when a successful bitmap has been loaded
	 * 
	 * @param bitmap The loaded Bitmap
	 * @return The Drawable to use in the ImageView
	 */
	protected Drawable getSuccessDrawable(Bitmap bitmap) {
		return new BitmapDrawable(getContext().getResources(), bitmap);
	}
	
}