package com.therealjoshua.essentials.bitmaploader.processors;

import android.graphics.Bitmap;

public class ResizeProcessor implements BitmapProcessor {
	private int width, height;
	
	public ResizeProcessor(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	public String getId() {
		return "r" + width +"x"+ height;
	}

	@Override
	public Bitmap process(Bitmap in) {
		Bitmap out = Bitmap.createScaledBitmap(in, width, height, false);
		if (out != in) in.recycle();
		return out;
	}
}