package io.cordova.zhqy.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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
import com.bumptech.glide.signature.StringSignature;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.UMShareAPI;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossGetCertResult;
import cn.org.bjca.signet.coss.component.core.enums.CertType;
import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.AppSetting;
import io.cordova.zhqy.activity.CertificateActivateActivity;
import io.cordova.zhqy.activity.InfoDetailsActivity;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.activity.ManagerCertificateOneActivity;
import io.cordova.zhqy.activity.MyCollectionActivity;
import io.cordova.zhqy.activity.MyDataActivity;
import io.cordova.zhqy.activity.MyToDoMsgActivity;
import io.cordova.zhqy.activity.OaMsgActivity;
import io.cordova.zhqy.bean.CheckRoleCodeBean;
import io.cordova.zhqy.bean.CountBean;
import io.cordova.zhqy.bean.MessageBean;
import io.cordova.zhqy.bean.MyCollectionBean;
import io.cordova.zhqy.bean.NoticeInfoBean;
import io.cordova.zhqy.bean.OAMsgListBean;
import io.cordova.zhqy.bean.ServiceAppListBean;
import io.cordova.zhqy.bean.UserMsgBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BadgeView;
import io.cordova.zhqy.utils.BaseFragment;
import io.cordova.zhqy.utils.DargeFaceByMefColletUtils;
import io.cordova.zhqy.utils.DargeFaceByMefgUtils;
import io.cordova.zhqy.utils.DargeFaceUtils;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.LighterHelper;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.netState;

import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import io.cordova.zhqy.widget.XCRoundImageView;
import io.cordova.zhqy.zixing.activity.CaptureActivity;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.CircleShape;


import static io.cordova.zhqy.UrlRes.findLoginTypeListUrl;
import static io.cordova.zhqy.utils.MyApp.getInstance;
import static io.cordova.zhqy.utils.TimeUtils.timeStamp2Date;

/**
 * Created by Administrator on 2018/11/22 0022.
 */

public class MyPre2Fragment extends BaseFragment implements PermissionsUtil.IPermissionsCallback{
    @BindView(R.id.tv_app_msg)
    ImageView ivAppMsg;
    @BindView(R.id.iv_user_head)
    XCRoundImageView ivUserHead;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
   @BindView(R.id.tv_zhuan_ye)
    TextView tvZhangye;
    @BindView(R.id.rv_user_data)
    RelativeLayout rvUserData;
    @BindView(R.id.tv_data_num)
    TextView tvDataNum;
    ImageView tvAppSetting;
    @BindView(R.id.tv_my_collection_num)
    TextView tvMyCollectionNum;
    @BindView(R.id.tv_my_to_do_msg_num)
    TextView tvMyToDoMsgNum;
    @BindView(R.id.my_oa_to_do_list)
    RecyclerView myOaToDoList;
    @BindView(R.id.ll_oa)
    LinearLayout llOa;
    @BindView(R.id.my_collection_list)
    RecyclerView myCollectionList;
    @BindView(R.id.ll_my_collection)
    LinearLayout llMyCollection;
    @BindView(R.id.my_data_list)
    RecyclerView myDataList;
    @BindView(R.id.ll_my_data)
    LinearLayout llMyData;
    @BindView(R.id.rl_msg_app)
    RelativeLayout rlMsgApp;

    @BindView(R.id.rl_title)
    RelativeLayout rl_title;
    @BindView(R.id.rl_in)
    RelativeLayout rl_in;

    @BindView(R.id.ll_isShow)
    LinearLayout ll_isShow;
    @BindView(R.id.ll_sign)
    LinearLayout ll_sign;
    @BindView(R.id.ll_yan_sign)
    LinearLayout ll_yan_sign;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_more)
    TextView tv_more;

    boolean isLogin = false;
    BadgeView badge1;
    String count;
    @Override
    public int getLayoutResID() {
        return R.layout.fragment2_my_pre;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        count = (String) SPUtils.get(getActivity(), "count", "");
        tvAppSetting = view.findViewById(R.id.tv_app_setting1);
        badge1 = new BadgeView(getActivity(), rlMsgApp);
        remind();
        if(count != null){
            if(count.equals("0")){

                badge1.hide();
            }else {
                badge1.show();
            }
        }else {
            badge1.hide();
        }
        tvMyToDoMsgNum.setText(count);
        initLoadPage();
        netWorkUserMsg();
        String home04 = (String) SPUtils.get(MyApp.getInstance(), "home04", "");
        if(home04.equals("")){
            setGuideView();
        }

        registerBoradcastReceiver();
        registerBoradcastReceiverCollet();
        registerBoradcastReceiver2();
        registerBoradcastReceiver3();
        registerBoradcastReceiver4();
        registerBoradcastReceiver5();


        getCaShowInfo();

        ll_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                permissionsUtil=  PermissionsUtil
                        .with(MyPre2Fragment.this)
                        .requestCode(1)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();

                if(isOpen == 1){
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.putExtra("clickWhich","0");
                    startActivity(intent);
                }
            }
        });

        ll_yan_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionsUtil=  PermissionsUtil
                        .with(MyPre2Fragment.this)
                        .requestCode(1)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();

                if(isOpen == 1){
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.putExtra("clickWhich","1");
                    startActivity(intent);
                }
            }
        });

    }



    private String certContent = "";
    private int flag = 0;
    /**
     * 查看证书
     */
    private void checkCertInfo() {

        String msspID = (String) SPUtils.get(getActivity(), "msspID", "");
        if(msspID.equals("")){
            tv_name.setText("下载");
            flag = 1;

        }else {
            final CossGetCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossGetCert(getActivity(), msspID, CertType.ALL_CERT);
            HashMap<CertType, String> certMap = result.getCertMap();

            for(HashMap.Entry<CertType, String> entry : certMap.entrySet()){
                //System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                if(entry.getValue() != null){
                    certContent = entry.getValue();
                }
            }

            if(!certContent.equals("")){//证书已存在
                tv_name.setText("进入");
                flag = 0;
            }else {//证书不存在
                tv_name.setText("下载");
                flag = 1;
            }
        }

        rl_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                switch (flag){
                    case 0://进入
                        Intent intent = new Intent(MyApp.getInstance(), ManagerCertificateOneActivity.class);
                        startActivity(intent);
                        break;
                    case 1://安装
                        Intent intent1 = new Intent(MyApp.getInstance(), CertificateActivateActivity.class);
                        startActivity(intent1);
                        break;
                }
            }
        });

    }

    /**
     * 判断角色是否展示Ca模块
     */
    private void getCaShowInfo() {

        OkGo.<String>get(UrlRes.HOME_URL +findLoginTypeListUrl)
                .tag(this)
                .params("type","enableShowCertificate")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("展示ca类型", response.body());
                        //rolecodes
                        CheckRoleCodeBean checkRoleCodeBean = JsonUtil.parseJson(response.body(), CheckRoleCodeBean.class);
                        boolean success = checkRoleCodeBean.getSuccess();
                        if (success) {
                            List<CheckRoleCodeBean.Obj> obj = checkRoleCodeBean.getObj();
                            if (null != obj) {
                                String rolecodes = (String) SPUtils.get(getActivity(), "rolecodes", "");
                                String[] split = rolecodes.split(",");
                                for (int j = 0; j < split.length; j++) {
                                    String userRole = split[j];
                                    for (int i = 0; i < obj.size(); i++) {
                                        if (obj.get(i).getConfigValue().contains(userRole)) {//符合后台提供显示角色的类型

                                            ll_isShow.setVisibility(View.VISIBLE);
                                        }

                                    }

                                }

                                if(ll_isShow.isShown()){
                                    checkCertInfo();
                                }


                            }

                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void registerBoradcastReceiverCollet() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refresh2");
        //注册广播
        getActivity().registerReceiver(broadcastReceiverShoucang, myIntentFilter);

    }

    private BroadcastReceiver broadcastReceiverShoucang = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("refresh2")){

                netWorkMyCollection();
            }
        }
    };

    private void registerBoradcastReceiver5() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refreshCAResult");
        //注册广播
        getActivity().registerReceiver(broadcastReceiverCAResult, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiverCAResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("refreshCAResult")){

                checkCertInfo();
            }
        }
    };


    private void registerBoradcastReceiver4() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facereYiQingClose02");
        //注册广播
        getActivity().registerReceiver(broadcastReceiverClose02, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiverClose02 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQingClose02")){

                SPUtils.put(getActivity(),"isloading2","");
            }
        }
    };


    private void registerBoradcastReceiver3() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceMeCollet");
        //注册广播
        getActivity().registerReceiver(broadcastReceiverCollet, myIntentFilter);
    }

    private void registerBoradcastReceiver2() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceMe");
        //注册广播
        getActivity().registerReceiver(broadcastReceiverFace, myIntentFilter);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refreshLogo");
        //注册广播
        getActivity().registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("refreshLogo")){
                netGetUserHead();
            }
        }
    };

    private int imageid = 0;

    private BroadcastReceiver broadcastReceiverFace = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("faceMe")){
                Log.e("imageid",imageid+"");
                String FaceActivity = intent.getStringExtra("FaceActivity");
                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;

                        DargeFaceByMefgUtils.jargeFaceResult(getActivity());
                        imageid = 0;
                    }else {
                        imageid = 0;
                    }
                }

            }
        }
    };



    private BroadcastReceiver broadcastReceiverCollet = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("faceMeCollet")){
                Log.e("imageid",imageid+"");
                String FaceActivity = intent.getStringExtra("FaceActivity");
                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;

                        DargeFaceByMefColletUtils.jargeFaceResult(getActivity());
                        imageid = 0;
                    }else {
                        imageid = 0;
                    }
                }

            }
        }
    };



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
                        SPUtils.put(MyApp.getInstance(),"home04","1");
                    }
                })
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(tvAppSetting)
                        .setTipLayoutId(R.layout.fragment_home_gl5)
                        .setLighterShape(circleShape)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(150, 0, 30, 0))
                        .build(),
                        new LighterParameter.Builder()
                        .setHighlightedView(rlMsgApp)
                        .setTipLayoutId(R.layout.fragment_home_gl3)
                        .setLighterShape(circleShape)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(150, 0, 30, 0))
                        .build()).show();
    }

    private void remind() { //BadgeView的具体使用


       try{
           int i1 = Integer.parseInt(count);
           if(i1>99){
               badge1.setText("99+"); // 需要显示的提醒类容
           }else {
               badge1.setText(count); // 需要显示的提醒类容
           }
           //badge1.setText(count); // 需要显示的提醒类容
           if(count == null){
               count = "0";
           }
           SPUtils.put(getActivity(),"count",count+"");
       }catch (Exception e){
            e.getMessage();
       }

        badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 显示的位置.右上角,BadgeView.POSITION_BOTTOM_LEFT,下左，还有其他几个属性
        badge1.setTextColor(Color.WHITE); // 文本颜色
        badge1.setBadgeBackgroundColor(Color.RED); // 提醒信息的背景颜色，自己设置
        badge1.setTextSize(10); // 文本大小
        badge1.setBadgeMargin(3, 3); // 水平和竖直方向的间距
        badge1.show();// 只有显示
    }
    /**判断登录状态*/
    private void initLoadPage() {
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
        isLoginState();
    }

    /**判断是否登录*/
    private void isLoginState() {
        if (isLogin){

            getBack();
            netWorkMyCollection();//我的收藏



        }else {

        }
    }


    private void getBack() {
        OkGo.<String>get(UrlRes.HOME_URL +findLoginTypeListUrl)
                .tag(this)
                .params("type","backLog")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result_ma0bing",response.body());


                        NoticeInfoBean noticeInfoBean = JsonUtil.parseJson(response.body(),NoticeInfoBean.class);

                        List<NoticeInfoBean.Obj> obj = noticeInfoBean.getObj();
                        if(obj != null){
                            String configValue = obj.get(0).getConfigValue();
                            if(configValue.equals("1")){
                                tv_more.setVisibility(View.GONE);
                                netWorkSystemMsg();
                                dbDataList();//OA待办消息

                            }else{
                                netWorkSystemMsg2();
                                dbDataList2();//OA待办消息

                            }
                        }else {
                            netWorkSystemMsg2();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        netWorkSystemMsg2();
                    }
                });


    }

    MessageBean messageBean;
    private void dbDataList2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.getBacklogUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("pageNum", 1)
                .params("pageSize", 15)
                .params("messageType","1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("待办列表",response.body());
                        //oaMsgListBean = JSON.parseObject(response.body(), OAMsgListBean.class);

                        messageBean = JSON.parseObject(response.body(), MessageBean.class);
                        if (messageBean.getSuccess()) {
                            if(messageBean.getCount() > 0){
                                setRvOAMsgList();
                            }else {
                                llOa.setVisibility(View.GONE);
                            }


                        }else {
                            llOa.setVisibility(View.GONE);
                        }

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);


                    }
                });
    }

    /**点击事件*/
    @OnClick({R.id.rv_user_data, R.id.rv_my_collection, R.id.rv_my_to_do_msg, R.id.exit_login,R.id.tv_app_setting1,R.id.tv_app_msg})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rv_user_data:
                if (isLogin){
                    intent = new Intent(MyApp.getInstance(), MyDataActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rv_my_collection:
                if (isLogin){
                    intent = new Intent(MyApp.getInstance(), MyCollectionActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rv_my_to_do_msg:
            case R.id.tv_app_msg:
                if (isLogin){
                    intent = new Intent(MyApp.getInstance(), MyToDoMsgActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_app_setting1:
                if (isLogin){
                    intent = new Intent(MyApp.getInstance(), AppSetting.class);
                    startActivity(intent);
                    //FinishActivity.addActivity(getActivity());
                }
                break;

        }
    }

    /**个人信息*/
    UserMsgBean userMsgBean;
    private void netWorkUserMsg() {
        try {
            OkGo.<String>post(UrlRes.HOME_URL + UrlRes.User_Msg)
                    .tag(this)
                    .params("userId", (String) SPUtils.get(MyApp.getInstance(),"userId",""))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("result1",response.body()+"   --防空");
                            userMsgBean = JSON.parseObject(response.body(), UserMsgBean.class);
                            if (userMsgBean.isSuccess()) {
                                if(null != userMsgBean.getObj()) {
                                    if(userMsgBean.getObj().getModules().getMemberOtherDepartment() != null){
                                        tvZhangye.setText(userMsgBean.getObj().getModules().getMemberOtherDepartment());
                                    }

                                    tvUserName.setText(userMsgBean.getObj().getModules().getMemberNickname());

                                    StringBuilder sb = new StringBuilder();
                                    if(userMsgBean.getObj().getModules().getRolecodes()!= null){

                                        if (userMsgBean.getObj().getModules().getRolecodes().size() > 0){
                                            for (int i = 0; i < userMsgBean.getObj().getModules().getRolecodes().size(); i++) {
                                                sb.append(userMsgBean.getObj().getModules().getRolecodes().get(i).getRoleCode()).append(",");
                                            }
                                            String ss = sb.substring(0, sb.lastIndexOf(","));
                                            Log.e("TAG",ss);
                                            SPUtils.put(MyApp.getInstance(),"rolecodes",ss);
                                        }

                                    }
                                    netWorkMyData();//我的信息
                                     /*获取头像*/
                                    netGetUserHead();

                                }else {
                                    llMyData.setVisibility(View.GONE);
                                }



                            }
                        }
                    });
        }catch (Exception e){

        }


    }

    private void netGetUserHead() {
        try {
            String pwd = URLEncoder.encode(userMsgBean.getObj().getModules().getMemberPwd(),"UTF-8");
         String ingUrl =  "http://kys.zzuli.edu.cn/authentication/public/getHeadImg?memberId="+userMsgBean.getObj().getModules().getMemberUsername()+"&pwd="+pwd;

            SPUtils.put(MyApp.getInstance(),"logoUrl",ingUrl);
            Glide.with(getActivity())
                    .load(ingUrl)
                    .asBitmap()
                    .placeholder(R.mipmap.tabbar_user_pre2)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(ivUserHead);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    OAMsgListBean oaMsgListBean;
    private void dbDataList() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_workFolwDbList)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type","db")
                .params("size", 15)
                .params("workType","workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("待办列表",response.body());
                        oaMsgListBean = JSON.parseObject(response.body(), OAMsgListBean.class);
                        if (oaMsgListBean.isSuccess()) {
                            if(oaMsgListBean.getCount() > 0){
                                setRvOAMsgList2();
                            }else {
                                llOa.setVisibility(View.GONE);
                            }


                        }else {
                            llOa.setVisibility(View.GONE);
                        }

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);


                    }
                });
    }


//    CountBean countBean2;
//    /**OA消息列表*/
//    OAMsgListBean oaMsgListBean;

    CountBean countBeanEmail;


    /**OA消息列表填充*/
    CommonAdapter<MessageBean.Obj> oaAdapter;
    private List<MessageBean.Obj> mlists = new ArrayList<>();
    private void setRvOAMsgList() {
        llOa.setVisibility(View.VISIBLE);
        myOaToDoList.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        List<MessageBean.Obj> obj = messageBean.getObj();
        mlists.clear();
        if(obj != null){
            for (int i = 0; i < obj.size(); i++) {
                if(i <5){
                    mlists.add(obj.get(i));
                }else {
                    break;
                }

            }
        }
        oaAdapter = new CommonAdapter<MessageBean.Obj>(getContext(),R.layout.item_to_do_my_msg,mlists) {
            @Override
            protected void convert(ViewHolder holder, MessageBean.Obj s, int position) {
                holder.setVisible(R.id.tv_msg_num,false);
                ImageView iv = holder.getConvertView().findViewById(R.id.oa_img);
                switch (position%6){
                    case 0:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon2)
                                .into(iv);
                        break;
                    case 1:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon1)
                                .into(iv);
                        break;
                    case 2:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon2)
                                .into(iv);
                        break;
                    case 3:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon4)
                                .into(iv);
                        break;
                    case 4:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon3)
                                .into(iv);
                        break;
                    case 5:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon5)
                                .into(iv);
                        break;
                }
                String messageSendTime = s.getMessageSendTime();
                String date = timeStamp2Date(messageSendTime, "yyyy-MM-dd HH:mm:ss");
                holder.setText(R.id.tv_name,date);
                holder.setText(R.id.tv_present,s.getMessageTitle());
                holder.setText(R.id.tv_name1,s.getMemberNickname());

            }
        };
        myOaToDoList.setAdapter(oaAdapter);
        oaAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
               /* if (!oaMsgListBean.getObj().get(position).getTodourl().isEmpty()){
                    Log.e("url  ==",oaMsgListBean.getObj().get(position).getTodourl()+ "");
                    if (null != oaMsgListBean.getObj().get(position).getTodourl()){
                        Intent intent = null;
                        String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                        if(isOpen.equals("") || isOpen.equals("1")){
                            intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                        }else {
                            intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                        }
                        intent.putExtra("appUrl",oaMsgListBean.getObj().get(position).getTodourl());
                        intent.putExtra("oaMsg","oaMsg");
                        intent.putExtra("appName",oaMsgListBean.getObj().get(position).getYwlx());
                        intent.putExtra("scan","scan");
                        startActivity(intent);
                    }

                }*/
                Intent intent = new Intent(MyApp.getInstance(), InfoDetailsActivity.class);

                intent.putExtra("title2",messageBean.getObj().get(position).getMessageTitle());
                intent.putExtra("time",messageBean.getObj().get(position).getMessageSendTime()+"");
                intent.putExtra("msgsender",messageBean.getObj().get(position).getSenderName()+"");
                intent.putExtra("backlogDetailId",messageBean.getObj().get(position).getBacklogDetailId()+"");
                if ("".equals(messageBean.getObj().get(position).getMessageMobileUrl())){
                    intent.putExtra("appUrl2",messageBean.getObj().get(position).getMessageContent());

                }else if(null == messageBean.getObj().get(position).getMessageMobileUrl() ){
                    intent.putExtra("appUrl2",messageBean.getObj().get(position).getMessageContent());
                }else {
                    intent.putExtra("appUrl",messageBean.getObj().get(position).getMessageMobileUrl());
                }


                startActivity(intent);

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        oaAdapter.notifyDataSetChanged();

        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OaMsgActivity.class);
                intent.putExtra("type","1");
                intent.putExtra("msgType","待办消息");
                startActivity(intent);
            }
        });
    }


    /**OA消息列表填充*/
    CommonAdapter<OAMsgListBean.ObjBean> oaAdapter2;
    private void setRvOAMsgList2() {
        llOa.setVisibility(View.VISIBLE);
        myOaToDoList.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        oaAdapter2 = new CommonAdapter<OAMsgListBean.ObjBean>(getContext(),R.layout.item_to_do_my_msg,oaMsgListBean.getObj()) {
            @Override
            protected void convert(ViewHolder holder, OAMsgListBean.ObjBean objBean, int position) {
                holder.setVisible(R.id.tv_msg_num,false);
                ImageView iv = holder.getConvertView().findViewById(R.id.oa_img);
                switch (position%6){
                    case 0:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon2)
                                .into(iv);
                        break;
                    case 1:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon1)
                                .into(iv);
                        break;
                    case 2:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon2)
                                .into(iv);
                        break;
                    case 3:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon4)
                                .into(iv);
                        break;
                    case 4:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon3)
                                .into(iv);
                        break;
                    case 5:
                        Glide.with(mContext)
                                .load(R.mipmap.message_icon5)
                                .into(iv);
                        break;
                }
                holder.setText(R.id.tv_name,objBean.getYwlx());
                holder.setText(R.id.tv_present,objBean.getTitle());

            }
        };
        myOaToDoList.setAdapter(oaAdapter2);
        oaAdapter2.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (!oaMsgListBean.getObj().get(position).getTodourl().isEmpty()){
                    Log.e("url  ==",oaMsgListBean.getObj().get(position).getTodourl()+ "");
                    if (null != oaMsgListBean.getObj().get(position).getTodourl()){
                        Intent intent = null;
                        String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                        if(isOpen.equals("") || isOpen.equals("1")){
                            intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                        }else {
                            intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                        }
                        intent.putExtra("appUrl",oaMsgListBean.getObj().get(position).getTodourl());
                        intent.putExtra("oaMsg","oaMsg");
                        intent.putExtra("appName",oaMsgListBean.getObj().get(position).getYwlx());
                        intent.putExtra("scan","scan");
                        startActivity(intent);
                    }

                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        oaAdapter.notifyDataSetChanged();
    }
    /**我的收藏列表*/
    MyCollectionBean collectionBean;
    private void netWorkMyCollection() {
        try {
            OkGo.<String>post(UrlRes.HOME_URL + UrlRes.My_Collection)
                    .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("我的收藏列表",response.body());
                            collectionBean = JSON.parseObject(response.body(), MyCollectionBean.class);
                            if (collectionBean.isSuccess()) {
                                if(collectionBean.getObj() != null){
                                    if (collectionBean.getObj().size() > 0) {
                                        llMyCollection.setVisibility(View.VISIBLE);
                                        tvMyCollectionNum.setText(collectionBean.getObj().size() + "");
                                        setCollectionList();

                                    } else {
                                        llMyCollection.setVisibility(View.GONE);

                                    }
                                }else {
                                    llMyCollection.setVisibility(View.GONE);
                                    tvMyCollectionNum.setText("0");
                                }

                            } else {
                                llMyCollection.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            llMyCollection.setVisibility(View.GONE);

                        }
                    });
        }catch (Exception e){

        }

    }

    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;

    /**我的收藏列表填充*/
    CommonAdapter<MyCollectionBean.ObjBean> collectionAdapter;
    private void setCollectionList() {
        myCollectionList.setLayoutManager(new GridLayoutManager(getActivity(), 4){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        collectionAdapter = new CommonAdapter<MyCollectionBean.ObjBean>(getActivity(), R.layout.item_service_app, collectionBean.getObj()) {
            @Override
            protected void convert(ViewHolder holder, MyCollectionBean.ObjBean objBean, int position) {
              

                holder.setText(R.id.tv_app_name,objBean.getAppName());

                   /*appIntranet  1 需要内网*/
                if (objBean.getAppIntranet()==1){
                    holder.setVisible(R.id.iv_del,true);
                    Glide.with(getActivity())
                            .load(R.mipmap.nei_icon)
                            .error(R.color.white)
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_del));
                }else {
                    holder.setVisible(R.id.iv_del,false);
                }

                if (null != objBean.getPortalAppIcon() && null != objBean.getPortalAppIcon().getTempletAppImage()){

                    Glide.with(getActivity())
                            .load(UrlRes.HOME3_URL + objBean.getPortalAppIcon().getTempletAppImage())
                            .error(getResources().getColor(R.color.app_bg))
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_app_icon));
                }else {
                    Glide.with(getActivity())
                            .load(UrlRes.HOME3_URL + objBean.getAppImages())
                            .error(getResources().getColor(R.color.app_bg))
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_app_icon));
                }
            }
        };
        myCollectionList.setAdapter(collectionAdapter);
        collectionAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                long nowTime = System.currentTimeMillis();
                if (nowTime - mLastClickTime > TIME_INTERVAL) {
                    mLastClickTime = nowTime;
                    MyCollectionBean.ObjBean appsBean = collectionBean.getObj().get(position);
                    if (null != appsBean.getAppAndroidSchema()&& appsBean.getAppAndroidSchema().trim().length() != 0){
                        if (!isLogin){
                            Intent intent = new Intent(getActivity(),LoginActivity2.class);
                            startActivity(intent);
                        }else {

                            String appUrl =  appsBean.getAppAndroidSchema()+"";
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
                                        String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
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
                                if(null!= appsBean.getAppAndroidDownloadLink()){
                                    String dwon = appsBean.getAppAndroidDownloadLink()+"";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dwon));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                    startActivity(intent);
                                }
                            }
                        }


                    }else if (!appsBean.getAppUrl().isEmpty()){
                        if (!isLogin){
                            if(appsBean.getAppLoginFlag()==0){
                                Intent intent = new Intent(getActivity(),LoginActivity2.class);
                                startActivity(intent);
                            }else {
                                Intent intent = null;
                                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                                if(isOpen.equals("") || isOpen.equals("1")){
                                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                                }else {
                                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                }
                                if (netState.isConnect(getActivity())) {
                                    netWorkAppClick(appsBean.getAppId());
                                }
                                Log.e("url  ==",appsBean.getAppUrl() + "");
                                intent.putExtra("appUrl",appsBean.getAppUrl());
                                intent.putExtra("appId",appsBean.getAppId()+"");
                                intent.putExtra("appName",appsBean.getAppName()+"");
                                startActivity(intent);

                            }

                        }else {


                            ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication portalAppAuthentication = appsBean.getPortalAppAuthentication();
                            if(portalAppAuthentication != null){
                                String appAuthenticationFace = appsBean.getPortalAppAuthentication().getAppAuthenticationFace();
                                if(appAuthenticationFace != null){
                                    if(!appAuthenticationFace.equals("0")){
                                        permissionsUtil=  PermissionsUtil
                                                .with(MyPre2Fragment.this)
                                                .requestCode(1)
                                                .isDebug(true)//开启log
                                                .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                                .request();

                                        if(isOpen == 1){
                                            DargeFaceByMefColletUtils.cameraTask(appsBean,getActivity());
                                        }
                                    }else {
                                        DargeFaceByMefColletUtils.cameraTask(appsBean,getActivity());
                                    }

                                }else {
                                    DargeFaceByMefColletUtils.cameraTask(appsBean,getActivity());
                                }
                            }else {
                                DargeFaceByMefColletUtils.cameraTask(appsBean,getActivity());
                            }


                        }


                    }

                }



            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        collectionAdapter.notifyDataSetChanged();
    }

    boolean haveMyData =false;
    /**我的应用信息列表*/

    ServiceAppListBean allAppListBean;
    private void netWorkMyData() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Service_APP_List)
                .params("Version", "1.0")
                .params("userId", (String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("rolecodes", (String) SPUtils.get(MyApp.getInstance(),"rolecodes",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("OA消息列表",response.body());
                        allAppListBean = JSON.parseObject(response.body(),ServiceAppListBean.class);
                        if (allAppListBean.isSuccess()){

                            for (int i = 0; i < allAppListBean.getObj().size() ; i++) {
                                if (allAppListBean.getObj().get(i).getModulesName().equals("我的信息")){
                                    setMyAppDataList(allAppListBean.getObj().get(i).getApps());
                                    haveMyData = true;
                                }
                            }


                            if (haveMyData){
                                llMyData.setVisibility(View.VISIBLE);

                            }else {
                                llMyData.setVisibility(View.GONE);
                            }

                        }else {
                            llMyData.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        llMyData.setVisibility(View.GONE);
                    }
                });
    }
    /**我的应用信息列表
     * @param appsBeans*/

    private void setMyAppDataList(final List<ServiceAppListBean.ObjBean.AppsBean> appsBeans) {
        final CommonAdapter<ServiceAppListBean.ObjBean.AppsBean> myAppListAdapter;
        myDataList.setLayoutManager(new GridLayoutManager(getActivity(),4){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        myAppListAdapter = new CommonAdapter<ServiceAppListBean.ObjBean.AppsBean>(getActivity(),R.layout.item_service_app,appsBeans) {


            @Override
            protected void convert(ViewHolder holder, final ServiceAppListBean.ObjBean.AppsBean appsBean, int position) {
                holder.setText(R.id.tv_app_name, appsBean.getAppName());
                if (null != appsBean.getPortalAppIcon() && null != appsBean.getPortalAppIcon().getTempletAppImage()){

                    Glide.with(getActivity())
                            .load(UrlRes.HOME3_URL + appsBean.getPortalAppIcon().getTempletAppImage())
                            .error(getResources().getColor(R.color.app_bg))
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_app_icon));
                }else {
                    Glide.with(getActivity())
                            .load(UrlRes.HOME3_URL + appsBean.getAppImages())
                            .error(getResources().getColor(R.color.app_bg))
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_app_icon));
                }

                        /*appIntranet  1 需要内网*/
                if (appsBean.getAppIntranet()==1){
                    holder.setVisible(R.id.iv_del,true);
                    Glide.with(getActivity())
                            .load(R.mipmap.nei_icon)
                            .error(R.mipmap.nei_icon)
                            .placeholder(R.mipmap.zwt)
                            .into((ImageView) holder.getView(R.id.iv_del));
                }else {
                    holder.setVisible(R.id.iv_del,false);
                }

                holder.setOnClickListener(R.id.ll_click, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long nowTime = System.currentTimeMillis();
                        if (nowTime - mLastClickTime > TIME_INTERVAL) {
                            mLastClickTime = nowTime;
                            if (null != appsBean.getAppAndroidSchema()&& appsBean.getAppAndroidSchema().trim().length() != 0){
                                if (!isLogin){
                                    Intent intent = new Intent(getActivity(),LoginActivity2.class);
                                    startActivity(intent);
                                }else {

                                    String appUrl =  appsBean.getAppAndroidSchema()+"";
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
                                                String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
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
                                        if(null!= appsBean.getAppAndroidDownloadLink()){
                                            String dwon = appsBean.getAppAndroidDownloadLink()+"";
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dwon));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                            startActivity(intent);
                                        }
                                    }
                                }


                            }else if (!appsBean.getAppUrl().isEmpty()){
                                if (!isLogin){
                                    if(appsBean.getAppLoginFlag()==0){
                                        Intent intent = new Intent(getActivity(),LoginActivity2.class);
                                        startActivity(intent);
                                    }else {
                                        Intent intent = null;
                                        String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                                        if(isOpen.equals("") || isOpen.equals("1")){
                                            intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                                        }else {
                                            intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                        }
                                        if (netState.isConnect(getActivity())) {
                                            netWorkAppClick(appsBean.getAppId());
                                        }
                                        Log.e("url  ==",appsBean.getAppUrl() + "");
                                        intent.putExtra("appUrl",appsBean.getAppUrl());
                                        intent.putExtra("appId",appsBean.getAppId()+"");
                                        intent.putExtra("appName",appsBean.getAppName()+"");
                                        startActivity(intent);

                                    }

                                }else {

                                    ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication portalAppAuthentication = appsBean.getPortalAppAuthentication();
                                    if(portalAppAuthentication != null){
                                        String appAuthenticationFace = appsBean.getPortalAppAuthentication().getAppAuthenticationFace();
                                        if(appAuthenticationFace != null){
                                            if(!appAuthenticationFace.equals("0")){
                                                permissionsUtil=  PermissionsUtil
                                                        .with(MyPre2Fragment.this)
                                                        .requestCode(1)
                                                        .isDebug(true)//开启log
                                                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                                        .request();

                                                if(isOpen == 1){
                                                    DargeFaceByMefgUtils.cameraTask(appsBean,getActivity());
                                                }
                                            }else {
                                                DargeFaceByMefgUtils.cameraTask(appsBean,getActivity());
                                            }

                                        }else {
                                            DargeFaceByMefgUtils.cameraTask(appsBean,getActivity());
                                        }
                                    }else {
                                        DargeFaceByMefgUtils.cameraTask(appsBean,getActivity());
                                    }


                                }


                            }
                        }


                    }
                });
            }
        } ;
        myDataList.setAdapter(myAppListAdapter);
        myAppListAdapter.notifyDataSetChanged();
    }

    private boolean hasApplication(String scheme) {
        PackageManager manager = getActivity().getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse(scheme));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
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

    @Override
    public void onResume() {
        super.onResume();

        if (netState.isConnect(getActivity())){
            //netWorkSystemMsg();
            getBack();
        }else {
            ToastUtils.showToast(getActivity(),"网络连接异常");
        }

        String isLoading2 = (String) SPUtils.get(getActivity(), "isloading2", "");
        Log.e("isLoading2",isLoading2);
        if(!isLoading2 .equals("")){
            ViewUtils.createLoadingDialog2(getActivity(),true,"人脸识别中");
            SPUtils.put(getActivity(),"isloading2","");

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        SPUtils.put(getActivity(),"isloading2","");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(broadcastReceiverClose02);
        getActivity().unregisterReceiver(broadcastReceiverCollet);
        getActivity().unregisterReceiver(broadcastReceiverFace);
        getActivity().unregisterReceiver(broadcastReceiverShoucang);
        getActivity().unregisterReceiver(broadcastReceiverCAResult);
    }

    CountBean countBean1;
    private void netWorkSystemMsg2() {

        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_countUnreadMessagesForCurrentUser)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("首页系统消息",response.body());

                        countBean1 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkOAToDoMsg2();//OA待办

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    CountBean countBean2;
    /**OA消息列表*/
    private void netWorkOAToDoMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "1")//(1:待办,2:待阅,3:已办,4:已阅,5:申请)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean2 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkEmailMsg2();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void netWorkEmailMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_emai_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("邮件数量首页",response.body());
                        countBeanEmail = JSON.parseObject(response.body(), CountBean.class);
                        netWorkDyMsg2();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }




    CountBean countBean3;
    private void netWorkDyMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "2")//(1:待办,2:待阅,3:已办,4:已阅,5:申请)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean3 = JSON.parseObject(response.body(), CountBean.class);

                        int count = Integer.parseInt(countBean2.getObj());//待办
                        int count2 = 0;
                        if(countBeanEmail != null){
                            count2 = countBeanEmail.getCount();
                        }else {
                            count2 = 0;
                        }

                        int i = Integer.parseInt(countBean1.getObj());//系统消息数量
                        int count1 = Integer.parseInt(countBean2.getObj());//待办消息数量
                        int i1 = Integer.parseInt(countBean3.getObj());//待阅消息数量
                        int count22 = count2;//未读邮件消息数量
                        MyPre2Fragment.this.count = Integer.parseInt(countBean2.getObj()) + Integer.parseInt(countBean1.getObj()) + Integer.parseInt(countBean3.getObj()) +count2+ "";

                        if(null == MyPre2Fragment.this.count){
                            MyPre2Fragment.this.count = "0";
                        }

                        SPUtils.put(MyApp.getInstance(),"count", MyPre2Fragment.this.count +"");
                        if(!MyPre2Fragment.this.count.equals("") && !"0".equals(MyPre2Fragment.this.count)){
                            remind();
                            try{
                                SPUtils.put(getActivity(),"count",count+"");
                            }catch (Exception e){
                                e.getMessage();
                            }
                        }else {
                            badge1.hide();
                        }


                        int i2 = Integer.parseInt(MyPre2Fragment.this.count);
                        if(i2>99){
                            tvMyToDoMsgNum.setText("99+");
                        }else {
                            tvMyToDoMsgNum.setText(MyPre2Fragment.this.count);
                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    private void netWorkSystemMsg() {

        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_countUnreadMessagesForCurrentUser)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean1 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkOAToDoMsg();//OA寰呭姙

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    /**OA娑堟伅鍒楄〃*/
    private void netWorkOAToDoMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "db")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean2 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkEmailMsg();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void netWorkEmailMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_emai_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("邮件",response.body());
                        countBeanEmail = JSON.parseObject(response.body(), CountBean.class);
                        netWorkDyMsg();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }



    private void netWorkDyMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "dy")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean3 = JSON.parseObject(response.body(), CountBean.class);

                        int count = countBean2.getCount();
                        int count1 = countBean3.getCount();
                        int count2 = 0;
                        if(countBeanEmail != null){
                            count2 = countBeanEmail.getCount();
                        }else {
                            count2 = 0;
                        }

                        MyPre2Fragment.this.count = countBean2.getCount() + Integer.parseInt(countBean1.getObj()) + countBean3.getCount() +count2+ "";
                        if(null == MyPre2Fragment.this.count){
                            MyPre2Fragment.this.count = "0";
                        }

                        SPUtils.put(MyApp.getInstance(),"count", MyPre2Fragment.this.count +"");
                        if(!MyPre2Fragment.this.count.equals("") && !"0".equals(MyPre2Fragment.this.count)){
                            remind();
                            try{
                                SPUtils.put(getActivity(),"count",count+"");
                            }catch (Exception e){
                                e.getMessage();
                            }
                        }else {
                            badge1.hide();
                        }
                        if(Integer.parseInt(MyPre2Fragment.this.count)>99){
                            tvMyToDoMsgNum.setText("99+");
                        }else {
                            tvMyToDoMsgNum.setText(MyPre2Fragment.this.count);
                        }
                        //tvMyToDoMsgNum.setText(MyPre2Fragment.this.count);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        isLogin = !StringUtils.isEmpty((String) SPUtils.get(MyApp.getInstance(),"username",""));

        if (!isLogin){
            badge1.hide();
        }else {
            if(!hidden){
                Log.e("我的页面fragment","执行了");
                //getBack();
                netInsertPortal("4");
            }

           /* netWorkSystemMsg();
            netInsertPortal("4");*/
        }

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

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(permissionsUtil != null){
            permissionsUtil.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
