package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoaderLocator;
import com.therealjoshua.essentials.bitmaploader.binders.FadeImageViewBinder;
import com.therealjoshua.essentials.bitmaploader.binders.ImageViewBinder;

public class Sample1Activity extends Activity {
	
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = new ListView(this);
		listView.setAdapter(new MyAdapter(this));
		setContentView(listView);
	}
	
	private class MyAdapter extends BaseAdapter {
		private Context context;
		private int width;
		private int height;
		private ImageViewBinder binder;
		private final int REMOTE_IMAGE_WIDTH_PX = 640;
		private int inSampleSize = 1;
		
		private MyAdapter(Context context) {
			this.context = context;
			width = context.getResources().getDisplayMetrics().widthPixels;
			
			// if the screen width is less than the loaded image width
			// less compress the image and save memory
			if (width < REMOTE_IMAGE_WIDTH_PX) {
				inSampleSize = 2;
			}
			height = (int)(width / (16/9f));
			
			// create a fade binder which is cross fade in the loaded image with 
			// what's currently in the image view
			binder = new FadeImageViewBinder(BitmapLoaderLocator.getBitmapLoader());
			binder.setLoadingDrawableResId(R.drawable.gray_k02);
			binder.setFaultDrawableResId(R.drawable.error_k02);
		}
		
		@Override
		public int getCount() {
			return Images.ANIMAL_IMAGES.length;
		}

		@Override
		public String getItem(int position) {
			return Images.ANIMAL_IMAGES[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = null;
			if (convertView != null) {
				imageView = (ImageView)convertView;
			} else {
				imageView = new ImageView(context);
				AbsListView.LayoutParams p = new AbsListView.LayoutParams(width, height);
				imageView.setLayoutParams(p);
			}
			String url = getItem(position);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = inSampleSize;
			binder.load(imageView, url, opts);
			return imageView;
		}
		
	}
	
}