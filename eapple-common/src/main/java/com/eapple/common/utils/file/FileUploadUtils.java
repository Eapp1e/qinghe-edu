package com.eapple.common.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.constant.Constants;
import com.eapple.common.exception.file.FileNameLengthLimitExceededException;
import com.eapple.common.exception.file.FileSizeLimitExceededException;
import com.eapple.common.exception.file.InvalidExtensionException;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.uuid.IdUtils;
import com.eapple.common.utils.uuid.Seq;

/**
 * 鏂囦欢涓婁紶宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class FileUploadUtils
{
    /**
     * 榛樿澶у皬 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024L;

    /**
     * 榛樿鐨勬枃浠跺悕鏈€澶ч暱搴?100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 榛樿涓婁紶鐨勫湴鍧€
     */
    private static String defaultBaseDir = PlatformConfig.getProfile();

    public static void setDefaultBaseDir(String defaultBaseDir)
    {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir()
    {
        return defaultBaseDir;
    }

    /**
     * 浠ラ粯璁ら厤缃繘琛屾枃浠朵笂浼?
     *
     * @param file 涓婁紶鐨勬枃浠?
     * @return 鏂囦欢鍚嶇О
     * @throws Exception
     */
    public static final String upload(MultipartFile file) throws IOException
    {
        try
        {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 鏍规嵁鏂囦欢璺緞涓婁紶
     *
     * @param baseDir 鐩稿搴旂敤鐨勫熀鐩綍
     * @param file 涓婁紶鐨勬枃浠?
     * @return 鏂囦欢鍚嶇О
     * @throws IOException
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException
    {
        try
        {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 鏂囦欢涓婁紶
     *
     * @param baseDir 鐩稿搴旂敤鐨勫熀鐩綍
     * @param file 涓婁紶鐨勬枃浠?
     * @param allowedExtension 涓婁紶鏂囦欢绫诲瀷
     * @return 杩斿洖涓婁紶鎴愬姛鐨勬枃浠跺悕
     * @throws FileSizeLimitExceededException 濡傛灉瓒呭嚭鏈€澶уぇ灏?
     * @throws FileNameLengthLimitExceededException 鏂囦欢鍚嶅お闀?
     * @throws IOException 姣斿璇诲啓鏂囦欢鍑洪敊鏃?
     * @throws InvalidExtensionException 鏂囦欢鏍￠獙寮傚父
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        return upload(baseDir, file, allowedExtension, false);
    }
    
    /**
     * 鏂囦欢涓婁紶
     *
     * @param baseDir 鐩稿搴旂敤鐨勫熀鐩綍
     * @param file 涓婁紶鐨勬枃浠?
     * @param useCustomNaming 绯荤粺鑷畾涔夋枃浠跺悕
     * @param allowedExtension 涓婁紶鏂囦欢绫诲瀷
     * @return 杩斿洖涓婁紶鎴愬姛鐨勬枃浠跺悕
     * @throws FileSizeLimitExceededException 濡傛灉瓒呭嚭鏈€澶уぇ灏?
     * @throws FileNameLengthLimitExceededException 鏂囦欢鍚嶅お闀?
     * @throws IOException 姣斿璇诲啓鏂囦欢鍑洪敊鏃?
     * @throws InvalidExtensionException 鏂囦欢鏍￠獙寮傚父
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension, boolean useCustomNaming)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        int fileNameLength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNameLength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH)
        {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, allowedExtension);

        String fileName = useCustomNaming ? uuidFilename(file) : extractFilename(file);

        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        file.transferTo(Paths.get(absPath));
        return getPathFileName(baseDir, fileName);
    }

    /**
     * 缂栫爜鏂囦欢鍚?鏃ユ湡鏍煎紡鐩綍 + 鍘熸枃浠跺悕 + 搴忓垪鍊?+ 鍚庣紑)
     */
    public static final String extractFilename(MultipartFile file)
    {
        return StringUtils.format("{}/{}_{}.{}", DateUtils.datePath(), FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId(Seq.uploadSeqType), getExtension(file));
    }

    /**
     * 缂栫紪鐮佹枃浠跺悕(鏃ユ湡鏍煎紡鐩綍 + UUID + 鍚庣紑)
     */
    public static final String uuidFilename(MultipartFile file)
    {
        return StringUtils.format("{}/{}.{}", DateUtils.datePath(), IdUtils.fastSimpleUUID(), getExtension(file));
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException
    {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists())
        {
            if (!desc.getParentFile().exists())
            {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    public static final String getPathFileName(String uploadDir, String fileName) throws IOException
    {
        int dirLastIndex = PlatformConfig.getProfile().length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    }

    /**
     * 鏂囦欢澶у皬鏍￠獙
     *
     * @param file 涓婁紶鐨勬枃浠?
     * @return
     * @throws FileSizeLimitExceededException 濡傛灉瓒呭嚭鏈€澶уぇ灏?
     * @throws InvalidExtensionException
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, InvalidExtensionException
    {
        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE)
        {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension))
        {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                        fileName);
            }
            else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension,
                        fileName);
            }
            else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
                        fileName);
            }
            else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension,
                        fileName);
            }
            else
            {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    /**
     * 鍒ゆ柇MIME绫诲瀷鏄惁鏄厑璁哥殑MIME绫诲瀷
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension)
    {
        for (String str : allowedExtension)
        {
            if (str.equalsIgnoreCase(extension))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 鑾峰彇鏂囦欢鍚嶇殑鍚庣紑
     * 
     * @param file 琛ㄥ崟鏂囦欢
     * @return 鍚庣紑鍚?
     */
    public static final String getExtension(MultipartFile file)
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension))
        {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType()));
        }
        return extension;
    }
}

