package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.afterclass.Guokr;
import com.csmy.minyuanplus.model.afterclass.GuokrHeader;
import com.csmy.minyuanplus.ui.activity.GuokrActivity;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Zero on 16/7/23.
 */
public class GuokrFragment extends AfterClassSwipeRereshFragment<GuokrHeader> {


    public static GuokrFragment newInstance() {
        return new GuokrFragment();
    }


    public static final String GUOKE = "http://www.guokr.com/apis/minisite/article.json?retrieve_type=by_channel&channel_key=hot";


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    @Override
    protected ItemViewDelegate<GuokrHeader> getFirstItemViewDelegate() {
        ItemViewDelegate<GuokrHeader> firstItemViewDelegate = new ItemViewDelegate<GuokrHeader>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_daily_text_image;
            }

            @Override
            public boolean isForViewType(GuokrHeader item, int position) {
                return !Util.isStringNull(item.getSmall_image());
            }

            @Override
            public void convert(ViewHolder holder, GuokrHeader GuokeHeader, int position) {
                /*
                设置标题
                 */
                AppCompatTextView textView = holder.getView(R.id.id_daily_title);
                textView.setText(GuokeHeader.getTitle());

                /*
                设置小图片
                 */
                SimpleDraweeView draweeView = holder.getView(R.id.id_daily_image);
                Uri uri = Uri.parse(GuokeHeader.getSmall_image());
                draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                draweeView.setImageURI(uri);
            }
        };
        return firstItemViewDelegate;
    }

    @Override
    protected ItemViewDelegate<GuokrHeader> getSecondItemViewDelegate() {
        ItemViewDelegate<GuokrHeader> secondItemViewDelegate = new ItemViewDelegate<GuokrHeader>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_daily_text;
            }

            @Override
            public boolean isForViewType(GuokrHeader item, int position) {
                return Util.isStringNull(item.getSmall_image());
            }

            @Override
            public void convert(ViewHolder holder, GuokrHeader GuokeHeader, int position) {
                /*
                设置标题
                 */
                AppCompatTextView textView = holder.getView(R.id.id_daily_text_title);
                AutoUtils.autoTextSize(textView);
                textView.setText(GuokeHeader.getTitle());
            }
        };
        return secondItemViewDelegate;
    }


    private void obtainGuokeHeaderList() {

        OkHttpUtils
                .get()
                .url(GUOKE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show("获取果壳失败" + e.getMessage());
                        setRefresh();
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Guokr guoke = gson.fromJson(response, Guokr.class);
                        List<GuokrHeader> guokrHeaderList = guoke.getResult();


                        DataSupport.deleteAll(GuokrHeader.class);
                        DataSupport.saveAll(guokrHeaderList);


                        Logger.d("果壳有：" + guokrHeaderList.size());
//
                        addAllData(guokrHeaderList);
                        setRefresh();
                    }
                });
    }


    @Override
    protected void loadFromDatabase() {
        List<GuokrHeader> guokrHeaderList = DataSupport.findAll(GuokrHeader.class);
        addAllData(guokrHeaderList);
    }


    @Override
    protected void setOnItemClick(GuokrHeader GuokrHeader) {
        Intent intent = new Intent(getHoldingActivity(), GuokrActivity.class);
        intent.putExtra("guokr", GuokrHeader.getResource_url());
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh();
        obtainGuokeHeaderList();
    }


}
