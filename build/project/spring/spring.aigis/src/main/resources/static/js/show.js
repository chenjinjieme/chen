const container = $(".container-fluid"), card_null = "ba03bf9e03940e468d0d67dc7a166ffc", cg_null = "77e8475e8dd35ccb9dc7d8b6a9f86186";
$(function () {
    l0();
    history.replaceState(null, null);
    onpopstate = function () {
        let search = location.search;
        if (search === "" || search === "?status=new") ne0();
        else if (search === "?status=all") a0();
        else if (search === "?status=null") nu0();
    };
});

function l0() {
    $("img").lazyload({
        effect: 'fadeIn',
        threshold: 12000,
        skip_invisible: true
    });
}

function l() {
    $.get(location.search, function (o) {
        container.html($(o).filter(".container-fluid").html());
        l0();
    });
}

function update(o) {
    let prevAll = o.prevAll(), name = prevAll.eq(3).val(), sex = prevAll.eq(2).val(), parent = o.parent().parent();
    if (name !== "" || sex === "-1") $.post("unit", {
        id: parent.prev().find("input").val(),
        name: name,
        sex: sex,
        rare: prevAll.eq(1).val(),
        clazz: prevAll.eq(0).val()
    }, function () {
        let p = parent.html(`<div class="col-2">\n<input class="form-control form-control-sm" value="${name}" readonly>\n</div>`).parent().addClass("check"), search = location.search;
        if (name === "") p.addClass("null");
        if (search === "" || search === "?status=new" || search === "?status=null" && name !== "") p.prop("hidden", true);
    });
}

function a0() {
    $(".check").prop("hidden", false);
}

function a() {
    history.pushState(null, null, "?status=all");
    a0();
}

function ne0() {
    $(".check").prop("hidden", true);
}

function ne() {
    history.pushState(null, null, "?status=new");
    ne0();
}

function nu0() {
    $(".check").prop("hidden", true).filter(".null").prop("hidden", false).each(function () {
        $(this).find("img").each(function () {
            let o = $(this), name = o.prev().find("input").val(), src = o.attr("data-original");
            if (name.indexOf("card") > 0 && src.indexOf(card_null) !== 74 || name.indexOf("HarlemCG") === 0 && src.indexOf(cg_null) !== 74) {
                let first = o.parent().removeClass().children().first();
                $.post(`unit/${first.find("input").val()}`);
                first.next().html(container.children().eq(1).html());
                return false;
            }
        });
    });
}

function nu() {
    history.pushState(null, null, "?status=null");
    nu0();
}