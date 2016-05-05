var page = {};
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
    var this_url = location.pathname.substr(1);

    page.scroll = function (selector) {
        $window.scrollTop($(selector).offset().top - 51);
    };
    page.fixpos = function () {
        $window.scrollTop($window.scrollTop() - 51);
    };
    page.is_this_page = function (url) {
        return this_url == url;
    };
    page.hash = function () {
        return location.hash;
    };
    device.width = function () {
        return $window.width();
    };
    device.is_small = function () {
        return this.width() < 768;
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
