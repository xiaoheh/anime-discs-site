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
            <a class="navbar-brand visible-xs float-right">在线人数: ${application.online}</a>
        </div>
        <div class="collapse navbar-collapse" role="navigation">
            <ul class="nav navbar-nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">定位<b class="caret"></b></a>
                    <ul id="postion" class="dropdown-menu">
                        <li><a href="#navbar" onclick="fixpos()">回到顶部</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">推荐<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="sakura_data.jsp">SAKURA数据</a></li>
                        <li><a href="curt_volume.jsp">首卷预测排名</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">浏览<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="list_disc.jsp">浏览碟片</a></li>
                        <li><a href="list_table.jsp">浏览榜单</a></li>
                        <li><a href="list_anime.jsp">浏览动画</a></li>
                        <li><a href="list_season.jsp">浏览番季</a></li>
                        <li><a href="list_series.jsp">浏览系列</a></li>
                        <li><a href="list_product.jsp">浏览制作</a></li>
                        <li><a href="list_publish.jsp">浏览发行</a></li>
                        <li><a href="list_original.jsp">浏览原作</a></li>
                        <li><a href="list_director.jsp">浏览监督</a></li>
                        <li><a href="list_scenarist.jsp">浏览编剧</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">查找<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="find_disc.jsp">查找碟片</a></li>
                        <li><a href="find_table.jsp">查找榜单</a></li>
                        <li><a href="find_anime.jsp">查找动画</a></li>
                        <li><a href="find_season.jsp">查找番季</a></li>
                        <li><a href="find_series.jsp">查找系列</a></li>
                        <li><a href="find_product.jsp">查找制作</a></li>
                        <li><a href="find_publish.jsp">查找发行</a></li>
                        <li><a href="find_original.jsp">查找原作</a></li>
                        <li><a href="find_director.jsp">查找监督</a></li>
                        <li><a href="find_scenarist.jsp">查找编剧</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">添加<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="add_disc.jsp">添加碟片</a></li>
                        <li><a href="add_table.jsp">添加榜单</a></li>
                        <li><a href="add_anime.jsp">添加动画</a></li>
                        <li><a href="add_season.jsp">添加番季</a></li>
                        <li><a href="add_series.jsp">添加系列</a></li>
                        <li><a href="add_product.jsp">添加制作</a></li>
                        <li><a href="add_publish.jsp">添加发行</a></li>
                        <li><a href="add_original.jsp">添加原作</a></li>
                        <li><a href="add_director.jsp">添加监督</a></li>
                        <li><a href="add_scenarist.jsp">添加编剧</a></li>
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
                <li class="hidden-xs"><a>在线人数: ${application.online}</a></li>
            </ul>
        </div>
    </div>
</div>
<!-- Navbar 结束 -->
