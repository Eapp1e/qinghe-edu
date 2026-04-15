package com.eapple.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 鎿嶄綔鏃ュ織璁板綍琛?oper_log
 * 
 * @author Eapp1e
 */
public class SysOperLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 鏃ュ織涓婚敭 */
    @Excel(name = "鎿嶄綔搴忓彿", cellType = ColumnType.NUMERIC)
    private Long operId;

    /** 鎿嶄綔妯″潡 */
    @Excel(name = "鎿嶄綔妯″潡")
    private String title;

    /** 涓氬姟绫诲瀷锛?鍏跺畠 1鏂板 2淇敼 3鍒犻櫎锛?*/
    @Excel(name = "涓氬姟绫诲瀷", readConverterExp = "0=鍏跺畠,1=鏂板,2=淇敼,3=鍒犻櫎,4=鎺堟潈,5=瀵煎嚭,6=瀵煎叆,7=寮洪€€,8=鐢熸垚浠ｇ爜,9=娓呯┖鏁版嵁")
    private Integer businessType;

    /** 涓氬姟绫诲瀷鏁扮粍 */
    private Integer[] businessTypes;

    /** 璇锋眰鏂规硶 */
    @Excel(name = "璇锋眰鏂规硶")
    private String method;

    /** 璇锋眰鏂瑰紡 */
    @Excel(name = "璇锋眰鏂瑰紡")
    private String requestMethod;

    /** 鎿嶄綔绫诲埆锛?鍏跺畠 1鍚庡彴鐢ㄦ埛 2鎵嬫満绔敤鎴凤級 */
    @Excel(name = "鎿嶄綔绫诲埆", readConverterExp = "0=鍏跺畠,1=鍚庡彴鐢ㄦ埛,2=鎵嬫満绔敤鎴?)
    private Integer operatorType;

    /** 鎿嶄綔浜哄憳 */
    @Excel(name = "鎿嶄綔浜哄憳")
    private String operName;

    /** 閮ㄩ棬鍚嶇О */
    @Excel(name = "閮ㄩ棬鍚嶇О")
    private String deptName;

    /** 璇锋眰url */
    @Excel(name = "璇锋眰鍦板潃")
    private String operUrl;

    /** 鎿嶄綔鍦板潃 */
    @Excel(name = "鎿嶄綔鍦板潃")
    private String operIp;

    /** 鎿嶄綔鍦扮偣 */
    @Excel(name = "鎿嶄綔鍦扮偣")
    private String operLocation;

    /** 璇锋眰鍙傛暟 */
    @Excel(name = "璇锋眰鍙傛暟")
    private String operParam;

    /** 杩斿洖鍙傛暟 */
    @Excel(name = "杩斿洖鍙傛暟")
    private String jsonResult;

    /** 鎿嶄綔鐘舵€侊紙0姝ｅ父 1寮傚父锛?*/
    @Excel(name = "鐘舵€?, readConverterExp = "0=姝ｅ父,1=寮傚父")
    private Integer status;

    /** 閿欒娑堟伅 */
    @Excel(name = "閿欒娑堟伅")
    private String errorMsg;

    /** 鎿嶄綔鏃堕棿 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "鎿嶄綔鏃堕棿", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    /** 娑堣€楁椂闂?*/
    @Excel(name = "娑堣€楁椂闂?, suffix = "姣")
    private Long costTime;

    public Long getOperId()
    {
        return operId;
    }

    public void setOperId(Long operId)
    {
        this.operId = operId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(Integer businessType)
    {
        this.businessType = businessType;
    }

    public Integer[] getBusinessTypes()
    {
        return businessTypes;
    }

    public void setBusinessTypes(Integer[] businessTypes)
    {
        this.businessTypes = businessTypes;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public Integer getOperatorType()
    {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType)
    {
        this.operatorType = operatorType;
    }

    public String getOperName()
    {
        return operName;
    }

    public void setOperName(String operName)
    {
        this.operName = operName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getOperUrl()
    {
        return operUrl;
    }

    public void setOperUrl(String operUrl)
    {
        this.operUrl = operUrl;
    }

    public String getOperIp()
    {
        return operIp;
    }

    public void setOperIp(String operIp)
    {
        this.operIp = operIp;
    }

    public String getOperLocation()
    {
        return operLocation;
    }

    public void setOperLocation(String operLocation)
    {
        this.operLocation = operLocation;
    }

    public String getOperParam()
    {
        return operParam;
    }

    public void setOperParam(String operParam)
    {
        this.operParam = operParam;
    }

    public String getJsonResult()
    {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult)
    {
        this.jsonResult = jsonResult;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public Date getOperTime()
    {
        return operTime;
    }

    public void setOperTime(Date operTime)
    {
        this.operTime = operTime;
    }

    public Long getCostTime()
    {
        return costTime;
    }

    public void setCostTime(Long costTime)
    {
        this.costTime = costTime;
    }
}
