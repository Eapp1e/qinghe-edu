package com.eapple.system.service;

import java.util.List;
import com.eapple.common.core.domain.TreeSelect;
import com.eapple.common.core.domain.entity.SysDept;

/**
 * 閮ㄩ棬绠＄悊 鏈嶅姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysDeptService
{
    /**
     * 鏌ヨ閮ㄩ棬绠＄悊鏁版嵁
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 閮ㄩ棬淇℃伅闆嗗悎
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 鏌ヨ閮ㄩ棬鏍戠粨鏋勪俊鎭?
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 閮ㄩ棬鏍戜俊鎭泦鍚?
     */
    public List<TreeSelect> selectDeptTreeList(SysDept dept);

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佹爲缁撴瀯
     * 
     * @param depts 閮ㄩ棬鍒楄〃
     * @return 鏍戠粨鏋勫垪琛?
     */
    public List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佷笅鎷夋爲缁撴瀯
     * 
     * @param depts 閮ㄩ棬鍒楄〃
     * @return 涓嬫媺鏍戠粨鏋勫垪琛?
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ閮ㄩ棬鏍戜俊鎭?
     * 
     * @param roleId 瑙掕壊ID
     * @return 閫変腑閮ㄩ棬鍒楄〃
     */
    public List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 鏍规嵁閮ㄩ棬ID鏌ヨ淇℃伅
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 閮ㄩ棬淇℃伅
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 鏍规嵁ID鏌ヨ鎵€鏈夊瓙閮ㄩ棬锛堟甯哥姸鎬侊級
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 瀛愰儴闂ㄦ暟
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 鏄惁瀛樺湪閮ㄩ棬瀛愯妭鐐?
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    public boolean hasChildByDeptId(Long deptId);

    /**
     * 鏌ヨ閮ㄩ棬鏄惁瀛樺湪鐢ㄦ埛
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉 true 瀛樺湪 false 涓嶅瓨鍦?
     */
    public boolean checkDeptExistUser(Long deptId);

    /**
     * 鏍￠獙閮ㄩ棬鍚嶇О鏄惁鍞竴
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    public boolean checkDeptNameUnique(SysDept dept);

    /**
     * 鏍￠獙閮ㄩ棬鏄惁鏈夋暟鎹潈闄?
     * 
     * @param deptId 閮ㄩ棬id
     */
    public void checkDeptDataScope(Long deptId);

    /**
     * 鏂板淇濆瓨閮ㄩ棬淇℃伅
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    public int insertDept(SysDept dept);

    /**
     * 淇敼淇濆瓨閮ㄩ棬淇℃伅
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    public int updateDept(SysDept dept);

    /**
     * 淇濆瓨閮ㄩ棬鎺掑簭
     *
     * @param deptIds 閮ㄩ棬ID鏁扮粍
     * @param orderNums 鎺掑簭鏁扮粍
     */
    public void updateDeptSort(String[] deptIds, String[] orderNums);

    /**
     * 鍒犻櫎閮ㄩ棬绠＄悊淇℃伅
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    public int deleteDeptById(Long deptId);
}
