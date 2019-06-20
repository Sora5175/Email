<%--
  Created by IntelliJ IDEA.
  User: 程鹏
  Date: 2019/6/15
  Time: 14:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>邮件详情</title>
    <link rel="stylesheet" href="/tcpip/css/index.css" type="text/css">
    <script type="text/javascript" src="/tcpip/js/tools/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/tcpip/js/detail.js"></script>
</head>
<body>
<div class="header">
    <div class="info">
        ${sessionScope.username}
    </div>
    <div class="action">
        <button class="logout">退出登录</button>
    </div>
</div>
<div class="body">
    <div class="content">
        <div class="detailEmail">
            <div class="detailEmail_header">
                <div class="detailEmail_info"><label class="detailEmail_subject">${sessionScope.emailList[param.index].subject}</label></div>
                <div class="detailEmail_info gray">发件人：<label class="detailEmail_from">${sessionScope.emailList[param.index].from}</label></div>
                <div class="detailEmail_info gray">发件时间：<label class="detailEmail_sendTime">${sessionScope.emailList[param.index].sendTime}</label></div>
            </div>
            <div class="detailEmail_body">
                <div class="detailEmail_content">${sessionScope.emailList[param.index].content}</div>
            </div>
            <div class="detailEmail_footer">
                <div class="detailEmail_fileList">
                    <c:if test="${!empty sessionScope.emailList[param.index].fileVOList}">
                        <b>附件：</b>
                        <c:forEach var="file" items="${sessionScope.emailList[param.index].fileVOList}" varStatus="status">
                            <div class="file">
                                <a class="filename" href="downloadFile?emailIndex=${param.index}&fileIndex=${status.index}" unset="true">${file.fileName}</a>
                            </div>
                        </c:forEach>
                        <div class="file">
                            <a href="downloadFileZip?emailIndex=${param.index}" class="downloadByPackage">打包下载</a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="footer"></div>
</body>
</html>
