function init(o) {
    let unit = o.attr("data-unit");
    $.get(`キャラ/${unit}/装備`, function (html) {
        o.before($(`<div id="${unit}" class="collapse" data-parent=".container-fluid"></div>`).html($(html).filter(".container-fluid").html())).attr("data-target", `#${unit}`).removeAttr("onclick").click();
    });
}