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
 * 閺傚洣娆㈡径鍕倞瀹搞儱鍙跨猾?
 * 
 * @author Eapp1e
 */
public class FileUtils
{
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 鏉堟挸鍤幐鍥х暰閺傚洣娆㈤惃鍒te閺佹壆绮?
     * 
     * @param filePath 閺傚洣娆㈢捄顖氱窞
     * @param os 鏉堟挸鍤ù?
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
     * 閸愭瑦鏆熼幑顔煎煂閺傚洣娆㈡稉?
     *
     * @param data 閺佺増宓?
     * @return 閻╊喗鐖ｉ弬鍥︽
     * @throws IOException IO瀵倸鐖?
     */
    public static String writeImportBytes(byte[] data) throws IOException
    {
        return writeBytes(data, PlatformConfig.getImportPath());
    }

    /**
     * 閸愭瑦鏆熼幑顔煎煂閺傚洣娆㈡稉?
     *
     * @param data 閺佺増宓?
     * @param uploadDir 閻╊喗鐖ｉ弬鍥︽
     * @return 閻╊喗鐖ｉ弬鍥︽
     * @throws IOException IO瀵倸鐖?
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
     * 缁夊娅庣捄顖氱窞娑擃厾娈戠拠閿嬬湴閸撳秶绱戦悧鍥唽
     * 
     * @param filePath 閺傚洣娆㈢捄顖氱窞
     * @return 缁夊娅庨崥搴ｆ畱閺傚洣娆㈢捄顖氱窞
     */
    public static String stripPrefix(String filePath)
    {
        return StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX);
    }

    /**
     * 閸掔娀娅庨弬鍥︽
     * 
     * @param filePath 閺傚洣娆?
     * @return
     */
    public static boolean deleteFile(String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        // 鐠侯垰绶炴稉鐑樻瀮娴犳湹绗栨稉宥勮礋缁屽搫鍨潻娑滎攽閸掔娀娅?
        if (file.isFile() && file.exists())
        {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 閺傚洣娆㈤崥宥囆炴宀冪槈
     * 
     * @param filename 閺傚洣娆㈤崥宥囆?
     * @return true 濮濓絽鐖?false 闂堢偞纭?
     */
    public static boolean isValidFilename(String filename)
    {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 濡偓閺屻儲鏋冩禒鑸垫Ц閸氾箑褰叉稉瀣祰
     * 
     * @param resource 闂団偓鐟曚椒绗呮潪鐣屾畱閺傚洣娆?
     * @return true 濮濓絽鐖?false 闂堢偞纭?
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 缁備焦顒涢惄顔肩秿娑撳﹨鐑︾痪褍鍩?
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 濡偓閺屻儱鍘戠拋闀愮瑓鏉炵晫娈戦弬鍥︽鐟欏嫬鍨?
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource)))
        {
            return true;
        }

        // 娑撳秴婀崗浣筋啅娑撳娴囬惃鍕瀮娴犳儼顫夐崚?
        return false;
    }

    /**
     * 娑撳娴囬弬鍥︽閸氬秹鍣搁弬鎵椽閻?
     * 
     * @param request 鐠囬攱鐪扮€电钖?
     * @param fileName 閺傚洣娆㈤崥?
     * @return 缂傛牜鐖滈崥搴ｆ畱閺傚洣娆㈤崥?
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
    {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE"))
        {
            // IE濞村繗顫嶉崳?
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox"))
        {
            // 閻忣偆瀚勫ù蹇氼潔閸?
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome"))
        {
            // google濞村繗顫嶉崳?
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else
        {
            // 閸忚泛鐣犲ù蹇氼潔閸?
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 娑撳娴囬弬鍥︽閸氬秹鍣搁弬鎵椽閻?
     *
     * @param response 閸濆秴绨茬€电钖?
     * @param realFileName 閻喎鐤勯弬鍥︽閸?
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
     * 閻ф儳鍨庨崣椋庣椽閻礁浼愰崗閿嬫煙濞?
     *
     * @param s 闂団偓鐟曚胶娅ㄩ崚鍡楀娇缂傛牜鐖滈惃鍕摟缁楋缚瑕?
     * @return 閻ф儳鍨庨崣椋庣椽閻礁鎮楅惃鍕摟缁楋缚瑕?
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 閼惧嘲褰囬崶鎯у剼閸氬海绱?
     * 
     * @param photoByte 閸ユ儳鍎氶弫鐗堝祦
     * @return 閸氬海绱戦崥?
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
     * 閼惧嘲褰囬弬鍥︽閸氬秶袨 /profile/upload/2022/04/16/qinghe.png -- qinghe.png
     * 
     * @param fileName 鐠侯垰绶為崥宥囆?
     * @return 濞屸剝婀侀弬鍥︽鐠侯垰绶為惃鍕倳缁?
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
     * 閼惧嘲褰囨稉宥呯敨閸氬海绱戦弬鍥︽閸氬秶袨 /profile/upload/2022/04/16/qinghe.png -- qinghe
     * 
     * @param fileName 鐠侯垰绶為崥宥囆?
     * @return 濞屸剝婀侀弬鍥︽鐠侯垰绶為崪灞芥倵缂傗偓閻ㄥ嫬鎮曠粔?
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


