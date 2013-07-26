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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Callback;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;
import com.therealjoshua.essentials.bitmaploader.processors.BitmapProcessor;
import com.therealjoshua.essentials.bitmaploader.processors.InverseProcessor;
import com.therealjoshua.essentials.bitmaploader.processors.ResizeProcessor;
import com.therealjoshua.essentials.bitmaploader.processors.RotateProcessor;
import com.therealjoshua.essentials.bitmaploader.processors.SaturationProcessor;
import com.therealjoshua.essentials.bitmaploader.Locator;

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
	private HashMap<String, BitmapProcessor> processorsPool = new HashMap<String, BitmapProcessor>();
	
	/**
	 * Constructor which uses the default BitmapLoader object in the Locator. If you need a specific
	 * BitmapLoader use the GroupViewBinder(Context context, BitmapLoader loader) method
	 * 
	 * @param context A general context;
	 */
	public GroupViewBinder(Context context) {
		this(context, Locator.getBitmapLoader());
	}
	
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
	
	public Cancelable load(T view, String uri) {
		return build(view, uri).load();
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
	
	public static class ViewBinderLoadRequest<T extends View> extends BitmapLoader.LoadRequest {
		private GroupViewBinder<T> binder;
		private WeakReference<T> view;
		private ViewBinderLoadRequest(GroupViewBinder<T> binder, BitmapLoader loader) {
			super(loader);
			this.binder = binder;
		}
		
		@Override
		public Cancelable load() {
			return binder.load(view.get(), this);
		}
		
		@SuppressWarnings("rawtypes")
		public ViewBinderLoadRequest resizeTo(int width, int height) {
			if (binder.processorsPool.containsKey("resize")) {
				ResizeProcessor rp = (ResizeProcessor)binder.processorsPool.get("resize");
				if (rp.getWidth() == width && rp.getHeight() == height) {
					addBitmapProcessor(rp);
				} else {
					ResizeProcessor p = new ResizeProcessor(width, height);
					binder.processorsPool.put("resize", p);
					addBitmapProcessor(p);
				}
			} else {
				ResizeProcessor p = new ResizeProcessor(width, height);
				binder.processorsPool.put("resize", p);
				addBitmapProcessor(p);
			}
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		public ViewBinderLoadRequest saturate(int saturation) {
			if (binder.processorsPool.containsKey("saturate")) {
				SaturationProcessor sp = (SaturationProcessor)binder.processorsPool.get("saturate");
				if (sp.getSaturation() == saturation) {
					addBitmapProcessor(sp);
				} else {
					SaturationProcessor p = new SaturationProcessor(saturation);
					binder.processorsPool.put("saturate", p);
					addBitmapProcessor(p);
				}
			} else {
				SaturationProcessor p = new SaturationProcessor(saturation);
				binder.processorsPool.put("saturate", p);
				addBitmapProcessor(p);
			}
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		public ViewBinderLoadRequest rotate(int degrees) {
			if (binder.processorsPool.containsKey("rotate")) {
				RotateProcessor rp = (RotateProcessor)binder.processorsPool.get("rotate");
				if (rp.getDegrees() == degrees) {
					addBitmapProcessor(rp);
				} else {
					RotateProcessor p = new RotateProcessor(degrees);
					binder.processorsPool.put("rotate", p);
					addBitmapProcessor(p);
				}
			} else {
				RotateProcessor p = new RotateProcessor(degrees);
				binder.processorsPool.put("rotate", p);
				addBitmapProcessor(p);
			}
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		public ViewBinderLoadRequest inverse() {
			if (binder.processorsPool.containsKey("inverse")) {
				InverseProcessor ip = (InverseProcessor)binder.processorsPool.get("inverse");
				addBitmapProcessor(ip);
			} else {
				InverseProcessor p = new InverseProcessor();
				binder.processorsPool.put("inverse", p);
				addBitmapProcessor(p);
			}
			return this;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ViewBinderLoadRequest build(T view, String uri) {
		ViewBinderLoadRequest<T> r = new ViewBinderLoadRequest(this, loader);
		r.setUri(uri);
		r.view = new WeakReference(view);
		return r;
	}
	
	public Cancelable load(T view, BitmapLoader.LoadRequest request) {
		cancel(view);
		request.setCallback(new ViewCallback(view, request.getCallback()));
		Cancelable q = loader.load(request);
		cancelables.put(view, q);
		return q;
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