$(function () {
    var logout = $(".logout");
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
});