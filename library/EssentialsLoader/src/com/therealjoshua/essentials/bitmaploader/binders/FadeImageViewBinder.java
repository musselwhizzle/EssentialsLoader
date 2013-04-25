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
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Callback;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;

/**
 * A binder used to associate apply a fade transition to the loaded image
 * in an ImageView
 */
public class FadeImageViewBinder extends ImageViewBinder {
	private int transitionDuration = 250;
	
	public FadeImageViewBinder(Context context, BitmapLoader loader) {
		super(context, loader);
	}
	
	/**
	 * The length of the set transition
	 * @return
	 */
	public int getTransitionDuration() { 
		return transitionDuration; 
	}
	
	/**
	 * Sets the length of the transition on the fade
	 * 
	 * @param transitionDuration
	 */
	public void setTransitionDuration(int transitionDuration) {
		this.transitionDuration = transitionDuration;
	}
	
	@Override
	public Cancelable load(ImageView imageView, String uri, Callback callback, 
			Options options, Rect outPadding) {
		return super.load(imageView, uri, callback, options, outPadding);
	}
	
	@Override
	public void onSuccess(ImageView imageView, Bitmap bitmap, 
			BitmapSource source, LoadRequest request) {
		if (source == BitmapSource.MEMORY || !imageView.isShown()) {
			super.onSuccess(imageView, bitmap, source, request);
		} else {
			TransitionDrawable td = getSuccessFadeDrawable(imageView, bitmap);
			applyTransition(td, imageView);
		}
	}
	
	@Override
	public void onError(ImageView imageView, Throwable error, ErrorSource source, LoadRequest request) {
		if (source != BitmapLoader.ErrorSource.EXTERNAL) {
			super.onError(imageView, error, source, request);
		}
		else {
			TransitionDrawable td = getErrorFadeDrawable(imageView);
			applyTransition(td, imageView);
		}
	}
	
	protected TransitionDrawable getSuccessFadeDrawable(ImageView imageView, Bitmap bitmap) {
		Drawable currentDrawable = imageView.getDrawable();
		if (currentDrawable == null) {
			currentDrawable = new ColorDrawable(android.R.color.transparent);
		}
		Drawable[] fades = new Drawable[2];
		fades[0] = currentDrawable;
		fades[1] = getSuccessDrawable(bitmap);
		TransitionDrawable transitionDrawable = new TransitionDrawable(fades);
		transitionDrawable.setCrossFadeEnabled(true);
		return transitionDrawable;
	}
	
	protected TransitionDrawable getErrorFadeDrawable(ImageView imageView) {
		Drawable currentDrawable = imageView.getDrawable();
		Drawable faultDrawable = getFaultDrawable();
		if (currentDrawable == null) {
			currentDrawable = new ColorDrawable(android.R.color.transparent);
		}
		if (faultDrawable == null) {
			faultDrawable = new ColorDrawable(android.R.color.transparent);
		}
		Drawable[] fades = new Drawable[2];
		fades[0] = currentDrawable;
		fades[1] = faultDrawable;
		TransitionDrawable transitionDrawable = new TransitionDrawable(fades);
		transitionDrawable.setCrossFadeEnabled(true);
		return transitionDrawable;
	}
	
	protected void applyTransition(TransitionDrawable td, ImageView imageView) {
		imageView.setImageDrawable(td);
		td.startTransition(transitionDuration);
	}
	
}