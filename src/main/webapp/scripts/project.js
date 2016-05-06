var page = {};
var cache = {};
var device = {};
var navbar = {};
initial_object();

$(function () {
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
                is_small = func(small) || small;
            }
        });
    };

    navbar.add_postion = function (id, title) {
        var data = {hash: "#" + id, title: title};
        $(template("postion-tmpl", data)).appendTo("#postion");
    };
}

function scroll() {
    setTimeout("page.scroll('" + $(this).attr("href") + "')", 10);
}

function restore_postion() {
    page.scroll(page.hash());
}
