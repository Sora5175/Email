var emailList = [];
var getEmailAjax = null;
$(function() {
    getEmailList(true);
    var username = $(".info").text().replace(/\ +/g,"").replace(/[ ]/g,"").replace(/[\r\n]/g,"");
    var url = $("#url");
    var subject = $("#subject");
    var content = $("#content");

    var sendEmail = $(".sendEmail");
    var logout = $(".logout");
    var confirmSend = $(".confirmSend");
    var cancelSend = $(".cancelSend");
    var changeItem = $(".changeItem");
    var item = $("#item");

    $("#scrollDiv").scroll(function(){
        var scrollTop = $(this).scrollTop();
        var ks_area = $(this).innerHeight();
        var nScrollHight = $(this)[0].scrollHeight;
        if(scrollTop + ks_area >= nScrollHight){
            getEmailList("false")
        }
    });

    changeItem.on("click",function () {
        if(item.val() != $(this).attr('id')){
            item.val($(this).attr('id'));
            item.change();
        }
    });

    item.on("change",function () {
        getEmailList(true);
    });

    sendEmail.on("click",function () {
        url.val("");
        subject.val("");
        content.val();
       $(".lid").show();
       $(".sendingEmail").fadeIn();
       url.focus();
    });
    cancelSend.on("click",function () {
        $(".sendingEmail").fadeOut();
        $(".lid").hide();
        url.val("");
        subject.val("");
        content.val();
    });
    logout.on("click",function () {
        $.ajax({
            "url":"logout",
            "method":"get",
            "data":"",
            "dataType":"json",
            "success":function(data){
                if(data.message == "true"){
                    location.href = "/tcpip/";
                    return true;
                }else{
                    alert(data.message);
                }
            },
            "fail":function(data){
                alert("服务器繁忙，请稍后重试");
                return false;
            },
        });
    });
    confirmSend.on("click",function () {
        var fileList = document.getElementById("fileList").files;
        var data = new FormData();
        data.append("username",username);
        data.append("url",url.val());
        data.append("subject",subject.val());
        data.append("content",content.val());
        for(var i = 0;i<fileList.length;i++){
            data.append("file",fileList[i]);
        }
        $.ajax({
            url:"sendEmail",
            method:"post",
            data:data,
            cache: false,
            processData: false,
            contentType: false,
            success:function(data){
                if(data.message == "true"){
                    cancelSend.click();
                    alert("邮件发送成功！")
                    return true;
                }else{
                    alert(data.message);
                }
            },
            fail:function(data){
                alert("服务器繁忙，请稍后重试");
                return false;
            },
        });
    });
    $(".body .content").on("click",".email",function () {
        location.href = "detail?index="+$(this).index();
    });
    function getEmailList(isChnaged) {
        var itemValue = $("#item").val();
        if(getEmailAjax != null){
            getEmailAjax.abort();
            getEmailAjax = null;
        }
        if(isChnaged == 'true'){
            loadingEmailList();
        }
        var ajax = $.ajax({
            url:"getEmailList?item="+itemValue+"&isChanged="+isChnaged,
            method:"get",
            data:"",
            processData:false,
            success:function(data){
                emailList = data;
                setEmailList();
            },
            fail:function(data){
                alert("服务器繁忙，请稍后重试");
                return false;
            },
        });
        getEmailAjax = ajax;
    }
    function loadingEmailList() {
        $(".body .content").html("");
        $(".body .content").append('<div class="emailDisabled">\n' +
            '                <div class="email_title"><div class="email_from">加载中..</div><div class="email_time"></div></div>\n' +
            '                <div class="email_subject">加载中，请稍后...</div>\n' +
            '            </div>');
    }
    function setEmailList() {
        $(".body .content").html("");
        for(var i = 0 ;i<emailList.length;i++){
            $(".body .content").append('<div class="email emailActive">\n' +
                '                <div class="email_title"><div class="email_from">'+emailList[i].from+'</div><div class="email_time">'+emailList[i].sendTime+'</div></div>\n' +
                '                <div class="email_subject">'+emailList[i].subject+'</div>\n' +
                '            </div>');
        }
    }
});