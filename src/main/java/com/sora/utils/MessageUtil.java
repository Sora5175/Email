package com.sora.utils;

import com.sora.web.vo.Email;
import com.sora.web.vo.FileVO;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sora5175
 * @date 2019/6/19 11:10
 */
public class MessageUtil {
    public static List<Email> decodeMessage(Message[] messages) throws Exception{
        List<Email> emailList = new ArrayList<Email>();
        for(Message message:messages){
            MimeMessage msg = (MimeMessage)message;
            try{
                boolean isContainTextAttach = msg.getContentType().indexOf("name") > 0;
            }catch (Exception e){
                MimeMessage cmsg = new MimeMessage(msg);
                msg = cmsg;
            }
            Email email = new Email();
            email.setSubject(getSubject(msg)==null?"":getSubject(msg));
            email.setFrom(getFrom(msg)==null?"":getFrom(msg));
            email.setUrl(getReceiveAddress(msg, null)==null?"":getReceiveAddress(msg, null));
            email.setSendTime(getSentDate(msg, null)==null?"":getSentDate(msg, null));
            StringBuffer contentBuffer = new StringBuffer();
            getMailTextContent(msg, contentBuffer);
            String content =  contentBuffer+"";
            email.setContent(content==null?"": content);
            boolean isContainerAttachment = isContainAttachment(msg);
            if (isContainerAttachment) {
                email.setFileList(new ArrayList<File>());
                saveAttachment(email,msg);
            }
            List<Email> emailListTemp = new ArrayList<Email>();
            emailListTemp.add(email);
            for(Email em:emailList){
                emailListTemp.add(em);
            }
            emailList = emailListTemp;
        }
        return emailList;
    }
    /**
     * 获得邮件主题
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件发件人
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();
        if (froms.length < 1)
            throw new MessagingException("没有发件人!");

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "<" + address.getAddress() + ">";

        return from;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     * @param msg 邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }

        if (addresss == null || addresss.length < 1)
            throw new MessagingException("没有收件人!");
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress)address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length()-1); //删除最后一个逗号

        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null)
            return "";

        if (pattern == null || "".equals(pattern))
            pattern = "yyyy年MM月dd日 E HH:mm ";

        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件中是否包含附件
     * @param part 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part)part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     * @param msg 邮件内容
     * @return 如果邮件已读返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     * @param msg 邮件内容
     * @return 需要回执返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null)
            replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "紧急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     * @param part 邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part)part.getContent(),content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart,content);
            }
        }
    }

    /**
     * 保存附件
     * @param part 邮件中多个组合体中的其中一个组合体
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveAttachment(Email email,Part part) throws Exception {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    saveFile(email,is, decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(email,bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(email,bodyPart.getInputStream(),decodeText(bodyPart.getFileName()));
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment(email,(Part) part.getContent());
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     * @param is 输入流
     * @param fileName 文件名
     */
    private static void saveFile(Email email,InputStream is,String fileName) throws Exception{
        List<FileVO> fileVOList = email.getFileVOList();
        if(fileVOList == null){
            fileVOList = new ArrayList<FileVO>();
        }
        FileVO fileVO = new FileVO();
        fileVO.setFileName(fileName);
        fileVO.setInputStream(is);
        fileVOList.add(fileVO);
        email.setFileVOList(fileVOList);
    }

    /**
     * 文本解码
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
}
