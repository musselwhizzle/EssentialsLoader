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
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.Locator;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Callback;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;

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
	
	public SingleViewBinder() {
		this(Locator.getBitmapLoader());
	}
	
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
	
	public BitmapLoader.LoadRequest build(String uri) {
		ViewBinderLoadRequest r = new ViewBinderLoadRequest(this, loader);
		r.setUri(uri);
		return r;
	}
	
	public Cancelable load(BitmapLoader.LoadRequest request) {
		if (request.getCallback() == null) {
			request.setCallback(this);
		} else {
			request.setCallback(new MyCallback(this, request.getCallback()));
		}
		return loader.load(request);
	}
	
	public Cancelable load(String uri) {
		return build(uri).load();
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
	
	public static class ViewBinderLoadRequest extends BitmapLoader.LoadRequest {
		private SingleViewBinder binder;
		private ViewBinderLoadRequest(SingleViewBinder binder, BitmapLoader loader) {
			super(loader);
			this.binder = binder;
		}
		
		@Override
		public Cancelable load() {
			return binder.load(this);
		}
	}
	
	private static class MyCallback implements Callback {
		private Callback callback;
		private SingleViewBinder binder;
		
		private MyCallback(SingleViewBinder binder, Callback callback) {
			this.binder = binder;
			this.callback = callback;
		}
		
		@Override
		public void onSuccess(Bitmap bitmap, BitmapSource source, LoadRequest request) {
			binder.onSuccess(bitmap, source, request);
			if (callback != null) callback.onSuccess(bitmap, source, request);
		}

		@Override
		public void onError(Throwable error, ErrorSource source, LoadRequest request) {
			binder.onError(error, source, request);
			if (callback != null) callback.onError(error, source, request);
		}
	}
	
}