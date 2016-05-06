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
            <input type="text" class="form-control" id="japan" disabled="disabled" value="{{japan}}">
        </div>
        <div class="form-group">
            <label for="sname">简短名称</label>
            <input type="text" class="form-control" id="sname" value="{{sname}}">
        </div>
    </form>
    <button type="button" class="btn btn-primary">提交</button>
    <button type="button" class="btn btn-default">返回</button>
    <span id="msg"></span>
</script>
<script>
    $(function () {
        $.getJSON("get_disc.do", {id: ${param.id}}, function (data) {
            $("#content").html(template("disc-tmpl", data));
            $("#content .btn-primary").click(function () {
                $("#msg").html("提交中...");
                $.post("update_disc.do", {
                    id: $("#id").val(),
                    title: $("#title").val(),
                    sname: $("#sname").val()
                }, function (data) {
                    if (data == "success") {
                        $("#msg").html("提交成功");
                    } else {
                        $("#msg").html("提交失败: " + data.error);
                    }
                });
            });
            $("#content .btn-default").click(function () {
                page.go("${param.src}");
            });
        });
    });
</script>
</body>
</html>
