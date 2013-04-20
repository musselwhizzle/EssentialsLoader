package com.therealjoshua.essentialsloadersample1;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.therealjoshua.essentials.bitmaploader.BitmapLoaderLocator;
import com.therealjoshua.essentials.bitmaploader.binders.FadeImageViewBinder;
import com.therealjoshua.essentials.bitmaploader.binders.ImageViewBinder;
import com.therealjoshua.essentials.logger.Log;

/*
 * This is pretty much the code from the ImageGridFragment from here: 
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 * All I did was modify it some to make it fix the EssentialsLoader and 
 * use an Activity instead of a Fragment.
 */
public class Sample4Activity extends Activity {
	private static final String TAG = "Sample4Activity";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        gridView = new GridView(this);
		gridView.setNumColumns(2);
		gridView.setDrawSelectorOnTop(true);
		gridView.setColumnWidth( mImageThumbSize );
		gridView.setHorizontalSpacing( mImageThumbSpacing );
		gridView.setVerticalSpacing( mImageThumbSpacing);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);
		mAdapter = new ImageAdapter(this);
		gridView.setAdapter(mAdapter);
		gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                            		gridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (gridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                            }
                        }
                    }
                });
		
		ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		setContentView(gridView, p);
		
    }


    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        private Resources res;
        private ImageViewBinder binder;
        
        
        public ImageAdapter(Context context) {
            super();
            mContext = context;
            res = context.getResources();
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            
            binder = new FadeImageViewBinder(BitmapLoaderLocator.getBitmapLoader());
            binder.setLoadingDrawableResId(R.drawable.empty_photo);
        }

        @Override
        public int getCount() {
            return Images.ROMAIN_GUY_THUMBS.length;
        }

        @Override
        public String getItem(int position) {
        	return Images.ROMAIN_GUY_THUMBS[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            binder.load(imageView, getItem(position));
            
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
