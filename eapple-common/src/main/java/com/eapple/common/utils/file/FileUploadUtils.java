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
 * 文件上传工具类。
 *
 * @author Eapp1e
 */
public class FileUploadUtils
{
    /** 默认最大上传大小：50MB */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024L;

    /** 默认最大文件名长度 */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /** 默认上传根目录 */
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
     * 使用默认配置上传文件。
     *
     * @param file 上传文件
     * @return 文件访问路径
     * @throws IOException 上传失败时抛出
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
     * 上传文件到指定目录。
     *
     * @param baseDir 上传根目录
     * @param file 上传文件
     * @return 文件访问路径
     * @throws IOException 上传失败时抛出
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
     * 上传文件并校验后缀。
     *
     * @param baseDir 上传根目录
     * @param file 上传文件
     * @param allowedExtension 允许的后缀集合
     * @return 文件访问路径
     * @throws FileSizeLimitExceededException 文件过大
     * @throws IOException 上传异常
     * @throws FileNameLengthLimitExceededException 文件名过长
     * @throws InvalidExtensionException 后缀不合法
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        return upload(baseDir, file, allowedExtension, false);
    }

    /**
     * 上传文件，可选择使用自定义命名。
     *
     * @param baseDir 上传根目录
     * @param file 上传文件
     * @param allowedExtension 允许的后缀集合
     * @param useCustomNaming 是否使用自定义命名
     * @return 文件访问路径
     * @throws FileSizeLimitExceededException 文件过大
     * @throws IOException 上传异常
     * @throws FileNameLengthLimitExceededException 文件名过长
     * @throws InvalidExtensionException 后缀不合法
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
     * 生成上传文件名：日期目录 + 原文件名 + 序列号 + 后缀。
     */
    public static final String extractFilename(MultipartFile file)
    {
        return StringUtils.format("{}/{}_{}.{}", DateUtils.datePath(), FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId(Seq.uploadSeqType), getExtension(file));
    }

    /**
     * 生成 UUID 文件名：日期目录 + UUID + 后缀。
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
     * 校验上传文件是否符合要求。
     *
     * @param file 上传文件
     * @param allowedExtension 允许的后缀集合
     * @throws FileSizeLimitExceededException 文件过大
     * @throws InvalidExtensionException 后缀不合法
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
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension, fileName);
            }
            else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension, fileName);
            }
            else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension, fileName);
            }
            else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION)
            {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension, fileName);
            }
            else
            {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    /**
     * 判断后缀是否在允许列表中。
     *
     * @param extension 当前后缀
     * @param allowedExtension 允许后缀集合
     * @return 是否允许
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
     * 获取上传文件后缀。
     *
     * @param file 上传文件
     * @return 文件后缀
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
