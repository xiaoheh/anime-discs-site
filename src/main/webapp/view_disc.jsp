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
        <div class="form-group">
            <label for="type">碟片类型</label>
            <select class="form-control" id="type" disabled="disabled" data-value="{{type}}">
                <option value="BD">BD</option>
                <option value="DVD">DVD</option>
                <option value="BOX">BOX</option>
            </select>
        </div>
        <div class="form-group">
            <label for="amzver">限定版本</label>
            <select class="form-control" id="amzver" disabled="disabled" data-value="{{amzver?'1':'2'}}">
                <option value="1">尼限定</option>
                <option value="2">非尼限定</option>
            </select>
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
            var $content = $("#content");
            $content.html(template("disc-tmpl", data));
            $content.find("select").each(function () {
                $(this).val($(this).data("value"));
            });
            $content.find(".btn-primary").click(function () {
                update_disc();
            });
            $content.find(".btn-default").click(function () {
                page.go("${param.src}");
            });
        });
    });

    function update_disc() {
        form.info("提交中...");
        $.post("update_disc.do", {
            id: $("#id").val(),
            title: $("#title").val(),
            sname: $("#sname").val()
        }, function (data) {
            if (data == "success") {
                form.success("提交成功");
            } else {
                form.danger("提交失败: " + data.error);
            }
        });
    }
</script>
</body>
</html>
