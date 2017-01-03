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
                        <li><a href="list_disc.jsp?filter=table&name=mydvd">球总的动画列表</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=myfav">站长的动画列表</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=xxlonge">xxlonge的动画列表</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=mycd">Schalke04的音乐列表</a></li>
                        <li><a href="list_disc.jsp?filter=type&type=dvd&name=top100">TOP100列表备份</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">列表<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="list_table.jsp">查看所有碟片列表</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=touken">刀剑乱舞-花丸-</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=euphonium">吹响吧! 悠风号2</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=gundam">机动战士高达</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=brave">无畏魔女</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=llss">LoveLive! Sunshine!!</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=rezero">RE: 从零开始的异世界生活</a></li>
                        <li><a href="list_disc.jsp?filter=table&name=macross">超时空要塞Δ</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">浏览<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="list_disc.jsp?filter=type&type=dvd&latest=true">近期动画碟片</a></li>
                        <li><a href="list_disc.jsp?filter=type&type=cd&latest=true">近期音乐碟片</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">专楼<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="http://tieba.baidu.com/f?kw=名作之壁&ie=utf-8" target="_blank">名作之壁吧</a></li>
                        <li><a href="http://tieba.baidu.com/f?kw=壁吧专楼&ie=utf-8" target="_blank">壁吧专楼吧</a></li>
                        <li><a href="http://tieba.baidu.com/p/4923220045?pn=9999" target="_blank">PT排名走势(17年01月)</a></li>
                        <li><a href="http://tieba.baidu.com/p/4802024610?pn=9999" target="_blank">PT排名走势(16年10月)</a></li>
                        <li><a href="http://tieba.baidu.com/p/4802499796?pn=9999" target="_blank">吹响!悠风号2专楼</a></li>
                        <li><a href="http://tieba.baidu.com/p/4633906279?pn=9999" target="_blank">Sunshine专楼</a></li>
                        <li><a href="http://tieba.baidu.com/p/4773136352?pn=9999" target="_blank">从零开始专楼</a></li>
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
