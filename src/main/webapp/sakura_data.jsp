<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - Sakura数据</title>
    <%@ include file="include/import.jsp" %>
    <script src="scripts/helper.js"></script>
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
                width: 81px;
            }

            table.table > tbody > tr > td {
                padding-left: 4px;
                padding-right: 4px;
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

            table.table > tbody > tr > td span {
                color: transparent;
            }
        }
    </style>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script>
    $(function () {
        $.getJSON("index.do", function (data) {
            handle_data(data);
            render_page(data);
            device.switch(function () {
                render_page(data);
            });
        });
    });

    function handle_data(data) {

    }

    function render_page(data) {
        if (device.is_small()) {
            var render = cache.get_or_create("tables_tmpl_small", function () {
                return template("tables-tmpl-small");
            });
            post_before_render_small(data);
            $("#content").html(render(data));
            post_after_render_small();
        } else {
            var render = cache.get_or_create("tables_tmpl", function () {
                return template("tables-tmpl");
            });
            post_before_render(data);
            $("#content").html(render(data));
            post_after_render();
        }
    }

    function post_before_render_small(data) {

    }

    function post_after_render_small() {

    }

    function post_before_render(data) {

    }

    function post_after_render() {

    }
</script>
<script id="tables-tmpl-small" type="text/html">
    {{each tables as table}}
    <table id="{{key}}" class="table table-bordered table-striped">
        <caption>
            <span><b>{{table.title}}</b></span>
            <span><span class="hidden-xxs">上次更新 </span>{{table.time | timeout}}</span>
        </caption>
        <thead>
        <tr>
            <th class="index hidden-xxm">ID</th>
            <th style="width: 0px"></th>
            <th class="rank">当前/前回</th>
            <th style="width: 0px"></th>
            <th class="cupt hidden-xxs">累积PT</th>
            <th style="width: 0px"></th>
            <th class="title">碟片标题</th>
        </tr>
        </thead>
        <tbody>
        {{each table.discs as disc index}}
        {{if index < 30 && disc.arnk < 9999 && disc.curk < 9999 && disc.prrk < 9999}}
        <tr>
            <td data-number="{{index+1}}" class="index hidden-xxm">{{index+1}}</td>
            <td><span>)</span></td>
            {{if disc.arnk != disc.curk}}
            <td data-number="{{disc.arnk}}" class="rank danger">{{disc.arnk | sakura}}/{{disc.curk | sakura}}</td>
            {{else}}
            <td data-number="{{disc.curk}}" class="rank">{{disc.curk | sakura}}/{{disc.prrk | sakura}}</td>
            {{/if}}
            <td><span>(</span></td>
            <td data-number="{{disc.cupt}}" class="cupt hidden-xxs">{{disc.cupt | sakura:6}} pt</td>
            <td><span>)</span></td>
            <td class="sname"><a href="view_disc.jsp?id={{disc.id}}" target="_blank">{{disc.sname}}</a></td>
        </tr>
        {{/if}}
        {{/each}}
        </tbody>
    </table>
    {{/each}}
</script>
</body>
</html>
