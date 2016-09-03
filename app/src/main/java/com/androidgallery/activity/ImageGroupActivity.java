package com.androidgallery.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.androidgallery.R;
import com.androidgallery.adapter.ImageFileAdapter;

import java.util.List;

/**
 * Created by A555LF on 2016/8/2.
 */


public class ImageGroupActivity extends Activity {
    private GridView mGridView;
    private List<String> mList;
    private ImageFileAdapter mImageFileAdapter;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);

       init();//初始化各控件
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           List<String> mlist=mImageFileAdapter.getSelectItems();
                ImagePageActivity.startMe(ImageGroupActivity.this,mlist,0);

            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    private void init(){
        mButton=(Button)findViewById(R.id.button_selected);
        mGridView = (GridView) findViewById(R.id.child_grid);
        mList = getIntent().getStringArrayListExtra("data");

        mImageFileAdapter = new ImageFileAdapter(this, mList, mGridView);
        mGridView.setAdapter(mImageFileAdapter);
    }




}

