/*
 * Copyrigth (c) 2012 Joshua Musselwhite
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

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruCacheAdapter implements Cache<String, Bitmap> {
	
	private LruCache<String, Bitmap> lruCache;
	
	public LruCacheAdapter(LruCache<String, Bitmap> lruCache) {
		this.lruCache = lruCache;
	}
	
	public LruCache<String, Bitmap> getLruCache() {
		return lruCache;
	}
	
	@Override
	public Bitmap get(String id) {
		return lruCache.get(id);
	}
	
	@Override
	public void put(String id, Bitmap bitmap) {
		if (id == null || bitmap == null) return;
		lruCache.put(id, bitmap);
	}
	
	@Override
	public void clear() {
		lruCache.evictAll();
	}
	
	@Override
	public boolean hasObject(String id) {
		if (id == null) return false;
		Bitmap bm = lruCache.get(id);
		return (bm != null && !bm.isRecycled());
	}
	
}