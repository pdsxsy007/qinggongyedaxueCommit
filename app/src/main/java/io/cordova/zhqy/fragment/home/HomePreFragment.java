package io.cordova.zhqy.fragment.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.PermissionInterceptor;
import com.just.agentweb.WebListenerManager;
import com.just.agentweb.download.AgentWebDownloader;
import com.just.agentweb.download.DefaultDownloadImpl;
import com.just.agentweb.download.DownloadListenerAdapter;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.CAResultActivity;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.activity.MyShenqingActivity;
import io.cordova.zhqy.activity.OaMsgActivity;
import io.cordova.zhqy.activity.SystemMsgActivity;
import io.cordova.zhqy.bean.CaBean;
import io.cordova.zhqy.bean.MyCollectionBean;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.LighterHelper;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyBaseFragment;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.TestShowDig;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.netState;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import io.cordova.zhqy.web.WebLayout2;
import io.cordova.zhqy.R;
import io.cordova.zhqy.utils.BaseFragment;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.web.FileUtil;
import io.cordova.zhqy.web.WebLayout4;
import io.cordova.zhqy.zixing.OnQRCodeListener;
import io.cordova.zhqy.zixing.QRCodeManager;
import io.cordova.zhqy.zixing.activity.CaptureActivity;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.CircleShape;


import static android.content.Context.NOTIFICATION_SERVICE;
import static io.cordova.zhqy.UrlRes.caQrCodeVerifyUrl;
import static io.cordova.zhqy.utils.MyApp.getInstance;


/**
 * Created by Administrator on 2018/11/19 0019.
 */

public class HomePreFragment extends BaseFragment implements PermissionsUtil.IPermissionsCallback{


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_msg)
    RelativeLayout layoutMsg;

    @BindView(R.id.msg_num)
    TextView msgNum;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_qr)
    ImageView iv_qr;



    @BindView(R.id.swipeLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.header)
    ClassicsHeader header;
    protected AgentWeb mAgentWeb;

    @BindView(R.id.linearLayout)
    LinearLayout mLinearLayout;

    private Gson mGson = new Gson();
    String tgc ,msgType;
    boolean isLogin =false;

    private static final int REQUEST_SHARE_FILE_CODE = 120;
    private PermissionsUtil permissionsUtil;

    @BindView(R.id.btn01)
    Button btn01;
    @Override
    public int getLayoutResID() {
        return R.layout.fragment_home_pre;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView(View view) {
        super.initView(view);

        tgc = (String) SPUtils.get(MyApp.getInstance(),"TGC","");
        msgType = (String) SPUtils.get(MyApp.getInstance(),"msgType","");
        iv_qr.setVisibility(View.VISIBLE);
        tvTitle.setText("首页");

        setWeb();
        setGoPushMsg();
        header.setEnableLastTime(false);
        checkNetState();

        String home01 = (String) SPUtils.get(MyApp.getInstance(), "home01", "");
        if(home01.equals("")){
            setGuideView();
        }
        //PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE

        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

                Notification notification =new NotificationCompat.Builder(getActivity(),"chat")

                        .setContentTitle("收到一条聊天消息")

                        .setContentText("今天中午吃什么？")

                        .setWhen(System.currentTimeMillis())

                        .setSmallIcon(R.drawable.icon)

                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))

                        .setAutoCancel(true)

                        .build();

                manager.notify(1, notification);
            }
        });
    }




    private void setGuideView() {
        CircleShape circleShape = new CircleShape(10);
        circleShape.setPaint(LighterHelper.getDashPaint()); //set custom paint
        // 使用图片
        Lighter.with(getActivity())
                .setBackgroundColor(0xB9000000)
                .setOnLighterListener(new OnLighterListener() {
                    @Override
                    public void onShow(int index) {


                    }

                    @Override
                    public void onDismiss() {
                        SPUtils.put(MyApp.getInstance(),"home01","1");




                        CircleShape circleShape = new CircleShape(10);
                        circleShape.setPaint(LighterHelper.getDashPaint()); //set custom paint
                        // 使用图片
                        Lighter.with(getActivity())
                                .setBackgroundColor(0xB9000000)
                                .setOnLighterListener(new OnLighterListener() {
                                    @Override
                                    public void onShow(int index) {


                                    }

                                    @Override
                                    public void onDismiss() {
                                        SPUtils.put(MyApp.getInstance(),"home06","1");
                                    }
                                })
                                .addHighlight(new LighterParameter.Builder()
                                        .setHighlightedViewId(R.id.rb_my)
                                        .setTipLayoutId(R.layout.fragment_home_gl_new)
                                        .setTipViewRelativeDirection(Direction.TOP)
                                        .build()).show();



                    }
                })
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(iv_qr)
                        .setTipLayoutId(R.layout.fragment_home_gl)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(150, 0, 30, 0))
                        .build()).show();



    }

    private void checkNetState() {
        if (!netState.isConnect(getActivity()) ){
            ToastUtils.showToast(getActivity(),"网络连接异常!");

        }else {


        }
    }

    /**
     * 推送消息跳转
     * msgType 消息类型(-1:删除，0:系统消息,1:待办,2:待阅,3:已办,4:已阅,5:我的申请)
     *
     * */
    private void setGoPushMsg() {
        if (!StringUtils.isEmpty(msgType)){
            Intent intent;
            if (msgType.equals("0")){
                intent = new Intent(MyApp.getInstance(), SystemMsgActivity.class);
                intent.putExtra("msgType","系统消息");
                startActivity(intent);
            }else if (msgType.equals("1")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","db");
                intent.putExtra("msgType","待办消息");
                startActivity(intent);
            }else if (msgType.equals("2")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","dy");
                intent.putExtra("msgType","待阅消息");
                startActivity(intent);
            }else if (msgType.equals("3")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","yb");
                intent.putExtra("msgType","已办消息");
                startActivity(intent);
            }else if (msgType.equals("4")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","yy");
                intent.putExtra("msgType","已阅消息");
                startActivity(intent);
            }else if (msgType.equals("5")){
                intent = new Intent(MyApp.getInstance(), MyShenqingActivity.class);
                intent.putExtra("type","sq");
                intent.putExtra("msgType","我的申请");
                startActivity(intent);
            }else if (msgType.equals("6")){
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=https://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                    startActivity(intent);
                }else {
                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=https://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                    startActivity(intent);
                }

            }
            SPUtils.remove(MyApp.getInstance(),"msgType");
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (!netState.isConnect(getActivity()) ){
                    ToastUtils.showToast(getActivity(),"网络连接异常!");
                    refreshlayout.finishRefresh();
                }else {
                    mAgentWeb.getWebCreator().getWebView().reload();
                    refreshlayout.finishRefresh();
                    setWeb();
                }

            }
        });

    }


    @SuppressLint("WrongConstant")
    private void setWeb() {
        layoutMsg.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mSwipeRefreshLayout, new SwipeRefreshLayout.LayoutParams(-1,-1))
                .useDefaultIndicator(getResources().getColor(R.color.title_bar_bg),3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setAgentWebWebSettings(getSettings())
                .setWebViewClient(mWebViewClient)//WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
                .setWebChromeClient(mWebChromeClient)
                .setMainFrameErrorView(R.layout.layout_no_net, 0)
                .setPermissionInterceptor(mPermissionInterceptor) //权限拦截 2.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .setWebLayout(new WebLayout4(getActivity()))
                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings。
                .go("http://www.zzuli.edu.cn/_t9/main.htm");


        mAgentWeb.getAgentWebSettings().getWebSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);

    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // super.onReceivedSslError(view, handler, error);
            /**
             *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
             *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                handler.cancel(); // 接受所有网站的证书
            }


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            //imgReset(view);
            CookieManager cookieManager = CookieManager.getInstance();
            if(Build.VERSION.SDK_INT>=21){
                cookieManager.setAcceptThirdPartyCookies(view, true);
            }

        }



        /**网址拦截*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        /**网址拦截*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (!url.equals("http://www.zzuli.edu.cn/_t9/main.htm")){
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    intent.putExtra("appUrl",url);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    intent.putExtra("appUrl",url);
                    startActivity(intent);
                }


                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("加载网页",url);
        }
    };

    //此方法获取里面的img，设置img的高度100%,固定图片不能左右滑动
    private void imgReset(WebView view) {
        if(view!=null){
            view.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    " img.style.maxWidth = '100%';img.style.height='auto';" +
                    "}" +
                    "})()");
        }}

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //   do you work
            Log.i("Info", "onProgress:" + newProgress);
        }

        /*Title*/
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            if (mTitleTextView != null) {
//                mTitleTextView.setText(title);
//            }
        }

    };
    /**
     * @return IAgentWebSettings
     */
    public IAgentWebSettings getSettings()  {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;
            private WebSettings mWebSettings;
            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            @Override
            public IAgentWebSettings toSetting(WebView webView) {

                return super.toSetting(webView);
            }

            @Override
            public WebListenerManager setDownloader(WebView webView, android.webkit.DownloadListener downloadListener) {
                return super.setDownloader(webView,
                        DefaultDownloadImpl
                                .create((Activity) webView.getContext(),
                                        webView,
                                        mDownloadListenerAdapter,
                                        mDownloadListenerAdapter,
                                        this.mAgentWeb.getPermissionInterceptor()));
            }
        };
    }

    protected DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {

        @Override
        public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {
            extra.setOpenBreakPointDownload(true) // 是否开启断点续传
                    .setIcon(R.drawable.ic_file_download_black_24dp) //下载通知的icon
                    .setConnectTimeOut(6000) // 连接最大时长
                    .setBlockMaxTime(10 * 60 * 1000)  // 以8KB位单位，默认60s ，如果60s内无法从网络流中读满8KB数据，则抛出异常
                    .setDownloadTimeOut(Long.MAX_VALUE) // 下载最大时长
                    .setParallelDownload(false)  // 串行下载更节省资源哦
                    .setEnableIndicator(true)  // false 关闭进度通知
                    //.addHeader("Cookie", "xx") // 自定义请求头
                    //  .setAutoOpen(false) // 下载完成自动打开
                    .setForceDownload(true); // 强制下载，不管网络网络类型
            return false;
        }

        @Override
        public void onProgress(String url, long loaded, long length, long usedTime) {
            int mProgress = (int) ((loaded) / Float.valueOf(length) * 100);
            Log.i("下载进度", "onProgress:" + mProgress);
            super.onProgress(url, loaded, length, usedTime);
        }

        @Override
        public boolean onResult(String path, String url, Throwable throwable) {
            if (null == throwable) { //下载成功

                Uri shareFileUrl = FileUtil.getFileUri(getActivity(),null,new File(path));
                new Share2.Builder(getActivity())
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            } else {//下载成功
                Uri shareFileUrl = FileUtil.getFileUri(getActivity(),null,new File(path));
                new Share2.Builder(getActivity())
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            }
            return false; // true  不会发出下载完成的通知 , 或者打开文件
        }
    };
    protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        /**
         * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
         * @param url
         * @param permissions
         * @param action
         * @return true 该Url对应页面请求权限进行拦截 ，false 表示不拦截。
         */
        @Override
        public boolean intercept(String url, String[] permissions, String action) {

            return false;
        }
    };


    @OnClick({R.id.msg_num, R.id.layout_msg,R.id.iv_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.msg_num:

                break;
            case R.id.iv_qr:

                //cameraTask();
                permissionsUtil=  PermissionsUtil
                        .with(this)
                        .requestCode(1)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();

                break;
            case R.id.layout_msg:

                break;
        }
    }

    @Override
    public void onResume() {
        if (!netState.isConnect(getActivity())) {
            ToastUtils.showToast(getActivity(),"网络连接异常!");
        }
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onResume();//恢复
        }

        super.onResume();
    }
    @Override
    public void onPause() {

        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onPause(); //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        }

        super.onPause();
    }
    @Override
    public void onDestroyView() {
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onDestroy();
        }

        super.onDestroyView();
    }

    /**
     * 进入扫描二维码页面
     *
     * @param
     */
    public void onScanQR() {
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));

        QRCodeManager.getInstance()
                .with(getActivity())
                .setReqeustType(0)
                .setRequestCode(55846)
                .scanningQRCode(new OnQRCodeListener() {
                    @Override
                    public void onCompleted(String result) {
                        Log.e("QRCodeManager = ",result);
                        if(!isLogin){
                            Intent intent = new Intent(MyApp.getInstance(), LoginActivity2.class);
                            startActivity(intent);
                        }else {
                            String[] split = result.split("_");
                            String s = split[1];
                            if(s.equals("1")){
                                yanqianData(split[0]);

                            }else {
                                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                                if(isOpen.equals("") || isOpen.equals("1")){
                                    Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                                    intent.putExtra("appUrl",result);
                                    intent.putExtra("scan","scan");
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                    intent.putExtra("appUrl",result);
                                    intent.putExtra("scan","scan");
                                    startActivity(intent);
                                }
                            }



                        }


                    }

                    @Override
                    public void onError(Throwable errorMsg) {

                    }

                    @Override
                    public void onCancel() {

                    }


                    @Override
                    public void onManual(int requestCode, int resultCode, Intent data) {

                    }
                });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){

        }else {
            netInsertPortal("1");
        }
    }

    private void yanqianData(String s) {
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        String s3 = s;
        OkGo.<String>post(UrlRes.HOME_URL+caQrCodeVerifyUrl)
                .tag(this)
                .params("userId", (String) SPUtils.get(MyApp.getInstance(), "userId", ""))
                .params("content", s)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("验签结果",response.body());
                            CaBean faceBean2 = JsonUtil.parseJson(response.body(),CaBean.class);

                            boolean success = faceBean2.getSuccess();
                            if(success == true){

                                Intent intent = new Intent(getActivity(),CAResultActivity.class);
                                intent.putExtra("result","0");
                                intent.putExtra("certDn",faceBean2.getObj().getCertDn());
                                intent.putExtra("signTime",faceBean2.getObj().getSignTime());
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getActivity(),CAResultActivity.class);
                                intent.putExtra("result","1");
                                startActivity(intent);
                            }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("s",response.toString());
                    }
                });

    }

    private void netInsertPortal(final String insertPortalAccessLog) {
        String imei = MobileInfoUtils.getIMEI(getActivity());
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Four_Modules)
                .tag(this)
                .params("portalAccessLogMemberId",(String) SPUtils.get(getInstance(),"userId",""))
                .params("portalAccessLogEquipmentId",(String) SPUtils.get(getInstance(),"imei",""))//设备ID
                .params("portalAccessLogTarget", insertPortalAccessLog)//访问目标
                .params("portalAccessLogVersionNumber", (String) SPUtils.get(getActivity(),"versionName", ""))//版本号
                .params("portalAccessLogOperatingSystem", "ANDROID")//版本号
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("sdsaas",response.body());
                        //getMyLocation();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //需要调用onRequestPermissionsResult
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //监听跳转到权限设置界面后再回到应用
        permissionsUtil.onActivityResult(requestCode, resultCode, intent);

        //注册onActivityResult
        if (requestCode == 55846){
            QRCodeManager.getInstance().with(getActivity()).onActivityResult(requestCode, resultCode, intent);
        }

    }

    private static final int RC_CAMERA_PERM = 123;


    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        Log.e("权限同意","权限同意");

        //onScanQR();
        Intent i = new Intent(getActivity(),CaptureActivity.class);
        startActivity(i);

    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        Log.e("权限拒绝","权限拒绝");
    }
}
