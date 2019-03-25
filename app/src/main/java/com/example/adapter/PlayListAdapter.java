package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.youtubechannel.R;
import com.app.youtubechannel.YtPlayActivity;
import com.example.item.YtChannelItem;
import com.example.util.OnLoadMoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ItemRowHolder> {

    private ArrayList<YtChannelItem> dataList;
    private Context mContext;
    private final int VIEW_ITEM = 1;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public PlayListAdapter(Context context, ArrayList<YtChannelItem> dataList, RecyclerView recyclerView) {
        this.dataList = dataList;
        this.mContext = context;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_playlist, parent, false);
            return new ContentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_ITEM) {
            ContentViewHolder holder = (ContentViewHolder) viewHolder;
            final YtChannelItem singleItem = dataList.get(position);
            holder.text.setText(singleItem.getChannelName());
            Picasso.with(mContext).load(singleItem.getChannelImageurl()).into(holder.image);
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, YtPlayActivity.class);
                    intent.putExtra("id", singleItem.getChannelId());
                    mContext.startActivity(intent);
                }
            });
            holder.imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, \n" + singleItem.getChannelName() + "\nhttps://www.youtube.com/watch?v=" + singleItem.getChannelId());
                    sendIntent.setType("text/plain");
                    mContext.startActivity(sendIntent);
                }
            });
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return dataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ItemRowHolder(View itemView) {
            super(itemView);
        }
    }

    class ContentViewHolder extends ItemRowHolder {
        ImageView image, imageShare;
        TextView text;
        LinearLayout lyt_parent;

        ContentViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            imageShare = itemView.findViewById(R.id.imageShare);
        }
    }

    class ProgressViewHolder extends ItemRowHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }
}
