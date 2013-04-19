package com.therealjoshua.essentials.bitmaploader.cache;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCacheAdapter{

	private LruCache<String, Bitmap> impl;
	
	public BitmapLruCache(float percentOfAvailableMemory) {
		super(new MyCache((int)(Runtime.getRuntime().maxMemory() * percentOfAvailableMemory)));
		impl = getLruCache();
	}
	
	public BitmapLruCache(int maxSize) {
		super(new MyCache(maxSize));
		impl = getLruCache();
	}
	
	public int maxSize() {
		return impl.maxSize();
	}
	
	private static class MyCache extends LruCache<String, Bitmap> {
		public MyCache(int maxSize) {
			super(maxSize);
		}
		
		@Override
		protected int sizeOf(String key, Bitmap value) {
			if (value == null) return 0;
			return getBitmapSize(value);
		}
		
		/**
	     * Get the size in bytes of a bitmap.
	     * @param bitmap
	     * @return size in bytes
	     */
	    @SuppressLint("NewApi")
	    private int getBitmapSize(Bitmap bitmap) {
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
	            return bitmap.getByteCount();
	        }
	        // Pre HC-MR1
	        return bitmap.getRowBytes() * bitmap.getHeight();
	    }
	}
	
}