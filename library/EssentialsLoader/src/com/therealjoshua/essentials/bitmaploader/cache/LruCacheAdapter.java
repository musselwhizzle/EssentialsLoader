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