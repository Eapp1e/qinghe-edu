package com.eapple.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.common.core.domain.entity.SysDept;

/**
 * 閮ㄩ棬绠＄悊 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysDeptMapper
{
    /**
     * 鏌ヨ閮ㄩ棬绠＄悊鏁版嵁
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 閮ㄩ棬淇℃伅闆嗗悎
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ閮ㄩ棬鏍戜俊鎭?
     * 
     * @param roleId 瑙掕壊ID
     * @param deptCheckStrictly 閮ㄩ棬鏍戦€夋嫨椤规槸鍚﹀叧鑱旀樉绀?
     * @return 閫変腑閮ㄩ棬鍒楄〃
     */
    public List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    /**
     * 鏍规嵁閮ㄩ棬ID鏌ヨ淇℃伅
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 閮ㄩ棬淇℃伅
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 鏍规嵁ID鏌ヨ鎵€鏈夊瓙閮ㄩ棬
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 閮ㄩ棬鍒楄〃
     */
    public List<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 鏍规嵁ID鏌ヨ鎵€鏈夊瓙閮ㄩ棬锛堟甯哥姸鎬侊級
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 瀛愰儴闂ㄦ暟
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 鏄惁瀛樺湪瀛愯妭鐐?
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    public int hasChildByDeptId(Long deptId);

    /**
     * 鏌ヨ閮ㄩ棬鏄惁瀛樺湪鐢ㄦ埛
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    public int checkDeptExistUser(Long deptId);

    /**
     * 鏍￠獙閮ㄩ棬鍚嶇О鏄惁鍞竴
     * 
     * @param deptName 閮ㄩ棬鍚嶇О
     * @param parentId 鐖堕儴闂↖D
     * @return 缁撴灉
     */
    public SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 鏂板閮ㄩ棬淇℃伅
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    public int insertDept(SysDept dept);

    /**
     * 淇敼閮ㄩ棬淇℃伅
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    public int updateDept(SysDept dept);

    /**
     * 淇敼鎵€鍦ㄩ儴闂ㄦ甯哥姸鎬?
     * 
     * @param deptIds 閮ㄩ棬ID缁?
     */
    public void updateDeptStatusNormal(Long[] deptIds);

    /**
     * 淇敼瀛愬厓绱犲叧绯?
     * 
     * @param depts 瀛愬厓绱?
     * @return 缁撴灉
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 淇濆瓨閮ㄩ棬鎺掑簭
     *
     * @param dept 閮ㄩ棬淇℃伅
     */
    public void updateDeptSort(SysDept dept);

    /**
     * 鍒犻櫎閮ㄩ棬绠＄悊淇℃伅
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    public int deleteDeptById(Long deptId);
}
