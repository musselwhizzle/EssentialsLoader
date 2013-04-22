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