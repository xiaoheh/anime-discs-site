<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="/include/meta.jsp" %>
    <title>Anime Discs - 后台</title>
    <%@ include file="/include/import.jsp" %>
</head>
<body>
<%@ include file="/include/navbar.jsp" %>
<h4>Welcome to this site!</h4>
<h4>推荐页面: <a href="sakura_data.jsp">Sakura数据</a></h4>
<button onclick="edit_mode()">编辑模式</button>
<button onclick="view_mode()">浏览模式</button>
<button onclick="do_update()">更新全表</button>
<script>
    function edit_mode() {
        $.cookie("admin", true, {expires: 7});
        alert("已经切换为编辑模式");
    }
    function view_mode() {
        $.cookie("admin", false, {expires: 7});
        alert("已经切换为浏览模式");
    }
    function do_update() {
        $.get("index.do", {method: "update"});
        alert("已经发出更新全表请求");
    }
</script>
<script>
</script>
</body>
</html>
