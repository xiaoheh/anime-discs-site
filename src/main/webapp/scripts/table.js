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
        if ($a.data("number") != null) {
            return get_number($a) - get_number($b);
        } else {
            return get_text($a).localeCompare(get_text($b));
        }
    }

    function get_number($td) {
        return parseInt($td.data("number"));
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
