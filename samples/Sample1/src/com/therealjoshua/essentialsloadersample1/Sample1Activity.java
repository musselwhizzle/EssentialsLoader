package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.therealjoshua.essentials.bitmaploader.binders.FadeImageViewBinder;
import com.therealjoshua.essentials.bitmaploader.binders.ImageViewBinder;

/*
 * Standard listview using the loader
 */
public class Sample1Activity extends Activity {
	
	private ListView listView;
	@SuppressWarnings("unused")
	private MyAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = new ListView(this);
		listView.setDrawSelectorOnTop(true);
		listView.setAdapter(adapter = new MyAdapter(this));
		
		// this is rather unnecessary, but if you want to listen
		// to when the renderer gets removed the listview you can
		// and then manually stop the load. I say it's unnecessary
		// because calling a new load automatically stops the old load
		/*
		listView.setRecyclerListener(new AbsListView.RecyclerListener() {
			@Override
			public void onMovedToScrapHeap(View view) {
				adapter.binder.cancel((ImageView)view);
			}
		});*/
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
			height = (int)(width / (16/9f));
			
			// if the screen width is less than the loaded image width
			// lets compress the image and save memory
			if (width < REMOTE_IMAGE_WIDTH_PX) {
				inSampleSize = 2;
			}
			
			// create a fade binder which is cross fade in the loaded image with 
			// what's currently in the image view
			binder = new FadeImageViewBinder(context);
			binder.setLoadingResource(R.drawable.gray_k02);
			binder.setFaultResource(R.drawable.error_k02);
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
			binder.build(imageView, url).setBitmapFactoryOptions(opts).load();
			return imageView;
		}
		
	}
	
}