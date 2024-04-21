package com.yupi.yurpc.utils;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置工具类
 * <p>
 * 加载配置文件规则：
 * <p>conf/application.properties > application.properties > conf/application.yaml >
 * application.yaml > conf/application.yml > application.yml</p>
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">程序员鱼皮的编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
public class ConfigUtils {

    private static final String BASE_PATH_DIR = "conf/";
    private static final String BASE_CONF_FILE_NAME = "application";
    private static final String PROPERTIES_FILE_EXT = ".properties";
    private static final String YAML_FILE_EXT = ".yaml";
    private static final String YML_FILE_EXT = ".yml";
    private static final String ENV_SPLIT = "-";

    /**
     * 加载配置
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param <T>    T
     * @return props
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置
     * <p>
     * 优先加载 properties, 找不到再加载 yaml / yml
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param env    environment
     * @param <T>    T
     * @return props
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String env) {
        T props;
        return (props = loadProperties(clazz, prefix, env)) != null ? props
                : loadYaml(clazz, prefix, env);
    }

    /**
     * 加载 properties 配置 application-{env}.properties
     * <p>
     * 优先加载 conf/conf.properties, 找不到再加载 conf.properties
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param env    environment
     * @param <T>    T
     * @return props
     */
    public static <T> T loadProperties(Class<T> clazz, String prefix, String env) {
        try {
            return doLoadProperties(clazz, BASE_PATH_DIR + BASE_CONF_FILE_NAME, prefix, env);
        } catch (NoResourceException e) {
            log.warn(
                    "Not exists properties conf file in [{}], will load properties file from classpath",
                    BASE_PATH_DIR);
        }
        try {
            return doLoadProperties(clazz, BASE_CONF_FILE_NAME, prefix, env);
        } catch (NoResourceException e) {
            log.warn("Not exists properties conf file,  will load yaml/yml file from classpath");
        }
        return null;
    }

    /**
     * 加载 yaml 配置 application-{env}.yaml / application-{env}.yml
     * <p>
     * 优先加载 conf/conf.yaml, 找不到再加载 conf.yaml，其次加载 conf/conf.yml, 找不到再加载 conf.yml
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param env    environment
     * @param <T>    T
     * @return props
     */
    public static <T> T loadYaml(Class<T> clazz, String prefix, String env) {
        // 读取 yaml 文件，优先读取 conf/application-{env}.yaml
        try {
            return doLoadYaml(clazz, BASE_PATH_DIR + BASE_CONF_FILE_NAME, prefix, env,
                    YAML_FILE_EXT);
        } catch (NoResourceException e) {
            log.warn("Not exists yaml conf file in [{}], will load yaml file from classpath",
                    BASE_PATH_DIR);
        }
        // 加载 application-{env}.yaml 文件
        try {
            return doLoadYaml(clazz, BASE_CONF_FILE_NAME, prefix, env,
                    YAML_FILE_EXT);
        } catch (NoResourceException e) {
            log.warn("Not exists yaml conf file in [{}], will load yml file", BASE_PATH_DIR);
        }
        // 读取 yml 文件，优先读取 conf/application-{env}.yml
        try {
            return doLoadYaml(clazz, BASE_PATH_DIR + BASE_CONF_FILE_NAME, prefix, env,
                    YML_FILE_EXT);
        } catch (NoResourceException e) {
            log.warn("Not exists yml conf file in [{}], will load yml file from classpath",
                    BASE_PATH_DIR);
        }
        // 加载 application-{env}.yml 文件
        try {
            return doLoadYaml(clazz, BASE_CONF_FILE_NAME, prefix, env,
                    YML_FILE_EXT);
        } catch (NoResourceException e) {
            log.error("no conf file!");
            throw e;
        }
    }

    /**
     * 加载 properties 配置 application-{env}.properties
     *
     * @param clazz  clazz
     * @param base   base path
     * @param prefix properties common prefix
     * @param env    environment
     * @param <T>    T
     * @return props
     */
    public static <T> T doLoadProperties(Class<T> clazz, String base, String prefix, String env)
            throws NoResourceException {
        String confFilePath = buildConfigFilePath(base, env, PROPERTIES_FILE_EXT);
        Props props = new Props(confFilePath);
        return props.toBean(clazz, prefix);
    }

    /**
     * 加载 yaml 配置 application-{env}.yaml / application-{env}.yml
     *
     * @param clazz  clazz
     * @param base   base path
     * @param prefix properties common prefix
     * @param env    environment
     * @param ext    file extension
     * @param <T>    T
     * @return props
     */
    public static <T> T doLoadYaml(Class<T> clazz, String base, String prefix, String env,
            String ext) throws NoResourceException {
        String confFilePath = buildConfigFilePath(base, env, ext);
        Map<String, Object> props = YamlUtil.loadByPath(confFilePath);
        JSONObject rpcConfigProps = JSONUtil.parseObj(props).getJSONObject(prefix);
        return JSONUtil.toBean(rpcConfigProps, clazz);
    }

    /**
     * 构建配置文件路径
     *
     * @param base base path
     * @param env  environment
     * @param ext  file extension
     * @return config file path
     */
    private static String buildConfigFilePath(String base, String env, String ext) {
        StringBuilder configFileBuilder = new StringBuilder(base);
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append(ENV_SPLIT).append(env);
        }
        configFileBuilder.append(ext);
        return configFileBuilder.toString();
    }
}
