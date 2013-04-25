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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Callback;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;

/**
 * The GroupViewBinder is an abstract class used to bind the loading to a view.
 * Subclasses of GroupViewBinder can implement how the loaded bitmap is used in the view.
 * For instance, the ImageViewBinder set's the loaded Bitmap to an ImageView while the 
 * BackgroundBinder sets the loaded Bitmap to be a background of any View. Custom Binders
 * can extend this class to provide their own behavior on load such as custom drawables
 * or any number of modifications. The GroupViewBinder and subclasses are meant to be used
 * as 1 binder instance per group of views. For instance, in a ListAdapter you will only
 * need 1 binder and just call the load method for each item renderer in the list
 * 
 * @param <T> Any type of view which the loaded image will be bound to
 */
public class GroupViewBinder<T extends View> {
	
	private BitmapLoader loader;
	private WeakHashMap<T, Cancelable> cancelables;
	private Bitmap loadingBitmap;
	private Bitmap faultBitmap;
	private Context context;
	private Resources res;
	
	public GroupViewBinder(Context context, BitmapLoader loader) {
		this.context = context;
		this.res = context.getResources();
		this.loader = loader;
		cancelables = new WeakHashMap<T, BitmapLoader.Cancelable>();
	}
	
	/**
	 * Gets the context passed to the constructor
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Creates a new drawable that represents what to show during the loading state. 
	 * 
	 * @return Drawable to show
	 */
	public Drawable getLoadingDrawable() {
//		if (loadingBitmap == null) return null;
		return new BitmapDrawable(res, loadingBitmap);
	}
	
	/**
	 * Creates a new drawable that represents what to show during the an error state. 
	 * 
	 * @return Drawable to show
	 */
	public Drawable getFaultDrawable() {
//		if (faultBitmap == null) return null;
		return new BitmapDrawable(res, faultBitmap);
	}
	
	/**
	 * Convenience method to create a Bitmap which will be used during the loading state.
	 * 
	 * @param resId
	 */
	public void setLoadingResource(int resId) {
		setLoadingBitmap( BitmapFactory.decodeResource(res, resId) );
	}
	
	/**
	 * Sets the Bitmap which will be used during the loading state.
	 * 
	 * @param Bitmap item to show while in the loading state
	 */
	public void setLoadingBitmap(Bitmap loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}
	
	/**
	 * Retrieves the Bitmap to show during the loading state
	 * 
	 * @return Bitmap
	 */
	public Bitmap getLoadingBitmap() {
		return loadingBitmap;
	}
	
	/**
	 * Convenience method to create a Bitmap which will be used during the error state.
	 * 
	 * @param resId
	 */
	public void setFaultResource(int resId) {
		setFaultBitmap( BitmapFactory.decodeResource(res, resId) );
	}
	
	/**
	 * Sets the Bitmap which will be used during the error state.
	 * 
	 * @param Bitmap item to show while in the error state
	 */
	public void setFaultBitmap(Bitmap faultBitmap) {
		this.faultBitmap = faultBitmap;
	}
	
	/**
	 * Retrieves the Bitmap to show during the error state
	 * 
	 * @return Bitmap
	 */
	public Bitmap getFaultBitmap() {
		return faultBitmap;
	}
	
	/**
	 * Starts the loading sequence. Should a load call already be in place for the view passed 
	 * in, the old call will be canceled and the new load call will proceed. 
	 * 
	 * @param view A view where the result of the load will be displayed
	 * @param uri A location to the bitmap. Can be http:// or file:///
	 * @return A object used to cancel the load
	 */
	public Cancelable load(T view, String uri) {
		return load(view, uri, null, null, null);
	}
	
	/**
	 * Starts the loading sequence. Should a load call already be in place for the view passed 
	 * in, the old call will be canceled and the new load call will proceed. 
	 * 
	 * @param view A view where the result of the load will be displayed
	 * @param uri A location to the bitmap. Can be http:// or file:///
	 * @param callback The callback for when a success of fail happens. A null value is ok.
	 * @return A object used to cancel the load
	 */
	public Cancelable load(T view, String uri, Callback callback) {
		return load(view, uri, callback, null, null);
	}
	
	/**
	 * Starts the loading sequence. Should a load call already be in place for the view passed 
	 * in, the old call will be canceled and the new load call will proceed. 
	 * 
	 * @param view A view where the result of the load will be displayed
	 * @param uri A location to the bitmap. Can be http:// or file:///
	 * @param options The BitmapFactory.Options options used to do manipulations to the image
	 * while it's inflating
	 * @return A object used to cancel the load
	 */
	public Cancelable load(T view, String uri, BitmapFactory.Options options) {
		return load(view, uri, null, options, null);
	}
	
	/**
	 * Starts the loading sequence. Should a load call already be in place for the view passed 
	 * in, the old call will be canceled and the new load call will proceed. 
	 * 
	 * @param view A view where the result of the load will be displayed
	 * @param uri A location to the bitmap. Can be http:// or file:///
	 * @param callback The callback for when a success of fail happens. A null value is ok.
	 * @param options The BitmapFactory.Options options used to do manipulations to the image
	 * while it's inflating
	 * @return A object used to cancel the load
	 */
	public Cancelable load(T view, String uri, Callback callback, BitmapFactory.Options options) {
		return load(view, uri, callback, options, null);
	}
	
	/**
	 * Starts the loading sequence. Should a load call already be in place for the view passed 
	 * in, the old call will be canceled and the new load call will proceed. 
	 * 
	 * @param view A view where the result of the load will be displayed
	 * @param uri A location to the bitmap. Can be http:// or file:///
	 * @param callback The callback for when a success of fail happens. A null value is ok.
	 * @param options The BitmapFactory.Options options used to do manipulations to the image
	 * while it's inflating
	 * @param outPadding A Rect from the BitmapFactory.decode method where the padding will be placed
	 * @return A object used to cancel the load
	 */
	public Cancelable load(T view, String uri, Callback callback, 
			BitmapFactory.Options options, Rect outPadding) {
		cancel(view);
		Cancelable q = loader.load(uri, new ViewCallback(view, callback), options, outPadding);
		cancelables.put(view, q);
		return q;
	}
	
	/**
	 * Cancels a load that is associated with the view
	 * 
	 * @param view The view that is associated with the load call from the load method
	 */
	public void cancel(T view) {
		Cancelable q = cancelables.get(view);
		if (q != null) q.cancel();
	}
	
	/**
	 * Cancels all the load calls in this grouping
	 */
	public void cancelAll() {
		Iterator<Entry<T, Cancelable>> it = cancelables.entrySet().iterator();
		while (it.hasNext()) {
			Entry<T, Cancelable> entry = it.next();
			Cancelable c = entry.getValue();
			if (c != null) c.cancel();
		}
	}
	
	/**
	 * A hook point for subclasses to handle successful load calls
	 * 
	 * @param view The view passed in during the load method
	 * @param bitmap The result of the loading
	 * @param source The location of where the Bitmap came from such as memory or disk cache
	 * @param request The params used to make the load request
	 */
	protected void onSuccess(T view, Bitmap bitmap, BitmapSource source, LoadRequest request) {
		
	}
	
	/**
	 * A hook point for subclasses to handle unsuccessful load calls
	 * 
	 * @param view The view passed in during the load method
	 * @param error The error that was thrown/given during the load call
	 * @param source The location of where the error came from such as memory, network IO, etc
	 * @param request The params used to make the load request
	 */
	protected void onError(T view, Throwable error, ErrorSource source, LoadRequest request) {
		
	}
	
	private class ViewCallback implements Callback {
		private WeakReference<T> ref;
		private Callback callback;
		
		private ViewCallback(T view, Callback callback) {
			ref = new WeakReference<T>(view);
			this.callback = callback;
		}
		
		@Override
		public void onSuccess(Bitmap bitmap, BitmapSource source, LoadRequest request) {
			T view = ref.get();
			if (view != null) {
				GroupViewBinder.this.onSuccess(view, bitmap, source, request);
				if (callback != null) callback.onSuccess(bitmap, source, request);
			}
		}

		@Override
		public void onError(Throwable error, ErrorSource source, LoadRequest request) {
			T view = ref.get();
			if (view != null) {
				GroupViewBinder.this.onError(view,  error, source, request);
				if (callback != null) callback.onError(error, source, request);
			}
		}
	}
}