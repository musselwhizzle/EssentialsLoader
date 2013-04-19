package com.therealjoshua.essentials.bitmaploader.cache;

public interface Cache<K, V> {
	
	public V get(K key);
	public void put(K key, V value);
	public void clear();
	public boolean hasObject(K key);
	
}