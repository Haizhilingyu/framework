package site.xiweihai.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
public class ToolUtils {

    private static final List<String> configExtensions = Arrays.asList(".properties", ".xml", ".yaml", ".yml", ".json", ".html", ".js");

    public static boolean isConfigFile(String fileName) {
        return configExtensions.stream().anyMatch(fileName::endsWith);
    }

    // 文件大小格式化方法
    public static String formatFileSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static String getParentPath(String root, File file) {
        if (file != null && file.getPath().length() > root.length() && file.getPath().startsWith(root)) {
            return file.getParent();
        }
        return null;
    }

    public static List<Map<String, Object>> getFileList(File targetFileOrDir) {
        File[] files = targetFileOrDir.listFiles();
        List<Map<String, Object>> fileList = new ArrayList<>();
        for (File file : files) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("name", file.getName());
            fileInfo.put("size", ToolUtils.formatFileSize(file.length()));
            fileInfo.put("lastModified", new Date(file.lastModified()));
            fileInfo.put("md5", getMD5(file));
            fileInfo.put("isDirectory", file.isDirectory());
            fileList.add(fileInfo);
        }
        return fileList;
    }
    public static String getMD5(File file) {
        String md5 = "";
        if (file.isDirectory()) {
            return md5;
        }
        try (FileInputStream fis = new java.io.FileInputStream(file)){
            md5 = DigestUtils.md5Hex(fis);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return md5;
    }
}
