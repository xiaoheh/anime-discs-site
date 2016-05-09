<%--suppress XmlDuplicatedId --%>
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
            <td class="rank danger" data-number="{{disc.arnk}}">{{disc.arnk | fm_star}}/{{disc.curk | fm_star}}</td>
            {{else}}
            <td class="rank" data-number="{{disc.curk}}">{{disc.curk | fm_star}}/{{disc.prrk | fm_star}}</td>
            {{/if}}
            <td class="cupt hidden-xxs zero-width">(</td>
            <td class="cupt hidden-xxs" data-number="{{disc.cupt}}">{{disc.cupt | fm_star:6}}</td>
            <td class="cupt hidden-xxs zero-width"> pt)</td>
            <td class="sname"><a href="view_disc.jsp?id={{disc.id}}">{{disc.sname}} {{disc | fm_verstr}}</a></td>
        </tr>
        {{/if}}
        {{/each}}
        </tbody>
    </table>
    {{/each}}
</script>
<script id="tables-tmpl" type="text/html">
    {{each lists as list idx}}
    <table id="{{list.key}}" class="table sorter table-striped table-bordered">
        <caption>
            <span><b>{{list.title}}</b></span>
            {{if list.time}}
            <span>更新时间: {{list.time | fm_date:"yyyy-MM-dd hh:mm:ss"}}</span>
            <span>(距离现在 {{list.time | fm_timeout}})</span>
            {{/if}}
        </caption>
        <thead>
        <tr>
            <th class="index sorter">序号</th>
            <th class="index zero-width"></th>
            <th class="arnk sorter">Amazon</th>
            <th class="atot sorter">更新时间</th>
            <th class="srnk sorter">当前/前回</th>
            <th class="cupt zero-width"></th>
            <th class="cupt sorter">累积PT</th>
            <th class="cupt zero-width"></th>
            <th class="capt sorter">预测PT</th>
            <th class="tapt sorter">新增PT</th>
            <th class="sday sorter">剩余天数</th>
            <th class="cubk zero-width"></th>
            <th class="cubk sorter">Nico预约</th>
            <th class="cubk zero-width"></th>
            <th class="stot sorter">更新时间</th>
            <th class="shelves sorter">上架日期</th>
            <th class="release sorter">发售日期</th>
            <th class="japan sorter">日文原名</th>
            <th class="title sorter">碟片标题</th>
        </tr>
        </thead>
        <tbody>
        {{each list.discs as disc idx2}}
        <tr>
            <td class="index" data-number="{{idx2+1}}">{{idx2+1}}</td>
            <td class="index zero-width">)</td>
            <td class="arnk" data-number="{{disc.arnk}}">
                <a href="http://www.amazon.co.jp/dp/{{disc.asin}}" target="_blank">{{disc.arnk | fm_number}}位</a>
            </td>
            <td class="atot" data-number="{{disc.amdt}}">{{disc.amdt | fm_timeout}}</td>
            <td class="srnk" data-number="{{disc.curk}}"><a href="http://rankstker.net/show.cgi?n={{disc.asin}}"
                   target="_blank">{{disc.curk | fm_sakura:6}}位/{{disc.prrk | fm_sakura:6}}位</a>
            </td>
            <td class="cupt zero-width">(</td>
            <td class="cupt" data-number="{{disc.cupt}}">{{disc.cupt | fm_sakura:6}} pt</td>
            <td class="cupt zero-width">)</td>
            <td class="capt" data-number="{{disc.capt}}">{{disc.capt | fm_number}} pt</td>
            <td class="tapt" data-number="{{disc.tapt}}">{{disc.tapt | fm_number}} pt</td>
            <td class="sday" data-number="{{disc.sday}}">{{disc.sday}}天</td>
            <td class="cubk zero-width">[</td>
            <td class="cubk" data-number="{{disc.cubk}}">{{disc.cubk | fm_sakura:4}} 预约</td>
            <td class="cubk zero-width">]</td>
            <td class="stot" data-number="{{disc.skdt}}">{{disc.skdt | fm_timeout}}</td>
            <td class="shelves" data-number="{{disc.shelves}}">{{disc.shelves | fm_date:"yyyy/MM/dd"}}</td>
            <td class="release" data-number="{{disc.release}}">{{disc.release | fm_date:"yyyy/MM/dd"}}</td>
            <td class="japan">
                <a href="view_disc.jsp?id={{disc.id}}">{{disc.japan}}</a>
            </td>
            <td class="title">
                <a href="view_disc.jsp?id={{disc.id}}">{{disc.title}}</a>
            </td>
        </tr>
        {{/each}}
        </tbody>
    </table>
    {{/each}}
</script>
<div id="profile-modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">自定义表格格式</h4>
            </div>
            <div id="profile-body" class="modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
<script id="profile-tmpl" type="text/html">
    <label>快速选择</label>
    <div id="div-quick">
        {{each quick as q}}
        <label>
            <button class="button" data-checked="{{q.checked.join(',')}}">{{q.title}}</button>
        </label>
        {{/each}}
    </div>
    <label>选择想显示的列</label>
    <div id="div-hidden">
        {{each hidden as h}}
        <label>
            <input type="checkbox" data-class="{{h.clazz}}" checked="checked">
            {{h.title}}
        </label>
        {{/each}}
    </div>
</script>
<script id="navbar-tmpl" type="text/html">
    <li class="dropdown hidden-xs">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
            功能<b class="caret"></b>
        </a>
        <ul id="control" class="dropdown-menu">
            <li><a href="javascript:navbar.refresh()">刷新</a></li>
            <li><a href="javascript:table.show_pro()">自定义表格格式</a></li>
        </ul>
    </li>
</script>
<script>
    $(function () {
        render_navber_menu();
        handle_switch_action();
        handle_refresh_action();
        handle_profile_action();
        handle_pageshow_action();
    });

    function render_navber_menu() {
        $("#nav-body").prepend($("#navbar-tmpl").html());
    }

    function handle_switch_action() {
        device.switch(function () {
            if (cache.data) {
                render_page(cache.data);
            } else {
                ajax_update_page();
            }
        });
    }

    function handle_profile_action() {
        table.show_pro(function () {
            $("#profile-modal").modal();
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
        $.getJSON("sakura.do", function (data) {
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
        setTimeout(function () {
            if (cache.is_first("restore")) {
                offset.restore();
            } else {
                offset.load();
            }
        }, 20);
        if (!device.is_small()) {
            render_profile([
                {title: "默认中文模式", checked: ["index", "arnk", "srnk", "cupt", "title"]},
                {title: "默认日文模式", checked: ["index", "arnk", "srnk", "cupt", "japan"]},
                {title: "Sakura标准模式", checked: ["index", "srnk", "cupt", "cubk", "release", "title"]},
                {title: "Sakura预测模式", checked: ["index", "srnk", "cupt", "tapt", "sday", "capt", "release", "title"]},
                {title: "Sakura精简模式", checked: ["srnk", "title"]},
                {title: "Amazon精简模式", checked: ["arnk", "title"]}
            ]);
            $("table.table>tbody>tr").each(function () {
                var $td1 = $(this).find("td.arnk");
                var $td2 = $(this).find("td.srnk");
                if ($td1.data("number") != $td2.data("number")) {
                    $td1.addClass("danger");
                }
            });
        }
    }
</script>
</body>
</html>
