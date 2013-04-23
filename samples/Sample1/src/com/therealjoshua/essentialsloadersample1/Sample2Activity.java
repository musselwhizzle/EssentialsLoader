package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.BitmapSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.Cancelable;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.LoadRequest;
import com.therealjoshua.essentials.bitmaploader.BitmapLoaderLocator;
import com.therealjoshua.essentials.bitmaploader.binders.SingleViewBinder;

/*
 * A view pager using the bitmap loader
 */
public class Sample2Activity extends Activity {
	
	private ViewPager viewPager;
	private BitmapLoader loader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader = BitmapLoaderLocator.getBitmapLoader();
		viewPager = new ViewPager(this);
		viewPager.setAdapter(new MyAdapter(this));
		setContentView(viewPager);
	}
	
	private class MyAdapter extends PagerAdapter {
		private Context context;
		private final int REMOTE_IMAGE_WIDTH_PX = 640;
		private int inSampleSize = 1;
		private int width;
		private int height;
		
		private MyAdapter(Context context) {
			this.context = context;
			width = context.getResources().getDisplayMetrics().widthPixels;
			
			// if the screen width is less than the loaded image width
			// less compress the image and save memory
			if (width < REMOTE_IMAGE_WIDTH_PX) {
				inSampleSize = 2;
			}
			height = (int)(width / (16/9f));
		}
		
		@Override
		public int getCount() {
			return Images.ANIMAL_IMAGES.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return view == arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object view) {
			MySpinnerBinder binder = (MySpinnerBinder)((View)view).getTag();
			if (binder != null) binder.cancel(); // stop any loading.
			((ViewPager) container).removeView((View) view);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			RelativeLayout layout = new RelativeLayout(context);
			layout.setId(1);
			
			ImageView imageView = new ImageView(context);
			imageView.setId(2);
			LayoutParams p = new LayoutParams(width, height);
			p.addRule(RelativeLayout.CENTER_IN_PARENT);
			layout.addView(imageView, p);
			
			ProgressBar spinner = new ProgressBar(context);
			p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			p.addRule(RelativeLayout.CENTER_IN_PARENT);
			layout.addView(spinner, p);
			
			TextView textView = new TextView(context);
			textView.setId(4);
			textView.setText("Image #" + (position +1));
			textView.setTextColor(0xFF000000);
			textView.setTextSize(30);
			p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			p.addRule(RelativeLayout.BELOW, imageView.getId());
			p.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layout.addView(textView, p);
			
			MySpinnerBinder binder = new MySpinnerBinder(loader, spinner, imageView);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			binder.setFaultResource(context, R.drawable.error_k02);
			opts.inSampleSize = inSampleSize;
			binder.load(Images.ANIMAL_IMAGES[position], opts);
			layout.setTag(binder);
			
			container.addView(layout);
			return layout;
		}
	}
	
	private class MySpinnerBinder extends SingleViewBinder {
		
		private View loadingView;
		private ImageView targetView;
		
		public MySpinnerBinder(BitmapLoader loader, View loadingView, ImageView target) {
			super(loader);
			this.loadingView = loadingView;
			this.targetView = target;
		}
		
		@Override
		public Cancelable load(String url, BitmapFactory.Options options, Rect outPadding) {
			loadingView.setVisibility(View.VISIBLE);
			return super.load(url, options, outPadding);
		}
		
		@Override
		public void onSuccess(Bitmap bitmap, BitmapSource source, LoadRequest request) {
			loadingView.setVisibility(View.GONE);
			targetView.setImageBitmap(bitmap);
			super.onSuccess(bitmap, source, request);
		}
		
		@Override
		public void onError(Throwable error, ErrorSource source, LoadRequest request) {
			loadingView.setVisibility(View.GONE);
			targetView.setImageDrawable(getFaultDrawable());
			super.onError(error, source, request);
		}
		
	}
}