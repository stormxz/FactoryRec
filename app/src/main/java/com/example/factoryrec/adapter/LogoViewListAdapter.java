package com.example.factoryrec.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.factoryrec.R;

import java.util.List;

public class LogoViewListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mDatas;

    public LogoViewListAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public String getItem(int i) {
        return mDatas == null ? null : mDatas.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        public ImageView logoImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.logo_image_item, null);
            viewHolder.logoImage = (ImageView) convertView.findViewById(R.id.myList_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (null != mDatas) {
            viewHolder.logoImage.setImageBitmap(BitmapFactory.decodeFile(mDatas.get(position)));
        } else {
            viewHolder.logoImage.setImageResource(-1);
        }
        return convertView;
    }
}
