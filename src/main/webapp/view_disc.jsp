<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 碟片信息</title>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="disc-tmpl" type="text/html">
    <form role="form">
        <input type="hidden" id="id" value="{{id}}">
        <div class="form-group">
            <label for="title">碟片标题</label>
            <input type="text" class="form-control" id="title" value="{{title}}">
        </div>
        <div class="form-group">
            <label for="japan">日文原名</label>
            <div class="textarea" id="japan">{{japan}}</div>
        </div>
        <div class="form-group">
            <label for="sname">简短名称</label>
            <input type="text" class="form-control" id="sname" value="{{sname}}">
        </div>
    </form>
    <div class="button-group">
        <button type="button" class="btn btn-primary">提交</button>
        <button type="button" class="btn btn-default">返回</button>
        <span id="msg"></span>
    </div>
</script>
<script>
    $(function () {
        $.getJSON("get_disc.do", {id: ${param.id}}, function (data) {
            $("#content").html(template("disc-tmpl", data));
            $("#content .btn-primary").click(function () {
                update_disc();
            });
            $("#content .btn-default").click(function () {
                page.go("${param.src}");
            });
        });
    });

    function update_disc() {
        $("#msg").html("<span class='text-info'>提交中...</span>");
        $.post("update_disc.do", {
            id: $("#id").val(),
            title: $("#title").val(),
            sname: $("#sname").val()
        }, function (data) {
            if (data == "success") {
                $("#msg").html("<span class='text-success'>提交成功</span>");
            } else {
                $("#msg").html("<span class='text-danger'>提交失败: " + data.error + "</span>");
            }
        });
    }
</script>
</body>
</html>
