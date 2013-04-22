/*
 * Copyrigth (c) 2012 Joshua Musselwhite
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

package com.therealjoshua.essentials.bitmaploader.cache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Looper;

import com.therealjoshua.essentials.logger.Log;
import com.therealjoshua.essentials.bitmaploader.BitmapOptionsDecoder;
import com.therealjoshua.essentials.bitmaploader.cache.DiskLruCache.Editor;
import com.therealjoshua.essentials.bitmaploader.cache.DiskLruCache.Snapshot;

public class DiskLruCacheFacade implements Cache<String, Bitmap>, BitmapOptionsDecoder {
	
	private static final String TAG = DiskLruCacheFacade.class.getSimpleName();
	private DiskLruCache cache;
	private File directory;
	private int appVersion;
	private int valueCount;
	private long maxSize;
	volatile private boolean isReady = false;
	private boolean hasIOError = false;
//	private Object lock = new Object();
	private CompressFormat compressFormat = CompressFormat.JPEG;
	private int compressQuality = 70;
	
	private BitmapFactory.Options options;
	private Rect outPadding;
	
	public DiskLruCacheFacade(File directory, long maxSize) {
		this.directory = directory;
		this.appVersion = 0;
		this.valueCount = 1;
		this.maxSize = maxSize;
		if (Looper.myLooper() == Looper.getMainLooper()) {
			initAsync();
		} else {
			initSync();
		}
	}
	
	public void initAsync() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				initSync();
				return null;
			}
		}.execute();
	}
	
	public void initSync() {
		try {
			cache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);
		} catch (IOException e) {
			hasIOError = true;
		}
		isReady = true;
//		lock.notifyAll();
	}
	
	public CompressFormat getCompressFormat() {
		return compressFormat;
	}
	
	public void setCompressFormat(CompressFormat compressFormat) {
		this.compressFormat = compressFormat;
	}
	
	public int getCompressQuality() {
		return compressQuality;
	}
	
	public void setCompressQuality(int compressQuality) {
		this.compressQuality = compressQuality;
	}
	
	public void useHighQuailityCompressPreset() {
		compressFormat = CompressFormat.PNG;
		compressQuality = 100;
	}
	
	public void useFastCompressPreset() {
		compressFormat = CompressFormat.JPEG;
		compressQuality = 70;
	}
	
	@Override
	public void setOptions(Options options, Rect outPadding) {
		this.options = options;
		this.outPadding = outPadding;
	}
	
	@Override
	public Bitmap get(String key) {
		waitForInit();
		if (!isOkay()) return null;
		Snapshot snapshot = null;
        InputStream in = null;
        try {
            snapshot = cache.get(key);
            if (snapshot != null) {
                in = snapshot.getInputStream(0);
                // need to ignore the inSampleSize if in the cache as it's already been sampled down before
                // set the sample size to the default
                int inSampleSize = 1;
                if (options != null) {
                	inSampleSize = options.inSampleSize;
                	options.inSampleSize = 1;
                }
                Bitmap bitmap = BitmapFactory.decodeStream(in, outPadding, options);
                // restore the inSampleSize back to the original
                if (options != null) options.inSampleSize = inSampleSize;
                return bitmap;
            }
        } catch (IOException e) {
            Log.w(TAG, "IO Error getting bitmap from disk", e);
        } catch (Exception e) {
            Log.w(TAG, "Error getting bitmap from disk", e);
        } finally {
            /*IOUtils.*/closeQuietly(in);
            /*IOUtils.*/closeQuietly(snapshot);
            options = null;
            outPadding = null;
        }
		
		return null;
	}
	
	@Override
	public void put(String key, Bitmap bitmap) {
		waitForInit();
		if (!isOkay()) return;
		long s = System.currentTimeMillis();
        Editor editor = null;
        OutputStream out = null;
        try {
            editor = cache.edit(key);
            if (editor != null) {
                out = editor.newOutputStream(0);
                bitmap.compress(compressFormat, compressQuality, out);
                editor.commit();
            }
        } catch (IllegalStateException e) { 
        	Log.w(TAG, "Failed to put bitmap on disk.", e);
		} catch (IOException e) {
			Log.w(TAG, "Failed to put bitmap on disk.", e);
        } catch (Exception e) {
            Log.w(TAG, "Failed to put bitmap on disk.", e);
        } finally {
            closeQuietly(out);
//            quietlyAbortUnlessCommitted(editor);
        }
        long d = System.currentTimeMillis() - s;
        Log.d(TAG, "time to write to disk: " + d);
	}

	
	@Override
	public void clear() {
		waitForInit();
		if (!isOkay()) return;
		try {
			cache.delete();
		} catch (IllegalStateException e) { 
		} catch (IOException e) {
		}
		hasIOError = false;
		isReady = false;
		initSync();
	}

	@Override
	public boolean hasObject(String key) {
		waitForInit();
		if (!isOkay()) return false;
		Snapshot shot;
		try {
			shot = cache.get(key);
			return shot != null;
		} catch (IllegalStateException e) { // And why would if I check for closed i still get an error thrown?
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	private boolean isOkay() {
		return isReady && !hasIOError && cache != null && !cache.isClosed();
	}
	
	private void waitForInit() {
		while (!isReady && !hasIOError) { }
	}
	
	// the comment just shows this comes from the apache IOUtils 
	private void /*IOUtils.*/closeQuietly(Closeable c) {
		if (c == null) return;
		try {
			c.close();
		} catch (Exception e) {}
	}
}