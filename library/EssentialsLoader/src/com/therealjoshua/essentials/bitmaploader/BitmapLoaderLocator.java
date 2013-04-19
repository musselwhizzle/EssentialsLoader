package com.therealjoshua.essentials.bitmaploader;

import java.util.HashMap;

import android.graphics.Bitmap;

import com.therealjoshua.essentials.bitmaploader.cache.Cache;

public class BitmapLoaderLocator {
	
	public static final String DEFAULT_BITMAP_LOADER = "defaultBitmapLoader";
	public static final String DEFAULT_MEMORY_CACHE = "defaultMemoryCache";
	public static final String DEFAULT_DISK_CACHE = "defaultDiskCache";
	
	
	private static HashMap<String, BitmapLoader> loaders = new HashMap<String, BitmapLoader>();
	private static HashMap<String, Cache<String, Bitmap>> caches = new HashMap<String, Cache<String,Bitmap>>();
	
	public static void putBitmapLoader(BitmapLoader loader) {
		loaders.put(DEFAULT_BITMAP_LOADER, loader);
	}
	
	public static BitmapLoader getBitmapLoader() {
		return loaders.get(DEFAULT_BITMAP_LOADER);
	}
	
	public static void putMemoryCache(Cache<String, Bitmap> cache) {
		caches.put(DEFAULT_MEMORY_CACHE, cache);
	}
	
	public static void putDiskCache(Cache<String, Bitmap> cache) {
		caches.put(DEFAULT_DISK_CACHE, cache);
	}
	
	public static Cache<String, Bitmap> getMemoryCache() {
		return caches.get(DEFAULT_MEMORY_CACHE);
	}
	
	public static Cache<String, Bitmap> getDiskCache() {
		return caches.get(DEFAULT_DISK_CACHE);
	}
	
	
	public static void putBitmapLoader(String name, BitmapLoader loader) {
		loaders.put(name, loader);
	}
	
	public static BitmapLoader getBitmapLoader(String name) {
		return loaders.get(name);
	}
	
	public static BitmapLoader removeBitmapLoader(String name) {
		return loaders.remove(name);
	}
	
	public static void putCache(String name, Cache<String, Bitmap> cache) {
		caches.put(name, cache);
	}
	
	public static Cache<String, Bitmap> getCache(String name) {
		return caches.get(name);
	}
	
	public static Cache<String, Bitmap> removeCache(String name) {
		return caches.remove(name);
	}
	
}