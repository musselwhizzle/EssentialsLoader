package com.therealjoshua.essentials.bitmaploader.processors;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RotateProcessor implements BitmapProcessor {
	private int rotateDegrees;
	
	public RotateProcessor(int rotateDegrees) {
		this.rotateDegrees = rotateDegrees;
	}
	
	public int getDegrees() {
		return rotateDegrees;
	}
	
	@Override
	public String getId() {
		return "ro" + rotateDegrees;
	}

	@Override
	public Bitmap process(Bitmap in) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegrees, in.getWidth()/2, in.getHeight()/2);
		Bitmap result = Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), matrix, false);
		if (result != in) in.recycle();
		return result;
	}

}
