EssentialsLoader
================

An image loading and caching system for Android. You can use this system to effeciently lazy load images in a ListView, a ViewPager, or any type of component you'd like.  

## Why use EssentialsLoader?

While systems like Google's sample from [Loading Large Bitmaps Efficiently](http://developer.android.com/training/displaying-bitmaps/load-bitmap.html) and others are nice, they often create a tight coupling between the loading system and the view. You are forced to reference an ImageView just to load an image. These systems make it very difficult to load an image as a background of a view or to do custom behaviors to the component when the image loads. EssentialsLoader removes that coupling so that you can load a bitmap without needing an ImageView or any view, and you are free to use the loaded image in any fashion you see fit whether it's an ImageView or an ImageView background or a custom drawable, or no View at all and just pure Bitmap data.  


One more issue EssentialsLoader addresses is often loading systems do not expose the caches, but internally compose them. EssentailsLoader aggregates the caches into the loader system allow you to freely to swap in and out and use any caching system you like. You may want to share the memory cache across the entire application but not the disk cache or vise versa. With EssentailsLoader you have the option to choose.


## Getting Started

 * Grab from Jar files from the sample or from the root git directory and put essentials-loader.jar into your libs folder
 * In your manifest file add the permissions, INTERNET, WRITE_EXTERNAL_STORAGE, and ACCESS_NETWORK_STATE
 * You'll probably want to do the initialization in your `Application` class
 * Optionally (not part of EssentialsLoader but Android sdk) set a cache using `HttpResponseCache.install`
 * Create a `BitmapLruCache` memory cache, `DiskLruCacheFacade` disk cache, and create a `BitmapLoader` instance
 * You'll probably want to store a global reference to these using `BitmapLoaderLocator.put` methods
 * In your Activity/Fragment/Adapter if you're loading an Bitmap into an `ImageView`, create a `FadeImageViewBinder` and call load method passing in your url.
 * that's it!

License
=======

    Copyright 2012 Joshua Musselwhite

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
