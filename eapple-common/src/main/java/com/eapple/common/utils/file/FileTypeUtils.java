package com.eapple.common.utils.file;

import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件类型工具类。
 *
 * @author Eapp1e
 */
public class FileTypeUtils
{
    /**
     * 获取文件扩展名。
     * <p>
     * 例如：qinghe.txt -> txt
     *
     * @param file 文件对象
     * @return 文件扩展名
     */
    public static String getFileType(File file)
    {
        if (null == file)
        {
            return StringUtils.EMPTY;
        }
        return getFileType(file.getName());
    }

    /**
     * 获取文件扩展名。
     * <p>
     * 例如：qinghe.txt -> txt
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getFileType(String fileName)
    {
        int separatorIndex = fileName.lastIndexOf(".");
        if (separatorIndex < 0)
        {
            return "";
        }
        return fileName.substring(separatorIndex + 1).toLowerCase();
    }

    /**
     * 根据文件字节头判断图片扩展名。
     *
     * @param photoByte 图片字节数组
     * @return 图片扩展名
     */
    public static String getFileExtendName(byte[] photoByte)
    {
        String strFileExtendName = "JPG";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97))
        {
            strFileExtendName = "GIF";
        }
        else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70))
        {
            strFileExtendName = "JPG";
        }
        else if ((photoByte[0] == 66) && (photoByte[1] == 77))
        {
            strFileExtendName = "BMP";
        }
        else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71))
        {
            strFileExtendName = "PNG";
        }
        return strFileExtendName;
    }
}
