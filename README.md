EssentialsLoader
================

An image loading and caching system for Android.

What sets this loader apart from the rest?
================

In terms of flexibility, this system has a couple of nice features:
1) It decouples the loading process from the displaying process. While systems like google's "Loading Large Bitmaps Efficiently" and others a are nice, you are forced to reference an ImageView just to load an image. This system removes that coupling so that you can load a bitmap without needing a view. 


2) It also decouples the caches. With the caches being aggregated into the loader system and using an interface
you are free to swap int and out and use any caching system you like. For instance, perhaps in a nested Activity you wanted to have a dedicated DiskCache just for that so that you may clear out the contents once that page is done but still use the same MemoryCache. This loader gives you that flexibility


How to use it?
================

1) First check out the samples.
2) Grab the jar files and put them in your libs folder
3) In your Application subclass, you'll probably want to initialize the loader along with your caches
4) If you want to load an Bitmap into an ImageView, you'll probably want to use a FadeImageViewBinder and now just call the load method
5) profit!
