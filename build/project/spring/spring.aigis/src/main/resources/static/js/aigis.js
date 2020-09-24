const row = $(".container-fluid").children(), col = row.eq(0).children(), sex = col.eq(0), rare = col.eq(1), clazz = col.eq(2), unit = row.eq(1), png = row.eq(2), img = row.eq(3), host = "http://assets.millennium-war.net/";
let pathname = decodeURIComponent(location.pathname);
$(function () {
    history.replaceState(null, null);
    onpopstate = function () {
        let o = pathname.split("/"), n = (pathname = decodeURIComponent(location.pathname)).split("/"), b = false;
        if (o[3] !== n[3]) {
            b = true;
            sex.find(`input[value='${n[3]}']`).prop("checked", true);
        } else if (o[4] !== n[4]) {
            b = true;
            rare.find(`input[value='${n[4]}']`).prop("checked", true);
        } else if (o[5] !== n[5]) {
            b = true;
            clazz.find(`input[value='${n[5]}']`).prop("checked", true);
        }
        let c = function (deferred) {
            if (b) $.get("/aigis/unit", {
                sex: sex.find(":checked").val(),
                rare: rare.find(":checked").val(),
                clazz: clazz.find(":checked").val()
            }, function (data) {
                unit.html("");
                data.forEach(function (o) {
                    unit.append(`<div class="input-group input-group-sm">\n<div class="input-group-prepend">\n<div class="input-group-text">\n<input type="radio" name="unit" value="${o.unit}">\n</div>\n</div>\n<div class="input-group-text form-control${o.own === 1 ? ' text-primary' : ''}">${o.unit}</div>\n</div>`);
                });
                unit.find("input").change(function () {
                    u();
                });
                if (typeof n[6] === "undefined") {
                    png.html("");
                    $("img").prop("hidden", true);
                    deferred.reject();
                    return deferred;
                }
                deferred.resolve();
            }); else deferred.resolve();
            return deferred;
        };
        $.when(c($.Deferred())).done(function () {
            let u = function (deferred) {
                if (o[6] !== n[6]) {
                    unit.find(`input[value='${n[6]}']`).prop("checked", true);
                    $.get("/aigis/png", {unit: n[6]}, function (data) {
                        png.html("");
                        data.forEach(function (o) {
                            png.append(`<div class="input-group input-group-sm">\n<div class="input-group-prepend">\n<div class="input-group-text">\n<input type="radio" name="png" value="${o.name}" data-url="${o.url}">\n</div>\n</div>\n<div class="input-group-text form-control${o.count !== null ? ' text-primary' : ''}">${o.name}</div>\n</div>`);
                        });
                        png.find("input").change(function () {
                            p();
                        });
                        deferred.resolve();
                    });
                } else deferred.resolve();
                return deferred;
            };
            $.when(u($.Deferred())).done(function () {
                let url = host + png.find(`[value='${n[7]}']`).prop("checked", true).attr("data-url");
                if (typeof $("img").prop("hidden", true).filter(`[src='${url}']`).prop("hidden", false)[0] === "undefined") {
                    img.append(`<img src="${url}">`);
                }
            });
        });
    };
    col.find("input").change(function () {
        c();
    });
    unit.find("input").change(function () {
        u();
    });
    png.find("input").change(function () {
        p();
    });
});

function c() {
    $.get("/aigis/unit", {
        sex: sex.find(":checked").val(),
        rare: rare.find(":checked").val(),
        clazz: clazz.find(":checked").val()
    }, function (data) {
        unit.html("");
        data.forEach(function (o) {
            unit.append(`<div class="input-group input-group-sm">\n<div class="input-group-prepend">\n<div class="input-group-text">\n<input type="radio" name="unit" value="${o.unit}">\n</div>\n</div>\n<div class="input-group-text form-control${o.own === 1 ? ' text-primary' : ''}">${o.unit}</div>\n</div>`);
        });
        unit.find("input").change(function () {
            u();
        }).eq(0).prop("checked", true);
        u();
    });
}

function u() {
    let val = unit.find(":checked").val();
    if (typeof val === "undefined") {
        png.html("");
        $("img").prop("hidden", true);
        history.pushState(null, null, pathname = `/aigis/index/${sex.find(":checked").val()}/${rare.find(":checked").val()}/${clazz.find(":checked").val()}`);
    } else $.get("/aigis/png", {unit: val}, function (data) {
        png.html("");
        data.forEach(function (o) {
            png.append(`<div class="input-group input-group-sm">\n<div class="input-group-prepend">\n<div class="input-group-text">\n<input type="radio" name="png" value="${o.name}" data-url="${o.url}">\n</div>\n</div>\n<div class="input-group-text form-control${o.count !== null ? ' text-primary' : ''}">${o.name}</div>\n</div>`);
        });
        png.find("input").change(function () {
            p();
        }).eq(0).prop("checked", true);
        p();
    });
}

function p() {
    let find = png.find(":checked");
    let url = host + find.attr("data-url");
    if (typeof $("img").prop("hidden", true).filter(`[src='${url}']`).prop("hidden", false)[0] === "undefined") {
        img.append(`<img src="${url}">`);
    }
    history.pushState(null, null, pathname = `/aigis/index/${sex.find(":checked").val()}/${rare.find(":checked").val()}/${clazz.find(":checked").val()}/${unit.find(":checked").val()}/${find.val()}`);
}