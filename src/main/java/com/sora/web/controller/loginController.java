package com.sora.web.controller;

import com.sora.utils.SMTPUtil;
import com.sora.web.vo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.util.Map;

/**
 * @author Sora5175
 * @date 2019/6/15 14:34
 */
@Controller("loginController")
@RequestMapping("/login")
@SessionAttributes({"username","password","emailList"})
public class loginController {

    @Autowired
    private Status status;

    @RequestMapping("/login")
    public Object login(){
        return "login";
    }

    @RequestMapping("/loginCommit")
    @ResponseBody
    public Object loginCommit(@RequestBody Map<String,String> map , ModelMap sessionMap){
        String username = map.get("username");
        String password = map.get("password");
        if(SMTPUtil.login(username,password)){
            sessionMap.addAttribute("username",username);
            sessionMap.addAttribute("password",password);
            status.setMessage("登录成功");
            status.setLocation("../email/index");
        }else{
            status.setMessage("登录失败");
            status.setLocation(null);
        }
        return status;
    }
}
