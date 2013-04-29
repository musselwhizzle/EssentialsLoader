package com.therealjoshua.essentialsloadersample1;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

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

import com.therealjoshua.essentials.bitmaploader.BitmapLoader;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorLogFactoryImpl;
import com.therealjoshua.essentials.bitmaploader.Locator;
import com.therealjoshua.essentials.bitmaploader.PortedAsyncTask;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorLog;
import com.therealjoshua.essentials.bitmaploader.BitmapLoader.ErrorSource;
import com.therealjoshua.essentials.bitmaploader.binders.FadeImageViewBinder;
import com.therealjoshua.essentials.bitmaploader.binders.ImageViewBinder;

/*
 * Standard listview using the loader with custom factories
 */
public class Sample7Activity extends Activity {
	
	private ListView listView;
	@SuppressWarnings("unused")
	private MyAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = new ListView(this);
		listView.setDrawSelectorOnTop(true);
		listView.setAdapter(adapter = new MyAdapter(this));
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
			// lets compress the image and save memory
			if (width < REMOTE_IMAGE_WIDTH_PX) {
				inSampleSize = 2;
			}
			height = (int)(width / (16/9f));
			
			// create a custom loader
			BitmapLoader loader = new BitmapLoader(context, Locator.getMemoryCache(), Locator.getDiskCache());
			
			// exectuor on a custom executor
			loader.setExecuteOnExecutor(PortedAsyncTask.DUAL_THREAD_EXECUTOR);
			
			// get the standard error factory and set our own time to live on the errors
			ErrorLogFactoryImpl errorFac = (ErrorLogFactoryImpl)loader.getErrorLogFactory();
			errorFac.setTimeToLive(1000*2*60); // set to 2 minutes
			
			// set a custom URLConnection properties
			loader.setConnectionFactory(new BitmapLoader.ConnectionFactory() {
				@Override
				public URLConnection getConnection(String uri) throws IOException {
					URL u = new URL(uri);
					URLConnection conn = u.openConnection();
					conn.setUseCaches(false);
					conn.setReadTimeout(10*1000); // 10 sec
					conn.setConnectTimeout(10*1000);
					return conn;
				}
			});
			
			
			// create a fade binder which is cross fade in the loaded image with 
			// what's currently in the image view
			binder = new FadeImageViewBinder(context, loader);
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
			binder.load(imageView, url, opts);
			return imageView;
		}
		
	}
	
}