package com.eapple.framework.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.eapple.common.constant.HttpStatus;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.text.Convert;
import com.eapple.common.exception.DemoModeException;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.html.EscapeUtil;

/**
 * 鍏ㄥ眬寮傚父澶勭悊鍣?
 * 
 * @author Eapp1e
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 鏉冮檺鏍￠獙寮傚父
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("璇锋眰鍦板潃'{}',鏉冮檺鏍￠獙澶辫触'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "娌℃湁鏉冮檺锛岃鑱旂郴绠＄悊鍛樻巿鏉?);
    }

    /**
     * 璇锋眰鏂瑰紡涓嶆敮鎸?
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("璇锋眰鍦板潃'{}',涓嶆敮鎸?{}'璇锋眰", requestURI, e.getMethod());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 涓氬姟寮傚父
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 璇锋眰璺緞涓己灏戝繀闇€鐨勮矾寰勫彉閲?
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public AjaxResult handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("璇锋眰璺緞涓己灏戝繀闇€鐨勮矾寰勫彉閲?{}',鍙戠敓绯荤粺寮傚父.", requestURI, e);
        return AjaxResult.error(String.format("璇锋眰璺緞涓己灏戝繀闇€鐨勮矾寰勫彉閲廩%s]", e.getVariableName()));
    }

    /**
     * 璇锋眰鍙傛暟绫诲瀷涓嶅尮閰?
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String value = Convert.toStr(e.getValue());
        if (StringUtils.isNotEmpty(value))
        {
            value = EscapeUtil.clean(value);
        }
        log.error("璇锋眰鍙傛暟绫诲瀷涓嶅尮閰?{}',鍙戠敓绯荤粺寮傚父.", requestURI, e);
        return AjaxResult.error(String.format("璇锋眰鍙傛暟绫诲瀷涓嶅尮閰嶏紝鍙傛暟[%s]瑕佹眰绫诲瀷涓猴細'%s'锛屼絾杈撳叆鍊间负锛?%s'", e.getName(), e.getRequiredType().getName(), value));
    }

    /**
     * 鎷︽埅鏈煡鐨勮繍琛屾椂寮傚父
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("璇锋眰鍦板潃'{}',鍙戠敓鏈煡寮傚父.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 绯荤粺寮傚父
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("璇锋眰鍦板潃'{}',鍙戠敓绯荤粺寮傚父.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 鑷畾涔夐獙璇佸紓甯?
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 鑷畾涔夐獙璇佸紓甯?
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 婕旂ず妯″紡寮傚父
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e)
    {
        return AjaxResult.error("婕旂ず妯″紡锛屼笉鍏佽鎿嶄綔");
    }
}
