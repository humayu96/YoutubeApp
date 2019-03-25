package com.example.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.youtubechannel.ChannelActivity;
import com.app.youtubechannel.R;
import com.example.db.DatabaseHelper;
import com.example.item.ItemChannel;
//import com.example.util.PopUpAds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ItemRowHolder> {

    private ArrayList<ItemChannel> dataList;
    private Context mContext;
    private boolean isVisible;
    private DatabaseHelper databaseHelper;

    public ChannelAdapter(Context context, ArrayList<ItemChannel> dataList, boolean flag) {
        this.dataList = dataList;
        this.mContext = context;
        this.isVisible = flag;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemChannel singleItem = dataList.get(position);
        holder.text.setText(singleItem.getPlayListName());
        Picasso.with(mContext).load(singleItem.getImage()).placeholder(R.drawable.placeholer).into(holder.image);
        if (isVisible) {
            holder.imageFavourite.setVisibility(View.VISIBLE);
            if (databaseHelper.getFavouriteById(String.valueOf(singleItem.getId()))) {
                holder.imageFavourite.setImageResource(R.drawable.ic_favourite_hover);
            } else {
                holder.imageFavourite.setImageResource(R.drawable.ic_favourite_1);
            }

            holder.imageFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues fav = new ContentValues();
                    if (databaseHelper.getFavouriteById(String.valueOf(singleItem.getId()))) {
                        databaseHelper.removeFavouriteById(String.valueOf(singleItem.getId()));
                        holder.imageFavourite.setImageResource(R.drawable.ic_favourite_1);
                        Toast.makeText(mContext, mContext.getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                    } else {
                        fav.put(DatabaseHelper.KEY_ID, String.valueOf(singleItem.getId()));
                        fav.put(DatabaseHelper.KEY_TITLE, singleItem.getPlayListName());
                        fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getImage());
                        fav.put(DatabaseHelper.KEY_PLAYLIST, singleItem.getPlayListUrl());
                        databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                        holder.imageFavourite.setImageResource(R.drawable.ic_favourite_hover);
                        Toast.makeText(mContext, mContext.getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PopUpAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, ChannelActivity.class);
                intent.putExtra("Id", singleItem.getPlayListUrl());
                intent.putExtra("name", singleItem.getPlayListName());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, imageFavourite;
        TextView text;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageFavourite = itemView.findViewById(R.id.imageFavourite);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
        }
    }
}
