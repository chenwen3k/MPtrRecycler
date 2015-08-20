package com.m.mptrrecycler.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.m.mptrrecycler.RefreshRecycleView;

/**
 * Created by mfw on 15/8/19.
 */
public abstract class BaseRecyclerActivity extends Activity {
    private RefreshRecycleView refreshRecycleView;
    private View emptyView;
    private View loadingView;

    abstract protected View getEmptyView();

    abstract protected View getLoadingView();

    abstract protected RefreshRecycleView getRecycleView();

    abstract protected void onRefresh();
    abstract protected void onLoadMore();

    protected void onRefreshFinish() {
        refreshRecycleView.refreshComplete();
        RecyclerView.Adapter adapter = refreshRecycleView.getAdapter();
        if (adapter == null) {
            return;
        }
        if (adapter.getItemCount() == 0) {
            refreshRecycleView.showEmptyView(emptyView);
        } else {
            refreshRecycleView.showRecycler();
        }
    }

    protected void onLoadMoreFinish() {
        refreshRecycleView.stopLoadMore();
    }

    abstract protected void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        refreshRecycleView = getRecycleView();
        emptyView = getEmptyView();
        loadingView = getLoadingView();
        refreshRecycleView.showLoadingView(loadingView);
        refreshRecycleView.setOnRefreshAndLoadMoreListener(new RefreshRecycleView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                BaseRecyclerActivity.this.onRefresh();
            }

            @Override
            public void onLoadMore() {
                BaseRecyclerActivity.this.onLoadMore();
            }
        });
        BaseRecyclerActivity.this.onRefresh();
    }
}
