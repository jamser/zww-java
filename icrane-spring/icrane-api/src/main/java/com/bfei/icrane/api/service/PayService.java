package com.bfei.icrane.api.service;

import com.bfei.icrane.common.util.ResultMap;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by SUN on 2018/1/9.
 */
public interface PayService {
    String wxNotify(HttpServletRequest request);
    /*ResultMap wxpay(HttpServletRequest request, int memberId, int chargeruleid);

    ResultMap alipay(int memberId, int chargeruleid);
*/

    String aliNotify(HttpServletRequest request);

    ResultMap queryOrder(String outTradeNo);
}
