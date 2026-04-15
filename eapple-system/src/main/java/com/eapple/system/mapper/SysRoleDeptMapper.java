package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysRoleDept;

/**
 * 瑙掕壊涓庨儴闂ㄥ叧鑱旇〃 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysRoleDeptMapper
{
    /**
     * 閫氳繃瑙掕壊ID鍒犻櫎瑙掕壊鍜岄儴闂ㄥ叧鑱?
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    public int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 鎵归噺鍒犻櫎瑙掕壊閮ㄩ棬鍏宠仈淇℃伅
     * 
     * @param ids 闇€瑕佸垹闄ょ殑鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteRoleDept(Long[] ids);

    /**
     * 鏌ヨ閮ㄩ棬浣跨敤鏁伴噺
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    public int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 鎵归噺鏂板瑙掕壊閮ㄩ棬淇℃伅
     * 
     * @param roleDeptList 瑙掕壊閮ㄩ棬鍒楄〃
     * @return 缁撴灉
     */
    public int batchRoleDept(List<SysRoleDept> roleDeptList);
}
