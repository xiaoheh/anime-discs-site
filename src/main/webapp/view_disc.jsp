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
        <div class="form-group">
            <label for="title">碟片标题</label>
            <input type="text" class="form-control" id="title" value="{{title}}">
        </div>
        <div class="form-group">
            <label for="japan">日文原名</label>
            <input type="text" class="form-control" id="japan" value="{{japan}}">
        </div>
        <div class="form-group">
            <label for="sname">简短名称</label>
            <input type="text" class="form-control" id="sname" value="{{sname}}">
        </div>
    </form>
</script>
<script>
    $(function () {
        $.getJSON("get_disc.do", {id: ${param.id}}, function (data) {
            $("#content").html(template("disc-tmpl", data));
        });
    });
</script>
</body>
</html>
