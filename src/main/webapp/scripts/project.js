var page = {};
var cache = {};
var device = {};
var navbar = {};
initial_object();

$(function () {
    active_current_page();
    register_close_action();
});

function active_current_page() {
    $("#navbar").find("li>a").each(function () {
        if (page.url() == $(this).attr("href")) {
            $(this).parents("li").addClass("active");
        }
    });
}

function register_close_action() {
    $("#postion").on("click", "a", function () {
        $(".navbar-collapse").collapse("hide");
    });
}

function initial_object() {
    var $window = $(window);

    page.scroll = function (selector) {
        $window.scrollTop($(selector).offset().top - 51);
    };
    page.hash = function () {
        return location.hash;
    };
    page.url = function () {
        return location.pathname.substr(1);
    };

    cache.get_or_create = function (key, func) {
        if (this[key]) {
            return this[key];
        } else {
            return this[key] = func();
        }
    };

    device.width = function () {
        return $window.width();
    };
    device.is_small = function () {
        return this.width() < 768;
    };
    device.switch = function (func) {
        var is_small = this.is_small();
        $window.resize(function () {
            var small = device.is_small();
            if (is_small != small) {
                is_small = func(small);
            }
        });
    };

    navbar.add_postion = function (id, title) {
        $("#postion").find("li").has("a[href='#" + id + "']").remove();
        var tmpl = '<li><a href="{{hash}}" onclick="scroll.call(this)">{{title}}</a></li>';
        $(Mustache.render(tmpl, {hash: "#" + id, title: title})).appendTo("#postion");
    };
}

function scroll() {
    setTimeout("page.scroll('" + $(this).attr("href") + "')", 10);
}
