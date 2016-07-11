package com.uva.adrmart.cronovisor_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.domain.Street;

import java.util.ArrayList;

/**
 * Created by Adrian on 02/06/2016.
 */
public class GridViewStreetAdapter extends BaseAdapter implements Filterable{

    private static final String TAG = GridViewStreetAdapter.class.getName();

    private final Context mContext;
    private final int layoutResourceId;
    private ArrayList<Street> mGridData = new ArrayList<>();
    private ArrayList<Street> mGridDataSearch = new ArrayList<>();

    private CustomFilter filter;

    public GridViewStreetAdapter(Context mContext, ArrayList<Street> mGridData) {
        this.layoutResourceId = R.layout.grid_item;
        this.mContext = mContext;
        this.mGridData = mGridData;
        this.mGridDataSearch = mGridData;
    }
    /**
     * Updates grid data and refresh grid items.
     * @param mGridData array of data
     */
    public void setGridData(ArrayList<Street> mGridData) {
        this.mGridData = mGridData;
        Log.d(TAG, "setGridData");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGridData.size();
    }

    @Override
    public Object getItem(int position) {
        return mGridData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mGridData.indexOf(getItem(position));
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
        Street item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getNombre()));
        if (item.getRepresentativo()!="null"){
            Glide.with(mContext).load(item.getRepresentativo()).into(holder.imageView);
        }

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new CustomFilter();
        }
        return filter;
    }

    private class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint!=null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<Street> filters = new ArrayList<>();
                for (int i=0;i<mGridDataSearch.size();i++){
                    if(mGridDataSearch.get(i).getNombre().toUpperCase().contains(constraint)){
                        filters.add(mGridDataSearch.get(i));
                    }
                }

                results.count=filters.size();
                results.values=filters;
            } else{
                results.count=mGridDataSearch.size();
                results.values=mGridDataSearch;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mGridData = (ArrayList<Street>) results.values;
            notifyDataSetChanged();
        }
    }
}

