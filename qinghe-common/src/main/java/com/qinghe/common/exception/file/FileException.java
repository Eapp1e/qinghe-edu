package com.qinghe.common.exception.file;

import com.qinghe.common.exception.base.BaseException;

/**
 * File information exception.
 *
 * @author EAPPLE
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
