package com.csmy.minyuanplus.ui.fragment.collegenews;

import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.collegenews.CollegeNews;
import com.csmy.minyuanplus.model.collegenews.NewsBean;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.MyNewsActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Zero on 16/7/23.
 */
public class CollegeNewsFragment extends SwipeRereshFragment<CollegeNews> {
//    @Bind(R.id.id_college_news_rv)
//    RecyclerView mCollegeNewsRecyclerView;
//    @Bind(R.id.id_college_news_srl)
//    SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPage;



    public static CollegeNewsFragment newInstance() {


        return new CollegeNewsFragment();
    }


    public static final String COLLEGE_NEWS = "http://web.csmzxy.com/netCourse/readData";
    private static final String SHARE_URL = "http://www.csmzxy.com/sub2.html?content,";


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    private void obtainCollegeNewsList() {
        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(COLLEGE_NEWS)
                .addParams("cmd", "7")
                .addParams("v1", "274")
                .addParams("v2", "1")
                .addParams("v3", "15")
                .addParams("tempData", new Date().toString())
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show("获取院部动态失败" + e.getMessage());
                        OkHttpUtils.getInstance().cancelTag(this);
                        setRefresh();
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type it = new TypeToken<List<CollegeNews>>() {
                        }.getType();
                        List<CollegeNews> CollegeNewsList = gson.fromJson(response, it);

                        DataSupport.deleteAll(CollegeNews.class);
                        DataSupport.saveAll(CollegeNewsList);

//                        for (CollegeNews cd : CollegeNewsList) {
//                            addData(cd);
//                        }
                        addAllData(CollegeNewsList);
                        Logger.d("一共有这么多条：" + CollegeNewsList.size());
                        setRefresh();
                        setLoadMore();
                        mPage = 1;
                    }
                });
    }

    private void loadMoreCollegeNewsList(int page) {
        Logger.d("加载更多ing...");
        OkHttpUtils
                .get()
                .url(COLLEGE_NEWS)
                .addParams("cmd", "7")
                .addParams("v1", "274")
                .addParams("v2", page+"")
                .addParams("v3", "15")
                .addParams("tempData", new Date().toString())
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show("获取院部动态失败" + e.getMessage());
                        OkHttpUtils.getInstance().cancelTag(this);
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type it = new TypeToken<List<CollegeNews>>() {
                        }.getType();
                        List<CollegeNews> CollegeNewsList = gson.fromJson(response, it);

                        DataSupport.saveAll(CollegeNewsList);

                        updateAllData(CollegeNewsList);
                        Logger.d("一共有这么多条：" + CollegeNewsList.size());
                    }
                });
    }



    @Override
    protected int getItemId() {
        return R.layout.item_college_news;
    }

    @Override
    protected void loadFromDatabase() {
        List<CollegeNews> CollegeNewsList = DataSupport.findAll(CollegeNews.class);
        addAllData(CollegeNewsList);
    }

    @Override
    protected void setItem(ViewHolder holder, CollegeNews cd, int position) {
        AppCompatTextView title = holder.getView(R.id.id_college_news_title_actv);
        AutoUtils.autoTextSize(title);
        title.setText(cd.getContentTitle());

//        holder.setText(R.id.id_college_news_title_actv, cd.getContentTitle());
        holder.setText(R.id.id_college_news_author_actv, cd.getContentAuthor());
        holder.setText(R.id.id_college_news_date_actv, cd.getSubmitTime());
    }

    @Override
    protected void setOnItemClick(CollegeNews cn) {
        Intent intent = new Intent(getHoldingActivity(), MyNewsActivity.class);
        NewsBean newsBean = new NewsBean();
        newsBean.setContentTitle(cn.getContentTitle())
                .setContentAuthor(cn.getContentAuthor())
                .setContentID(cn.getContentID())
                .setSubmitTime(cn.getSubmitTime())
                .setShareUrl(SHARE_URL+cn.getContentID());

        intent.putExtra("college_news", newsBean);
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh();
        mPage = 1;
        obtainCollegeNewsList();
    }

    @Override
    protected void loadMore() {
        mPage++;
        loadMoreCollegeNewsList(mPage);
    }

//    @Override
//    protected void initView(View view, Bundle saveInstanceState) {
//        init();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_college_news;
//    }
}