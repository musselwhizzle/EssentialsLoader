package com.therealjoshua.essentials.bitmaploader.processors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class SaturationProcessor implements BitmapProcessor {

	private ColorMatrix colorMatrix;
	private Paint paint;
	private int saturation;
	
	public SaturationProcessor(int saturation) {
		colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(saturation);
		paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		this.saturation = saturation;
	}
	
	public int getSaturation() {
		return saturation;
	}
	
	@Override
	public String getId() {
		return "saturation"+saturation;
	}

	@Override
	public Bitmap process(Bitmap in) {
		Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(out);
		canvas.drawBitmap(in, 0, 0, paint);
		in.recycle();
		
		return out;
	}
	
}