package com.m.mptrrecycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mfw on 14-11-7.
 */
public abstract class MRefreshAdapter<T extends MRefreshAdapter.PullToRefreshViewHolder> extends RecyclerView.Adapter<MRefreshAdapter.PullToRefreshViewHolder> {

    private static final int FOOT_VIEW_TYPE = -1;
    private View mFooterView;
    Context mContext;

    /**
     * --------------------
     * |      Container     | size - 1
     * --------------------
     * |   Refresh footer   | size
     * --------------------
     */
    public MRefreshAdapter(Context context) {
        mContext = context;
    }

    public abstract PullToRefreshViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindContentViewHolder(T pullToRefreshViewHolder, int position);

    public abstract int getContentItemViewType(int position);

    public abstract int getContentItemCount();

    @Override
    final public PullToRefreshViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (FOOT_VIEW_TYPE == viewType) {
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mFooterView.setLayoutParams(layoutParams);
            return new PullToRefreshViewHolder(mFooterView);
        }
        return onCreateContentViewHolder(parent, viewType);
    }

    @Override
    final public void onBindViewHolder(PullToRefreshViewHolder pullToRefreshViewHolder, int position) {
        if (mFooterView != null && position >= getContentItemCount()) {
            return;
        }
        onBindContentViewHolder((T) pullToRefreshViewHolder, position);
    }

    @Override
    final public long getItemId(int i) {
        return i;
    }

    void setFooterView(View view) {
        mFooterView = view;
    }

    @Override
    public int getItemCount() {
        int itemCount = getContentItemCount();
        if (mFooterView != null) {
            itemCount++;
        }
        return itemCount;
    }

    @Override
    final public int getItemViewType(int position) {
        if (mFooterView != null && position >= getItemCount() - 1) {
            return FOOT_VIEW_TYPE;
        }
        return getContentItemViewType(position);
    }

    public static class PullToRefreshViewHolder extends RecyclerView.ViewHolder {

        public PullToRefreshViewHolder(View itemView) {
            super(itemView);
        }
    }
}
