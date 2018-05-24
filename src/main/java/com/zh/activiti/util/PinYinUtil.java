package com.zh.activiti.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author ZH_GJ
 * @date 2018/4/13
 */
public class PinYinUtil {

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    //拼音字符串数组
    private static String[] pinyin = null;


    public static String getCharPinYinArray(Character pinYinStr) {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            //执行转换
            pinyin = PinyinHelper.toHanyuPinyinStringArray(pinYinStr, format);

        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String result = "";
        if (pinyin != null) {
            for (int i = 0; i < pinyin.length; i++) {
                result += pinyin[i] + ",";
            }
        }
        return result;
    }

    public static String getCharPinYin(Character pinYinStr)

    {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            //执行转换
            pinyin = PinyinHelper.toHanyuPinyinStringArray(pinYinStr, format);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String result = "";
        if (pinyin != null && pinyin.length > 0 && pinyin[0].length() > 0) {
            result += pinyin[0].charAt(0);
        } else {
            result = pinYinStr.toString();
        }
        return result;
    }

    public static String getStringPinYin(Character pinYinStr)

    {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            //执行转换
            pinyin = PinyinHelper.toHanyuPinyinStringArray(pinYinStr, format);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String result = "";
        if (pinyin != null && pinyin.length > 0) {
            result += pinyin[0];
        } else {
            result = pinYinStr.toString();
        }
        return result;
    }

    public static String getStringPinYin(String pinYinStr)

    {
        StringBuffer sb = new StringBuffer();
        String tempStr = null;
        //循环字符串
        for (int i = 0; i < pinYinStr.length(); i++) {

            tempStr = getStringPinYin(pinYinStr.charAt(i));
            if (tempStr == null) {
                //非汉字直接拼接
                sb.append(pinYinStr.charAt(i));
            } else {
                sb.append(tempStr);
            }
        }

        return sb.toString();

    }

}