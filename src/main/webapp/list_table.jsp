<%--suppress XmlDuplicatedId --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 全部碟片列表</title>
    <%@ include file="include/import.jsp" %>
    <link href="styles/table-20160527.css" rel="stylesheet"/>
    <script src="scripts/table-20171114.js"></script>
    <style>

        table.table th.index {
            width: 32px;
            text-align: center;
            padding-left: 2px;
            padding-right: 2px;
        }

        table.table th.name {
            width: 80px;
        }

        table.table th.link {
            width: 70px;
        }

    </style>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="template" type="text/html">
    <table class="table sorter table-bordered table-striped">
        <caption>
            <span><b>所有碟片列表</b></span>
        </caption>
        <thead>
        <tr>
            <th class="index">ID</th>
            <th class="index zero-width"></th>
            <th class="name sorter">名称</th>
            <th class="link sorter">操作</th>
            <th class="title sorter">标题</th>
        </tr>
        </thead>
        <tbody>
        {{each tables as table idx}}
        <tr id="row-{{idx+1}}">
            <td class="index" data-number="{{idx+1}}">{{idx+1}}</td>
            <td class="index zero-width">)</td>
            <td class="name">{{table.name}}</td>
            <td><a href="${cookie.admin.value?"edit":"view"}_table.jsp?id={{table.id}}">
                ${cookie.admin.value?"编辑":"查看"}
            </a></td>
            <td class="title"><a href="list_disc.jsp?filter=table&name={{table.name}}">{{table.title}}</a></td>
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
<script>
    $(function () {
        render_navber_menu();
        handle_refresh_action();
        handle_pageshow_action();
    });

    function render_navber_menu() {
        $("#nav-body").prepend($("#navbar-tmpl").html());
    }

    function handle_refresh_action() {
        navbar.refresh(ajax_update_page);
    }

    function handle_pageshow_action() {
        $("body").get(0).onpageshow = function () {
            setTimeout(function () {
                navbar.refresh();
            }, 10);
        };
    }

    function ajax_update_page() {
        $.getJSON("list_table.do", function (data) {
            cache.data = data;
            render_page(data);
        });
    }

    function render_page(data) {
        post_before_render();
        $("#content").html(render("template", {tables: data}));
        post_after_render();
    }

    function post_before_render() {
        table.save_status();
        offset.save();
    }

    function post_after_render() {
        table.sorter("table.table.sorter");
        table.load_status();
        setTimeout(function () {
            if (cache.is_first("restore")) {
                offset.restore();
            } else {
                offset.load();
            }
        }, 20);
    }
</script>
</body>
</html>
