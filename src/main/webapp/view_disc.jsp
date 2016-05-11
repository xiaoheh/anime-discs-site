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
            <input type="text" class="form-control" id="title" value="{{title}}" disabled>
        </div>
        <div class="form-group">
            <label for="japan">日文原名</label>
            <div class="textarea-readonly" id="japan">{{japan}}</div>
        </div>
        <div class="form-group">
            <label for="sname">简短名称</label>
            <input type="text" class="form-control" id="sname" value="{{sname}}" disabled>
        </div>
        <div class="form-group">
            <label for="dvdver">碟片类型</label>
            <select class="form-control" id="dvdver" data-value="{{dvdver?'1':'2'}}" disabled>
                <option value="1">DVD</option>
                <option value="2">Blu-ray</option>
            </select>
        </div>
        <div class="form-group">
            <label for="boxver">分卷类型</label>
            <select class="form-control" id="boxver" data-value="{{boxver?'1':'2'}}" disabled>
                <option value="1">BOX</option>
                <option value="2">非BOX</option>
            </select>
        </div>
        <div class="form-group">
            <label for="amzver">限定类型</label>
            <select class="form-control" id="amzver" data-value="{{amzver?'1':'2'}}" disabled>
                <option value="1">尼限定</option>
                <option value="2">非尼限定</option>
            </select>
        </div>
        <div class="form-group">
            <label for="release">发售日期</label>
            <input type="text" class="form-control" id="release" value="{{release | fm_date:'yyyy/MM/dd'}}" disabled>
        </div>
    </form>
    <div class="button-group">
        <button type="button" class="btn btn-default">返回</button>
        <span id="msg"></span>
    </div>
</script>
<script>
    $(function () {
        $.getJSON("get_disc.do", {id: ${param.id}}, function (data) {
            var $content = $("#content");
            $content.html(template("disc-tmpl", data));
            $content.find("select").each(function () {
                $(this).val($(this).data("value"));
            });
            $content.find(".btn-default").click(function () {
                page.back();
            });
        });
    });
</script>
</body>
</html>
