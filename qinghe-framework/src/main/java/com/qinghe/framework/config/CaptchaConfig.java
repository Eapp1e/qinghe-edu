package com.qinghe.framework.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import static com.google.code.kaptcha.Constants.*;

/**
 * 验证码配置。
 *
 * @author Eapp1e
 */
@Configuration
public class CaptchaConfig
{
    @Bean(name = "captchaProducer")
    public DefaultKaptcha getKaptchaBean()
    {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框，默认使用 yes
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 验证码文本颜色
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
        // 图片宽度
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 图片高度
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 字体大小
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "38");
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
        // 字符长度
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        // 字体名称
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 干扰实现类
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @Bean(name = "captchaProducerMath")
    public DefaultKaptcha getKaptchaBeanMath()
    {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 边框颜色
        properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        // 文本颜色
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
        // 图片宽度
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 图片高度
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 字体大小
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35");
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
        // 数学表达式生成器
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.qinghe.framework.config.KaptchaTextCreator");
        // 字符间距
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "3");
        // 字符长度
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
        // 字体名称
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 噪点颜色
        properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
        // 不使用额外噪点
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 干扰实现类
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}