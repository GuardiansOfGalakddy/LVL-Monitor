package com.guardiansofgalakddy.lvlmonitor.junhwa;

import java.security.AlgorithmParameters;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Aes {
    public static byte[] aesKey = { 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f };
    public static byte[] aesIv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

    public static byte[] hexStringToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    public static String byteArrayToHexString(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    public static byte[] encrypt(byte[] bytes) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv);

        Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(bytes);
        return encrypted;
    }

    public static byte[] decrypt(byte[] bytes) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv);

        Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] original = cipher.doFinal(bytes);
        return original;
    }
}