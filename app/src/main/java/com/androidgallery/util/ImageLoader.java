package com.androidgallery.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by A555LF on 2016/8/2.
 */
public class ImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    private static ImageLoader mInstance;
    private ExecutorService mImageThreadPool;//设置了线程池
    private Handler mHandler;
    private   LinkedList<Runnable> mRunnableList;
    public static final int ONE = 0;
    private static final int BIG_IMAGE = 1;
    private static final int SMALL_IMAGE = 2;

    private volatile Semaphore mSemaphore;//设置信号量以便获取在线程池中加载的顺序


    private ImageLoader() {
        HandlerThread mHandlerThread = new HandlerThread("loading");
        mHandlerThread.start();
        mRunnableList = new LinkedList<Runnable>();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (!mImageThreadPool.isShutdown()) {
                    mImageThreadPool.execute(getLoadTask());
                }
            }


        };

        mImageThreadPool = Executors.newFixedThreadPool(5);
        mSemaphore = new Semaphore(5);


        //获取应用程序的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        //用最大内存的1/4来存储图片
        final int cacheSize = maxMemory / 4;
        Log.d("最大内存", "最大内存是\n" + maxMemory);
        Log.d("分配的内存", "分配的内存\n" + cacheSize);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            //获取每张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // Log.d("每张图片大小","每张图片的大小"+bitmap.getRowBytes() * bitmap.getHeight() / 1024);
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * 通过此方法来获取CustomizeImageLoader的实例
     */
    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                mInstance = new ImageLoader();
            }
        }
        return mInstance;
    }


    /**
     * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，根据自定义的ImageView控件的大小来裁剪Bitmap
     */
    public Bitmap loadNativeImage(final String path, final Point mPoint, final ImageCallBack mCallBack) {
        //先获取内存中的Bitmap
        Bitmap bitmap = getBitmapFromMemCache(path);


        final Handler hander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap) msg.obj, path);
            }


        };


        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (bitmap == null) {

            addList(new Runnable() {

                @Override
                public void run() {
                    //先获取图片的缩略图
                    Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y, SMALL_IMAGE);
                    Message msg = hander.obtainMessage();
                    msg.obj = mBitmap;
                    hander.sendMessage(msg);

                    mSemaphore.release();
                    Log.d("释放信号量","释放信号量");
                    //将图片加入到内存缓存
                    addBitmapToMemoryCache(path, mBitmap);
                }
            });
        }
        return bitmap;

    }


    /**
     * 往内存缓存中添加Bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            Log.d("每一张被添加的图片缓存", "每一张被添加的图片缓存" + key);
            mMemoryCache.put(key, bitmap);

        }
    }

    /**
     * 根据key来获取内存中的图片
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    /**
     * 根据自定义ImageView的宽和高来获取图片的缩略图
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight, int type) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight, type);
      Log.d("压缩比例","压缩比例为"+options.inSampleSize);
        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;


        return BitmapFactory.decodeFile(path, options);
    }


    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight, int type) {
        int inSampleSize = 1;
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        if (type == SMALL_IMAGE) {

            if (viewWidth == 0 || viewWidth == 0) {
                return inSampleSize;
            }


            //当Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例，否则不执行
            if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
                int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
                int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

                //为了保证图片不缩放变形，取宽高比例最小的那个
                inSampleSize = widthScale < heightScale ? widthScale : heightScale;
            }
        } else {
            inSampleSize = 800/100;
        }

        return inSampleSize;
    }


    /**
     * 加载本地图片的回调接口
     */
    public interface ImageCallBack {
        /**
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
         */
        public void onImageLoader(Bitmap bitmap, String path);
    }

    private void addList(Runnable runnable) {

        mRunnableList.add(runnable);


        mHandler.sendEmptyMessage(ONE);

    }

    private Runnable getLoadTask() {
        try {
            mSemaphore.acquire();
            Log.d("获得信号量","获得信号量");//获取信号量,没有则被阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        return mRunnableList.removeLast();//从任务队列的最后一个开始取
    }

    public void LoadDetail(final String path, final Point mPoint, final ImageCallBack mCallBack) {

        final Handler hander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap) msg.obj, path);
                Log.d("加载大图有执行","加载大图有执行");

            }


        };


        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中


        addList(new Runnable() {

            @Override
            public void run() {

                Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint.x, mPoint.y, BIG_IMAGE);
                Message msg = hander.obtainMessage();
                msg.obj = mBitmap;
                hander.sendMessage(msg);
                mSemaphore.release();

                //将图片加入到内存缓存
                addBitmapToMemoryCache(path, mBitmap);
            }
        });

    }


}





