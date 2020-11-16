package pt.isec.deis.lei.pd.trabprat.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AES {

    private static final String KEY = "0+pSXbap9rey60Yo6U9UQg==";
    private static final String IV = "CCLyxWcfBQ5clfMMEdTfaQ==";

    public static String Encrypt(String toEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(Encrypt(toEncrypt.getBytes()));
    }

    // Yes you shouldn't use this function...
    public static byte[] Encrypt(byte[] toEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return _Encrypt(toEncrypt, KEY, IV);
    }

    private static byte[] _Encrypt(byte[] toEncrypt, String keyStr, String IVStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] decKey = Base64.getDecoder().decode(keyStr);
        byte[] IV = Base64.getDecoder().decode(IVStr);
        SecretKey key = new SecretKeySpec(decKey, 0, decKey.length, "AES");
        return Encrypt(toEncrypt, key, IV);
    }

    public static byte[] Encrypt(byte[] toEncrypt, SecretKey key, byte[] IV) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(toEncrypt);
    }

    public static String Decrypt(String toDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return new String(Decrypt(Base64.getDecoder().decode(toDecrypt)));
    }

    // Yes you shouldn't use this function...
    public static byte[] Decrypt(byte[] toDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return _Decrypt(toDecrypt, KEY, IV);
    }

    private static byte[] _Decrypt(byte[] toDecrypt, String keyStr, String IVStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] decKey = Base64.getDecoder().decode(keyStr);
        byte[] IV = Base64.getDecoder().decode(IVStr);
        SecretKey key = new SecretKeySpec(decKey, 0, decKey.length, "AES");
        return Decrypt(toDecrypt, key, IV);
    }

    public static byte[] Decrypt(byte[] toDecrypt, SecretKey key, byte[] IV) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(toDecrypt);
    }

    private AES() {
    }
}
