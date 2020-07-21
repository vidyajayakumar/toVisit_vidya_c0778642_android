package com.vidya.toVisit_vidya_c0778642_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.vidya.toVisit_vidya_c0778642_android.networking.Favourites;

import java.util.ArrayList;

public
class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "recyclerAdapter";
    private final CustomAdapterClickListener clickListener;
    dbHelper mDatabase;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Favourites> listFavourites;
    private ArrayList<Favourites> mArrayList;

    public
    recyclerAdapter(Context context, ArrayList<Favourites> favouritesArrayList, final CustomAdapterClickListener clickListener) {
        this.context        = context;
        inflater            = LayoutInflater.from(context);
        this.listFavourites = favouritesArrayList;
        this.mArrayList     = favouritesArrayList;
        this.clickListener  = clickListener;
        mDatabase           = new dbHelper(context);
    }

    @Override
    public
    recyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View         view   = inflater.inflate(R.layout.rv_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public
    void onBindViewHolder(final recyclerAdapter.MyViewHolder holder, int position) {
        final Favourites favourites = listFavourites.get(position);
        holder.address.setText(favourites.getFavAddress());
        holder.visited.setChecked(favourites.isFavVisited());
        //TODO add smth here

        holder.visited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                favourites.setFavVisited(holder.visited.isChecked());
                mDatabase.updateFavVisited(new Favourites(favourites.get_id(), holder.visited.isChecked()));
            }
        });
    }

    @Override
    public
    int getItemCount() {
        return listFavourites.size();
    }

    @Override
    public
    Filter getFilter() {
        return null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        CheckBox visited;

        public
        MyViewHolder(View itemView) {
            super(itemView);
            address   = (TextView) itemView.findViewById(R.id.tv_name);
            visited   = (CheckBox) itemView.findViewById(R.id.rb_visit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public
                void onClick(View v) {
                    if (clickListener == null) {
                        int elementId = listFavourites.get(getAdapterPosition()).get_id(); // Get the id of the item on that position
//                        clickListener.onItemClick( elementId); // we catch the id on the item view then pass it over the interface and then to our activity

//                    clickListener.onItemClick(v, (int) v.getTag());
                    Log.i(TAG, "onClick: element "+elementId);}
                    MapActivity.dialog.dismiss();
                }
            });
        }
    }

}