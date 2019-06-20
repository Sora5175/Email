package com.sora.web.controller;

import com.sora.utils.*;
import com.sora.web.vo.Email;
import com.sora.web.vo.FileVO;
import com.sora.web.vo.Status;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Sora5175
 * @date 2019/6/15 10:37
 */
@Controller("emailController")
@RequestMapping("/email")
@SessionAttributes({"username","password","emailList"})
public class EmailController {

    @Autowired
    private Status status;

    @Autowired
    private List<Email> emailList;

    @RequestMapping("/index")
    public Object index(){
        return "index";
    }

    @RequestMapping("/detail")
    public Object detail(){
        return "detail";
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Object logout(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        status.setMessage("true");
        return status;
    }

    @RequestMapping("/sendEmail")
    @ResponseBody
    public Object sendEmail(HttpServletRequest request){
        Email email = EmailUtil.changeToEmail(request);
        if(SMTPUtil.send(email)){
            status.setMessage("true");
        }else{
            status.setMessage("发送失败！");
        }
        return status;
    }

    @RequestMapping("/getEmailList")
    @ResponseBody
    public Object getEmailList(HttpServletRequest request, ModelMap sessionMap){
        HttpSession session = request.getSession();
        String username = (String)session.getAttribute("username");
        String password = (String)session.getAttribute("password");
        String item = request.getParameter("item");
        String isChanged = request.getParameter("isChanged");
        if(isChanged.equals("true")){
            emailList = new ArrayList<Email>();
        }
        if(item.equals("INBOX")){
            emailList.addAll(POP3Util.getEmailList(username,password));
        }else {
            emailList.addAll(IMAPUtil.getEmailList(username,password,item));
        }
        List<Email> compressedEmailList =  EmailUtil.compress(emailList);
        sessionMap.addAttribute("emailList",compressedEmailList);
        return compressedEmailList;
    }

    @RequestMapping("/downloadFile")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response){
        int emailIndex = Integer.parseInt(request.getParameter("emailIndex"));
        int fileIndex = Integer.parseInt(request.getParameter("fileIndex"));
        FileVO fileVO = emailList.get(emailIndex).getFileVOList().get(fileIndex);
        String fileName = fileVO.getFileName();
        InputStream is = fileVO.getInputStream();
        try {
            ByteArrayOutputStream baos = FileUtil.streamToByte(is);
            InputStream input_use = new ByteArrayInputStream(baos.toByteArray());
            InputStream input_standby= new ByteArrayInputStream(baos.toByteArray());
            emailList.get(emailIndex).getFileVOList().get(fileIndex).setInputStream(input_standby);
            //设置文件下载头
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            int len = 0;
            while((len = input_use.read()) != -1){
                out.write(len);
                out.flush();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/downloadFileZip")
    public void downloadFileZip(HttpServletRequest request, HttpServletResponse response){
        int emailIndex = Integer.parseInt(request.getParameter("emailIndex"));
        Email email = emailList.get(emailIndex);
        String zipName = email.getSubject();
        try {
            zipName = java.net.URLEncoder.encode(zipName, "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + zipName + ".zip");
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            BufferedOutputStream bos = new BufferedOutputStream(zos);
            for(int i = 0;i<email.getFileVOList().size();i++){
                FileVO fileVO = email.getFileVOList().get(i);
                InputStream is = fileVO.getInputStream();
                ByteArrayOutputStream baos = FileUtil.streamToByte(is);
                InputStream input_use = new ByteArrayInputStream(baos.toByteArray());
                InputStream input_standby= new ByteArrayInputStream(baos.toByteArray());
                emailList.get(emailIndex).getFileVOList().get(i).setInputStream(input_standby);
                String fileName = fileVO.getFileName();
                BufferedInputStream bis = new BufferedInputStream(input_use);
                zos.putNextEntry(new ZipEntry(fileName));
                int len = 0;
                byte[] buf = new byte[10 * 1024];
                while( (len=bis.read(buf, 0, buf.length)) != -1){
                    bos.write(buf, 0, len);
                }
                bis.close();
                bos.flush();
            }
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
