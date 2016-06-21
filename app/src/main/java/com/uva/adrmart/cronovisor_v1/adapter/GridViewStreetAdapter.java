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

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Street> mGridData = new ArrayList<Street>();
    private ArrayList<Street> mGridDataSearch = new ArrayList<Street>();

    CustomFilter filter;

    public GridViewStreetAdapter(Context mContext, int layoutResourceId, ArrayList<Street> mGridData) {
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
        this.mGridDataSearch = mGridData;
    }
    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
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
        } else{
            Glide.with(mContext).load(R.drawable.colon).into(holder.imageView);
        }

        return row;
/*
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
*/
       /* LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (convertView==null){
            convertView=inflater.inflate(layoutResourceId, null);
        }
        TextView text = (TextView) convertView.findViewById(R.id.name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        Street item = mGridData.get(position);
        text.setText(Html.fromHtml(item.getNombre()));
        if (item.getRepresentativo()!="null"){
            Glide.with(mContext).load(item.getRepresentativo()).into(imageView);
        } else{
            Glide.with(mContext).load(R.drawable.colon).into(imageView);
        }
        return convertView;*/
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
   /* public void search(String text){
       *//* mGridDataSearch.clear();
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);*//*
        Toast.makeText(getContext(),"Texto de busqueda: " + text, Toast.LENGTH_LONG );
        if(TextUtils.isEmpty(text)){

        }
        *//*for (int i =0; i<mGridData.size();i++){
            Log.d(TAG, mGridData.get(i).getNombre().compareToIgnoreCase(text) + " ");
            if (mGridData.get(i).getNombre().compareToIgnoreCase(text)!=-1){
                mGridDataSearch.add(mGridData.get(i));
            }
        }
        this.clear();
        for (int i =0; i<mGridDataSearch.size();i++){
            add(mGridDataSearch.get(i));
        }
*//*
    }*/

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint!=null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<Street> filters = new ArrayList<Street>();
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

