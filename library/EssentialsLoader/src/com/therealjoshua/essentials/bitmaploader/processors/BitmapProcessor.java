package com.therealjoshua.essentials.bitmaploader.processors;

import android.graphics.Bitmap;

public interface BitmapProcessor {
	public String getId();
	public Bitmap process(Bitmap in);
}