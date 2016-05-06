<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="include/meta.jsp" %>
    <title>Anime Discs - Sakura数据</title>
    <%@ include file="include/import.jsp" %>
</head>
<body>
<%@ include file="include/navbar.jsp" %>
<div id="content"></div>
<script>
    var discs = [{
        amzn: 99, curk: 100, prrk: 100, cupt: 3456, tdpt: 3200, title: "少女与战车 非尼 BD1"
    }, {
        amzn: 110, curk: 110, prrk: 120, cupt: 24901, tdpt: 120, title: "少女与战车 尼限 BD1"
    }, {
        amzn: 110, curk: 110, prrk: 120, cupt: 999999, tdpt: 120, title: "少女与战车 非尼 DVD1"
    }, {
        amzn: 110, curk: 110, prrk: 120, cupt: 2490, tdpt: 120, title: "少女与战车 尼限 DVD1"
    }, {
        amzn: 1234, curk: 3000, prrk: 2980, cupt: 203, tdpt: 40, title: "少女与战车 3"
    }];
    var view = {
        tables: [
            {key: "top_100", title: "日亚实时TOP100", discs: discs},
            {key: "2016-04", title: "2016年04月新番", discs: discs},
            {key: "2016-01", title: "2016年01月新番", discs: discs}
        ]
    };

    handle_data(view);
    render_page(device.is_small());
    register_switch();

    function handle_data(view) {
        $(view.tables).each(function () {
            var index = 0;
            navbar.add_postion(this.key, this.title);
            $(this.discs).each(function () {
                this.index = ++index;
                if (this.amzn == this.curk) {
                    this.rank = Mustache.render("{{curk}}/{{prrk}}", this);
                    this.rank_clazz = 'danger';
                } else {
                    this.rank = Mustache.render("{{amzn}}/{{curk}}", this);
                }
            });
        });
    }

    function render_page(is_small) {
        if (is_small) {
            $.get("template/sakura_data_small.html", function (text) {
                $("#content").html(Mustache.render(text, view));
            });
        } else {
            $.get("template/sakura_data.html", function (text) {
                $("#content").html(Mustache.render(text, view));
            });
        }
    }

    function register_switch() {
        device.switch(function (is_small) {
            render_page(is_small);
            return is_small;
        });
    }
</script>
</body>
</html>
