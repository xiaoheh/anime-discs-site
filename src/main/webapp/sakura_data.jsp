<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - Sakura数据</title>
    <%@ include file="include/import.jsp" %>
    <link href="styles/table.css" rel="stylesheet"/>
    <script src="scripts/table.js"></script>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="tables-tmpl-small" type="text/html">
    {{each lists as list idx}}
    <table id="{{list.key}}" class="table sorter table-bordered table-striped">
        <caption>
            <span><b>{{list.title}}</b></span>
            <span><span class="hidden-xxs">上次更新 </span>{{list.time | fm_timeout}}</span>
        </caption>
        <thead>
        <tr>
            <th class="index hidden-xxm">ID</th>
            <th class="index hidden-xxm zero-width"></th>
            <th class="rank sorter">排名</th>
            <th class="cupt hidden-xxs zero-width"></th>
            <th class="cupt hidden-xxs sorter">累积</th>
            <th class="cupt hidden-xxs zero-width"></th>
            <th class="sname sorter">碟片标题</th>
        </tr>
        </thead>
        <tbody>
        {{each list.discs as disc idx2}}
        {{if disc.arnk < 9999 && disc.curk < 9999 && disc.prrk < 9999}}
        <tr id="row-{{idx+1}}-{{idx2+1}}">
            <td class="index hidden-xxm" data-number="{{idx2+1}}">{{idx2+1}}</td>
            <td class="index hidden-xxm zero-width">)</td>
            {{if disc.arnk != disc.curk}}
            <td class="rank danger" data-number="{{disc.arnk}}">{{disc.arnk | fm_sakura}}/{{disc.curk | fm_sakura}}</td>
            {{else}}
            <td class="rank" data-number="{{disc.curk}}">{{disc.curk | fm_sakura}}/{{disc.prrk | fm_sakura}}</td>
            {{/if}}
            <td class="cupt hidden-xxs zero-width">(</td>
            <td class="cupt hidden-xxs" data-number="{{disc.cupt}}">{{disc.cupt | fm_sakura:6}}</td>
            <td class="cupt hidden-xxs zero-width"> pt)</td>
            <td class="sname"><a href="view_disc.jsp?id={{disc.id}}">{{disc.sname}} {{disc | fm_verstr}}</a></td>
        </tr>
        {{/if}}
        {{/each}}
        </tbody>
    </table>
    {{/each}}
</script>
<script>
    $(function () {
        handle_switch_action();
        handle_refresh_action();
        handle_pageshow_action();
    });

    function handle_switch_action() {
        device.switch(function () {
            if (cache.data) {
                render_page(cache.data);
            } else {
                ajax_update_page();
            }
        });
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
        $.getJSON("index.do", function (data) {
            cache.data = data;
            handle_data(data);
            render_page(data);
        });
    }

    function handle_data(data) {
        $(data).each(function () {
            navbar.add_postion(this.key, this.title);
        });
    }

    function render_page(data) {
        post_before_render();
        if (device.is_small()) {
            $("#content").html(render("tables-tmpl-small", {lists: data}));
        } else {
            $("#content").html(render("tables-tmpl", {lists: data}));
        }
        post_after_render();
    }

    function post_before_render() {
        table.save_status();
        offset.save();
    }

    function post_after_render() {
        table.sorter("table.table.sorter");
        table.load_status();
        offset.load();
        if (cache.is_first("restore")) {
            offset.restore();
        } else {
            offset.load();
        }
    }
</script>
</body>
</html>
