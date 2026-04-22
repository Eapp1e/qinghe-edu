package com.eapple.common.utils.spring;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.eapple.common.utils.StringUtils;

/**
 * Spring 容器工具类，可在非 Spring 管理对象中获取 Bean。
 *
 * @author Eapp1e
 */
@Component
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware
{
    /** Spring BeanFactory 容器 */
    private static ConfigurableListableBeanFactory beanFactory;

    /** Spring 应用上下文 */
    private static ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 根据名称获取 Bean。
     *
     * @param name Bean 名称
     * @return Bean 实例
     * @throws BeansException 获取失败时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException
    {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 根据类型获取 Bean。
     *
     * @param clz Bean 类型
     * @return Bean 实例
     * @throws BeansException 获取失败时抛出
     */
    public static <T> T getBean(Class<T> clz) throws BeansException
    {
        return beanFactory.getBean(clz);
    }

    /**
     * 判断容器中是否包含指定 Bean。
     *
     * @param name Bean 名称
     * @return 是否存在
     */
    public static boolean containsBean(String name)
    {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断指定 Bean 是否为单例。
     *
     * @param name Bean 名称
     * @return 是否为单例
     * @throws NoSuchBeanDefinitionException Bean 不存在时抛出
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.isSingleton(name);
    }

    /**
     * 获取指定 Bean 的类型。
     *
     * @param name Bean 名称
     * @return Bean 类型
     * @throws NoSuchBeanDefinitionException Bean 不存在时抛出
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getType(name);
    }

    /**
     * 获取指定 Bean 的别名。
     *
     * @param name Bean 名称
     * @return Bean 别名数组
     * @throws NoSuchBeanDefinitionException Bean 不存在时抛出
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取当前 AOP 代理对象。
     *
     * @param invoker 原始对象
     * @return 代理对象或原始对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker)
    {
        Object proxy = AopContext.currentProxy();
        if (((Advised) proxy).getTargetSource().getTargetClass() == invoker.getClass())
        {
            return (T) proxy;
        }
        return invoker;
    }

    /**
     * 获取当前激活的环境配置。
     *
     * @return 激活的环境数组
     */
    public static String[] getActiveProfiles()
    {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前首个激活环境。
     *
     * @return 当前激活环境
     */
    public static String getActiveProfile()
    {
        final String[] activeProfiles = getActiveProfiles();
        return StringUtils.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

    /**
     * 获取环境中的必需配置项。
     *
     * @param key 配置键
     * @return 配置值
     */
    public static String getRequiredProperty(String key)
    {
        return applicationContext.getEnvironment().getRequiredProperty(key);
    }
}
