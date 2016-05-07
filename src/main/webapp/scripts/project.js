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
            page.go(page.href());
        }
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
    init_device();
    init_navbar();
    init_offset();

    function init_page() {
        page.href = function () {
            return location.href;
        };
        page.path = function () {
            return location.pathname;
        };
        page.hash = function () {
            return location.hash;
        };
        page.url = function () {
            return this.path().substr(1);
        };
        page.go = function (url) {
            location.href = url;
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
