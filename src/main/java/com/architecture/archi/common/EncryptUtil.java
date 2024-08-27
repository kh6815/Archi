package com.architecture.archi.common;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 단방향 암호화 유틸
 */
public class EncryptUtil {

    public static String encryptString(String str) throws CustomException {
        String encryptStr;
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            encryptStr = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            encryptStr = null;
            throw new CustomException(ExceptionCode.UNKNOWN_ERROR);
        }
        return encryptStr;
    }

}
