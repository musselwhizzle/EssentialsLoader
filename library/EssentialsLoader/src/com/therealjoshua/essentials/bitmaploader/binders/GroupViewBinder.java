package com.therealjoshua.essentials.bitmaploader.binders;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;

public class GroupViewBinder<T extends View> {
	
	public static interface DrawableFactory {
		public Drawable createDrawable(Context context);
	}
	
	private BitmapLoader loader;
	private WeakHashMap<T, Cancelable> cancelables;
	private WeakHashMap<T, Drawable> loadingDrawables;
	private WeakHashMap<T, Drawable> faultDrawables;
	private int loadingDrawableResId = 0;
	private int faultDrawableResId = 0;
	
	/*
	 * The reason for the drawable factories is views shouldn't be sharing references 
	 * to the drawable. If one changes the the appears all other views referencing that
	 * will also change their appearance when they invalidate. E.g. in the FadeImageViewBinder
	 * when 1 changes the drawable to fade it, the others update as well. 
	 */
	private DrawableFactory loadingDrawableFactory;
	private DrawableFactory faultDrawableFactory;
	
	
	public GroupViewBinder(BitmapLoader loader) {
		this.loader = loader;
		cancelables = new WeakHashMap<T, BitmapLoader.Cancelable>();
		loadingDrawables = new WeakHashMap<T, Drawable>();
		faultDrawables = new WeakHashMap<T, Drawable>();
	}
	
	public Drawable getLoadingDrawable(T view) {
		Drawable d = loadingDrawables.get(view);
		if (d != null) return d;
		if (loadingDrawableFactory != null) {
			d = loadingDrawableFactory.createDrawable(view.getContext());
		}
		else if (loadingDrawableResId > 0) {
			d = view.getContext().getResources().getDrawable(loadingDrawableResId);
		}
		if (d != null) loadingDrawables.put(view, d);
		return d;
	}
	
	public Drawable getFaultDrawable(T view) {
		Drawable d = faultDrawables.get(view);
		if (d != null) return d;
		if (faultDrawableFactory != null) {
			d = faultDrawableFactory.createDrawable(view.getContext());
		}
		else if (faultDrawableResId > 0) {
			d = view.getContext().getResources().getDrawable(faultDrawableResId);
		}
		if (d != null) faultDrawables.put(view, d);
		return d;
	}
	
	public void setLoadingDrawableResId(int resId) {
		this.loadingDrawableResId = resId;
	}
	
	public void setLoadingDrawableFactory(DrawableFactory factory) {
		this.loadingDrawableFactory = factory;
	}
	
	public void setFaultDrawableResId(int resId) {
		this.faultDrawableResId = resId;
	}
	
	public void setFaultDrawableFactory(DrawableFactory factory) {
		this.faultDrawableFactory = factory;
	}
	
	public Cancelable load(T view, String uri) {
		return load(view, uri, null, null);
	}
	
	public Cancelable load(T view, String uri, BitmapFactory.Options options) {
		return load(view, uri, options, null);
	}
	
	public Cancelable load(T view, String uri, BitmapFactory.Options options, Rect outPadding) {
		cancel(view);
		Cancelable q = loader.load(uri, new Callback(view), options, outPadding);
		cancelables.put(view, q);
		return q;
	}
	
	public void cancel(T view) {
		Cancelable q = cancelables.get(view);
		if (q != null) q.cancel();
	}
	
	public void cancelAll() {
		Iterator<Entry<T, Cancelable>> it = cancelables.entrySet().iterator();
		while (it.hasNext()) {
			Entry<T, Cancelable> entry = it.next();
			Cancelable c = entry.getValue();
			if (c != null) c.cancel();
		}
	}
	
	protected void onSuccess(T view, Bitmap bitmap, BitmapSource source, LoadRequest request) {
		
	}
	
	protected void onError(T view, Throwable error, ErrorSource source, LoadRequest request) {
		
	}
	
	private class Callback implements BitmapLoader.Callback {
		private WeakReference<T> ref;
		
		private Callback(T view) {
			ref = new WeakReference<T>(view);
		}
		
		@Override
		public void onSuccess(Bitmap bitmap, BitmapSource source, LoadRequest request) {
			T view = ref.get();
			if (view != null) {
				GroupViewBinder.this.onSuccess(view, bitmap, source, request);
			}
		}

		@Override
		public void onError(Throwable error, ErrorSource source, LoadRequest request) {
			T view = ref.get();
			if (view != null) {
				GroupViewBinder.this.onError(view,  error, source, request);
			}
		}
	}
}