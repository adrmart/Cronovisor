package com.uva.adrmart.cronovisor_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
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
 * Created by Adrian on 01/07/2016.
 */
public class ListViewStreetAdapter extends BaseAdapter implements Filterable{

    Context mContext;

    private ArrayList<Street> mGridData = new ArrayList<>();
    private ArrayList<Street> mGridDataSearch = new ArrayList<>();
    private CustomFilter filter;

    public ListViewStreetAdapter(Context mContext, ArrayList<Street> mGridData) {
        this.mGridData = mGridData;
        this.mGridDataSearch = mGridData;
        this.mContext = mContext;
    }

    public void setGridData(ArrayList<Street> gridData) {
        this.mGridData = gridData;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView description;
    }

    @Override
    public int getCount() {
        return mGridData.size();
    }

    @Override
    public Street getItem(int position) {
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
            row = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.list_title);
            holder.description = (TextView) row.findViewById(R.id.list_description);
            holder.imageView = (ImageView) row.findViewById(R.id.list_image);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }
        Street item = mGridData.get(position);
        holder.name.setText(Html.fromHtml(item.getNombre()));
        holder.description.setText(Html.fromHtml(item.getDescripcion()));
        if (item.getRepresentativo()!="null"){
            Glide.with(mContext).load(item.getRepresentativo()).into(holder.imageView);
        }

        return row;
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new CustomFilter();
        }
        return filter;
    }

    private class CustomFilter extends Filter {

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
