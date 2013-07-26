/*
 * Copyright (c) 2012 Joshua Musselwhite
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.therealjoshua.essentials.bitmaploader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.therealjoshua.essentials.bitmaploader.cache.Cache;
import com.therealjoshua.essentials.bitmaploader.processors.BitmapProcessor;

/**
 * This class is responsible for the actual loading process. Generally, you will not 
 * interact with it directly but instead use a ViewBinder which aggregates an instance
 * of the BitmapLoader class. The APIs of this of BitmapLoader are simple and should be 
 * familiar. You can create an instance (which you may want to access globally) but passing
 * in your 2 caches: A memory cache and a disk cache. The caches are aggregated to the
 * BitmapLoader to allow for multiple instances which can use the same cache. It's possible, 
 * you may want a BitmapLoader to use the same memory cache but a seperate disk cache, but 
 * in general you probably will not worry about this. 
 * 
 * @author Joshua Musselwhite
 *
 */
public class BitmapLoader {
	
	/**
	 * An ErrorLog is created when an IO happens from loading an image from an external source. 
	 * The object is used to validate if the error is still valid so that the load request should 
	 * abort immediately. If the object is not valid any longer, it will be removed from the list
	 * and the load request will continue. If you need specific validation logic, create a custom 
	 * implementation of the ErrorLogFactory and ErrorLog.
	 * 
	 * @author Joshua
	 *
	 */
	public static interface ErrorLog {
		
		/**
		 * The error that happened to cause the fault
		 * @return
		 */
		public Throwable getError();
		
		/**
		 * Validates to make sure this error is still valid. Implementations can perform any check it would like
		 * such as time passed, or specific urls. 
		 * 
		 * @return true if the error is still considered valid
		 */
		public boolean isValid();
	}
	
	/**
	 * This is used to create instances of an ErrorLog object. If you have specific needs
	 * for validating if an error is valid, create an implementation of this class and a
	 * ErrorLog and add your own custom validation log. The default validates based upon time
	 * and for 60 seconds
	 *
	 */
	public static interface ErrorLogFactory {
		 public ErrorLog createErrorLog(String url, Throwable exception, ErrorSource errorSource);
	}
	
	/**
	 * The callback for when an image is loaded or failed to load. 
	 *
	 */
	public static interface Callback {
		
		/**
		 * Called when a successful load of an image happens. Note, if you call a load passing in 
		 * BitmapFactory.Option.inJustDecodeBounds = true, the bitmap here will be null.
		 * 
		 * @param bitmap the returned image from the call
		 * @param source the location of where the image came from. It could be from memory cache, 
		 * 		disk cache, or external (from the web).
		 * @param request The parameters used in the load() call to request the image
		 */
		public void onSuccess(Bitmap bitmap, BitmapSource source, LoadRequest request);
		
		/**
		 * Called when a fault happens trying to load the image. This could be from the 
		 * Error cache or from the web. 
		 * 
		 * @param url The url of the image call
		 * @param error The exception that was originally about the error
		 * @param source Where the fault occurred in the loading sequence. 
		 */
		public void onError(Throwable error, ErrorSource source, LoadRequest request);
	}
	
	/**
	 * The ConnectionFactory creates a URLConnection from the given URL.
	 * The purpose of the factory is to allow calling clients a chance to
	 * set custom properties on the URLConnection object like "no-cache" 
	 * and custom timeouts. Custom URLStreamHandlers can also be set here
	 * if the client decides to do so. 
	 */
	public static interface ConnectionFactory {
		/**
		 * Get a URLConnection object from the given uri.
		 * 
		 * @param uri
		 * @return
		 * @throws IOException
		 */
		public URLConnection getConnection(String uri) throws IOException;
	}
	
	/**
	 * Location of where the bitmap came from
	 */
	public static enum BitmapSource {
		MEMORY, 
		DISK, 
		EXTERNAL
	}
	
	/**
	 * Location of where the error came from
	 */
	public static enum ErrorSource {
		ARGUMENT,
		ERROR_CACHE,
		NO_NETWORK, 
		EXTERNAL
	}
	
	/**
	 * An interface used to to allow a canceling of a load
	 */
	public static interface Cancelable {
		public void cancel();
	}
	
	/**
	 * This class is a transfer object of the parameters used to make the 
	 * load request. It configures how the image will be loaded
	 *
	 */
	public static class LoadRequest {
		public LoadRequest(BitmapLoader loader) {
			this.loader = loader;
		}
		private BitmapLoader loader;
		private String uri;
		private BitmapFactory.Options options;
		private Rect outPadding;
		private Callback callback;
		
		
		public BitmapFactory.Options getOptions() { return options; }
		public Rect getOutPadding() { return outPadding; }
		
		private ArrayList<BitmapProcessor> processes;
		
		/**
		 * Gets the URI that was set for the request
		 * @return
		 */
		public String getUri() { 
			return uri; 
		}
		
		/**
		 * Set the URI for the image to load
		 * @param uri
		 * @return An instance of this to daisy chain
		 */
		public LoadRequest setUri(String uri) {
			this.uri = uri;
			return this;
		}
		
		/**
		 * Gets the callback that was set. 
		 * 
		 * @return Callback
		 */
		public Callback getCallback() {
			return callback;
		}
		
		/**
		 * The callback used for notification about the state of the loading process
		 * 
		 * @param callback
		 * @return An instance of this to daisy chain
		 */
		public LoadRequest setCallback(Callback callback) {
			this.callback = callback;
			return this;
		}
		
		/**
		 * Sets the BitmapFactory.Options for when decompressing the image
		 * 
		 * @param options standard BitmapFactory.Options
		 * @return An instance of this to daisy chain
		 */
		public LoadRequest setBitmapFactoryOptions(BitmapFactory.Options options) {
			this.options = options;
			return this;
		}
		
		/**
		 * Sets the OutPadding Rect used in in the BitmapFactory.decode method
		 * 
		 * @param outPadding Standard Rect
		 * @return An instance of this to daisy chain
		 */
		public LoadRequest setOutPadding(Rect outPadding) {
			this.outPadding = outPadding;
			return this;
		}
		
		/**
		 * Adds an image process allowing a client to make modifications to the image 
		 * in a background thread before it is returned. The manipulations are cacheable. 
		 * 
		 * @param processor 
		 * @return An instance of this to daisy chain
		 */
		public LoadRequest addBitmapProcessor(BitmapProcessor processor) {
			if (processor == null) return this;
			if (processes == null) processes = new ArrayList<BitmapProcessor>();
			processes.add(processor);
			return this;
		}
		
		/**
		 * Starts the loading process
		 * @return The Cancelable task that is loading the image
		 */
		public Cancelable load() {
			return loader.load(this);
		}
		
		/**
		 * Generates a savable name for the image loaded via the url and other options
		 * The same url that has different options will produce different keys. 
		 * 
		 * @param url
		 * @param options
		 * @return
		 */
		private String generateKey() {
			// http://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l
			// http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
			// doesn't seem a need to use MD5, so just hash it.
			StringBuilder b = new StringBuilder();
			
			b.append( uri.hashCode() );
			
			if (options == null) b.append("_1");
			else {
				b.append("_");
				b.append( options.inSampleSize );
			}
			
			if (processes != null) {
				for (BitmapProcessor p : processes) {
					b.append("_");
					b.append(p.getId());
				}
			}
		
			return Integer.toHexString(b.toString().hashCode());
		}
	}
	
	public LoadRequest build(String uri) {
		LoadRequest r = new LoadRequest(this);
		r.uri = uri;
		return r;
	}
	
	private static final String TAG = BitmapLoader.class.getSimpleName();
	// Are these caches thread safe? 
	private Cache<String, Bitmap> memCache;
	private Cache<String, Bitmap> diskCache;
	private LruCache<String, ErrorLog> errors;
	private Executor executor;
	private boolean canAccessNetworkState = false;
	private Context appContext;
	private ErrorLogFactory errorLogFactory;
	private ConnectionFactory connectionFactory;
	
	/**
	 * Constructor
	 * 
	 * @param context - a standard context 
	 * @param memCache - a cache used where the calls happen on the UI thread. This cache needs to be fast
	 * @param diskCache - a cache used where the calls happen on a background thread. This cache can be slower. 
	 * 		While you can pass in any type of Cache<String, Bitmap> object you'd like, {DiskLruCacheFascade} is 
	 * 		recommended.
	 */
	@SuppressLint("NewApi")
	public BitmapLoader(Context context, Cache<String, Bitmap> memCache, Cache<String, Bitmap> diskCache) {
		this.memCache = memCache;
		this.diskCache = diskCache;
		errors = new LruCache<String, BitmapLoader.ErrorLog>(200);
		errorLogFactory = new ErrorLogFactoryImpl(60 * 1000);
		
		if (!(context instanceof Service)) {
			appContext = context.getApplicationContext();
		} else {
			appContext = context;
		}
		
		PackageManager pm = context.getPackageManager();
		int hasPerm = pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, 
				context.getPackageName());
		if (hasPerm != PackageManager.PERMISSION_GRANTED) {
			canAccessNetworkState = true;
		}
		connectionFactory = new ConnectionFactoryImpl();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			executor = PortedAsyncTask.PENTA_THREAD_EXECUTOR;
		} else {
			executor = PortedAsyncTask.DUAL_THREAD_EXECUTOR;
		}
	}
	
	/**
	 * Sets the executor where the asyn task will execute on. 
	 * 
	 * @param executor
	 */
	public void setExecuteOnExecutor(Executor executor) {
		this.executor = executor;
	}
	
	/**
	 * Gets the ErrorLogFactory that was set
	 * 
	 * @return
	 */
	public ErrorLogFactory getErrorLogFactory() {
		return errorLogFactory;
	}
	
	/**
	 * Sets the factory which is responsible for creating the ErrorLog event when an error happens.
	 * By allowing a factory, the client can use the implementation of the ErrorLog and have
	 * it validate itself how it sees fit. The default factory validates upon time passed
	 * 
	 * @param errorLogFactory
	 */
	public void setErrorLogFactory(ErrorLogFactory errorLogFactory) {
		this.errorLogFactory = errorLogFactory;
	}
	
	/**
	 * Sets the URLConnectionFactory which controls the creation of the URLConnection.
	 * Clients which need to set custom properties can set their own factory
	 * and provide options on the connection like "no-cache" and such
	 * 
	 * @param connectionFactory
	 */
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	/**
	 * Get's the ConnectionFactory that was set 
	 * 
	 * @return ConnectionFactory
	 */
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}
	
	/**
	 * Call to load an bitmap. The call will first check if the image is available in memory, 
	 * then it moves to checking the disk cache and finally it will go to an external source (the web). 
	 * Before checking for the image on the web, the call will check to see if the url is in a cache of
	 * IO errors such the call has failed previously. If this is the case and the error is considered
	 * valid, the call will return immediately.
	 * 
	 * @param request The value object used to describe how the image should be loaded.
	 * @return a token which may be null which can be used to cancel the loading task
	 */
	@SuppressLint("NewApi")
	public Cancelable load(LoadRequest request) {
		
		// if the url is blank, fault out immediately
		if (TextUtils.isEmpty(request.uri)) {
			if (request.callback != null) request.callback.onError(
					new IllegalArgumentException("Uri is empty"), 
					ErrorSource.ARGUMENT,
					request);
			return null;
		}
		
		// if the url is in our cached urls, fault out immediately
		ErrorLog error = getValidError(request.uri);
		if (error != null) {
			if (request.callback != null) request.callback.onError(error.getError(), 
					ErrorSource.ERROR_CACHE, 
					request);
			return null;
		}
		
		Bitmap bitmap = null;
		
		// check if the image is in memory
		bitmap = getFromMemCache(request);
		if (bitmap != null) {
			if (request.callback != null) request.callback.onSuccess(bitmap, BitmapSource.MEMORY, request);
			return null;
		}
		
		// if the image was no in memory, begin to load it async
		if (request.options != null && request.options.inJustDecodeBounds) {
			FetchImageBoundsOnlyTask task = new FetchImageBoundsOnlyTask(request.callback);
			if (executor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
				task.executeOnExecutor(executor, request);
			else task.execute(request);
			return task;
		} else {
			FetchImageTask task = new FetchImageTask(request.callback);
			if (executor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
				task.executeOnExecutor(executor, request);
			else task.execute(request);
			return task;
		}
	}
	
	/**
	 * Convenience method to clear all the caches. The clearing of the disk cache
	 * will run in a seperate thread.
	 */
	public void clearCache() {
		clearMemCache();
		clearDiskCache();
	}
	
	/**
	 * Convenience method to clear the memory cache.
	 */
	public void clearMemCache() {
		if (memCache == null) return;
		memCache.clear();
	}
	
	/**
	 * Convenience method to clear the disk cache. If this is called from the UI thread, 
	 * the process will automatically run in a separate thread. If it's already in a non-UI thread
	 * the process will be synchronous. 
	 */
	public void clearDiskCache() {
		if (diskCache == null) return;
		if (Looper.myLooper() == Looper.getMainLooper()) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					diskCache.clear();
					return null;
				}
			}.execute();
		} 
		
		// if on a BG thread
		else {
			diskCache.clear();
		}
	}
	
	/**
	 * Convenience method to get a Bitmap from the memory cache
	 */
	public Bitmap getFromMemCache(LoadRequest request) {
		if (memCache == null) return null;
		return memCache.get(request.generateKey());
	}
	
	/**
	 * Convenience method to get a Bitmap from the disk cache. Do not call this method from the UI 
	 * thread.
	 */
	public Bitmap getFromDiskCache(LoadRequest request) {
		if (diskCache == null) return null;
		if (diskCache instanceof BitmapOptionsDecoder) 
			((BitmapOptionsDecoder)diskCache).setOptions(request.options, request.outPadding);
		return diskCache.get(request.generateKey());
	}
	
	/**
	 * This method is synchronous. Make sure to call it from a background thread
	 * 
	 * @param url
	 * @param options
	 * @return
	 * @throws IOException
	 */
	private Bitmap loadExternalBitmap(String url, BitmapFactory.Options options, Rect outPadding) 
			throws IOException, Exception {
		URLConnection connection = connectionFactory.getConnection(url);
		final int IO_BUFFER_SIZE = 8 * 1024;
		InputStream in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
		Bitmap bitmap = BitmapFactory.decodeStream(in, outPadding, options);
		return bitmap;
	}
	
	// should the LoadRequest just apply the processors?
	private Bitmap applyBitmapProcessors(LoadRequest request, Bitmap src) {
		if (request.processes == null) return src;
		Bitmap bm = src;
		for (BitmapProcessor p : request.processes) {
			bm = p.process(bm);
		}
		return bm;
	}
	
	private ErrorLog getValidError(String url) {
		ErrorLog error = errors.get(url);
		if (error == null) return null;
		if (error.isValid()) return error;
		else {
			errors.remove(url);
			return null;
		}
	}
	
	private ErrorLog getError(String url) {
		return errors.get(url);
	}
	
	private void putInMemCache(LoadRequest request, Bitmap bitmap) {
		if (memCache != null) {
			memCache.put(request.generateKey(), bitmap);
		}
	}
	
	private void putInDiskCache(LoadRequest request, Bitmap bitmap) {
		if (diskCache != null) {
			diskCache.put(request.generateKey(), bitmap);
		}
	}
	
	private boolean needsInternet(String uri) {
		String protocol = UrlUtils.getSchemePrefix(uri);
		if (protocol == null) return false;
		return !protocol.equals("file");
	}
	
	private boolean hasInternet() {
		if (canAccessNetworkState) {
			ConnectivityManager cm = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isConnectedOrConnecting()) {
				return true;
			} else {
				return false;
			}
		} else {
			// if can't access the network state, assume we have internet
			return true;
		}
	}
	
	/*
	 * This subclass of AsynTask is used to do the actual image fetching. 
	 * The order of fetching is
	 * 1) Checks the memory
	 * 2) Checks the disk cache
	 * 3) Goes to the web
	 */
	private class FetchImageTask extends PortedAsyncTask<LoadRequest, Void, Bitmap> implements Cancelable {
		private Throwable exc;
		private ErrorSource errorSource;
		private LoadRequest request;
		private Callback callback;
		private BitmapSource source;
		
		private FetchImageTask(Callback callback) {
			this.callback = callback;
		}
		
		/**
		 * So calling clients can quit the task without having to know the implementation of what
		 * this is.
		 */
		@Override
		public void cancel() {
			cancel(true);
		}
		
		@Override
		protected Bitmap doInBackground(LoadRequest... params) {
			request = params[0];
			Bitmap bitmap = null;
			
			// if task is not canceled, check if the image is in the memory
			if (!isCancelled()) {
				bitmap = getFromMemCache(request);
				if (bitmap != null) {
					source = BitmapSource.MEMORY;
					// add to the disk cache while here
					// at this point, we don't care if the task has been canceled
					if (diskCache != null) {
						boolean hasItem = diskCache.hasObject(request.generateKey());
						if (!hasItem) {
							putInDiskCache(request, bitmap);
						}
					}
					return bitmap; 
				}
			}
			
			// if task is not canceled, check if the image is in the disk cache
			if (!isCancelled()) {
				try {
					bitmap = getFromDiskCache(request);
				} catch (OutOfMemoryError e) {
					// clear up some memory and let's try again
					clearMemCache();
					System.gc();
					try {
						bitmap = getFromDiskCache(request);
					} catch (OutOfMemoryError e2) {
						// give up
					}
				}
				if (bitmap != null) {
					source = BitmapSource.DISK;
					putInMemCache(request, bitmap);
//					Log.i(TAG, "transaction time : " + (System.currentTimeMillis() - s));
					return bitmap; 
				}
			}
			
			// if task is not canceled, go to the web/external to get the image
			if (!isCancelled()) {
				
				// make sure the url isn't in the cached errors
				ErrorLog loadError = getError(request.uri);
				if (loadError != null) {
					exc = loadError.getError();
					errorSource = ErrorSource.ERROR_CACHE;
					return null;
				}
				
				// make sure we have internet before requesting the image
				if (needsInternet(request.uri) && !hasInternet()) {
					exc = new NetworkErrorException("No Network Connection");
					errorSource = ErrorSource.NO_NETWORK;
					return null;
				}
				
				// no errors cached, valid internet connection, load the image
				
				// TODO: if the uri protocol is of file:// seems needless to cache the image
				// to the disk cache....right? Or does bitmap options matter here. 
				try {
					bitmap = loadExternalBitmap(request.uri, request.options, request.outPadding);
					bitmap = applyBitmapProcessors(request, bitmap);
				} catch (OutOfMemoryError e) {
					clearMemCache();
					System.gc();
					try {
						// try 1 more time
						bitmap = loadExternalBitmap(request.uri, request.options, request.outPadding);
						bitmap = applyBitmapProcessors(request, bitmap);
					} catch (OutOfMemoryError in2) {
						// give up
						exc = in2;
						errorSource = ErrorSource.EXTERNAL;
					} catch (IOException in3) {
						exc = in3.getCause() != null ? in3.getCause() : in3;
						errorSource = ErrorSource.EXTERNAL;
					} catch (Exception in4) {
						exc = in4;
						if (in4 instanceof NetworkErrorException)
							errorSource = ErrorSource.NO_NETWORK;
						else 
							errorSource = ErrorSource.EXTERNAL;
					}
				} catch (IOException e3) {
					exc = e3;
					errorSource = ErrorSource.EXTERNAL;
				} catch (Exception e4) {
					exc = e4;
					if (e4 instanceof NetworkErrorException)
						errorSource = ErrorSource.NO_NETWORK;
					else 
						errorSource = ErrorSource.EXTERNAL;
				}
				if (exc != null && errorLogFactory != null) {
					ErrorLog errorLog = errorLogFactory.createErrorLog(request.uri, exc, errorSource);
					errors.put(request.uri, errorLog);
				}
				
				if (bitmap != null) {
					source = BitmapSource.EXTERNAL;
					// add to the disk cache while here
					// at this point, we don't care if the task has been canceled
					putInMemCache(request, bitmap);
					putInDiskCache(request, bitmap);
				}
			}
//			Log.i(TAG, "transaction time : " + (System.currentTimeMillis() - s));
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (isCancelled()) return;
			if (callback == null) return;
			
			if (exc != null || result == null) {
				callback.onError(exc, errorSource, request);
			} else {
				callback.onSuccess(result, source, request);
			}
			request = null;
			callback = null;
			exc = null;
		}
	}
	
	private class FetchImageBoundsOnlyTask extends PortedAsyncTask<LoadRequest, Void, Bitmap> implements Cancelable {
		
		private boolean checkDiskCache = true;
		private Throwable exc;
		private ErrorSource errorSource;
		private LoadRequest request;
		private Callback callback;
		private BitmapSource source;
		
		private FetchImageBoundsOnlyTask(Callback callback) {
			this.callback = callback;
		}
		
		@Override
		public void cancel() {
			cancel(true);
		}

		@Override
		protected Bitmap doInBackground(LoadRequest... params) {
			request = params[0];
			
			if (!isCancelled() && diskCache != null && checkDiskCache) {
				boolean hasObject = diskCache.hasObject(request.generateKey());
				if (hasObject) {
					getFromDiskCache(request);
					source = BitmapSource.DISK;
					return null;
				}
			}
			
			if (!isCancelled()) {
				ErrorLog loadError = getError(request.uri);
				if (loadError != null) {
					exc = loadError.getError();
					errorSource = ErrorSource.ERROR_CACHE;
					return null;
				}
				
				// make sure we have internet before requesting the image
				if (needsInternet(request.uri) && !hasInternet()) {
					exc = new NetworkErrorException("No Network Connection");
					errorSource = ErrorSource.NO_NETWORK;
					return null;
				}
				
				try {
					loadExternalBitmap(request.uri, request.options, request.outPadding);
					source = BitmapSource.EXTERNAL;
					return null;
				} catch (IOException e) {
					exc = e;
					errorSource = ErrorSource.EXTERNAL;
				} catch (Exception e) {
					if (e instanceof NetworkErrorException)
						errorSource = ErrorSource.NO_NETWORK;
					else 
						errorSource = ErrorSource.EXTERNAL;
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (isCancelled() || callback == null) return;
			if (exc != null) callback.onError(exc, errorSource, request);
			else callback.onSuccess(null, source, request);
		}
	}
	
	
	/**
	 * Implementation of the ErrorLogFactory to create ErrorLog objects which validate based
	 * upon time passed. The default is 60 seconds. 
	 * 
	 * @author Joshua
	 */
	public static class ErrorLogFactoryImpl implements ErrorLogFactory {
		
		private long timeToLive;
		
		/**
		 * Gets the time the error is considered valid
		 * @return
		 */
		public long getTimeToLive() {
			return timeToLive;
		}
		
		/**
		 * Sets in milliseconds how long this error is considered valid
		 * 
		 * @param validationTime milliseconds
		 */
		public void setTimeToLive(long timeToLiveMilli) {
			this.timeToLive = timeToLiveMilli;
		}
		
		public ErrorLogFactoryImpl(long timeToLiveMilli) {
			this.timeToLive = timeToLiveMilli;
		}
		 
		@Override
		public ErrorLog createErrorLog(String url, Throwable exception, ErrorSource errorSource) {
			return new ErrorLogImpl(timeToLive, exception);
		}
	}
	
	private static class ErrorLogImpl implements ErrorLog {
		private long when;
		private Throwable thr;
		private long timeToLive;
		
		private ErrorLogImpl(long timeToLiveMilli, Throwable error) {
			thr = error;
			timeToLive = timeToLiveMilli;
			when = System.currentTimeMillis();
		}
		
		@Override
		public Throwable getError() {
			return thr;
		}
		
		@Override
		public boolean isValid() {
			long diff = System.currentTimeMillis() - when;
			// TODO: maybe check for if the error is a NetworkErrorException
			// and if we have internet now the error is no longer valid
			// else use the time
			return (diff < timeToLive);
		}
	}
	
	public static class ConnectionFactoryImpl implements ConnectionFactory {
		
		private int readTimeout = 0;
		private int connectTimeout = 0;
		
		public ConnectionFactoryImpl() {
		}
		
		public void setReadTimeout(int milli) {
			this.readTimeout = milli;
		}
		
		public void setConnectTimeout(int milli) {
			this.connectTimeout = milli;
		}
		
		@Override
		public URLConnection getConnection(String url) throws IOException {
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			if (connectTimeout > 0) conn.setConnectTimeout(connectTimeout);
			if (readTimeout > 0) conn.setReadTimeout(readTimeout);
			return conn;
		}
		
	}
	
}