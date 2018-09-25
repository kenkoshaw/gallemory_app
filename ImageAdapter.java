package com.example.kenkoku.gallemory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private ArrayList<MyData> photoList;
    private ArrayList<Integer> idList;
    private LayoutInflater inflater;
    private Context context;

    ImageAdapter(Context c, ArrayList<MyData> photoList, ArrayList<Integer> idList) {
        context = c;
        this.photoList = photoList;
        this.idList = idList;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addPhoto(Integer id, MyData data) {
        idList.add(id);
        photoList.add(data);
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public MyData getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getImageId(int position) {
        return idList.get(position);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_grid_item, null);
        }

        MyData imgData = photoList.get(position);
        ImageView imageView = convertView.findViewById(R.id.menu_image);

        Bitmap bitmap = imgData.getImageDataInBitmap();
        bitmap = MainActivity.getResizedBitmap(bitmap, 200);

        GlideApp
                .with(context)
                .load(bitmap)
                .placeholder(R.drawable.totoro)
                .transforms(new CenterCrop(), new RoundedCorners(60))
                .into(imageView);

        return imageView;
    }


}
