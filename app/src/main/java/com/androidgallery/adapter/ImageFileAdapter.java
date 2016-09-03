package com.androidgallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;

import com.androidgallery.R;
import com.androidgallery.util.CustomizeImageView;
import com.androidgallery.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by A555LF on 2016/8/2.
 */
public class ImageFileAdapter extends BaseAdapter {
    private Point mPoint = new Point(0, 0);//用point来封装图片的宽和高的信息

    private HashMap<String, Boolean> mSelectMap= new HashMap<String, Boolean>() ;

    private GridView mGridView;
    private List<String> list;
    protected LayoutInflater mInflater;



    public ImageFileAdapter(Context context, List<String> list, GridView mGridView) {
        this.list = list;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;


        String path = list.get(position);

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.grid_child_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (CustomizeImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);


            viewHolder.mImageView.setOnMeasureListener(new CustomizeImageView.OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            //  viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        viewHolder.mImageView.setTag(path);
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             //   mSelectMap = new HashMap<String, Boolean>();
                mSelectMap.put(list.get(position), isChecked);

        }
        });



        viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(list.get(position)) ? mSelectMap.get(list.get(position)) : false);


        Bitmap bitmap = ImageLoader.getInstance().loadNativeImage(path, mPoint, new ImageLoader.ImageCallBack() {

            @Override
            public void onImageLoader(Bitmap bitmap, String path) {


                ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                if(bitmap != null && mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }


            }

        });

        if(bitmap != null){
            viewHolder.mImageView.setImageBitmap(bitmap);
        }else{

            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        return convertView;
    }
    public List<String> getSelectItems(){
        List<String> list=new ArrayList<String>();
        for(Iterator<Map.Entry<String,Boolean>> it=mSelectMap.entrySet().iterator();it.hasNext();){
            Map.Entry<String,Boolean> entry=it.next();
            if(entry.getValue()){
                list.add(entry.getKey());
            }
        }
        return list;
    }



    public static class ViewHolder{
        public CustomizeImageView mImageView;
        public CheckBox mCheckBox;
    }



}


