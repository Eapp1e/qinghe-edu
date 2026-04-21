package com.eapple.common.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

/**
 * 鎺掗櫎JSON鏁忔劅灞炴€?
 * 
 * @author Eapp1e
 */
public class PropertyPreExcludeFilter extends SimplePropertyPreFilter
{
    public PropertyPreExcludeFilter()
    {
    }

    public PropertyPreExcludeFilter addExcludes(String... filters)
    {
        for (int i = 0; i < filters.length; i++)
        {
            this.getExcludes().add(filters[i]);
        }
        return this;
    }
}
