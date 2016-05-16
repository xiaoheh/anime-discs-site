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
    <ul class="nav nav-tabs">
        <li class="active"><a href="#disc-tab" data-toggle="tab">基本数据</a></li>
        <li><a href="#rank-tab" data-toggle="tab">排名数据</a></li>
        <li><a href="#other-tab" data-toggle="tab">其他数据</a></li>
    </ul>
    <div class="tab-content" style="padding-top: 10px">
        <div id="disc-tab" class="tab-pane fade in active">
            <form role="form">
                <input type="hidden" id="id" value="{{id}}">
                <div class="form-group">
                    <label>碟片标题</label>
                    <input type="text" id="title" class="form-control" value="{{title}}">
                </div>
                <div class="form-group">
                    <label>日文原名</label>
                    <div class="textarea-readonly">{{japan}}</div>
                </div>
                <div class="form-group">
                    <label>简短名称</label>
                    <input type="text" id="sname" class="form-control" value="{{sname}}">
                </div>
                <div class="form-group">
                    <label>碟片类型</label>
                    <select class="form-control" id="dvdver" data-value="{{dvdver?'1':'2'}}">
                        <option value="1">DVD</option>
                        <option value="2">Blu-ray</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>分卷类型</label>
                    <select class="form-control" id="boxver" data-value="{{boxver?'1':'2'}}">
                        <option value="1">BOX</option>
                        <option value="2">非BOX</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>限定类型</label>
                    <select class="form-control" id="amzver" data-value="{{amzver?'1':'2'}}">
                        <option value="1">尼限定</option>
                        <option value="2">非尼限定</option>
                    </select>
                </div>
            </form>
        </div>
        <div id="rank-tab" class="tab-pane fade">
            <div class="form-group">
                <label>碟片标题</label>
                <input type="text" class="form-control" value="{{title}}">
            </div>
            <div class="form-group">
                <label>Amazon1</label>
                <input type="text" class="form-control" value="{{rank1 | fm_number}}位 ({{date1 | fm_timeout}} 前)">
            </div>
            <div class="form-group">
                <label>Amazon2</label>
                <input type="text" class="form-control" value="{{rank2 | fm_number}}位 ({{date2 | fm_timeout}} 前)">
            </div>
            <div class="form-group">
                <label>Amazon3</label>
                <input type="text" class="form-control" value="{{rank3 | fm_number}}位 ({{date3 | fm_timeout}} 前)">
            </div>
            <div class="form-group">
                <label>Amazon4</label>
                <input type="text" class="form-control" value="{{rank4 | fm_number}}位 ({{date4 | fm_timeout}} 前)">
            </div>
            <div class="form-group">
                <label>Amazon5</label>
                <input type="text" class="form-control" value="{{rank5 | fm_number}}位 ({{date5 | fm_timeout}} 前)">
            </div>
        </div>
        <div id="other-tab" class="tab-pane fade">
            <div class="form-group">
                <label>碟片标题</label>
                <input type="text" class="form-control" value="{{title}}">
            </div>
            <div class="form-group">
                <label>当前/前回</label>
                <input type="text" class="form-control" value="{{curk | fm_sakura}}位/{{prrk | fm_sakura}}位">
            </div>
            <div class="form-group">
                <label>累计PT</label>
                <input type="text" class="form-control" value="{{cupt | fm_number}} pt">
            </div>
            <div class="form-group">
                <label>Nico预约</label>
                <input type="text" class="form-control" value="{{cubk | fm_number}} 预约">
            </div>
            <div class="form-group">
                <label>发售日期</label>
                <input type="text" id="release" class="form-control" value="{{release | fm_date:'yyyy/MM/dd'}}">
            </div>
            <div class="form-group">
                <label>剩余天数</label>
                <input type="text" class="form-control" value="{{sday}}天">
            </div>
        </div>
    </div>
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
                page.back();
            });
            $(".tab-content").find(":input").each(function () {
                if ($(this).attr("id") == null) {
                    $(this).prop("disabled", true);
                }
            });
        });
    });

    function update_disc() {
        form.info("提交中...");
        $.post("update_disc.do", {
            id: $("#id").val(),
            title: $("#title").val(),
            sname: $("#sname").val(),
            dvdver: $("#dvdver").val() == "1",
            boxver: $("#boxver").val() == "1",
            amzver: $("#amzver").val() == "1",
            release: $("#release").val()
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
