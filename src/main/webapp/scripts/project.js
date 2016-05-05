var page = {};

$(function () {
    active_current_page();
});

function active_current_page() {
    var page_url = get_page_url();
    $("#navbar").find("li>a").each(function () {
        if (page_url == $(this).attr("href")) {
            $(this).parents("li").addClass("active");
        }
    });
}

function get_page_url() {
    var text = window.location.toString();
    if (text.indexOf("?") > 0) {
        text = text.substr(0, text.indexOf("?"));
    }
    if (text.indexOf("#") > 0) {
        text = text.substr(0, text.indexOf("#"));
    }
    if (text.indexOf("/") >= 0) {
        text = text.substr(text.lastIndexOf("/") + 1);
    }
    return text == "" ? "index.jsp" : text;
}

function get_page_hash() {
    var text = window.location.toString();
    var indexOf = text.lastIndexOf("#");
    return indexOf > 0 ? text.substr(indexOf + 1) : null;
}

function scroll_window(hash) {
    var top = $("#" + hash).offset().top;
    var fix = hash == 'navbar' ? 60 : 51;
    $(window).scrollTop(top - fix);
}

function fix_scroll(size) {
    setTimeout(function () {
        $(window).scrollTop(window.scrollY - (size || 51));
    }, 10);
}

function add_postion(hash, title) {
    $("#postion").find("li").has("a[href='#" + hash + "']").remove();
    var tmpl = '<li><a href="#{{hash}}" onclick="fix_scroll()">{{title}}</a></li>';
    $(Mustache.render(tmpl, {hash: hash, title: title})).appendTo("#postion");
}
