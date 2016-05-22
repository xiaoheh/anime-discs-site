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
<script>
    function edit_mode() {
        $.cookie("admin", true, {expires: 7});
    }
    function view_mode() {
        $.cookie("admin", false, {expires: 7});
    }
</script>
<script>
</script>
</body>
</html>
