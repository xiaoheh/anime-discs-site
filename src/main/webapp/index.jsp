<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - 首页</title>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<h4 style="margin-bottom: 15px">欢迎访问本站! </h4>
<h4 style="margin-bottom: 15px">推荐访问的页面:</h4>
<ul style="font-size: 16px; margin-bottom: 5px">
    <li style="margin-bottom: 10px"><a href="sakura_data.jsp">SAKURA数据</a></li>
    <li style="margin-bottom: 10px"><a href="sakura_data.jsp#2016-04">SAKURA数据(定位到4月)</a></li>
    <li style="margin-bottom: 10px"><a href="sakura_data.jsp#2016-07">SAKURA数据(定位到7月)</a></li>
    <li style="margin-bottom: 10px"><a href="list_disc.jsp?filter=table&name=mydvd">球总的动画列表</a></li>
    <li style="margin-bottom: 10px"><a href="list_disc.jsp?filter=table&name=myfav">站长的动画列表</a></li>
    <li style="margin-bottom: 10px"><a href="list_disc.jsp?filter=table&name=mycd">Schalke04的音乐列表</a></li>
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
