package io.cordova.zhqy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.UMShareAPI;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.DargeFaceByMefgUtils;
import io.cordova.zhqy.utils.DargeFaceSearchUtils;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.web.BaseWebActivity4;

import io.cordova.zhqy.bean.AppListBean;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;


public class AppSearchActivity extends BaseActivity2 implements PermissionsUtil.IPermissionsCallback{


    //上传代码
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_search_cache)
    TextView tvSearchCache;
    @BindView(R.id.tv_search)
    EditText tvSearch;
    @BindView(R.id.search_result)
    RecyclerView searchResult;

    @BindView(R.id.rl_history)
    RelativeLayout rl_history;

    @BindView(R.id.tv_clear)
    TextView tv_clear;

    @BindView(R.id.mFlowLayout)
    TagFlowLayout flSearchCache;
    boolean isLogin =false;
    private int flag = 0;
    @Override
    protected int getResourceId() {
        return R.layout.app_search_activity;
    }

    @Override
    protected void initView() {

        super.initView();
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
        tvTitle.setText("应用搜索");
        tvResult.setVisibility(View.GONE);
        //tvSearchCache.setVisibility(View.GONE);
        rl_history.setVisibility(View.GONE);
        setEditListener();
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_history.setVisibility(View.GONE);
                flSearchCache.setVisibility(View.GONE);
                ViewUtils.clearSearchHistory(AppSearchActivity.this);
            }
        });

        registerBoradcastReceiver();
        registerBoradcastReceiver2();
        registerBoradcastReceiver3();
    }

    private void registerBoradcastReceiver3() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facereYiQingClose04");
        //注册广播
        registerReceiver(broadcastReceiverClose, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiverClose = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQingClose04")){

                SPUtils.put(AppSearchActivity.this,"isloading2","");
            }
        }
    };



    private void registerBoradcastReceiver2() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceSearch");
        //注册广播
        registerReceiver(broadcastReceiverFace, myIntentFilter);
    }

    private int imageid = 0;

    private BroadcastReceiver broadcastReceiverFace = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("faceSearch")){
                Log.e("imageid",imageid+"");
                String FaceActivity = intent.getStringExtra("FaceActivity");
                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;

                        DargeFaceByMefgUtils.jargeFaceResult(AppSearchActivity.this);
                        imageid = 0;
                    }else {
                        imageid = 0;
                    }
                }

            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refresh2");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("refresh2")){
                netWorkSearchApp();
            }
        }
    };

    private void setEditListener() {
        tvSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = tvSearch.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > tvSearch.getWidth()
                        - tvSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    Log.e("tvSearch --- ",StringUtils.getEditTextData(tvSearch));
                    Log.e("isEmpty --- ",StringUtils.getEditTextData(tvSearch).isEmpty() +"");
                   /* if (StringUtils.getEditTextData(tvSearch).isEmpty()){
                        ToastUtils.showToast(MyApp.getInstance(),"请输入搜索关键字");
                    }else {
                        ViewUtils.saveSearchHistory(StringUtils.getEditTextData(tvSearch));
                        netWorkSearchApp();
                        flag = 1;
                        rl_history.setVisibility(View.GONE);
                        flSearchCache.setVisibility(View.GONE);
                    }*/

                    ViewUtils.saveSearchHistory(StringUtils.getEditTextData(tvSearch));
                    netWorkSearchApp();
                    flag = 1;
                    rl_history.setVisibility(View.GONE);
                    flSearchCache.setVisibility(View.GONE);

                }
                return false;

            }
        });

        tvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                   /* if (StringUtils.getEditTextData(tvSearch).isEmpty()){
                        ToastUtils.showToast(MyApp.getInstance(),"请输入搜索关键字");
                    }else {
                        ViewUtils.saveSearchHistory(StringUtils.getEditTextData(tvSearch));
                        netWorkSearchApp();
                        flag = 1;
                        rl_history.setVisibility(View.GONE);
                        flSearchCache.setVisibility(View.GONE);
                    }*/
                    ViewUtils.saveSearchHistory(StringUtils.getEditTextData(tvSearch));
                    netWorkSearchApp();
                    flag = 1;
                    rl_history.setVisibility(View.GONE);
                    flSearchCache.setVisibility(View.GONE);
                    return true;
                }

                return false;
            }
        });
    }

    /*搜索数据请求*/
    AppListBean allAppListBean;
    private void netWorkSearchApp() {
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        ViewUtils.createLoadingDialog(this);
        String editTextData = StringUtils.getEditTextData(tvSearch);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.APP_List)
                .params("Version", "1.0")
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("appName", tvSearch.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("dsadadsad",response.body());
                        allAppListBean = JSON.parseObject(response.body(),AppListBean.class);
                        ViewUtils.cancelLoadingDialog();
                        if (allAppListBean.isSuccess()){
                            tvResult.setVisibility(View.VISIBLE);
                            searchResult.setVisibility(View.VISIBLE);
                            setRvAppList();
                            flSearchCache.setVisibility(View.GONE);
                            tvSearchCache.setVisibility(View.GONE);
                        }else {
                            tvResult.setVisibility(View.VISIBLE);
                            searchResult.setVisibility(View.GONE);
                            tvSearchCache.setVisibility(View.GONE);
                            flSearchCache.setVisibility(View.GONE);
                            ToastUtils.showToast(MyApp.getInstance(),allAppListBean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tvResult.setVisibility(View.GONE);
                        ViewUtils.cancelLoadingDialog();
                        ToastUtils.showToast(MyApp.getInstance(),"网络异常");
                    }
                });
    }

    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;

    /*搜索数据填充*/
    CommonAdapter<AppListBean.ObjBean.ListBean> searchResultListAdapter;
    private void setRvAppList() {
        searchResult.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        searchResultListAdapter = new CommonAdapter<AppListBean.ObjBean.ListBean>(getApplicationContext(),R.layout.item_search_app,allAppListBean.getObj().getList()) {
            @Override
            protected void convert(ViewHolder holder, final AppListBean.ObjBean.ListBean listBean, int position) {
                holder.setText(R.id.tv_app_name,listBean.getAppName());
                holder.setText(R.id.tv_app_come,"来源 ：" +listBean.getSystemName());
                holder.setVisible(R.id.iv_del,true);

                if (null != listBean.getPortalAppIcon() && null != listBean.getPortalAppIcon().getTempletAppImage()){

                    Glide.with(getApplicationContext())
                            .load(UrlRes.HOME3_URL + listBean.getPortalAppIcon().getTempletAppImage())
                            .error(getResources().getColor(R.color.app_bg))
                            .into((ImageView) holder.getView(R.id.iv_app_img));
                }else {
                    Glide.with(getApplicationContext())
                            .load(UrlRes.HOME3_URL + listBean.getAppImages())
                            .error(getResources().getColor(R.color.app_bg))
                            .into((ImageView) holder.getView(R.id.iv_app_img));
                }
                holder.setOnClickListener(R.id.ll_go, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long nowTime = System.currentTimeMillis();
                        if (nowTime - mLastClickTime > TIME_INTERVAL) {
                            mLastClickTime = nowTime;


                            if (null != listBean.getAppAndroidSchema() && listBean.getAppAndroidSchema().trim().length() != 0){
                                if (!isLogin){
                                    Intent intent = new Intent(AppSearchActivity.this,LoginActivity2.class);
                                    startActivity(intent);
                                }else {

                                    String appUrl =  listBean.getAppAndroidSchema()+"";
                                    String intercept = appUrl.substring(0,appUrl.indexOf(":")+3);
//                                    hasApplication(appUrl);
                                    Log.e("TAG", hasApplication(intercept)+"");
                                    if (hasApplication(intercept)){
                                        try {
                                            //直接根据Scheme打开软件  拼接参数
                                            if (appUrl.contains("{memberid}")){
                                                String s1=  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                                                appUrl =  appUrl.replace("{memberid}", s1);
                                            }
                                            if (appUrl.contains("{memberAesEncrypt}")){
                                                String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(listBean.getAppSecret()));
                                                String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                                                appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                                            }
                                            if (appUrl.contains("{quicklyTicket}")){
                                                String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                                                appUrl = appUrl.replace("{quicklyTicket}",s3);
                                            }
                                            Log.e("TAG", appUrl+"");
                                            Uri uri = Uri.parse(appUrl);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                            intent.addCategory(Intent.CATEGORY_DEFAULT);

                                            startActivity(intent);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        //获取下载地址 后跳到浏览器下载
                                        if(null!= listBean.getAppAndroidDownloadLink()){
                                            String dwon = listBean.getAppAndroidDownloadLink()+"";
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dwon));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                            startActivity(intent);
                                        }
                                    }
                                }


                            }else if (!listBean.getAppUrl().isEmpty()){
                                if (!isLogin) {
                                    if(listBean.getAppLoginFlag()==0){
                                        Intent intent = new Intent(AppSearchActivity.this,LoginActivity2.class);
                                        startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                        if (listBean.getAppUrl().contains("{memberid}")){
                                            String appUrl = listBean.getAppUrl();
                                            String s1= null;
                                            try {
                                                s1 = URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                                                appUrl =  appUrl.replace("{memberid}", s1);
                                                intent.putExtra("appUrl",appUrl);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }else {
                                            intent.putExtra("appUrl",listBean.getAppUrl());
                                        }

                                        //intent.putExtra("appUrl",listBean.getAppUrl());
                                        intent.putExtra("appName",listBean.getAppName());
                                        intent.putExtra("search","1");
                                        startActivity(intent);
                                    }

                                }else {
                                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                    if (listBean.getAppUrl().contains("{memberid}")){
                                        String appUrl = listBean.getAppUrl();
                                        String s1= null;
                                        try {
                                            s1 = URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                                            appUrl =  appUrl.replace("{memberid}", s1);
                                            intent.putExtra("appUrl",appUrl);
                                            intent.putExtra("appName",listBean.getAppName());
                                            intent.putExtra("search","1");
                                            intent.putExtra("appId",listBean.getAppId()+"");
                                            startActivity(intent);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                    }else if(listBean.getAppUrl().contains("{memberAesEncrypt}")){
                                        String appUrl = listBean.getAppUrl();
                                        try {
                                            String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(listBean.getAppSecret()));
                                            String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                                            appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                                            intent.putExtra("appUrl",appUrl);
                                            intent.putExtra("appId",listBean.getAppId()+"");
                                            intent.putExtra("appName",listBean.getAppName()+"");
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else if(listBean.getAppUrl().contains("{quicklyTicket}")){
                                        String appUrl = listBean.getAppUrl();
                                        try {
                                            String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                                            appUrl = appUrl.replace("{quicklyTicket}",s3);
                                            intent.putExtra("appUrl",appUrl);
                                            intent.putExtra("appId",listBean.getAppId()+"");
                                            intent.putExtra("appName",listBean.getAppName()+"");
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                    /*permissionsUtil=  PermissionsUtil
                                            .with(AppSearchActivity.this)
                                            .requestCode(1)
                                            .isDebug(true)//开启log
                                            .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                            .request();
                                    if(isOpen == 1){
                                        DargeFaceSearchUtils.cameraTask(listBean,AppSearchActivity.this);
                                    }
*/
                                        AppListBean.ObjBean.PortalAppAuthentication portalAppAuthentication = listBean.getPortalAppAuthentication();
                                        if(portalAppAuthentication != null){
                                            String appAuthenticationFace = portalAppAuthentication.getAppAuthenticationFace();
                                            if(appAuthenticationFace != null){
                                                if(!appAuthenticationFace.equals("0")){
                                                    permissionsUtil=  PermissionsUtil
                                                            .with(AppSearchActivity.this)
                                                            .requestCode(1)
                                                            .isDebug(true)//开启log
                                                            .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                                            .request();

                                                    if(isOpen == 1){
                                                        DargeFaceSearchUtils.cameraTask(listBean,AppSearchActivity.this);
                                                    }
                                                }else {
                                                    DargeFaceSearchUtils.cameraTask(listBean,AppSearchActivity.this);
                                                }

                                            }else {
                                                DargeFaceSearchUtils.cameraTask(listBean,AppSearchActivity.this);
                                            }
                                        }else {
                                            DargeFaceSearchUtils.cameraTask(listBean,AppSearchActivity.this);
                                        }


                                    }



                                }


                            }
                        }

                    }
                });
            }
        };
        searchResult.setAdapter(searchResultListAdapter);
        //searchResultListAdapter.notifyDataSetChanged();

    }


    /**
     * 判断是否安装了应用
     * @return true 为已经安装
     */
    private boolean hasApplication(String scheme) {
        PackageManager manager = getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse(scheme));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(flag == 0){
            setSearchCache();
            List<String> searchHistory = ViewUtils.getSearchHistory();
            if(searchHistory.size() > 0){
                flSearchCache.setVisibility(View.VISIBLE);
                //tvSearchCache.setVisibility(View.VISIBLE);
                rl_history.setVisibility(View.VISIBLE);
            }else {
                rl_history.setVisibility(View.GONE);
                flSearchCache.setVisibility(View.GONE);
            }

        }else {
            isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
            setSearchCache();
            rl_history.setVisibility(View.GONE);
            flSearchCache.setVisibility(View.GONE);


        }

        String isLoading2 = (String) SPUtils.get(this, "isloading2", "");
        Log.e("isLoading2",isLoading2);
        if(!isLoading2 .equals("")){
            ViewUtils.createLoadingDialog2(this,true,"人脸识别中");
            SPUtils.put(this,"isloading2","");


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiverFace);
        unregisterReceiver(broadcastReceiverClose);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.put(this,"isloading2","");
    }

    /*历史记录数据填充*/
    private void setSearchCache() {

        final LayoutInflater mInflater = LayoutInflater.from(getApplication());

        flSearchCache.setAdapter(new TagAdapter<String>(ViewUtils.getSearchHistory()) {

            @Override
            public View getView(com.zhy.view.flowlayout.FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        flSearchCache, false);
                tv.setText(s);
                return tv;
            }
        });

        flSearchCache.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, com.zhy.view.flowlayout.FlowLayout parent)
            {
                tvSearch.setText(ViewUtils.getSearchHistory().get(position));
                netWorkSearchApp();
                flag = 1;
                rl_history.setVisibility(View.GONE);
                flSearchCache.setVisibility(View.GONE);
                return true;
            }
        });


//        flSearchCache.setOnSelectListener(new TagFlowLayout.OnSelectListener()
//        {
//            @Override
//            public void onSelected(Set<Integer> selectPosSet)
//            {
//
//            }
//        });

    }

    private PermissionsUtil permissionsUtil;


    private int isOpen = 0;

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        isOpen = 1;
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
