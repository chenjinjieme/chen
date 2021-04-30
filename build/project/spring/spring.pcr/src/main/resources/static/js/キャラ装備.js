function copy(o) {
    o.select();
    document.execCommand("copy");
}

function addAll(o) {
    let parent = o.parent(), rank = parent.attr("data-rank");
    $.post(`キャラ/${parent.parent().attr("data-unit")}/rank/${rank}/新增`, function () {
        parent.before(`<div data-rank='${rank}'>\n<div class="row">\n<div class="col-2">\n<input class="form-control form-control-sm" value="Rank${rank}" readonly>\n</div>\n</div>\n<div class="row">\n<div class="col-2" data-order="1">\n<img src="icon/装備/icon_equipment_999999.png">\n</div>\n<div class="col-2" data-order="2">\n<img src="icon/装備/icon_equipment_999999.png">\n</div>\n<div class="col-2" data-order="3">\n<img src="icon/装備/icon_equipment_999999.png">\n</div>\n<div class="col-2" data-order="4">\n<img src="icon/装備/icon_equipment_999999.png">\n</div>\n<div class="col-2" data-order="5">\n<img src="icon/装備/icon_equipment_999999.png">\n</div>\n<div class="col-2" data-order="6">\n<img src="icon/装備/icon_equipment_999999.png">\n</div>\n</div>\n<div class="row">\n<div class="col-2" data-order="1">\n<input class="form-control form-control-sm" value="？？？" readonly onclick="copy($(this))">\n</div>\n<div class="col-2" data-order="2">\n<input class="form-control form-control-sm" value="？？？" readonly onclick="copy($(this))">\n</div>\n<div class="col-2" data-order="3">\n<input class="form-control form-control-sm" value="？？？" readonly onclick="copy($(this))">\n</div>\n<div class="col-2" data-order="4">\n<input class="form-control form-control-sm" value="？？？" readonly onclick="copy($(this))">\n</div>\n<div class="col-2" data-order="5">\n<input class="form-control form-control-sm" value="？？？" readonly onclick="copy($(this))">\n</div>\n<div class="col-2" data-order="6">\n<input class="form-control form-control-sm" value="？？？" readonly onclick="copy($(this))">\n</div>\n</div>        \n<div class="row">\n<div class="col-2" data-order="1">\n<div class="input-group input-group-sm">\n<input class="form-control">\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="update($(this))">编辑</button>\n</div>\n</div>\n<div class="col-2" data-order="2">\n<div class="input-group input-group-sm">\n<input class="form-control">\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="update($(this))">编辑</button>\n</div>\n</div>\n<div class="col-2" data-order="3">\n<div class="input-group input-group-sm">\n<input class="form-control">\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="update($(this))">编辑</button>\n</div>\n</div>\n<div class="col-2" data-order="4">\n<div class="input-group input-group-sm">\n<input class="form-control">\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="update($(this))">编辑</button>\n</div>\n</div>\n<div class="col-2" data-order="5">\n<div class="input-group input-group-sm">\n<input class="form-control">\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="update($(this))">编辑</button>\n</div>\n</div>\n<div class="col-2" data-order="6">\n<div class="input-group input-group-sm">\n<input class="form-control">\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="update($(this))">编辑</button>\n</div>\n</div>\n</div>\n</div>`)
        parent.attr("data-rank", parseInt(rank) + 1);
    });
}

function update(o) {
    let parent = o.parent().parent(), parents = parent.parents(), order = parent.attr("data-order"), equipment = o.prev().val();
    $.post(`キャラ/${parents.eq(2).attr("data-unit")}/rank/${parents.eq(1).attr("data-rank")}/order/${order}/编辑`, {equipment: equipment}, function () {
        let parent1 = parents.eq(0), prev = parent1.prev();
        prev.find(`div[data-order='${order}']`).children().val(equipment);
        prev.prev().find(`div[data-order='${order}']`).children().prop("src", `装備/${equipment}/icon`);
        parent.html(`<button type="button" class="btn btn-outline-primary btn-sm float-right" onclick="set($(this))">装備</button>`);
        if (parent1.siblings().length === 3) parent1.after(`<div>\n<button type="button" class="btn btn-outline-primary btn-sm" onclick="setAll($(this))">まともて装備</button>\n</div>`);
    });
}

function set(o) {
    let parent = o.parent(), parents = parent.parents();
    $.post(`キャラ/${parents.eq(2).attr("data-unit")}/rank/${parents.eq(1).attr("data-rank")}/order/${parent.attr("data-order")}/装備`, function () {
        let parent1 = parents.eq(0), next = parent1.next(), find = parent1.find("button");
        if (find.length > 1) {
            o.remove();
            if (find.filter(".float-right").length === 1) next.remove();
        } else {
            parent1.remove();
            next.remove();
        }
    });
}

function setAll(o) {
    o.parent().parent().next().prevAll().find("button.float-right").click();
}

function count(o) {
    let all = o.prevAll(), length = all.length * 6 - all.children("div.row").find("button").length - 1;
    o.next().children().html(`<div class="input-group input-group-sm">\n<input class="form-control form-control-sm" value="${Math.floor(length / 6)}" readonly>\n<input class="form-control form-control-sm" value="${length % 6 + 1}" readonly>\n</div>`);
}