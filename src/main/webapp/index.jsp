<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 首页</title>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<h4>Welcome to this site!</h4>
<h4>推荐页面: <a href="sakura_data.jsp">Sakura数据</a></h4>
<h4>推荐页面: <a href="list_disc.jsp?filter=table&name=top_100">日亚实时TOP100</a></h4>
<h4>推荐页面: <a href="list_disc.jsp?filter=table&name=2016-04">2016年04月新番</a></h4>
<h4>推荐页面: <a href="list_disc.jsp?filter=table&name=2016-01">2016年01月新番</a></h4>
<h4>推荐页面: <a href="list_disc.jsp?filter=table&name=mycd">手动添加的CD</a></h4>
<h4>推荐页面: <a href="list_disc.jsp?filter=table&name=mydvd">手动添加的DVD</a></h4>
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
