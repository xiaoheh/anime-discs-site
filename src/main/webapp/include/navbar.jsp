<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Navbar 开始 -->
<div id="navbar">
    <div class="navbar navbar-default navbar-fixed-top">
        <div class="navbar-header">
            <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">切换导航</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.jsp">Anime Discs</a>
            <a class="navbar-brand visible-xs float-right">
                <span class="btn-link" id="refresh">刷新</span>
                在线<span class="hidden-xxs">人数</span>: ${application.online}
            </a>
        </div>
        <div class="collapse navbar-collapse" role="navigation">
            <ul id="nav-body" class="nav navbar-nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">定位<b class="caret"></b></a>
                    <ul id="nav-mark" class="dropdown-menu">
                        <li><a href="#navbar" onclick="scroll.call(this)">回到顶部</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">推荐<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="sakura_data.jsp">SAKURA数据</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=top_100">日亚实时TOP100</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=2016-04">2016年04月新番</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=2016-01">2016年01月新番</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=mydvd">手动添加的DVD</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=mycd">手动添加的CD</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">浏览<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="list_table.jsp">浏览榜单</a></li>
                        <li><a href="list_disc.jsp">浏览碟片</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">专楼<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="http://tieba.baidu.com/p/4452273267?pn=9999" target="_blank">甲铁城专楼</a></li>
                        <li><a href="http://tieba.baidu.com/p/4452227415?pn=9999" target="_blank">马三角专楼</a></li>
                        <li><a href="http://tieba.baidu.com/f?kw=名作之壁&ie=utf-8" target="_blank">名作之壁吧</a></li>
                        <li><a href="http://tieba.baidu.com/f?kw=壁吧专楼&ie=utf-8" target="_blank">壁吧专楼吧</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">关于<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="http://tieba.baidu.com/home/main?un=fuhaiwei" target="_blank">@fuhaiwei</a></li>
                        <li><a href="mailto:fuhaiwei@163.com?subject=关于AnimeDiscs网站的建议" target="_blank">给我发邮件</a></li>
                    </ul>
                </li>
                <li class="hidden-xs"><a>在线人数: ${application.online}</a></li>
            </ul>
        </div>
    </div>
</div>
<script id="nav-mark-tmpl" type="text/html">
    <li><a href="{{hash}}" onclick="scroll.call(this)">{{title}}</a></li>
</script>
<!-- Navbar 结束 -->
