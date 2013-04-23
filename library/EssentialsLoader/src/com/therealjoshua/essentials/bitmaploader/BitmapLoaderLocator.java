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

package com.therealjoshua.essentials.bitmaploader;

import java.util.HashMap;

import android.graphics.Bitmap;

import com.therealjoshua.essentials.bitmaploader.cache.Cache;

/**
 * This class is used to store global instances of the BitmapLoader (and caches, maybe refactor???) 
 * so that it can be shared across the application
 * 
 * @author Joshua
 *
 */
public class BitmapLoaderLocator {
	
	public static final String DEFAULT_BITMAP_LOADER = "defaultBitmapLoader";
	public static final String DEFAULT_MEMORY_CACHE = "defaultMemoryCache";
	public static final String DEFAULT_DISK_CACHE = "defaultDiskCache";
	
	
	private static HashMap<String, BitmapLoader> loaders = new HashMap<String, BitmapLoader>();
	private static HashMap<String, Cache<String, Bitmap>> caches = new HashMap<String, Cache<String,Bitmap>>();
	
	/**
	 * Store the BitmapLoader as the default
	 * 
	 * @param loader
	 */
	public static void putBitmapLoader(BitmapLoader loader) {
		loaders.put(DEFAULT_BITMAP_LOADER, loader);
	}
	
	/**
	 * Gets the default BitmapLoader that was set
	 * 
	 * @return
	 */
	public static BitmapLoader getBitmapLoader() {
		return loaders.get(DEFAULT_BITMAP_LOADER);
	}
	
	/**
	 * Stores the default memory cache
	 * 
	 * @param cache
	 */
	public static void putMemoryCache(Cache<String, Bitmap> cache) {
		caches.put(DEFAULT_MEMORY_CACHE, cache);
	}
	
	/**
	 * Stores the default disk cache
	 * 
	 * @param cache
	 */
	public static void putDiskCache(Cache<String, Bitmap> cache) {
		caches.put(DEFAULT_DISK_CACHE, cache);
	}
	
	/**
	 * Gets the default memory cache that was set
	 * 
	 * @return
	 */
	public static Cache<String, Bitmap> getMemoryCache() {
		return caches.get(DEFAULT_MEMORY_CACHE);
	}
	
	/**
	 * Gets the default disk cache that was set
	 * 
	 * @return
	 */
	public static Cache<String, Bitmap> getDiskCache() {
		return caches.get(DEFAULT_DISK_CACHE);
	}
	
	/**
	 * Stores a BitmapLoader by a tag name. You can retreive this object later
	 * but using the get method passing in this same tag name
	 * 
	 * @param name
	 * @param loader
	 */
	public static void putBitmapLoader(String name, BitmapLoader loader) {
		loaders.put(name, loader);
	}
	
	/**
	 * Gets a BitmapLoader instance that was set using putBitmapLoader(String name, BitmapLoader loader)
	 * 
	 * @param name
	 * @return
	 */
	public static BitmapLoader getBitmapLoader(String name) {
		return loaders.get(name);
	}
	
	/**
	 * Removes the stored BitmapLoader instance by tag name
	 * 
	 * @param name
	 * @return
	 */
	public static BitmapLoader removeBitmapLoader(String name) {
		return loaders.remove(name);
	}
	
	/**
	 * Stores a Cache by a tag name. You can retreive this object later
	 * but using the get method passing in this same tag name
	 * 
	 * @param name
	 * @param loader
	 */
	public static void putCache(String name, Cache<String, Bitmap> cache) {
		caches.put(name, cache);
	}
	
	/**
	 * Gets a Cache instance that was set using putCache(String name, Cache<String, Bitmap> cache)
	 * 
	 * @param name
	 * @return
	 */
	public static Cache<String, Bitmap> getCache(String name) {
		return caches.get(name);
	}
	
	/**
	 * Removes the stored Cache instance by tag name
	 * 
	 * @param name
	 * @return
	 */
	public static Cache<String, Bitmap> removeCache(String name) {
		return caches.remove(name);
	}
	
}