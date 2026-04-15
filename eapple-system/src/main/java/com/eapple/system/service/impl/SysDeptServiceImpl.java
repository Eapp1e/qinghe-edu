package com.eapple.system.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eapple.common.annotation.DataScope;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.TreeSelect;
import com.eapple.common.core.domain.entity.SysDept;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.text.Convert;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.spring.SpringUtils;
import com.eapple.system.mapper.SysDeptMapper;
import com.eapple.system.mapper.SysRoleMapper;
import com.eapple.system.service.ISysDeptService;

/**
 * 閮ㄩ棬绠＄悊 鏈嶅姟瀹炵幇
 * 
 * @author Eapp1e
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService
{
    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 鏌ヨ閮ㄩ棬绠＄悊鏁版嵁
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 閮ㄩ棬淇℃伅闆嗗悎
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 鏌ヨ閮ㄩ棬鏍戠粨鏋勪俊鎭?
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 閮ㄩ棬鏍戜俊鎭泦鍚?
     */
    @Override
    public List<TreeSelect> selectDeptTreeList(SysDept dept)
    {
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        return buildDeptTreeSelect(depts);
    }

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佹爲缁撴瀯
     * 
     * @param depts 閮ㄩ棬鍒楄〃
     * @return 鏍戠粨鏋勫垪琛?
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts)
    {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts)
        {
            // 濡傛灉鏄《绾ц妭鐐? 閬嶅巻璇ョ埗鑺傜偣鐨勬墍鏈夊瓙鑺傜偣
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佷笅鎷夋爲缁撴瀯
     * 
     * @param depts 閮ㄩ棬鍒楄〃
     * @return 涓嬫媺鏍戠粨鏋勫垪琛?
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts)
    {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ閮ㄩ棬鏍戜俊鎭?
     * 
     * @param roleId 瑙掕壊ID
     * @return 閫変腑閮ㄩ棬鍒楄〃
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }

    /**
     * 鏍规嵁閮ㄩ棬ID鏌ヨ淇℃伅
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 閮ㄩ棬淇℃伅
     */
    @Override
    public SysDept selectDeptById(Long deptId)
    {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 鏍规嵁ID鏌ヨ鎵€鏈夊瓙閮ㄩ棬锛堟甯哥姸鎬侊級
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 瀛愰儴闂ㄦ暟
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId)
    {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 鏄惁瀛樺湪瀛愯妭鐐?
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    @Override
    public boolean hasChildByDeptId(Long deptId)
    {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 鏌ヨ閮ㄩ棬鏄惁瀛樺湪鐢ㄦ埛
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉 true 瀛樺湪 false 涓嶅瓨鍦?
     */
    @Override
    public boolean checkDeptExistUser(Long deptId)
    {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 鏍￠獙閮ㄩ棬鍚嶇О鏄惁鍞竴
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkDeptNameUnique(SysDept dept)
    {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙閮ㄩ棬鏄惁鏈夋暟鎹潈闄?
     * 
     * @param deptId 閮ㄩ棬id
     */
    @Override
    public void checkDeptDataScope(Long deptId)
    {
        if (!SecurityUtils.isAdmin() && StringUtils.isNotNull(deptId))
        {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts))
            {
                throw new ServiceException("娌℃湁鏉冮檺璁块棶閮ㄩ棬鏁版嵁锛?);
            }
        }
    }

    /**
     * 鏂板淇濆瓨閮ㄩ棬淇℃伅
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertDept(SysDept dept)
    {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 濡傛灉鐖惰妭鐐逛笉涓烘甯哥姸鎬?鍒欎笉鍏佽鏂板瀛愯妭鐐?
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("閮ㄩ棬鍋滅敤锛屼笉鍏佽鏂板");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }

    /**
     * 淇敼淇濆瓨閮ㄩ棬淇℃伅
     * 
     * @param dept 閮ㄩ棬淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateDept(SysDept dept)
    {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors()))
        {
            // 濡傛灉璇ラ儴闂ㄦ槸鍚敤鐘舵€侊紝鍒欏惎鐢ㄨ閮ㄩ棬鐨勬墍鏈変笂绾ч儴闂?
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 淇敼璇ラ儴闂ㄧ殑鐖剁骇閮ㄩ棬鐘舵€?
     * 
     * @param dept 褰撳墠閮ㄩ棬
     */
    private void updateParentDeptStatusNormal(SysDept dept)
    {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 淇敼瀛愬厓绱犲叧绯?
     * 
     * @param deptId 琚慨鏀圭殑閮ㄩ棬ID
     * @param newAncestors 鏂扮殑鐖禝D闆嗗悎
     * @param oldAncestors 鏃х殑鐖禝D闆嗗悎
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors)
    {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 淇濆瓨閮ㄩ棬鎺掑簭
     *
     * @param deptIds 閮ㄩ棬ID鏁扮粍
     * @param orderNums 鎺掑簭鏁扮粍
     */
    @Override
    @Transactional
    public void updateDeptSort(String[] deptIds, String[] orderNums)
    {
        try
        {
            for (int i = 0; i < deptIds.length; i++)
            {
                SysDept dept = new SysDept();
                dept.setDeptId(Convert.toLong(deptIds[i]));
                dept.setOrderNum(Convert.toInt(orderNums[i]));
                deptMapper.updateDeptSort(dept);
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("淇濆瓨鎺掑簭寮傚父锛岃鑱旂郴绠＄悊鍛?);
        }
    }

    /**
     * 鍒犻櫎閮ㄩ棬绠＄悊淇℃伅
     * 
     * @param deptId 閮ㄩ棬ID
     * @return 缁撴灉
     */
    @Override
    public int deleteDeptById(Long deptId)
    {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 閫掑綊鍒楄〃
     */
    private void recursionFn(List<SysDept> list, SysDept t)
    {
        // 寰楀埌瀛愯妭鐐瑰垪琛?
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 寰楀埌瀛愯妭鐐瑰垪琛?
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t)
    {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext())
        {
            SysDept n = (SysDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 鍒ゆ柇鏄惁鏈夊瓙鑺傜偣
     */
    private boolean hasChild(List<SysDept> list, SysDept t)
    {
        return getChildList(list, t).size() > 0;
    }
}
