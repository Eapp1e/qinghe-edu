package com.eapple.framework.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import static com.google.code.kaptcha.Constants.*;

/**
 * 楠岃瘉鐮侀厤缃?
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
        // 鏄惁鏈夎竟妗?榛樿涓簍rue 鎴戜滑鍙互鑷繁璁剧疆yes锛宯o
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 楠岃瘉鐮佹枃鏈瓧绗﹂鑹?榛樿涓篊olor.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
        // 楠岃瘉鐮佸浘鐗囧搴?榛樿涓?00
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 楠岃瘉鐮佸浘鐗囬珮搴?榛樿涓?0
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 楠岃瘉鐮佹枃鏈瓧绗﹀ぇ灏?榛樿涓?0
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "38");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
        // 楠岃瘉鐮佹枃鏈瓧绗﹂暱搴?榛樿涓?
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        // 楠岃瘉鐮佹枃鏈瓧浣撴牱寮?榛樿涓簄ew Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 鍥剧墖鏍峰紡 姘寸汗com.google.code.kaptcha.impl.WaterRipple 楸肩溂com.google.code.kaptcha.impl.FishEyeGimpy 闃村奖com.google.code.kaptcha.impl.ShadowGimpy
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
        // 鏄惁鏈夎竟妗?榛樿涓簍rue 鎴戜滑鍙互鑷繁璁剧疆yes锛宯o
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 杈规棰滆壊 榛樿涓篊olor.BLACK
        properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        // 楠岃瘉鐮佹枃鏈瓧绗﹂鑹?榛樿涓篊olor.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
        // 楠岃瘉鐮佸浘鐗囧搴?榛樿涓?00
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 楠岃瘉鐮佸浘鐗囬珮搴?榛樿涓?0
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 楠岃瘉鐮佹枃鏈瓧绗﹀ぇ灏?榛樿涓?0
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
        // 楠岃瘉鐮佹枃鏈敓鎴愬櫒
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.eapple.framework.config.KaptchaTextCreator");
        // 楠岃瘉鐮佹枃鏈瓧绗﹂棿璺?榛樿涓?
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "3");
        // 楠岃瘉鐮佹枃鏈瓧绗﹂暱搴?榛樿涓?
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
        // 楠岃瘉鐮佹枃鏈瓧浣撴牱寮?榛樿涓簄ew Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 楠岃瘉鐮佸櫔鐐归鑹?榛樿涓篊olor.BLACK
        properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
        // 骞叉壈瀹炵幇绫?
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 鍥剧墖鏍峰紡 姘寸汗com.google.code.kaptcha.impl.WaterRipple 楸肩溂com.google.code.kaptcha.impl.FishEyeGimpy 闃村奖com.google.code.kaptcha.impl.ShadowGimpy
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
