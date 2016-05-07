template.helper('dateFormat', function (date, format) {

    date = new Date(date);

    var map = {
        "M": date.getMonth() + 1, //月份
        "d": date.getDate(), //日
        "h": date.getHours(), //小时
        "m": date.getMinutes(), //分
        "s": date.getSeconds(), //秒
        "q": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    };

    format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
        var v = map[t];
        if (v !== undefined) {
            if (all.length > 1) {
                v = '0' + v;
                v = v.substr(v.length - 2);
            }
            return v;
        }
        else if (t === 'y') {
            return (date.getFullYear() + '').substr(4 - all.length);
        }
        return all;
    });

    return format;

});

template.helper('fm_timeout', function (time) {

    var timeout = new Date().getTime() - time;

    if (timeout >= 3600000) {
        var m = timeout % 3600000;
        var h = Math.floor(timeout / 3600000);
        return h + "小时 " + Math.floor(m / 60000) + "分";
    } else {
        var s = timeout % 60000;
        var m = Math.floor(timeout / 60000);
        return m + "分 " + Math.floor(s / 1000) + "秒";
    }

});

template.helper('fm_sakura', function (number, width) {
    width = width | 4;
    var format = "******";
    var string = number + "";
    return format.substring(6 - width, 6 - string.length) + string;
});

template.helper("fm_sname", function (disc) {
    var sname = disc.sname + typestr(disc["type"]);
    return disc["amzver"] ? sname + " 卐" : sname;
});

function typestr(name) {
    switch (name) {
        case "BD":
            return " ☆";
        case "DVD":
            return " △";
        case "BOX":
            return " ◎";
        default:
            return " ♢";
    }
}
