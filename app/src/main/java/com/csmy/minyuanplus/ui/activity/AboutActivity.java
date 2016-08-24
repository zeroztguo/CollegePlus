package com.csmy.minyuanplus.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.VersionInfo;
import com.csmy.minyuanplus.support.adapter.DividerItemDecoration;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.service.DownloadService;
import com.google.gson.Gson;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.Bind;
import okhttp3.Call;

public class AboutActivity extends BaseActivity {
    @Bind(R.id.id_about_college_plus_rv)
    RecyclerView mCollegePlusRecyclerView;
    @Bind(R.id.id_about_copyright_rv)
    RecyclerView mCopyrightRecyclerView;
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;

    LinearLayoutManager mCollegePlusLayoutManager;
    private CommonAdapter<String> mCollegePlusAdapter;
    private String[] mCollegePlusTitles = {"应用简介", "检查更新", "意见反馈", "分享民院+"};

    LinearLayoutManager mCopyrightLayoutManager;
    private CommonAdapter<String> mCopyrightAdapter;
    private String[] mCopyrightTitles = {"教务系统和校闻数据", "课余页面数据", "开源协议"};
    private String[] mCopyrightContents = {"抓取自民政学院教务系统和官网，版权归学校所有", "数据来自于网上api，版权归原作者所有", ""};
    private String mVersionCode;
    private String mLatestVersionUrl;
    private String mUpdateMessage;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        initToolbar();
        initCollegePlusRecyclerView();
        initCopyrightRecyclerView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("关于");
    }

    private void initCollegePlusRecyclerView() {
        mCollegePlusLayoutManager = new LinearLayoutManager(this);
        mCollegePlusRecyclerView.setLayoutManager(mCollegePlusLayoutManager);
        mCollegePlusRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mCollegePlusAdapter = new CommonAdapter<String>(this, R.layout.item_setting_text, Arrays.asList(mCollegePlusTitles)) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }

            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_setting_text_title_actv, item);
                viewHolder.setVisible(R.id.id_setting_text_subtitle_actv, false);
            }
        };
        mCollegePlusAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                switch (position) {
                    case 0:
                        appIntroduce();
                        break;
                    case 1:
                        checkUpdate();
                        break;
                    case 2:
                        feedback();
                        break;
                    case 3:
                        shareApp();
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });
        mCollegePlusRecyclerView.setAdapter(mCollegePlusAdapter);
    }

    private void appIntroduce() {
        Intent intent = new Intent(AboutActivity.this, AppIntroduceActivity.class);
        startActivity(intent);
    }


    /**
     * 意见反馈
     */
    private void feedback() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("意见反馈");
        builder.setMessage("民院+现在属于初期版本，用起来不爽，数据获取失败，发现bug，或者有好的idea，都欢迎给作者提供建议。\n \n 方式：\n 1.email:zeroztguo@163.com \n 2.在作者大学城空间留言建议");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 分享民院+
     */
    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "欢迎使用【民院+】，它是一款民院师生专属的应用。它集课表、成绩查询，民院新闻，课外新闻，校园信息等功能于一体，希望它能给你的民院生活，带来些许便利。你可以到~~~下载");
        startActivity(Intent.createChooser(shareIntent, "分享至"));
    }

    private void checkUpdate() {
        showWaitDialog(this);
        OkHttpUtils
                .get()
                .url("https://coding.net/u/zeroztguo/p/CollegePlus/git/raw/master/latestVersionInfo.txt")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        dismissWaitDialog();
                        ToastUtil.show("获取更新信息失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        dismissWaitDialog();

                        Gson gson = new Gson();
                        VersionInfo versionInfo = gson.fromJson(response, VersionInfo.class);
                        mVersionCode = versionInfo.getVersionCode().trim();
                        mLatestVersionUrl = versionInfo.getLatestVersionUrl().trim();
                        mUpdateMessage = versionInfo.getUpdateMessage().trim();
                        showUpdateDialog(mLatestVersionUrl);

                        if (Integer.parseInt(mVersionCode) > Util.getVersionCode()) {
                        } else {
                            ToastUtil.show("当前已是最新版本");
                        }
                    }
                });
    }


    /**
     * 显示提示更新的dialog
     *
     * @param updateUrl 最新版本apk下载地址
     */
    private void showUpdateDialog(final String updateUrl) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(mUpdateMessage);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AboutActivity.this, DownloadService.class);
                intent.putExtra("update_url", updateUrl);
                startService(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void downloadApk() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder
                .setContentTitle("正在下载民院+")
//                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                .build();
        manager.notify(1, notification);
    }

    private void initCopyrightRecyclerView() {
        mCopyrightLayoutManager = new LinearLayoutManager(this);
        mCopyrightRecyclerView.setLayoutManager(mCopyrightLayoutManager);
        mCopyrightRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mCopyrightAdapter = new CommonAdapter<String>(this, R.layout.item_setting_text, Arrays.asList(mCopyrightTitles)) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }

            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_setting_text_title_actv, item);
                viewHolder.setText(R.id.id_setting_text_subtitle_actv, mCopyrightContents[position]);
            }
        };
        mCopyrightRecyclerView.setAdapter(mCopyrightAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        if (eventModel.getEventCode() == Event.DOWNLOAD_PROGRESS_UPDATE) {
            ToastUtil.show("进度更新啦~~~~:" + eventModel.getData().toString());
        }
    }
}
