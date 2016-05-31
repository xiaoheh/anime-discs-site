template.helper('fm_date', function (date, format) {
    return fm_date(date, format);
});

function fm_date(date, format) {
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
}

template.helper("fm_rank_date", function (time, type) {
    return fm_rank_date(time, type);
});

function fm_rank_date(time, type) {
    if (type == "CD") {
        return fm_date(time, 'yyyy-MM-dd hh时');
    } else {
        return fm_date(time, 'yyyy-MM-dd hh:mm:ss');
    }
}

template.helper('fm_timeout', function (time) {
    return fm_timeout(time);
});

function fm_timeout(time) {
    if (time == null) {
        return "00分 00秒";
    }
    var timeout = new Date().getTime() - time;
    if (timeout < 0) {
        return "00分 00秒";
    }
    if (timeout >= 3600000) {
        return fm_hour(timeout) + " " + fm_hour_end(timeout);
    } else {
        return fm_hour_end(timeout);
    }

    function fm_hour(timeout) {
        return Math.floor(timeout / 3600000) + "小时";
    }

    function fm_hour_end(timeout) {
        timeout %= 3600000;
        var m = Math.floor(timeout / 60000);
        var s = Math.floor(timeout % 60000 / 1000);
        return fm_num(m) + "分 " + fm_num(s) + "秒";
    }

    function fm_num(num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }
}

template.helper('fm_sakura', function (number, length) {
    return fm_sakura(number, length);
});

function fm_sakura(number, length) {
    number = number || 0;
    length = length || 6;
    length = length > 3 ? length + 1 : length;
    var zerofm = "---,---";
    var format = "***,***";
    if (!number || number <= 0) {
        return zerofm.substr(zerofm.length - length, length);
    }
    var string = fm_number(number, "###,###");
    return format.substr(format.length - length, length - string.length) + string;
}

template.helper('fm_star', function (number, length) {
    return fm_star(number, length);
});

function fm_star(number, length) {
    number = number || 0;
    length = length || 4;
    var zerofm = "------";
    var format = "******";
    if (!number || number <= 0) {
        return zerofm.substr(zerofm.length - length, length);
    }
    var string = number + "";
    return format.substr(format.length - length, length - string.length) + string;
}

template.helper("fm_number", function (number, format) {
    return fm_number(number, format);
});

function fm_number(number, format) {
    number = number || 0;
    format = format || "###,###";
    var result = "";
    var string = number < 0 ? -number + "" : number + "";
    var stridx = string.length - 1;
    var fmtidx = format.length - 1;
    while (stridx >= 0) {
        if (fmtidx < 0 || format.charAt(fmtidx) == "#") {
            result = string.charAt(stridx) + result;
            fmtidx--;
            stridx--;
        } else {
            result = format.charAt(fmtidx) + result;
            fmtidx--;
        }
    }
    return number < 0 ? "-" + result : result;
}

template.helper("fm_type", function (disc) {
    return fm_type(disc);
});

function fm_type(disc) {
    switch (disc["type"]) {
        case 0:
            return "◎";
        case 1:
            return "☆";
        case 2:
            return "△";
        case 3:
            return "★";
        case 4:
            return "▲";
        default:
            return "☒";
    }
}

template.helper("fm_srnk", function (disc) {
    return fm_srnk(disc);
});

function fm_srnk(disc) {
    return fm_sakura(disc["curk"]) + "位/" + fm_sakura(disc["prrk"]) + "位";
}

template.helper("fm_arnk", function (disc) {
    return fm_arnk(disc);
});

function fm_arnk(disc) {
    return fm_sakura(disc["rank1"]) + "位/" + fm_sakura(disc["rank2"]) + "位";
}

template.helper("fm_sname", function (disc) {
    return fm_sname(disc);
});

function fm_sname(disc) {
    var sname_text = disc["sname"] + " " + fm_type(disc);
    return disc["amzver"] ? sname_text + " 卐" : sname_text;
}

template.helper("fm_rank", function (disc, length) {
    return fm_rank(disc, length);
});

function fm_rank(disc, length) {
    if (is_sakura_late(disc)) {
        return fm_star(disc["rank1"], length) + "/" + fm_star(disc["rank2"], length);
    } else {
        return fm_star(disc["curk"], length) + "/" + fm_star(disc["prrk"], length);
    }
}

function is_sakura_late(disc) {
    return disc["acot"] && disc["rank1"] != disc["curk"] && disc["acot"] > disc["stot"] - 20 * 60000
}

template.helper("fm_rank_number", function (disc) {
    return fm_rank_number(disc);
});

function fm_rank_number(disc) {
    if (is_sakura_late(disc)) {
        return disc["rank1"];
    } else {
        return disc["curk"];
    }
}

template.helper("fm_rank_class", function (disc) {
    return fm_rank_class(disc);
});

function fm_rank_class(disc) {
    if (is_sakura_late(disc)) {
        if (is_timein(disc["acot"], 10)) {
            return "danger";
        } else {
            return "info";
        }
    } else {
        return fm_time_class(disc["stot"]);
    }
}

template.helper("fm_time_class", function (time) {
    return fm_time_class(time);
});

function fm_time_class(time) {
    if (is_timein(time, 10)) {
        return "success";
    }
    if (is_timeout(time, 60)) {
        return "warning";
    }
    return "";
}

template.helper("is_timeout", function (time, minute) {
    return is_timeout(time, minute);
});

function is_timeout(time, minute) {
    minute = minute || 120;
    if (time)
        return new Date().getTime() - time > minute * 60000;
    return false;
}

function is_timein(time, minute) {
    minute = minute || 120;
    if (time)
        return new Date().getTime() - time < minute * 60000;
    return false;
}
