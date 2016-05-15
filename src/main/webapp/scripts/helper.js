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

template.helper('fm_timeout', function (time) {
    return fm_timeout(time);
});

function fm_timeout(time) {
    if (time == null) {
        return "--分 --秒";
    }
    var timeout = new Date().getTime() - time;
    if (timeout < 0) {
        return "--分 --秒";
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

template.helper("fm_verstr", function (disc) {
    return fm_verstr(disc);
});

function fm_verstr(disc) {
    return disc["amzver"] ? fm_type(disc) + " 卐" : fm_type(disc);
}

template.helper("fm_type", function (disc) {
    return fm_type(disc);
});

function fm_type(disc) {
    if (disc["dvdver"]) {
        return disc["boxver"] ? "▲" : "△";
    } else {
        return disc["boxver"] ? "★" : "☆";
    }
}

template.helper("fm_srnk", function (disc) {
    return fm_srnk(disc);
});

function fm_srnk(disc) {
    return fm_sakura(disc["curk"]) + "位/" + fm_sakura(disc["prrk"]) + "位";
}

template.helper("fm_dirk", function (disc) {
    return fm_dirk(disc);
});

function fm_dirk(disc) {
    if (disc["rank1"] && disc["rank2"]) {
        return fm_star(disc["rank1"]) + "/" + fm_star(disc["rank2"]);
    } else {
        return fm_star(disc["arnk"]) + "/" + fm_star(disc["curk"]);
    }
}

template.helper("fm_eqrk", function (disc) {
    return fm_eqrk(disc);
});

function fm_eqrk(disc) {
    return fm_star(disc["curk"]) + "/" + fm_star(disc["prrk"]);
}

template.helper("is_timeout", function (time) {
    return is_timeout(time);
});

function is_timeout(time) {
    if (time)
        return new Date().getTime() - time > 7200000;
    return false;
}
