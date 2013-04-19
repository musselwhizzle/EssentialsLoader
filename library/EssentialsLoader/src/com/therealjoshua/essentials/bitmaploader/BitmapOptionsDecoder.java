package com.therealjoshua.essentials.bitmaploader;

import android.graphics.BitmapFactory;
import android.graphics.Rect;

public interface BitmapOptionsDecoder {
	
	public void setOptions(BitmapFactory.Options options, Rect outPadding);
	
}