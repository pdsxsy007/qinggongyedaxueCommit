package io.cordova.zhqy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.adapter.MessageAdapter;
import io.cordova.zhqy.adapter.MyAdapter;
import io.cordova.zhqy.bean.MessageBean;
import io.cordova.zhqy.bean.OAMsgListBean;
import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.CircleCrop;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.T;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;

/**
 * Created by Administrator on 2019/2/22 0022.
 */

public class OaMsgActivity extends BaseActivity2  {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_msg_list)
    RecyclerView rvMsgList;

    @BindView(R.id.swipeLayout)
    SmartRefreshLayout mSwipeLayout;


    @BindView(R.id.rl_empty)
    RelativeLayout rl_empty;

    private MessageAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    String type,msgType;
    private int num = 1;
    @BindView(R.id.header)
    ClassicsHeader header;
    private int size = 0;
    private int lastSize = -1;
    @Override
    protected int getResourceId() {
        return R.layout.oa_msg_activity;
    }

    @Override
    protected void initView() {
        super.initView();
        type = getIntent().getStringExtra("type");
        msgType = getIntent().getStringExtra("msgType");
        Log.e("type = ",type);
        tvTitle.setText(msgType);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        rvMsgList.setLayoutManager(mLinearLayoutManager);
        ViewUtils.createLoadingDialog(this);
        netWorkOaMsgList();

        header.setEnableLastTime(false);
        registerBoradcastReceiver();
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refreshOaMessage");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("refreshOaMessage")){
                String state = intent.getStringExtra("state");
                if(state.equals("0")){
                    num = 1;
                    netWorkOaMsgList();
                }else {
                    mLinearLayoutManager.scrollToPositionWithOffset(firstItemPosition, 0);
                }
               /* num = 1;
                netWorkSysMsgList();*/
                //adapter.notifyDataSetChanged();

            }
        }
    };


    int lastItemPosition;
    int firstItemPosition;

    @Override
    protected void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                num = 1;
                netWorkSysMsgListOnRefresh(refreshlayout);
            }
        });

        mSwipeLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore( RefreshLayout refreshlayout) {
                netWorkSysMsgListOnLoadMore(refreshlayout);

            }
        });

        rvMsgList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置

                    lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置

                    firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    Log.e("lastItemPosition------",lastItemPosition+"");
                    Log.e("firstItemPosition------",firstItemPosition+"");
                }
            }
        });

    }

    private void netWorkSysMsgListOnLoadMore(final RefreshLayout refreshlayout) {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.getBacklogUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("pageNum", num)
                .params("pageSize", 15)
                .params("messageType",type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("num",num+"");
                        Log.e("详情列表查看更多",response.body());
                        MessageBean messageBeanAll = JSON.parseObject(response.body(), MessageBean.class);

                        if (messageBeanAll.getSuccess()) {
                            List<MessageBean.Obj> objMore = messageBeanAll.getObj();
                            if(objMore.size() > 0){
                                messageBean.getObj().addAll(objMore);
                                //adapter = new MessageAdapter(OaMsgActivity.this,R.layout.item_to_do_my_msg,messageBean.getObj());
                                //rvMsgList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                num += 1;
                                refreshlayout.finishLoadmore();
                            }else {
                                ToastUtils.showToast(OaMsgActivity.this,"暂无更多数据!");
                                refreshlayout.finishLoadmore();
                            }

                        }else {
                            refreshlayout.finishLoadmore();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        refreshlayout.finishLoadmore();

                    }
                });

    }

    private void netWorkSysMsgListOnRefresh(final RefreshLayout refreshlayout) {
        num = 1;
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.getBacklogUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("pageNum", num)
                .params("pageSize", 15)
                .params("messageType",type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("刷新数据",response.body());
                        messageBean = JSON.parseObject(response.body(), MessageBean.class);
                        if (messageBean.getSuccess()) {
                            adapter = new MessageAdapter(OaMsgActivity.this,R.layout.item_to_do_my_msg,messageBean.getObj());
                            rvMsgList.setAdapter(adapter);
                            num = 2;
                            mSwipeLayout.setVisibility(View.VISIBLE);
                            rl_empty.setVisibility(View.GONE);
                        }else {
                            mSwipeLayout.setVisibility(View.GONE);
                            rl_empty.setVisibility(View.VISIBLE);
                        }
                        refreshlayout.finishRefresh();
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        refreshlayout.finishRefresh();

                    }
                });
    }


    MessageBean messageBean;
    private void netWorkOaMsgList() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.getBacklogUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("pageNum", num)
                .params("pageSize", 15)
                .params("messageType",type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("详情列表",response.body());
                        ViewUtils.cancelLoadingDialog();
                        messageBean = JSON.parseObject(response.body(), MessageBean.class);
                        if (messageBean.getSuccess()) {
                            if(messageBean.getObj().size() > 0){
                                adapter = new MessageAdapter(OaMsgActivity.this,R.layout.item_to_do_my_msg,messageBean.getObj());
                                rvMsgList.setAdapter(adapter);
                                num = 2;
                                mSwipeLayout.setVisibility(View.VISIBLE);
                                rl_empty.setVisibility(View.GONE);
                            }else {
                                mSwipeLayout.setVisibility(View.GONE);
                                rl_empty.setVisibility(View.VISIBLE);
                            }

                        }else {
                            mSwipeLayout.setVisibility(View.GONE);
                            rl_empty.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
