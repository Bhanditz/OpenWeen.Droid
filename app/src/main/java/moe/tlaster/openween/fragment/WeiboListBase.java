package moe.tlaster.openween.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import moe.tlaster.openween.R;
import moe.tlaster.openween.common.SimpleDividerItemDecoration;
import moe.tlaster.openween.common.controls.Pivot;
import moe.tlaster.openween.common.helpers.JsonCallback;
import okhttp3.Call;

/**
 * Created by Tlaster on 2016/9/10.
 */
public abstract class WeiboListBase<T> extends Pivot.PivotItemFragment {

    protected interface Callback<T> {
        void onError(Exception e);
        void onResponse(T response, int totalCount);
    }

    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected BaseQuickAdapter<T> mAdapter;
    private int mTotalCount = 0;
    protected int mLoadCount = 20;
    protected int mPage = 1;
    protected int getContentView() {
        return R.layout.list_layout;
    }
    protected int getRecyclerView() {
        return R.id.recyclerView;
    }
    protected int getSwipeRefreshLayout() {
        return R.id.refresher;
    }
    protected RecyclerView.LayoutManager getLayoutManager(){
        return new LinearLayoutManager(getContext());
    }
    protected abstract BaseQuickAdapter<T> initAdapter();
    protected abstract RecyclerView.OnItemTouchListener itemTouch();

    private boolean HasMore(){
        return mAdapter.getData().size() < mTotalCount;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        mRecyclerView = (RecyclerView) view.findViewById(getRecyclerView());
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(getSwipeRefreshLayout());
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = initAdapter();
        mAdapter.openLoadAnimation();
        mAdapter.openLoadMore(20);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> mRecyclerView.post(this::loadMore));
        mRecyclerView.addOnItemTouchListener(itemTouch());
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mSwipeRefreshLayout.post(()-> mSwipeRefreshLayout.setRefreshing(true));
        refresh();
        return view;
    }


    private void loadMore() {
        if (!HasMore()) {
            mAdapter.loadComplete();
            mAdapter.addFooterView(getLayoutInflater(null).inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false));
            return;
        }
        loadMoreOverride(new Callback<List<T>>() {
            @Override
            public void onError(Exception e) {
                //mAdapter.showLoadMoreFailedView();//TODO:Show refresh failed
            }

            @Override
            public void onResponse(List<T> response, int totalCount) {
                mAdapter.addData(response);
                mTotalCount = totalCount;
            }
        });
    }

    private void refresh() {
        mPage = 1;
        refreshOverride(new Callback<List<T>>() {
            @Override
            public void onError(Exception e) {
                //mAdapter.showLoadMoreFailedView();//TODO:Show refresh failed
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(List<T> response, int totalCount) {
                mAdapter.setNewData(response);
                mTotalCount = totalCount;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    protected abstract void loadMoreOverride(Callback<List<T>> callback);
    protected abstract void refreshOverride(Callback<List<T>> callback);
}