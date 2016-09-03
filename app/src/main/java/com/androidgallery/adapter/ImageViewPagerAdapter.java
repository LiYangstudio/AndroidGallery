package com.androidgallery.adapter;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidgallery.R;
import com.androidgallery.util.ImageLoader;

import java.util.List;

/**
 * Created by A555LF on 2016/8/29.
 */
public class ImageViewPagerAdapter extends PagerAdapter {
    private Point mPoint=new Point(500,800);
  private List<String> mList;
    private SparseArray<ImageView> mViewArray;
    private View view;
    public ImageViewPagerAdapter(List<String> mImageList,SparseArray<ImageView> ViewArray){
        mList=mImageList;
        mViewArray=ViewArray;
    }
    @Override
public int getCount(){return mList.size();}

    @Override
    public Object instantiateItem(ViewGroup contanier, int position) {

         view = LayoutInflater.from(contanier.getContext()).inflate(R.layout.image_viewpage_picture, null);
         final ImageView imageView=(ImageView) view.findViewById(R.id.detail_show);

       // ImageLoader.getInstance().loadLargePicture(mList.get(position),imageView);
     ImageLoader.getInstance().LoadDetail(mList.get(position), mPoint, new ImageLoader.ImageCallBack() {
                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {



                 imageView.setImageBitmap(bitmap);

                    }
                });

                mViewArray = new SparseArray<ImageView>();
        //mViewArray.put(position,imageView);
        contanier.addView(view);
        return view;


    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);

    }
}
