package com.zh.activiti.util;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Mrkin on 2017/2/13.
 */


public class RSAUtil {
    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    //Android  必须使用这个
    private static final  String AL="RSA/ECB/PKCS1Padding";

    /**
     * 密钥长度，用来初始化
     */
    private static final int KEYSIZE = 1024;
    /**
     * 指定公钥存放字符串
     */
    private static String PublicKey = "PublicKey";
    /**
     * 指定私钥存放字符串
     */
    private static String PrivateKey = "PrivateKey";

    private static String init=generateKeyPair();
    /**
     * 生成密钥对
     *
     * @throws Exception
     */
    public static String generateKeyPair()  {

//        /** RSA算法要求有一个可信任的随机数源 */
//        SecureRandom secureRandom = new SecureRandom();
        try {


        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
//        keyPairGenerator.initialize(KEYSIZE, secureRandom);
        keyPairGenerator.initialize(KEYSIZE);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();

        PublicKey = new String(new BASE64Encoder().encodeBuffer(publicKey.getEncoded()));
        PublicKey=PublicKey.replaceAll("\r\n","");
        PrivateKey = new String(new BASE64Encoder().encodeBuffer(privateKey.getEncoded()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密方法
     *
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec pKCS8EncodedKeySpec =new X509EncodedKeySpec (new BASE64Decoder().decodeBuffer(PublicKey));
        Key publicKey = keyFactory.generatePublic(pKCS8EncodedKeySpec);
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(AL);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b1);
    }

    /**
     * 加密方法
     *
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source,String PublicKey) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec pKCS8EncodedKeySpec =new X509EncodedKeySpec (new BASE64Decoder().decodeBuffer(PublicKey));
        Key publicKey = keyFactory.generatePublic(pKCS8EncodedKeySpec);
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(AL);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b1).replaceAll("\r\n","");
    }


    /**
     * 解密算法
     *
     * @param cryptograph 密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph) throws Exception {
        cryptograph=cryptograph.replaceAll("\n","");
        cryptograph=cryptograph.replaceAll("\t","");
        cryptograph=cryptograph.replaceAll("\r","");
        cryptograph=cryptograph.replaceAll("\r\n","");
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec =new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(PrivateKey));
        Key privateKey = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(AL);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = decoder.decodeBuffer(cryptograph);
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

   /* public static String encrypt(String source,String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec pKCS8EncodedKeySpec =new X509EncodedKeySpec (new BASE64Decoder().decodeBuffer(publicKey));
        Key key = keyFactory.generatePublic(pKCS8EncodedKeySpec);
        *//** 得到Cipher对象来实现对源数据的RSA加密 *//*
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = source.getBytes();
        *//** 执行加密操作 *//*
        byte[] b1 = cipher.doFinal(b);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b1);
    }*/

    public static String getPublicKey(){

        return PublicKey;
    }
    public static String getPrivateKey(){

        return PrivateKey;
    }
}

