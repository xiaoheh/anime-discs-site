var page = {};
var cache = {};
var device = {};
var navbar = {};
initial_object();

$(function () {
    active_current_page();
});

function active_current_page() {
    $("#navbar").find("li>a").each(function () {
        if (page.is_this_page($(this).attr("href"))) {
            $(this).parents("li").addClass("active");
        }
    });
}

function initial_object() {
    var $window = $(window);

    page.scroll = function (selector) {
        $window.scrollTop($(selector).offset().top - 51);
    };
    page.fixpos = function () {
        $window.scrollTop($window.scrollTop() - 51);
    };
    page.is_this_page = function (url) {
        return this.url() == url;
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
        var tmpl = '<li><a href="{{hash}}" onclick="fixpos()">{{title}}</a></li>';
        $(Mustache.render(tmpl, {hash: "#" + id, title: title})).appendTo("#postion");
    };
}

function fixpos() {
    setTimeout(page.fixpos, 10);
}
