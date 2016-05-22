<%--suppress XmlDuplicatedId --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 全部排名</title>
    <%@ include file="include/import.jsp" %>
    <link href="styles/table.css" rel="stylesheet"/>
    <script src="scripts/table.js"></script>
    <style>

        table.table th.date {
            width: 170px;
        }

        table.table th.rank {
            width: 60px;
        }

        table.table th.adpt {
            width: 60px;
        }

        table.table td.cupt {
            text-align: left;
            padding-left: 15px;
        }

    </style>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="template" type="text/html">
    <table class="table sorter table-bordered table-striped">
        <caption>
            <span>{{title}} 的全部排名</span>
        </caption>
        <thead>
        <tr>
            <th class="date sorter">时间</th>
            <th class="rank sorter">排名</th>
            <th class="adpt sorter hidden-sx">增加</th>
            <th class="cupt sorter">累积</th>
        </tr>
        </thead>
        <tbody>
        {{each ranks as rank idx}}
        <tr id="row-{{idx+1}}">
            <td class="date" data-number="{{date}}">{{rank.date | fm_rank_date:type}}</td>
            <td class="rank" data-number="{{rank}}">{{rank.rank}}</td>
            <td class="adpt" data-number="{{adpt}}">{{rank.adpt}}</td>
            <td class="cupt" data-number="{{cupt}}">{{rank.cupt}}</td>
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
        $.getJSON("list_rank.do", {id: ${param.id}}, function (data) {
            cache.data = data;
            render_page(data);
        });
    }

    function render_page(data) {
        post_before_render();
        $("#content").html(render("template", data));
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
