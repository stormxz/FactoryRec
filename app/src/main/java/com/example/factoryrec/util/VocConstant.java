package com.example.factoryrec.util;

import android.Manifest;
/**
 * 常量
 *
 * @author yangcheng
 */
public class VocConstant {

    // 调试模式（调试true，正式false）
    public static final boolean DEBUG_MODE = false;
    // 短信校验（调试true，正式false）
    public static final boolean SMS_CHECK_OFF = false;

    //public static final boolean IS_BETA_USER = false;
    public static final boolean IS_BETA_USER = true;

    //是否打印LOG日志（调试true，正式false）
    public static final boolean IS_PRINT_LOG = true;

    // 服务器请求地址
    public static String HTTP_BS_URL = "http://bs.coolpad.com";// 电商
    public static String HTTP_EC_URL = "http://ecms.coolpad.com/ecms";// 电商
    public static String HTTP_EC_R_URL = "http://res.coolpad.com";// 电商
    public static String HTTP_EC_JD_URL = "http://ecms.coolpad.com/ecms";//电商京东
    public static String HTTP_BBS_URL = "http://bbs.coolpad.com";// 酷友社区
    public static String HTTP_CSC_URL = "http://m.yulong.com/moacsc";// CSC
    public static String HTTP_QMS_URL = "http://m.yulong.com/moaqms";// QMS
    public static String HTTP_MSI_URL = "http://m.yulong.com/msi";// MSI


    public static String HTTP_PRICE_URL = "http://mall1.coolpad.com/questionnaire/part.html";//配件查询URL
    public static String HTTP_QUESTION_URL = "http://mall1.coolpad.com/questionnaire/index.html";//问卷调查URL
    //    public static String HTTP_QUESTION_URL = "http://172.16.42.200/questionnaire/part.html";//问卷调查URL
    //    public static String HTTP_PRICE_URL = "http://172.16.42.200/questionnaire/index.html";//配件查询URL


    static {
        if (DEBUG_MODE) {

            // 测试环境地址
            HTTP_BS_URL = "http://10.1.21.30";
            HTTP_EC_URL = "http://10.1.21.31/ecms";
            HTTP_EC_R_URL = "http://10.1.21.8";
            HTTP_EC_JD_URL = "http://58.251.137.53/ecms";
            HTTP_BBS_URL = "http://bbs.coolpad.com";
            HTTP_CSC_URL = "http://192.168.87.50/CSC";
            HTTP_QMS_URL = "http://192.168.87.50";
            HTTP_MSI_URL = "http://172.16.4.103";

            // 开发环境
            //            HTTP_CSC_URL = "http://192.168.105.32:8080/CSCApp";
            //            HTTP_QMS_URL = "http://192.168.105.25:8080/QMS";
            //            HTTP_MSI_URL = "http://10.2.39.90:8080/MSM";
        }
    }

    // 统一编码
    public static final String CODE_UNICODE = "UTF-8";

    // 系统标识
    public static final String SYSTEM_CODE = "VOC";

    // SharedPreferences标识
    public static final String COMMON_SHARED_KEY = "com.yulong.mobile.voc";

    // 页面大小（默认20条）
    public static final int PAGE_SIZE = 20;

    // 请求成功
    public static final String REQUEST_SUCCESS = "S";

    // 请求失败
    public static final String REQUEST_FAIL = "E";

    // 请求成功
    public static final String REQUEST_MSG_OK = "ok";

    // 请求失败
    public static final String REQUEST_MSG_ERROR = "error";

    // 数据缓存路径
    public static final String DATA_CACHE_DIR = "/yulong/" + SYSTEM_CODE + "/cache/datas";

    // 图片缓存路径
    public static final String IMAGE_CACHE_DIR = "/yulong/" + SYSTEM_CODE + "/cache/images";

    // 视频缓存路径
    public static final String VEDIO_CACHE_DIR = "/yulong/" + SYSTEM_CODE + "/cache/vedios";

    // 网页标题
    public static final String INTENT_WEB_TITLE = "intent_web_title";

    // 网页地址
    public static final String INTENT_WEB_URL = "intent_web_url";

    // 是否启用标题栏
    public static final String INTENT_WEB_NAVIGATION = "intent_web_navigation";

    // 视频目录
    public static final String KEY_CURRENT_VEDIO_DIR = "key_current_vedio_dir";

    // 电商地址的KEY
    public static final String KEY_OR_ADDRESS = "ket_or_address";

    // 用户ID
    public static final String KEY_USER_ID = "key_user_id";

    // 会话ID
    public static final String KEY_TKT_ID = "key_tkt_id";

    // 用户名称
    public static final String KEY_USER_NAME = "key_user_name";

    //用户网络头像地址
    public static final String KEY_USER_NETWORK_HEAD_IMAGE = "headimage";
    //用户本地头像地址
    public static final String KEY_USER_LOCAL_HEAD_IMAGE = "localAvatarUrl";

    //日志
    public static final String FEED_BACK_uplode_log_KEY = "feed_back_uplode_log";

    //运行是权限组
    public static final String[] PERMISSIONS =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String APP_ID = "3030004979";

    public static final String REMOTE_SCHEME = "http://";

    public static final String REMOTE_HOST_IP = "betalog.coolpad.com:6767";
    //public static final String REMOTE_HOST_IP = "172.16.3.206:6767";
    //    public static final String REMOTE_HOST_IP = "10.2.46.32:6767";
    public static final String REMOTE_URL = REMOTE_SCHEME + REMOTE_HOST_IP;

    public static final int REQUEST_CODE_NETWORK_SCORE_AWARD = 302;
    public static final int REQUEST_CODE_NETWORK_CREATE_EVENT = 302;
    public static final int REQUEST_CODE_NETWORK_REPORT_LIST = 302;

    public static final int REQUEST_CODE_NETWORK_SERVER_LIST = 301;

    //    是否为BATE用户
    public static final String IS_BETA_UESR = "key_beta_user";

    public static Boolean LogBusy = false;
    public static Boolean ScreenRecording = false;
    //录屏之后跳转到提交反馈页面，显示录屏内容的标记
    public static boolean isUpdateScreenResult = false;
    //录屏log标记
    public static boolean isUpdateOfflineLogResult = false;
    //three items need Refresh OnResume   1.first into 2.home background 3.login or logout
    public static boolean isRefreshPersonFragmentOnResume = false;
    //新建feedback 添加图片时，完成录屏时不新建feedback ，解决重复创建feedback的问题
    public static boolean isCreateNewFeedBack = false;


    //保存日志开关状态初始值
    public static final String PERSIST_SYS_OFFLINELOG = "persist.sys.offlinelog";
    public static final String PERSIST_SYS_ZSLOG_LOGCAT = "persist.zslogd.logcatd";
    public static final String PERSIST_SYS_ZSLOG_MODEM = "persist.zslogd.mdlogd";
    public static final String PERSIST_SYS_ZSLOG_GPS = "persist.zslogd.gpslogd";
    public static final String PERSIST_SYS_ZSLOG_WLAN = "persist.zslogd.wlanlogd";
    public static final String PERSIST_SYS_ZSLOG_SENSOR = "persist.zslogd.sensorlogd";
    public static final String PERSIST_SYS_ZSLOG_TCP = "persist.zslogd.tcpdumpd";
    public static final String PERSIST_ZSLOG_BATTD = "persist.zslogd.battd";
    public static final String PERSIST_ZSLOG_BT = "persist.zslogd.btlogd";
    public static final String PERSIST_SYS_DOWNLOAD_MOD = "persist.sys.download_mode";
    public static final String PERSIST_SYS_SSR_RESTART_LEVEL = "persist.sys.ssr.restart_level";
    public static final String PERSIST_SYS_SSR_ENABLE_RAMDUMPS = "persist.sys.ssr.enable_ramdumps";
    public static final String PERSIST_CAMERA_MOBICAT = "persist.camera.mobicat";
}
