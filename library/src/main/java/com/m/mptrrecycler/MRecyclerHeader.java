/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.m.mptrrecycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m.mptrrecycler.util.DateTimeUtils;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class MRecyclerHeader extends LinearLayout implements PtrUIHandler {
    private LinearLayout mContainer;
    private TextView mHintTextView;
    private TextView refreshTimeView;
    private int mState = STATE_NORMAL;
    private ProgressBar progressView;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    private long refreshTime;

    public MRecyclerHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public MRecyclerHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.xlistview_header, null, false);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
        refreshTimeView = (TextView) findViewById(R.id.xlistview_header_time);
        progressView = (ProgressBar) findViewById(R.id.progressView);
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {

        if (state == mState) return;

        refreshTimeView.setText(DateTimeUtils.getRefreshTimeText(refreshTime));
//		if (state == STATE_REFRESHING) {	// 显示进度
//			mArrowImageView.clearAnimation();
//			mArrowImageView.setVisibility(View.INVISIBLE);
//			mProgressBar.setVisibility(View.VISIBLE);
//		} else {	// 显示箭头图片
//			mArrowImageView.setVisibility(View.VISIBLE);
//			mProgressBar.setVisibility(View.INVISIBLE);
//		}

        switch (state) {
            case STATE_NORMAL:
//			if (mState == STATE_READY) {
//				mArrowImageView.startAnimation(mRotateDownAnim);
//			}
//			if (mState == STATE_REFRESHING) {
//				mArrowImageView.clearAnimation();
//			}
                mHintTextView.setText(R.string.xlistview_header_hint_normal);
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
//				mArrowImageView.clearAnimation();
//				mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.xlistview_header_hint_ready);
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText(R.string.xlistview_header_hint_loading);
                break;
            default:
        }

        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer
                .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }


    public void setRefreshTime(long time) {
        refreshTime = time;
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        setState(STATE_NORMAL);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        setState(STATE_READY);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        setState(STATE_REFRESHING);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        setState(STATE_NORMAL);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
    }
}
