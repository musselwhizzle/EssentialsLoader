package com.therealjoshua.essentials.bitmaploader.processors;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class MatrixProcessor implements BitmapProcessor {
	private Matrix matrix;
	
	public MatrixProcessor(Matrix matrix) {
		this.matrix = matrix;
	}
	
	@Override
	public String getId() {
		return "m"+matrix.toString();
	}

	@Override
	public Bitmap process(Bitmap in) {
		Bitmap result = Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), matrix, false);
		if (result != in) in.recycle();
		return result;
	}
	
}