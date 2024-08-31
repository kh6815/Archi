package com.architecture.archi.config.aws;

/**
 * 파일 유틸
 */
public class StorageUtils {
    /**
     * 파일 저장 경로를 생성한다.
     *
     * @param fileName
     * @param uploadFolder
     * @return
     * @throws Exception
     */
    public static String createStoragePath(String fileName, String uploadFolder) {

        String uploadFileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        return String.format("%s/%s", uploadFolder, "file_" + System.currentTimeMillis() + "." + uploadFileExtension);
    }
}
