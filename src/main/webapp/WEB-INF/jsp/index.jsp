<%--
  Created by IntelliJ IDEA.
  User: 程鹏
  Date: 2019/6/15
  Time: 14:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>邮箱首页</title>
    <link rel="stylesheet" href="/tcpip/css/index.css" type="text/css">
    <script type="text/javascript" src="/tcpip/js/tools/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/tcpip/js/index.js"></script>
</head>
<body>
    <div class="header">
        <div class="info">
            ${sessionScope.username}
        </div>
        <div class="action">
            <button class="sendEmail">发送邮件</button>
            <button class="logout">退出登录</button>
        </div>
    </div>
    <div class="body">
        <div class="leftContent">
            <input id="item" type="hidden" value="INBOX">
            <div id="INBOX" class="changeItem">收件箱</div>
            <div id="Sent Messages" class="changeItem">发件箱</div>
            <div id="Drafts" class="changeItem">草稿箱</div>
            <div id="Deleted Messages" class="changeItem">已删除</div>
            <div id="Junk" class="changeItem">垃圾箱</div>
        </div>
        <div class="content rightContent" id="scrollDiv">
        </div>
    </div>
    <div class="lid">
        <div class="sendingEmail">
            <div class="sendingEmail_header">发送邮件</div>
            <div class="sendingEmail_body">
                <div class="input"><div class="tips">收件邮箱：</div><input type="text" id="url"></div>
                <div class="input"><div class="tips">主题：</div><input type="text" id="subject"></div>
                <div class="input"><div class="tips">正文：</div><textarea id="content" rows="7"></textarea></div>
                <div class="input"><div class="tips">附件：</div><input id="fileList" type="file" multiple="multiple" value="添加附件"/></div>
            </div>
            <div class="sendingEmail_footer">
                <div class="left"><button class="confirmSend">确认发送</button></div>
                <div class="right"><button class="cancelSend">取消发送</button></div>
            </div>
        </div>
    </div>
</body>
</html>
