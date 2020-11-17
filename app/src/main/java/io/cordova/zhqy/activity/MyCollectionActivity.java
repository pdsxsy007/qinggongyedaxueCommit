package io.cordova.zhqy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cxz.swipelibrary.SwipeBackLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.OnClick;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.AppListBean;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.bean.MyCollectionBean;
import io.cordova.zhqy.bean.ServiceAppListBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.DargeFaceByMefgUtils;
import io.cordova.zhqy.utils.DargeFaceMeColletUtils;
import io.cordova.zhqy.utils.DargeFaceSearchUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;

/**
 * Created by Administrator on 2018/11/22 0022.
 */

public class MyCollectionActivity extends BaseActivity2 implements PermissionsUtil.IPermissionsCallback{

    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.tv_cancel_collection)
    TextView tvCancelCollection;
    @BindView(R.id.rv_my_collection)
    RecyclerView rvMyCollection;

    @BindView(R.id.rl_empty)
    RelativeLayout rl_empty;
    int edit = 0;
    private SwipeBackLayout mSwipeBackLayout;
    @Override
    protected int getResourceId() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void initView() {
        super.initView();


        registerBoradcastReceiver1();
        registerBoradcastReceiver2();
    }

    private void registerBoradcastReceiver1() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facereYiQingClose03");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQingClose03")){

                SPUtils.put(MyCollectionActivity.this,"isloading2","");
            }
        }
    };



    private void registerBoradcastReceiver2() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceMeCollet");
        //注册广播
       registerReceiver(broadcastReceiverFace, myIntentFilter);
    }

    private int imageid = 0;

    private BroadcastReceiver broadcastReceiverFace = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("faceMeCollet")){
                Log.e("imageid",imageid+"");
                String FaceActivity = intent.getStringExtra("FaceActivity");
                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;

                        DargeFaceMeColletUtils.jargeFaceResult(MyCollectionActivity.this);
                        imageid = 0;
                    }else {
                        imageid = 0;
                    }
                }

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        String isLoading2 = (String) SPUtils.get(this, "isloading2", "");
        Log.e("isLoading2",isLoading2);
        if(!isLoading2 .equals("")){
            ViewUtils.createLoadingDialog2(this,true,"人脸识别中");
            SPUtils.put(this,"isloading2","");


        }
        netWorkMyCollection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverFace);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.put(this,"isloading2","");
    }

    MyCollectionBean collectionBean;
    private void netWorkMyCollection() {

        //ViewUtils.createLoadingDialog(this);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.My_Collection)
                .params("userId", (String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("我的收藏",response.body());
                        collectionBean = JSON.parseObject(response.body(), MyCollectionBean.class);

                        if (collectionBean.isSuccess()) {
                            if(null != collectionBean.getObj()){
                                rvMyCollection.setVisibility(View.VISIBLE);
                                rl_empty.setVisibility(View.GONE);
                                setCollectionList();
                            }else {
                                rvMyCollection.setVisibility(View.GONE);
                                rl_empty.setVisibility(View.VISIBLE);
                            }

                        } else {

                            ToastUtils.showToast(MyApp.getInstance(), collectionBean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        //ViewUtils.cancelLoadingDialog();

                    }
                });
    }

    CommonAdapter<MyCollectionBean.ObjBean> adapter;
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;

    private void setCollectionList() {

        rvMyCollection.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        adapter = new CommonAdapter<MyCollectionBean.ObjBean>(getApplicationContext(), R.layout.item_service_app, collectionBean.getObj()) {
            @Override
            protected void convert(ViewHolder holder, final MyCollectionBean.ObjBean objBean, final int position) {
                if (tvEdit.getText().equals("编辑")){
                    holder.setVisible(R.id.iv_lock_open,false);
//                    holder.setVisible(R.id.iv_lock_close,true);

                             /*appIntranet  1 需要内网*/
                    if (objBean.getAppIntranet()==1){
                        holder.setVisible(R.id.iv_del,true);
                        Glide.with(getApplicationContext())
                                .load(R.mipmap.nei_icon)
                                .error(R.color.white)
                                .placeholder(R.mipmap.zwt)
                                .into((ImageView) holder.getView(R.id.iv_del));
                    }else {
                        holder.setVisible(R.id.iv_del,false);
                    }


                }else if(tvEdit.getText().equals("完成")) {
                    holder.setVisible(R.id.iv_lock_close,false);
                    holder.setVisible(R.id.iv_del,false);
                    holder.setVisible(R.id.iv_lock_open,true);

                }
//                holder.setVisible(R.id.iv_lock_open,true);
//
//                Glide.with(getApplicationContext())
//                        .load(R.mipmap.lock_icon)
//                        .transform(new CircleCrop(getApplicationContext()))
//                        .into((ImageView) holder.getView(R.id.iv_lock_open));

                holder.setText(R.id.tv_app_name,objBean.getAppName());
                if (null != objBean.getPortalAppIcon() && null != objBean.getPortalAppIcon().getTempletAppImage()){

                    Glide.with(getApplicationContext())
                            .load(UrlRes.HOME3_URL + objBean.getPortalAppIcon().getTempletAppImage())
                            .error(R.mipmap.message_icon1)
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_app_icon));
                }else {
                    Glide.with(getApplicationContext())
                            .load(UrlRes.HOME3_URL + objBean.getAppImages())
                            .error(R.mipmap.message_icon1)
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_app_icon));
                }

                holder.setOnClickListener(R.id.ll_click, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long nowTime = System.currentTimeMillis();
                        if (nowTime - mLastClickTime > TIME_INTERVAL) {
                            mLastClickTime = nowTime;
                            netWorkAppClick(objBean.getAppId());
                            Intent intent = null;
                            String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                            if(isOpen.equals("") || isOpen.equals("1")){
                                intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                            }else {
                                intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                            }
                            ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication portalAppAuthentication = collectionBean.getObj().get(position).getPortalAppAuthentication();
                            if(portalAppAuthentication != null){
                                String appAuthenticationFace = portalAppAuthentication.getAppAuthenticationFace();
                                if(appAuthenticationFace != null){
                                    if(!appAuthenticationFace.equals("0")){
                                        permissionsUtil=  PermissionsUtil
                                                .with(MyCollectionActivity.this)
                                                .requestCode(1)
                                                .isDebug(true)//开启log
                                                .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                                .request();

                                        if(isOpens == 1){
                                            DargeFaceMeColletUtils.cameraTask(collectionBean.getObj().get(position),MyCollectionActivity.this);
                                        }
                                    }else {
                                        DargeFaceMeColletUtils.cameraTask(collectionBean.getObj().get(position),MyCollectionActivity.this);
                                    }

                                }else {
                                    DargeFaceMeColletUtils.cameraTask(collectionBean.getObj().get(position),MyCollectionActivity.this);
                                }
                            }else {
                                DargeFaceMeColletUtils.cameraTask(collectionBean.getObj().get(position),MyCollectionActivity.this);
                            }
                        }





                    }
                });

                holder.setOnClickListener(R.id.iv_lock_open, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消收藏
                        netCancelCollection(objBean.getAppId());
                    }
                });


            }
        };
        rvMyCollection.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        adapter.notifyDataSetChanged();
    }
    BaseBean baseBean;
    private void netCancelCollection(int appId) {
        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.Cancel_Collection)
                .params( "version","1.0" )
                .params( "collectionAppId",appId )
                .params( "userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        //handleResponse(response);
                        Log.e("tag",response.body());
                        baseBean = JSON.parseObject(response.body(), BaseBean.class);
                        if (baseBean.isSuccess()){
                            netWorkMyCollection();
                            ToastUtils.showToast(MyApp.getInstance(),baseBean.getMsg());
                            Intent intent = new Intent();
                            intent.putExtra("refreshService","dongtai");
                            intent.setAction("refresh2");
                            sendBroadcast(intent);
                        }else {

                            ToastUtils.showToast(MyApp.getInstance(),baseBean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);


                    }
                });

    }

    /**
     * 记录该微应用的的访问量
     * @param appId
     *
     * */
    private void netWorkAppClick(int appId) {
        OkGo.<String>get(UrlRes.HOME_URL +UrlRes.APP_Click_Number)

                .params("appId",appId)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result1",response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("错误",response.body());
                    }
                });
    }


    @OnClick({R.id.tv_edit, R.id.tv_cancel_collection})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_edit:
                if (edit!=0){
                    edit = 0;
                    tvEdit.setText("编辑");
                }else {
                    edit = 1;
                    tvEdit.setText("完成");
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_cancel_collection:

                break;
        }
    }

    private PermissionsUtil permissionsUtil;

    private int isOpens = 0;

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        isOpens = 1;
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(permissionsUtil != null){
            permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(permissionsUtil != null){
            permissionsUtil.onActivityResult(requestCode, resultCode, data);
        }


    }
}
