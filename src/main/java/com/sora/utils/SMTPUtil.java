package com.sora.utils;

import com.sora.web.vo.Email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author Sora5175
 * @date 2019/6/15 9:42
 */
public class SMTPUtil {
    private static String protocol = "smtp";//使用smtp协议

    private static Properties props = new Properties();

    private static Session session;

    private static Transport transport;

    private static String host;

    static {
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("SMTP.properties"));
            host = props.getProperty("mail.smtp.host");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean login(String username,String password){
        boolean login = false;
        final String user = username;
        final String pwd = password;
        /*
         *Session类定义了一个基本的邮件对话。
         */
        /*
         *Transport类用来发送邮件。
         *传入参数smtp，transport将自动按照smtp协议发送邮件。
         */
        host = "smtp."+username.substring((username.lastIndexOf("@")+1),username.length());
        props.setProperty("mail.smtp.host",host);
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //登录用户名密码
                return new PasswordAuthentication(user,pwd);
            }
        });
        session.setDebug(true);
        try {
            transport = session.getTransport(protocol);
            transport.connect(host, username, password);
            login = true;
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return login;
    }
    public static boolean send(Email email){
        boolean send = false;
        try {
            /*
             *Message对象用来储存实际发送的电子邮件信息
             */
            MimeMessage message = new MimeMessage(session);
            message.setSubject(email.getSubject());
            //消息发送者接收者设置(发件地址，昵称)，收件人看到的昵称是这里设定的
            message.setFrom(new InternetAddress(email.getUsername(),email.getUsername()));
            message.addRecipients(Message.RecipientType.TO,new InternetAddress[]{
                    //消息接收者(收件地址，昵称)
                    //不过这个昵称貌似没有看到效果
                    new InternetAddress(email.getUrl(),null),
            });
            message.saveChanges();

            //创建多部分主体
            MimeMultipart list = new MimeMultipart();
            //文字部分
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(email.getContent(), "text/plain;charset=UTF-8");
            list.addBodyPart(textPart);
            //附件
            for(File file:email.getFileList()){
                MimeBodyPart part = new MimeBodyPart();
                part.attachFile(file);
                list.addBodyPart(part);
            }
            message.setContent(list);
            //发送
            //transport.send(message);
            Transport.send(message);
            transport.close();
            send = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return send;
    }
}
