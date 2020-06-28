let d = $(document), val = "";

function equipment(o) {
    $.get(o.attr("data-src"), function (html) {
        let fix = o.offset().top - d.scrollTop();
        o.before($("<div></div>").html(html)).attr("onclick", "toggle($(this))");
        d.scrollTop(o.offset().top - fix);
    });
}

function toggle(o) {
    let fix = o.offset().top - d.scrollTop();
    o.prev().toggle();
    d.scrollTop(o.offset().top - fix);
}