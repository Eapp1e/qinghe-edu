package com.qinghe.framework.aspectj;

import java.util.Collection;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson2.JSON;
import com.qinghe.common.annotation.Log;
import com.qinghe.common.core.domain.entity.SysUser;
import com.qinghe.common.core.domain.model.LoginUser;
import com.qinghe.common.core.text.Convert;
import com.qinghe.common.enums.BusinessStatus;
import com.qinghe.common.enums.HttpMethod;
import com.qinghe.common.filter.PropertyPreExcludeFilter;
import com.qinghe.common.utils.ExceptionUtil;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.common.utils.ServletUtils;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.common.utils.ip.IpUtils;
import com.qinghe.framework.manager.AsyncManager;
import com.qinghe.framework.manager.factory.AsyncFactory;
import com.qinghe.system.domain.SysOperLog;

/**
 * 操作日志记录处理切面。
 * 
 * @author Eapp1e
 */
@Aspect
@Component
public class LogAspect
{
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /** 需要排除的敏感字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };

    /** 记录请求开始时间的线程变量 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");

    /** 参数最大长度 */
    private static final int PARAM_MAX_LENGTH = 2000;

    /**
     * 在切点方法执行前记录开始时间。
     */
    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, Log controllerLog)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 方法正常返回后记录操作日志。
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult)
    {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 方法抛出异常后记录操作日志。
     * 
     * @param joinPoint 切点
     * @param e 异常信息
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult)
    {
        try
        {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();

            // *======== 数据库日志对象 ========*//
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的 IP 地址
            String ip = IpUtils.getIpAddr();
            operLog.setOperIp(ip);
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
            if (loginUser != null)
            {
                operLog.setOperName(loginUser.getUsername());
                SysUser currentUser = loginUser.getUser();
                if (StringUtils.isNotNull(currentUser) && StringUtils.isNotNull(currentUser.getDept()))
                {
                    operLog.setDeptName(currentUser.getDept().getDeptName());
                }
            }

            if (e != null)
            {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(Convert.toStr(e.getMessage(), ExceptionUtil.getExceptionMessage(e)), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 获取注解中的描述信息并组装日志
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            // 设置耗时
            operLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 异步写入日志
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
        }
        catch (Exception exp)
        {
            // 记录日志过程中的异常不影响主流程
            log.error("记录操作日志异常:{}", exp.getMessage());
            exp.printStackTrace();
        }
        finally
        {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取注解中声明的业务信息，并写入操作日志对象。
     * 
     * @param log 日志注解
     * @param operLog 操作日志对象
     * @throws Exception 异常信息
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) throws Exception
    {
        // 业务类型
        operLog.setBusinessType(log.businessType().ordinal());
        // 业务标题
        operLog.setTitle(log.title());
        // 操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否保存请求参数
        if (log.isSaveRequestData())
        {
            // 排除指定字段后再写入请求参数
            setRequestValue(joinPoint, operLog, log.excludeParamNames());
        }
        // 是否保存响应结果
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult))
        {
            operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求参数并写入日志对象。
     * 
     * @param operLog 操作日志对象
     * @throws Exception 异常信息
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog, String[] excludeParamNames) throws Exception
    {
        String requestMethod = operLog.getRequestMethod();
        Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        if (StringUtils.isEmpty(paramsMap) && StringUtils.equalsAny(requestMethod, HttpMethod.PUT.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name()))
        {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(params);
        }
        else
        {
            operLog.setOperParam(StringUtils.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, PARAM_MAX_LENGTH));
        }
    }

    /**
     * 将参数数组转换为字符串。
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames)
    {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0)
        {
            for (Object o : paramsArray)
            {
                if (StringUtils.isNotNull(o) && !isFilterObject(o))
                {
                    try
                    {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params.append(jsonObj).append(" ");
                        if (params.length() >= PARAM_MAX_LENGTH)
                        {
                            return StringUtils.substring(params.toString(), 0, PARAM_MAX_LENGTH);
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("获取请求参数异常 msg:{}, params:{}", e.getMessage(), paramsArray, e);
                    }
                }
            }
        }
        return params.toString();
    }

    /**
     * 构建需要排除字段的过滤器。
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames)
    {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 判断对象是否属于不需要记录的过滤类型。
     * 
     * @param o 待判断对象
     * @return true 表示需要过滤，false 表示可记录
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}