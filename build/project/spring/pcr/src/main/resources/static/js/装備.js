$(function () {
    $("input").each(function () {
        let o = $(this);
        if (parseInt(o.val()) < 0) compose(o.next());
    });
});

function update(o) {
    let own = parseInt(o.val());
    $.post("/プリコネR/装備/" + o.parent().attr("data-equipment") + "/编辑", {own: own}, function () {
        let find = o.next().next().find("span");
        find.first().html(parseInt(find.last().html()) + own);
    });
}

function compose(o) {
    let prev = o.prev();
    let own = parseInt(prev.val());
    let num = own < 0 ? -own : 1;
    prev.val(own + num);
    update(prev);
    o.next().find(".num").each(function () {
        let children = $("div[data-equipment='" + $(this).attr("data-material") + "']").children("input");
        let own = parseInt(children.val()) - parseInt($(this).html()) * num;
        children.val(own);
        if (own < 0) compose(children.next()); else update(children);
    });
}