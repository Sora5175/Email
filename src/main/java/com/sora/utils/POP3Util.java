package com.sora.utils;

import com.sora.web.vo.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;

/**
 * @author Sora5175
 * @date 2019/6/15 10:22
 */
public class POP3Util {
    private static String protocol = "pop3";//使用pop3协议

    private static Properties props = new Properties();

    static {
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("POP3.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Email> getEmailList(String username,String password){
        List<Email> emailList = new ArrayList<Email>();
        String host = "pop."+username.substring((username.lastIndexOf("@")+1),username.length());
        props.setProperty("mail.pop3.host",host);
        final String user = username;
        final String pwd = password;
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //登录用户名密码
                return new PasswordAuthentication(user,pwd);
            }
        });
        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(username, password);
            Folder folders[ ]=store.getDefaultFolder().list();
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            emailList = MessageUtil.decodeMessage(messages);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (folder != null) {
                    folder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return emailList;
    }
}