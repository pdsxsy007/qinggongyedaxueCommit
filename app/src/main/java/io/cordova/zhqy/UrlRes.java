package io.cordova.zhqy;

/**
 * Created by Administrator on 2018/11/24 0024.
 */

public class UrlRes {

    private UrlRes() {
        throw new Error("Do not need instantiate!");
    }


    /**
     * 服务器地址
     */
    public static String HOME_URL ="http://iapp.zzuli.edu.cn";
    //public static String HOME_URL ="http://192.168.30.68:8080";  //韩鹏
    //public static String HOME_URL ="http://192.168.30.28:8080";  //毛兵



    /*tgt  相关*/
    public static String HOME2_URL ="http://kys.zzuli.edu.cn";
    //public static String HOME2_URL ="http://192.168.30.68:8090";  //韩鹏
    //public static String HOME2_URL ="http://192.168.30.28:8090";  //毛兵


    /*图片前缀*/
    public static String HOME3_URL =HOME_URL+"/portal/public/getImg?path=";


    /**
     *  获取极光绑定ID
     * portal/mobile/login
     * */
    public static String Registration_Id = "/portal/mobile/equipment/add";

     /**
     * 解除极光绑定ID
     * portal/mobile/login
     * */
    public static String Relieve_Registration_Id = "/portal/mobile/equipment/del";

    /**
     * 登录
     *
     * */
    public static String loginUrl = "/cas/casApiLoginController";


    /**
     * 退出登录*/
    public static String Exit_Out = "/cas/logout";

    /**
     * 应用列表
     * */
    public static String APP_List ="/portal/mobile/findAppList";

   /**
     * 应用服务列表
     * */
    public static String Service_APP_List ="/portal/mobile/memberApp/getMemberApp";

    /**
     * 个人信息
     * */
    public static String User_Msg ="/portal/mobile/casMember/getMemberByUsername";

    /**
     * 获取用户头像
     * */
    public static String User_Head_Image="http://kys.zzuli.edu.cn/authentication/public/getHeadImg";
    /**
     *  获取服务器返回的图片地址
     * */
    public static String Get_Img_uri ="/portal/mobile/casMember/updateUserInfo";
    /**
     *
     * 上传图片到服务器
     * */
    public static String Upload_Img ="/portal/mobile/public/photoUpload";
    /**
     * 系统信息
     * */

    public static String System_Msg_List ="/portal/mobile/weiMessage/listMessageIniDtoForCurrentUser";

    /**我的收藏*/
    public static String My_Collection ="/portal/mobile/collectionApp/findCollectionAppList";

    /**添加收藏*/
    public static String Add_Collection ="/portal/mobile/collectionApp/addCollectionApp";

    /**取消收藏*/
    public static String Cancel_Collection ="/portal/mobile/collectionApp/delCollectionApp";

    /**查询应用收藏状态*/
    public static String Query_IsCollection ="/portal/mobile/collectionApp/isCollectionApp";
    /**统计当前访问目标（四大模块） */
    public static String Four_Modules ="/portal/mobile/portalAccessLog/insertPortalAccessLog";
    /**统计应用响应时间 */
    public static String APP_Time ="/portal/mobile/response/responseTime";
     /**统计应用访问量*/
    public static String APP_Click_Number ="/portal/mobile/statistical/appAccessStatistical";
    /**OA*/
    public static String Query_count="/portal/mobile/oa/workFolwDbCount";
    /**
     * 邮箱数量
     */
    public static String Query_emai_count="/portal/mobile/mailbox/emailCount";

    /**OA*/
    public static String Query_workFolwDbList="/portal/mobile/oa/workFolwDbList";
    /**
     统计该用户未读的消息数量*/
    public static String Query_countUnreadMessagesForCurrentUser="/portal/mobile/weiMessage/countUnreadMessagesForCurrentUser";

    /**
     * 更新
     */
    public static String getNewVersionInfo="/portal/mobile/config/getNewVersionInfo";
    /**
     * 查看消息接口
     */
    public static String searchMessageById="/portal/mobile/weiMessage/getMessageIniDtoForCurrentUserByDetailId";

    /**
     * 上传手机信息
     */
    public static String addMobileInfoUrl="/portal/mobile/equipment/addMobile";

    /**
     * 输入账号获取邮箱或者手机号
     */
    public static String getUserInfoByMemberIdUrl="/authentication/api/casMember/getUserInfoByMemberId";


    /**
     * 获取验证码
     */
    public static String sendVerificationUrl="/authentication/api/verification/sendVerification";

    /**
     * 验证验证码
     */
    public static String verificationUrl="/authentication/api/verification/verification";

    /**
     * 修改密码
     */
    public static String updatePasswordUrl="/authentication/api/casMember/updatePassword";

    /**
     * 获取下载类型
     */
    public static String getDownLoadTypeUrl="/portal/mobile/config/getDownLoadType";

    /**
     * app授权接口
     */
    public static String functionInvocationLogUrl="/portal/mobile/functionInvocationLog/addInvocationLog";

    /**
     * 人脸识别
     */
    public static String getPassByFaceUrl="/authentication/api/face/getPassByFace";

    /**
     * 人脸校验
     */
    public static String distinguishFaceUrl="/authentication/api/face/distinguishFace";

    /**
     * 人脸识别上传图片
     */
    public static String addFaceUrl="/authentication/api/face/addFace";

    /**
     * 一键登录
     */
    public static String loginTokenVerifyUrl="/authentication/api/jgMessage/loginTokenVerify";

    /**
     * webview上传位置阅读时间等信息
     */
    public static String addPortalReadingAccessUrl="/portal/mobile/portalReadingAccess/addPortalReadingAccess";

    /**
     * 5分钟一次上传信息
     */
    public static String insertPortalPositionUrl="/portal/mobile/portalPosition/insertPortalPosition";

    /**
     * 新生报到是否弹出人脸识别
     */
    public static String jugdeFaceUrl="/portal/mobile/newStudentRegister/jugdeFace";

    /**
     * 是否强制修改密码
     */
    public static String newStudentUpdatePwdStateUrl="/portal/mobile/newStudentRegister/newStudentUpdatePwdState";


    /**
     * 新消息数量
     */
    public static String countUserMessagesByTypeUrl="/portal/mobile/weiMessage/countUserMessagesByType";

    /**
     * 新消息列表
     */
    public static String findUserMessagesByTypeUrl="/portal/mobile/weiMessage/findUserMessagesByType";

    /**
     * 新生跳转到修改密码页面
     */
    public static String newStudentDbUrl="http://microapp.zzuli.edu.cn/microapplication/db_qy/app/newStudentDb.html";

    /**
     * 新生跳转到修改密码页面
     */
    public static String changePwdUrl="/authentication/login/casLogin?toUrl=/authentication/views/appNative/changePwd.html";

    /**
     *
     信任设备增加
     */

    public static String addTrustDevice = "/portal/mobile/trustDeviceManage/addTrustDevice";

    /**
     信任设备查询
     */
    public static String trustDeviceList = "/portal/mobile/trustDeviceManage/trustDeviceList";

    /**
     删除信任设备
     */
    public static String updateTrustDevice = "/portal/mobile/trustDeviceManage/updateTrustDevice";

   /**
    意见反馈
    */
   public static String fankuiUrl = "http://iapp.zzuli.edu.cn/portal/login/appLogin?tourl=/portal-app/app-5/feedback/feedback.html";

   public static String huanxingUrl = "http://iapp.zzuli.edu.cn/portal/portal-app/app-5/huanxing.html";
   public static String caQrCodeVerifyUrl = "/portal/mobile/ca/caQrCodeVerify";

    /**
     * 协议地址
     */
    public static String xieyiUrl = "http://kys.zzuli.edu.cn/authentication/authentication/views/appNative/privacyProtocol.html";

    public static String findLoginTypeListUrl = "/portal/mobile/config/findLoginTypeList";

    /**
     * 获取激活码
     */
    public static String getAuthCodeUrl = "/authentication/api/v1/ca/getAuthCode";
    //public static String getAuthCodeUrl = "/portal/mobile/ca/getAuthCode";

    /**
     * 查询证书
     */
    public static String queryCertUrl = "/portal/mobile/ca/queryCert";

    /**
     * 解析证书
     */
    public static String getCertInfoUrl = "/portal/mobile/ca/getCertInfo";

    /**
     * 扫码签名----扫完码之后调用此接口
     */
    public static String scanQrCodeUrl = "/portal/mobile/ca/scanQrCode";

   /**
    * 消息通知弹出页面调用此接口
    */
   public static String waiteSignUrl = "/portal/mobile/ca/waiteSign";

    /**
     * App签名---仅在消息通知打开弹出页面后，先调用SDK签名，拿到签名结果之后调用此接口
     */
    public static String clickSignUrl = "/portal/mobile/ca/clickSign";

    /**
     * 取消签名
     */
    public static String cancelSignUrl = "/portal/mobile/ca/cancelSign";

    /**
     * 上传msPid
     */
    public static String saveMemberAndCAUrl = "/authentication/api/v1/ca/saveMemberAndCA";

   /**
    * 上传错误信息到服务器
    */
   public static String signFailedUrl = "/portal/mobile/ca/signFailed";

   public static String getBacklogUrl = "/portal/mobile/backlog/getBacklog";

    /**
     * 统计该用户未读的消息数量
     */
   public static String countUnreadMessagesForCurrentUserUrl = "/portal/mobile/backlog/countUnreadMessagesForCurrentUser";

   public static String markBackLogAsReadUrl = "/portal/mobile/backlog/markBackLogAsRead";

}
