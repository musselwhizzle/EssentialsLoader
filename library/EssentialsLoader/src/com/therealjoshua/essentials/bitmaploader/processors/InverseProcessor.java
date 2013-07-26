package com.therealjoshua.essentials.bitmaploader.processors;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class InverseProcessor implements BitmapProcessor {
	
	public InverseProcessor() {
		
	}
	
	@Override
	public String getId() {
		return "i";
	}

	@Override
	public Bitmap process(Bitmap in) {
		Matrix matrix = new Matrix();
		matrix.setScale(-1, 1);
		matrix.postTranslate(in.getWidth(), 0);
		Bitmap result = Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), matrix, false);
		if (result != in) in.recycle();
		return result;
	}
	
}