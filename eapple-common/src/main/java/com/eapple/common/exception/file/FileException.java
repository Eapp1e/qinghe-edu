package com.eapple.common.exception.file;

import com.eapple.common.exception.base.BaseException;

/**
 * 鏂囦欢淇℃伅寮傚父绫?
 * 
 * @author Eapp1e
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
