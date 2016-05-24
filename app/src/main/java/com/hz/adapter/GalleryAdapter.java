package com.hz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.hz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/23.
 */
public class GalleryAdapter {
    private static CommonAdapter adapter;
    public static List<Bitmap> list = new ArrayList<>();

    private static Context context;

    private static ImageView iv_cell;



    public static CommonAdapter getAdapter(Context ctx){
        if(adapter == null) {
            context = ctx;
            list.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_camera_enhance_black_48dp));
            adapter = new CommonAdapter(ctx, list, R.layout.cell_gallery, new CommonAdapter.OnGetViewHolderListener() {
                @Override
                public void setConvertView(ViewHolder holder, int position) {
                    iv_cell = holder.setImageBitmap(R.id.iv_gallery, list.get(position));
                }
            });
        }
        return adapter;
    }


    public static CommonAdapter get(){
        return adapter;
    }


}

