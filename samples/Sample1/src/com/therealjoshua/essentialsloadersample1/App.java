package com.therealjoshua.essentialsloadersample1;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.http.HttpResponseCache;
import android.os.Build;

import com.therealjoshua.essentials.logger.Log;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoaderLocator;
import com.therealjoshua.essentials.bitmaploader.cache.BitmapLruCache;
import com.therealjoshua.essentials.bitmaploader.cache.DiskLruCacheFascade;

public class App extends Application {
	
	long DEFAULT_CACHE_SIZE = 16 * 1024 * 1024; // 16 MB
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		installHttpCache();
		initBitmapLoader();
	}
	
	@SuppressLint("NewApi")
	private void installHttpCache() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			try {
				HttpResponseCache.install(new File(getCacheDir(), "HttpCache"), DEFAULT_CACHE_SIZE);
			} catch (Exception e) {
				Log.w("App", "Could not create http cache. Permissions???");
			}
		}
	}
	
	private void initBitmapLoader() {
		// create the memory cache to use 1/4 of the total available memory
		BitmapLruCache memCache = new BitmapLruCache(1/4f);
		
		// create the disk cache
		DiskLruCacheFascade diskCache = new DiskLruCacheFascade(
				this.getCacheDir(), 
				memCache.getLruCache().maxSize());
		
		// create the loader itself passing in our caches
		BitmapLoader loader = new BitmapLoader(this, memCache, diskCache);
		
		// stores the references in a locator for reference globally.
		// there's many ways you could go about this, this one is just 
		// simple and convenient. Feel free to use your own. 
		BitmapLoaderLocator.putBitmapLoader(loader);
		BitmapLoaderLocator.putMemoryCache(memCache);
		BitmapLoaderLocator.putDiskCache(diskCache);
	}

}
