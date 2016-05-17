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
        #content th.index {
            width: 20px;
        }

        #content th.date {
            width: 80px;
        }

        #content th.rank {
            width: 40px;
        }
    </style>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="tables-tmpl" type="text/html">
    <table class="table sorter table-bordered table-striped">
        <caption>
            <span>{{title}}的全部排名</span>
        </caption>
        <thead>
        <tr>
            <th class="index">ID</th>
            <th class="index zero-width"></th>
            <th class="date sorter">时间</th>
            <th class="rank sorter">排名</th>
        </tr>
        </thead>
        <tbody>
        {{each ranks as rank idx}}
        <tr id="row-{{idx+1}}">
            <td class="index" data-number="{{idx+1}}">{{idx+1}}</td>
            <td class="index zero-width">)</td>
            <td class="date" data-number="{{date}}">{{rank.date | fm_date:'yyyy/MM/dd hh:mm:ss'}}</td>
            <td class="rank" data-number="{{rank}}">{{rank.rank}}</td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<script>
    $(function () {
        handle_refresh_action();
        handle_pageshow_action();
    });

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
        $("#content").html(render("tables-tmpl", data));
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
