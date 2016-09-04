package com.androidgallery.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Window;
import android.widget.ImageView;

import com.androidgallery.R;
import com.androidgallery.adapter.ImageViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A555LF on 2016/8/29.
 */
public class ImagePageActivity extends Activity implements ViewPager.OnPageChangeListener {

    private List<String> mList;
    private int mLevel;
    private SparseArray<ImageView> mViewArray;

    public static void startMe(Context context, List<String> mSelectedList, int position) {
        Intent intent = new Intent(context, ImagePageActivity.class);
        intent.putStringArrayListExtra("SelectedImageList", (ArrayList<String>) mSelectedList);
        intent.putExtra("level", position);
        context.startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_viewpage);
        init();


    }

    private void init() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_vp_detail);
        mList = getIntent().getStringArrayListExtra("SelectedImageList");
        mLevel = getIntent().getIntExtra("level", 0);
        mViewArray = new SparseArray<ImageView>();
        ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(mList, mViewArray);
        viewPager.setAdapter(imageViewPagerAdapter);
        viewPager.setCurrentItem(mLevel);
        viewPager.setOnPageChangeListener(this);

    }

    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
