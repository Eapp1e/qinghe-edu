package com.eapple.common.utils.file;

import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * 鏂囦欢绫诲瀷宸ュ叿绫?
 *
 * @author Eapp1e
 */
public class FileTypeUtils
{
    /**
     * 鑾峰彇鏂囦欢绫诲瀷
     * <p>
     * 渚嬪: ruoyi.txt, 杩斿洖: txt
     * 
     * @param file 鏂囦欢鍚?
     * @return 鍚庣紑锛堜笉鍚?.")
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
     * 鑾峰彇鏂囦欢绫诲瀷
     * <p>
     * 渚嬪: ruoyi.txt, 杩斿洖: txt
     *
     * @param fileName 鏂囦欢鍚?
     * @return 鍚庣紑锛堜笉鍚?.")
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
     * 鑾峰彇鏂囦欢绫诲瀷
     * 
     * @param photoByte 鏂囦欢瀛楄妭鐮?
     * @return 鍚庣紑锛堜笉鍚?.")
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