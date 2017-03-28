function render_profile(quick) {
    var data = {quick: quick, hidden: scan_hidden()};
    $("#profile-body").html(template("profile-tmpl", data));
    handle_quick_action();
    handle_hidden_action();
    restore_checked();

    function scan_hidden() {
        var hidden = [];
        $("table").first().find("th").not(".zero-width").each(function () {
            hidden.push({
                clazz: $(this).prop("class").split(" ")[0],
                title: $(this).text()
            });
        });
        return hidden;
    }

    function handle_quick_action() {
        $("#div-quick").find("button").click(function () {
            apply_checked($(this).data("checked").split(","));
        });
    }

    function handle_hidden_action() {
        $("#div-hidden").find(":checkbox").click(function () {
            $("table").find("tr ." + $(this).data("class")).toggle();
            save_checked_status();
        });
    }

    function get_checked_key() {
        return page.key("checked-01");
    }

    function save_checked_status() {
        var checked = [];
        $("#div-hidden").find(":checked").each(function () {
            checked.push($(this).data("class"));
        });
        $.cookie(get_checked_key(), checked.join("-"), {expires: 7});
    }

    function restore_checked() {
        if ($.cookie(get_checked_key())) {
            apply_checked($.cookie(get_checked_key()).split("-"));
        } else {
            $("#div-quick").find("button").first().click();
        }
    }

    function apply_checked(checked) {
        $("#div-hidden").find(":checkbox").each(function () {
            if ($(this).prop("checked") != checked.indexOf($(this).data("class")) > -1) {
                $(this).click();
            }
        });
    }
}

function tablesorter(table) {

    var $table = $(table);

    $table.find("thead>tr>th.sorter").click(function () {
        var $th = $(this);
        var index = $th.index();
        var rows = get_sorted_rows($table, index);
        if (!$th.hasClass("ascing")) {
            set_sorted_clazz($th, "ascing");
            $table.find("tbody").append(rows);
        } else {
            set_sorted_clazz($th, "descing");
            $table.find("tbody").append(rows.reverse());
        }
    });

    function set_sorted_clazz($th, clazz) {
        $th.parents("tr").find("th").removeClass("ascing");
        $th.parents("tr").find("th").removeClass("descing");
        $th.addClass(clazz);
        $table.data("th", $th.index());
        $table.data("st", clazz);
    }

    function get_sorted_rows($table, index) {
        var rows = $table.find("tbody tr").toArray();
        rows.sort(function (a, b) {
            var $a = $(a).find("td").eq(index);
            var $b = $(b).find("td").eq(index);
            return compare_to($a, $b);
        });
        return rows;
    }

    function compare_to($a, $b) {
        if (has_number($a) || has_number($b)) {
            if (has_number($a) && has_number($b)) {
                return get_number($a) - get_number($b);
            } else {
                return has_number($a) ? 1 : -1;
            }
        }
        return get_text($a).localeCompare(get_text($b));
    }

    function get_number($td) {
        return parseInt($td.data("number"));
    }

    function has_number($td) {
        return $td.data("number") !== undefined && $td.data("number") !== "";
    }

    function get_text($td) {
        if ($td.hasClass("editable")) {
            return $td.data("value");
        } else {
            return $td.text();
        }
    }

}

function save_status(talbes) {
    var status = [];
    $(talbes).each(function () {
        status.push({
            id: $(this).attr("id"),
            th: $(this).data("th"),
            st: $(this).data("st")
        });
    });
    return status;
}

function load_status(status) {
    $(status).each(function () {
        if (this.th) {
            var $th = $("#" + this.id).find("th").eq(this.th);
            if (this.st == "descing") {
                $th.click();
                $th.click();
            } else {
                $th.click();
            }
        }
    });
}
