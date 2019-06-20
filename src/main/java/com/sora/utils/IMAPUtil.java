package com.sora.utils;

import com.sora.web.vo.Email;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import java.util.*;

/**
 * @author Sora5175
 * @date 2019/6/19 11:08
 */
public class IMAPUtil {
    private static String protocol = "imap";

    private static Properties props = new Properties();

    static {
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("IMAP.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Email> getEmailList(String username, String password,String item){
        List<Email> emailList = new ArrayList<Email>();
        String host = "imap."+username.substring((username.lastIndexOf("@")+1),username.length());
        props.setProperty("mail.imap.host",host);
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
        IMAPFolder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(username, password);
            folder = (IMAPFolder)store.getFolder(item);
            Folder folders[ ]=store.getDefaultFolder().list();
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            emailList = MessageUtil.decodeMessage(messages);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
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