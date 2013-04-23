package com.therealjoshua.essentialsloadersample1;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class StarBitmapDrawable extends Drawable {
	
	private Paint paint;
	private BitmapShader shader;
	private Bitmap bitmap;
	private Path path;
	
	private static final int STAR_OPP_ANGLE = 72;
//	private static final int STAR_ANGLE = 36;
	private static final int STAR_ANGLE_HALF = 18;
	
	private int paddingLeft = 0;
	private int paddingRight = 0;
	private int paddingTop = 0;
	private int paddingBottom = 0;
	
	
	public StarBitmapDrawable(Bitmap bitmap) {
		super();
		this.bitmap = bitmap;
		paint = new Paint();
		paint.setAntiAlias(true);
		shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		path = new Path();
//		paint.setStyle(Paint.Style.STROKE);
	}
	
	public void setPadding(int left, int top, int right, int bottom) {
		paddingLeft = left;
		paddingTop = top;
		paddingRight = right;
		paddingBottom = bottom;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		
		int minDim = Math.min(bounds.width() - paddingLeft - paddingRight, 
				bounds.height() - paddingTop - paddingBottom);
		
		// b = | 
		// a = _
		// hyp = \
		
		// bigHypot = height / cos(18) 
		double bigHypot = (minDim / Math.cos(Math.toRadians(STAR_ANGLE_HALF)));
		double bigB = minDim;
		double bigA = Math.tan(Math.toRadians(18)) * bigB;
		
		// lengths of the little triangles.
		// littleC = littleC + littleC + littleA + littleA
		// cos(72)*C = A
		double littleHypot = bigHypot / (2 + Math.cos(Math.toRadians(STAR_OPP_ANGLE)) + Math.cos(Math.toRadians(STAR_OPP_ANGLE)));
		double littleA = Math.cos(Math.toRadians(STAR_OPP_ANGLE)) * littleHypot;
		double littleB = Math.sin(Math.toRadians(STAR_OPP_ANGLE)) * littleHypot;
		
		int topXPoint = (bounds.width() - paddingLeft - paddingRight)/2;
		int topYPoint = paddingTop;
		
		// start at the top point
		path.moveTo(topXPoint, topYPoint);
		
		// top to bottom right point
		path.lineTo((int)(topXPoint + bigA), (int)(topYPoint + bigB));
		
		// bottom right to middle left point 
		path.lineTo((int)(topXPoint - littleA - littleB), (int)(topYPoint + littleB));
		
		// middle left to middle right point
		path.lineTo((int)(topXPoint + littleA + littleB), (int)(topYPoint + littleB));
		
//		// middle right to bottom left point
		path.lineTo((int)(topXPoint - bigA), (int)(topYPoint + bigB));
		
//		// bottom left to top point
		path.lineTo(topXPoint, topYPoint);
		path.close();
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	@Override
	public void setAlpha(int alpha) {
		paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	
	@Override
	public int getIntrinsicHeight() {
		return bitmap.getHeight();
	}
	
	@Override
	public int getIntrinsicWidth() {
		return bitmap.getWidth();
	}

}
