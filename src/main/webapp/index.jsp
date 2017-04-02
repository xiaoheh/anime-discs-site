<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 首页</title>
    <style type="text/css">
        #links h4 {
            margin-bottom: 15px;
        }

        #links ul {
            font-size: 16px;
            margin-bottom: 5px;
        }

        #links ul li {
            margin-bottom: 10px;
        }
    </style>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="links">
    <h4>欢迎访问本站! </h4>
    <h4>推荐访问的页面:</h4>
    <ul>
        <li><a href="sakura_data.jsp">SAKURA数据</a></li>
        <li><a href="sakura_data.jsp#2017-04">SAKURA数据(定位到17年04月)</a></li>
        <li><a href="sakura_data.jsp#2017-01">SAKURA数据(定位到17年01月)</a></li>
        <li><a href="sakura_data.jsp#2016-10">SAKURA数据(定位到16年10月)</a></li>
        <li><a href="list_disc.jsp?filter=table&name=shiqiu">球总的动画列表</a></li>
        <li><a href="list_disc.jsp?filter=table&name=xxlonge">xxlonge的动画列表</a></li>
        <li><a href="list_disc.jsp?filter=table&name=music">正在关注的音乐列表</a></li>
    </ul>
    <h4>推荐访问的列表</h4>
    <ul>
        <%@ include file="include/season-list.jsp" %>
    </ul>
    <h4>推荐访问的专贴:</h4>
    <ul>
        <%@ include file="include/season-tieba.jsp" %>
    </ul>
    <h4>推荐访问的专楼:</h4>
    <ul>
        <%@ include file="include/season-zhuan.jsp" %>
    </ul>
    <h4>贴吧搜索辅助:</h4>
    <ul>
        <li><a href="http://tieba.baidu.com/f/search/res?ie=utf-8&kw=名作之壁&qw=HOHO&only_thread=1" target="_blank">
            HOHO榜
        </a></li>
        <li><a href="http://tieba.baidu.com/f/search/res?ie=utf-8&kw=名作之壁&qw=日榜单&only_thread=1" target="_blank">
            日榜单
        </a></li>
        <li><a href="http://tieba.baidu.com/f?kw=名作之壁&ie=utf-8&tab=good&cid=1" target="_blank">
            周榜单
        </a></li>
    </ul>
</div>
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
    });

    function render_navber_menu() {
        $("#nav-body").prepend($("#navbar-tmpl").html());
    }

</script>
</body>
</html>
