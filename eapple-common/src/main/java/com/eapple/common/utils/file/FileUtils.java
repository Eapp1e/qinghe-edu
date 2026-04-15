package com.eapple.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.constant.Constants;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.uuid.IdUtils;

/**
 * 鏂囦欢澶勭悊宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class FileUtils
{
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 杈撳嚭鎸囧畾鏂囦欢鐨刡yte鏁扮粍
     * 
     * @param filePath 鏂囦欢璺緞
     * @param os 杈撳嚭娴?
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException
    {
        FileInputStream fis = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0)
            {
                os.write(b, 0, length);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            IOUtils.close(os);
            IOUtils.close(fis);
        }
    }

    /**
     * 鍐欐暟鎹埌鏂囦欢涓?
     *
     * @param data 鏁版嵁
     * @return 鐩爣鏂囦欢
     * @throws IOException IO寮傚父
     */
    public static String writeImportBytes(byte[] data) throws IOException
    {
        return writeBytes(data, PlatformConfig.getImportPath());
    }

    /**
     * 鍐欐暟鎹埌鏂囦欢涓?
     *
     * @param data 鏁版嵁
     * @param uploadDir 鐩爣鏂囦欢
     * @return 鐩爣鏂囦欢
     * @throws IOException IO寮傚父
     */
    public static String writeBytes(byte[] data, String uploadDir) throws IOException
    {
        FileOutputStream fos = null;
        String pathName = "";
        try
        {
            String extension = getFileExtendName(data);
            pathName = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension;
            File file = FileUploadUtils.getAbsoluteFile(uploadDir, pathName);
            fos = new FileOutputStream(file);
            fos.write(data);
        }
        finally
        {
            IOUtils.close(fos);
        }
        return FileUploadUtils.getPathFileName(uploadDir, pathName);
    }

    /**
     * 绉婚櫎璺緞涓殑璇锋眰鍓嶇紑鐗囨
     * 
     * @param filePath 鏂囦欢璺緞
     * @return 绉婚櫎鍚庣殑鏂囦欢璺緞
     */
    public static String stripPrefix(String filePath)
    {
        return StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX);
    }

    /**
     * 鍒犻櫎鏂囦欢
     * 
     * @param filePath 鏂囦欢
     * @return
     */
    public static boolean deleteFile(String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        // 璺緞涓烘枃浠朵笖涓嶄负绌哄垯杩涜鍒犻櫎
        if (file.isFile() && file.exists())
        {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 鏂囦欢鍚嶇О楠岃瘉
     * 
     * @param filename 鏂囦欢鍚嶇О
     * @return true 姝ｅ父 false 闈炴硶
     */
    public static boolean isValidFilename(String filename)
    {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 妫€鏌ユ枃浠舵槸鍚﹀彲涓嬭浇
     * 
     * @param resource 闇€瑕佷笅杞界殑鏂囦欢
     * @return true 姝ｅ父 false 闈炴硶
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 绂佹鐩綍涓婅烦绾у埆
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 妫€鏌ュ厑璁镐笅杞界殑鏂囦欢瑙勫垯
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource)))
        {
            return true;
        }

        // 涓嶅湪鍏佽涓嬭浇鐨勬枃浠惰鍒?
        return false;
    }

    /**
     * 涓嬭浇鏂囦欢鍚嶉噸鏂扮紪鐮?
     * 
     * @param request 璇锋眰瀵硅薄
     * @param fileName 鏂囦欢鍚?
     * @return 缂栫爜鍚庣殑鏂囦欢鍚?
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
    {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE"))
        {
            // IE娴忚鍣?
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox"))
        {
            // 鐏嫄娴忚鍣?
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome"))
        {
            // google娴忚鍣?
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else
        {
            // 鍏跺畠娴忚鍣?
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 涓嬭浇鏂囦欢鍚嶉噸鏂扮紪鐮?
     *
     * @param response 鍝嶅簲瀵硅薄
     * @param realFileName 鐪熷疄鏂囦欢鍚?
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
    {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 鐧惧垎鍙风紪鐮佸伐鍏锋柟娉?
     *
     * @param s 闇€瑕佺櫨鍒嗗彿缂栫爜鐨勫瓧绗︿覆
     * @return 鐧惧垎鍙风紪鐮佸悗鐨勫瓧绗︿覆
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 鑾峰彇鍥惧儚鍚庣紑
     * 
     * @param photoByte 鍥惧儚鏁版嵁
     * @return 鍚庣紑鍚?
     */
    public static String getFileExtendName(byte[] photoByte)
    {
        String strFileExtendName = "jpg";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97))
        {
            strFileExtendName = "gif";
        }
        else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70))
        {
            strFileExtendName = "jpg";
        }
        else if ((photoByte[0] == 66) && (photoByte[1] == 77))
        {
            strFileExtendName = "bmp";
        }
        else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71))
        {
            strFileExtendName = "png";
        }
        return strFileExtendName;
    }

    /**
     * 鑾峰彇鏂囦欢鍚嶇О /profile/upload/2022/04/16/ruoyi.png -- ruoyi.png
     * 
     * @param fileName 璺緞鍚嶇О
     * @return 娌℃湁鏂囦欢璺緞鐨勫悕绉?
     */
    public static String getName(String fileName)
    {
        if (fileName == null)
        {
            return null;
        }
        int lastUnixPos = fileName.lastIndexOf('/');
        int lastWindowsPos = fileName.lastIndexOf('\\');
        int index = Math.max(lastUnixPos, lastWindowsPos);
        return fileName.substring(index + 1);
    }

    /**
     * 鑾峰彇涓嶅甫鍚庣紑鏂囦欢鍚嶇О /profile/upload/2022/04/16/ruoyi.png -- ruoyi
     * 
     * @param fileName 璺緞鍚嶇О
     * @return 娌℃湁鏂囦欢璺緞鍜屽悗缂€鐨勫悕绉?
     */
    public static String getNameNotSuffix(String fileName)
    {
        if (fileName == null)
        {
            return null;
        }
        String baseName = FilenameUtils.getBaseName(fileName);
        return baseName;
    }
}

