let updating = false, list = [], collapse = $(".collapse"), x, y, z, bar = collapse.find(".progress-bar"), yBar = bar.eq(1), zBar = bar.eq(0);
$(function () {
    $("input[onblur]").each(function () {
        let o = $(this);
        if (parseInt(o.val()) < 0) compose(o.next());
    });
});

function copy(o) {
    o.select();
    document.execCommand("copy");
}

function progress() {
    yBar.attr("style", `width: ${y / x * 100}%`).html(y);
    zBar.attr("style", `width: ${z / x * 100}%`).html(z);
}

function update(o) {
    let id = o.attr("id");
    if (list.indexOf(id) < 0) {
        list.push(id);
        if (!updating) {
            updating = true;
            x = 1;
            y = 0;
            z = 0;
            collapse.collapse("show");
            update0();
        } else x++;
        progress();
    }
}

function update0() {
    let shift = list.shift(), o = $(`#${shift}`), parent = o.closest("[data-equipment]"), own = parseInt(o.val());
    $.post(`装備/${parent.attr("data-equipment")}/编辑`, {own: own}, function () {
        let find = parent.children().last().find(".input-group").find("input");
        find.first().val(parseInt(find.last().val()) + own);
        y++;
        progress();
        if (list.length > 0) update0(); else updating = false;
    }).fail(function () {
        z++;
        progress();
        update(o);
    });
}

function compose(o) {
    let prev = o.prev(), own = parseInt(prev.val()), num = own < 0 ? -own : 1;
    prev.val(own + num);
    update(prev);
    o.parents(".row").nextAll().last().find("input[data-material]").each(function () {
        let material = $(this);
        let children = $(`div[data-equipment="${material.attr("data-material")}"]`).find("input[onblur]"), own = parseInt(children.val()) - parseInt(material.val()) * num;
        children.val(own);
        if (own < 0) compose(children.next()); else update(children);
    });
}