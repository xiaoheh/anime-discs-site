<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 首页</title>
    <style type="text/css">
        h4 {
            margin-bottom: 15px;
        }

        ul {
            font-size: 16px;
            margin-bottom: 5px;
        }

        ul li {
            margin-bottom: 10px;
        }
    </style>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<h4>欢迎访问本站! </h4>
<h4>推荐访问的页面:</h4>
<ul>
    <li><a href="sakura_data.jsp">SAKURA数据</a></li>
    <li><a href="sakura_data.jsp#2016-07">SAKURA数据(定位到7月)</a></li>
    <li><a href="sakura_data.jsp#2016-04">SAKURA数据(定位到4月)</a></li>
    <li><a href="list_disc.jsp?filter=table&name=mydvd">球总的动画列表</a></li>
    <li><a href="list_disc.jsp?filter=table&name=myfav">站长的动画列表</a></li>
    <li><a href="list_disc.jsp?filter=table&name=mycd">Schalke04的音乐列表</a></li>
</ul>
<h4>推荐访问的专贴:</h4>
<ul>
    <li><a href="http://tieba.baidu.com/f?kw=名作之壁&ie=utf-8" target="_blank">名作之壁吧</a></li>
    <li><a href="http://tieba.baidu.com/f?kw=壁吧专楼&ie=utf-8" target="_blank">壁吧专楼吧</a></li>
    <li><a href="http://tieba.baidu.com/p/4448722919?pn=9999" target="_blank">PT排名走势(4月)</a></li>
</ul>
<h4>推荐访问的专楼:</h4>
<ul>
    <li><a href="http://tieba.baidu.com/p/4452273267?pn=9999" target="_blank">甲铁城专楼</a></li>
    <li><a href="http://tieba.baidu.com/p/4452227415?pn=9999" target="_blank">马三角专楼</a></li>
    <li><a href="http://tieba.baidu.com/p/4438693662?pn=9999" target="_blank">从零开始专楼</a></li>
    <li><a href="http://tieba.baidu.com/p/4438519074?pn=9999" target="_blank">青春波纹专楼</a></li>
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
    <li><a href="http://tieba.baidu.com/f/search/res?ie=utf-8&kw=壁吧专楼&qw=专楼%2016夏&only_thread=1" target="_blank">
        7月专楼
    </a></li>
</ul>

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
