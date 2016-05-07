var page = {};
var form = {};
var cache = {};
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
        if (page.url() == $(this).attr("href")) {
            $(this).parents("li").addClass("active");
        }
    });
}

function handle_aclick_action() {
    $("#nav-mark").on("click", "a", function () {
        $(".navbar-collapse").collapse("hide");
    });
    $("#refresh").click(function () {
        if (typeof (refresh) == "function") {
            refresh();
        } else {
            page.go(page.path());
        }
    });
}

function render(id, data) {
    return cache.get_or_create(id, function () {
        return template(id);
    })(data);
}

function scroll() {
    setTimeout("offset.scroll('" + $(this).attr("href") + "')", 10);
}

function initial_object() {
    init_page();
    init_form();
    init_cache();
    init_device();
    init_navbar();
    init_offset();

    function init_page() {
        page.hash = function () {
            return location.hash;
        };
        page.path = function () {
            return location.pathname;
        };
        page.url = function () {
            return this.path().substr(1);
        };
        page.go = function (url) {
            location.replace(url);
        };
        page.go_with_src = function (url, data, hash) {
            data.src = this.url() + (hash || "");
            this.go(url + "?" + $.param(data));
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
        navbar.add_postion = function (id, title) {
            var data = {hash: "#" + id, title: title};
            $("#nav-mark").find("a[href='" + data.hash + "']").remove();
            $(template("nav-mark-tmpl", data)).appendTo("#nav-mark");
        };
    }

    function init_offset() {
        offset.scroll = function (selector) {
            var $elements = $(selector);
            if ($elements.size() > 0) {
                var pos = $elements.offset().top;
                $(window).scrollTop(pos - 60);
            }
        };
        offset.tohash = function () {
            if (page.hash() != "") {
                this.scroll(page.hash());
            }
        };
        offset.save = function () {
            cache["offset"] = $(window).scrollTop();
        };
        offset.load = function () {
            $(window).scrollTop(cache["offset"]);
        };
    }

}
