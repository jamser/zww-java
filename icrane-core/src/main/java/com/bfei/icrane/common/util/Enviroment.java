package com.bfei.icrane.common.util;

/**
 * @author Administrator
 */
public class Enviroment {

    /**
     *
     */
    public static final boolean RETURN_SUCCESS = true;
    /**
     *
     */
    public static final boolean RETURN_FAILE = false;
    /**
     *
     */
    public static final String RETURN_SUCCESS_CODE = "200";
    public static final String RETURN_SUCCESS_REAPEAT = "201";
    /**
     *
     */
    public static final String RETURN_UNAUTHORIZED_CODE1 = "400";
    //1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。 　　
    // 2、请求参数有误。
    public static final String RETURN_UNAUTHORIZED_CODE = "401";
    // 	当前请求需要用户验证。该响应必须包含一个适用于被请求资源的 WWW-Authenticate 信息头用以询问用户信息。
    // 客户端可以重复提交一个包含恰当的 Authorization 头信息的请求。
    // 如果当前请求已经包含了 Authorization 证书，那么401响应代表着服务器验证已经拒绝了那些证书。
    // 如果401响应包含了与前一个响应相同的身份验证询问，且浏览器已经至少尝试了一次验证，
    // 那么浏览器应当向用户展示响应中包含的实体信息，因为这个实体信息中可能包含了相关诊断信息。参见RFC 2617。
    public static final String RETURN_FAILE_CODE = "403";
    //服务器已经理解请求，但是拒绝执行它。与401响应不同的是，身份验证并不能提供任何帮助，而且这个请求也不应该被重复提交。
    // 如果这不是一个 HEAD 请求，而且服务器希望能够讲清楚为何请求不能被执行，那么就应该在实体内描述拒绝的原因。
    // 当然服务器也可以返回一个404响应，假如它不希望让客户端获得任何信息。
    //没有授权
    public static final String FAILE_CODE = "405";
    public static final String NO_VIP_CODE = "406";
    public static final String ERROR_CODE = "500";

    /**
     *
     */
    public static final String RETURN_SUCCESS_MESSAGE = "操作成功";

    /**
     *
     */
    public static final String RETURN_FAILE_MESSAGE = "操作失败";
    public static final String RETURN_INVALID_PARA_MESSAGE = "参数错误";
    public static final String RETURN_UNAUTHORIZED_MESSAGE = "没有授权";
    public static final String RETURN_NODATA_MESSAGE = "无返回数据";
    public static final String HAVE_ERROR = "系统异常";
    public static final String PLEASE_SLOW_DOWN = "请勿频繁点击";

    /**
     * 通用
     */
    public static final int MAX_TABLE_LIMIT = 1000000000;
    public static final String IOS_STATE = "1";//控制IOS部分功能开关
    public static final String RETURN_MESSAGE_TIMEOUT = "查询超时,订单未到账";
    public static final int ACCESS_CONTROL_TIME = 30;//通用访问频率限制时间
    public static final String ERROR = "未知错误";

    /**
     * 收藏相关
     */
    public static final String THERE_IS_NO_DOLL = "不存在该娃娃";
    public static final String REPEAT_LIKE = "重复收藏";
    public static final String LIKE_SUCCESS = "收藏成功";
    public static final String THERE_IS_NO_COLLECTION = "尚未收藏该娃娃";
    public static final String DISLIKE_SUCCESS = "取消收藏成功";
    public static final String NO_COLLECTION = "收藏列表为空";

    /**
     * 房间相关
     */
    public static final String ON_ONLINE = "该娃娃当前没有在线娃娃机";
    public static final String NO_LIANXIFANG = "当前没有在线练习房";
    public static final int COMMENT_TIME = 8;

    /**
     * 游戏相关
     */
    public static final String SHARE_EXCEPTION = "分享得币出错";
    public static final String YOU_ALREADY_SHARED = "您已经分享过了,或者非法参数";

    /**
     * 发货兑换相关
     */
    public static final String CREATE_ORDER_FAILED = "创建发货订单失败";
    public static final String SELECT_SENDORDER_FAILED = "查询发货娃娃失败";
    public static final String SELECT_SENDORDER_ITEMS_FAILED = "查询发货娃娃详情失败";
    public static final int ACCESS_SENDDOLL_TIME = 5;//发货兑换接口访问频率限制接口
    public static final String SELECT_EXCHANGE_FAILED = "查询兑换娃娃失败";
    public static final String UPDATE_ORDER_FAILED = "修改兑换订单失败";


    /**
     * 礼品码相关
     */
    public static final String THE_CODE_NOT_EXIST = "该礼品码不存在";
    public static final String THE_CODE_OVERDUE = "该礼品码已过期";
    public static final String THE_CODE_USE_UP = "该礼品码已兑换";
    public static final String PRIZE_SUCCESS = "兑换成功";
    public static final String PRIZE_FAILE = "兑换失败";
    public static final String CHECK_FALSE = "您已经兑换过该礼包";
    public static final String PLEASE_PRIZE_DOWN = "请在15秒后再次兑换";
    public static final int PRIZE_CONTROL_TIME = 5;

    /**
     * 登录相关
     */
    public static final String CODE_REPEAT = "重复登录";
    public static final String USERNAME_ERROR = "该用户不存在";
    public static final String USERNAME_EXIST = "该用户已存在";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String CODE_IS_NULL = "code无效";
    public static final String ACCOUNT_DISABLED = "账号已禁用";
    public static final String REGISTRATION_FAILED = "注册失败";
    public static final String LINK_SUCCESS = "绑定成功";
    public static final String LINK_NO_SUCCESS = "绑定失败";
    public static final String LINK_NO_SUCCESS2 = "该手机号已被其他账号绑定,请更换其他手机号";
    public static final String SMSCODELOGIN_ERROR = "验证码登录异常";
    public static final String SENDSMSCODELOGIN_ERROR = "验证码发送失败,请过一分钟再重试";
    public static final String SMSCODE_IS_FALSE = "验证码错误";
    public static final String SMSCODE_IS_OVER = "验证码不存在或者过期";
    public static final String LOGIN_SUCCESS_MESSAGE = "登录成功";
    public static final String MOBILE_ERROR = "手机号码错误";
    public static final String LOGIN_ERROR = "退出异常";
    public static final String LOGIN_OUT_SUCCESS = "登出成功";
    public static final String BZC = "该网段用户暂不支持手机号注册登录呢,请使用微信登录。";
    public static final String TO_WX = "该手机号未注册请使用微信注册登录";
    public static final String RISK_CONTROL_ABNORMAL = "风控信息获取异常";
    public static final String IMEI_TO_LONG = "IMEI过长";

    /**
     * banner相关
     */
    public static final String SCROLLINGBANNER_JDBC = "从数据库中获取滚动横幅成功";
    public static final String SCROLLINGBANNER_REDIS = "从缓存中获取滚动横幅成功";
    public static final String BANNER0 = "0";//banner类型0 首页banner
    public static final String BANNER1 = "1";//banner类型1 首页banner
    public static final String STARTPAGE = "2";//banner类型2 启动页
    public static final String NO_STARTPAGE = "没有启动页";//banner类型2 启动页
    public static final String POPUPAD = "3";//banner类型3 弹窗广告
    public static final String NO_POPUPAD = "没有弹窗广告";

    /**
     * 短信相关
     */
    public static final String TEXT_MESSAGING_SUCCESS = "短信发送成功";
    public static final String TEXT_MESSAGING_FAILURE = "短信发送失败,请过一分钟再重试";
    public static final String LINKMOBILE_ERROR = "手机号码已存在";
    public static final int SMS_ENDTIME = 60 * 5;//验证码过期时间(秒)


    /**
     * 支付相关
     */
    public static final String ALIPAY_ERROR = "支付宝支付出错";
    public static final String PAY_ERROR = "支付出错";
    public static final String EXCEED_THE_LIMIT = "超过限购次数";
    public static final String HAVE_BOUGHT_CARD = "已购买周卡或者月卡";
    public static final String ALIPAY_GET_ORDER_ERROR = "支付宝支付接口获取预支付订单出错";
    public static final String ALIPAY_SUCCESSFULLY_PLACED = "支付宝支付统一下单";
    public static final String WXPAY_SUCCESSFULLY_PLACED = "微信支付统一下单";
    public static final String ALIPAY_TIMEOUT_EXPRESS = "30m";
    public static final String PAY_SUCCESS = "充值成功";

    /**
     * 签到相关
     */
    public static final String SIGN_IN_ABNORMAL = "签到异常";
    public static final String GETS_SIGN_STATUS_EXCEPTION = "获取签到状态异常";
    public static final String SIGN_IN_SUCCESS = "签到成功";
    public static final String HAVE_BEEN_SIGN = "您已经签到过了";
    public static final int HAVE_BEEN_SIGN_CODE = 0;//今日已签到
    public static final String N_IN = "今日未签到";
    public static final int N_IN_CODE = 2;//今日未签到

    /**
     * 周月卡相关
     */
    public static final String COMBO_IN_ABNORMAL = "领取异常";
    public static final String GETS_COMBO_STATUS_EXCEPTION = "获取领取状态异常";
    public static final String COMBO_IN_SUCCESS = "领取成功";
    public static final String HAVE_BEEN_COMBO = "您已经领取过了";
    public static final int HAVE_BEEN_COMBO_CODE = 0;//今日已领取
    public static final String N_IN_COMBO = "今日未领取";
    public static final int N_IN_CODE_COMBO = 2;//今日未领取
    public static final String WEEKSCARD_END = "周卡已到期";
    public static final String MCARD_END = "月卡已到期";


    /**
     * 活动相关
     */
    public static final String RECEIVED_INVITATION_AWARD = "已领取过邀请奖励";
    public static final String PROHIBIT_INVITING_ONESELF = "不能邀请自己";
    public static final String ISONEIMEI_ONESELF = "相同设备无法邀请";
    public static final String INVALID_INVITATION_CODE = "无效邀请码";
    public static final String INVALID_SUCCESS_MESSAGE = "邀请成功";
    public static final String DOLL_NOTIFY_PHONE = "DOLL_NOTIFY_PHONE";
    public static final String STOCK_NOTIFY = "STOCK_NOTIFY";
    public static final String STOCK_LOWEST_NUM = "STOCK_LOWEST_NUM";//库存预警数量

    /**
     * 流水相关
     */
    public static final String CODE_INVITE_BONUS = "INVITE_BONUS";//邀请奖励
    public static final String CODE_INVITE_BONUS_COUNT = "INVITE_BONUS_COUNT";//邀请奖励上限
    public static final String TYPE_INCOME = "income";
    public static final String METHOD_INVITE = "由邀请好友获取";
    public static final String BALANCE_CHANGE_TIME = "BALANCE_CHANGE_TIME";//代理不可用余额转化时间


    /**
     * 收货地址相关
     */
    public static final int ISDEFAULTFLG = 1;
    public static final int NOTDEFAULTFLG = 0;

    /**
     * 代理相关
     */
    public static final String ADD_AGENT_SUCCESS = "添加代理成功";
    public static final String AGENT_NOT_EXIT = "添加代理成功";
    public static final String AGENT_LOGIN_FAILE = "该账户已失效或者被冻结";
    public static final String ADD_AGENT_ERROER = "添加代理失败";
    public static final String AGENT_LOGIN_ERROR = "代理登陆失败";
    public static final String AGENT_PHONE_ERROR = "代理手机号不匹配";
    public static final String AGENT_PASSWORD_ERROR = "密码不一致";
    public static final String AGENT_PHONE_NOT_EXIT = "手机号不存在，请到代理后台修改";
    public static final String ADD_AGENT_ERROER_MESSAGE = "该代理没有添加代理权限";
    public static final String AGENT_SHARE = "agent_share";

    /**
     * 充值和抓取参数
     */
    public static final int RECHARGE_TYPE = 1;
    public static final int CATCH_TYPE = 2;


}
