package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysLogininfor;

/**
 * 系统访问日志数据层。
 * 
 * @author Eapp1e
 */
public interface SysLogininforMapper
{
    /**
     * 新增系统登录日志。
     * 
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合。
     * 
     * @param logininfor 访问日志对象
     * @return 登录日志集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 批量删除系统登录日志。
     * 
     * @param infoIds 访问日志 ID 数组
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志。
     * 
     * @return 结果
     */
    public int cleanLogininfor();
}
