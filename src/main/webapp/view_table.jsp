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
<script id="template" type="text/html">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#info-tab" data-toggle="tab">基本数据</a></li>
        <li><a href="list_disc.jsp?filter=table&name={{name}}">全部碟片</a></li>
    </ul>
    <div class="tab-content" style="padding-top: 10px">
        <div id="info-tab" class="tab-pane fade in active">
            <div class="form-group">
                <label>榜单名称</label>
                <input type="text" class="form-control" value="{{name}}">
            </div>
            <div class="form-group">
                <label>榜单标题</label>
                <input type="text" class="form-control" value="{{title}}">
            </div>
            <div class="form-group">
                <label>榜单类型</label>
                <select class="form-control" data-value="{{sakura?'1':'2'}}">
                    <option value="1">Sakura</option>
                    <option value="2">非Sakura</option>
                </select>
            </div>
        </div>
    </div>
    <div class="button-group">
        <button onclick="page.back()" class="btn btn-default">返回</button>
        <span id="msg"></span>
    </div>
</script>
<script>
    $(function () {
        $.getJSON("view_table.do", {id: ${param.id}}, function (data) {
            $("#content").html(template("template", data));
            $("select").each(function () {
                $(this).val($(this).data("value"));
            });
            $(".tab-content").find(":input").each(function () {
                if ($(this).attr("id") == null) {
                    $(this).prop("disabled", true);
                }
            });
        });
    });
</script>
</body>
</html>
