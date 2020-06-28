function copy(o) {
    val = o.prev().html();
    o.next().html("√");
}

function addAll(o) {
    let parent = o.parent(), rank = parent.attr("data-rank");
    $.post("/プリコネR/キャラ/" + parent.parent().attr("data-unit") + "/rank/" + rank + "/新增", function () {
        parent.before("<div class='rank' data-rank='" + rank + "'>\n<p>Rank" + rank + "</p>\n<table>\n<tbody>\n<tr>\n<td class='equipment' data-order='1'>\n<img src='/プリコネR/装備/？？？/icon'>\n</td>\n<td class='equipment' data-order='2'>\n<img src='/プリコネR/装備/？？？/icon'>\n</td>\n<td class='equipment' data-order='3'>\n<img src='/プリコネR/装備/？？？/icon'>\n</td>\n<td class='equipment' data-order='4'>\n<img src='/プリコネR/装備/？？？/icon'>\n</td>\n<td class='equipment' data-order='5'>\n<img src='/プリコネR/装備/？？？/icon'>\n</td>\n<td class='equipment' data-order='6'>\n<img src='/プリコネR/装備/？？？/icon'>\n</td>\n</tr>\n<tr>\n<td data-order='1'>\n<span>？？？</span>\n<button type=\"button\" onclick=\"copy($(this))\">コピー</button>\n<span></span>\n</td>\n<td data-order='2'>\n<span>？？？</span>\n<button type=\"button\" onclick=\"copy($(this))\">コピー</button>\n<span></span>\n</td>\n<td data-order='3'>\n<span>？？？</span>\n<button type=\"button\" onclick=\"copy($(this))\">コピー</button>\n<span></span>\n</td>\n<td data-order='4'>\n<span>？？？</span>\n<button type=\"button\" onclick=\"copy($(this))\">コピー</button>\n<span></span>\n</td>\n<td data-order='5'>\n<span>？？？</span>\n<button type=\"button\" onclick=\"copy($(this))\">コピー</button>\n<span></span>\n</td>\n<td data-order='6'>\n<span>？？？</span>\n<button type=\"button\" onclick=\"copy($(this))\">コピー</button>\n<span></span>\n</td>\n</tr>\n<tr>\n<td data-order='1'>\n<input>\n<button type='button' name='update' onclick='update($(this))'>编辑</button>\n</td>\n<td  data-order='2'>\n<input>\n<button type='button' name='update' onclick='update($(this))'>编辑</button>\n</td>\n<td  data-order='3'>\n<input>\n<button type='button' name='update' onclick='update($(this))'>编辑</button>\n</td>\n<td data-order='4'>\n<input>\n<button type='button' name='update' onclick='update($(this))'>编辑</button>\n</td>\n<td data-order='5'>\n<input>\n<button type='button' name='update' onclick='update($(this))'>编辑</button>\n</td>\n<td data-order='6'>\n<input>\n<button type='button' name='update' onclick='update($(this))'>编辑</button>\n</td>\n</tr>\n</tbody>\n</table>\n</div>")
        parent.attr("data-rank", parseInt(rank) + 1);
    });
}

function update(o) {
    let parent = o.parent(), parents = o.parents("div"), order = parent.attr("data-order"), equipment = o.prev().val();
    if (equipment === "") equipment = val;
    $.post("/プリコネR/キャラ/" + parents.eq(1).attr("data-unit") + "/rank/" + parents.eq(0).attr("data-rank") + "/order/" + order + "/编辑", {equipment: equipment}, function () {
        let parent1 = parent.parent(), prev = parent1.prev();
        prev.find("td[data-order='" + order + "']").children().eq(0).html(equipment);
        prev.prev().find("td[data-order='" + order + "']").children().prop("src", "/プリコネR/装備/" + equipment + "/icon");
        parent.html("<button type='button' name='set' onclick='set($(this))'>装備</button>");
        if (parent1.siblings().length === 2) parent1.after("<tr>\n<td>\n<button type='button' onclick='setAll($(this))'>まともて装備</button>\n</td>\n</tr>");
    });
}

function set(o) {
    let parents = o.parents("div"), parent = o.parent();
    $.post("/プリコネR/キャラ/" + parents.eq(1).attr("data-unit") + "/rank/" + parents.eq(0).attr("data-rank") + "/order/" + parent.attr("data-order") + "/装備", function () {
        let parent1 = parent.parent(), next = parent1.next(), find = parent1.find("button");
        if (find.length > 1) {
            o.remove();
            if (find.filter("[name='set']").length === 1) next.remove();
        } else {
            parent1.remove();
            next.remove();
        }
    });
}

function setAll(o) {
    o.parent().parent().parent().parent().parent().next().prevAll().find("button[name='set']").click();
}

function count(o) {
    let length = $(".set").filter(function () {
        return $(this).parent().parent().parent().parent().parent().parent().attr("data-unit") === o.parent().attr("data-unit");
    }).length;
    o.next().html(Math.floor(length / 6) + " / " + length % 6);
}