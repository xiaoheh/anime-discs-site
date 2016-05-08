<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - Sakura数据</title>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script id="tables-tmpl-small" type="text/html">
    {{each lists as list idx}}
    <table id="{{list.key}}" class="table table-bordered table-striped">
        <caption>
            <span><b>{{list.title}}</b></span>
            <span><span class="hidden-xxs">上次更新 </span>{{list.time | fm_timeout}}</span>
        </caption>
        <thead>
        <tr>
            <th class="index hidden-xxm">ID</th>
            <th class="index hidden-xxm zero-width"></th>
            <th class="rank">排名</th>
            <th class="cupt hidden-xxs zero-width"></th>
            <th class="cupt hidden-xxs">累积</th>
            <th class="cupt hidden-xxs zero-width"></th>
            <th class="title">碟片标题</th>
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
<style>
    @media (max-width: 767px) {

        table.table > thead > tr > th.index {
            width: 32px;
            text-align: center;
            padding-left: 2px;
            padding-right: 2px;
        }

        table.table > thead > tr > th.rank {
            width: 88px;
        }

        table.table > thead > tr > th.cupt {
            width: 66px;
        }

        table.table > tbody > tr > td.index {
            text-align: center;
        }

        table.table > tbody > tr > td.rank {
            text-align: center;
        }

        table.table > tbody > tr > td.cupt {
            text-align: center;
        }
    }
</style>
<script>
    $(function () {
        ajax_update_page();
        device.switch(function () {
            if (cache.data) {
                render_page(cache.data);
            } else {
                ajax_update_page();
            }
        });
    });

    function ajax_update_page() {
        $.getJSON("index.do", function (data) {
            cache.data = data;
            handle_data(data);
            render_page(data);
        });
    }

    function refresh() {
        ajax_update_page();
    }

    function handle_data(data) {
        $(data).each(function () {
            navbar.add_postion(this.key, this.title);
        });
    }

    function render_page(data) {
        offset.save();
        if (device.is_small()) {
            $("#content").html(render("tables-tmpl-small", {lists: data}));
        } else {
            $("#content").html(render("tables-tmpl", {lists: data}));
        }
        if (cache.is_first("restore")) {
            offset.restore();
        } else {
            offset.load();
        }
    }
</script>
</body>
</html>
