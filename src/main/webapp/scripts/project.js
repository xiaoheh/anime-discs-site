var page = {};
var form = {};
var cache = {};
var table = {};
var device = {};
var navbar = {};
var offset = {};

$(function () {
    initial_object();
    update_active_status();
    handle_aclick_action();
});

function update_active_status() {
    $("#navbar").find("li>a").each(function () {
        if (page.url() + page.search() == $(this).attr("href")) {
            $(this).parents("li").addClass("active");
        }
    });
}

function handle_aclick_action() {
    $("#nav-mark").on("click", "a", function () {
        $(".navbar-collapse").collapse("hide");
    });
    $("#refresh").click(function () {
        navbar.refresh();
    });
}

function render(id, data) {
    return cache.get_or_create(id, function () {
        return template(id);
    })(data);
}

function scroll() {
    setTimeout("offset.to_hash('" + $(this).attr("href") + "')", 10);
}

function initial_object() {
    init_page();
    init_form();
    init_cache();
    init_table();
    init_device();
    init_navbar();
    init_offset();

    function init_page() {
        page.href = function () {
            return location.href;
        };
        page.home = function () {
            return location.origin + "/";
        };
        page.path = function () {
            return this.href().substr(this.home().length);
        };
        page.url = function () {
            return location.pathname.substr(1);
        };
        page.hash = function () {
            return location.hash;
        };
        page.search = function () {
            return location.search;
        };
        page.go = function (url) {
            location.href = url;
        };
        page.back = function () {
            history.back();
        };
    }

    function init_form() {
        form.info = function (msg) {
            $("#msg").html("<span class='text-info'>" + msg + "</span>");
        };
        form.danger = function (msg) {
            $("#msg").html("<span class='text-danger'>" + msg + "</span>");
        };
        form.success = function (msg) {
            $("#msg").html("<span class='text-success'>" + msg + "</span>");
        };
    }

    function init_cache() {
        cache.get_or_create = function (key, func) {
            if (this[key]) {
                return this[key];
            } else {
                return this[key] = func();
            }
        };
        cache.is_first = function (key) {
            return this[key] ? false : (this[key] = true);
        };
    }

    function init_table() {
        var tables, status, show_profile;
        table.sorter = function (selector) {
            tables = [];
            $(selector).each(function () {
                tables.push(this);
                tablesorter(this);
            });
        };
        table.save_status = function () {
            status = save_status(tables);
        };
        table.load_status = function () {
            load_status(status);
        };
        table.show_pro = function (func) {
            if (func) {
                show_profile = func;
            } else if (show_profile) {
                show_profile();
            }
        }
    }

    function init_device() {
        device.width = function () {
            return $(window).width();
        };
        device.is_small = function () {
            return this.width() < 768;
        };
        device.switch = function (func) {
            var is_small = this.is_small();
            $(window).resize(function () {
                var small = device.is_small();
                if (is_small != small) {
                    is_small = func(small) || small;
                }
            });
        };
    }

    function init_navbar() {
        var refresh_func;

        function do_refresh() {
            $("#refresh").text("更新中");
            refresh_func();
            setTimeout(function () {
                $("#refresh").text("刷新");
            }, 200);
        }

        navbar.add_postion = function (id, title) {
            var data = {hash: "#" + id, title: title};
            $("#nav-mark").find("a[href='" + data.hash + "']").remove();
            $(template("nav-mark-tmpl", data)).appendTo("#nav-mark");
        };
        navbar.refresh = function (func) {
            if (func) {
                refresh_func = func;
            } else if (refresh_func) {
                do_refresh();
            } else {
                page.go(page.href());
            }
        };
    }

    function init_offset() {
        offset.pos = function () {
            return $(window).scrollTop();
        };
        offset.to_pos = function (pos) {
            $(window).scrollTop(pos);
        };
        offset.to_hash = function (selector) {
            var $elem = $(selector);
            if ($elem.size() > 0) {
                this.to_pos($elem.offset().top - 60);
            }
        };
        offset.restore = function () {
            if (page.hash() != "") {
                this.to_hash(page.hash());
            }
        };
        offset.save = function () {
            cache["offset"] = this.pos();
        };
        offset.load = function () {
            this.to_pos(cache["offset"]);
        };
    }

}
