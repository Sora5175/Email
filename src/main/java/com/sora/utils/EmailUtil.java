package com.sora.utils;

import com.sora.web.vo.Email;
import com.sora.web.vo.FileVO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author Sora5175
 * @date 2019/6/15 20:10
 */
public class EmailUtil {
    public static Email changeToEmail(HttpServletRequest request){
        Email email = new Email();
        HashMap<String, String> map = new HashMap<>();
        List<File> fileList= new ArrayList<File>();
        //解析和检查请求，请求方法是POST，请求编码是mulitipart/form-data
        String fileName = "";
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(2 * 1024); //设置缓存大小
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                String fieldName = item.getFieldName();
                if (item.isFormField()) {
                    String value = item.getString("utf-8");
                    map.put(fieldName,value);
                } else {
                    File file = new File("C:\\Users\\程鹏\\Desktop\\SMTP附件\\"+item.getName());
                    if(file.exists())
                    {
                        file.delete();
                    }
                    item.write(file);
                    fileList.add(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //email赋值
        email.setUsername(map.get("username"));
        email.setUrl(map.get("url"));
        email.setSubject(map.get("subject"));
        email.setContent(map.get("content"));
        email.setFileList(fileList);
        return email;
    }
    public static List<Email> compress(List<Email> emailList){
        List<Email> compressedEmailList = new ArrayList<Email>();
        for(int i = 0 ;i<emailList.size();i++){
            Email email = emailList.get(i);
            Email compressedEmail = new Email();
            compressedEmail.setUsername(email.getUsername());
            compressedEmail.setFrom(email.getFrom());
            compressedEmail.setUrl(email.getUrl());
            compressedEmail.setSubject(email.getSubject());
            compressedEmail.setContent(email.getContent());
            compressedEmail.setSendTime(email.getSendTime());
            compressedEmail.setFileList(email.getFileList());
            List<FileVO> compressedFileVOList = new ArrayList<FileVO>();
            if(email.getFileVOList() != null){
                for(FileVO fileVO:email.getFileVOList()){
                    FileVO compressedFileVO = new FileVO();
                    compressedFileVO.setFileName(fileVO.getFileName());
                    compressedFileVOList.add(compressedFileVO);
                }
                compressedEmail.setFileVOList(compressedFileVOList);
            }
            compressedEmailList.add(compressedEmail);
        }
        return compressedEmailList;
    }
    public static List<Email> addList(List<Email> oringalList,List<Email> addList) throws IOException {
        for(int i = 0 ; i<addList.size();i++){
            Email email = addList.get(i);
            Email addEmail = new Email();
            addEmail.setUsername(email.getUsername());
            addEmail.setFrom(email.getFrom());
            addEmail.setUrl(email.getUrl());
            addEmail.setSubject(email.getSubject());
            addEmail.setContent(email.getContent());
            addEmail.setSendTime(email.getSendTime());
            addEmail.setFileList(email.getFileList());
            List<FileVO> addFileVOList = new ArrayList<>();
            if(email.getFileVOList() != null){
                for(FileVO fileVO:email.getFileVOList()){
                    FileVO addFileVO = new FileVO();
                    addFileVO.setFileName(fileVO.getFileName());
                    ByteArrayOutputStream baos = FileUtil.streamToByte(fileVO.getInputStream());
                    InputStream input_use = new ByteArrayInputStream(baos.toByteArray());
                    addFileVO.setInputStream(input_use);
                    addFileVOList.add(addFileVO);
                }
                addEmail.setFileVOList(addFileVOList);
            }
            oringalList.add(addEmail);
        }
        return oringalList;
    }
}
