package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoaderLocator;
import com.therealjoshua.essentials.bitmaploader.binders.FadeImageViewBinder;
import com.therealjoshua.essentials.bitmaploader.binders.ImageViewBinder;

/*
 * Standard listview using the loader
 */
public class Sample4Activity extends Activity {
	
	private GridView gridView;
	private int size = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gridView = new GridView(this);
		gridView.setNumColumns(3);
		gridView.setColumnWidth(size);
		size = getResources().getDisplayMetrics().widthPixels/3;
		gridView.setAdapter(new MyAdapter(this));
		setContentView(gridView);
	}
	
	private class MyAdapter extends BaseAdapter {
		private Context context;
		private ImageViewBinder binder;
		private int inSampleSize = 4;
		
		private MyAdapter(Context context) {
			this.context = context;
			
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
				AbsListView.LayoutParams p = new AbsListView.LayoutParams(size, size);
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