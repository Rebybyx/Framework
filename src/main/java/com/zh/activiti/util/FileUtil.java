package com.zh.activiti.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mrkin on 2017/2/14.
 */
public class FileUtil {
    private static SimpleDateFormat dirSDF=new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat fileNameSDF=new SimpleDateFormat("HHmmssSSS");
    public static String saveFile(String type, MultipartFile commonsMultipartFile, HttpServletRequest request, int suffix , String fileName) {
        String targetDirectory = "";
        String filename="";
        try {
            String dirDateStr=dirSDF.format(new Date());
            if (type == null || type.equals("")) {
                targetDirectory= request.getServletContext().getRealPath("/uploadFile/images/"+dirDateStr+"/");
            } else {
                targetDirectory= request.getServletContext().getRealPath("/uploadFile/images/" + type + "/"+dirDateStr+"/");
            }
            File filedir = new File(targetDirectory);
            if (!filedir.exists()) {
                System.out.println("------文件夹不存在");
                filedir.mkdirs();
            }

            String fileformat=".jpg";
            if (commonsMultipartFile.getName().contains("jpg")){
                fileformat=".jpg";
            }else if (commonsMultipartFile.getName().contains("png")){
                fileformat=".png";
            }else if(commonsMultipartFile.getName().contains("gif")){
                fileformat=".gif";
            }
            filename=fileName+(suffix==0?"":suffix)+fileformat;
            File file = new File(targetDirectory, filename);
            byte[] bytes= commonsMultipartFile.getBytes();
            OutputStream out = new FileOutputStream(file);
            out.write(bytes, 0, bytes.length);
            out.close();
            String result=targetDirectory.substring(targetDirectory.indexOf("\\uploadFile\\images\\"))+filename;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }

    /** base64 字符串存储
     * @param type
     * @param imgStr
     * @param request
     * @return
     */
    public static String saveFile(String type, String imgStr, HttpServletRequest request) {
        String targetDirectory = "";
        String filename="";
        try {
            String dirDateStr=dirSDF.format(new Date());
            if (type == null || type.equals("")) {
                targetDirectory= request.getServletContext().getRealPath("/uploadFile/images/"+dirDateStr+"/");
            } else {
                targetDirectory= request.getServletContext().getRealPath("/uploadFile/images/" + type + "/"+dirDateStr+"/");
            }
            File filedir = new File(targetDirectory);
            if (!filedir.exists()) {
                System.out.println("------文件夹不存在");
                filedir.mkdirs();
            }
            String fileformat=".jpg";
            if (imgStr.contains("jpg")){
                fileformat=".jpg";
            }else if (imgStr.contains("png")){
                fileformat=".png";
            }else if(imgStr.contains("gif")){
                fileformat=".gif";
            }

            filename=fileNameSDF.format(new Date())+fileformat;
            File file = new File(targetDirectory, filename);
            byte[] bytes= Base64.decodeBase64(imgStr.substring(22).getBytes());
            OutputStream out = new FileOutputStream(file);
            out.write(bytes, 0, bytes.length);
            out.close();
            String result=targetDirectory.substring(targetDirectory.indexOf("\\uploadFile\\images\\"))+filename;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
