const card_null = "ba03bf9e03940e468d0d67dc7a166ffc", cg_null = "77e8475e8dd35ccb9dc7d8b6a9f86186",
    update = $(".update").html();
String.prototype.temp = function (obj) {
    return this.replace(/\$\w+\$/gi, function (matchs) {
        let returns = obj[matchs.replace(/\$/g, "")];
        return (returns + "") === "undefined" ? "" : returns;
    });
};
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
    $(".lazy").lazyload({
        effect: 'fadeIn',
        threshold: 12000,
        skip_invisible: true
    });
}

function l() {
    $.get(location.search, function (o) {
        $(".warp").html($("<div></div>").html(o).find(".warp").html());
        l0();
    });
}

function u(o) {
    let clazz = o.prev(), rare = clazz.prev(), sex = rare.prev(), name = sex.prev();
    if ((name = name.val()) !== "" || sex.val() === "-1") $.post("/aigis/character", {
        id: o.parent().prev().html(),
        name: name,
        sex: sex.val(),
        rare: rare.val(),
        clazz: clazz.val()
    }, function () {
        let p = o.parent().html(name).parent().addClass("check");
        if (name === "") p.addClass("null");
        if (location.search !== "?status=null" || name !== "") p.prop("hidden", true);
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
    $(".check").prop("hidden", true);
    $(".check.null").prop("hidden", false).each(function () {
        $(this).find("img").each(function () {
            let name = $(this).prev().html(), src = $(this).attr("data-original");
            if (name.indexOf("card") === 4 && src.indexOf(card_null) !== 74 || name.indexOf("HarlemCG") === 0 && src.indexOf(cg_null) !== 74) {
                let first = $(this).parent().parent().removeClass().children().first();
                $.post("/aigis/character/" + first.html());
                first.next().html(update);
                return false;
            }
        });
    });
}

function nu() {
    history.pushState(null, null, "?status=null");
    nu0();
}