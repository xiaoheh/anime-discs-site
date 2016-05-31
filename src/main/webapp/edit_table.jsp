<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 碟片信息</title>
    <%@ include file="include/import.jsp" %>
    <link href="styles/table-20160527.css" rel="stylesheet"/>
    <script src="scripts/table-20160531.js"></script>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="template" type="text/html">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#info-tab" data-toggle="tab">基本数据</a></li>
        <li><a href="#edit-tab" data-toggle="tab">管理碟片</a></li>
        <li><a href="list_disc.jsp?filter=table&name={{name}}">全部碟片</a></li>
    </ul>
    <div class="tab-content" style="padding-top: 10px">
        <div id="info-tab" class="tab-pane fade in active">
            <input type="hidden" id="id" value="{{id}}">
            <div class="form-group">
                <label>碟片列表名称</label>
                <input type="text" id="name" class="form-control" value="{{name}}">
            </div>
            <div class="form-group">
                <label>碟片列表标题</label>
                <input type="text" id="title" class="form-control" value="{{title}}">
            </div>
            <div class="form-group">
                <label>碟片列表类型</label>
                <select class="form-control" id="sakura" data-value="{{sakura?'1':'2'}}">
                    <option value="1">Sakura</option>
                    <option value="2">非Sakura</option>
                </select>
            </div>
            <div class="button-group">
                <button onclick="update()" class="btn btn-primary">提交</button>
                <button onclick="page.back()" class="btn btn-default">返回</button>
                <span id="msg"></span>
            </div>
        </div>
        <div id="edit-tab" class="tab-pane fade in active">

        </div>
    </div>
</script>
<script id="edit-tmpl" type="text/html">
    <table id="manager" class="table sorter table-bordered table-striped">
        <caption>
            <div class="form-group">
                <label>
                    <button onclick="add_disc()">添加碟片</button>
                </label>
                <input type="text" id="disc" class="form-control" placeholder="请输入ASIN或碟片ID">
            </div>
        </caption>
        <thead>
        <tr>
            <th style="width: 60px; text-align: center; padding-left: 8px">操作</th>
            <th class="sorter" style="width: 120px">ASIN</th>
            <th class="sorter">碟片标题</th>
        </tr>
        </thead>
        <tbody>
        {{each discs as disc}}
        <tr id="{{disc.id}}">
            <td><a href="javascript:remove('{{disc.id}}')">移除</a></td>
            <td><a href="http://www.amazon.co.jp/dp/{{disc.asin}}">{{disc.asin}}</a></td>
            <td style="text-align: left"><a href="edit_disc.jsp?id={{disc.id}}">{{disc.title}}</a></td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<script id="navbar-tmpl" type="text/html">
    <li class="dropdown hidden-xs">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
            功能<b class="caret"></b>
        </a>
        <ul id="control" class="dropdown-menu">
            <li><a href="javascript:navbar.refresh()">刷新</a></li>
        </ul>
    </li>
</script>
<script id="row-tmpl" type="text/html">
    <tr id="{{id}}">
        <td><a href="javascript:remove('{{id}}')">移除</a></td>
        <td><a href="http://www.amazon.co.jp/dp/{{asin}}">{{asin}}</a></td>
        <td style="text-align: left"><a href="edit_disc.jsp?id={{id}}">{{title}}</a></td>
    </tr>
</script>
<script>

    $(function () {
        render_navber_menu();
        $.getJSON("view_table.do", {id: ${param.id}}, function (data) {
            $("#content").html(template("template", data));
            $("select").each(function () {
                $(this).val($(this).data("value"));
            });
            $(":input").not("button").each(function () {
                if ($(this).attr("id") == null) {
                    $(this).prop("disabled", true);
                }
            });
            $.getJSON("list_disc.do", {filter: "table", name: data.name}, function (data2) {
                $("#edit-tab").html(template("edit-tmpl", {discs: data2.discs}));
                $("#edit-tab").removeClass("in active");
                tablesorter("#manager");
            });
        });
    });

    function render_navber_menu() {
        $("#nav-body").prepend($("#navbar-tmpl").html());
    }

    function update() {
        form.info("提交中...");
        $.post("edit_table.do", {
            id: $("#id").val(),
            name: $("#name").val(),
            title: $("#title").val(),
            sakura: $("#sakura").val() == "1"
        }, function (data) {
            if (data == "success") {
                form.success("提交成功");
            } else {
                form.danger("提交失败: " + data.error);
            }
        });
    }

    function add_disc() {
        var text = $("#disc").val().trim();
        if (/^\d+$/.test(text)) {
            $.getJSON("add_disc_to_table_with_id.do", {
                id: ${param.id},
                discId: text
            }, function (data) {
                if (data["success"]) {
                    $("tbody").prepend(template("row-tmpl", data["disc"]));
                } else {
                    alert(data.error);
                }
            });
        } else if (/^[A-Z0-9]{10}$/.test(text)) {
            $.getJSON("add_disc_to_table_with_asin.do", {
                id: ${param.id},
                asin: text
            }, function (data) {
                if (data["success"]) {
                    $("tbody").prepend(template("row-tmpl", data["disc"]));
                } else {
                    alert(data.error);
                }
            });
        } else {
            alert("输入不合法");
        }
    }

    function remove(discId) {
        $.getJSON("remove_disc_from_table.do", {
            id: ${param.id},
            discId: discId
        }, function (data) {
            if (data) {
                $("#" + discId).remove();
            } else {
                alert("移除失败: " + data.error);
            }
        });
    }

</script>
</body>
</html>
