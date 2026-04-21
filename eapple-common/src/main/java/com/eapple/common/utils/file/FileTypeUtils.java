package com.eapple.common.utils.file;

import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * 閺傚洣娆㈢猾璇茬€峰銉ュ徔缁?
 *
 * @author Eapp1e
 */
public class FileTypeUtils
{
    /**
     * 閼惧嘲褰囬弬鍥︽缁鐎?
     * <p>
     * 娓氬顩? qinghe.txt, 鏉╂柨娲? txt
     * 
     * @param file 閺傚洣娆㈤崥?
     * @return 閸氬海绱戦敍鍫滅瑝閸?.")
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
     * 閼惧嘲褰囬弬鍥︽缁鐎?
     * <p>
     * 娓氬顩? qinghe.txt, 鏉╂柨娲? txt
     *
     * @param fileName 閺傚洣娆㈤崥?
     * @return 閸氬海绱戦敍鍫滅瑝閸?.")
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
     * 閼惧嘲褰囬弬鍥︽缁鐎?
     * 
     * @param photoByte 閺傚洣娆㈢€涙濡惍?
     * @return 閸氬海绱戦敍鍫滅瑝閸?.")
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
