package com.uva.adrmart.cronovisor_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.domain.Image;

import java.util.ArrayList;

/**
 * Created by Adrian on 02/06/2016.
 */
public class GridViewImageAdapter extends ArrayAdapter<Image> {

    private static final String TAG = GridViewStreetAdapter.class.getName();

    private final Context mContext;
    private final int layoutResourceId;
    private ArrayList<Image> mGridData = new ArrayList<>();

    public GridViewImageAdapter(Context mContext, ArrayList<Image> mGridData) {
        super(mContext, R.layout.grid_item, mGridData);
        this.layoutResourceId = R.layout.grid_item;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }
    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<Image> mGridData) {
        this.mGridData = mGridData;
        Log.d(TAG, "setGridData");
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.name);
            holder.imageView = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Image item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitulo()));
        Glide.with(mContext).load(item.getUrl()).into(holder.imageView);

        if (item.getUrl()!=null){
            Glide.with(mContext).load(item.getUrl()).into(holder.imageView);
        }
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}
