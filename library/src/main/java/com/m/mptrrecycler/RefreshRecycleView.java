package com.m.mptrrecycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by mfw on 15/8/18.
 */
public class RefreshRecycleView extends FrameLayout {
    private final static String TAG = RefreshRecycleView.class.getSimpleName();
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px at bottom, trigger load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull feature.
    private final static int SCROLL_DURATION = 400; // scroll back duration

    private RecyclerView mRecyclerView;
    private PtrClassicFrameLayout mPtrFrameLayout;
    private LinearLayout mStatusView;
    private OnRefreshAndLoadMoreListener onRefreshAndLoadMoreListener;
    private MRefreshAdapter adapter;
    private MRecyclerFooter footerView;
    private Scroller mScroller; // used for scroll back

    private boolean enablePullToRefresh = false;
    private boolean mEnablePullLoad = true;
    private boolean mPullLoading;

    private float mLastY = -1; // save event y

    public RefreshRecycleView(Context context) {
        super(context);
        initViews();
    }

    public RefreshRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public RefreshRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    protected void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_view_layout, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_list);
        mPtrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.recyclerview_ptr_frame);
        mStatusView = (LinearLayout) findViewById(R.id.status_view);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(400);
        // header
        final MRecyclerHeader header = new MRecyclerHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View view, View header) {
                return enablePullToRefresh && !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                if (onRefreshAndLoadMoreListener != null) {
                    onRefreshAndLoadMoreListener.onRefresh();
                }
            }
        });
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent");
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        RecyclerViewPositionHelper helper = RecyclerViewPositionHelper.createHelper(mRecyclerView);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
//                if (getLastVisiblePosition() == mTotalItemCount - 1
                if (helper.findLastVisibleItemPosition() == adapter.getItemCount() - 1
                        && (footerView.getBottomMargin() > 0 || deltaY < 0) && mEnablePullLoad) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                Log.d(TAG, (helper.findLastVisibleItemPosition() == adapter.getItemCount() - 1) + ", " + footerView.getBottomMargin());
                if (helper.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // step 1. show loading view before fetch data.
    public void showLoadingView(View loadingView) {
        if (loadingView == null) {
            return;
        }
        mStatusView.setVisibility(VISIBLE);
        mStatusView.removeAllViews();
        mStatusView.addView(loadingView);
        mPtrFrameLayout.setVisibility(GONE);
    }

    // step 2. notify ptr refresh complete when request come back.
    public void refreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    // step 3. show empty view, if data is empty.
    public void showEmptyView(View emptyView) {
        if (emptyView == null) {
            return;
        }
        mStatusView.setVisibility(VISIBLE);
        mStatusView.removeAllViews();
        mStatusView.addView(emptyView);
        mPtrFrameLayout.setVisibility(GONE);
    }

    // step 4. show recycler, if data is not empty.
    public void showRecycler() {
        mPtrFrameLayout.setVisibility(VISIBLE);
        mStatusView.setVisibility(GONE);
    }

    public void setEnablePullToRefresh(boolean enablePullToRefresh) {
        this.enablePullToRefresh = enablePullToRefresh;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setAdapter(MRefreshAdapter adapter) {
        this.adapter = adapter;
        mRecyclerView.setAdapter(adapter);
        footerView = new MRecyclerFooter(getContext());
        adapter.setFooterView(footerView);
    }

    public void setOnRefreshAndLoadMoreListener(OnRefreshAndLoadMoreListener listener) {
        this.onRefreshAndLoadMoreListener = listener;
    }

    public MRefreshAdapter getAdapter() {
        return adapter;
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }
        return min;
    }

    private void updateFooterHeight(float delta) {
        Log.d(TAG, "updateFooterHeight");
        int height = (int) (footerView.getBottomMargin() + delta);
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load more.
                footerView.setState(MRecyclerFooter.STATE_READY);
            } else {
                footerView.setState(MRecyclerFooter.STATE_NORMAL);
            }
        }
        footerView.setBottomMargin(height);

//		setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = footerView.getBottomMargin();
        if (bottomMargin > 0) {
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        footerView.setState(MRecyclerFooter.STATE_LOADING);
        if (onRefreshAndLoadMoreListener != null) {
            onRefreshAndLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            footerView.setBottomMargin(mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            footerView.setState(MRecyclerFooter.STATE_NORMAL);
        }
    }

    public interface OnRefreshAndLoadMoreListener {
        public void onRefresh();
        public void onLoadMore();
    }
}
