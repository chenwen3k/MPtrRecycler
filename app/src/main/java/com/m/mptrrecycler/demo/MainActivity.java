package com.m.mptrrecycler.demo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m.mptrrecycler.MRefreshAdapter;
import com.m.mptrrecycler.RefreshRecycleView;
import com.m.mptrrecycler.util.DPIUtil;


public class MainActivity extends BaseRecyclerActivity {
    private static final int FLAG_REFRESH = 0;
    private static final int FLAG_LOAD_MORE = 1;
    private TextAdapter adapter;
    private RefreshRecycleView refreshRecycleView;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        refreshRecycleView = (RefreshRecycleView) findViewById(R.id.refresh_recycler_view);
        refreshRecycleView.setLayoutManager(new LinearLayoutManager(this));
        refreshRecycleView.setEnablePullToRefresh(true);
        adapter = new TextAdapter(this);
        refreshRecycleView.setAdapter(adapter);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.addData();
            switch (msg.what) {
                case FLAG_REFRESH:
                    // this is a mock, usually, this is called when get a response from api.
                    onRefreshFinish();
                    break;
                case FLAG_LOAD_MORE:
                    onLoadMoreFinish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected View getEmptyView() {
        TextView textView = new TextView(MainActivity.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText("Oops, no data here...");
        return textView;
    }

    @Override
    protected View getLoadingView() {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText("loading...");
        return textView;
    }

    @Override
    protected RefreshRecycleView getRecycleView() {
        return refreshRecycleView;
    }

    @Override
    protected void onRefresh() {
        handler.sendEmptyMessageDelayed(FLAG_REFRESH, 1500);
    }

    @Override
    protected void onLoadMore() {
        handler.sendEmptyMessageDelayed(FLAG_LOAD_MORE, 1500);
    }

    public static class TextAdapter extends MRefreshAdapter<TextHolder> {
        private int refreshTime = 0;
        private Context mContext;

        public TextAdapter(Context context) {
            super(context);
            this.mContext = context;
        }

        @Override
        public PullToRefreshViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {

            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DPIUtil.dip2px(mContext, 50)));
            return new TextHolder(textView);
        }

        @Override
        public void onBindContentViewHolder(TextHolder pullToRefreshViewHolder, int position) {
            pullToRefreshViewHolder.textView.setText("第" + position + "项");
        }

        @Override
        public int getContentItemViewType(int position) {
            return 0;
        }

        @Override
        public int getContentItemCount() {
            return 15 * refreshTime;
        }

        public void addData() {
            refreshTime++;
            notifyDataSetChanged();
        }
    }

    public static class TextHolder extends MRefreshAdapter.PullToRefreshViewHolder {

        protected TextView textView;

        public TextHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }
}
