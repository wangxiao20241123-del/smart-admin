/*
 * WxFinance HTTP服务配置类
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.wecom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WxFinance HTTP服务配置
 *
 * @author wangxiao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wecom.finance")
public class WxFinanceConfig {

    /**
     * wx-finance HTTP服务地址
     */
    private String apiUrl = "http://localhost:8088";

    /**
     * 连接超时时间(毫秒)
     */
    private Integer connectTimeout = 10000;

    /**
     * 读取超时时间(毫秒)
     */
    private Integer readTimeout = 30000;

    /**
     * 写入超时时间(毫秒)
     */
    private Integer writeTimeout = 30000;
}
