package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.support.util.NetworkType;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 课外基类,弃用
 * Created by Zero on 16/7/27.
 */
public abstract class AfterClassSwipeRereshFragment<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * 被下拉的RecyclerView
     */
    @Bind(R.id.id_after_class_swipe_refresh_rv)
    RecyclerView mRecyclerView;

    /**
     * 下拉刷新布局
     */
    @Bind(R.id.id_after_class_swipe_refresh_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * 布局管理
     */
    private LinearLayoutManager mLayoutManager;
    /**
     * 适配器
     */
    private MultiItemTypeAdapter<T> mAdapter;
    /**
     * 数据源
     */
    protected List<T> mDatas;
    /**
     * 最后一个可见的item
     */
    private int mLastVisibleItem;
    /**
     * 是否刷新的变量，用来更新进度条状态
     */
    private boolean isRefresh = false;


    /**
     * 初始化的方法，被子类调用
     */
    protected void init() {

        mDatas = new ArrayList<>();


        /**
         * 初始化SwipeRefreshLayout
         */
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE
//                        &&mLastVisibleItem +1 == mAdapter.getItemCount()){
//                    mSwipeRefreshLayout.setRefreshing(true);
//                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                //不能再往上滑动，上拉刷新
                if (!recyclerView.canScrollVertically(-1)
                        && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    refresh();
                }
                //不能再往下滑动，下拉加载
                else if (!recyclerView.canScrollVertically(1)) {

                }
            }
        });


        /**
         * 初始化RecyclerView
         */
        mAdapter = new AfterClassAdapter(getContext(), mDatas);

        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener<T>() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, T t, int position) {
                setOnItemClick(t);

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, T t, int position) {
                return false;
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        loadFromDatabase();

    }


    private void loadFromNet() {
        //网络不可用，提示。可用，刷新新闻
        if (Util.getNetWorkType() != NetworkType.NETWORKTYPE_INVAILD) {
            refresh();
            Logger.d("开始从网络获取新闻数据。。。。。。。。。");
        }
    }

    @Override
    protected void loadData() {
        super.loadData();
        loadFromNet();
    }

    /**
     * 实现懒加载，当用户界面变得可见时从网络加载数据
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            loadData();
        }
    }

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_after_class_swipe_refresh;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getUserVisibleHint()) {
            loadData();
        }
    }


    /**
     * 从本地数据库读取
     */
    protected abstract void loadFromDatabase();


    /**
     * 设置item点击事件
     *
     * @param t
     */
    protected abstract void setOnItemClick(T t);


    /**
     * 刷新的处理方法
     */
    protected abstract void refresh();


    protected void addAllData(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 设置刷新状态
     */
    protected void setRefresh() {
        isRefresh = !isRefresh;
        mSwipeRefreshLayout.setRefreshing(isRefresh);
        Logger.d("下拉刷新状态：" + isRefresh);
    }


    @Override
    public void onRefresh() {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    protected abstract ItemViewDelegate<T> getFirstItemViewDelegate();

    protected abstract ItemViewDelegate<T> getSecondItemViewDelegate();


    private class AfterClassAdapter extends MultiItemTypeAdapter<T> {

        public AfterClassAdapter(Context context, List<T> datas) {
            super(context, datas);

            addItemViewDelegate(getFirstItemViewDelegate());
            addItemViewDelegate(getSecondItemViewDelegate());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
            AutoUtils.autoSize(viewHolder.getConvertView());
            return viewHolder;
        }
    }
}
