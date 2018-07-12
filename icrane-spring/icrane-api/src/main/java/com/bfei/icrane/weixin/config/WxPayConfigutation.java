package com.bfei.icrane.weixin.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by moying on 2018/7/11.
 */
@Component
public class WxPayConfigutation {
    @Bean
    public WxPayConfig config() {
        WxPayConfig payConfig = new WxPayConfig();
        return payConfig;
    }

    @Bean
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(config());
        return wxPayService;
    }
}
