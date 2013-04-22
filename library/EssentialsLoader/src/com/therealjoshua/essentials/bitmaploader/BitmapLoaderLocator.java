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