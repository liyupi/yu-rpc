package com.yupi.yurpc.utils;

import com.yupi.yurpc.config.RpcConfig;
import com.yupi.yurpc.constant.RpcConstant;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ConfigUtilsTest extends TestCase {

    public void testLoadConfig() {
        RpcConfig config = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        log.info("config: {}", config);
    }
}